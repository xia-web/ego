package com.ego.item.pojo;

import java.util.List;

// 返回的菜单项
public class CategoryNode {

    private String u;
    private String n;
    private List<Object> i;


    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public List<Object> getI() {
        return i;
    }

    public void setI(List<Object> i) {
        this.i = i;
    }
}
