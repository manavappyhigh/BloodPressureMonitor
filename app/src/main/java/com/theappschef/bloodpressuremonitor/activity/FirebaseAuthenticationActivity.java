package com.theappschef.bloodpressuremonitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.theappschef.bloodpressuremonitor.R;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class FirebaseAuthenticationActivity extends AppCompatActivity {


    private static final int MY_REQUEST_CODE = 123;
//    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        SharedPref sharedPref2 = new SharedPref(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_activity);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.google_sign_in).setOnClickListener(v -> {
            googleSignin();
        });
        EditText editText = findViewById(R.id.phone);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    findViewById(R.id.showOTP).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.showOTP).setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final String[] phone = new String[1];
        findViewById(R.id.showOTP).setOnClickListener(v -> {
            findViewById(R.id.Lsign).setVisibility(View.GONE);
            findViewById(R.id.Lotp).setVisibility(View.VISIBLE);
            phone[0] = editText.getText().toString();
            if (phone[0].length() == 10) {
                verifyNumber("+91" + phone[0], otp_being_retrived);
            }
            else {
                Toast.makeText(getApplicationContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.confirm).setOnClickListener(v -> {
            EditText editText1=findViewById(R.id.otp);
            PhoneAuthCredential cred = PhoneAuthProvider.getCredential(authVerificationID, editText1.getText().toString());
//            Log.d("otp",phone[0]);
            signInUsingCredentials(cred, "phone");
        });
        findViewById(R.id.confirmRe).setOnClickListener(v -> {
            verifyNumber("+91" + phone[0], otp_being_retrived);
        });
    }

//    private void showSignInOptions() {
//        startActivityForResult(
//                AuthUI.getInstance().createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .setLogo(R.drawable.img_logo)
//                        .setTheme(R.style.LoginTheme)
//                        .build(), MY_REQUEST_CODE
//        );
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 555) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                if(task.isSuccessful()) {
                    GoogleSignInAccount gAccount = task.getResult(ApiException.class);
                    AuthCredential mCred = GoogleAuthProvider.getCredential(gAccount.getIdToken(), null);
                    signInUsingCredentials(mCred, "gmail");
//                 Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
//                firebaseAuthWithGoogle(account.getIdToken());
                }
                else {
                    task.getException().printStackTrace();
                }
            } catch (ApiException e) {
                e.printStackTrace();
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private FirebaseAuth mAuth;

    private void signInUsingCredentials(AuthCredential credential, final String loginType) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                        startActivity(new Intent(FirebaseAuthenticationActivity.this, MainActivity.class));

                    finish();
                }
            } else {
                task.getException().printStackTrace();
                Toast.makeText(getApplicationContext(), "Error Signing in", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void googleSignin() {
        GoogleSignInOptions gsOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauth_client_id))
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        GoogleSignInClient mClient = GoogleSignIn.getClient(this, gsOptions);
        Intent mIntent = mClient.getSignInIntent();
        startActivityForResult(mIntent, 555);
    }


    Boolean otp_being_retrived = false;

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String authVerificationID;

    private void verifyNumber(String phone, final boolean isResend) {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        otp_being_retrived = true;
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                otp_being_retrived = false;
                signInUsingCredentials(phoneAuthCredential, "phone");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                otp_being_retrived = false;
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                if (!isResend) {
                } else {
                    Toast.makeText(getApplicationContext(), "Code Resent", Toast.LENGTH_SHORT).show();
                }
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                mResendToken = forceResendingToken;
                authVerificationID = s;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                otp_being_retrived = false;
            }
        };
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build();

        PhoneAuthOptions options2 = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .setForceResendingToken(mResendToken)
                .build();
        if (!isResend) {
            PhoneAuthProvider.verifyPhoneNumber(options);
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options2);
        }
    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.Lotp).getVisibility()==View.VISIBLE){
            findViewById(R.id.Lsign).setVisibility(View.VISIBLE);
            findViewById(R.id.Lotp).setVisibility(View.GONE);
        }
        else {
        super.onBackPressed();
    }
    }
}
