package com.weibuddy;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Folder {

    @Id
    private String id;
    private String name;
    private int version;
    private int fresh;

    @Generated(hash = 706736789)
    public Folder(String id, String name, int version, int fresh) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.fresh = fresh;
    }

    @Generated(hash = 1947132626)
    public Folder() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

}
