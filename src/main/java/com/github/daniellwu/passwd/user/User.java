package com.github.daniellwu.passwd.user;

/**
 * Defines a User POJO that can serialized into JSON in the REST response
 */
public class User {
    private String name;
    private long uid; // use long because some OS uses unsigned 32-bit integers
    private long gid; // use long because some OS uses unsigned 32-bit integers
    private String comment;
    private String home;
    private String shell;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getShell() {
        return shell;
    }

    public void setShell(String shell) {
        this.shell = shell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", uid=" + uid +
                ", gid=" + gid +
                ", comment='" + comment + '\'' +
                ", home='" + home + '\'' +
                ", shell='" + shell + '\'' +
                '}';
    }
}
