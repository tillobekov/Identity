package com.example.identity.model;

public class VisionLabError {

    private int error_code;
    private String desc;
    private String detail;

    public VisionLabError(int error_code, String desc, String detail) {
        this.error_code = error_code;
        this.desc = desc;
        this.detail = detail;
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

    @Override
    public String toString() {
        return "VisionLabError{" +
                "error_code=" + error_code +
                ", desc='" + desc + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
