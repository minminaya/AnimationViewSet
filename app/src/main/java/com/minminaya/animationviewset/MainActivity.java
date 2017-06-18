package com.minminaya.animationviewset;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.minminaya.animationviewset.activity.ConfirmViewActivity;
import com.minminaya.animationviewset.activity.ErrorViewActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.activity_confirm)
    Button activityConfirm;
    @Bind(R.id.activity_errorview)
    Button activityErrorview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.activity_confirm, R.id.activity_errorview})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.activity_confirm:
                intent = new Intent(this, ConfirmViewActivity.class);

                break;
            case R.id.activity_errorview:
                intent = new Intent(this, ErrorViewActivity.class);
                break;
        }
        startActivity(intent);
    }

}
