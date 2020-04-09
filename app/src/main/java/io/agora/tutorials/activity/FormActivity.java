package io.agora.tutorials.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.entity.CallStatus;
import io.agora.tutorials.entity.ClientCommitInfo;
import io.agora.tutorials.entity.ClientFormStatus;
import io.agora.tutorials.net.NetClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //用户网名
    @BindView(R.id.form_client_screen_name)
    EditText formClientScreenName;
    //用户手机号
    @BindView(R.id.form_client_mobile)
    EditText formClientMobile;
    //用户性别 男
    @BindView(R.id.form_rb_man)
    RadioButton formRbMan;
    //用户性别 女
    @BindView(R.id.form_rb_woman)
    RadioButton formRbWoman;
    //车型
    @BindView(R.id.form_car_type)
    NiceSpinner formCarType;
    //用户级别
    @BindView(R.id.form_client_level)
    NiceSpinner formClientLevel;
    //是否愿意试驾 是
    @BindView(R.id.form_rb_yes)
    RadioButton formRbYes;
    //是否愿意试驾 否
    @BindView(R.id.form_rb_no)
    RadioButton formRbNo;
    //原因
    @BindView(R.id.form_cause)
    EditText formCause;
    //原因框
    @BindView(R.id.layout_cause)
    LinearLayout layoutCause;
    //提交按钮
    @BindView(R.id.form_commit)
    ImageView formCommit;
    //性别
    @BindView(R.id.form_sex_group)
    RadioGroup formSexGroup;
    //试驾
    @BindView(R.id.form_drive_group)
    RadioGroup formDriveGroup;

    private ClientCommitInfo commitInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
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
     * 获取用户填写的form表单
     */
    private void getClientForm() {
        int adminId = MyApplication.getInstance().getUserInfo().getAdmin_id();
        int userId = MyApplication.getInstance().getClientInfo().getData().getUser_id();
        Log.i("--==>>", adminId + "     " + userId);
        NetClient.getInstance().getTreatrueApi().getClientCommitInfo(adminId, userId).enqueue(new Callback<ClientFormStatus>() {
            @Override
            public void onResponse(Call<ClientFormStatus> call, Response<ClientFormStatus> response) {
                if (response.isSuccessful()) {
                    ClientFormStatus clientFormStatus = response.body();
                    if (clientFormStatus != null) {
                        commitInfo = clientFormStatus.getData();
                        fullInForm(commitInfo);
                        Log.i("--==>>", commitInfo.toString());
                    } else {
                        Log.i("--==>>", "未知错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<ClientFormStatus> call, Throwable t) {
                Log.i("--==>>", "查询用户填写form表单请求失败" + t.getMessage());
                Toast.makeText(getApplicationContext(), "用户未提交预约", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * 自动填充已有内容
     *
     * @param clientCommitInfo
     */
    private void fullInForm(ClientCommitInfo clientCommitInfo) {
        //网名
        formClientScreenName.setText(clientCommitInfo.getUsername());
        //手机号
        formClientMobile.setText(clientCommitInfo.getMobile());
        //性别
        fillInSex(clientCommitInfo.getSex());
        //车型
        fillInCarType(clientCommitInfo.getType_name());
        //客户级别
        fillInLevel(clientCommitInfo.getLevel());
//        //试驾
        fillInDrive(clientCommitInfo.getBy_car());
    }

    /**
     * 性别选择框
     *
     * @return
     */
    private void fillInSex(int sex) {
        switch (sex) {
            case 1:
                formRbMan.setChecked(true);
                break;
            case 2:
                formRbWoman.setChecked(true);
                break;
            default:
                formRbMan.setChecked(true);
                break;
        }
        formSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton RB = (RadioButton) findViewById(i);//获取被选择的单选按钮
                if (RB.getText().equals("男")) {
                    commitInfo.setSex(1);
                } else {
                    commitInfo.setSex(2);
                }
            }
        });
    }

    /**
     * 级别选择框
     *
     * @return
     */
    private void fillInCarType(String type) {
        final LinkedList<String> data = new LinkedList<>(Arrays.asList("新楼兰", "全新奇骏", "全新途达", "全新劲客", "新轩逸·经典", "蓝鸟", "骐达TIIDA", "第14代轩逸", "第七代天籁"));
        formCarType.attachDataSource(data);
        if (type != null) {
            formCarType.setText(type);
        } else {
            formCarType.setSelectedIndex(0);
        }
        commitInfo.setType_name(data.get(formCarType.getSelectedIndex()));
    }

    /**
     * 级别选择框
     *
     * @return
     */
    private void fillInLevel(String level) {
        final LinkedList<String> data = new LinkedList<>(Arrays.asList("H", "A", "B", "C"));
        formClientLevel.attachDataSource(data);
        if (level != null) {
            formClientLevel.setText(level);
        } else {
            formClientLevel.setSelectedIndex(0);
        }
        commitInfo.setLevel(data.get(formClientLevel.getSelectedIndex()));
    }

    /**
     * 试驾选择框
     *
     * @return
     */
    private void fillInDrive(int flag) {
        switch (flag) {
            case 1:
                formRbYes.setChecked(true);
                layoutCause.setVisibility(View.GONE);
                break;
            case 2:
                formRbNo.setChecked(true);
                layoutCause.setVisibility(View.VISIBLE);
                formCause.setText(commitInfo.getContents());
                break;
            default:
                formRbYes.setChecked(true);
                layoutCause.setVisibility(View.GONE);
                break;
        }
        formDriveGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton RB = (RadioButton) findViewById(i);//获取被选择的单选按钮
                if (RB.getText().equals("是")) {
                    commitInfo.setBy_car(1);
                    layoutCause.setVisibility(View.GONE);
                } else {
                    commitInfo.setBy_car(2);
                    layoutCause.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick(R.id.form_commit)
    public void onViewClicked() {
        String name = formClientScreenName.getText().toString().trim();
        if (name.isEmpty()) {
            commitInfo.setUsername("空");
        } else {
            commitInfo.setUsername(name);
        }
        commitInfo.setMobile(formClientMobile.getText().toString().trim());
        commitInfo.setType_name(formCarType.getText().toString().trim());
        commitInfo.setContents(formCause.getText().toString().trim());
        String jsonObject = new Gson().toJson(commitInfo);
        Log.i("--==>>", jsonObject);
        NetClient.getInstance().getTreatrueApi().commitClientInfo(jsonObject).enqueue(new Callback<CallStatus>() {
            @Override
            public void onResponse(Call<CallStatus> call, Response<CallStatus> response) {
                if (response.isSuccessful()) {
                    CallStatus closeCall = response.body();
                    if (closeCall != null) {
                        String status = closeCall.getStatus();
                        Log.i("--==>>", status);
                        if (status.equals("success")) {
                            Log.i("--==>>", "数据提交成功");
                            Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.i("--==>>", "未知错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<CallStatus> call, Throwable t) {
                Log.i("--==>>", "提交数据请求失败" + t.getMessage());
            }
        });
    }
}
