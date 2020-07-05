package com.example.manasask.contactappdemo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.example.manasask.contactappdemo.ContactHomeActivity;
import com.example.manasask.contactappdemo.CreateContactActivity;
import com.example.manasask.contactappdemo.R;
import com.example.manasask.contactappdemo.model.UserContact;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_CONTACT_APP_DEMO="CONTACT_APP_DEMO";
    public static final String SETTINGS="SORT";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_IMAGE = "image";
    public static final String COLUMN_FIRST_NAME = "user_first_name";
    public static final String COLUMN_LAST_NAME  = "user_last_name";
    public static final String COLUMN_ADDRESS  = "user_address";
    public static final String COLUMN_PHONE_NUMBER="user_phone_number";
    public static final String COLUMN_SECOND_NUMBER="user_second_number";

    public static final String SORT="sort";

   // public static final String GROUPS="groups";
   // public static final String GROUPID="groupid";
    public static final String GROUP_NAMES="names";
   // public static final String NUMBER="number";

    private static final String TAG = "MySQLiteHelper";
    private static final String DATABASE_NAME="CONTACTAPPDEMO";
    private static final Integer DATABASE_VERSION=1;

    // Data creation sql statement;
    public static String gid;

    private static final String DATABASE_CREATE="create table "
            +TABLE_CONTACT_APP_DEMO+" ( "
            +COLUMN_USER_ID+" integer primary key autoincrement, "
            +COLUMN_USER_IMAGE+" BLOB, "
            +COLUMN_FIRST_NAME+" varchar(255), "
            +COLUMN_LAST_NAME+" varchar(255), "
            +COLUMN_ADDRESS+" varchar(255), "
            +COLUMN_PHONE_NUMBER+" varchar(10), "
            +COLUMN_SECOND_NUMBER+" varchar(10),"
            +GROUP_NAMES+" varchar(255));";

    private static final String DATABASE="create table "
            +SETTINGS+" ( "
            +SORT+" varchar(10)); ";

   /* private static final String DB="create table "
            +GROUPS+" ( "
            +GROUPID+" integer primary key autoincrement, "
            +GROUP_NAME+" varchar(10), "
            +NUMBER+" varchar(10));";*/

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE);
        //db.execSQL(DB);
        String query="INSERT INTO "+ SETTINGS + "(" + SORT +") values ('nothing')";
        Log.d("sort",query);
        db.execSQL(query);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_APP_DEMO);
    db.execSQL("DROP TABLE IF EXISTS " + SETTINGS);
    //db.execSQL("DROP TABLE IF EXISTS " + GROUPS);
    // create Table again
        onCreate(db);
    }

    public String getState()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String S="SELECT "+SORT+" FROM "+SETTINGS;
        Cursor cursor=db.rawQuery(S,null);
        if(cursor!=null)
            cursor.moveToFirst();
        S=cursor.getString(cursor.getColumnIndex(SORT));
        Log.d("sortget",S);
        return S;
    }
    public void updateState(String s){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("UPDATE "+SETTINGS+" SET "+ SORT + "="+"'"+s+"'");
        Log.d("sortupdate",s);

    }

    public ArrayList addContact(UserContact userContact)
    {
    // open database in write mode
        CreateContactActivity createContactActivity = null;
        ArrayList arrayList=new ArrayList();
        //long id = 0;
    SQLiteDatabase db =this.getWritableDatabase();
     //Cursor res =  db.rawQuery( "SELECT * FROM "+ TABLE_CONTACT_APP_DEMO +" WHERE " +COLUMN_PHONE_NUMBER+" = ? ", new String[]{ userContact.getContactUserPhone() } );
    Cursor res1=db.rawQuery("SELECT * FROM "+TABLE_CONTACT_APP_DEMO+" WHERE "+COLUMN_FIRST_NAME+" = ? AND "+COLUMN_LAST_NAME+" = ? ",new String[]{ userContact.getContactUserFirstName(),userContact.getContactUserLastName()});
    //Content values is used to insert value
        //if( res1.getCount()==0 && createContactActivity.merge==false)

        /*if(res.getCount()>0){
            id=-9999;
            Log.d("manasa","already exists");
        }*/
        if(res1!=null && res1.moveToFirst())
        {
            while (!res1.isAfterLast())
            {
                if(res1.getString(res1.getColumnIndex(COLUMN_SECOND_NUMBER)).isEmpty())
                    break;
                else
                    res1.moveToNext();

            }
            try
            {
            createContactActivity = new CreateContactActivity();
                if (res1.getCount() > 0 && createContactActivity.merge.equals(true)) {
                    //createContactActivity.merge=false;
                    arrayList.add(Long.parseLong(res1.getString(res1.getColumnIndex(COLUMN_USER_ID))));
                    arrayList.add(res1.getString(res1.getColumnIndex(COLUMN_PHONE_NUMBER)));
                    Log.d("manasa-abc", "merge");
                }
                else
                {
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(COLUMN_USER_IMAGE, userContact.getContactUserImage());
                    contentValues.put(COLUMN_FIRST_NAME, userContact.getContactUserFirstName());
                    contentValues.put(COLUMN_LAST_NAME, userContact.getContactUserLastName());
                    contentValues.put(COLUMN_ADDRESS, userContact.getContactUserAddress());
                    contentValues.put(COLUMN_PHONE_NUMBER, userContact.getContactUserPhone());
                    contentValues.put(COLUMN_SECOND_NUMBER, userContact.getContactUserSecond());
                    contentValues.put(GROUP_NAMES, userContact.getContactGroupName());
                    Log.d("inserted-sk", "inserted");
                    createContactActivity.merge=true;
                    // insert row
                    db.insert(TABLE_CONTACT_APP_DEMO, null, contentValues);
                    arrayList.add(-9999);
                    arrayList.add(null);

                    Log.d("exception", "exception");
                }
            }
            catch (Exception e) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(COLUMN_USER_IMAGE, userContact.getContactUserImage());
                contentValues.put(COLUMN_FIRST_NAME, userContact.getContactUserFirstName());
                contentValues.put(COLUMN_LAST_NAME, userContact.getContactUserLastName());
                contentValues.put(COLUMN_ADDRESS, userContact.getContactUserAddress());
                contentValues.put(COLUMN_PHONE_NUMBER, userContact.getContactUserPhone());
                contentValues.put(COLUMN_SECOND_NUMBER, userContact.getContactUserSecond());
                contentValues.put(GROUP_NAMES, userContact.getContactGroupName());
                Log.d("inserted-sk", "inserted");
                createContactActivity.merge=true;
                // insert row
                db.insert(TABLE_CONTACT_APP_DEMO, null, contentValues);
                arrayList.add(-9999);
                arrayList.add(null);

                Log.d("exception", "exception");
            }

        }
        else {
                ContentValues contentValues = new ContentValues();

                contentValues.put(COLUMN_USER_IMAGE, userContact.getContactUserImage());
                contentValues.put(COLUMN_FIRST_NAME, userContact.getContactUserFirstName());
                contentValues.put(COLUMN_LAST_NAME, userContact.getContactUserLastName());
                contentValues.put(COLUMN_ADDRESS, userContact.getContactUserAddress());
                contentValues.put(COLUMN_PHONE_NUMBER, userContact.getContactUserPhone());
                contentValues.put(COLUMN_SECOND_NUMBER, userContact.getContactUserSecond());
                contentValues.put(GROUP_NAMES, userContact.getContactGroupName());
                Log.d("inserted-sk", "inserted");
                createContactActivity.merge=true;
                // insert row
                db.insert(TABLE_CONTACT_APP_DEMO, null, contentValues);
                arrayList.add(-9999);
                arrayList.add(null);
        }
       // close db;
        db.close();
        Log.d("manasa-abc","entered"+" "+createContactActivity.merge);
        return arrayList;

    }
   /* public void addtogroup(UserContact userContact)
    {
        //Log.d("id",s);
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(GROUP_NAME,userContact.getContactGroupName());
        contentValues.put(NUMBER,userContact.getContactUserPhone());
        Log.d("id-manasa",userContact.getContactGroupName()+userContact.getContactUserPhone());
        long id=db.insert(GROUPS, null, contentValues);
        if(id>0)
            Log.d("id-manasa","inserted");

    }*/
    public String getGroup(String id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor =  db.rawQuery( "SELECT "+GROUP_NAMES+" FROM "+ TABLE_CONTACT_APP_DEMO +" WHERE " +COLUMN_USER_ID+" = ? ", new String[]{ id } );
        //int glength=res.getCount();
        String S="";
        if(cursor!=null)
            cursor.moveToFirst();
        S=cursor.getString(cursor.getColumnIndex(GROUP_NAMES));
        db.close();
       // Log.d("id-manasask",S);
        return S;

    }
    /*public void updatetogroup(UserContact userContact)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        String[] selectionArgs={userContact.getContactGroupId()};

        contentValues.put(GROUPID,userContact.getContactGroupId());
        contentValues.put(GROUP_NAME,userContact.getContactGroupName());
        //Log.d("Android", String.valueOf(contentValues));
        contentValues.put(COLUMN_PHONE_NUMBER,userContact.getContactUserPhone());
        Log.d("id-manasa",GROUPID);
        db.update(GROUPS,contentValues,GROUPID + " = ? ",selectionArgs);

    }*/


    public UserContact getContact(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_USER_ID,
                COLUMN_USER_IMAGE,
                COLUMN_FIRST_NAME,
                COLUMN_LAST_NAME,
                COLUMN_ADDRESS,
                COLUMN_PHONE_NUMBER,
                COLUMN_SECOND_NUMBER,
                GROUP_NAMES
        };
        String selection = COLUMN_USER_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                TABLE_CONTACT_APP_DEMO,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    if (cursor!=null)
        cursor.moveToFirst();

    UserContact userContact=new UserContact();
    userContact.setContactUserId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
    userContact.setContactUserImage(cursor.getBlob(cursor.getColumnIndex(COLUMN_USER_IMAGE)));
    userContact.setContactUserFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
    userContact.setContactUserLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));
    userContact.setContactUserAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
    userContact.setContactUserPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER)));
    userContact.setContactUserSecond(cursor.getString(cursor.getColumnIndex(COLUMN_SECOND_NUMBER)));
    userContact.setContactGroupName(cursor.getString(cursor.getColumnIndex(GROUP_NAMES)));
    Log.d("details","do "+cursor.getString(cursor.getColumnIndex(COLUMN_SECOND_NUMBER)));
    cursor.close();
    return userContact;
    }

    public List<UserContact> getAllContacts()
    {
        List<UserContact> userContactList=new ArrayList<>();
        String selectQuery = null;
        String sort=getState();
        String grp=ContactHomeActivity.groups;
        Log.d("id-manasask",ContactHomeActivity.groups);
        if(grp.equals("Friends") || grp.equals("Family") || grp.equals("Colleagues") || grp.equals("Schoolmates") || grp.equals("VIPs") || grp.equals("Others"))
        {
            selectQuery = "SELECT * FROM " + TABLE_CONTACT_APP_DEMO+" WHERE "+GROUP_NAMES+" LIKE '%"+ContactHomeActivity.groups+"%'" + " ORDER BY " + COLUMN_USER_ID + " ASC;";

        }

        else
        {
            Log.d("id-manasask","entered-all");
            if (sort.equals("firstname"))
            {
                selectQuery = "SELECT * FROM " + TABLE_CONTACT_APP_DEMO + " ORDER BY " + "LOWER(" + COLUMN_FIRST_NAME + ") ASC, LOWER(" + COLUMN_LAST_NAME + ") ASC;";

            }
            else if (sort.equals("lastname"))
                selectQuery = "SELECT * FROM " + TABLE_CONTACT_APP_DEMO + " ORDER BY " + "LOWER(" + COLUMN_LAST_NAME + ") ASC, LOWER(" + COLUMN_FIRST_NAME + ") ASC;";
            else
                selectQuery = "SELECT * FROM " + TABLE_CONTACT_APP_DEMO + " ORDER BY " + COLUMN_USER_ID + " ASC;";
        }
       /* else {
            selectQuery = "SELECT * FROM " + TABLE_CONTACT_APP_DEMO+" WHERE "+GROUP_NAMES+" LIKE '%"+ContactHomeActivity.groups+"%'" + " ORDER BY " + COLUMN_USER_ID + " ASC;";
        }*/


        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor!=null && cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
            UserContact userContact=cursorToRecord(cursor);
            userContactList.add(userContact);
            cursor.moveToNext();
            }
        }
        db.close();
        return userContactList;
    }
    public String send(String str, String kind)
    {
        String selectquery;
        String sendList="";
        SQLiteDatabase db = this.getWritableDatabase();
        if(kind=="mail")
        {
            //for()
                selectquery = "SELECT * FROM " + TABLE_CONTACT_APP_DEMO + " WHERE " + GROUP_NAMES + " LIKE '%" + str + "%'";

                Cursor cursor = db.rawQuery(selectquery, null);
                if (cursor != null && cursor.moveToFirst()) {
                    while (!cursor.isAfterLast())
                    {
                        sendList+=(","+cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                        cursor.moveToNext();
                    }
                }
        }
        else {
                selectquery = "SELECT * FROM " + TABLE_CONTACT_APP_DEMO + " WHERE " + GROUP_NAMES + " LIKE '%" + str + "%'";

                //SQLiteDatabase db = this.getWritableDatabase();
                Cursor cursor = db.rawQuery(selectquery, null);
                if (cursor != null && cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        sendList+=(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER))+";");
                        cursor.moveToNext();
                    }
                }
            }
        db.close();
        return sendList;
    }

    private UserContact cursorToRecord(Cursor cursor) {
        UserContact userContact=new UserContact();
        userContact.setContactUserId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
        userContact.setContactUserImage(cursor.getBlob(cursor.getColumnIndex(COLUMN_USER_IMAGE)));
        userContact.setContactUserFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
        userContact.setContactUserLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));
        userContact.setContactUserAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
        userContact.setContactUserPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER)));
        userContact.setContactUserSecond(cursor.getString(cursor.getColumnIndex(COLUMN_SECOND_NUMBER)));
        userContact.setContactGroupName(cursor.getString(cursor.getColumnIndex(GROUP_NAMES)));

        return userContact;
    }



    public int updateContact(UserContact userContact){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        String[] selectionArgs={userContact.getContactUserId()};

        contentValues.put(COLUMN_USER_ID,userContact.getContactUserId());
        contentValues.put(COLUMN_USER_IMAGE,userContact.getContactUserImage());
        //Log.d("Android", String.valueOf(contentValues));
        contentValues.put(COLUMN_FIRST_NAME,userContact.getContactUserFirstName());
        contentValues.put(COLUMN_LAST_NAME,userContact.getContactUserLastName());
        contentValues.put(COLUMN_ADDRESS,userContact.getContactUserAddress());
        contentValues.put(COLUMN_PHONE_NUMBER,userContact.getContactUserPhone());
        contentValues.put(COLUMN_SECOND_NUMBER,userContact.getContactUserSecond());
        contentValues.put(GROUP_NAMES,userContact.getContactGroupName());
        Log.d(TAG, "Before return of update contact ");
        return db.update(TABLE_CONTACT_APP_DEMO,contentValues,COLUMN_USER_ID + " = ? ",selectionArgs);
    }
    public void deleteContact(UserContact userContact){
        SQLiteDatabase db=this.getWritableDatabase();
        String[] selectionArgs={userContact.getContactUserId()};

        db.delete(TABLE_CONTACT_APP_DEMO,COLUMN_USER_ID+" = ? ",selectionArgs);
        db.close();
    }

}
