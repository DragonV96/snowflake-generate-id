package com.glw.snowflake;

import com.glw.snowflake.core.SnowFlakeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class SnowflakeApplicationTests {

    private final static long DATA_CENTER_ID = 1L;
    private final static long MACHINE_ID = 2L;

    @Autowired
    private SnowFlakeService snowFlakeService;

    @Test
    void testSnowFlake() {
        snowFlakeService.initDataCenterAndMachine(DATA_CENTER_ID, MACHINE_ID);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            log.info("SnowFlake generate id = {}", snowFlakeService.nextId());
        }
        log.info("It takes {} milliseconds totally!", System.currentTimeMillis() - startTime);
    }
}
