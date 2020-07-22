package com.yu.eclock.enums;

public enum  PersistenceEnum {
    MONGO("mongo"),
    REDIS("redis"),
    MEMORY("memory");
    private String name;
    PersistenceEnum(String name){
        this.name = name;
    }
}
