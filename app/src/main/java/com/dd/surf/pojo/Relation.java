package com.dd.surf.pojo;

import java.io.Serializable;

public class Relation implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer otherSideId;
    private Integer relationType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOtherSideId() {
        return otherSideId;
    }

    public void setOtherSideId(Integer otherSideId) {
        this.otherSideId = otherSideId;
    }

    public Integer getRelationType() {
        return relationType;
    }

    public void setRelationType(Integer relationType) {
        this.relationType = relationType;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "id=" + id +
                ", userId=" + userId +
                ", otherSideId=" + otherSideId +
                ", relationType=" + relationType +
                '}';
    }
}
