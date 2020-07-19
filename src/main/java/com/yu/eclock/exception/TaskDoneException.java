package com.yu.eclock.exception;

public class TaskDoneException extends RuntimeException{
    private final String msg;
    public TaskDoneException(String msg){
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
