package io.agora.tutorials.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * 存储在本地数据库的用户信息
 */
@Entity
public class UserInfo {
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    private int id;
    private int user_id;
    private int admin_id;
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

    public UserInfo(int user_id, int admin_id, String nickname, String photo, int house_id, String mobile, String password) {
        this.user_id = user_id;
        this.admin_id = admin_id;
        this.nickname = nickname;
        this.photo = photo;
        this.house_id = house_id;
        this.mobile = mobile;
        this.password = password;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return user_id == userInfo.getUser_id() && admin_id == getAdmin_id() && house_id == userInfo.getHouse_id() &&
                nickname.equals(userInfo.getNickname()) && photo.equals(userInfo.getPhoto()) && mobile.equals(userInfo.getMobile()) && password.equals(userInfo.getPassword());
    }

    public void setInfo(UserInfo info) {
        user_id = info.getUser_id();
        admin_id = info.getAdmin_id();
        house_id = info.getHouse_id();
        nickname = info.getNickname();
        photo = info.getPhoto();
        mobile = info.getMobile();
        password = info.getPassword();
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", admin_id=" + admin_id +
                ", nickname='" + nickname + '\'' +
                ", photo='" + photo + '\'' +
                ", house_id=" + house_id +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
