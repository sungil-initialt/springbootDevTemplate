package com.sptek.webfw.support;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
