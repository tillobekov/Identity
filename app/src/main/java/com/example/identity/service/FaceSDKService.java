package com.example.identity.service;


import com.example.identity.model.FaceSDKResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface FaceSDKService {

    @Multipart
    @POST("process/face")
    Call<FaceSDKResponse> match(@Part MultipartBody.Part file, @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("process/faces")
    Call<FaceSDKResponse> match2faces(@Part MultipartBody.Part file1, @Part MultipartBody.Part file2);
}
