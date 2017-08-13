package com.davitmartirosyan.exp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.davitmartirosyan.exp.APIService;
import com.davitmartirosyan.exp.R;
import com.davitmartirosyan.exp.pojo.UserResponseDTO;
import com.davitmartirosyan.exp.util.Constant;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText lastName;
    private EditText email;
    private Button logIn;


    // GET
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.API.URL_GET)
            .client(new OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private APIService service = retrofit.create(APIService.class);


    // POST
//    private Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl(Constant.API.URL_POST)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();
//    private APIService service = retrofit.create(APIService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText) findViewById(R.id.activity_main_username_et);
        lastName = (EditText) findViewById(R.id.activity_main_lastname_et);
        email = (EditText) findViewById(R.id.activity_main_email_et);

        logIn = (Button) findViewById(R.id.activity_main_login_btn);
        logIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_login_btn:

//                UserDTO userDTO =  new UserDTO(userName.getText().toString(),Long.parseLong(passWord.getText().toString()),1,true);
//
//                service.sendPost(userDTO).enqueue(new Callback<UserDTO>() {
//                    @Override
//                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
//                        if (response.isSuccessful())
//                            Log.i("User", "post submitted to API." + response.body().toString());
//                        else try {
//                            throw new IllegalStateException(response.code() + " " + response.errorBody().string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Call<UserDTO> call, Throwable t) {
//                        Log.e("err", "Unable to submit post to API.");
//                    }
//                });


                // GET
                service.getUser(
                        userName.getText().toString(),
                        lastName.getText().toString(),
                        email.getText().toString()).enqueue(new Callback<UserResponseDTO>() {

                    @Override
                    public void onResponse(Call<UserResponseDTO> call, Response<UserResponseDTO> response) {
                        Log.d("User", Long.toString(response.body().getId()));
                    }

                    @Override
                    public void onFailure(Call<UserResponseDTO> call, Throwable t) {
                        Log.e("err", t.toString());
                    }
                });
                startActivity(new Intent(this, Activity2.class));
        }
    }
}
