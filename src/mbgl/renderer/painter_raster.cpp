#include <mbgl/renderer/painter.hpp>
#include <mbgl/gl/gl.hpp>
#include <mbgl/renderer/raster_bucket.hpp>
#include <mbgl/style/layers/raster_layer.hpp>
#include <mbgl/style/layers/raster_layer_impl.hpp>
#include <mbgl/shader/raster_shader.hpp>

namespace mbgl {

using namespace style;

void Painter::renderRaster(RasterBucket& bucket,
                           const RasterLayer& layer,
                           const UnwrappedTileID&,
                           const mat4& matrix) {
    if (pass != RenderPass::Translucent) return;

    const RasterPaintProperties& properties = layer.impl->paint;

    if (bucket.hasData()) {
        const auto& shaderRaster = isOverdraw() ? rasterOverdrawShader : rasterShader;
        auto& vaoRaster = isOverdraw() ? coveringRasterOverdrawArray : coveringRasterArray;
        config.program = shaderRaster->getID();
        shaderRaster->u_matrix = matrix;
        shaderRaster->u_buffer = 0;
        shaderRaster->u_opacity = properties.rasterOpacity;
        shaderRaster->u_brightness_low = properties.rasterBrightnessMin;
        shaderRaster->u_brightness_high = properties.rasterBrightnessMax;
        shaderRaster->u_saturation_factor = saturationFactor(properties.rasterSaturation);
        shaderRaster->u_contrast_factor = contrastFactor(properties.rasterContrast);
        shaderRaster->u_spin_weights = spinWeights(properties.rasterHueRotate);

        config.stencilTest = GL_FALSE;

        shaderRaster->u_image = 0;
        config.activeTexture = GL_TEXTURE0;

        config.depthFunc.reset();
        config.depthTest = GL_TRUE;
        config.depthMask = GL_FALSE;
        setDepthSublayer(0);
        bucket.drawRaster(*shaderRaster, tileStencilBuffer, vaoRaster, store);
    }
}

float Painter::saturationFactor(float saturation) {
    if (saturation > 0) {
        return 1 - 1 / (1.001 - saturation);
    } else {
        return -saturation;
    }
}

float Painter::contrastFactor(float contrast) {
    if (contrast > 0) {
        return 1 / (1 - contrast);
    } else {
        return 1 + contrast;
    }
}

std::array<float, 3> Painter::spinWeights(float spin) {
    spin *= util::DEG2RAD;
    float s = std::sin(spin);
    float c = std::cos(spin);
    std::array<float, 3> spin_weights = {{
        (2 * c + 1) / 3,
        (-std::sqrt(3.0f) * s - c + 1) / 3,
        (std::sqrt(3.0f) * s - c + 1) / 3
    }};
    return spin_weights;
}

} // namespace mbgl
