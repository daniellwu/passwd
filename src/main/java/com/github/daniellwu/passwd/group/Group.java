package com.github.daniellwu.passwd.group;

import java.util.List;

/**
 * Defines a Group POJO that can serialized into JSON in the REST response
 */
public class Group {
    private String name;
    private long gid;
    private List<String> member;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public List<String> getMember() {
        return member;
    }

    public void setMember(List<String> member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", gid=" + gid +
                ", member=" + member +
                '}';
    }
}
