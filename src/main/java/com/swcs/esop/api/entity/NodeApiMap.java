package com.swcs.esop.api.entity;

/**
 * @author 阮程
 * @date 2022/11/8
 */
public class NodeApiMap {

    public String apiGet;
    public String apiGetMultiple;
    public String apiAdd;
    public String apiAddMultiple;
    public String apiUpdate;
    public String apiUpdateMultiple;
    public String apiDelete;

    public NodeApiMap(String apiGet, String apiGetMultiple, String apiAdd, String apiAddMultiple, String apiUpdate, String apiUpdateMultiple, String apiDelete) {
        this.apiGet = apiGet;
        this.apiGetMultiple = apiGetMultiple;
        this.apiAdd = apiAdd;
        this.apiAddMultiple = apiAddMultiple;
        this.apiUpdate = apiUpdate;
        this.apiUpdateMultiple = apiUpdateMultiple;
        this.apiDelete = apiDelete;
    }
}
