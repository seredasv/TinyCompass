package com.ssv.tinycompass.views.activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ssv.tinycompass.R;
import com.ssv.tinycompass.presenters.CompassPresenter;
import com.ssv.tinycompass.presenters.CompassPresenterImpl;
import com.ssv.tinycompass.utils.CompassCustomView;
import com.ssv.tinycompass.views.dialogs.CoordinateDialog;

public class CompassActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    //region fields
    private CompassPresenter compassPresenter;
    private Button btnLatitude, btnLongitude;
    private ImageView arrowView;
    private CompassCustomView compassCustomView;
    private TextView tvDistance;
    //endregion

    //region activity lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        findView();

        compassPresenter = new CompassPresenterImpl(this);

        compassPresenter.setArrowView(arrowView);
        compassPresenter.setCompassCustomView(compassCustomView);
        compassPresenter.setTvDistanceView(tvDistance);

        btnLatitude.setOnClickListener(this);
        btnLongitude.setOnClickListener(this);

        registerLocationListener(LocationManager.GPS_PROVIDER, 0, 0, this);
        registerLocationListener(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        compassPresenter.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compassPresenter.unregisterListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compassPresenter.registerListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compassPresenter.unregisterListener();
    }
    //endregion

    //region methods
    private void findView() {
        btnLatitude = (Button) findViewById(R.id.btn_latitude);
        btnLongitude = (Button) findViewById(R.id.btn_longitude);
        arrowView = (ImageView) findViewById(R.id.main_image_hands);
        compassCustomView = (CompassCustomView) findViewById(R.id.compass_view);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
    }

    private void openDialogFragment(String title, String coordinateType) {
        FragmentManager fm = getFragmentManager();
        CoordinateDialog dialog = CoordinateDialog.newInstance(title, coordinateType);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog.show(fm, "");
    }

    private void registerLocationListener(String provider, long minTime, float minDistance, LocationListener listener) {
        try {
            ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .requestLocationUpdates(provider, minTime, minDistance, listener);
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.gps_security_exception, Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region implementation of View.OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_latitude:
                openDialogFragment(getString(R.string.please_enter) + " " + CoordinateDialog.LATITUDE, CoordinateDialog.LATITUDE);
                break;
            case R.id.btn_longitude:
                openDialogFragment(getString(R.string.please_enter) + " " + CoordinateDialog.LONGITUDE, CoordinateDialog.LONGITUDE);
                break;
        }
    }
    //endregion

    //region implementation of LocationListener
    @Override
    public void onLocationChanged(Location location) {
        compassPresenter.setCurrentCoordinates(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    //endregion
}
