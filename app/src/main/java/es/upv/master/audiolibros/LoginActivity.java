package es.upv.master.audiolibros;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by padres on 06/02/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;
    //private LibrosSharedPreferenceStorage preferenceStorage;
    private UserStorage userStorage;
    private final String KEY_EMAIL = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuthSingleton.getInstance().getAuth();
        doLogin();
    }

    private void doLogin() {
        final FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            guardarUsuario(currentUser);
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String provider = currentUser.getProviders().get(0);
            userStorage = (UserStorage) LibrosSharedPreferenceStorage.getInstance(this);
            userStorage.setName(name);
            userStorage.setProvider(provider);
            String actualProvider = currentUser.getProviders().get(0);
            if (email != null) {
                userStorage.setEMail(email);
            }
            if (actualProvider.equals(KEY_EMAIL)) {
                if (currentUser.isEmailVerified()) {
                    gotoMain();
                } else {
                    //TODO necesario hacerlo en 2º plano
                    final android.app.AlertDialog.Builder alertDialogAbout = new android.app.AlertDialog.Builder(this);
                    alertDialogAbout.setMessage(R.string.verifyEmailMsg)
                            .setTitle(R.string.verifyEmailTit)
                            .setPositiveButton(R.string.okBtn, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    currentUser.sendEmailVerification();
                                    showAutenticationOptions();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancelBtn, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cleanPreferences();
                                    showAutenticationOptions();
                                    finish();
                                    dialog.dismiss();

                                }
                            })
                            .show();
                }
            } else {
                gotoMain();
            }

        } else {
            showAutenticationOptions();
        }
    }

    void guardarUsuario(final FirebaseUser user) {
        DatabaseReference userReference = FirebaseDBSingleton.getInstance().getUsersReference();
        final DatabaseReference currentUserReference = userReference.child(user.getUid());
        ValueEventListener userListener = new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    currentUserReference.setValue(new User(
                            user.getDisplayName(), user.getEmail()));
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        };
        currentUserReference.addListenerForSingleValueEvent(userListener);
    }

    private void gotoMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == ResultCodes.OK) {
                doLogin();
                finish();
            } else {
                Intent i = new Intent(this, CustomLoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }
    }

    private void showAutenticationOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
                setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                .setIsSmartLockEnabled(false).build(), RC_SIGN_IN);
    }

    private void cleanPreferences() {
        userStorage.remove("provider");
        userStorage.remove("email");
        userStorage.remove("name");
    }
}
