package com.ssv.tinycompass.presenters;

import android.content.Context;
import android.location.Location;
import android.widget.ImageView;
import android.widget.TextView;
import com.ssv.tinycompass.utils.CompassCustomView;
import com.ssv.tinycompass.views.Compass;

public class CompassPresenterImpl implements CompassPresenter {
    private Compass compass;

    public CompassPresenterImpl(Context context) {
        compass = Compass.getInstance(context);
    }

    @Override
    public void setArrowView(ImageView arrowView) {
        if (compass != null && arrowView != null) {
            compass.setArrowView(arrowView);
        }
    }

    @Override
    public void setCompassCustomView(CompassCustomView compassCustomView) {
        if (compass != null && compassCustomView != null) {
            compass.setCompassCustomView(compassCustomView);
        }
    }

    @Override
    public void setTvDistanceView(TextView tvDistanceView) {
        if (compass != null && tvDistanceView != null) {
            compass.setTvDistanceView(tvDistanceView);
        }
    }

    @Override
    public void setCurrentCoordinates(Location location) {
        if (compass != null && location != null) {
            compass.setCurrentCoordinates(location);
        }
    }

    @Override
    public void registerListener() {
        if (compass != null) {
            compass.registerListener();
        }
    }

    @Override
    public void unregisterListener() {
        if (compass != null) {
            compass.unregisterListener();
        }
    }
}
