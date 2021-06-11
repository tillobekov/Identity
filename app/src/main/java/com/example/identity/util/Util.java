package com.example.identity.util;

public class Util {
    private static Integer counter = 0;
    private static String passportFilename = "pass_";
    private static String portraitFilename = "portait_";
    private static String passportFileExtention = ".png";

    public static String getNextPassportFilename(){
        counter++;
        return passportFilename + counter + passportFileExtention;
    }

    public static String getNextPortraitFilename(){
        counter++;
        return portraitFilename + counter + passportFileExtention;
    }

}
