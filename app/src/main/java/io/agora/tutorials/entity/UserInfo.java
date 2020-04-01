package io.agora.tutorials.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * 存储在本地数据库的用户信息
 */
@Entity
public class UserInfo {
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    private int id;
    //昵称
    private String nickname;
    //头像
    private String photo;
    //房间号
    private int house_id;
    //手机号
    private String mobile;
    //密码
    private String password;

    public UserInfo(String nickname, String photo, int house_id, String mobile, String password) {
        this.nickname = nickname;
        this.photo = photo;
        this.house_id = house_id;
        this.mobile = mobile;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getHouse_id() {
        return house_id;
    }

    public void setHouse_id(int house_id) {
        this.house_id = house_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
