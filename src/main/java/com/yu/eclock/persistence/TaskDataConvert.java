package com.yu.eclock.persistence;

import com.yu.eclock.core.DefaultTask;

public interface TaskDataConvert<T> {
    default DataModel convert(){
        if(this instanceof DefaultTask){
            DefaultTask<T> defaultTask = (DefaultTask<T>)this;
            DataModel dataModel = new DataModel();
            dataModel.setTimestamp(System.currentTimeMillis());
            dataModel.setData(defaultTask.getTaskData());
            dataModel.setLoopTask(false);
            dataModel.setRetryCount(defaultTask.getRetryCount()==null? 0 : defaultTask.getRetryCount().intValue());
            dataModel.setRollback(defaultTask.isRollback());
            dataModel.setSlot(defaultTask.getSlot());
            dataModel.setRounds(defaultTask.getRounds());
            dataModel.setTaskId(defaultTask.getId());
            return dataModel;
        }else{
            return null;
        }
    }
}
