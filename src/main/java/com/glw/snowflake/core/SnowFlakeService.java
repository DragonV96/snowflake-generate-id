package com.glw.snowflake.core;

/**
 * @author : glw
 * @date : 2019/10/31
 * @time : 0:45
 * @Description : Twitter的分布式自增ID雪花算法接口(java实现)
 */
public interface SnowFlakeService {

    /**
     * 初始化数据中心与机器标识的序号
     * @param dataCenterId
     * @param machineId
     */
    void initDataCenterAndMachine(long dataCenterId, long machineId);

    /**
     * 生成下一个唯一id
     */
    long nextId();

    /**
     * 获取下一毫秒的时间戳
     */
    long getNextMill();

    /**
     * 获取下一时刻的系统时间戳
     */
    long getNextTimeStamp();
}
