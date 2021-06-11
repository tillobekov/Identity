package com.example.identity.model;

import java.io.Serializable;

public class PassportData implements Serializable {
    private boolean valid;
    private String documentNumber;
    private String dateOfIssue;
    private String dateOfExpire;
    private String monthsToExpire;
    private String authority;
    private String givenName;
    private String surName;
    private String fathersName;
    private String issuingState;
    private String issuingStateCode;
    private String nationality;
    private String nationalityCode;
    private String age;
    private String sex;
    private String dateOfBirth;
    private String placeOfBirth;
    private String address;
    private String inn;
    private String pin;


    public PassportData(){}

    public PassportData(boolean valid, String documentNumber, String dateOfIssue, String dateOfExpire, String monthsToExpire, String authority, String givenName, String surName, String fathersName, String issuingState, String issuingStateCode, String nationality, String nationalityCode, String age, String sex, String dateOfBirth, String placeOfBirth, String address, String inn, String pin) {
        this.valid = valid;
        this.documentNumber = documentNumber;
        this.dateOfIssue = dateOfIssue;
        this.dateOfExpire = dateOfExpire;
        this.monthsToExpire = monthsToExpire;
        this.authority = authority;
        this.givenName = givenName;
        this.surName = surName;
        this.fathersName = fathersName;
        this.issuingState = issuingState;
        this.issuingStateCode = issuingStateCode;
        this.nationality = nationality;
        this.nationalityCode = nationalityCode;
        this.age = age;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.placeOfBirth = placeOfBirth;
        this.address = address;
        this.inn = inn;
        this.pin = pin;
    }

    public String getInn() {
        return inn;
    }

    public String getPin() {
        return pin;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isValid() {
        return valid;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public String getDateOfExpire() {
        return dateOfExpire;
    }

    public String getMonthsToExpire() {
        return monthsToExpire;
    }

    public String getAuthority() {
        return authority;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurName() {
        return surName;
    }

    public String getFathersName() {
        return fathersName;
    }

    public String getIssuingState() {
        return issuingState;
    }

    public String getIssuingStateCode() {
        return issuingStateCode;
    }

    public String getNationality() {
        return nationality;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public String getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public void setDateOfExpire(String dateOfExpire) {
        this.dateOfExpire = dateOfExpire;
    }

    public void setMonthsToExpire(String monthsToExpire) {
        this.monthsToExpire = monthsToExpire;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setFathersName(String fathersName) {
        this.fathersName = fathersName;
    }

    public void setIssuingState(String issuingState) {
        this.issuingState = issuingState;
    }

    public void setIssuingStateCode(String issuingStateCode) {
        this.issuingStateCode = issuingStateCode;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }


    @Override
    public String toString() {
        return "PassportData{" +
                "valid=" + valid +
                ", documentNumber='" + documentNumber + '\'' +
                ", dateOfIssue='" + dateOfIssue + '\'' +
                ", dateOfExpire='" + dateOfExpire + '\'' +
                ", monthsToExpire='" + monthsToExpire + '\'' +
                ", authority='" + authority + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surName='" + surName + '\'' +
                ", fathersName='" + fathersName + '\'' +
                ", issuingState='" + issuingState + '\'' +
                ", issuingStateCode='" + issuingStateCode + '\'' +
                ", nationality='" + nationality + '\'' +
                ", nationalityCode='" + nationalityCode + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", address='" + address + '\'' +
                ", inn='" + inn + '\'' +
                ", pin='" + pin + '\'' +
                '}';
    }
}
