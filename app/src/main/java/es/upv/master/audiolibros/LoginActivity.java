package es.upv.master.audiolibros;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import es.upv.master.audiolibros.singletons.FirebaseAuthSingleton;
import es.upv.master.audiolibros.singletons.FirebaseDBSingleton;

import static android.R.id.message;

/**
 * Created by padres on 06/02/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;
    //private LibrosSharedPreferenceStorage preferenceStorage;
    private UserStorage userStorage;
    private final String KEY_EMAIL = "password";

    private final int CONFIRM_EMAIL = 10;
    private final int ERROR = 20;
    private final int NILL = 0;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    private int messageType;


//this.findViewById(android.R.id.content).getRootView()
//this.findViewById(android.R.id.content)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setMessageType(NILL);
        auth = FirebaseAuthSingleton.getInstance().getAuth();
        doLogin();
    }

    private void doLogin() {
        final FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
          //  showMessage();
            guardarUsuario(currentUser);
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String provider = currentUser.getProviders().get(0);
            userStorage = (UserStorage) LibrosSharedPreferenceStorage.getInstance(this);
            userStorage.setName(name);
            userStorage.setProvider(provider);
            String actualProvider = currentUser.getProviders().get(0);
            Log.d("TRAZA", "Entro en doLogin");
            if (email != null) {
                userStorage.setEMail(email);
            }
            if (actualProvider.equals(KEY_EMAIL)) {
                Log.d("TRAZA", "Autenticación por E-MAil");
                if (currentUser.isEmailVerified()) {
                    Log.d("TRAZA", "Si ya tiene verificado el Mail, entro en aplicación");
                    gotoMain();
                } else {
                    Log.d("TRAZA", "No tiene verificado el Mail ..... muestro diálogo");
                    currentUser.sendEmailVerification().
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TRAZA", "email enviado");
                                        FirebaseAuth.getInstance().signOut();
                                        setMessageType(CONFIRM_EMAIL);
                                        Log.d("TRAZA", "Cambio messageType a " + getMessageType());
                                        resetLogin();
                                        finish();
                                    } else {
                                        // email not sent, so display message and restart the activity or do whatever you wish to do
                                        Log.d("TRAZA", "NO ENVIA email ");
                                        cancelaTodo();
                                    }
                                }
                            });
                }
            } else {
                gotoMain();
            }

        } else {
            showAutenticationOptions();
        }
    }

    private void showMessage(){
        Log.d("TRAZA", "Dentro showMessage, valor messageType = " + messageType);
        String msg = "";
        if (getMessageType() != NILL) {
            switch (getMessageType()) {
                case ERROR:
                    Log.d("TRAZA", "Dentro showMessage, rellnando valor de ERROR ");
                    msg = "Ha existido en Error en al autenticación, pruebe de nuevo";
                    break;
                case CONFIRM_EMAIL:
                    Log.d("TRAZA", "Dentro showMessage, rellnando valor de CONFIRM_EMAIL ");
                    msg = "Revise su correo, confirme y acceda";
                    break;
            }

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
            Log.d("TRAZA", "Dentro showMessage, tras Toast y SnackBAr ");
        }
    }

    private void cancelaTodo() {
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
        setMessageType(ERROR);
        Log.d("TRAZA", "Nuevo valor de messageType  " + getMessageType());
        resetLogin();
        //startActivity(getIntent());
    }


    void guardarUsuario(final FirebaseUser user) {
        DatabaseReference userReference = FirebaseDBSingleton.getInstance().getUsersReference();
        final DatabaseReference currentUserReference = userReference.child(user.getUid());
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    currentUserReference.setValue(new User(
                            user.getDisplayName(), user.getEmail()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        currentUserReference.addListenerForSingleValueEvent(userListener);
    }

    private void gotoMain() {
        Log.d("TRAZA", "Logado, entro");
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void resetLogin() {
        String msg;
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    //Según Usua, llama a lo que tengo en el else aqui mismo sin finish()
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == ResultCodes.OK) {
                Log.d("TRAZA", "ActivityResult, requestCode Correcto, vuelvo a entrar ya autenticado");
                doLogin();
                finish();
            } else {
                Log.d("TRAZA", "ActivityResult, requestCode InCorrecto");
                resetLogin();
            }
        }
    }

    private void showAutenticationOptions() {
        Log.d("TRAZA", "Muestro opciones  de autenticación ...");
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
                setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                .setIsSmartLockEnabled(false).build(), RC_SIGN_IN);
        showMessage();
    }

    private void cleanPreferences() {
        userStorage.remove("provider");
        userStorage.remove("email");
        userStorage.remove("name");
    }
}
