#pragma once

#include <mapbox/geometry/geometry.hpp>
#include <mapbox/geometry/point_arithmetic.hpp>

namespace mbgl {

enum class FeatureType : uint8_t {
    Unknown = 0,
    Point = 1,
    LineString = 2,
    Polygon = 3
};

struct ToFeatureType {
    FeatureType operator()(mapbox::geometry::point<int16_t>) const { return FeatureType::Point; }
    FeatureType operator()(mapbox::geometry::multi_point<int16_t>) const { return FeatureType::Point; }
    FeatureType operator()(mapbox::geometry::line_string<int16_t>) const { return FeatureType::LineString; }
    FeatureType operator()(mapbox::geometry::multi_line_string<int16_t>) const { return FeatureType::LineString; }
    FeatureType operator()(mapbox::geometry::polygon<int16_t>) const { return FeatureType::Polygon; }
    FeatureType operator()(mapbox::geometry::multi_polygon<int16_t>) const { return FeatureType::Polygon; }
    FeatureType operator()(mapbox::geometry::geometry_collection<int16_t>) const { return FeatureType::Unknown; }
};

template <class T>
using Point = mapbox::geometry::point<T>;

template <class T>
using LineString = mapbox::geometry::line_string<T>;

template <class T>
using Polygon = mapbox::geometry::polygon<T>;

template <class T>
using MultiPoint = mapbox::geometry::multi_point<T>;

template <class T>
using MultiLineString = mapbox::geometry::multi_line_string<T>;

template <class T>
using MultiPolygon = mapbox::geometry::multi_polygon<T>;

template <class T>
using LinearRing = mapbox::geometry::linear_ring<T>;

template <class T>
using Geometry = mapbox::geometry::geometry<T>;

template <class S, class T>
Point<S> convertPoint(const Point<T>& p) {
    return Point<S>(p.x, p.y);
}

} // namespace mbgl
