package com.example.vishal.msg_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class ListviewAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<MainContact> contactlist;

    public ListviewAdapter(Context context, LinkedList<MainContact> contactlist) {
        this.context = context;
        this.contactlist = contactlist;
    }

    @Override
    public int getCount() {
        return contactlist.size();
    }

    @Override
    public Object getItem(int i) {
        return contactlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context,R.layout.listrow,null);
        TextView name = (TextView)v.findViewById(R.id.name);
        TextView number = (TextView)v.findViewById(R.id.number);

        name.setText(contactlist.get(i).getName());
        number.setText(contactlist.get(i).getNumber().toString());
        return v;
    }
}
