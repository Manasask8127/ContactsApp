package com.example.manasask.contactappdemo;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manasask.contactappdemo.Adapter.ContactViewAdapter;
import com.example.manasask.contactappdemo.data.MySQLiteHelper;
import com.example.manasask.contactappdemo.model.UserContact;
import com.example.manasask.contactappdemo.utils.AndroidProjUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ContactHomeActivity extends AppCompatActivity implements View.OnLongClickListener
{
    private static final int PERMISSION_EXTERNAL_WRITE = 2;
    private static final int PERMISSION_EXTERNAL_READ = 3;
    private static final int CREATE_JSON_FILE = 4;
    private static final int PICK_JSON_FILE = 5;
    private static final int CREATE_VCF_FILE = 6;
    private static final int PICK_VCF_FILE = 7;
    private static final int PERMISSION_WRITE_CONTACTS=8;
    private static final int PERMISSION_READ_CONTACTS=9;
    public static boolean iscontextualmodeenabled=false;
    public static String groups="basic";
    public static TextView title;

    RecyclerView listViewContact;
    ContactViewAdapter contactViewAdapter;
    ArrayList<UserContact>  userContacts;
    List<UserContact> userContactList;
    TextView noContact;
    MySQLiteHelper mySQLiteHelper;
    UserContact userContact;
    ArrayList<UserContact> selection_list=new ArrayList<>();
    Toolbar toolbar;

    public static FloatingActionButton fab;


    private static final String TAG = "ContactHomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_home);
        toolbar = (Toolbar) findViewById(R.id.normal_toolbar);
        title=findViewById(R.id.text_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText("");
        userContacts=new ArrayList<>();

        listViewContact=findViewById(R.id.contact_home_recycler_view);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listViewContact.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(ContactHomeActivity.this, R.drawable.divider));
        listViewContact.addItemDecoration(dividerItemDecoration);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AndroidProjUtils.launchNewActivity(ContactHomeActivity.this, CreateContactActivity.class, false);
            }
        });

        getContacts();


    }

  @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.imports)
        {
            if (ContextCompat.checkSelfPermission(ContactHomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                permissionNotGrandted(Manifest.permission.READ_EXTERNAL_STORAGE);}
             else {
                importcontacts();
            }
        }
        if (item.getItemId() == R.id.exports) {
            if (ContextCompat.checkSelfPermission(ContactHomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionNotGrandted(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                exportcontacts();
            }

        }
       if(item.getItemId()==R.id.share)
        {
            String S="";
            mySQLiteHelper = new MySQLiteHelper(ContactHomeActivity.this);
            userContact = new UserContact();
            for(int i=0;i<selection_list.size();i++) {
                userContact = mySQLiteHelper.getContact(Long.parseLong(selection_list.get(i).getContactUserId()));
                S=S+"\n\n"+String.format("Name: " + userContact.getContactUserFirstName() + "\b" + userContact.getContactUserLastName() + "\n" + "Number: "
                        + userContact.getContactUserPhone()+"\b"+userContact.getContactUserSecond()+"\n" + "EmailId :" + userContact.getContactUserAddress());
            }
            selection_list.clear();
                Intent shareIntent=new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, S);
                //shareIntent.putExtra(Intent.EXTRA_TEXT,"hI");
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent,"Sharing Contact"));
            return true;

        }
            if(item.getItemId()==R.id.sort)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ContactHomeActivity.this);
                alertDialog.setTitle("Sort By");
                String[] items = {"Firstname","Lastname"};
                alertDialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mySQLiteHelper= new MySQLiteHelper(ContactHomeActivity.this);
                        switch (which) {
                            case 0:
                                mySQLiteHelper.updateState("firstname");
                                getContacts();
                                Toast.makeText(ContactHomeActivity.this, "Sorted by firstname", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                mySQLiteHelper.updateState("lastname");
                                getContacts();
                                Toast.makeText(ContactHomeActivity.this, "Sorted by lastname", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(true);
                alert.show();

                return true;
            }
            if(item.getItemId()==R.id.selectall)
            {
                selection_list.addAll(userContactList);
                contactViewAdapter.selectAll();

            }
            if(item.getItemId()==R.id.unselectall)
            {
                selection_list.removeAll(userContactList);
                contactViewAdapter.unselectAll();
            }
        if(item.getItemId()==R.id.delete)
        {
            iscontextualmodeenabled = false;

            if(selection_list.size()==0)
                Toast.makeText(getApplicationContext(),"no contacts selected",Toast.LENGTH_LONG).show();
            else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ContactHomeActivity.this);
                alertDialog.setMessage("Delete contacts?");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0;i<selection_list.size();i++)
                        {
                        mySQLiteHelper = new MySQLiteHelper(ContactHomeActivity.this);
                            userContact = new UserContact();
                            userContact = mySQLiteHelper.getContact(Long.parseLong(selection_list.get(i).getContactUserId()));
                            mySQLiteHelper.deleteContact(userContact);
                        }
                        if(selection_list.size()>0){
                            //ContactViewAdapter contentAdapter = (ContactViewAdapter) contactViewAdapter;
                            //contentAdapter.updateAdapter(selection_list);

                            Toast.makeText(getApplicationContext(),"Deleted Successfully",Toast.LENGTH_LONG).show();
                            //getContacts();
                            selection_list.removeAll(selection_list);
                    Intent intent = new Intent(ContactHomeActivity.this, ContactHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }}});
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();


                    }
                });
                alertDialog.show();
               // ClearActionMode();
            }
            return true;
        }
        if(item.getItemId()==R.id.search)
        {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    contactViewAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    contactViewAdapter.getFilter().filter(query);
                    return false;
                }
            });
            return true;
        }
        if(item.getItemId()==R.id.groups)
        {
            //fab.setVisibility(View.INVISIBLE);
            AndroidProjUtils.launchNewActivity(ContactHomeActivity.this,ContactGroupActivity.class,false);

        }
        if(item.getItemId()==android.R.id.home){
            ClearActionMode();
            AndroidProjUtils.launchNewActivity(ContactHomeActivity.this,ContactHomeActivity.class,true);
        }
        return true;
    }

    public void permissionNotGrandted(String permission) {

        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactHomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To import contacts allow to read external storage ");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ContactHomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_READ);
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(ContactHomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_READ);
            }
        }

        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactHomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To import contacts allow to read external storage ");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ContactHomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_WRITE);
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(ContactHomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_WRITE);
            }

        }
        if(permission.equals(Manifest.permission.WRITE_CONTACTS)){
            if(ActivityCompat.shouldShowRequestPermissionRationale(ContactHomeActivity.this,Manifest.permission.WRITE_CONTACTS)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To Export contacts to SIM allow to write contacts ");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ContactHomeActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_WRITE_CONTACTS);
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(ContactHomeActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_WRITE_CONTACTS);
            }
        }
        if(permission.equals(Manifest.permission.READ_CONTACTS)){
            if(ActivityCompat.shouldShowRequestPermissionRationale(ContactHomeActivity.this,Manifest.permission.READ_CONTACTS)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To Import contacts from SIM allow read contacts permission");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ContactHomeActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(ContactHomeActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);
            }
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSION_EXTERNAL_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ContactHomeActivity.this, "Write External Storage Granted", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_TITLE,"contacts.json");
                startActivityForResult(intent,CREATE_JSON_FILE);
            } else {
                Toast.makeText(ContactHomeActivity.this, "Grant Write Permission To Export Contact List", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PERMISSION_EXTERNAL_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ContactHomeActivity.this, "Read External Storage Granted", Toast.LENGTH_SHORT).show();
                importcontacts();
            }
        }

        if (requestCode == PERMISSION_EXTERNAL_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ContactHomeActivity.this, "Write External Storage Granted", Toast.LENGTH_SHORT).show();
                exportcontacts();
            }
        }
        if(requestCode == PERMISSION_WRITE_CONTACTS){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ContactHomeActivity.this,"Write Contact Permission Granted",Toast.LENGTH_SHORT).show();
                new ExportToSimAsyncTask().execute();
            }
        }
        if(requestCode== PERMISSION_READ_CONTACTS){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ContactHomeActivity.this,"Read Contact Permission Granted",Toast.LENGTH_SHORT).show();
                new ImportFromSimAsyncTask().execute();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_JSON_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri=null;
            if(data!=null){
                uri=data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                new ImportAsyncTask(0).execute(split[0]);
            }
            else {
                Toast.makeText(ContactHomeActivity.this,"Choose Appropriate File",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==CREATE_JSON_FILE && resultCode==Activity.RESULT_OK)
        {
            Uri uri=null;
            if(data!=null){
                uri=data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                new ExportAsyncTask(contactViewAdapter,0).execute(split[0]);
            }
            else{
                Toast.makeText(ContactHomeActivity.this,"Export Failed",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PICK_VCF_FILE && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if (data != null)
            {
                uri = data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                new ImportAsyncTask(1).execute(split[0]);
            }
        }

        if (requestCode == CREATE_VCF_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null)
            {
                uri = data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                new ExportAsyncTask(contactViewAdapter,1).execute(split[0]);
            } else {
                Toast.makeText(ContactHomeActivity.this, "Export Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void importcontacts()
    {
        final String[] dialogList = new String[]{"Import from json", "Import from vcf","Import from SIM"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactHomeActivity.this);
        builder.setItems(dialogList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, PICK_JSON_FILE);
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, PICK_VCF_FILE);
                        break;
                    }
                    case 2:{
                        if (ContextCompat.checkSelfPermission(ContactHomeActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            permissionNotGrandted(Manifest.permission.READ_CONTACTS);
                        }else {
                            new ImportFromSimAsyncTask().execute();
                        }
                    }
                }
            }
        });
        builder.create();
        builder.show();

    }


    void exportcontacts() {
        final String[] dialogList = new String[]{"Export to .json", "Export to .vcf","Export to SIM"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactHomeActivity.this);
        builder.setItems(dialogList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:{
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/json");
                        intent.putExtra(Intent.EXTRA_TITLE, "contacts.json");
                        startActivityForResult(intent, CREATE_JSON_FILE);
                    }
                    case 1:{
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/x-vcard");
                        intent.putExtra(Intent.EXTRA_TITLE, "contacts.vcf");
                        startActivityForResult(intent, CREATE_VCF_FILE);
                    }
                    case 2:{
                        if (ContextCompat.checkSelfPermission(ContactHomeActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            permissionNotGrandted(Manifest.permission.WRITE_CONTACTS);
                        }else{
                            new ExportToSimAsyncTask().execute();
                        }
                    }
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
       // alertDialog.getWindow().setLayout(600, 400);
    }


    // Start of ImportAsyncTask
    private class ImportAsyncTask extends AsyncTask<String,Void,String> {
        int which;
        public ImportAsyncTask(int which)
        {
            this.which=which;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(ContactHomeActivity.this, s, Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
            Log.d("import", path);
            File file = new File(Environment.getExternalStorageDirectory().toString(), path);
            String filePath = fileExtenstion(path,which);
            Log.d("file",filePath);
            if(which==0)
            {
                if (filePath==".json") {
                    return "Select JSON format file";
                } else {
                    try (FileReader fileReader = new FileReader(file)) {
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = bufferedReader.readLine();
                        while (line != null) {
                            stringBuilder.append(line).append("\n");
                            line = bufferedReader.readLine();
                        }
                        bufferedReader.close();
                        String jsonString = stringBuilder.toString();
                        Log.d(TAG, jsonString);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray jsonArray = jsonObject.getJSONArray("Contacts");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            UserContact userContact = new UserContact();
                            JSONObject tempObject = jsonArray.getJSONObject(i);
                            //Log.d("err-img",tempObject.getString("image"));
                                String encodedString = tempObject.getString("image");
                                byte[] byteArray = android.util.Base64.decode(encodedString, 0);
                                userContact.setContactUserImage(byteArray);
                            userContact.setContactUserFirstName(tempObject.getString("first"));
                            userContact.setContactUserLastName(tempObject.getString("last"));
                            userContact.setContactUserPhone(tempObject.getString("phone"));
                            userContact.setContactUserSecond(tempObject.getString("second"));
                            userContact.setContactUserAddress(tempObject.getString("email"));
                            if(tempObject.getString("group_names")!=null)
                            userContact.setContactGroupName(tempObject.getString("group_names"));
                            mySQLiteHelper.addContact(userContact);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(ContactHomeActivity.this, ContactHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return "Contacts Imported";
                }

            }

            if(which==1)
            {

                    try (FileInputStream fileInputStream = new FileInputStream(file))
                    {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                        String line = bufferedReader.readLine();
                        //Log.d("vcf-file",line);
                        boolean hasNext = false;
                        if (line.equals("BEGIN:VCARD"))
                            hasNext = true;
                        String first = "", last = "", email = "";
                        List<String> phonenumbers = new ArrayList<>();
                        while (hasNext)
                        {
                            String s = bufferedReader.readLine();
                            if (s.lastIndexOf("N:", 2) == 0) {
                                String[] temp = s.split(";", 3);
                                last = temp[0].substring(temp[0].lastIndexOf(":") + 1);
                                first = temp[1];
                                Log.d("con", "last:" + temp[0]);
                                Log.d("con", "first:" + temp[1]);
                            }
                            if (s.contains("TEL;") && s.lastIndexOf(":") != 0) {
                                phonenumbers.add(s.substring(s.lastIndexOf(":") + 1));
                                //primary = s.substring(s.lastIndexOf(":") + 1);
                                //Log.d("con", "primary" + primary);
                                Log.d("con", "primary:" + s.substring(s.lastIndexOf(":") + 1));
                                Log.d("con", "Now phonenumber size is:" + String.valueOf(phonenumbers.size()));
                            }
                            /*if (s.contains("TEL;") && s.lastIndexOf(":") != 0 ) {
                                secondary = s.substring(s.lastIndexOf(":") + 1);
                                Log.d("con", "secondary:" + secondary);
                            }*/
                            if (s.contains("EMAIL") && s.lastIndexOf(":") != 0) {
                                email = s.substring(s.lastIndexOf(":") + 1);
                                Log.d("con", "email:" + email);
                            }
                            if (s.equals("END:VCARD"))
                            {
                                UserContact userContact = new UserContact();
                                if (phonenumbers.size() == 1)
                                {
                                    userContact.setContactUserFirstName(first);
                                    userContact.setContactUserLastName(last);
                                    userContact.setContactUserPhone(phonenumbers.get(0));
                                    userContact.setContactUserSecond("");
                                    userContact.setContactUserAddress(email);
                                }
                                else if (phonenumbers.size() >= 2)
                                {
                                    userContact.setContactUserFirstName(first);
                                    userContact.setContactUserLastName(last);
                                    userContact.setContactUserPhone(phonenumbers.get(0));
                                    userContact.setContactUserSecond(phonenumbers.get(1));
                                    userContact.setContactUserAddress(email);
                                }
                                mySQLiteHelper.addContact(userContact);
                                first = "";
                                last = "";
                                email = "";
                                phonenumbers.clear();
                                String next = bufferedReader.readLine();
                                if (next == null)
                                    hasNext = false;
                            }
                        }

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(ContactHomeActivity.this, ContactHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return "Contacts Imported";

            }
            return "Import failed";
        }
        private String fileExtenstion(String path, int which) {
            if (path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0) {
                if (which == 0) {
                    int index = path.lastIndexOf(".");
                    String p = path.substring(index);
                    if (p.length() <= 4) {
                        Log.d("file", p);
                        return p;
                    } else {
                        return path.substring(index, index + 5);
                    }
                    //Log.d("file", path.substring(index, index + 5));
                }
                if (which == 1) {
                    int index = path.lastIndexOf(".");
                    String p = path.substring(index);
                    if (p.length() <= 4) {
                        Log.d("file", p);
                        return p;
                    } else {
                        Log.d("file", path.substring(index, index + 4));
                        return path.substring(index, index + 4);
                    }
                }
            }
            return "";
        }
    }

    private class ImportFromSimAsyncTask extends AsyncTask<Void,Void,String>{
        Uri simUri=Uri.parse("content://icc/adn");
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor=contentResolver.query(simUri,null,null,null,"ASC");

        @Override
        protected String doInBackground(Void... voids) {
            while (cursor.moveToNext()){
                UserContact userContact=new UserContact();
                String name=cursor.getString(cursor.getColumnIndex("name"));
                String number=cursor.getString(cursor.getColumnIndex("number"));
                Log.d("simImport","name="+name);
                Log.d("simImport","number="+number);
                String S=name;
                String Sp[];
                Sp=S.split(" ");
                userContact.setContactUserFirstName(Sp[0]);
                try{
               if(Sp[1].equals(null))
               userContact.setContactUserLastName(Sp[1]+" "+Sp[2]);}
                catch (Exception e){
                    Log.d(TAG,"index error");
                }
               userContact.setContactUserPhone(number);
               mySQLiteHelper.addContact(userContact);

            }
            Intent intent = new Intent(ContactHomeActivity.this, ContactHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //return "Contacts Imported";

            return null;
        }
    }


    private class ExportAsyncTask extends AsyncTask<String,Void,String>
    {
        private ContactViewAdapter contactViewAdapter;
        int which;

        private ExportAsyncTask(ContactViewAdapter contactViewAdapter, int which) {
            this.contactViewAdapter = contactViewAdapter;
            this.which = which;
        }

        /*private ExportAsyncTask(ContactViewAdapter adapter) {
            this.contactViewAdapter = adapter;
        }*/

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Toast.makeText(ContactHomeActivity.this,"Exported started in background",Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... strings)
        {
            String path = strings[0];
            File dir = new File(Environment.getExternalStorageDirectory().toString());
            dir.mkdirs();
            if (which == 0)
            {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                userContactList = new ArrayList<>();
                mySQLiteHelper = new MySQLiteHelper(ContactHomeActivity.this);
                userContactList.addAll(mySQLiteHelper.getAllContacts());
                for (int i = 0; i < userContactList.size(); i++) {
                    UserContact contact = userContactList.get(i);
                    JSONObject tempObject = new JSONObject();
                    try
                    {
                        if(contact.getContactUserImage()!=null) {
                            String encodedString = android.util.Base64.encodeToString(contact.getContactUserImage(), 0);
                            tempObject.put("image", encodedString);
                        }
                        tempObject.put("first", contact.getContactUserFirstName());
                        tempObject.put("last", contact.getContactUserLastName());
                        tempObject.put("phone", contact.getContactUserPhone());
                        tempObject.put("second", contact.getContactUserSecond());
                        tempObject.put("email", contact.getContactUserAddress());
                        tempObject.put("group_names", contact.getContactGroupName());
                        jsonArray.put(tempObject);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject.put("Contacts", jsonArray);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    File newfile = new File(dir, path);
                    if (newfile.exists()) {
                        newfile.delete();
                    }
                    try (FileOutputStream fileOutputStream = new FileOutputStream(newfile)) {
                        fileOutputStream.write(jsonObject.toString().getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return "Contacts Exported to: " + path;
            }
            if (which == 1)
            {

                try
                {
                    File newfile = new File(dir, path);
                    FileOutputStream fop=new FileOutputStream(newfile);
                    userContactList = new ArrayList<>();
                    mySQLiteHelper = new MySQLiteHelper(ContactHomeActivity.this);
                    userContactList.addAll(mySQLiteHelper.getAllContacts());
                    byte[] begin = "BEGIN:VCARD\n".getBytes();
                    byte[] version = "VERSION:2.1\n".getBytes();
                    byte[] end = "END:VCARD\n".getBytes();
                    for (int i = 0; i < userContactList.size(); i++)
                    {
                        UserContact contact = userContactList.get(i);
                        fop.write(begin);
                        fop.write(version);
                        byte[] n = ("N:" + contact.getContactUserLastName() + ";" + contact.getContactUserFirstName() + ";;;\n").getBytes();
                        fop.write(n);
                        Log.d("vcf-filef",String.valueOf(n[0]));
                        byte[] fn = ("FN:" + contact.getContactUserFirstName() + " " + contact.getContactUserLastName() + "\n").getBytes();
                        fop.write(fn);
                        byte[] tel1 = ("TEL;CELL;PREF:" + contact.getContactUserPhone() + "\n").getBytes();
                        fop.write(tel1);
                        byte[] tel2 = ("TEL;CELL:" + contact.getContactUserSecond() + "\n").getBytes();
                        fop.write(tel2);
                        byte[] email = ("EMAIL;HOME:" + contact.getContactUserAddress() + "\n").getBytes();
                        fop.write(email);
                        fop.write(end);
                        Log.d("vcf-files",n.toString());


                    }
                    fop.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                    return "Contacts Exported to: " + path;
            }

                return "Export Failed";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            Toast.makeText(ContactHomeActivity.this,s,Toast.LENGTH_SHORT).show();
        }
    }//end of export

    private class ExportToSimAsyncTask extends AsyncTask<Void,Void,Void>{
        Uri simUri=Uri.parse("content://icc/adn");
        ContentValues contentValues=new ContentValues();
        ContentResolver contentResolver = getContentResolver();
        @Override
        protected Void doInBackground(Void... voids) {
            for(UserContact contact:userContactList){
                String name=contact.getContactUserFirstName()+" "+contact.getContactUserLastName();
                if(!contact.getContactUserPhone().equals("")){
                    String phonenumber=contact.getContactUserPhone();
                    contentValues.put("tag",name);
                    contentValues.put("number",phonenumber);
                    Uri insteredPrimary=contentResolver.insert(simUri,contentValues);
                    if(insteredPrimary!=null)
                        //Toast.makeText(MainActivity.this,name+" exported to sim",Toast.LENGTH_SHORT).show();
                        Log.d("sim", String.valueOf(insteredPrimary));
                }
                /*if(!contact.getContactUserSecond().equals("")){
                    String phonenumber=contact.getContactUserSecond();
                    contentValues.put("tag",name);
                    contentValues.put("number",phonenumber);
                    Uri insteredSecondary=contentResolver.insert(simUri,contentValues);
                    if(insteredSecondary!=null)
                        //Toast.makeText(MainActivity.this,name+" exported to sim",Toast.LENGTH_SHORT).show();
                        Log.d("sim", String.valueOf(insteredSecondary));
                }*/

            }
            return null;
        }
    }


    private void getContacts()
    {
        //String groups="";
        Intent intent = getIntent();
        if (intent != null) {
            groups = String.valueOf(intent.getStringExtra("group_name"));
            if(groups.equals("Friends") || groups.equals("Family") || groups.equals("Colleagues") || groups.equals("Schoolmates") || groups.equals("VIPs") || groups.equals("Others")) {
                title.setText(groups);
            }
            else{
                title.setText("Contacts");}
            if(groups!="")
            Log.d("id-manasa","go lo"+groups);
        }
        userContactList =new ArrayList<>();
        mySQLiteHelper=new MySQLiteHelper(this);
        userContactList.addAll(mySQLiteHelper.getAllContacts());
        //Log.d("id-manasa",groups);
        if (!userContactList.isEmpty())
        {
            Log.d(TAG, "getContacts: ");
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            RecyclerView recyclerView=findViewById(R.id.contact_home_recycler_view);
            contactViewAdapter=new ContactViewAdapter(userContactList,this);
            recyclerView.setAdapter(contactViewAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        else
        {
            Toast.makeText(this,"No data",Toast.LENGTH_SHORT).show();
            noContact=findViewById(R.id.no_contact);
            noContact.setVisibility(View.VISIBLE);
        }
    }

    protected void onDestroy()
    {
        mySQLiteHelper.close();
        super.onDestroy();

    }



    public void ClearActionMode()
    {
        iscontextualmodeenabled = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.toolbar);
        //getSupportActionBar().setHomeButtonEnabled(false);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public boolean onLongClick(View view)
    {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.contextual_toolbar);
        iscontextualmodeenabled = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contactViewAdapter.notifyDataSetChanged();

       // getSupportActionBar().setHomeButtonEnabled(true);
        return true;
    }

    public void MakeSelection(View view, int adapterPosition)
    {
        if(((CheckBox)view).isChecked())
        {
            selection_list.add(userContactList.get(adapterPosition));
        }
        else {
            selection_list.remove(userContactList.get(adapterPosition));
        }
    }

    @Override
    public void onBackPressed()
    {
        if (iscontextualmodeenabled)
        {
            ClearActionMode();
            contactViewAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
        finish();
    }
}