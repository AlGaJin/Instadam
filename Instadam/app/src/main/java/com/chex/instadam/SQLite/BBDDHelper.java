package com.chex.instadam.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chex.instadam.activities.MainActivity;
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

    public boolean isUsernameAvailable(String username){
        Cursor cursor = this.getReadableDatabase().query(
                EstructuraBBDD.TABLE_USERS,
                new String[]{EstructuraBBDD.COLUMN_USERNAME},
                EstructuraBBDD.COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null
        );

        return !cursor.moveToNext();
    }

    public boolean isEmailAvailable(String email){
        Cursor cursor = this.getReadableDatabase().query(
                EstructuraBBDD.TABLE_USERS,
                new String[]{EstructuraBBDD.COLUMN_EMAIL},
                EstructuraBBDD.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        return !cursor.moveToNext();
    }

    public int insertUser(String username, String psswd, String email, Date date){
        if(!isUsernameAvailable(username)) return 1; //Código de error: Nombre de usuario ya registrado
        if(!isEmailAvailable(email)) return 2; //Código de error: Email ya registrado

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
            return cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID));
        }

        return null;
    }

    public User getUserById(String id){
        Cursor cursor = this.getReadableDatabase().query(
                EstructuraBBDD.TABLE_USERS,
                EstructuraBBDD.login_projection,
                EstructuraBBDD.COLUMN_ID + "=?",
                new String[]{id},
                null, null, null
        );

        if(cursor.moveToNext()){
            String username = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_USERNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_EMAIL));
            String profilePic = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_PROFILE_PIC));
            String dscp = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_DSCRIP));

            return new User(Integer.parseInt(id), username, email, profilePic, dscp);
        }

        return null;
    }

    public String getNumberUserFollowed(int id) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
          "SELECT COUNT(*) FROM " + EstructuraBBDD.TABLE_FOLLOWERS +
          " WHERE " + EstructuraBBDD.COLUMN_ID_FOLLOWING + "=?",
          new String[]{id+""}
        );

        if(cursor.moveToNext()) return cursor.getString(0);
        return null;
    }

    public String getNumberUserFollowing(int id) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + EstructuraBBDD.TABLE_FOLLOWERS +
                        " WHERE " + EstructuraBBDD.COLUMN_ID_FOLLOWED + "=?",
                new String[]{id+""}
        );

        if(cursor.moveToNext()) return cursor.getString(0);
        return null;
    }

    public String getNumberUserCompendium(int id) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + EstructuraBBDD.TABLE_POSTS +
                        " WHERE " + EstructuraBBDD.COLUMN_ID_USER + "=?",
                new String[]{id+""}
        );

        if(cursor.moveToNext()) return cursor.getString(0);
        return null;
    }

    public int editUser(User user) {
        ContentValues values = new ContentValues();
        User logedUser = MainActivity.logedUser;

        if(!logedUser.getUsername().equals(user.getUsername()) && !isUsernameAvailable(user.getUsername())) return 1;
        if (!logedUser.getEmail().equals(user.getEmail()) && !isEmailAvailable(user.getEmail())) return 2;

        values.put(EstructuraBBDD.COLUMN_USERNAME, user.getUsername());
        values.put(EstructuraBBDD.COLUMN_EMAIL, user.getEmail());
        values.put(EstructuraBBDD.COLUMN_DSCRIP, user.getDscp());
        values.put(EstructuraBBDD.COLUMN_PROFILE_PIC, user.getProfilePic());

        this.getReadableDatabase()
                .update(EstructuraBBDD.TABLE_USERS,
                        values,
                        EstructuraBBDD.COLUMN_ID+"=?",
                        new String[]{user.getId()+""}
                );

        return 0;
    }
}
