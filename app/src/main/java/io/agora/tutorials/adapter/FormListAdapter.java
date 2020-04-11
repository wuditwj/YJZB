package io.agora.tutorials.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.entity.FormInfo;

public class FormListAdapter extends BaseAdapter {
    public List<FormInfo> list;
    private Context context;

    public FormListAdapter(Context context) {
        this.list = new ArrayList<>();
        this.context = context;
    }

    public void add(FormInfo formInfo) {
        list.add(formInfo);
    }

    public void clear(){
        list.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        Log.i("--==>>","i="+i);
        Log.i("--==>>","信息:"+list.get(i-1).toString());
        return list.get(i-1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHold viewHold;
        if (view == null) {
            viewHold = new ViewHold();
            view = View.inflate(context, R.layout.item_list_form, null);
            viewHold.name = view.findViewById(R.id.form_list_item_name);
            viewHold.car = view.findViewById(R.id.form_list_item_car_type);
            viewHold.drive = view.findViewById(R.id.form_list_item_drive);
            view.setTag(viewHold);
        } else {
            viewHold = (ViewHold) view.getTag();
        }
        FormInfo formInfo = list.get(i);
        viewHold.name.setText(formInfo.getUsername());
        viewHold.car.setText(formInfo.getType_name());
        if (formInfo.getBay_car() == 1) {
            viewHold.drive.setText("是");
        } else {
            viewHold.drive.setText("否");
        }
        return view;
    }

    class ViewHold {
        private TextView name, car, drive;
    }
}
