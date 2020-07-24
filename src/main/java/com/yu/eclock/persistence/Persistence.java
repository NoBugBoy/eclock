package com.yu.eclock.persistence;

import java.util.List;

public interface Persistence {
    boolean add(DataModel model);
    List<DataModel> get();
    boolean remove(String key);
}
