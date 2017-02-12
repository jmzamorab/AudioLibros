package es.upv.master.audiolibros;

import android.app.Activity;
import android.app.Fragment;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import es.upv.master.audiolibros.singletons.FirebaseAuthSingleton;
import android.support.v4.app.FragmentActivity;
/**
 * Created by padres on 12/02/2017.
 */

public class CustomLoginActivity extends FragmentActivity
        implements View.OnClickListener , GoogleApiClient.OnConnectionFailedListener{
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
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private GoogleApiClient googleApiClient;


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

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).
                requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).
                enableAutoManage(this, this).
                addApi(Auth.GOOGLE_SIGN_IN_API, gso).
                build();
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
            case R.id.btnGoogle:
                googleLogin();
                break;
        }
    }

    public void googleLogin() {
        showProgress();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                googleAuth(account);
            } else {
                hideProgress();
                showSnackbar(getResources().getString(R.string.error_google));
            }
        }
    }

    private void googleAuth(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    hideProgress();
                    showSnackbar(task.getException().getLocalizedMessage());
                } else {
                    doLogin();
                }
            }
        });
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showSnackbar(getString(R.string.error_connection_failed));
    }
}