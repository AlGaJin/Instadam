package com.chex.instadam.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chex.instadam.java.User;

import java.security.MessageDigest;
import java.sql.Date;

public class BBDDHelper extends SQLiteOpenHelper {

    public BBDDHelper(Context context){
        super(context, EstructuraBBDD.DATABASE_NAME, null, EstructuraBBDD.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_CREATE_TABLE_NOTIFICATIONS);
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_CREATE_TABLE_FOLLOWERS);
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_CREATE_TABLE_CHATS);
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_CREATE_TABLE_MESSAGES);
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_CREATE_TABLE_POSTS);
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_CREATE_TABLE_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_DELETE_TABLES);
        onCreate(sqLiteDatabase);
    }

    private String encrypt(String toEncrypt){
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(toEncrypt.getBytes());
            byte[] bytes = md5.digest();
            StringBuilder sBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                sBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            return sBuilder.toString(); //Contraseña encriptada
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int insertUser(String username, String psswd, String email, Date date){

        Cursor cursor;

        cursor = this.getReadableDatabase().query(
                EstructuraBBDD.TABLE_USERS,
                new String[]{EstructuraBBDD.COLUMN_USERNAME},
                EstructuraBBDD.COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null
        );

        if(cursor.moveToNext()) return 1; //Código de error: Nombre de usuario ya registrado


        cursor = this.getReadableDatabase().query(
                EstructuraBBDD.TABLE_USERS,
                new String[]{EstructuraBBDD.COLUMN_EMAIL},
                EstructuraBBDD.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        if(cursor.moveToNext()) return 2; //Código de error: Email ya registrado

        ContentValues values = new ContentValues();
        values.put(EstructuraBBDD.COLUMN_USERNAME, username);
        values.put(EstructuraBBDD.COLUMN_PASSWORD, encrypt(psswd));
        values.put(EstructuraBBDD.COLUMN_EMAIL, email);
        values.put(EstructuraBBDD.COLUMN_CREATION_DATE, date.getTime());

        this.getWritableDatabase().insert(EstructuraBBDD.TABLE_USERS, null, values);
        return 0; //No ha habido ningún error.
    }

    public Integer login_user(String[] selectionArgs){

        selectionArgs[2] = encrypt(selectionArgs[2]);

        Cursor cursor = this.getReadableDatabase().query(
                EstructuraBBDD.TABLE_USERS,
                EstructuraBBDD.login_projection,
                EstructuraBBDD.login_selection,
                selectionArgs,
                null, null, null
        );

        if(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID));
            return id;
        }

        return null;
    }
}
