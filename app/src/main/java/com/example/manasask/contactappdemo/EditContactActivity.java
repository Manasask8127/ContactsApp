package com.example.manasask.contactappdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manasask.contactappdemo.data.MySQLiteHelper;
import com.example.manasask.contactappdemo.model.UserContact;
import com.example.manasask.contactappdemo.utils.AndroidProjUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditContactActivity extends AppCompatActivity implements View.OnTouchListener{

    @BindView(R.id.update_contact_image)
    CircleImageView contact_image;

    @BindView(R.id.update_contact_first_name)
    EditText first_name;

    @BindView(R.id.update_contact_last_name)
    EditText last_name;

    @BindView(R.id.update_contact_mail)
    EditText mail;

    @BindView(R.id.update_contact_phn_num)
    EditText phn_num;

    @BindView(R.id.update_contact_sec_num)
    EditText sec_num;

    @BindView(R.id.update_contact_group)
    TextView groups;


    @BindView(R.id.btn_update)
    Button update;

    @BindView(R.id.btn_update_cancel)
    Button cancel;

    @BindView(R.id.edit_contact_layout)
    RelativeLayout editContactLayout;

    final int REQUEST_CODE_GALLERY = 999;
    UserContact userContact;
    MySQLiteHelper mySQLiteHelper;
    String contactUserId;
    String s="";
    String mFirstName, mLastName, mMail, mPhoneNumber,nPhoneNumber,group_names;
    byte[] mImage;
    Bitmap bitmap;
    List<String> selectedList=new ArrayList<String>();
    AlertDialog.Builder builder;
    AlertDialog alert;
    CreateContactActivity createContactActivity=new CreateContactActivity();
    boolean[] checkeds = { false,false,false,false,false,false };
    public static String[] items={"Friends","Family","Colleagues","Schoolmates","VIPs"};



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        setTitle("Edit Contact");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        //selectedList=createContactActivity.selectedList;
        //checkeds=createContactActivity.checkeds;
        //items=createContactActivity.items;
        getIntentData();
        init();
        retrieveContact();


    }

    private void init()
    {
        editContactLayout.setOnTouchListener((View.OnTouchListener) this);

    }

    @OnClick(R.id.update_contact_group)
    void addgroup()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditContactActivity.this);
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Groups");
        //selectedList.clear();
        //{"Friends","Family","Colleagues","Schoolmates","VIPs","Others"};
        //group_names=mySQLiteHelper.getGroup(contactUserId);
        if(group_names!=null){
        String[] gps=group_names.split(" ");
        for(int i=0;i<gps.length;i++)
            selectedList.add(gps[i]);
        //selectedList= Arrays.asList(gps);
        if(selectedList.contains("Friends"))
            checkeds[0]=true;
        if(selectedList.contains("Family"))
            checkeds[1]=true;
        if(selectedList.contains("Colleagues"))
            checkeds[2]=true;
        if(selectedList.contains("Schoolmates"))
            checkeds[3]=true;
        if(selectedList.contains("VIPs"))
            checkeds[4]=true;}
        //selectedList=new ArrayList<String>(selectedList);
        alertDialog.setMultiChoiceItems(items,checkeds, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which,boolean isChecked)
            {
                mySQLiteHelper= new MySQLiteHelper(EditContactActivity.this);
                if(isChecked)
                {
                    selectedList.add(items[which]);
                    Log.d("checked",items[which]);
                }
                else{
                    selectedList.remove(items[which]);
                    Log.d("checked",items[which]);
                }
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Set<String> set = new HashSet<>(selectedList);
                selectedList.clear();
                selectedList.addAll(set);
                s="";
                for(int j=0;j<selectedList.size();j++)
                {
                    s+=selectedList.get(j)+" ";
                }
                groups.setText(s);

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.cancel();
                //Toast.makeText(EditContactActivity.this,"no option selected",Toast.LENGTH_LONG).show();
            }
        });
        alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
        //alert.dismiss();

    }

    private void retrieveContact() {
        mySQLiteHelper=new MySQLiteHelper(this);
        userContact=new UserContact();
        userContact=mySQLiteHelper.getContact(Long.parseLong(contactUserId));
        //userContact = mySQLiteHelper.getContact(Long.parseLong(contactUserId));
        byte[] foodImage = userContact.getContactUserImage();
        if(foodImage!=null){
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        contact_image.setImageBitmap(bitmap);}
        first_name.setText(userContact.getContactUserFirstName());
        last_name.setText(userContact.getContactUserLastName());
        mail.setText(userContact.getContactUserAddress());
        phn_num.setText(userContact.getContactUserPhone());
        sec_num.setText(userContact.getContactUserSecond());
        groups.setText(userContact.getContactGroupName());
        group_names=userContact.getContactGroupName();
        s=group_names;
    }

    private void getIntentData()
    {
        Intent intent = getIntent();
        if (intent != null) {
            contactUserId = intent.getStringExtra("contactUserId");
            //contactgroupId=intent.getStringExtra("groupid");
        }


    }

    @OnClick(R.id.update_contact_image)
    void addimage()
    {
        ActivityCompat.requestPermissions(
                EditContactActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY);
    }

    @OnClick(R.id.btn_update_cancel)
    void onButtonCancel() {

        onBackPressed();
    }

    @OnClick(R.id.btn_update)
    void onButtonUpdate()
    {

        if(!validateUser()){
            return;

        }
        else
            {
            updateContact();
            AndroidProjUtils.hideSoftKeyboard(this,editContactLayout);
            Toast.makeText(this, "Contact Updated!", Toast.LENGTH_SHORT).show();
        }


    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public byte[] imageViewToByte(ImageView image)
    {

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        bitmap=getResizedBitmap(bitmap,500);
        contact_image.setImageBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bitmap.createBitmap(bitmap,50,50,50,50);
        byte[] byteArray = stream.toByteArray();
        return byteArray;

    }


    private boolean validateUser() {
        getUserValue();

        if (mFirstName.isEmpty())
        {
            first_name.setError("First name is empty");
            requestFocus(first_name);
            return false;
        }
        /*if (mLastName.isEmpty())
        {
            last_name.setText("Last name is empty");
            requestFocus(last_name);
        }*///optional
        /*if (mMail.isEmpty()){
            mail.setError("Address name is empty");
            requestFocus(mail);
            return false;
        }*/
        if (mPhoneNumber.isEmpty() || mPhoneNumber.length()>10 || mPhoneNumber.length()<10){
            phn_num.setError("invalid number");
            requestFocus(phn_num);
            return false;
        }
        if(nPhoneNumber.length()>10 || (nPhoneNumber.length()<10 && !(nPhoneNumber.isEmpty()))) {
            sec_num.setError("invalid number");
            requestFocus(sec_num);
            return false;
        }
        return true;
    }

    private void getUserValue() {
        mImage=imageViewToByte(contact_image);
        mFirstName=first_name.getText().toString().trim();
        mLastName=last_name.getText().toString().trim();
        mMail=mail.getText().toString().trim();
        mPhoneNumber=phn_num.getText().toString().trim();
        nPhoneNumber=sec_num.getText().toString().trim();
        group_names=groups.getText().toString().trim();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "Permission not granted!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                bitmap = BitmapFactory.decodeStream(inputStream);
               Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                contact_image.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void updateContact() {
        mySQLiteHelper=new MySQLiteHelper(this);
        userContact=new UserContact();

        userContact.setContactUserId(contactUserId);
        userContact.setContactUserImage(mImage);
        userContact.setContactUserFirstName(mFirstName);
        userContact.setContactUserLastName(mLastName);
        userContact.setContactUserAddress(mMail);
        userContact.setContactUserPhone(mPhoneNumber);
        userContact.setContactUserSecond(nPhoneNumber);
        userContact.setContactGroupName(s);

        if (userContact!=null){
            mySQLiteHelper.updateContact(userContact);
           /* for(int j=0;j<selectedList.size();j++)
            {
                // do userContact.setContactGroupId();
                userContact.setContactGroupName(selectedList.get(j));
                userContact.setContactUserPhone(mPhoneNumber);
               // mySQLiteHelper.updatetogroup(userContact);
            }*/

            Intent intent = new Intent(EditContactActivity.this,
                    ContactHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
         else{
            Toast.makeText(this,"Error Occured while adding contact",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AndroidProjUtils.hideSoftKeyboard(this,v);
        return false;
    }


}



