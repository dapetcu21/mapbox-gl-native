#include "node_request.hpp"
#include <mbgl/storage/response.hpp>
#include <mbgl/util/chrono.hpp>

#include <cmath>

namespace node_mbgl {

NodeRequestWorker::NodeRequestWorker(
    v8::Local<v8::Object> nodeMapHandle_,
    const mbgl::Resource& resource_,
    mbgl::FileSource::Callback fileSourceCallback_)
    : AsyncWorker(nullptr),
    nodeMapHandle(nodeMapHandle_),
    resource(resource_),
    fileSourceCallback(std::make_unique<mbgl::FileSource::Callback>(fileSourceCallback_)) {}

NodeRequestWorker::~NodeRequestWorker() {}

void NodeRequestWorker::Execute() {
    Nan::HandleScope scope;

    auto req = Nan::NewInstance(Nan::New(NodeRequest::constructor)).ToLocalChecked();

    Nan::Set(req, Nan::New("url").ToLocalChecked(), Nan::New<v8::String>(resource.url).ToLocalChecked());
    Nan::Set(req, Nan::New("kind").ToLocalChecked(), Nan::New<v8::Integer>(resource.kind));

    auto callback = Nan::New(handleCallback);

    // Bind a reference to this object on the callback function
    callback->SetHiddenValue(Nan::New("worker").ToLocalChecked(), Nan::New<v8::External>(this));

    v8::Local<v8::Value> argv[] = {
        req,
        callback
    };

    Nan::MakeCallback(nodeMapHandle, "request", 2, argv);
}

void NodeRequestWorker::Destroy() {
    // When this object gets destroyed, make sure that the
    // AsyncRequest can no longer attempt to remove the callback function
    // this object was holding (it can't be fired anymore).
    if (asyncRequest) {
        asyncRequest->worker = nullptr;
    }
}

void NodeRequestWorker::WorkComplete() {
    // If callback has already been called, no-op
    if (!fileSourceCallback) return;

    ErrorMessage() ? HandleErrorCallback() : HandleOKCallback();
}

void NodeRequestWorker::HandleCallback(const Nan::FunctionCallbackInfo<v8::Value>& info) {
    auto hiddenValue = info.Callee()->GetHiddenValue(Nan::New("worker").ToLocalChecked());
    auto external = hiddenValue.As<v8::External>();
    auto externalValue = external->Value();
    auto worker = reinterpret_cast<NodeRequestWorker*>(externalValue);

    if (info[0]->IsObject()) {
        auto err = info[0]->ToObject();
        auto msg = Nan::New("message").ToLocalChecked();

        if (Nan::Has(err, msg).IsJust()) {
            worker->SetErrorMessage(*Nan::Utf8String(
                Nan::Get(err, msg).ToLocalChecked()));
        }
    } else if (info[0]->IsString()) {
        worker->SetErrorMessage(*Nan::Utf8String(info[0]));
    } else if (info.Length() < 1) {
        worker->response.noContent = true;
    } else if (info.Length() < 2 || !info[1]->IsObject()) {
        return Nan::ThrowTypeError("Second argument must be a response object");
    } else {
        auto res = info[1]->ToObject();

        if (Nan::Has(res, Nan::New("modified").ToLocalChecked()).IsJust()) {
            const double modified = Nan::To<double>(Nan::Get(res, Nan::New("modified").ToLocalChecked()).ToLocalChecked()).FromJust();
            if (!std::isnan(modified)) {
                worker->response.modified = mbgl::Timestamp { mbgl::Seconds(
                    static_cast<mbgl::Seconds::rep>(modified / 1000)) };
            }
        }

        if (Nan::Has(res, Nan::New("expires").ToLocalChecked()).IsJust()) {
            const double expires = Nan::To<double>(Nan::Get(res, Nan::New("expires").ToLocalChecked()).ToLocalChecked()).FromJust();
            if (!std::isnan(expires)) {
                worker->response.expires = mbgl::Timestamp { mbgl::Seconds(
                    static_cast<mbgl::Seconds::rep>(expires / 1000)) };
            }
        }

        if (Nan::Has(res, Nan::New("etag").ToLocalChecked()).IsJust()) {
            Nan::Utf8String etag(Nan::Get(res, Nan::New("etag").ToLocalChecked()).ToLocalChecked());
            if (*etag) {
                worker->response.etag = std::string { *etag };
            }
        }

        if (Nan::Has(res, Nan::New("data").ToLocalChecked()).IsJust()) {
            auto data = Nan::Get(res, Nan::New("data").ToLocalChecked()).ToLocalChecked();
            if (node::Buffer::HasInstance(data)) {
                worker->response.data = std::make_shared<const std::string>(
                    node::Buffer::Data(data),
                    node::Buffer::Length(data)
                );
            } else {
                return Nan::ThrowTypeError("Response data must be a Buffer");
            }
        }
    }

    worker->WorkComplete();
}

void NodeRequestWorker::HandleOKCallback() {
    // Move out of the object so callback() can only be fired once.
    auto cb = fileSourceCallback.release();

    // Send the response object to the NodeFileSource object
    (*cb)(response);

    // Clean up callback
    delete cb;
    cb = nullptr;
}

void NodeRequestWorker::HandleErrorCallback() {
    // Move out of the object so callback() can only be fired once.
    auto cb = fileSourceCallback.release();

    response.error = std::make_unique<mbgl::Response::Error>(
        mbgl::Response::Error::Reason::Other,
        std::string{ ErrorMessage() });
  
    // Send the response object to the NodeFileSource object
    (*cb)(response);

    // Clean up callback
    delete cb;
    cb = nullptr;
}

NodeRequestWorker::NodeRequest::NodeRequest() {}
NodeRequestWorker::NodeRequest::~NodeRequest() {}

Nan::Persistent<v8::Function> NodeRequestWorker::NodeRequest::constructor;

NAN_MODULE_INIT(NodeRequestWorker::NodeRequest::Init) {
    v8::Local<v8::FunctionTemplate> tpl = Nan::New<v8::FunctionTemplate>(New);

    tpl->InstanceTemplate()->SetInternalFieldCount(1);
    tpl->SetClassName(Nan::New("Request").ToLocalChecked());

    constructor.Reset(tpl->GetFunction());
    Nan::Set(target, Nan::New("Request").ToLocalChecked(), tpl->GetFunction());
}

void NodeRequestWorker::NodeRequest::New(const Nan::FunctionCallbackInfo<v8::Value>& info) {
    auto req = new NodeRequest();
    req->Wrap(info.This());
    info.GetReturnValue().Set(info.This());
}

NodeRequestWorker::NodeAsyncRequest::NodeAsyncRequest(NodeRequestWorker* worker_) : worker(worker_) {
    assert(worker);

    // Make sure the JS object has a pointer to this so that it can remove
    // its pointer in the destructor
    worker->asyncRequest = this;
}

NodeRequestWorker::NodeAsyncRequest::~NodeAsyncRequest() {
    if (worker) {
        // Remove the callback function because the AsyncRequest was
        // canceled and we are no longer interested in the result.
        worker->fileSourceCallback.reset();
        worker->asyncRequest = nullptr;
    }
}

Nan::Persistent<v8::Function> NodeRequestWorker::handleCallback(Nan::New<v8::Function>(NodeRequestWorker::HandleCallback));

}
