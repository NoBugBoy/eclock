package com.yu.eclock.exception;

public class PersistenceInstanceException extends RuntimeException{
    private final String msg;
    public PersistenceInstanceException(String msg){
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
