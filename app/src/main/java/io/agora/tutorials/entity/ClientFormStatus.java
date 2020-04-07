package io.agora.tutorials.entity;

/**
 * 获取用户填写form表单结果
 */
public class ClientFormStatus {

    /**
     * {
     * "status": "success",
     * "data": {
     * "id": "1",
     * "username": "明",
     * "mobile": "18851332604",
     * "type_name": "楼兰",
     * "sex": "0",
     * "level": null,
     * "by_car": "1",
     * "contents": ""
     * }
     * }
     */

    private String status;

    private ClientCommitInfo data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ClientCommitInfo getData() {
        return data;
    }

    public void setData(ClientCommitInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ClientFormStatus{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
