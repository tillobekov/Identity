package com.example.identity.service;


import com.example.identity.model.DocumentReaderResponse;
import com.example.identity.model.NIBBDRequest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DocumentReaderService {

    @Multipart
    @POST("process")
    Call<DocumentReaderResponse> process(@Part MultipartBody.Part file);

    @POST("process/nibbd")
    Call<DocumentReaderResponse> nibbd(@Body NIBBDRequest body);

}
