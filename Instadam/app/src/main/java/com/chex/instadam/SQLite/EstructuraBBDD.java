package com.chex.instadam.SQLite;

public class EstructuraBBDD {
    public static final String DATABASE_NAME = "instadam";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME ="username";
    public static final String COLUMN_PASSWORD = "psswd";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PROFILE_PIC = "profile_pic";
    public static final String COLUMN_CREATION_DATE = "creation";
    public static final String COLUMN_DSCRIP = "dscrip";


    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_NOTIFY_TYPE = "notify_type";
    public static final String ENUM_MSG = "MSG";
    public static final String ENUM_POST = "POST";
    public static final String ENUM_FOLLOWER = "FOLLOWER";

    public static final String TABLE_FOLLOWERS = "followers";
    public static final String COLUMN_ID_FOLLOWING = "id_following";
    public static final String COLUMN_ID_FOLLOWED = "id_followed";

    public static final String TABLE_CHATS = "chats";
    public static final String COLUMN_ID_OTHERUSER = "id_otherUser";

    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_ID_USER = "id_user";
    public static final String COLUMN_ID_CHAT = "id_chat";
    public static final String COLUMN_SEND_DATE = "send_date";

    public static final String TABLE_POSTS = "posts";
    public static final String COLUMN_PUBLISH_DATE = "publish_date";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SCIENTIFIC_NAME = "scientific_name";
    public static final String COLUMN_COMMON_NAME = "common_name";

    public static final String TABLE_COMMENTS = "comments";
    public static final String COLUMN_ID_POST = "id_post";
    public static final String COLUMN_MESSAGE = "msg";

    public static final String SQL_CREATE_TABLE_USERS =
            "CREATE TABLE " + EstructuraBBDD.TABLE_USERS + "(" +
                    EstructuraBBDD.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    EstructuraBBDD.COLUMN_USERNAME + " TEXT, " +
                    EstructuraBBDD.COLUMN_PASSWORD + " TEXT, " +
                    EstructuraBBDD.COLUMN_EMAIL + " TEXT, " +
                    EstructuraBBDD.COLUMN_PROFILE_PIC + " TEXT, " +
                    EstructuraBBDD.COLUMN_CREATION_DATE + " DATE, " +
                    EstructuraBBDD.COLUMN_DSCRIP + " TEXT)";

    public static final String SQL_CREATE_TABLE_NOTIFICATIONS =
            "CREATE TABLE " + EstructuraBBDD.TABLE_NOTIFICATIONS + "(" +
                    EstructuraBBDD.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    EstructuraBBDD.COLUMN_ID_USER + " INTEGER, " +
                    EstructuraBBDD.COLUMN_NOTIFY_TYPE + " TEXT CHECK(" + EstructuraBBDD.COLUMN_NOTIFY_TYPE + " IN ('" + EstructuraBBDD.ENUM_MSG + "', '" + EstructuraBBDD.ENUM_POST + "', '" + EstructuraBBDD.ENUM_FOLLOWER + "'))," +
                    EstructuraBBDD.COLUMN_SEND_DATE + " DATE, " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_USER + ") REFERENCES " + EstructuraBBDD.TABLE_USERS + "(" + EstructuraBBDD.COLUMN_ID + "))";


    public static final String SQL_CREATE_TABLE_FOLLOWERS =
            "CREATE TABLE " + EstructuraBBDD.TABLE_FOLLOWERS + "(" +
                    EstructuraBBDD.COLUMN_ID_FOLLOWING + " INTEGER, " +
                    EstructuraBBDD.COLUMN_ID_FOLLOWED + " INTEGER, " +
                    "PRIMARY KEY (" + EstructuraBBDD.COLUMN_ID_FOLLOWING + ", " + EstructuraBBDD.COLUMN_ID_FOLLOWED + "), " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_FOLLOWING + ") REFERENCES " + EstructuraBBDD.TABLE_USERS + "(" + EstructuraBBDD.COLUMN_ID + "), " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_FOLLOWED + ") REFERENCES " + EstructuraBBDD.TABLE_USERS + "(" + EstructuraBBDD.COLUMN_ID + "))";

    public static final String SQL_CREATE_TABLE_CHATS =
            "CREATE TABLE " + EstructuraBBDD.TABLE_CHATS + "(" +
                    EstructuraBBDD.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    EstructuraBBDD.COLUMN_ID_USER + " INTEGER, " +
                    EstructuraBBDD.COLUMN_ID_OTHERUSER + " INTEGER, " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_USER + ") REFERENCES " + EstructuraBBDD.TABLE_USERS + "(" + EstructuraBBDD.COLUMN_ID + "), " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_OTHERUSER + ") REFERENCES " + EstructuraBBDD.TABLE_USERS + "(" + EstructuraBBDD.COLUMN_ID + "))";

    public static final String SQL_CREATE_TABLE_MESSAGES =
            "CREATE TABLE " + EstructuraBBDD.TABLE_MESSAGES + "(" +
                    EstructuraBBDD.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    EstructuraBBDD.COLUMN_ID_USER + " INTEGER, " +
                    EstructuraBBDD.COLUMN_ID_CHAT + " INTEGER, " +
                    EstructuraBBDD.COLUMN_SEND_DATE + " DATE, " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_USER + ") REFERENCES " + EstructuraBBDD.TABLE_USERS + "(" + EstructuraBBDD.COLUMN_ID + "), " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_CHAT + ") REFERENCES " + EstructuraBBDD.TABLE_CHATS + "(" + EstructuraBBDD.COLUMN_ID + "))";

    public static final String SQL_CREATE_TABLE_POSTS =
            "CREATE TABLE " + EstructuraBBDD.TABLE_POSTS + "(" +
                    EstructuraBBDD.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    EstructuraBBDD.COLUMN_ID_USER + " INTEGER, " +
                    EstructuraBBDD.COLUMN_PUBLISH_DATE + " DATE, " +
                    EstructuraBBDD.COLUMN_TITLE + " TEXT, " +
                    EstructuraBBDD.COLUMN_DSCRIP + " TEXT, " +
                    EstructuraBBDD.COLUMN_SCIENTIFIC_NAME + " TEXT, " +
                    EstructuraBBDD.COLUMN_COMMON_NAME + " TEXT, " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_USER + ") REFERENCES " + EstructuraBBDD.TABLE_USERS + "(" + EstructuraBBDD.COLUMN_ID + "))";

    public static final String SQL_CREATE_TABLE_COMMENTS =
            "CREATE TABLE " + EstructuraBBDD.TABLE_COMMENTS + "("+
                    EstructuraBBDD.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    EstructuraBBDD.COLUMN_ID_POST + " INTEGER, " +
                    EstructuraBBDD.COLUMN_ID_USER + " INTEGER, " +
                    EstructuraBBDD.COLUMN_MESSAGE + " TEXT, " +
                    EstructuraBBDD.COLUMN_SEND_DATE + " DATE, " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_POST + ") REFERENCES " + EstructuraBBDD.TABLE_POSTS + "(" + EstructuraBBDD.COLUMN_ID + "), " +
                    "FOREIGN KEY (" + EstructuraBBDD.COLUMN_ID_USER + ") REFERENCES " + EstructuraBBDD.TABLE_USERS + "(" + EstructuraBBDD.COLUMN_ID + "))";

    public static final String SQL_DELETE_TABLES =
            "DROP TABLES IF EXISTS " + EstructuraBBDD.TABLE_USERS + ", " +
                    EstructuraBBDD.TABLE_NOTIFICATIONS + ", " + EstructuraBBDD.TABLE_FOLLOWERS + ", " +
                    EstructuraBBDD.TABLE_CHATS + ", " + EstructuraBBDD.TABLE_MESSAGES + ", " +
                    EstructuraBBDD.TABLE_POSTS + ", " + EstructuraBBDD.TABLE_COMMENTS;

    public static final String[] login_projection =
            {EstructuraBBDD.COLUMN_ID,
                EstructuraBBDD.COLUMN_USERNAME,
                EstructuraBBDD.COLUMN_EMAIL,
                EstructuraBBDD.COLUMN_PROFILE_PIC,
                EstructuraBBDD.COLUMN_DSCRIP};

    public static final String login_selection = "(" + EstructuraBBDD.COLUMN_USERNAME + "=? OR "
            + EstructuraBBDD.COLUMN_EMAIL + "=?) AND " + EstructuraBBDD.COLUMN_PASSWORD + "=?";
}
