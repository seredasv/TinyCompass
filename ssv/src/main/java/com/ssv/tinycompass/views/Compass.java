package com.ssv.tinycompass.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.ssv.tinycompass.R;
import com.ssv.tinycompass.utils.CompassCustomView;
import com.ssv.tinycompass.views.dialogs.CoordinateDialog;

public class Compass implements SensorEventListener {
    //region constants
    private static final long ANIMATION_DURATION = 500;
    private static final float FLOAT_ZERO_POINT_FIVE = 0.5f;
    private static final float FLOAT_ZERO = 0f;
    private static final float DEFAULT_ALPHA = 0.97f;
    private static final int ARRAY_SIZE = 3;
    private static final int ARRAY_SIZE_MATRIX = 9;
    private static final int SMALLEST_DISTANCE = 10;
    private static final int DEGREES_360 = 360;
    //endregion

    //region fields
    private static Compass instance;
    private Context context;
    private ImageView arrowView = null;
    private CompassCustomView compassCustomView = null;
    private TextView tvDistance = null;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
    private float[] gravity = new float[ARRAY_SIZE];
    private float[] geomagnetic = new float[ARRAY_SIZE];
    private float azimuth = FLOAT_ZERO;
    private float currentAzimuth = FLOAT_ZERO;
    private SharedPreferences sp;
    private Location location;
    //endregion

    //region constructors
    private Compass() {
    }

    private Compass(Context context) {
        this.context = context;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }
    //endregion

    //region static methods
    public static synchronized Compass getInstance(Context context) {
        if (instance == null) {
            instance = new Compass(context);
        }
        return instance;
    }
    //endregion

    //region getters / setters
    public ImageView getArrowView() {
        return arrowView;
    }

    public void setArrowView(ImageView arrowView) {
        this.arrowView = arrowView;
    }

    public CompassCustomView getCompassCustomView() {
        return compassCustomView;
    }

    public void setCompassCustomView(CompassCustomView compassCustomView) {
        this.compassCustomView = compassCustomView;
    }

    public TextView getTvDistanceView() {
        return tvDistance;
    }

    public void setTvDistanceView(TextView tvDistance) {
        this.tvDistance = tvDistance;
    }

    public void setCurrentCoordinates(Location location) {
        this.location = location;
    }
    //endregion

    //region methods
    public void registerListener() {
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    private void updateCompassArrow() {
        if (arrowView == null) {
            return;
        }

        Animation animation = new RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF,
                FLOAT_ZERO_POINT_FIVE, Animation.RELATIVE_TO_SELF, FLOAT_ZERO_POINT_FIVE);
        currentAzimuth = azimuth;

        animation.setDuration(ANIMATION_DURATION);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);

        arrowView.startAnimation(animation);
    }

    private float calculateAzimuth() {
        return getStartLocation(location).bearingTo(getEndLocation());
    }

    private float calculateDistance() {
        return getStartLocation(location).distanceTo(getEndLocation());
    }

    private Location getStartLocation(Location location) {
        Location startLocation = new Location("");
        if (location != null) {
            startLocation.setLatitude(location.getLatitude());
            startLocation.setLongitude(location.getLongitude());
        }
        return startLocation;
    }

    private Location getEndLocation() {
        Location endLocation = new Location("");
        String latitude = sp.getString(CoordinateDialog.LATITUDE, "");
        String longitude = sp.getString(CoordinateDialog.LONGITUDE, "");

        if (latitude.length() > 0 && longitude.length() > 0) {
            endLocation.setLatitude(Double.parseDouble(latitude));
            endLocation.setLongitude(Double.parseDouble(longitude));
        }

        return endLocation;
    }

    private void updateCompassCustomView() {
        if (compassCustomView != null) {
            compassCustomView.updateData(calculateAzimuth());
        }
    }

    private void updateTvDistanceView() {
        if (tvDistance != null) {
            int distance = (int) calculateDistance();
            tvDistance.setText(context.getString(R.string.distance_to_point) + " " + String.valueOf(distance)
                    + " " + context.getString(R.string.meters));

            if (distance < SMALLEST_DISTANCE) {
                tvDistance.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            } else {
                tvDistance.setTextColor(context.getResources().getColor(android.R.color.black));
            }
        }
    }
    //endregion

    //region implementation SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = DEFAULT_ALPHA;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * event.values[0];
                geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * event.values[1];
                geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * event.values[2];
            }

            float matrixR[] = new float[ARRAY_SIZE_MATRIX];
            float matrixI[] = new float[ARRAY_SIZE_MATRIX];
            boolean success = SensorManager.getRotationMatrix(matrixR, matrixI, gravity, geomagnetic);
            if (success) {
                float orientation[] = new float[ARRAY_SIZE];
                SensorManager.getOrientation(matrixR, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + DEGREES_360) % DEGREES_360;

                updateCompassArrow();
                updateCompassCustomView();
                updateTvDistanceView();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    //endregion
}