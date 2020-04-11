package io.agora.tutorials.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.Visibility;

import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.entity.ClientCommitInfo;
import io.agora.tutorials.entity.FormInfo;

public class FormInformationActivity extends AppCompatActivity {

    //状态栏
    @BindView(R.id.form_status_bar)
    View formStatusBar;
    //标题栏
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //名字
    @BindView(R.id.form_info_name)
    TextView formInfoName;
    //手机号
    @BindView(R.id.form_info_mobile)
    TextView formInfoMobile;
    //性别
    @BindView(R.id.form_info_sex)
    TextView formInfoSex;
    //车型
    @BindView(R.id.form_info_car)
    TextView formInfoCar;
    //级别
    @BindView(R.id.form_info_level)
    TextView formInfoLevel;
    //试驾
    @BindView(R.id.form_info_drive)
    TextView formInfoDrive;
    //原因
    @BindView(R.id.form_info_cause)
    TextView formInfoCause;
    //原因框
    @BindView(R.id.layout_cause)
    LinearLayout layoutCause;

    private ClientCommitInfo commitInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_information);
        //初始化状态栏的高度
        View statusbar = (View) findViewById(R.id.form_status_bar);
        //沉浸式状态栏
        ImmersionBar.with(this).statusBarView(statusbar).statusBarDarkFont(true, 0.2f).init();
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //设置是否有NvagitionIcon（返回图标）
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //点击返回图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //自动填充内容
        getClientForm();
    }

    /**
     * 获取form表单
     */
    private void getClientForm() {
        Intent intent = this.getIntent();
        FormInfo formInfo = (FormInfo) intent.getSerializableExtra("formInfo");
        fullInForm(formInfo);
    }

    /**
     * 自动填充已有内容
     */
    private void fullInForm(FormInfo formInfo) {
        formInfoName.setText(formInfo.getUsername());
        formInfoMobile.setText(formInfo.getMobile());
        formInfoSex.setText(formInfo.getSex() == 1 ? this.getResources().getString(R.string.form_man) : this.getResources().getString(R.string.form_woman));
        formInfoCar.setText(formInfo.getType_name());
        formInfoLevel.setText(formInfo.getLevel());
        if (formInfo.getBay_car() == 1) {
            formInfoDrive.setText(this.getResources().getString(R.string.form_yes));
            layoutCause.setVerticalGravity(View.GONE);
        } else {
            formInfoDrive.setText(this.getResources().getString(R.string.form_no));
            layoutCause.setVerticalGravity(View.VISIBLE);
            formInfoCause.setText(formInfo.getContents());
        }
    }
}
