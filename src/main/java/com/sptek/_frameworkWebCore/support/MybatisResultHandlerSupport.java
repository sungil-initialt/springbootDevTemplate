package com.sptek._frameworkWebCore.support;

/*
MyBatisCommonDao 의 selectListWithResultHandler() 를 사용하기 위한 약속된 핸들러 추상클레스
 */
public abstract class MybatisResultHandlerSupport <T, R> {

    private boolean stopFlag = false;

    // overwrite somthing to do before the start
    public void open(){
    }

    //더이사 처리가 필요없을때 호출한다.
    public void stop() {
        this.stopFlag = true;
    }
    public boolean isStop() {
        return stopFlag;
    }

    // overwrite somthing to do for finishing
    public void close(){
    }

    // overwrite somthing to do for processing
    public abstract R handleResultRow(T resultRow);
}
