package com.yu.eclock.persistence;

public interface EclockPersistenceFactory {
     boolean init();
     boolean add();
     boolean remove();
}
