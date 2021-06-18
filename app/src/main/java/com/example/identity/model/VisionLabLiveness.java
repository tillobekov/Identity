package com.example.identity.model;

public class VisionLabLiveness {
    private int prediction;
    private VisionLabEstimation estimations;

    public VisionLabLiveness(){}

    public VisionLabLiveness(int prediction, VisionLabEstimation estimations) {
        this.prediction = prediction;
        this.estimations = estimations;
    }

    public int getPrediction() {
        return prediction;
    }

    public VisionLabEstimation getEstimations() {
        return estimations;
    }

    public void setPrediction(int prediction) {
        this.prediction = prediction;
    }

    public void setEstimations(VisionLabEstimation estimations) {
        this.estimations = estimations;
    }

    @Override
    public String toString() {
        return "VisionLabLiveness{" +
                "prediction=" + prediction +
                ", estimations=" + estimations +
                '}';
    }
}
