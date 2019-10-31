package com.glw.snowflake.service.impl;

import com.glw.snowflake.service.SnowFlakeService;
import org.springframework.stereotype.Service;

/**
 * @author : glw
 * @date : 2019/10/31
 * @time : 0:49
 * @Description : Twitter的分布式自增ID雪花算法实现类(java实现)
 */
@Service
public class SnowFlakeServiceImpl implements SnowFlakeService {

    private long dataCenterId;      // 数据中心部分的id段
    private long machineId;         // 机器标识部分的id段
    private long sequenceId = 0L;   // 序列号部分的id段
    private long lastStamp = -1L;   // 上一时刻时间戳部分的id段

    @Override
    public void initDataCenterAndMachine(long dataCenterId, long machineId) {
        if (dataCenterId < 0
                || dataCenterId > MAX_DATA_CENTER
                || machineId < 0
                || machineId > MAX_MACHINE
        ) {
            throw new IllegalArgumentException();
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    @Override
    public synchronized long nextId() {
        long currentStamp = getNextTimeStamp();
        if (currentStamp < lastStamp) {
            throw new RuntimeException("The system time of current OS is wrong!Generate id when you fixed it!");
        }

        if (currentStamp == lastStamp) {
            // 同一毫秒内，序列号自增，自增到最大值时置为0继续自增
            sequenceId = (sequenceId + 1) & MAX_SEQUENCE;
            // 同一毫秒内的序列号已经达到最大
            if (sequenceId == 0L) {
                currentStamp = getNextMill();
            }
        } else {
            // 不同毫秒内，序号置为0
            sequenceId = 0;
        }
        lastStamp = currentStamp;
        return (currentStamp - START_STAMP) << LEFT_TIME_STAMP  // 时间戳左移到最高位
                | dataCenterId << LEFT_DATA_CENTER              // 数据中心左移
                | machineId << LEFT_MACHINE                     // 机器标识左移
                | sequenceId;                                   // 序列号
    }

    @Override
    public long getNextMill() {
        long nextMill = getNextTimeStamp();
        while (nextMill <= lastStamp) {
            nextMill = getNextTimeStamp();
        }
        return nextMill;
    }

    @Override
    public long getNextTimeStamp() {
        return System.currentTimeMillis();
    }
}
