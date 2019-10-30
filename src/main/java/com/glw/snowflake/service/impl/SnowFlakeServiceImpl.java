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


    @Override
    public long nextId() {
        return 0;
    }

    @Override
    public long getNextMills() {
        return 0;
    }

    @Override
    public long getNextTimeStamp() {
        return 0;
    }
}
