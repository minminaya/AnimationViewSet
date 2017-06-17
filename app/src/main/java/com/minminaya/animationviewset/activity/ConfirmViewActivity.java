package com.minminaya.animationviewset.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.minminaya.animationviewset.R;
import com.minminaya.animationviewset.view.ConfirmView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmViewActivity extends AppCompatActivity {


    @Bind(R.id.view)
    ConfirmView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_view);
        ButterKnife.bind(this);
        view.addCircleAnimatorEndListener(new ConfirmView.onCircleFinishListener() {
            @Override
            public void onCircleDone() {
//                结束时的颜色
                view.setPaintColor(Color.BLACK);
            }
        });
    }

    @OnClick(R.id.view)
    public void onViewClicked() {
        view.loadCircle(300);
    }
}
