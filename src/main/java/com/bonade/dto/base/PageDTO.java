package com.bonade.dto.base;


import java.io.Serializable;

public class PageDTO implements Serializable {
    private Integer page=1;//当前页
    private Integer rows=10;//每页数
    private Integer start=0; //数据开始

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getStart() {
        return this.page <= 1 ? 0 : (this.page - 1) * this.rows;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

}
