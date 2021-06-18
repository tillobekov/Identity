package com.example.identity.service;

import com.example.identity.model.VisionLabResponse;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface VisionLabsService {

    @Multipart
    @POST("6/liveness")
    Call<VisionLabResponse> liveness(@Part MultipartBody.Part file);


    @Multipart
    @POST("visionlab/liveness")
    Call<VisionLabResponse> liveness1(@Part MultipartBody.Part file);


}
