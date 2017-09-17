package com.davitmartirosyan.exp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.davitmartirosyan.exp.APIService;
import com.davitmartirosyan.exp.R;
import com.davitmartirosyan.exp.util.Constant;
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

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";

    private SignInButton signInButton;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog progressDialog;

    // GET
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.API.URL_GET)
            .client(new OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private APIService service = retrofit.create(APIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.d("testt", FirebaseInstanceId.getInstance().getToken());
        //Log.d("testt", FirebaseApp.getInstance().getOptions().getStorageBucket());

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this,"onConnectionFailed:" + connectionResult,Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sign_in_button:
                signIn();
                break;

                // POST
                /* UserDTO userDTO =  new UserDTO(userName.getText().toString(),Long.parseLong(passWord.getText().toString()),1,true);

                service.sendPost(userDTO).enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.isSuccessful())
                            Log.i("User", "post submitted to API." + response.body().toString());
                        else try {
                            throw new IllegalStateException(response.code() + " " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        Log.e("err", "Unable to submit post to API.");
                    }
                }); */



                // GET
//                service.getUser(
//                        userName.getText().toString(),
//                        lastName.getText().toString(),
//                        email.getText().toString()).enqueue(new Callback<UserResponseDTO>() {
//
//                    @Override
//                    public void onResponse(Call<UserResponseDTO> call, Response<UserResponseDTO> response) {
//                        Log.d("User", Long.toString(response.body().getId()));
//                        Preference.getInstance(MainActivity.this).setUserID(response.body().getId());
//                    }
//
//                    @Override
//                    public void onFailure(Call<UserResponseDTO> call, Throwable t) {
//                        Log.e("err", t.toString());
//                    }
//                });
//                startActivity(new Intent(this, Activity2.class));
//                break;
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d("testt: mail",account.getEmail());
                Log.d("testt: name", account.getGivenName());
                Log.d("testt: surname",account.getFamilyName());
                Log.d("testt: id",account.getId());

                // TODO - GET request

                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(MainActivity.this,"Google Sign In failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        progressDialog.dismiss();
        if(user!=null) {
            Intent intent = new Intent(MainActivity.this,Activity2.class);
            startActivity(intent);
        }
    }

}
