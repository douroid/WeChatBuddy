package com.weibuddy.util.http;

public class RequestException extends Exception {

    private int responseCode;

    public RequestException(int responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public int responseCode() {
        return responseCode;
    }
}
