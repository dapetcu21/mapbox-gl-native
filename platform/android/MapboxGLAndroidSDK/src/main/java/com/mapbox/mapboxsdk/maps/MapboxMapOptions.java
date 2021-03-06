package com.mapbox.mapboxsdk.maps;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import com.mapbox.mapboxsdk.R;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.utils.ColorUtils;
import java.util.Arrays;

/**
 * Defines configuration MapboxMapMapOptions for a MapboxMap. These options can be used when adding a
 * map to your application programmatically (as opposed to via XML). If you are using a MapFragment,
 * you can pass these options in using the static factory method newInstance(MapboxMapOptions).
 * If you are using a MapView, you can pass these options in using the constructor
 * MapView(Context, MapboxMapOptions). If you add a map using XML, then you can apply these options
 * using custom XML tags.
 */
public class MapboxMapOptions implements Parcelable {

    private static final float DIMENSION_SEVEN_DP = 7f;
    private static final float DIMENSION_TEN_DP = 10f;
    private static final float DIMENSION_SIXTEEN_DP = 16f;
    private static final float DIMENSION_SEVENTY_SIX_DP = 76f;

    private CameraPosition cameraPosition;

    private boolean debugActive;

    private boolean compassEnabled = true;
    private int compassGravity = Gravity.TOP | Gravity.END;
    private int compassMargins[];

    private boolean logoEnabled = true;
    private int logoGravity = Gravity.BOTTOM | Gravity.START;
    private int logoMargins[];

    @ColorInt
    private int attributionTintColor = -1;
    private boolean attributionEnabled = true;
    private int attributionGravity = Gravity.BOTTOM;
    private int attributionMargins[];

    private float minZoom = MapboxConstants.MINIMUM_ZOOM;
    private float maxZoom = MapboxConstants.MAXIMUM_ZOOM;

    private boolean rotateGesturesEnabled = true;
    private boolean scrollGesturesEnabled = true;
    private boolean tiltGesturesEnabled = true;
    private boolean zoomGesturesEnabled = true;
    private boolean zoomControlsEnabled = false;

    private boolean myLocationEnabled;
    private Drawable myLocationForegroundDrawable;
    private Drawable myLocationForegroundBearingDrawable;
    private Drawable myLocationBackgroundDrawable;
    private int myLocationForegroundTintColor;
    private int myLocationBackgroundTintColor;
    private int[] myLocationBackgroundPadding;
    private int myLocationAccuracyTintColor;
    private int myLocationAccuracyAlpha;

    private String style;
    @Deprecated
    private String accessToken;

    /**
     * Creates a new MapboxMapOptions object.
     */
    public MapboxMapOptions() {
    }

    private MapboxMapOptions(Parcel in) {
        cameraPosition = in.readParcelable(CameraPosition.class.getClassLoader());
        debugActive = in.readByte() != 0;

        compassEnabled = in.readByte() != 0;
        compassGravity = in.readInt();
        compassMargins = in.createIntArray();

        logoEnabled = in.readByte() != 0;
        logoGravity = in.readInt();
        logoMargins = in.createIntArray();

        attributionEnabled = in.readByte() != 0;
        attributionGravity = in.readInt();
        attributionMargins = in.createIntArray();
        attributionTintColor = in.readInt();

        minZoom = in.readFloat();
        maxZoom = in.readFloat();

        rotateGesturesEnabled = in.readByte() != 0;
        scrollGesturesEnabled = in.readByte() != 0;
        tiltGesturesEnabled = in.readByte() != 0;
        zoomControlsEnabled = in.readByte() != 0;
        zoomGesturesEnabled = in.readByte() != 0;

        myLocationEnabled = in.readByte() != 0;
        //myLocationForegroundDrawable;
        //myLocationForegroundBearingDrawable;
        //myLocationBackgroundDrawable;
        myLocationForegroundTintColor = in.readInt();
        myLocationBackgroundTintColor = in.readInt();
        myLocationBackgroundPadding = in.createIntArray();
        myLocationAccuracyAlpha = in.readInt();
        myLocationAccuracyTintColor = in.readInt();

        style = in.readString();
        accessToken = in.readString();
    }

    /**
     * Creates a GoogleMapsOptions from the attribute set
     *
     * @param context Context related to a map view.
     * @param attrs   Attributeset containing configuration
     * @return
     */
    public static MapboxMapOptions createFromAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        MapboxMapOptions mapboxMapOptions = new MapboxMapOptions();
        float screenDensity = context.getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MapView, 0, 0);
        try {
            mapboxMapOptions.debugActive(typedArray.getBoolean(R.styleable.MapView_debug_active, false));

            mapboxMapOptions.camera(new CameraPosition.Builder(typedArray).build());

            mapboxMapOptions.accessToken(typedArray.getString(R.styleable.MapView_access_token));
            mapboxMapOptions.styleUrl(typedArray.getString(R.styleable.MapView_style_url));

            mapboxMapOptions.zoomGesturesEnabled(typedArray.getBoolean(R.styleable.MapView_zoom_enabled, true));
            mapboxMapOptions.scrollGesturesEnabled(typedArray.getBoolean(R.styleable.MapView_scroll_enabled, true));
            mapboxMapOptions.rotateGesturesEnabled(typedArray.getBoolean(R.styleable.MapView_rotate_enabled, true));
            mapboxMapOptions.tiltGesturesEnabled(typedArray.getBoolean(R.styleable.MapView_tilt_enabled, true));
            mapboxMapOptions.zoomControlsEnabled(typedArray.getBoolean(R.styleable.MapView_zoom_controls_enabled, false));

            mapboxMapOptions.maxZoom(typedArray.getFloat(R.styleable.MapView_zoom_max, MapboxConstants.MAXIMUM_ZOOM));
            mapboxMapOptions.minZoom(typedArray.getFloat(R.styleable.MapView_zoom_min, MapboxConstants.MINIMUM_ZOOM));

            mapboxMapOptions.compassEnabled(typedArray.getBoolean(R.styleable.MapView_compass_enabled, true));
            mapboxMapOptions.compassGravity(typedArray.getInt(R.styleable.MapView_compass_gravity, Gravity.TOP | Gravity.END));
            mapboxMapOptions.compassMargins(new int[]{(int) (typedArray.getDimension(R.styleable.MapView_compass_margin_left, DIMENSION_TEN_DP) * screenDensity)
                    , ((int) typedArray.getDimension(R.styleable.MapView_compass_margin_top, DIMENSION_TEN_DP * screenDensity))
                    , ((int) typedArray.getDimension(R.styleable.MapView_compass_margin_right, DIMENSION_TEN_DP * screenDensity))
                    , ((int) typedArray.getDimension(R.styleable.MapView_compass_margin_bottom, DIMENSION_TEN_DP * screenDensity))});

            mapboxMapOptions.logoEnabled(typedArray.getBoolean(R.styleable.MapView_logo_enabled, true));
            mapboxMapOptions.logoGravity(typedArray.getInt(R.styleable.MapView_logo_gravity, Gravity.BOTTOM | Gravity.START));
            mapboxMapOptions.logoMargins(new int[]{(int) (typedArray.getDimension(R.styleable.MapView_logo_margin_left, DIMENSION_SIXTEEN_DP) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_logo_margin_top, DIMENSION_SIXTEEN_DP) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_logo_margin_right, DIMENSION_SIXTEEN_DP) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_logo_margin_bottom, DIMENSION_SIXTEEN_DP) * screenDensity)});

            mapboxMapOptions.attributionTintColor(typedArray.getColor(R.styleable.MapView_attribution_tint, -1));
            mapboxMapOptions.attributionEnabled(typedArray.getBoolean(R.styleable.MapView_attribution_enabled, true));
            mapboxMapOptions.attributionGravity(typedArray.getInt(R.styleable.MapView_attribution_gravity, Gravity.BOTTOM));
            mapboxMapOptions.attributionMargins(new int[]{(int) (typedArray.getDimension(R.styleable.MapView_attribution_margin_left, DIMENSION_SEVENTY_SIX_DP) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_attribution_margin_top, DIMENSION_SEVEN_DP) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_attribution_margin_right, DIMENSION_SEVEN_DP) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_attribution_margin_bottom, DIMENSION_SEVEN_DP) * screenDensity)});

            mapboxMapOptions.locationEnabled(typedArray.getBoolean(R.styleable.MapView_my_location_enabled, false));
            mapboxMapOptions.myLocationForegroundTintColor(typedArray.getColor(R.styleable.MapView_my_location_foreground_tint, Color.TRANSPARENT));
            mapboxMapOptions.myLocationBackgroundTintColor(typedArray.getColor(R.styleable.MapView_my_location_background_tint, Color.TRANSPARENT));

            Drawable foregroundDrawable = typedArray.getDrawable(R.styleable.MapView_my_location_foreground);
            if(foregroundDrawable==null){
                foregroundDrawable = ContextCompat.getDrawable(context,R.drawable.ic_mylocationview_normal);
            }

            Drawable foregroundBearingDrawable = typedArray.getDrawable(R.styleable.MapView_my_location_foreground_bearing);
            if(foregroundBearingDrawable==null){
                foregroundBearingDrawable = ContextCompat.getDrawable(context,R.drawable.ic_mylocationview_bearing);
            }

            Drawable backgroundDrawable = typedArray.getDrawable(R.styleable.MapView_my_location_background);
            if(backgroundDrawable==null){
                backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.ic_mylocationview_background);
            }

            mapboxMapOptions.myLocationForegroundDrawables(foregroundDrawable, foregroundBearingDrawable);
            mapboxMapOptions.myLocationBackgroundDrawable(backgroundDrawable);
            mapboxMapOptions.myLocationBackgroundPadding(new int[]{(int) (typedArray.getDimension(R.styleable.MapView_my_location_background_left, 0) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_my_location_background_top, 0) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_my_location_background_right, 0) * screenDensity)
                    , (int) (typedArray.getDimension(R.styleable.MapView_my_location_background_bottom, 0) * screenDensity)});
            mapboxMapOptions.myLocationAccuracyAlpha(typedArray.getInt(R.styleable.MapView_my_location_accuracy_alpha, 100));
            mapboxMapOptions.myLocationAccuracyTint(typedArray.getColor(R.styleable.MapView_my_location_accuracy_tint, ColorUtils.getPrimaryColor(context)));
        } finally {
            typedArray.recycle();
        }
        return mapboxMapOptions;
    }

    /**
     * Specifies a the initial camera position for the map view.
     *
     * @param cameraPosition Inital camera position
     * @return This
     */
    public MapboxMapOptions camera(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
        return this;
    }

    /**
     * <p>
     * DEPRECATED @see MapboxAccountManager#start(String)
     * </p>
     * Specifies the accesstoken associated with a map view.
     *
     * @param accessToken Token to be used to access the service
     * @return This
     */
    @Deprecated
    public MapboxMapOptions accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    /**
     * Specifies the style url associated with a map view.
     *
     * @param styleUrl Url to be used to load a style
     * @return This
     */
    public MapboxMapOptions styleUrl(String styleUrl) {
        style = styleUrl;
        return this;
    }

    /**
     * Specifies the used debug type for a map view.
     *
     * @param enabled True is debug is enabled
     * @return This
     */
    public MapboxMapOptions debugActive(boolean enabled) {
        debugActive = enabled;
        return this;
    }

    /**
     * Specifies the used minimum zoom level for a map view.
     *
     * @param minZoom Zoom level to be used
     * @return This
     */
    public MapboxMapOptions minZoom(float minZoom) {
        this.minZoom = minZoom;
        return this;
    }

    /**
     * Specifies the used maximum zoom level for a map view.
     *
     * @param maxZoom Zoom level to be used
     * @return This
     */
    public MapboxMapOptions maxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
        return this;
    }

    /**
     * Specifies the visibility state of a compass for a map view.
     *
     * @param enabled True and compass is shown
     * @return This
     */
    public MapboxMapOptions compassEnabled(boolean enabled) {
        compassEnabled = enabled;
        return this;
    }

    /**
     * Specifies the gravity state of compass for a map view.
     *
     * @param gravity see {@link android.view.Gravity}
     * @return This
     */
    public MapboxMapOptions compassGravity(int gravity) {
        compassGravity = gravity;
        return this;
    }

    /**
     * Specifies the margin state of compass for a map view
     *
     * @param margins 4 long array for LTRB margins
     * @return This
     */
    public MapboxMapOptions compassMargins(int[] margins) {
        compassMargins = margins;
        return this;
    }

    /**
     * Specifies the visibility state of a logo for a map view.
     *
     * @param enabled True and logo is shown
     * @return This
     */
    public MapboxMapOptions logoEnabled(boolean enabled) {
        logoEnabled = enabled;
        return this;
    }

    /**
     * Specifies the gravity state of logo for a map view.
     *
     * @param gravity see {@link android.view.Gravity}
     * @return This
     */
    public MapboxMapOptions logoGravity(int gravity) {
        logoGravity = gravity;
        return this;
    }

    /**
     * Specifies the margin state of logo for a map view
     *
     * @param margins 4 long array for LTRB margins
     * @return This
     */
    public MapboxMapOptions logoMargins(int[] margins) {
        logoMargins = margins;
        return this;
    }

    /**
     * Specifies the visibility state of a attribution for a map view.
     *
     * @param enabled True and attribution is shown
     * @return This
     */
    public MapboxMapOptions attributionEnabled(boolean enabled) {
        attributionEnabled = enabled;
        return this;
    }

    /**
     * Specifies the gravity state of attribution for a map view.
     *
     * @param gravity see {@link android.view.Gravity}
     * @return This
     */
    public MapboxMapOptions attributionGravity(int gravity) {
        attributionGravity = gravity;
        return this;
    }

    /**
     * Specifies the margin state of attribution for a map view
     *
     * @param margins 4 long array for LTRB margins
     * @return This
     */
    public MapboxMapOptions attributionMargins(int[] margins) {
        attributionMargins = margins;
        return this;
    }

    /**
     * Specifies the tint color of the attribution for a map view
     *
     * @param color integer resembling a color
     * @return This
     */
    public MapboxMapOptions attributionTintColor(@ColorInt int color) {
        attributionTintColor = color;
        return this;
    }

    /**
     * Specifies if the rotate gesture is enabled for a map view.
     *
     * @param enabled True and gesture will be enabled
     * @return This
     */
    public MapboxMapOptions rotateGesturesEnabled(boolean enabled) {
        rotateGesturesEnabled = enabled;
        return this;
    }

    /**
     * Specifies if the scroll gesture is enabled for a map view.
     *
     * @param enabled True and gesture will be enabled
     * @return This
     */
    public MapboxMapOptions scrollGesturesEnabled(boolean enabled) {
        scrollGesturesEnabled = enabled;
        return this;
    }

    /**
     * Specifies if the tilt gesture is enabled for a map view.
     *
     * @param enabled True and gesture will be enabled
     * @return This
     */
    public MapboxMapOptions tiltGesturesEnabled(boolean enabled) {
        tiltGesturesEnabled = enabled;
        return this;
    }

    /**
     * Specifies if the zoom controls are enabled for a map view.
     *
     * @param enabled True and gesture will be enabled
     * @return This
     */
    public MapboxMapOptions zoomControlsEnabled(boolean enabled) {
        zoomControlsEnabled = enabled;
        return this;
    }

    /**
     * Specifies if the zoom gesture is enabled for a map view.
     *
     * @param enabled True and gesture will be enabled
     * @return This
     */
    public MapboxMapOptions zoomGesturesEnabled(boolean enabled) {
        zoomGesturesEnabled = enabled;
        return this;
    }

    /**
     * Specifies if the user location view is enabled for a map view.
     *
     * @param locationEnabled True and gesture will be enabled
     * @return This
     */
    public MapboxMapOptions locationEnabled(boolean locationEnabled) {
        this.myLocationEnabled = locationEnabled;
        return this;
    }

    /**
     *
     * @param myLocationForegroundDrawable
     * @param myLocationBearingDrawable
     * @return This
     */
    public MapboxMapOptions myLocationForegroundDrawables(Drawable myLocationForegroundDrawable, Drawable myLocationBearingDrawable ) {
        this.myLocationForegroundDrawable = myLocationForegroundDrawable;
        this.myLocationForegroundBearingDrawable = myLocationBearingDrawable;
        return this;
    }

    /**
     *
     * @param myLocationForegroundDrawable
     * @return This
     */
    public MapboxMapOptions myLocationForegroundDrawable(Drawable myLocationForegroundDrawable) {
        this.myLocationForegroundDrawable = myLocationForegroundDrawable;
        return this;
    }

    /**
     * @param myLocationBackgroundDrawable
     * @return This
     */
    public MapboxMapOptions myLocationBackgroundDrawable(Drawable myLocationBackgroundDrawable) {
        this.myLocationBackgroundDrawable = myLocationBackgroundDrawable;
        return this;
    }

    /**
     * @param myLocationForegroundTintColor
     * @return This
     */
    public MapboxMapOptions myLocationForegroundTintColor(@ColorInt int myLocationForegroundTintColor) {
        this.myLocationForegroundTintColor = myLocationForegroundTintColor;
        return this;
    }

    /**
     * @param myLocationBackgroundTintColor
     * @return This
     */
    public MapboxMapOptions myLocationBackgroundTintColor(@ColorInt int myLocationBackgroundTintColor) {
        this.myLocationBackgroundTintColor = myLocationBackgroundTintColor;
        return this;
    }

    /**
     * @param myLocationBackgroundPadding
     * @return This
     */
    public MapboxMapOptions myLocationBackgroundPadding(int[] myLocationBackgroundPadding) {
        this.myLocationBackgroundPadding = myLocationBackgroundPadding;
        return this;
    }

    /**
     * @param myLocationAccuracyTintColor
     * @return This
     */
    public MapboxMapOptions myLocationAccuracyTint(@ColorInt int myLocationAccuracyTintColor) {
        this.myLocationAccuracyTintColor = myLocationAccuracyTintColor;
        return this;
    }

    /**
     * @param alpha
     * @return This
     */
    public MapboxMapOptions myLocationAccuracyAlpha(@IntRange(from = 0, to = 255) int alpha) {
        this.myLocationAccuracyAlpha = alpha;
        return this;
    }

    /**
     * Get the current configured initial camera position for a map view.
     *
     * @return CameraPosition to be initially used.
     */
    public CameraPosition getCamera() {
        return cameraPosition;
    }

    /**
     * Get the current configured min zoom for a map view.
     *
     * @return Mininum zoom level to be used.
     */
    public float getMinZoom() {
        return minZoom;
    }

    /**
     * Get the current configured maximum zoom for a map view.
     *
     * @return Maximum zoom to be used.
     */
    public float getMaxZoom() {
        return maxZoom;
    }

    /**
     * Get the current configured visibility state for compass for a map view.
     *
     * @return Visibility state of the compass
     */
    public boolean getCompassEnabled() {
        return compassEnabled;
    }

    /**
     * Get the current configured gravity state for compass for a map view.
     *
     * @return Gravity state of the compass
     */
    public int getCompassGravity() {
        return compassGravity;
    }

    /**
     * Get the current configured margins for compass for a map view.
     *
     * @return Margins state of the compass
     */
    public int[] getCompassMargins() {
        return compassMargins;
    }

    /**
     * Get the current configured visibility state for compass for a map view.
     *
     * @return Visibility state of the compass
     */
    public boolean getLogoEnabled() {
        return logoEnabled;
    }

    /**
     * Get the current configured gravity state for logo for a map view.
     *
     * @return Gravity state of the logo
     */
    public int getLogoGravity() {
        return logoGravity;
    }

    /**
     * Get the current configured margins for logo for a map view.
     *
     * @return Margins state of the logo
     */
    public int[] getLogoMargins() {
        return logoMargins;
    }

    /**
     * <p>
     * DEPRECATED @see MapboxAccountManager#start(String)
     * </p>
     * Get the current configured access token for a map view.
     *
     * @return Access token to be used.
     */
    @Deprecated
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Get the current configured style url for a map view.
     *
     * @return Style url to be used.
     */
    public String getStyle() {
        return style;
    }

    /**
     * Get the current configured rotate gesture state for a map view.
     *
     * @return True indicates gesture is enabled
     */
    public boolean getRotateGesturesEnabled() {
        return rotateGesturesEnabled;
    }

    /**
     * Get the current configured scroll gesture state for a map view.
     *
     * @return True indicates gesture is enabled
     */
    public boolean getScrollGesturesEnabled() {
        return scrollGesturesEnabled;
    }

    /**
     * Get the current configured tilt gesture state for a map view.
     *
     * @return True indicates gesture is enabled
     */
    public boolean getTiltGesturesEnabled() {
        return tiltGesturesEnabled;
    }

    /**
     * Get the current configured zoom controls state for a map view.
     *
     * @return True indicates gesture is enabled
     */
    public boolean getZoomControlsEnabled() {
        return zoomControlsEnabled;
    }

    /**
     * Get the current configured zoom gesture state for a map view.
     *
     * @return True indicates gesture is enabled
     */
    public boolean getZoomGesturesEnabled() {
        return zoomGesturesEnabled;
    }

    /**
     * Get the current configured visibility state for attribution for a map view.
     *
     * @return Visibility state of the attribution
     */
    public boolean getAttributionEnabled() {
        return attributionEnabled;
    }

    /**
     * Get the current configured gravity state for attribution for a map view.
     *
     * @return Gravity state of the logo
     */
    public int getAttributionGravity() {
        return attributionGravity;
    }

    /**
     * Get the current configured margins for attribution for a map view.
     *
     * @return Margins state of the logo
     */
    public int[] getAttributionMargins() {
        return attributionMargins;
    }

    @ColorInt
    public int getAttributionTintColor() {
        return attributionTintColor;
    }

    /**
     * Get the current configured user location view state for a map view.
     *
     * @return True and user location will be shown
     */
    public boolean getLocationEnabled() {
        return myLocationEnabled;
    }

    /**
     * @return
     */
    public Drawable getMyLocationForegroundDrawable() {
        return myLocationForegroundDrawable;
    }

    /**
     * @return
     */
    public Drawable getMyLocationForegroundBearingDrawable() {
        return myLocationForegroundBearingDrawable;
    }

    /**
     * @return
     */
    public Drawable getMyLocationBackgroundDrawable() {
        return myLocationBackgroundDrawable;
    }

    /**
     * @return
     */
    public int getMyLocationForegroundTintColor() {
        return myLocationForegroundTintColor;
    }

    /**
     * @return
     */
    public int getMyLocationBackgroundTintColor() {
        return myLocationBackgroundTintColor;
    }

    /**
     * @return
     */
    public int[] getMyLocationBackgroundPadding() {
        return myLocationBackgroundPadding;
    }

    /**
     * @return
     */
    public int getMyLocationAccuracyTintColor() {
        return myLocationAccuracyTintColor;
    }

    /**
     * @return
     */
    public int getMyLocationAccuracyAlpha() {
        return myLocationAccuracyAlpha;
    }

    /**
     * Get the current configured debug state for a map view.
     *
     * @return True indicates debug is enabled.
     */
    public boolean getDebugActive() {
        return debugActive;
    }

    public static final Parcelable.Creator<MapboxMapOptions> CREATOR
            = new Parcelable.Creator<MapboxMapOptions>() {
        public MapboxMapOptions createFromParcel(Parcel in) {
            return new MapboxMapOptions(in);
        }

        public MapboxMapOptions[] newArray(int size) {
            return new MapboxMapOptions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(cameraPosition, flags);
        dest.writeByte((byte) (debugActive ? 1 : 0));

        dest.writeByte((byte) (compassEnabled ? 1 : 0));
        dest.writeInt(compassGravity);
        dest.writeIntArray(compassMargins);

        dest.writeByte((byte) (logoEnabled ? 1 : 0));
        dest.writeInt(logoGravity);
        dest.writeIntArray(logoMargins);

        dest.writeByte((byte) (attributionEnabled ? 1 : 0));
        dest.writeInt(attributionGravity);
        dest.writeIntArray(attributionMargins);
        dest.writeInt(attributionTintColor);

        dest.writeFloat(minZoom);
        dest.writeFloat(maxZoom);

        dest.writeByte((byte) (rotateGesturesEnabled ? 1 : 0));
        dest.writeByte((byte) (scrollGesturesEnabled ? 1 : 0));
        dest.writeByte((byte) (tiltGesturesEnabled ? 1 : 0));
        dest.writeByte((byte) (zoomControlsEnabled ? 1 : 0));
        dest.writeByte((byte) (zoomGesturesEnabled ? 1 : 0));

        dest.writeByte((byte) (myLocationEnabled ? 1 : 0));
        //myLocationForegroundDrawable;
        //myLocationForegroundBearingDrawable;
        //myLocationBackgroundDrawable;
        dest.writeInt(myLocationForegroundTintColor);
        dest.writeInt(myLocationBackgroundTintColor);
        dest.writeIntArray(myLocationBackgroundPadding);
        dest.writeInt(myLocationAccuracyAlpha);
        dest.writeInt(myLocationAccuracyTintColor);

        dest.writeString(style);
        dest.writeString(accessToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapboxMapOptions options = (MapboxMapOptions) o;

        if (debugActive != options.debugActive) return false;
        if (compassEnabled != options.compassEnabled) return false;
        if (compassGravity != options.compassGravity) return false;
        if (logoEnabled != options.logoEnabled) return false;
        if (logoGravity != options.logoGravity) return false;
        if (attributionEnabled != options.attributionEnabled) return false;
        if (attributionGravity != options.attributionGravity) return false;
        if (Float.compare(options.minZoom, minZoom) != 0) return false;
        if (Float.compare(options.maxZoom, maxZoom) != 0) return false;
        if (rotateGesturesEnabled != options.rotateGesturesEnabled) return false;
        if (scrollGesturesEnabled != options.scrollGesturesEnabled) return false;
        if (tiltGesturesEnabled != options.tiltGesturesEnabled) return false;
        if (zoomGesturesEnabled != options.zoomGesturesEnabled) return false;
        if (zoomControlsEnabled != options.zoomControlsEnabled) return false;
        if (myLocationEnabled != options.myLocationEnabled) return false;
        if (myLocationForegroundTintColor != options.myLocationForegroundTintColor) return false;
        if (myLocationBackgroundTintColor != options.myLocationBackgroundTintColor) return false;
        if (myLocationAccuracyTintColor != options.myLocationAccuracyTintColor) return false;
        if (myLocationAccuracyAlpha != options.myLocationAccuracyAlpha) return false;
        if (cameraPosition != null ? !cameraPosition.equals(options.cameraPosition) : options.cameraPosition != null)
            return false;
        if (!Arrays.equals(compassMargins, options.compassMargins)) return false;
        if (!Arrays.equals(logoMargins, options.logoMargins)) return false;
        if (!Arrays.equals(attributionMargins, options.attributionMargins)) return false;
        if (myLocationForegroundDrawable != null ? !myLocationForegroundDrawable.equals(options.myLocationForegroundDrawable) : options.myLocationForegroundDrawable != null)
            return false;
        if (myLocationForegroundBearingDrawable != null ? !myLocationForegroundBearingDrawable.equals(options.myLocationForegroundBearingDrawable) : options.myLocationForegroundBearingDrawable != null)
            return false;
        if (myLocationBackgroundDrawable != null ? !myLocationBackgroundDrawable.equals(options.myLocationBackgroundDrawable) : options.myLocationBackgroundDrawable != null)
            return false;
        if (!Arrays.equals(myLocationBackgroundPadding, options.myLocationBackgroundPadding))
            return false;
        if (style != null ? !style.equals(options.style) : options.style != null) return false;
        return accessToken != null ? accessToken.equals(options.accessToken) : options.accessToken == null;
    }

    @Override
    public int hashCode() {
        int result = cameraPosition != null ? cameraPosition.hashCode() : 0;
        result = 31 * result + (debugActive ? 1 : 0);
        result = 31 * result + (compassEnabled ? 1 : 0);
        result = 31 * result + compassGravity;
        result = 31 * result + Arrays.hashCode(compassMargins);
        result = 31 * result + (logoEnabled ? 1 : 0);
        result = 31 * result + logoGravity;
        result = 31 * result + Arrays.hashCode(logoMargins);
        result = 31 * result + (attributionEnabled ? 1 : 0);
        result = 31 * result + attributionGravity;
        result = 31 * result + Arrays.hashCode(attributionMargins);
        result = 31 * result + (minZoom != +0.0f ? Float.floatToIntBits(minZoom) : 0);
        result = 31 * result + (maxZoom != +0.0f ? Float.floatToIntBits(maxZoom) : 0);
        result = 31 * result + (rotateGesturesEnabled ? 1 : 0);
        result = 31 * result + (scrollGesturesEnabled ? 1 : 0);
        result = 31 * result + (tiltGesturesEnabled ? 1 : 0);
        result = 31 * result + (zoomGesturesEnabled ? 1 : 0);
        result = 31 * result + (zoomControlsEnabled ? 1 : 0);
        result = 31 * result + (myLocationEnabled ? 1 : 0);
        result = 31 * result + (myLocationForegroundDrawable != null ? myLocationForegroundDrawable.hashCode() : 0);
        result = 31 * result + (myLocationForegroundBearingDrawable != null ? myLocationForegroundBearingDrawable.hashCode() : 0);
        result = 31 * result + (myLocationBackgroundDrawable != null ? myLocationBackgroundDrawable.hashCode() : 0);
        result = 31 * result + myLocationForegroundTintColor;
        result = 31 * result + myLocationBackgroundTintColor;
        result = 31 * result + Arrays.hashCode(myLocationBackgroundPadding);
        result = 31 * result + myLocationAccuracyTintColor;
        result = 31 * result + myLocationAccuracyAlpha;
        result = 31 * result + (style != null ? style.hashCode() : 0);
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        return result;
    }
}
