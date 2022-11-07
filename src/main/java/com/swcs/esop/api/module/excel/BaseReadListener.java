package com.swcs.esop.api.module.excel;

import com.alibaba.excel.read.listener.ReadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量新增
 *
 * @author 阮程
 * @date 2022/11/1
 */
public abstract class BaseReadListener<T> implements ReadListener<T> {
    protected List<T> cachedDataList = new ArrayList<>();
    protected List<T> insertList = new ArrayList<>();
    protected List<T> updateList = new ArrayList<>();
    /**
     * 是否覆盖
     */
    protected boolean upsert;
    protected int errorNum = 0;

    public BaseReadListener(boolean upsert) {
        this.upsert = upsert;
    }

    public List<T> getCachedDataList() {
        return cachedDataList;
    }

    public boolean isSuccess() {
        return errorNum == 0;
    }
}
