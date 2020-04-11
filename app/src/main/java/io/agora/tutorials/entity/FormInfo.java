package io.agora.tutorials.entity;

import java.io.Serializable;

public class FormInfo implements Serializable {
    /**
     * {
     * "id": "2",
     * "user_id": "157",
     * "username": "aaa",
     * "mobile": "18862924829",
     * "type_name": "鹅鹅鹅",
     * "time": "2020-04-09",
     * "admin_id": "6",
     * "sex": "0",
     * "level": "H",
     * "by_car": "1",
     * "contents": "",
     * "adviser_id": "23",
     * "createtime": "1586399838"
     * }
     */
    //每条新闻的ID
    private int id;
    //客户的id
    private int user_id;
    //客户昵称
    private String username;
    //手机号
    private String mobile;
    //车型
    private String type_name;
    //预约时间
    private String time;
    //
    private int admin_id;
    //性别
    private int sex;
    //级别
    private String level;
    //是否试驾
    private int bay_car;
    //原因
    private String contents;
    //提交者(用户)Id
    private int adviser_id;
    //创建类型
    private int createtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getBay_car() {
        return bay_car;
    }

    public void setBay_car(int bay_car) {
        this.bay_car = bay_car;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getAdviser_id() {
        return adviser_id;
    }

    public void setAdviser_id(int adviser_id) {
        this.adviser_id = adviser_id;
    }

    public int getCreatetime() {
        return createtime;
    }

    public void setCreatetime(int createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "FromInfo{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", username='" + username + '\'' +
                ", mobile='" + mobile + '\'' +
                ", type_name='" + type_name + '\'' +
                ", time='" + time + '\'' +
                ", admin_id=" + admin_id +
                ", sex=" + sex +
                ", level='" + level + '\'' +
                ", bay_car=" + bay_car +
                ", contents='" + contents + '\'' +
                ", adviser_id=" + adviser_id +
                ", createtime=" + createtime +
                '}';
    }
}
