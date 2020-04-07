package io.agora.tutorials.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    //用户性别
    @BindView(R.id.form_client_sex)
    NiceSpinner formClientSex;
    //车型
    @BindView(R.id.form_car_type)
    EditText formCarType;
    //用户级别
    @BindView(R.id.form_client_level)
    NiceSpinner formClientLevel;
    //是否愿意试驾
    @BindView(R.id.form_test_drive)
    NiceSpinner formTestDrive;
    //原因
    @BindView(R.id.form_cause)
    EditText formCause;
    //提交按钮
    @BindView(R.id.form_commit)
    Button formCommit;
    //原因框
    @BindView(R.id.layout_cause)
    LinearLayout layoutCause;

    private ClientCommitInfo commitInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
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
        NetClient.getInstance().getTreatrueApi().getClientCommitInfo(3, 18).enqueue(new Callback<ClientFormStatus>() {
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
        formCarType.setText(clientCommitInfo.getType_name());
        //客户级别
        fillInLevel(clientCommitInfo.getLevel());
        //试驾
        fillInDrive(clientCommitInfo.getBy_car());
    }

    /**
     * 性别选择框
     *
     * @return
     */
    private void fillInSex(int sex) {
        final LinkedList<String> data = new LinkedList<>(Arrays.asList("男", "女"));
        formClientSex.attachDataSource(data);
        if (sex == 1 || sex == 2) {
            formClientSex.setSelectedIndex(sex - 1);
        } else {
            formClientSex.setSelectedIndex(0);
        }
        commitInfo.setSex(formClientSex.getSelectedIndex() + 1);
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
        final LinkedList<String> data = new LinkedList<>(Arrays.asList("是", "否"));
        formTestDrive.attachDataSource(data);
        switch (flag) {
            case 0:
            case 1:
                formTestDrive.setSelectedIndex(0);
                layoutCause.setVisibility(View.GONE);
                break;
            case 2:
                formTestDrive.setSelectedIndex(1);
                layoutCause.setVisibility(View.VISIBLE);
                break;
        }

        formTestDrive.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        layoutCause.setVisibility(View.GONE);
                        break;
                    case 1:
                        layoutCause.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        commitInfo.setBy_car(formTestDrive.getSelectedIndex());
    }

    @OnClick(R.id.form_commit)
    public void onViewClicked() {
        commitInfo.setUsername(formClientScreenName.getText().toString().trim());
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
                        } else {
                            Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.i("--==>>", "未知错误");
                    }
                }
                ;
            }

            @Override
            public void onFailure(Call<CallStatus> call, Throwable t) {
                Log.i("--==>>", "提交数据请求失败" + t.getMessage());
            }
        });
    }
}
