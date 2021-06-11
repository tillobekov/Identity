package com.example.identity.model;

public class NIBBDRequest {
    String docNumber;
    String dateOfBirth;

    public NIBBDRequest(){}

    public NIBBDRequest(String docNumber, String dateOfBirth) {
        this.docNumber = docNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
