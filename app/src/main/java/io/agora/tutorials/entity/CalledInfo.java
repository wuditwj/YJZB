package io.agora.tutorials.entity;

/**
 * 呼叫
 */
public class CalledInfo {

    //{
    //   "status": "success",
    //       "data": "1"
    //}

    private String status;
    private int data;
    private int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CalledInfo{" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", uid=" + uid +
                '}';
    }
}
