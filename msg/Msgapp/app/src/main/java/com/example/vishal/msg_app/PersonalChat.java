package com.example.vishal.msg_app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;

public class PersonalChat extends Activity {
    private String usernumber,currentusername,currentusernumber;
    private FirebaseAuth mAuth;
    private TextView chatusername;
    private ListView personalchatlist;
    private EditText messagewrite;
    private Button send;
    private DatabaseReference sendref,reciveref;
    private Personalchatlistadepter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_chat);

        currentusername = getIntent().getStringExtra("currentusername").toString();
        currentusernumber = getIntent().getStringExtra("currentusernumber").toString();
        mAuth = FirebaseAuth.getInstance();
        usernumber = mAuth.getCurrentUser().getPhoneNumber().substring(3,mAuth.getCurrentUser().getPhoneNumber().length());
        sendref = FirebaseDatabase.getInstance().getReference(usernumber).child(currentusernumber);
        reciveref = FirebaseDatabase.getInstance().getReference(currentusernumber).child(usernumber);

        chatusername = (TextView)findViewById(R.id.chatusername);
        personalchatlist = (ListView)findViewById(R.id.personalchatlist);
        messagewrite = (EditText)findViewById(R.id.messagewrite);
        send = (Button)findViewById(R.id.sendbutton);


        chatusername.setText(currentusername);

        sendref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                new AsyncTask<Void, Void, LinkedList<Message>>() {
                    @Override
                    protected LinkedList<Message> doInBackground(Void... voids) {
                        LinkedList<Message> msglist = new LinkedList<Message>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            Message message = ds.getValue(Message.class);
                            msglist.add(message);
                        }
                        return msglist;
                    }

                    @Override
                    protected void onPostExecute(LinkedList<Message> messages) {

                        adapter = new Personalchatlistadepter(getApplicationContext(),messages);
                        personalchatlist.setAdapter(adapter);
                        personalchatlist.setSelection(messages.size()-1);
                        super.onPostExecute(messages);
                    }
                }.execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messagewrite.getText().toString().isEmpty()){ return; }
                sendref.push().setValue(new Message(messagewrite.getText().toString(), DateFormat.getDateTimeInstance().format(new Date()),true));
                reciveref.push().setValue(new Message(messagewrite.getText().toString(), DateFormat.getDateTimeInstance().format(new Date()),false));
                messagewrite.setText("");
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
