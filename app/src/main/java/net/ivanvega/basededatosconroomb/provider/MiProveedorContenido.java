package net.ivanvega.basededatosconroomb.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.ivanvega.basededatosconroomb.data.AppDataBase;
import net.ivanvega.basededatosconroomb.data.User;
import net.ivanvega.basededatosconroomb.data.UserDao;

import java.util.ArrayList;
import java.util.List;

public class MiProveedorContenido extends ContentProvider {
 /*Estructura de mi uri:
        uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user  -> insert y query
        uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user/#  -> uodate, query y delete
        uri -> content://net.ivanvega.basededatoslocalconrooma.provider/user/*  -> query, update y delete}
                        net.ivanvega.basededatoslocalconrooma.provider
     */

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {

        sURIMatcher.addURI("net.ivanvega.basededatosconroomb.provider","usuario", 1);
        sURIMatcher.addURI("net.ivanvega.basededatosconroomb.provider","usuario/#", 2);
        sURIMatcher.addURI("net.ivanvega.basededatosconroomb.provider","usuario/*", 3);
    }

    @Override
    public boolean onCreate() {

        return false;
    }

    private Cursor listUserToCursorUser( List<User>   usuaarios){
        MatrixCursor cursor = new MatrixCursor(new String[]{
                "uid","first_name","last_name"
        })    ;

        for(User usuario: usuaarios ){
            cursor.newRow().add("uid", usuario.uid)
                    .add("first_name", usuario.firstName)
                    .add("last_name", usuario.lastName);
        }

        return cursor;
    }



    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] strings,
                        @Nullable String s,
                        @Nullable String[] strings1,
                        @Nullable String s1) {

        AppDataBase db =
                AppDataBase.getDatabaseInstance(getContext());

        Cursor cursor= null;
        String apellido;

        UserDao dao = db.userDao();
        switch (sURIMatcher.match(uri)){
            case 1:
                cursor =   listUserToCursorUser( dao.getAll());
                break;
            case 2:
                apellido =uri.getLastPathSegment();
                cursor= listUserToCursorUser ((List<User>) dao.findByName(apellido));
                break;

            case 3:
                apellido=uri.getLastPathSegment();
                List<User> usuario= new ArrayList<User>();
                usuario.add(dao.findByName(apellido));

                cursor= listUserToCursorUser (usuario);
                if(cursor==null){
                    Log.d("Usuario", "Esta vacio");
                }
                else{
                    Log.d("Consulta", "No esta vacio");
                }
                break;

        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        String typeMime = "";

        switch (sURIMatcher.match(uri)) {
            case 1:
                typeMime = "vnd.android.cursor.dir/vnd.net.ivanvega.basededatosconroomb.provider.usuario";
                break;
            case 2:
                typeMime = "vnd.android.cursor.item/vnd.net.ivanvega.basededatosconroomb.provider.usuario";
                break;
            case 3:

                typeMime = "vnd.android.cursor.dir/vnd.net.ivanvega.basededatosconroomb.provider.usuario";
                break;
        }
        return typeMime;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues contentValues) {
        AppDataBase db =
                AppDataBase.getDatabaseInstance(getContext());
        Cursor cursor= null;
        UserDao dao = db.userDao();
        User usuario= new User();;
        switch (sURIMatcher.match(uri)){
            case 1:

                usuario.firstName = contentValues.getAsString(UsuarioProviderContract.FIRSTNAME_COLUMN);
                usuario.lastName = contentValues.getAsString(UsuarioProviderContract.LASTNAME_COLUMN);

                long  newid = dao.insert(usuario);
                return  Uri.withAppendedPath(uri, String.valueOf( newid));

        }

        return   Uri.withAppendedPath(uri, String.valueOf( -1))  ;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        switch (sURIMatcher.match(uri)){
            case 2:
                AppDataBase db =
                        AppDataBase.getDatabaseInstance(getContext());
                UserDao dao = db.userDao();
                int id=Integer.parseInt(uri.getLastPathSegment());
                List <User> usuarios=    dao.getAll();
                for ( User u:usuarios) {
                    if(u.uid==id){
                        dao.delete(u);
                        break;
                    }
                }

                break;
        }


        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String s, @Nullable String[] strings) {

        int id  = Integer.parseInt( uri.getLastPathSegment());

        AppDataBase db =
                AppDataBase.getDatabaseInstance(getContext());
        Cursor cursor= null;
        UserDao dao = db.userDao();

        List<User> usuarioUpdate  =  dao.loadAllByIds(new int[]{id});

        usuarioUpdate.get(0).firstName =
                contentValues.getAsString(UsuarioProviderContract.FIRSTNAME_COLUMN );
        usuarioUpdate.get(0).lastName =
                contentValues.getAsString(UsuarioProviderContract.LASTNAME_COLUMN );

        return dao.updateUser(usuarioUpdate.get(0));
    }
}
