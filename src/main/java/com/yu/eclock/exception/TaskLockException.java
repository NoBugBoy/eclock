package com.yu.eclock.exception;

public class TaskLockException extends RuntimeException{
    private final String msg;
    public TaskLockException(String msg){
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
