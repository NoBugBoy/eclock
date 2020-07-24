package com.yu.eclock.exception;

public class PersistenceNameException extends RuntimeException{
    private final String msg;
    public PersistenceNameException(String msg){
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
