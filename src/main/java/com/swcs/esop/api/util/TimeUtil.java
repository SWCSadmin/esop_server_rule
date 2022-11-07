package com.swcs.esop.api.util;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 阮程
 * @date 2022/10/27
 */
public class TimeUtil {

    private static Map<String, TimeUtil> instance = new ConcurrentHashMap<>(16);

    private String id;
    private Long startTime;
    private Long endTime;
    private Long cost;

    private String msg;

    private TimeUtil() {
    }

    private static TimeUtil getInstance(Object i) {
        String id;
        String threadId = Long.toString(Thread.currentThread().getId());
        if (Objects.nonNull(i)) {
            id = threadId + "-" + i.toString();
        } else {
            id = threadId;
        }
        if (instance.containsKey(id)) {
            return instance.get(id);
        } else {
            TimeUtil timeUtil = new TimeUtil();
            timeUtil.id = id;
            instance.put(id, timeUtil);
            return timeUtil;
        }
    }

    private static TimeUtil remove(TimeUtil timeUtil) {
        return instance.remove(timeUtil.id);
    }

    /**
     * 开始计时
     * <br/>
     * 创建一个 id = Thread.currentThread().getId() 的计时对象开始计时
     * <br/>
     * 如果在当前线程中存在多个计时操作, 请使用
     *
     * @see TimeUtil#start(java.lang.Object)
     */
    public static TimeUtil start() {
        return start(null);
    }

    /**
     * 结束计时
     * <br/>
     * 结束 id = Thread.currentThread().getId() 的计时对象的计时
     * <br/>
     * 如果在当前线程中存在多个计时操作, 请使用
     *
     * @see TimeUtil#end(java.lang.Object)
     */
    public static TimeUtil end() {
        return end(null);
    }

    /**
     * 获取耗时
     * <br/>
     * 获取 id = Thread.currentThread().getId() 的计时对象的耗时时间
     * <br/>
     * 如果在当前线程中存在多个计时操作, 请使用
     *
     * @return 耗时
     * @see TimeUtil#getCost(java.lang.Object)
     */
    public static Long getCost() {
        return getCost(null);
    }

    /**
     * 开始计时
     * <br/>
     * 创建一个 id = Thread.currentThread().getId() + "-" + i 的计时对象开始计时
     */
    public static TimeUtil start(Object i) {
        Long time = Instant.now().toEpochMilli();
        TimeUtil timeUtil = getInstance(i);
        timeUtil.startTime = time;
        return timeUtil;
    }

    /**
     * 结束计时
     * <br/>
     * 结束 id = Thread.currentThread().getId() + "-" + i 的计时对象的计时
     */
    public static TimeUtil end(Object i) {
        Long time = Instant.now().toEpochMilli();
        TimeUtil timeUtil = getInstance(i);
        timeUtil.endTime = time;
        timeUtil.cost = timeUtil.endTime - timeUtil.startTime;
        return timeUtil;
    }

    /**
     * 获取耗时
     * <br/>
     * 获取 id = Thread.currentThread().getId() + "-" + i 的计时对象的耗时
     *
     * @return 耗时
     */
    public static Long getCost(Object i) {
        TimeUtil timeUtil = getInstance(i);
        remove(timeUtil);
        return timeUtil.cost;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
