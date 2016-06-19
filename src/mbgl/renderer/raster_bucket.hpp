#pragma once

#include <mbgl/renderer/bucket.hpp>
#include <mbgl/util/raster.hpp>

namespace mbgl {

class RasterShader;
class StaticRasterVertexBuffer;
class VertexArrayObject;

class RasterBucket : public Bucket {
public:
    void upload(gl::ObjectStore&) override;
    void render(Painter&, const style::Layer&, const UnwrappedTileID&, const mat4&) override;
    bool hasData() const override;
    bool needsClipping() const override;

    void setImage(PremultipliedImage);

    void drawRaster(RasterShader&, StaticVertexBuffer&, VertexArrayObject&, gl::ObjectStore&);

    Raster raster;
};

} // namespace mbgl
