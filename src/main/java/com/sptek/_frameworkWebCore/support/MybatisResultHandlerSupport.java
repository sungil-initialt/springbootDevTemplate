package com.sptek._frameworkWebCore.support;

/*
MyBatisCommonDao 의 selectListWithResultHandler() 를 사용하기 위한 약속된 핸들러 추상클레스
 */
public abstract class MybatisResultHandlerSupport <T, R> {

    private boolean stopFlag = false;

    //처리 시작전에 할일이 있다면 overwrite 한다.
    public void open(){
    }

    //더이사 처리가 필요없을때 호출한다.
    public void stop() {
        this.stopFlag = true;
    }

    public boolean isStop() {
        return stopFlag;
    }

    //처리를 끝내며 할일이 있다면 overwrite 한다.
    public void close(){
    }

    //result row를 받아 실체 처리할 부분을 구현한다.
    public abstract R handleResultRow(T resultRow);
}
