package com.yu.eclock.exception;

public class DataNullException extends RuntimeException{
    private final String msg;
    public DataNullException(String msg){
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
