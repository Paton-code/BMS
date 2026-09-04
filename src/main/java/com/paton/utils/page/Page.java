package com.paton.utils.page;

import java.util.List;

/**
 * 分页功能的实现
 * @author yangxuechen
 * @Date 2018/10/30
 * @param <T>
 */
public class Page<T> {
    private List<T> list;        // T类型的对象链表
    private int pageNum;         // 当前页码
    private int pageSize;        // 每页数量
    private int totalCount;      // 总记录数（新添加的字段）
    private int pageCount;       // 总页数

    // Getter 和 Setter 方法
    public List<T> getList() {
        return list;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        // 可以在这里自动计算总页数
        if (pageSize > 0) {
            this.pageCount = (totalCount + pageSize - 1) / pageSize;
        }
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    // 可以添加一个便捷的计算总页数的方法
    public void calculatePageCount() {
        if (pageSize > 0 && totalCount >= 0) {
            this.pageCount = (totalCount + pageSize - 1) / pageSize;
        }
    }
}