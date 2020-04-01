package io.agora.tutorials.entity;

/**
 * 关闭通话
 */
public class CallStatus {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CloseCall{" +
                "status='" + status + '\'' +
                '}';
    }
}
