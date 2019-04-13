package com.ssm.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qp
 * @date 2019/4/12 16:36
 */
@Slf4j
public class HelloWorldJob implements Job {
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.info("quartz start!");
        log.info("Welcome to Spring_Quartz World!"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) );
        log.info("quartz end!\n");
    }
}
