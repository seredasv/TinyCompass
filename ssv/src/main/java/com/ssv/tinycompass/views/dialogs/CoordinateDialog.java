package com.ssv.tinycompass.views.dialogs;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ssv.tinycompass.R;

public class CoordinateDialog extends DialogFragment {
    //region constants
    //region public constants
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    //endregion
    //region private constants
    private static final String COORDINATE_TYPE = "coordinate_type";
    private static final String TITLE = "title";
    //endregion
    //endregion

    //region fields
    private TextView tvTitle;
    private EditText etSetCoordinate;
    private Button btnSetCoordinate;
    private SharedPreferences sp;
    private String coordinateType = "", title = "";
    //endregion

    //region static methods
    public static CoordinateDialog newInstance(String title, String coordinateType) {
        CoordinateDialog fragment = new CoordinateDialog();

        Bundle args = new Bundle();
        args.putString(COORDINATE_TYPE, coordinateType);
        args.putString(TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }
    //endregion

    //region dialog lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        title = getArguments().getString(TITLE);
        coordinateType = getArguments().getString(COORDINATE_TYPE);
    }

    @Override
    public void onResume() {
        super.onResume();

        setCurrentDialogLayout();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_set_coordinate, container, false);

        findView(view);

        tvTitle.setText(title);

        btnSetCoordinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditTextData();
                dismiss();
            }
        });

        setEditTextData();

        return view;
    }
    //endregion

    //region methods
    private void findView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        etSetCoordinate = (EditText) view.findViewById(R.id.et_set_coordinate);
        btnSetCoordinate = (Button) view.findViewById(R.id.btn_set_coordinate);
    }

    private void setCurrentDialogLayout() {
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void setEditTextData() {
        if (etSetCoordinate != null && sp != null) {
            if (coordinateType.contains(LATITUDE)) {
                etSetCoordinate.setText(sp.getString(LATITUDE, ""));
            } else {
                etSetCoordinate.setText(sp.getString(LONGITUDE, ""));
            }
        }
    }

    private void saveEditTextData() {
        if (etSetCoordinate != null) {
            String coordinate = etSetCoordinate.getText().toString();
            if (coordinate.length() > 0 && sp != null) {
                if (coordinateType.contains(LATITUDE)) {
                    sp.edit().putString(LATITUDE, coordinate).apply();
                } else {
                    sp.edit().putString(LONGITUDE, coordinate).apply();
                }
            }
        }
    }
    //endregion
}
