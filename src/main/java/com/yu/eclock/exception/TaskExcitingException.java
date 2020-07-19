package com.yu.eclock.exception;

public class TaskExcitingException extends RuntimeException{
    private final String msg;
    public TaskExcitingException(String msg){
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
