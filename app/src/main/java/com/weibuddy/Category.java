package com.weibuddy;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class Category {

    @Id
    private String id;
    @Index
    private String fid;
    @Index
    private String name;
    private int version;
    private int fresh;
    private int childCount;

    @Generated(hash = 813936374)
    public Category(String id, String fid, String name, int version, int fresh,
            int childCount) {
        this.id = id;
        this.fid = fid;
        this.name = name;
        this.version = version;
        this.fresh = fresh;
        this.childCount = childCount;
    }

    @Generated(hash = 1150634039)
    public Category() {
    }

    public String getFid() {
        return this.fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getFresh() {
        return this.fresh;
    }

    public void setFresh(int fresh) {
        this.fresh = fresh;
    }

    public int getChildCount() {
        return this.childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
