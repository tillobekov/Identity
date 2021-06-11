package com.example.identity.model;

import java.io.Serializable;

public class DocumentReaderResponse implements Serializable {
    private PassportData passportData;
    private String filename;

    public DocumentReaderResponse(){}

    public DocumentReaderResponse(PassportData passportData) {
        this.passportData = passportData;
    }

    public DocumentReaderResponse(PassportData passportData, String filename) {
        this.passportData = passportData;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public PassportData getPassportData() {
        return passportData;
    }

    public void setFilename(String filename){
        this.filename = filename;
    }

    public void setPassportData(PassportData passportData) {
        this.passportData = passportData;
    }

    public void setValidity(boolean validity){
        passportData.setValid(validity);
    }

    @Override
    public String toString() {
        return "DocumentReaderResponse{" +
                "passportData=" + passportData +
                ", filename='" + filename + '\'' +
                '}';
    }
}
