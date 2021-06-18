package com.example.identity.model;

public class VisionLabEstimation {
    private double probability;
    private double quality;

    public VisionLabEstimation(){}

    public VisionLabEstimation(double probability, double quality) {
        this.probability = probability;
        this.quality = quality;
    }

    public double getProbability() {
        return probability;
    }

    public double getQuality() {
        return quality;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    @Override
    public String toString() {
        return "VisionLabEstimation{" +
                "probability=" + probability +
                ", quality=" + quality +
                '}';
    }
}
