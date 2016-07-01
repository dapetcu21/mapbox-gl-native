#pragma once

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"
#pragma GCC diagnostic ignored "-Wshadow"
#include <nan.h>
#pragma GCC diagnostic pop

#include <mbgl/storage/resource.hpp>
#include <mbgl/storage/file_source.hpp>

namespace node_mbgl {

class NodeRequestWorker : public Nan::AsyncWorker {
public:
    NodeRequestWorker(v8::Local<v8::Object>, const mbgl::Resource&, mbgl::FileSource::Callback);
    virtual ~NodeRequestWorker();

    virtual void Execute();
    virtual void Destroy();
    virtual void WorkComplete();

    static void HandleCallback(const Nan::FunctionCallbackInfo<v8::Value>& info);

    class NodeRequest : public Nan::ObjectWrap {
    public:
        static NAN_MODULE_INIT(Init);
        static Nan::Persistent<v8::Function> constructor;
        static void New(const Nan::FunctionCallbackInfo<v8::Value>&);

    private:
        NodeRequest();
        ~NodeRequest();
    };

    struct NodeAsyncRequest : public mbgl::AsyncRequest {
        NodeAsyncRequest(NodeRequestWorker*);
        ~NodeAsyncRequest() override;

        NodeRequestWorker* worker;
    };

protected:
    virtual void HandleOKCallback();
    virtual void HandleErrorCallback();

private:
    v8::Local<v8::Object> nodeMapHandle;
    const mbgl::Resource& resource;
    std::unique_ptr<mbgl::FileSource::Callback> fileSourceCallback;
    NodeAsyncRequest* asyncRequest = nullptr;
    mbgl::Response response;

    static Nan::Persistent<v8::Function> handleCallback;
};

}
