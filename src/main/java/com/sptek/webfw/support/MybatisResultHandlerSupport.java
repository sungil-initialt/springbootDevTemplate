package com.sptek.webfw.support;

public abstract class MybatisResultHandlerSupport <T, R> {

    private boolean stopFlag = false;

    public void open(){
    }

    public void stop(){
        this.stopFlag = true;
    }

    public boolean isStop(){
        return stopFlag;
    }

    public void close(){
    }

    public abstract R handleResultRow(T resultRow);

}
