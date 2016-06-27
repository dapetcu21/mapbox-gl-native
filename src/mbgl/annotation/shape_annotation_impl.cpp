#include <mbgl/annotation/shape_annotation_impl.hpp>
#include <mbgl/annotation/annotation_tile.hpp>
#include <mbgl/tile/tile_id.hpp>
#include <mbgl/math/wrap.hpp>
#include <mbgl/math/clamp.hpp>
#include <mbgl/util/string.hpp>
#include <mbgl/util/constants.hpp>

namespace mbgl {

using namespace style;
namespace geojsonvt = mapbox::geojsonvt;

ShapeAnnotationImpl::ShapeAnnotationImpl(const AnnotationID id_, const uint8_t maxZoom_)
    : id(id_),
      maxZoom(maxZoom_),
      layerID("com.mapbox.annotations.shape." + util::toString(id)) {
}

void ShapeAnnotationImpl::updateTileData(const CanonicalTileID& tileID, AnnotationTileData& data) {
    static const double baseTolerance = 4;

    if (!shapeTiler) {
        const uint64_t maxAmountOfTileFeatures = (1ull << maxZoom) * util::EXTENT;
        const double tolerance = baseTolerance / maxAmountOfTileFeatures;
        mapbox::geometry::feature_collection<double> features;
        // features.emplace_back(geometry());
        mapbox::geojsonvt::Options options;
        options.maxZoom = maxZoom;
        options.buffer = 255u;
        options.extent = util::EXTENT;
        options.tolerance = baseTolerance;
        shapeTiler = std::make_unique<mapbox::geojsonvt::GeoJSONVT>(features, options);
    }

    const auto& shapeTile = shapeTiler->getTile(tileID.z, tileID.x, tileID.y);
    if (shapeTile.features.size() == 0)
        return;

    AnnotationTileLayer& layer = *data.layers.emplace(layerID,
        std::make_unique<AnnotationTileLayer>(layerID)).first->second;

    for (auto& shapeFeature : shapeTile.features) {
        FeatureType featureType = FeatureType::Unknown;

        GeometryCollection renderGeometry;

        if (typeid(shapeFeature.geometry) == typeid(mapbox::geometry::line_string<int16_t>)) {
            featureType = FeatureType::LineString;
            GeometryCoordinates renderLine;
            for (auto& shapePoint : shapeFeature.geometry.get<mapbox::geometry::line_string<int16_t>>()) {
                renderLine.emplace_back(shapePoint.x, shapePoint.y);
            }
            renderGeometry.push_back(renderLine);
        } else if (typeid(shapeFeature.geometry) == typeid(mapbox::geometry::polygon<int16_t>)) {
            featureType = FeatureType::Polygon;
            for (auto& shapeRing : shapeFeature.geometry.get<mapbox::geometry::polygon<int16_t>>()) {
                GeometryCoordinates renderLine;
                for (auto& shapePoint : shapeRing) {
                    renderLine.emplace_back(shapePoint.x, shapePoint.y);
                }
                renderGeometry.push_back(renderLine);
            }
        }

        assert(featureType != FeatureType::Unknown);

        // https://github.com/mapbox/geojson-vt-cpp/issues/44
        if (featureType == FeatureType::Polygon) {
            renderGeometry = fixupPolygons(renderGeometry);
        }

        layer.features.emplace_back(
            std::make_shared<AnnotationTileFeature>(featureType, renderGeometry));
    }
}

} // namespace mbgl
