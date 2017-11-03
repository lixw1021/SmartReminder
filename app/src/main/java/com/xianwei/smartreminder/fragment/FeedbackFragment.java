package com.xianwei.smartreminder.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xianwei.smartreminder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {
    @BindView(R.id.btn_feedback)
    Button button;
    @BindView(R.id.et_feedback)
    EditText editText;

    private static final String URI_PARSE = "mailto:";

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_feedback)
    void seedFeedback() {
        String feedback = editText.getText().toString().trim();
        if (!feedback.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse(URI_PARSE));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.feedback_email)});
            intent.putExtra(Intent.EXTRA_SUBJECT, editText.getText().toString());
            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            Toast.makeText(getContext(), R.string.toast_feedback, Toast.LENGTH_LONG).show();
        }
    }
}
