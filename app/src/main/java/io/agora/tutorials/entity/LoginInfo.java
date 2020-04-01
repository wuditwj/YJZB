package io.agora.tutorials.entity;

/**
 * 登录后返回用户信息
 */
public class LoginInfo {

//    {
//        "status": "success",
//        "data": {
//                "id": "1",
//                "admin_id": "1",
//                "nickname": "郭仕友",
//                "photo": "/Uploads/2019-08-26/5d637bdd591d0.jpg",
//                "house_id": "1",
//                "mobile": "18851332601",
//                "password": "e10adc3949ba59abbe56e057f20f883e",
//                "status": "0"
//                 }
//    }
    //请求状态
    private String status;
    private data data;

    public class data {
        private int id;
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
        //通话状态
        private String status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAdmin_id() {
            return admin_id;
        }

        public void setAdmin_id(int admin_id) {
            this.admin_id = admin_id;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "data{" +
                    "id=" + id +
                    ", admin_id=" + admin_id +
                    ", nickname='" + nickname + '\'' +
                    ", photo='" + photo + '\'' +
                    ", house_id=" + house_id +
                    ", mobile='" + mobile + '\'' +
                    ", password='" + password + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LoginInfo.data getData() {
        return data;
    }

    public void setData(LoginInfo.data data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "ClientInfo{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
