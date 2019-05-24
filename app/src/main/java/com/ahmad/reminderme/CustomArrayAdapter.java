package com.ahmad.reminderme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> list;

    public CustomArrayAdapter(@NonNull Context context, List<String> list) {
        super(context, 0 , list);
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
        v = LayoutInflater.from(mContext).inflate(R.layout.dropdown_item, parent, false);

        TextView tv_item = v.findViewById(R.id.tv_item);
        tv_item.setText(list.get(position));

        return v;
    }
}
