package io.agora.tutorials.entity;

/**
 * 客户信息
 */
public class ClientInfo {

    /**
     * {
     * "status": "success",
     * "data": {
     * "user_id": "11",
     * "admin_id": "0",
     * "mobile": "18851332604",
     * "open_id": "o7Q9X40qG7117lJsmhvsqy4BKyVY",
     * "token": "6f3d258719da1a4800d62de5dfe9891e",
     * "nickname": "明",
     * "headimgurl": "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKUB29SpDkCxUNx8LJfg2nUyicWn4b3wDU64aWOVAxd48QqTDMxUiaDeSzYC7CGyic4vdNiarIf99tiaFA/132",
     * "money": "0",
     * "create_time": "1579160645",
     * "create_ip": "180.120.160.235",
     * "last_time": null,
     * "closed": "0"
     * }
     * }
     */
    private String status;
    private data data;

    public static class data {
        //用户id
        private int user_id;
        private int admin_id;
        private String mobile;
        private String open_id;
        private String token;
        //用户昵称
        private String nickname;
        //头像地址
        private String headimgurl;
        private int money;
        private int create_time;
        private String create_ip;
        private int last_time;
        private int closed;


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

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public String getCreate_ip() {
            return create_ip;
        }

        public void setCreate_ip(String create_ip) {
            this.create_ip = create_ip;
        }

        public int getLast_time() {
            return last_time;
        }

        public void setLast_time(int last_time) {
            this.last_time = last_time;
        }

        public int getClosed() {
            return closed;
        }

        public void setClosed(int closed) {
            this.closed = closed;
        }

        @Override
        public String toString() {
            return "data{" +
                    "user_id=" + user_id +
                    ", admin_id=" + admin_id +
                    ", mobile='" + mobile + '\'' +
                    ", open_id='" + open_id + '\'' +
                    ", token='" + token + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", headimgurl='" + headimgurl + '\'' +
                    ", money=" + money +
                    ", create_time=" + create_time +
                    ", create_ip='" + create_ip + '\'' +
                    ", last_time=" + last_time +
                    ", closed=" + closed +
                    '}';
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ClientInfo.data getData() {
        return data;
    }

    public void setData(ClientInfo.data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "status='" + status + '\'' +
                data.toString()+
                '}';
    }
}
