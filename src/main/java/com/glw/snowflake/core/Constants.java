package com.glw.snowflake.core;

/**
 * @author : glw
 * @date : 2019/12/24
 * @time : 17:17
 * @Description : 常量接口
 */
public interface Constants {
    /**
     * 启用的当前时间戳
     */
    long START_STAMP = 1572454031L;

    /**
     * 每部分占用的位数（长度）
     * ※注：这四部分的位数可根据自身情况变动
     */
    long DATA_CENTER_BIT = 5;       // 数据中心的占用位数（长度）
    long MACHINE_BIT = 5;           // 机器标识的占用位数（长度）
    long SEQUENCE_BIT = 12;         // 序列号的占用位数（长度）

    /**
     * 每部分占用位数的最大值(通过移位与异或运算计算出最大值)
     */
    long MAX_DATA_CENTER = -1L ^ (-1L << DATA_CENTER_BIT);
    long MAX_MACHINE = -1L ^ (-1L << MACHINE_BIT);
    long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每部分向左的移位距离（序列号在最低位，即最右边；时间戳在最高位，即最左边）
     */
    long LEFT_MACHINE = SEQUENCE_BIT;                           // 机器标识需要左移到序列号左边（高位）
    long LEFT_DATA_CENTER = SEQUENCE_BIT + MACHINE_BIT;        // 数据中心需要左移到机器标识左边（高位）
    long LEFT_TIME_STAMP = LEFT_DATA_CENTER + DATA_CENTER_BIT;  // 时间戳需要左移到数据中心左边（高位）
}
