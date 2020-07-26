package com.yu.eclock.exception;

public class FixTaskException extends RuntimeException{
    private final String msg;
    public FixTaskException(String msg){
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
