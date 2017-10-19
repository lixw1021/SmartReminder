package com.xianwei.smartreminder.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xianwei.smartreminder.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditTimeFragment extends Fragment {


    public EditTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_time, container, false);
    }

}
