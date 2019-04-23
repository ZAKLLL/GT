package com.example.hp.gt.entity;

public class Note {

    public long id;
    public String source;
    public String target;
    public Integer ismark=0; //默认为0 表示未标记

    public Integer getIsmark() {
        return ismark;
    }

    public void setIsmark(Integer ismark) {
        this.ismark = ismark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Note(String source, String target,Integer ismark) {
        this.source = source;
        this.target = target;
        this.ismark = ismark;
    }
}