package net.ivanvega.basededatosconroomb;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import net.ivanvega.basededatosconroomb.data.AppDataBase;
import net.ivanvega.basededatosconroomb.data.User;
import net.ivanvega.basededatosconroomb.data.UserDao;
import net.ivanvega.basededatosconroomb.provider.UsuarioProviderContract;

public class MainActivity extends AppCompatActivity {
    Button btnIn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIn = findViewById(R.id.btnInsert);

        btnIn.setOnClickListener(view -> {

            AppDataBase db =
                    AppDataBase.getDatabaseInstance(getApplication());

            UserDao dao = db.userDao();



            AppDataBase.databaseWriteExecutor.execute(() -> {
                User u = new User();

                //u.uid = 0;
                u.firstName = "Juan";
                u.lastName = "Peres";

                dao.insertAll( u );
                /*
                Toast.makeText(this,
                        "Insertado",
                        Toast.LENGTH_LONG).show();

                 */
                Log.d("DBUsuario", "Elemento insertado");
            });
        });

        findViewById(R.id.btnQuery).setOnClickListener(view -> {
            AppDataBase db = AppDataBase.getDatabaseInstance(getApplication());
            UserDao dao = db.userDao();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                AppDataBase.databaseWriteExecutor.execute(() -> {
                    dao.getAll().stream().forEach(user -> {
                        Log.i("Consulta User",
                                user.uid + " " + user .firstName+  " "+  user.lastName);

                    });
                });


            }else{
                AppDataBase.databaseWriteExecutor.execute(() -> {
                    for ( User user : dao.getAll()){
                        Log.d("DBUsuario", user.firstName+  " "+  user.lastName +  " "+  user.firstName);
                    }
                });
            }
        });

        findViewById(R.id.btnQueryApellido).setOnClickListener(v ->{
            AppDataBase db = AppDataBase.getDatabaseInstance(getApplication());
            UserDao dao = db.userDao();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                AppDataBase.databaseWriteExecutor.execute(() -> {
                    User u = dao.findByName("Peres");
                    if (u != null) {
                        Log.i("Consulta ",
                                u.uid + " " + u.firstName);
                    } else {
                        Log.d("Consulta", "Esta vacio");
                    }

                });
            }
        });


    }
}