package io.agora.tutorials.adapter;

import android.content.Context;
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

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
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
            viewHold.name=view.findViewById(R.id.form_list_item_name);
            viewHold.car=view.findViewById(R.id.form_list_item_car_type);
            viewHold.drive=view.findViewById(R.id.form_list_item_drive);
            view.setTag(viewHold);
        }else{
            viewHold= (ViewHold) view.getTag();
        }
        return view;
    }

    class ViewHold {
        private TextView name, car, drive;
    }
}
