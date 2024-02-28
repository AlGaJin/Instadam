package com.chex.instadam.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.enums.PostTypes;
import com.chex.instadam.java.Chat;
import com.chex.instadam.java.Message;
import com.chex.instadam.java.Post;
import com.chex.instadam.java.User;

import java.security.MessageDigest;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_CREATE_TABLE_LIKED_POSTS);
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

    public boolean isUsernameRegistered(String username){
        Cursor cursor = this.getReadableDatabase().query(
                EstructuraBBDD.TABLE_USERS,
                new String[]{EstructuraBBDD.COLUMN_USERNAME},
                EstructuraBBDD.COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null
        );

        return cursor.moveToNext();
    }

    public boolean isEmailRegistered(String email){
        Cursor cursor = this.getReadableDatabase().query(
                EstructuraBBDD.TABLE_USERS,
                new String[]{EstructuraBBDD.COLUMN_EMAIL},
                EstructuraBBDD.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );

        return cursor.moveToNext();
    }

    public int insertUser(String username, String psswd, String email){
        if(isUsernameRegistered(username)) return 1; //Código de error: Nombre de usuario ya registrado
        if(isEmailRegistered(email)) return 2; //Código de error: Email ya registrado

        ContentValues values = new ContentValues();
        values.put(EstructuraBBDD.COLUMN_USERNAME, username);
        values.put(EstructuraBBDD.COLUMN_PASSWORD, encrypt(psswd));
        values.put(EstructuraBBDD.COLUMN_PROFILE_PIC, "profilePics/DEFAULT.png");
        values.put(EstructuraBBDD.COLUMN_EMAIL, email);
        values.put(EstructuraBBDD.COLUMN_CREATION_DATE, new Date(System.currentTimeMillis()).getTime());

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

        if(!logedUser.getUsername().equals(user.getUsername()) && isUsernameRegistered(user.getUsername())) return 1;
        if (!logedUser.getEmail().equals(user.getEmail()) && isEmailRegistered(user.getEmail())) return 2;

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

    public List<User> getAllUsers(User logedUser){
        List<User> allUsers = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + EstructuraBBDD.TABLE_USERS + " WHERE " + EstructuraBBDD.COLUMN_ID + " <>?", new String[]{logedUser.getId()+""});
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_USERNAME));
            String profilePic = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_PROFILE_PIC));
            allUsers.add(new User(id, username, null, profilePic, null));
        }
        return allUsers;
    }

    public boolean isFollowing(User follower, User followed) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + EstructuraBBDD.TABLE_FOLLOWERS +
                " WHERE " + EstructuraBBDD.COLUMN_ID_FOLLOWING + " =? AND "
                + EstructuraBBDD.COLUMN_ID_FOLLOWED + "=?",
                new String[]{follower.getId()+"", followed.getId()+""});
        return cursor.moveToNext();
    }

    public void changeFollow(User follower, User followed) {
        if(isFollowing(follower, followed)){
            this.getWritableDatabase().delete(EstructuraBBDD.TABLE_FOLLOWERS,
                    EstructuraBBDD.COLUMN_ID_FOLLOWING + "=? AND "
                            + EstructuraBBDD.COLUMN_ID_FOLLOWED + "=?",
                    new String[]{follower.getId()+"", followed.getId()+""});
        }else{
            ContentValues values = new ContentValues();
            values.put(EstructuraBBDD.COLUMN_ID_FOLLOWING, follower.getId());
            values.put(EstructuraBBDD.COLUMN_ID_FOLLOWED, followed.getId());

            this.getWritableDatabase().insert(EstructuraBBDD.TABLE_FOLLOWERS, null, values);
        }
    }

    public List<User> getFilteredUsers(User logedUser, String filter) {
        List<User> filteredUsers = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + EstructuraBBDD.TABLE_USERS +
                " WHERE " + EstructuraBBDD.COLUMN_ID + " <>? AND " +
                EstructuraBBDD.COLUMN_USERNAME + " like ?",
                new String[]{logedUser.getId()+"", filter+"%"});

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_USERNAME));
            String profilePic = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_PROFILE_PIC));
            filteredUsers.add(new User(id, username, null, profilePic, null));
        }
        return filteredUsers;
    }

    public void insertPost(String title, String sciName, String cmnName, String dsc, String type, User publisher, String fbPath){
        ContentValues values = new ContentValues();
        values.put(EstructuraBBDD.COLUMN_ID_USER, publisher.getId()+"");
        values.put(EstructuraBBDD.COLUMN_PUBLISH_DATE, new Date(System.currentTimeMillis()).getTime());
        values.put(EstructuraBBDD.COLUMN_TITLE, title);
        values.put(EstructuraBBDD.COLUMN_DSCRIP, dsc);
        values.put(EstructuraBBDD.COLUMN_SCIENTIFIC_NAME, sciName);
        values.put(EstructuraBBDD.COLUMN_COMMON_NAME, cmnName);
        values.put(EstructuraBBDD.COLUMN_FIREBASE_PATH, fbPath);

        switch (type){
            case "Fungi":
                type = PostTypes.FNG.toString();
                break;
            case "Plantae":
                type = PostTypes.PLT.toString();
                break;
            case "Animalia":
                type = PostTypes.ANM.toString();
                break;
        }

        values.put(EstructuraBBDD.COLUMN_TYPE, type);

        this.getWritableDatabase().insert(EstructuraBBDD.TABLE_POSTS, null, values);
    }

    private Post createPost(Cursor postCursor){
        int id = postCursor.getInt(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID));
        int idPublisher = postCursor.getInt(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID_USER));
        long publishDate = postCursor.getLong(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_PUBLISH_DATE));
        String title = postCursor.getString(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_TITLE));
        String dsc = postCursor.getString(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_DSCRIP));
        String fbPostPath = postCursor.getString(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_FIREBASE_PATH));

        PostTypes postType;
        switch (postCursor.getString(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_TYPE))){
            case EstructuraBBDD.ENUM_FUNGI:
                postType = PostTypes.FNG;
                break;
            case EstructuraBBDD.ENUM_PLANTAE:
                postType = PostTypes.PLT;
                break;
            case EstructuraBBDD.ENUM_ANIMALIA:
                postType = PostTypes.ANM;
                break;
            default:
                postType = null;
                break;
        }

        String sciName = postCursor.getString(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_SCIENTIFIC_NAME));
        String cmnName = postCursor.getString(postCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_COMMON_NAME));

        return new Post(id, idPublisher, new Date(publishDate), title, dsc, postType, sciName, cmnName, fbPostPath);
    }
    public List<Post> getUserPosts(User user){
        List<Post> posts = new ArrayList<>();
        Cursor cursor = this.getWritableDatabase().rawQuery(
                "SELECT * FROM " + EstructuraBBDD.TABLE_POSTS +
                        " WHERE " + EstructuraBBDD.COLUMN_ID_USER + "=?",
                        new String[]{user.getId()+""}
        );

        while (cursor.moveToNext()){
            posts.add(createPost(cursor));
        }

        return posts;
    }

    public List<Post> getFollowedPosts(User logedUser) {
        List<Post> posts = new ArrayList<>();
        Cursor userCursor = this.getReadableDatabase().rawQuery(
                "SELECT " + EstructuraBBDD.COLUMN_ID_FOLLOWED +
                        " FROM " + EstructuraBBDD.TABLE_FOLLOWERS +
                        " WHERE " + EstructuraBBDD.COLUMN_ID_FOLLOWING + "=?",
                        new String[]{logedUser.getId()+""});

        while (userCursor.moveToNext()){
            Cursor postCursor = this.getReadableDatabase().rawQuery(
                    "SELECT * FROM " + EstructuraBBDD.TABLE_POSTS +
                            " WHERE " + EstructuraBBDD.COLUMN_ID_USER + "=?",
                            new String[]{userCursor.getString(userCursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID_FOLLOWED))});
            while (postCursor.moveToNext()) {
                posts.add(createPost(postCursor));
            }
        }

        return posts;
    }

    public Integer getTotalLikes(Post post){
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + EstructuraBBDD.TABLE_LIKED_POSTS +
                " WHERE " + EstructuraBBDD.COLUMN_ID_POST + "=?",
                new String[]{post.getId()+""}
        );

        if(cursor.moveToNext()){
            return cursor.getInt(0);
        }

        return 0;
    }

    public boolean isLiked(User user, Post post){
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + EstructuraBBDD.TABLE_LIKED_POSTS +
                    " WHERE " + EstructuraBBDD.COLUMN_ID_USER + "=? AND " +
                    EstructuraBBDD.COLUMN_ID_POST + "=?",
                new String[]{user.getId()+"", post.getId()+""}
        );

        return cursor.moveToNext();
    }

    public void changeLikedPost(User user, Post post) {
        if(isLiked(user, post)){
            this.getWritableDatabase().delete(EstructuraBBDD.TABLE_LIKED_POSTS,
                    EstructuraBBDD.COLUMN_ID_USER + "=? AND "
                            + EstructuraBBDD.COLUMN_ID_POST + "=?",
                    new String[]{user.getId()+"", post.getId()+""});
        }else{
            ContentValues values = new ContentValues();
            values.put(EstructuraBBDD.COLUMN_ID_USER, user.getId());
            values.put(EstructuraBBDD.COLUMN_ID_POST, post.getId());

            this.getWritableDatabase().insert(EstructuraBBDD.TABLE_LIKED_POSTS, null, values);
        }
    }

    private void createChat(User user, User otherUser){
        ContentValues values = new ContentValues();
        values.put(EstructuraBBDD.COLUMN_ID_USER, user.getId());
        values.put(EstructuraBBDD.COLUMN_ID_OTHERUSER, otherUser.getId());

        this.getWritableDatabase().insert(EstructuraBBDD.TABLE_CHATS, null, values);
    }

    public int getChatId(User user, User otherUser) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT " + EstructuraBBDD.COLUMN_ID +" FROM " + EstructuraBBDD.TABLE_CHATS +
                    " WHERE (" + EstructuraBBDD.COLUMN_ID_USER + " =? AND " +
                    EstructuraBBDD.COLUMN_ID_OTHERUSER + " =?) OR ("+
                    EstructuraBBDD.COLUMN_ID_USER + "=? AND " +
                    EstructuraBBDD.COLUMN_ID_OTHERUSER + "=?)",
                new String[]{user.getId()+"",otherUser.getId()+"",otherUser.getId()+"",user.getId()+""}
        );
        int chatId = -1;
        if(cursor.moveToNext()){
            chatId = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID));
        }else{
            createChat(user, otherUser);
            chatId = getChatId(user, otherUser);
        }

        return chatId;
    }

    public void insertMsg(String msg, int userId, int chatId){
        ContentValues values = new ContentValues();
        values.put(EstructuraBBDD.COLUMN_ID_CHAT, chatId);
        values.put(EstructuraBBDD.COLUMN_ID_USER, userId);
        values.put(EstructuraBBDD.COLUMN_MESSAGE, msg);
        values.put(EstructuraBBDD.COLUMN_SEND_DATE, String.valueOf(new Timestamp(System.currentTimeMillis())));

        this.getWritableDatabase().insert(EstructuraBBDD.TABLE_MESSAGES, null, values);
    }

    public Message createMsg(Cursor cursor, int chatId){
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID_USER));
        String msg = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_MESSAGE));
        Timestamp sendTime = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_SEND_DATE)));

        return new Message(chatId, userId, msg, sendTime);
    }

    public List<Message> getChatMsg(int chatId){
        List<Message> msgList = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().rawQuery(
          "SELECT * FROM " + EstructuraBBDD.TABLE_MESSAGES +
              " WHERE " + EstructuraBBDD.COLUMN_ID_CHAT + "=?",
              new String[]{chatId+""}
        );

        while (cursor.moveToNext()){
            msgList.add(createMsg(cursor, chatId));
        }

        return msgList;
    }

    public List<Chat> getChats(int userId) {
        List<Chat> chats = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + EstructuraBBDD.TABLE_CHATS +
                        " WHERE " + EstructuraBBDD.COLUMN_ID_USER + "=? OR " +
                        EstructuraBBDD.COLUMN_ID_OTHERUSER + "=?",
                new String[]{userId+"",userId+""}
        );

        while (cursor.moveToNext()){
            int chatId = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID));
            int mUserId = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID_USER));
            int otherUserId = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID_OTHERUSER));

            chats.add(new Chat(chatId, mUserId, otherUserId));
        }

        return chats;
    }

    public Message getLastMsg(int chatId) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + EstructuraBBDD.TABLE_MESSAGES +
                    " WHERE " + EstructuraBBDD.COLUMN_ID_CHAT + "=? ORDER BY "
                    + EstructuraBBDD.COLUMN_SEND_DATE + " DESC LIMIT 1",
                    new String[]{chatId+""}
        );
        if(cursor.moveToNext()){
            return createMsg(cursor, chatId);
        }
        return null;
    }
}
