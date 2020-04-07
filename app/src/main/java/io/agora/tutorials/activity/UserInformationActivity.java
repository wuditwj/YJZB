package io.agora.tutorials.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.db.UserDatabase;
import io.agora.tutorials.entity.UserInfo;
import io.agora.tutorials.utils.CircleTransform;

public class UserInformationActivity extends AppCompatActivity {

    @BindView(R.id.user_toolbar)
    Toolbar userToolbar;
    @BindView(R.id.user_info_head)
    ImageView userInfoHead;
    @BindView(R.id.user_info_name)
    TextView userInfoName;
    @BindView(R.id.user_info_mobile)
    TextView userInfoMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        ButterKnife.bind(this);
        info();
    }

    private void info() {
        setSupportActionBar(userToolbar);
        //设置是否有NvagitionIcon（返回图标）
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //点击返回图标
        userToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        String head = userInfo.getPhoto();
        Glide.with(this).load(head).into(userInfoHead);
        String name = userInfo.getNickname();
        userInfoName.setText(name);
        String mobile = userInfo.getMobile();
        userInfoMobile.setText(mobile);
    }
}
