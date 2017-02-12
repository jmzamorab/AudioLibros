package es.upv.master.audiolibros;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import es.upv.master.audiolibros.singletons.FirebaseAuthSingleton;

/**
 * Created by padres on 12/02/2017.
 */

public class CustomLoginActivity extends Activity implements View.OnClickListener {
    private LinearLayout layoutSocialButtons;
    private LinearLayout layoutEmailButtons;
    private TextInputLayout wrapperPassword;
    private TextInputLayout wrapperEmail;
    private RelativeLayout container;
    private ProgressBar progressBar;
    private EditText inputPassword;
    private EditText inputEmail;
    private SignInButton btnGoogle;
    private LoginButton btnFacebook;
    private TwitterLoginButton btnTwitter;
    private FirebaseAuth auth;
    FirebaseUser currentUser;
    private UserStorage userStorage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_login);
        btnGoogle = (SignInButton) findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputEmail = (EditText) findViewById(R.id.editTxtEmail);
        inputPassword = (EditText) findViewById(R.id.editTxtPassword);
        wrapperEmail = (TextInputLayout) findViewById(R.id.wrapperEmail);
        wrapperPassword = (TextInputLayout) findViewById(R.id.wrapperPassword);
        container = (RelativeLayout) findViewById(R.id.loginContainer);
        layoutSocialButtons = (LinearLayout) findViewById(R.id.layoutSocial);
        layoutEmailButtons = (LinearLayout) findViewById(R.id.layoutEmailButtons);
        auth = FirebaseAuthSingleton.getInstance().getAuth();
        doLogin();
    }

    private void doLogin() {
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String provider = currentUser.getProviders().get(0);
            userStorage = (UserStorage) LibrosSharedPreferenceStorage.getInstance(this);
            userStorage.setProvider(provider);

            if (name == null) {
                name = email;
            }
            userStorage.setName(name);

            if (email != null) {
                userStorage.setEMail(email);
            }
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void showSnackbar(String message) {
        Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showProgress() {
        layoutSocialButtons.setVisibility(View.GONE);
        layoutEmailButtons.setVisibility(View.GONE);
        wrapperPassword.setVisibility(View.GONE);
        wrapperEmail.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        layoutSocialButtons.setVisibility(View.VISIBLE);
        layoutEmailButtons.setVisibility(View.VISIBLE);
        wrapperPassword.setVisibility(View.VISIBLE);
        wrapperEmail.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void signin(View v) {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            showProgress();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        doLogin();
                    } else {
                        hideProgress();
                        showSnackbar(task.getException().getLocalizedMessage());
                    }
                }
            });
        } else {
            wrapperEmail.setError(getString(R.string.error_empty));
        }
    }

    public void signup(View v) {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            showProgress();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        doLogin();
                    } else {
                        hideProgress();
                        showSnackbar(task.getException().getLocalizedMessage());
                    }
                }
            });
        } else {
            wrapperEmail.setError(getString(R.string.error_empty));
        }
    }
}