package com.abb.bye.client.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public class Paging<T> {
    private boolean previous;
    private boolean next;
    private int currentPage = 1;
    private int totalPage;
    private int totalData;
    private int pageSize = 10;
    private int maxShowPageNum = 10;
    private List<Integer> pages = new ArrayList<>();
    private List<T> data;

    public Paging<T> build() {
        if (totalData > 0) {
            totalPage = totalData / pageSize + (totalData % pageSize == 0 ? 0 : 1);
        }
        if (currentPage > 1) {
            previous = true;
        }
        if (currentPage < totalPage) {
            next = true;
        }
        int c = 0;
        for (int i = currentPage; i <= totalPage; i++) {
            if (++c > maxShowPageNum) {
                break;
            }
            if (i > totalPage) {
                break;
            }
            pages.add(i);
        }
        return this;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public Paging<T> setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public Paging<T> setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Paging<T> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public boolean isPrevious() {
        return previous;
    }

    public boolean isNext() {
        return next;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public Paging<T> setTotalData(int totalData) {
        this.totalData = totalData;
        return this;
    }

    public Paging<T> setPages(List<Integer> pages) {
        this.pages = pages;
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public Paging<T> setData(List<T> data) {
        this.data = data;
        return this;
    }
}
