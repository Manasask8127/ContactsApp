package com.example.manasask.contactappdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manasask.contactappdemo.data.MySQLiteHelper;
import com.example.manasask.contactappdemo.model.UserContact;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactDetailActivity extends AppCompatActivity {
    public static final int PERMISSION_CALL = 1;

    @BindView(R.id.create_contact_first_name)
    TextView first_name;

    //@BindView(R.id.create_contact_last_name)
    //TextView last_name;

    @BindView(R.id.create_contact_mail)
    TextView address;

    @BindView(R.id.create_contact_phn_num)
    TextView phn_num;

    @BindView(R.id.create_contact_sec_num)
    TextView sec_num;

    @BindView(R.id.create_contact_image)
    CircleImageView image;

    @BindView(R.id.groups)
    TextView group;


    @BindView(R.id.edit)
    FloatingActionButton edit;

    UserContact userContact;
    MySQLiteHelper mySQLiteHelper;
    public String contactUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentData();
        retrieveContact();
       /* String nums= String.valueOf(phn_num);
        String[] phonenums=nums.split("\n");*/

        phn_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = phn_num.getText().toString();
                final Intent phoneIntent = new Intent((Intent.ACTION_CALL));
                phoneIntent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(ContactDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestCallPermission();
                }
                startActivity(phoneIntent);
            }
        });

        sec_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number=sec_num.getText().toString();
                final Intent phoneIntent = new Intent((Intent.ACTION_CALL));
                phoneIntent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(ContactDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestCallPermission();
                }
                startActivity(phoneIntent);
            }
        });




        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = address.getText().toString();
                final Intent emailIntent = new Intent((Intent.ACTION_SENDTO));
                emailIntent.setData(Uri.parse("mailto:" + email));
                startActivity(emailIntent);

            }
        });
    }



    @SuppressLint("ResourceType")
    private void retrieveContact()
    {
        mySQLiteHelper = new MySQLiteHelper(this);
        userContact = new UserContact();
        userContact = mySQLiteHelper.getContact(Long.parseLong(contactUserId));
        byte[] foodImage = userContact.getContactUserImage();
        if(foodImage!=null){
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        image.setImageBitmap(bitmap);}
        first_name.setText(userContact.getContactUserFirstName()+" "+userContact.getContactUserLastName());
        //last_name.setText(userContact.getContactUserLastName());
        phn_num.setText(userContact.getContactUserPhone());
        sec_num.setText(userContact.getContactUserSecond());
        address.setText(userContact.getContactUserAddress());
        group.setText(userContact.getContactGroupName());
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            contactUserId = intent.getStringExtra("contactUserID");

        }
    }

    @OnClick(R.id.edit)
    void onFabEditContactClicked() {
        Intent intent = new Intent(ContactDetailActivity.this, EditContactActivity.class);
        intent.putExtra("contactUserId", contactUserId);
        startActivity(intent);
    }

    @OnClick(R.id.delete)
    void onDeleteContactClicked() {
        mySQLiteHelper = new MySQLiteHelper(this);
        userContact = new UserContact();
        userContact = mySQLiteHelper.getContact(Long.parseLong(contactUserId));


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ContactDetailActivity.this);
        alertDialog.setMessage("Delete contact?");
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "contact deleted!", Toast.LENGTH_LONG).show();
                mySQLiteHelper.deleteContact(userContact);
                Intent intent = new Intent(ContactDetailActivity.this,
                        ContactHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();


            }
        });
        alertDialog.show();

    }
    private void requestCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ContactDetailActivity.this, Manifest.permission.CALL_PHONE)) {
            Toast.makeText(ContactDetailActivity.this, "Grant Phone Call Permission inorder to Call", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(ContactDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
        } else {
            ActivityCompat.requestPermissions(ContactDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
        }

    }


}
