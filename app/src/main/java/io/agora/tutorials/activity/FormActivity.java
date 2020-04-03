package io.agora.tutorials.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.tutorials.customizedvideosource.R;

public class FormActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //用户网名
    @BindView(R.id.form_client_screen_name)
    EditText formClientScreenName;
    //用户手机号
    @BindView(R.id.form_client_mobile)
    EditText formClientMobile;
    //用户性别
    @BindView(R.id.form_client_sex)
    EditText formClientSex;
    //车型
    @BindView(R.id.form_car_type)
    EditText formCarType;
    //用户级别
    @BindView(R.id.form_client_level)
    EditText formClientLevel;
    //是否愿意试驾
    @BindView(R.id.form_test_drive)
    EditText formTestDrive;
    //原因
    @BindView(R.id.form_cause)
    EditText formCause;
    //提交按钮
    @BindView(R.id.form_commit)
    Button formCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.form_commit)
    public void onViewClicked() {
    }
}
