package com.weibuddy;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class Content implements Parcelable {

    @Id
    private String id;
    private String name;
    private String content;
    private String videoPic;

    @Index
    private String fid;
    @Index
    private String cname;

    @Transient
    private boolean isReady = false;

    @Generated(hash = 1153420023)
    public Content(String id, String name, String content, String videoPic,
                   String fid, String cname) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.videoPic = videoPic;
        this.fid = fid;
        this.cname = cname;
    }

    @Generated(hash = 940998559)
    public Content() {
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        name = in.readString();
        content = in.readString();
        videoPic = in.readString();
        fid = in.readString();
        cname = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(content);
        dest.writeString(videoPic);
        dest.writeString(fid);
        dest.writeString(cname);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            Content content = new Content();
            content.readFromParcel(in);
            return content;
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

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

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFid() {
        return this.fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getCname() {
        return this.cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getVideoPic() {
        return this.videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
