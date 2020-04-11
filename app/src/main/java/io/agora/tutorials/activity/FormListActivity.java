package io.agora.tutorials.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gyf.immersionbar.ImmersionBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.tutorials.adapter.FormListAdapter;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.entity.FormInfo;
import io.agora.tutorials.entity.FormListInfo;
import io.agora.tutorials.net.NetClient;
import io.agora.tutorials.widget.XListView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormListActivity extends AppCompatActivity implements XListView.IXListViewListener {

    @BindView(R.id.form_status_bar)
    View formStatusBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list_form)
    XListView mListView;

    private FormListAdapter mAdapter;
    private Handler mHandler;
    private int indexPage = 1;
    private int totalPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
        //初始化状态栏的高度
        View statusbar = (View) findViewById(R.id.form_status_bar);
        //沉浸式状态栏
        ImmersionBar.with(this).statusBarView(statusbar).statusBarDarkFont(true, 0.2f).init();
        ButterKnife.bind(this);
        //给集合添加数据
//        geneItems();
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
        mHandler = new Handler();
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(getTime());

        mAdapter = new FormListAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = mAdapter.getItem(i);
                if (item != null) {
                    FormInfo formInfo = (FormInfo) item;
                    Intent intent = new Intent(FormListActivity.this, FormInformationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("formInfo", formInfo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });

//        getFormList(indexPage);
    }


    @Override
    public void onRefresh() {
        Log.i("--==>>", "下滑");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.clear();
                indexPage = 1;
                getFormList(indexPage);
                mAdapter = new FormListAdapter(FormListActivity.this);
                mListView.setAdapter(mAdapter);
                onLoad();
            }
        }, 0);
    }

    @Override
    public void onLoadMore() {
        Log.i("--==>>", "上拉");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (indexPage == totalPage) {
                    Toast.makeText(FormListActivity.this, "没有数据了", Toast.LENGTH_SHORT).show();
                } else {
                    getFormList(++indexPage);
                    mAdapter.notifyDataSetChanged();
                }
                onLoad();
            }
        }, 0);
    }

    private void getFormList(int page) {
        Log.i("--==>>", "page:" + page);
        NetClient.getInstance().getTreatrueApi().getFormList(MyApplication.getInstance().getUserInfo().getUser_id(), page).enqueue(new Callback<FormListInfo>() {
            @Override
            public void onResponse(Call<FormListInfo> call, Response<FormListInfo> response) {
                if (response.isSuccessful()) {
                    FormListInfo formListInfo = response.body();
                    if (formListInfo != null) {
                        totalPage = formListInfo.getTotalPages();
                        FormInfo[] data = formListInfo.getData();
                        for (FormInfo formInfo : data) {
                            mAdapter.add(formInfo);
                        }
                        Log.i("--==>>", formListInfo.toString());
                    } else {
                        Log.i("--==>>", "未知错误");
                    }
                } else {
                    Log.i("--==>>", "rs:" + response.code());
                }
            }

            @Override
            public void onFailure(Call<FormListInfo> call, Throwable t) {
                Log.i("--==>>", "查询表单列表信息请求失败" + t.getMessage());
            }
        });
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            mListView.autoRefresh();
        }
    }
}
