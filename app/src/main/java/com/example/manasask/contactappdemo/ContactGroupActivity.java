package com.example.manasask.contactappdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.manasask.contactappdemo.Adapter.ContactGroupViewAdapter;
import com.example.manasask.contactappdemo.data.MySQLiteHelper;
import com.example.manasask.contactappdemo.utils.AndroidProjUtils;

import java.util.ArrayList;

public class ContactGroupActivity extends AppCompatActivity implements View.OnLongClickListener
{
    ArrayList<String> selection_list=new ArrayList<>();
    public static boolean iscontextmode=false;
    RecyclerView recyclerView;
    RecyclerView listViewContact;
    ContactGroupViewAdapter contactGroupViewAdapter;
    MySQLiteHelper mySQLiteHelper;

    String[] items = {"Friends","Family","Colleagues","Schoolmates","VIPs"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        setTitle("Groups");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listViewContact=findViewById(R.id.contact_home_recycler_view_group);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listViewContact.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(ContactGroupActivity.this, R.drawable.divider));
        listViewContact.addItemDecoration(dividerItemDecoration);
        getGroups();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_group,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        mySQLiteHelper = new MySQLiteHelper(ContactGroupActivity.this);
        if(item.getItemId()==R.id.send_sms_to_group)
        {
            String select="";
            Intent intent = null;
            intent = new Intent(Intent.ACTION_VIEW);
            //intent.setData(Uri.parse("smsto:"));
           // intent.setType("vnd.android-dir/mms-sms");
            for(int i=0;i<selection_list.size();i++)
                select+=(mySQLiteHelper.send(selection_list.get(i),"phone")+";");
            intent.setData(Uri.parse("sms:" + Uri.encode(select)));
            //intent.putExtra("address",select );
            startActivity(intent);
        }
        if(item.getItemId()==R.id.send_email_to_group)
        {
            String select="";
            Intent intent = null;
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            for(int i=0;i<selection_list.size();i++)
                select+=mySQLiteHelper.send(selection_list.get(i),"mail");
            selection_list.clear();
            intent.putExtra(Intent.EXTRA_EMAIL,select.split(",") );
            startActivity(intent);
        }

        if(item.getItemId()==android.R.id.home) {
            iscontextmode = false;
            Intent intent = new Intent(ContactGroupActivity.this,
                    ContactHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);}
        return true;
    }


        private void getGroups()
    {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView=findViewById(R.id.contact_home_recycler_view_group);
        contactGroupViewAdapter=new ContactGroupViewAdapter(items,this);
        recyclerView.setAdapter(contactGroupViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onLongClick(View view)
    {
        iscontextmode=true;
        contactGroupViewAdapter.notifyDataSetChanged();
        Log.d("manasa-sk","clicked");
        return true;
    }

    public void MakeSelection(View view, int adapterPosition)
    {
        if(((CheckBox)view).isChecked())
        {
            selection_list.add(items[adapterPosition]);
            Log.d("manasa-skm",items[adapterPosition]);
        }
        else {
            selection_list.remove(items[adapterPosition]);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (iscontextmode)
        {
            iscontextmode=false;
            contactGroupViewAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
        finish();
    }



}
