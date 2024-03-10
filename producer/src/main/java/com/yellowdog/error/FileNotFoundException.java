package com.yellowdog.error;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String path) {
        super("File for path " + path +  " not found");
    }

}