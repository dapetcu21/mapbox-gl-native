#import <Mapbox/Mapbox.h>

/// Minimum size of an annotation’s accessibility element.
extern const CGSize MGLAnnotationAccessibilityElementMinimumSize;

@interface MGLMapView (Internal)

/** Triggers another render pass even when it is not necessary. */
- (void)setNeedsGLDisplay;

/** Returns whether the map view is currently loading or processing any assets required to render the map */
- (BOOL)isFullyLoaded;

/** Empties the in-memory tile cache. */
- (void)didReceiveMemoryWarning;

@end
