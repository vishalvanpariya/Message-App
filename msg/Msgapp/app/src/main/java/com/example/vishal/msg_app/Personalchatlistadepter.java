package com.example.vishal.msg_app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class Personalchatlistadepter extends BaseAdapter {

    private Context context;
    private LinkedList<Message> chatlist;

    public Personalchatlistadepter(Context context, LinkedList<Message> chatlist) {
        this.context = context;
        this.chatlist = chatlist;
    }

    @Override
    public int getCount() {
        return chatlist.size();
    }

    @Override
    public Object getItem(int i) {
        return chatlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = null;
        if (!chatlist.get(i).isFlag()) {
            v = View.inflate(context, R.layout.currentuserlayout, null);
            TextView msg = (TextView)v.findViewById(R.id.message);
            TextView time = (TextView)v.findViewById(R.id.time);

            msg.setText(chatlist.get(i).getMsg());
            time.setText(chatlist.get(i).getTime());
        }
        if (chatlist.get(i).isFlag()){
            v = View.inflate(context,R.layout.userlayout,null);
            TextView msg = (TextView)v.findViewById(R.id.message);
            TextView time = (TextView)v.findViewById(R.id.time);

            msg.setText(chatlist.get(i).getMsg());
            time.setText(chatlist.get(i).getTime());
        }
        return v;
    }
}
