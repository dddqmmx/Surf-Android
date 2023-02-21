package com.dd.surf.pojo;

import java.io.Serializable;

public class Group implements Serializable {
    private Integer id;
    private String groupName;
    private Integer groupAvatar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(Integer groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", groupAvatar=" + groupAvatar +
                '}';
    }
}
