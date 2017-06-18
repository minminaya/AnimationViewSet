package com.minminaya.animationviewset.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.minminaya.animationviewset.R;
import com.minminaya.animationviewset.view.ErrorView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ErrorViewActivity extends AppCompatActivity {

    @Bind(R.id.error_view)
    ErrorView errorView;

    @Bind(R.id.error_view_button)
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_view);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.error_view_button)
    public void onViewClicked() {
        errorView.startAnimation(300);
        errorView.addOnPaintFinishListener(new ErrorView.OnPaintFinishListener() {
            @Override
            public void onPaintEnd() {
                errorView.setPaintColor(Color.RED);
            }
        });
    }
}
