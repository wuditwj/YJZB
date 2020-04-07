package io.agora.tutorials.entity;

/**
 * 勿扰状态
 */
public class MuteInfo {

    /**
     * {
     * "status": "success",
     * "data": {
     * "rest": "1"
     * }
     * }
     */

    private String status;
    private data data;

    public class data {
        private String rest;

        public String getRest() {
            return rest;
        }

        public void setRest(String rest) {
            this.rest = rest;
        }

        @Override
        public String toString() {
            return "data{" +
                    "rest='" + rest + '\'' +
                    '}';
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MuteInfo.data getData() {
        return data;
    }

    public void setData(MuteInfo.data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MuteInfo{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
