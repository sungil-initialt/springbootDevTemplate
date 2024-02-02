package com.sptek.webfw.support;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/*
온픈 소스인 PageHelper Lib를 Wrap한 클레스임
게시판등에 활동할 수 있는 페이징 처리가 가능한 result 정보를 얻는데 활용
특별한 버그가 없는한 수정할 부분은 없다.
 */
@Slf4j
public class PageHelperSupport {
    public static void setPageForSelect(int currentPageNum, int setRowSizePerPage) {
        PageHelper.startPage(currentPageNum, setRowSizePerPage);
    }

    public static <T> PageInfoSupport<T> selectPaginatedList(List<? extends T> list, int setButtomPageNavigationSize) {
        PageInfo<T> pageInfo =  PageInfo.of(list, setButtomPageNavigationSize);
        PageInfoSupport<T> pageInfoSupport = new PageInfoSupport(pageInfo);

        return pageInfoSupport;
    }
}
