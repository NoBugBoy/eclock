package com.yu.eclock.core;

public interface TimeWheelLoopTask {
    void setLoopCount(int loopCount);
    void setEnableLoop(boolean enableLoop);;
    int getLoopCount();
    boolean isInfiniteCycle();
    boolean isEnableLoop();
     int minusLoop();
}
