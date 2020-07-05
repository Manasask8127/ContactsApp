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
import android.support.design.widget.BottomSheetDialog;
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
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateContactActivity extends AppCompatActivity implements View.OnTouchListener{

    @BindView(R.id.details_contact_first_name)
    EditText contact_first_name;

    @BindView(R.id.details_contact_last_name)
    EditText contact_last_name;

    @BindView(R.id.details_contact_mail)
    EditText contact_mail;

    @BindView(R.id.details_contact_phn_num)
    EditText contact_phn_num;

    @BindView(R.id.details_contact_sec_num)
    EditText contact_sec_num;

    @BindView(R.id.details_contact_group)
    TextView contact_group;


    @BindView(R.id.details_contact_image)
    CircleImageView contact_image;

    @BindView(R.id.btn_save)
    Button save;

    @BindView(R.id.create_contact_relative_layout)
    RelativeLayout create_contact_relative_layout;

    Button b1;
    Button b2;

    String s="";
    String mFirstName,mLastName,mMail,mPhone,nPhone;
    byte[] mImage;
    Bitmap bitmap;
    MySQLiteHelper mySQLiteHelper;
    ArrayList d=new ArrayList();
    final int REQUEST_CODE_GALLERY = 999;
    public final ArrayList<String> selectedList = new ArrayList<>();
    AlertDialog.Builder builder;
    AlertDialog alert;
    public static String[] items = {"Friends","Family","Colleagues","Schoolmates","VIPs"};
    public static Boolean merge=true;
    UserContact userContact=new UserContact();
    BottomSheetDialog dialogs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Create New Contact");
        ButterKnife.bind(this);
        init();
        String groups=ContactHomeActivity.groups;
        if(groups.equals("Friends") || groups.equals("Family") || groups.equals("Colleagues") || groups.equals("Schoolmates") || groups.equals("VIPs") || groups.equals("Others")) {
            contact_group.setText(groups);
            s=groups;
            selectedList.add(groups);
        }


    }

    private void init() {
        create_contact_relative_layout.setOnTouchListener(this);
        mySQLiteHelper=new MySQLiteHelper(this);
    }

    @OnClick(R.id.details_contact_group)
    void addgroup()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateContactActivity.this);
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Groups");
        selectedList.clear();
        boolean[] checkeds = { false,false,false,false,false,false };

        alertDialog.setMultiChoiceItems(items,checkeds, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which,boolean isChecked)
            {
                mySQLiteHelper= new MySQLiteHelper(CreateContactActivity.this);
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
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Set<String> set = new HashSet<>(selectedList);
                selectedList.clear();
                selectedList.addAll(set);
                for(int j=0;j<selectedList.size();j++)
                {
                    s+=selectedList.get(j)+" ";
                }
                contact_group.setText(s);

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(CreateContactActivity.this,"no option selected",Toast.LENGTH_LONG).show();
            }
        });
        alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
        //

    }

    @OnClick(R.id.btn_save)
    void onBtnSave(){

        //alert.dismiss();
         builder=new AlertDialog.Builder(CreateContactActivity.this);
        builder.setCancelable(true);
        builder.setMessage("Do you want to save the contact?");
        if(!validateUser()){
            return;
        } else {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    addNewContact();
                   if( d.isEmpty()==true || d.get(0).equals(-9999))
                    {

                        Toast.makeText(getApplicationContext(),"contact saved!",Toast.LENGTH_LONG).show();
                        //merge=true;
                    Intent intent = new Intent(CreateContactActivity.this,
                            ContactHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                        //Toast.makeText(getApplicationContext(),"contact already exists!",Toast.LENGTH_LONG).show();
                    }
                    //Log.d("merging_func",String.valueOf(d));
                    else
                    {
                        dialogs = new BottomSheetDialog(CreateContactActivity.this);
                        dialogs.setContentView(R.layout.bottom_sheet_layout);
                        dialogs.show();
                        b1=(Button)dialogs.findViewById(R.id.no_merge);
                        b1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               merge=false;
                                mySQLiteHelper.addContact(userContact);
                                //merge=true;
                                Intent intent = new Intent(CreateContactActivity.this,
                                        ContactHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        b2=(Button)dialogs.findViewById(R.id.merge);
                        b2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Log.d("contents",userContact.getContactUserFirstName()+d.get(1)+userContact.getContactUserLastName()+userContact.getContactUserPhone());
                                merge=true;
                                UserContact user=new UserContact();
                                user=mySQLiteHelper.getContact((Long) d.get(0));
                                //userContact.setContactUserId(String.valueOf(d.get(0)));
                               // if(user.getContactUserSecond()==null)
                                    user.setContactUserSecond(mPhone);
                                mySQLiteHelper.updateContact(user);
                                //merge=false;
                                Intent intent = new Intent(CreateContactActivity.this,
                                        ContactHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                    /*else{


                    //AndroidProjUtils.hideSoftKeyboard(this, create_contact_relative_layout);
                    //AndroidProjUtils.launchNewActivity(CreateContactActivity.this,ContactHomeActivity.class,true);
                    }*/
                }
            });
            builder.show();
            //finish();

        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (dialogs != null) {
            dialogs.dismiss();
            dialogs = null;
        }
    }

    @OnClick(R.id.details_contact_image)
    void addimage()
    {
        ActivityCompat.requestPermissions(
                CreateContactActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY);
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


    public byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

        /*Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        */
        bitmap=getResizedBitmap(bitmap,500);
        contact_image.setImageBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bitmap.createBitmap(bitmap,50,50,50,50);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
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
                Toast.makeText(getApplicationContext(), "No sufficient permission!", Toast.LENGTH_SHORT).show();
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


    public void addNewContact() {


        mImage=imageViewToByte(contact_image);
        userContact.setContactUserImage(mImage);
        userContact.setContactUserFirstName(mFirstName);
        userContact.setContactUserLastName(mLastName);
        userContact.setContactUserAddress(mMail);
        userContact.setContactUserPhone(mPhone);
        userContact.setContactUserSecond(nPhone);
        userContact.setContactGroupName(s);
        Log.d("id-manasa",s);


        if (userContact!=null){
            d=mySQLiteHelper.addContact(userContact);
            //Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
            /*for(int j=0;j<selectedList.size();j++)
            {
                userContact.setContactGroupName(selectedList.get(j));
                userContact.setContactUserId(mPhone);
                mySQLiteHelper.addtogroup(userContact);
            }*/

        }else{
            Toast.makeText(this,"empty field",Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validateUser(){
        getUserValue();
         if(mFirstName.isEmpty()){
            contact_first_name.setError("First Name missing");
            requestFocus(contact_first_name);
            return false;
        }

        /*else if (mLastName.isEmpty()){
            contact_last_name.setError("Last Name missing");
            requestFocus(contact_last_name);
            return false;
        }//optional

        if (mMail.isEmpty()){
            contact_mail.setError("Address missing");
            requestFocus(contact_mail);
            return false;
        }*/

        else if (mPhone.isEmpty() || mPhone.length()>10 || mPhone.length()<10)
        {
            contact_phn_num.setError("invalid number");
            requestFocus(contact_phn_num);
            return false;
        }
        else if(nPhone.length()>10 || (nPhone.length()<10 && !(nPhone.isEmpty())))
        {
            contact_sec_num.setError("invalid number");
            requestFocus(contact_sec_num);
            return false;
        }


        return true;

    }

    private void requestFocus(View view) {
    if (view.requestFocus()){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    }

    private void getUserValue()
    {
        mImage=imageViewToByte(contact_image);
        mFirstName=contact_first_name.getText().toString().trim();
        mLastName=contact_last_name.getText().toString().trim();
        mMail=contact_mail.getText().toString().trim();
        mPhone=contact_phn_num.getText().toString().trim();
        nPhone=contact_sec_num.getText().toString().trim();
        //groupname=

    }
    @Override
    protected void onDestroy() {
        mySQLiteHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AndroidProjUtils.hideSoftKeyboard(this, v);
        return false;
    }



}



