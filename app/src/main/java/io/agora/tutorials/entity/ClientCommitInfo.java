package io.agora.tutorials.entity;

/**
 * 提交用户信息
 */
public class ClientCommitInfo {
    //这条信息的ID
    private int id;
    //网名
    private String username;
    //手机号
    private String mobile;
    //车型
    private String type_name;
    //性别 1:男  2:女
    private int sex;
    //用户级别
    private String level;
    //是否愿意试驾 1:是  2:否
    private int by_car;
    //原因
    private String contents;
    //使用者的ID
    private int adviser_id;

    public int getAdviser_id() {
        return adviser_id;
    }

    public void setAdviser_id(int adviser_id) {
        this.adviser_id = adviser_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getBy_car() {
        return by_car;
    }

    public void setBy_car(int by_car) {
        this.by_car = by_car;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "ClientCommitInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", mobile='" + mobile + '\'' +
                ", type_name='" + type_name + '\'' +
                ", sex=" + sex +
                ", level='" + level + '\'' +
                ", by_car=" + by_car +
                ", contents='" + contents + '\'' +
                ", adviser_id=" + adviser_id +
                '}';
    }
}
