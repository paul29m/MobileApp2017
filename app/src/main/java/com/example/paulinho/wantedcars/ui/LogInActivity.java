package com.example.paulinho.wantedcars.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.paulinho.wantedcars.R;
import com.example.paulinho.wantedcars.util.SQLiteHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;


/**
 * Created by paulinho on 1/7/2018.
 */

public class LogInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LogInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 420;
    public static SQLiteHelper sqLiteHelper;
    public static FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    GoogleSignInAccount acct;

    private SignInButton btnSignIn;
    private Button btnSignOut, btnList, btnAdd;
    private LinearLayout llProfileLayout;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initializeControls();
        initializeGPlusSettings();
    }

    private void initializeControls(){
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnList = (Button) findViewById(R.id.btn_list);
        btnAdd = (Button) findViewById(R.id.btn_addA);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        sqLiteHelper = new SQLiteHelper(this, "CARS.sqlite", null, 1);
        //sqLiteHelper.queryData("Drop Table CARS");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CARS(Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, year VARCHAR, description  VARCHAR, category VARCHAR, image BLOB)");
        btnList.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
    }

    private void initializeGPlusSettings(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            firebaseAuthWithGoogle();
            //Fetch values
            String personName = acct.getDisplayName();
            String personPhotoUrl =  acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String familyName = acct.getFamilyName();

            Log.e(TAG, "Name: " + personName +
                    ", email: " + email +
                    ", Image: " + personPhotoUrl +
                    ", Family Name: " + familyName);

            //Set values
            txtName.setText(personName);
            txtEmail.setText(email);

            //Set profile pic with the help of Glide
            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;
            case R.id.btn_sign_out:
                signOut();
                break;
            case  R.id.btn_list:
                goToList();
                break;
            case  R.id.btn_addA:
                goToAdd();
                break;
        }
    }

    private void firebaseAuthWithGoogle() {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(acct.getEmail(), acct.getId());
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Token: "+token);
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
//        mAuth.signInWithCredential(credential);

    }
    private void goToAdd() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private void goToList() {
        Intent intent = new Intent(LogInActivity.this, CarListActivity.class);
        intent.putExtra("SESSION_USER", acct.getEmail());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            if(txtEmail.getText().equals("m7paul29@gmail.com")){
                btnAdd.setVisibility(View.VISIBLE);
            }
            btnList.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
            btnList.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
        }
    }
}
