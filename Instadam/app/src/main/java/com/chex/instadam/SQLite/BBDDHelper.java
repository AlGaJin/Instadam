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

/**
 * Permite hacer cambios en la base de dato, es una conexión entre la aplicación y la base de datos local
 */
public class BBDDHelper extends SQLiteOpenHelper {

    public BBDDHelper(Context context){
        super(context, EstructuraBBDD.DATABASE_NAME, null, EstructuraBBDD.DATABASE_VERSION);
    }

    //Crea las tablas en la base de datos
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

    //Si se hace alguna actualización se eliminarán las tablas y se crearán de nuevo
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(EstructuraBBDD.SQL_DELETE_TABLES);
        onCreate(sqLiteDatabase);
    }

    //Encriptado sencillo para las contraseñas con MD5
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

    /**
     * Comprueba si un nombre de usuario ya está registrado
     * @param username el nombre de usuario que se quiere usar
     * @return si encuentra un registro con ese nombre devuelve un true
     */
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

    /**
     * Comprueba si un email de usuario ya está registrado
     * @param email el correo electrónico que se quiere usar
     * @return si encuentra un registro con ese email devuelve un true
     */
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

    /**
     * Añade un usuario a la base de datos
     * @param username el nombre de usuario que va a tener
     * @param psswd la contraseña para poder iniciar sesión
     * @param email el email al que se vincula el usuario
     * @return un código de estado:
     *         0 -> se ha credo el usuario
     *         1 -> el nombre de usuario ya está registrado
     *         2 -> el email ya está registrado
     */
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

    /**
     * Permite iniciar sesión, comprobando que el usuario con los datos recibidos existe en base de datos
     * @param selectionArgs los datos necesarios para iniciar sesión
     * @return el id del usuario que ha iniciado sesión o null si no se ha podido iniciar sesión
     */
    public Integer login_user(String[] selectionArgs){

        selectionArgs[2] = encrypt(selectionArgs[2]); //Encripta la contraseña

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

    /**
     * Permite obtener un usuario de la base de datos
     * @param id id del usuario en base de datos
     * @return un objeto de tipo usuario (User) o null si no se ha encontrado
     */
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

    /**
     * Devuelve el número de personas que sigue un usuario
     * @param id id del usuario que está siguiendo
     * @return número de usuarios que sigue el usuario
     */
    public String getNumberUserFollowed(int id) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
          "SELECT COUNT(*) FROM " + EstructuraBBDD.TABLE_FOLLOWERS +
          " WHERE " + EstructuraBBDD.COLUMN_ID_FOLLOWING + "=?",
          new String[]{id+""}
        );

        if(cursor.moveToNext()) return cursor.getString(0);
        return null;
    }

    /**
     * Devuelve el número de personas que siguen a un usuario
     * @param id id del usuario que están siguiendo
     * @return número de usuarios que siguen al usuario
     */
    public String getNumberUserFollowing(int id) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + EstructuraBBDD.TABLE_FOLLOWERS +
                        " WHERE " + EstructuraBBDD.COLUMN_ID_FOLLOWED + "=?",
                new String[]{id+""}
        );

        if(cursor.moveToNext()) return cursor.getString(0);
        return null;
    }

    /**
     * Deveuelve el total de publicaciones que tiene un usuario
     * @param id id del usuario del que quiere saberse el compendio total
     * @return número de publicaciones que tiene el usuario
     */
    public String getNumberUserCompendium(int id) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + EstructuraBBDD.TABLE_POSTS +
                        " WHERE " + EstructuraBBDD.COLUMN_ID_USER + "=?",
                new String[]{id+""}
        );

        if(cursor.moveToNext()) return cursor.getString(0);
        return null;
    }

    /**
     * Cambia los datos del usuario en base de datos
     * @param user el usuario que contiene los datos nuevos a aplicar
     * @return un código de estado:
     *         0 -> se han actualizado los datos correctamente
     *         1 -> el nombre de usuario ya está registrado
     *         2 -> el email ya está registrado
     */
    public int editUser(User user) {
        ContentValues values = new ContentValues();
        User logedUser = MainActivity.logedUser; //Los datos se comparan con los datos actuales del usuario

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

    /**
     * Devuelve una lista de todos los usuarios registrados
     * @param logedUser el usuario que ha iniciado sesión para no añadirlo en la lista
     * @return una lista cargada con objetos de tipo usuario (User)
     */
    public List<User> getAllUsers(User logedUser){
        List<User> allUsers = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + EstructuraBBDD.TABLE_USERS +
                    " WHERE " + EstructuraBBDD.COLUMN_ID + " <>?",
                    new String[]{logedUser.getId()+""});

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_USERNAME));
            String profilePic = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_PROFILE_PIC));
            allUsers.add(new User(id, username, null, profilePic, null));
        }

        return allUsers;
    }

    /**
     * Indica si un usuario sigue a otro
     * @param follower el usuario seguidor
     * @param followed al usuario que está siendo seguido
     * @return true en el caso de que el usuario lo esté siguiendo, false en el caso contrario
     */
    public boolean isFollowing(User follower, User followed) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + EstructuraBBDD.TABLE_FOLLOWERS +
                " WHERE " + EstructuraBBDD.COLUMN_ID_FOLLOWING + " =? AND "
                + EstructuraBBDD.COLUMN_ID_FOLLOWED + "=?",
                new String[]{follower.getId()+"", followed.getId()+""});
        return cursor.moveToNext();
    }

    /**
     * Cambia el estado del seguidor, es decir, si lo estaba siguiendo, ya no lo va a seguir y viceversa
     * @param follower el usuario seguidor
     * @param followed al usuario que está siendo seguido
     */
    public void changeFollow(User follower, User followed) {
        if(isFollowing(follower, followed)){ //Si lo sigue, se borra el dato de la base de dato (deja de seguirlo)
            this.getWritableDatabase().delete(EstructuraBBDD.TABLE_FOLLOWERS,
                    EstructuraBBDD.COLUMN_ID_FOLLOWING + "=? AND "
                            + EstructuraBBDD.COLUMN_ID_FOLLOWED + "=?",
                    new String[]{follower.getId()+"", followed.getId()+""});
        }else{ //Si no lo seguía lo añade a la base de datos (comienza a seguirle)
            ContentValues values = new ContentValues();
            values.put(EstructuraBBDD.COLUMN_ID_FOLLOWING, follower.getId());
            values.put(EstructuraBBDD.COLUMN_ID_FOLLOWED, followed.getId());

            this.getWritableDatabase().insert(EstructuraBBDD.TABLE_FOLLOWERS, null, values);
        }
    }

    /**
     * Filtra a los usuarios por su nombre de usuario y devuelve una lista con los que concidan con el filtro
     * @param logedUser usuario que ha iniciado sesión para obviarlo en la lista
     * @param filter el filtro que se va a aplicar
     * @return lista de usuarios filtrada por nombre de usuario
     */
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

    /**
     * Crea en la base de datos una nueva publicación
     * @param title titulo de la publicación
     * @param sciName nombre cientifico del ser vivo
     * @param cmnName nombre común del ser vivo
     * @param dsc descripción dada por el usuario
     * @param type el reino al que pertenece el ser vivo
     * @param publisher id del usuario que hace la publicación
     * @param fbPath la ruta en la que se encuentra la imagen en la base de datos remota (FireBase)
     */
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

    /**
     * Es usado internamente para no repetir código y crear un objeto publicación a partir de un cursor
     * @param postCursor el cursor que señala a los datos en base de datos
     * @return el objeto de tipo publicación (Post)
     */
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

    /**
     * Devuelve una lista de los post de un usuario concreto
     * @param user el usuario del que se quiere obtener las publicaciones
     * @return lista cargada con los objetos de tipo publicación (Post)
     */
    public List<Post> getUserPosts(User user){
        List<Post> posts = new ArrayList<>();
        Cursor cursor = this.getWritableDatabase().rawQuery(
                "SELECT * FROM " + EstructuraBBDD.TABLE_POSTS +
                        " WHERE " + EstructuraBBDD.COLUMN_ID_USER + "=? ORDER BY " + EstructuraBBDD.COLUMN_PUBLISH_DATE + " DESC",
                        new String[]{user.getId()+""}
        );

        while (cursor.moveToNext()){
            posts.add(createPost(cursor));
        }

        return posts;
    }

    /**
     * Devuelve la lista de posts de los usuarios a los que sigue el usuario que ha iniciado sesión
     * @param logedUser el usuario que ha iniciado sesión
     * @return lista cargada con objetos de tipo publicación (Post)
     */
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

        //Ordena las publicaciones por fecha, pues de forma original están en bloques por usuario
        posts.sort((post1, post2) -> {
            if(post1.getPublish_date().before(post2.getPublish_date())){
                return 1;
            }else if(post1.getPublish_date().after(post2.getPublish_date())){
                return -1;
            }else{
                return 0;
            }
        });

        return posts;
    }

    /**
     * Devuelve el total de likes que tiene una publicación
     * @param post la publicación de la cuál se quiere saber el total de me gustas
     * @return el total de me gustas que tiene una publicación
     */
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

    /**
     * Comprueba si un usuario le ha dado me gusta a una publicación
     * @param user el usuario del que se quiere saber si le ha dado me gusta
     * @param post la publicación de la que se quiere saber si se le ha dado me gusta
     * @return true en el caso de que le haya dado me gusta, false en el contrario
     */
    public boolean isLiked(User user, Post post){
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + EstructuraBBDD.TABLE_LIKED_POSTS +
                    " WHERE " + EstructuraBBDD.COLUMN_ID_USER + "=? AND " +
                    EstructuraBBDD.COLUMN_ID_POST + "=?",
                new String[]{user.getId()+"", post.getId()+""}
        );

        return cursor.moveToNext();
    }

    /**
     * Cambia el estado del me gusta, es decir, si antes no estaba con un me gusta, ahora sí lo estará
     * @param user el usuario que le da a me gusta o lo quita
     * @param post la publicación sobre la que se hace la acción
     */
    public void changeLikedPost(User user, Post post) {
        if(isLiked(user, post)){//Si estaba con un me gusta se le quirará eliminandolo de la base de datos
            this.getWritableDatabase().delete(EstructuraBBDD.TABLE_LIKED_POSTS,
                    EstructuraBBDD.COLUMN_ID_USER + "=? AND "
                            + EstructuraBBDD.COLUMN_ID_POST + "=?",
                    new String[]{user.getId()+"", post.getId()+""});
        }else{ //Si no tenía un me gusta se añaderá a la base de datos
            ContentValues values = new ContentValues();
            values.put(EstructuraBBDD.COLUMN_ID_USER, user.getId());
            values.put(EstructuraBBDD.COLUMN_ID_POST, post.getId());

            this.getWritableDatabase().insert(EstructuraBBDD.TABLE_LIKED_POSTS, null, values);
        }
    }

    /**
     * Método interno que crea un chat en la base de datos
     * @param user el usuario que lo crea
     * @param otherUser el usuario con el que se crea
     */
    private void createChat(User user, User otherUser){
        ContentValues values = new ContentValues();
        values.put(EstructuraBBDD.COLUMN_ID_USER, user.getId());
        values.put(EstructuraBBDD.COLUMN_ID_OTHERUSER, otherUser.getId());

        this.getWritableDatabase().insert(EstructuraBBDD.TABLE_CHATS, null, values);
    }

    /**
     * Devuelve el id del chat que existe entre los usuarios, si no existiera, se crea el chat y se devuelve ese nuevo id
     * @param user un usuario del chat
     * @param otherUser el otro usuario del chat
     * @return el id del chat
     */
    public int getChatId(User user, User otherUser) {
        //Se comprueba tanto que el usuario creador sea user como otherUser
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

    /**
     * Inserta un nuevo mensaje en la base de datos
     * @param msg el texto del mensaje
     * @param userId el usuario que lo envia
     * @param chatId el chat en el que se envía
     */
    public void insertMsg(String msg, int userId, int chatId){
        ContentValues values = new ContentValues();
        values.put(EstructuraBBDD.COLUMN_ID_CHAT, chatId);
        values.put(EstructuraBBDD.COLUMN_ID_USER, userId);
        values.put(EstructuraBBDD.COLUMN_MESSAGE, msg);
        values.put(EstructuraBBDD.COLUMN_SEND_DATE, String.valueOf(new Timestamp(System.currentTimeMillis())));

        this.getWritableDatabase().insert(EstructuraBBDD.TABLE_MESSAGES, null, values);
    }

    /**
     * Es usado internamente para no repetir código y crear un objeto mensaje a partir de un cursor
     * @param cursor el cursor que señala a los datos en base de datos
     * @return el objeto de tipo publicación (Post)
     */
    private Message createMsg(Cursor cursor){
        int chatId = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID_CHAT));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_ID_USER));
        String msg = cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_MESSAGE));
        Timestamp sendTime = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EstructuraBBDD.COLUMN_SEND_DATE)));

        return new Message(chatId, userId, msg, sendTime);
    }

    /**
     * Devuelve los mensajes de un chat
     * @param chatId el id del chat del que se quieren obtener los mensajes
     * @return una lista cargada con objetos de tipo mensaje (Message)
     */
    public List<Message> getChatMsg(int chatId){
        List<Message> msgList = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().rawQuery(
          "SELECT * FROM " + EstructuraBBDD.TABLE_MESSAGES +
              " WHERE " + EstructuraBBDD.COLUMN_ID_CHAT + "=? ORDER BY " + EstructuraBBDD.COLUMN_SEND_DATE,
              new String[]{chatId+""}
        );

        while (cursor.moveToNext()){
            msgList.add(createMsg(cursor));
        }

        return msgList;
    }

    /**
     * Devuelve la lista de chat que tiene un usuario
     * @param userId el id del usuario del que se quiere obtener sus chats
     * @return lista cargada con objetos de tipo chat
     */
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

    /**
     * Devuelve el último mensaje del chat
     * @param chatId id del chat que se quiere obtener el último mensaje
     * @return un objeto de tipo mensaje (Message)
     */
    public Message getLastMsg(int chatId) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + EstructuraBBDD.TABLE_MESSAGES +
                    " WHERE " + EstructuraBBDD.COLUMN_ID_CHAT + "=? ORDER BY "
                    + EstructuraBBDD.COLUMN_SEND_DATE + " DESC LIMIT 1",
                    new String[]{chatId+""}
        );
        if(cursor.moveToNext()){
            return createMsg(cursor);
        }
        return null;
    }
}
