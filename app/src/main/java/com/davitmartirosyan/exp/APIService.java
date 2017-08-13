package com.davitmartirosyan.exp;

import com.davitmartirosyan.exp.pojo.UserDTO;
import com.davitmartirosyan.exp.pojo.UserResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {

    @POST("/getPostInfo")
    Call<UserDTO> sendPost (@Body UserDTO user);

    @GET("/addUser")
    Call<UserResponseDTO> getUser(@Query("name") String name, @Query("lastName") String lastName, @Query("email") String email);
}
