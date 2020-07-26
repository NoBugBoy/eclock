package com.yu.eclock.enums;

public enum  PersistenceStrategyEnum {
    DISCARD("discard"),
    INIT("init"),
    NOW("now"),
    DISCARD_ALL("discard_all");
    private String name;
    PersistenceStrategyEnum(String name){
        this.name = name;
    }
}
