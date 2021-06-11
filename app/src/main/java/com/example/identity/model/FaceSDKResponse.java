package com.example.identity.model;

public class FaceSDKResponse {
    private boolean match;
    private double similarity;

    public FaceSDKResponse(){}

    public FaceSDKResponse(boolean match, double similarity) {
        this.match = match;
        this.similarity = similarity;
    }

    public boolean isMatch() {
        return match;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}
