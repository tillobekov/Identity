package com.example.identity.service;

public class APIUtils {
    private APIUtils(){}

    public static final String API_URL = "http://195.158.30.142:8081/api/";
//    public static final String API_URL = "http://10.1.1.12:8081/api/";

    public static DocumentReaderService getDocumentReaderService(){
        return RetrofitClient.getClient(API_URL).create(DocumentReaderService.class);
    }

    public static FaceSDKService getFaceSDKService(){
        return RetrofitClient.getClient(API_URL).create(FaceSDKService.class);
    }

}
