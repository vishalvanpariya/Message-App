package com.example.vishal.msg_app;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contactpage extends Fragment {

    private View view;
    private DatabaseReference ref;
    private TextView emptylist;
    private FirebaseAuth mAuth;

    public Contactpage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_contactpage, container, false);
        mAuth = FirebaseAuth.getInstance();
        emptylist = (TextView)view.findViewById(R.id.emptylist);
        String n = mAuth.getCurrentUser().getPhoneNumber();
        final Long num = Long.parseLong(n.substring(3,n.length()));


        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    200);
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    200);
            emptylist.setText("You does't give permission for contact");
            emptylist.setVisibility(View.VISIBLE);

        }else {
            emptylist.setVisibility(View.GONE);
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            final LinkedList<String> namelist = new LinkedList<String>();
            LinkedList<String> numberlist = new LinkedList<String>();

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    Cursor Phonecursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?", new String[]{id}, null);

                    List<Long> list = new LinkedList<Long>();

                    if (Phonecursor.getCount() > 0) {
                        list.clear();
                        while (Phonecursor.moveToNext()) {

                            long phonenumber = Phonecursor.getLong(Phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                            if (String.valueOf(phonenumber).length() > 9) {
                                if (String.valueOf(phonenumber).length() > 10 &&
                                        Integer.parseInt(String.valueOf(String.valueOf(phonenumber).charAt(0))) == 9 &&
                                        Integer.parseInt(String.valueOf(String.valueOf(phonenumber).charAt(1))) == 1) {

                                    Log.d(TAG, "onCreateView1: " + phonenumber);

                                    String temp = String.valueOf(phonenumber).substring(2, String.valueOf(phonenumber).length());
                                    if (list.size() == 0) {
                                        list.add(Long.parseLong(temp));
                                    } else {
                                        if (!(list.get(list.size() - 1).equals(Long.parseLong(temp)))) {
                                            list.add(Long.parseLong(temp));
                                        }
                                    }
                                } else {
                                    if (list.size() == 0) {
                                        list.add(phonenumber);
                                    } else {
                                        if (!(list.get(list.size() - 1).equals(phonenumber))) {
                                            list.add(phonenumber);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Phonecursor.close();

                    if (list.size() > 1) {
                        for (int i = 0; i < list.size(); i++) {
                            namelist.add(name);
                            numberlist.add(String.valueOf(list.get(i)));
                        }
                    } else if (list.size() == 1) {
                        namelist.add(name);
                        numberlist.add(String.valueOf(list.get(0)));
                    }
                }
            }
            cursor.close();


            final LinkedList<Contact> contactlist = new LinkedList<Contact>();


            for (int i = 0; i < namelist.size(); i++) {
                contactlist.add(new Contact(namelist.get(i), Long.parseLong(numberlist.get(i))));
            }

            final LinkedList<User> contactfromserver = new LinkedList<User>();


            ref = FirebaseDatabase.getInstance().getReference("Contact");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    new AsyncTask<Void, Void, LinkedList<MainContact>>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            contactfromserver.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                User user = ds.getValue(User.class);
                                contactfromserver.add(user);
                            }
                        }

                        @Override
                        protected LinkedList<MainContact> doInBackground(Void... voids) {
                            LinkedList<MainContact> userlist = new LinkedList<MainContact>();
                            userlist.clear();
                            for (int i = 0; i < contactfromserver.size(); i++) {
                                for (int j = 0; j < contactlist.size(); j++) {
                                    if (contactlist.get(j).getNumber().equals(Long.parseLong(contactfromserver.get(i).getNumber()))) {
                                        if (!num.equals(Long.parseLong(contactfromserver.get(i).getNumber()))) {
                                            userlist.add(new MainContact(contactlist.get(j).getName(), contactlist.get(j).getNumber(), contactfromserver.get(i).getUserId()));
                                            j = contactlist.size();
                                        }
                                    }
                                }
                            }
                            return userlist;
                        }

                        @Override
                        protected void onPostExecute(final LinkedList<MainContact> contacts) {
                            super.onPostExecute(contacts);
                            if (contacts.size()==0){
                                emptylist.setText("no user use this app from your contact");
                                emptylist.setVisibility(View.VISIBLE);
                            }
                            if (contacts.size()!=0){
                                emptylist.setVisibility(View.GONE);
                            }
                            ListView listView = (ListView) view.findViewById(R.id.contact_listview);
                            ListviewAdapter adapter = new ListviewAdapter(getContext(), contacts);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(getActivity(),PersonalChat.class);
                                    intent.putExtra("currentusername",contacts.get(i).getName());
                                    intent.putExtra("currentusernumber",String.valueOf(contacts.get(i).getNumber()));
                                    startActivity(intent);
                                }
                            });
                        }
                    }.execute();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        return view;
    }
   public void permissiondranted(){

       String n = mAuth.getCurrentUser().getPhoneNumber();
       final Long num = Long.parseLong(n.substring(3,n.length()));

       emptylist.setVisibility(View.GONE);
       ContentResolver resolver = getActivity().getContentResolver();
       Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

       LinkedList<String> namelist = new LinkedList<String>();
       LinkedList<String> numberlist = new LinkedList<String>();

       if (cursor.getCount() > 0) {
           while (cursor.moveToNext()) {
               String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
               String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


               Cursor Phonecursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                       ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?", new String[]{id}, null);

               List<Long> list = new LinkedList<Long>();

               if (Phonecursor.getCount() > 0) {
                   list.clear();
                   while (Phonecursor.moveToNext()) {

                       long phonenumber = Phonecursor.getLong(Phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                       if (String.valueOf(phonenumber).length() > 9) {
                           if (String.valueOf(phonenumber).length() > 10 &&
                                   Integer.parseInt(String.valueOf(String.valueOf(phonenumber).charAt(0))) == 9 &&
                                   Integer.parseInt(String.valueOf(String.valueOf(phonenumber).charAt(1))) == 1) {

                               Log.d(TAG, "onCreateView1: " + phonenumber);

                               String temp = String.valueOf(phonenumber).substring(2, String.valueOf(phonenumber).length());
                               if (list.size() == 0) {
                                   list.add(Long.parseLong(temp));
                               } else {
                                   if (!(list.get(list.size() - 1).equals(Long.parseLong(temp)))) {
                                       list.add(Long.parseLong(temp));
                                   }
                               }
                           } else {
                               if (list.size() == 0) {
                                   list.add(phonenumber);
                               } else {
                                   if (!(list.get(list.size() - 1).equals(phonenumber))) {
                                       list.add(phonenumber);
                                   }
                               }
                           }
                       }
                   }
               }
               Phonecursor.close();

               if (list.size() > 1) {
                   for (int i = 0; i < list.size(); i++) {
                       namelist.add(name);
                       numberlist.add(String.valueOf(list.get(i)));
                   }
               } else if (list.size() == 1) {
                   namelist.add(name);
                   numberlist.add(String.valueOf(list.get(0)));
               }
           }
       }
       cursor.close();


       final LinkedList<Contact> contactlist = new LinkedList<Contact>();


       for (int i = 0; i < namelist.size(); i++) {
           contactlist.add(new Contact(namelist.get(i), Long.parseLong(numberlist.get(i))));
       }
       Log.d(TAG, "onCreateView: " + contactlist.size());
       final LinkedList<User> contactfromserver = new LinkedList<User>();


       ref = FirebaseDatabase.getInstance().getReference("Contact");
       ref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(final DataSnapshot dataSnapshot) {
               new AsyncTask<Void, Void, LinkedList<MainContact>>() {

                   @Override
                   protected void onPreExecute() {
                       super.onPreExecute();
                       contactfromserver.clear();
                       for (DataSnapshot ds : dataSnapshot.getChildren()) {
                           User user = ds.getValue(User.class);
                           contactfromserver.add(user);
                       }
                   }

                   @Override
                   protected LinkedList<MainContact> doInBackground(Void... voids) {
                       LinkedList<MainContact> userlist = new LinkedList<MainContact>();
                       userlist.clear();
                       for (int i = 0; i < contactfromserver.size(); i++) {
                           for (int j = 0; j < contactlist.size(); j++) {
                               if (contactlist.get(j).getNumber().equals(Long.parseLong(contactfromserver.get(i).getNumber()))) {
                                   if (!num.equals(Long.parseLong(contactfromserver.get(i).getNumber()))) {
                                       userlist.add(new MainContact(contactlist.get(j).getName(), contactlist.get(j).getNumber(), contactfromserver.get(i).getUserId()));
                                       j = contactlist.size();
                                   }
                               }
                           }
                       }
                       return userlist;
                   }

                   @Override
                   protected void onPostExecute(final LinkedList<MainContact> contacts) {
                       super.onPostExecute(contacts);
                       if (contacts.size()==0){
                           emptylist.setText("no user use this app from your contact");
                           emptylist.setVisibility(View.VISIBLE);
                       }
                       if (contacts.size()!=0){
                           emptylist.setVisibility(View.GONE);
                       }
                       ListView listView = (ListView) view.findViewById(R.id.contact_listview);
                       ListviewAdapter adapter = new ListviewAdapter(getContext(), contacts);
                       listView.setAdapter(adapter);
                       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                           @Override
                           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                               Intent intent = new Intent(getActivity(),PersonalChat.class);
                               intent.putExtra("currentusername",contacts.get(i).getName());
                               intent.putExtra("currentusernumber",String.valueOf(contacts.get(i).getNumber()));
                               startActivity(intent);
                           }
                       });
                   }
               }.execute();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
            permissiondranted();
        }else {
            emptylist.setText("You does't give permission for contact");
            emptylist.setVisibility(View.VISIBLE);
        }
    }
}
