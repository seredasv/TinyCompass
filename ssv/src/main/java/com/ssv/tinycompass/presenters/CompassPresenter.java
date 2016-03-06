package com.ssv.tinycompass.presenters;

import android.location.Location;
import android.widget.ImageView;
import android.widget.TextView;
import com.ssv.tinycompass.utils.CompassCustomView;

public interface CompassPresenter {
    void setArrowView(ImageView arrowView);

    void setCompassCustomView(CompassCustomView compassCustomView);

    void setTvDistanceView(TextView tvDistanceView);

    void setCurrentCoordinates(Location location);

    void registerListener();

    void unregisterListener();
}
