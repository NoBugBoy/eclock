package com.yu.eclock.exception;

public class PersistenceStrategyException extends RuntimeException{
    private final String msg;
    public PersistenceStrategyException(String msg){
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
