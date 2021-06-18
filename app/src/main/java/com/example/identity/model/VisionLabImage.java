package com.example.identity.model;

public class VisionLabImage {

    private String filename;
    private int status;
    private VisionLabLiveness liveness;
    private VisionLabError error;

    public VisionLabImage(){}

    public VisionLabImage(String filename, int status, VisionLabLiveness liveness, VisionLabError error) {
        this.filename = filename;
        this.status = status;
        this.liveness = liveness;
        this.error = error;
    }

    public VisionLabError getError() {
        return error;
    }

    public String getFilename() {
        return filename;
    }

    public int getStatus() {
        return status;
    }

    public VisionLabLiveness getLiveness() {
        return liveness;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setError(VisionLabError error) {
        this.error = error;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setLiveness(VisionLabLiveness liveness) {
        this.liveness = liveness;
    }

    @Override
    public String toString() {
        return "VisionLabImage{" +
                "filename='" + filename + '\'' +
                ", status=" + status +
                ", liveness=" + liveness +
                ", error=" + error +
                '}';
    }
}
