package com.example.identity.model;

import android.graphics.Bitmap;

import java.util.List;

public class VisionLabResponse {
    private List<VisionLabImage> images;

    private int error_code;
    private String desc;
    private String detail;

    public VisionLabResponse(){}

    public VisionLabResponse(List<VisionLabImage> images, int error_code, String desc, String detail) {
        this.images = images;
        this.error_code = error_code;
        this.desc = desc;
        this.detail = detail;
    }

    public List<VisionLabImage> getImages() {
        return images;
    }

    public int getError_code() {
        return error_code;
    }

    public String getDesc() {
        return desc;
    }

    public String getDetail() {
        return detail;
    }

    public void setImages(List<VisionLabImage> images) {
        this.images = images;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "VisionLabResponse{" +
                "images=" + images +
                ", error_code=" + error_code +
                ", desc='" + desc + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
