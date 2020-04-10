package io.agora.tutorials.entity;

import java.util.Arrays;

/**
 * 查询提交的表单列表
 */
public class FormListInfo {

    /**
     * {
     * "status": "success",
     * "data": [
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
     * },
     * {
     * "id": "1",
     * "user_id": "168",
     * "username": "明",
     * "mobile": "18851332604",
     * "type_name": "",
     * "time": "",
     * "admin_id": "6",
     * "sex": "0",
     * "level": null,
     * "by_car": "1",
     * "contents": null,
     * "adviser_id": "23",
     * "createtime": "1586398506"
     * }
     * ],
     * "totalPages": 1,
     * "page": "1"
     * }
     */

    private String status;
    //表单数组
    private FormInfo[] data;
    //总页数
    private int totalPages;
    //当前页数
    private int page;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FormInfo[] getData() {
        return data;
    }

    public void setData(FormInfo[] data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "FormListInfo{" +
                "status='" + status + '\'' +
                ", data=" + Arrays.toString(data) +
                ", totalPages=" + totalPages +
                ", page=" + page +
                '}';
    }
}
