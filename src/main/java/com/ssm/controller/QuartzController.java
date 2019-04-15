package com.ssm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ssm.model.JobEntity;
import com.ssm.service.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 定时任务Controller
 */
@Controller
@RequestMapping("/quartz")
@Slf4j
public class QuartzController {

	@Autowired
	private Scheduler quartzScheduler;
	
	@Autowired
	private QuartzService quartzService;

	/**
	 * 定时列表页
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	@RequestMapping(value="/listJob")
	public String listJob(HttpServletRequest request, HttpServletResponse response) throws SchedulerException {
		List<JobEntity> jobInfos = getSchedulerJobInfo();
		request.setAttribute("jobInfos", jobInfos);
		return "quartz/listjob";
	}

	/**
	 * 查询job信息
	 * @return
	 * @throws SchedulerException
	 */
	private List<JobEntity> getSchedulerJobInfo() throws SchedulerException {
		List<JobEntity> jobInfos = new ArrayList<JobEntity>();

		List<String> triggerGroupNames = quartzScheduler.getTriggerGroupNames();
		for (String triggerGroupName : triggerGroupNames) {
			Set<TriggerKey> triggerKeySet = quartzScheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroupName));

			for (TriggerKey triggerKey : triggerKeySet) {
				Trigger t = quartzScheduler.getTrigger(triggerKey);
				if (t instanceof CronTrigger) {
					CronTrigger trigger = (CronTrigger) t;
					JobKey jobKey = trigger.getJobKey();
					JobDetail jd = quartzScheduler.getJobDetail(jobKey);
					JobEntity jobInfo = new JobEntity();
					jobInfo.setJobName(jobKey.getName());
					jobInfo.setJobGroup(jobKey.getGroup());
					jobInfo.setTriggerName(triggerKey.getName());
					jobInfo.setTriggerGroupName(triggerKey.getGroup());
					jobInfo.setCronExpr(trigger.getCronExpression());
					jobInfo.setNextFireTime(trigger.getNextFireTime());
					jobInfo.setPreviousFireTime(trigger.getPreviousFireTime());
					jobInfo.setStartTime(trigger.getStartTime());
					jobInfo.setEndTime(trigger.getEndTime());
					jobInfo.setJobClass(jd.getJobClass().getCanonicalName());
					// jobInfo.setDuration(Long.parseLong(jd.getDescription()));
					Trigger.TriggerState triggerState = quartzScheduler.getTriggerState(trigger.getKey());
					// NONE无,
					// NORMAL正常,
					// PAUSED暂停,
					// COMPLETE完全,
					// ERROR错误,
					// BLOCKED阻塞
					jobInfo.setJobStatus(triggerState.toString());

					JobDataMap map = quartzScheduler.getJobDetail(jobKey).getJobDataMap();
					if (null != map && map.size() != 0) {
						jobInfo.setCount(Integer.parseInt((String) map.get("count")));
						jobInfo.setJobDataMap(map);
					} else {
						jobInfo.setJobDataMap(new JobDataMap());
					}
					jobInfos.add(jobInfo);
				}
			}
		}
		return jobInfos;
	}
	
	/**
	 * 跳转到新增页面
	 * 
	 * @return
	 * @throws SchedulerException
	 * @throws ClassNotFoundException 
	 */
	@RequestMapping(value = "/toAdd")
	public String toAdd() {
		return "quartz/addjob";
	}

    /**
     * 新增job
     *
     * @return
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    @RequestMapping(value="/add",method= RequestMethod.POST)
    @ResponseBody
    public String add(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jobName = request.getParameter("jobName");
        String jobGroupName = request.getParameter("jobGroupName");
        String triggerName = request.getParameter("triggerName");
        String triggerGroupName = request.getParameter("triggerGroupName");
        String clazz = request.getParameter("clazz");
        Class cls = Class.forName(clazz);
        String cron = request.getParameter("cron");

		JSONObject json = new JSONObject();
        try {
            quartzService.addJob(jobName, jobGroupName, triggerName, triggerGroupName, cls, cron);
			json.put("status", "success");
        }catch (Exception e) {
			json.put("status", "error");
			log.error("add job error: {}", e);
        }

		return json.toJSONString();
    }


	/**
	 * 跳转到编辑
	 *
	 * @return
	 * @throws SchedulerException
	 * @throws ClassNotFoundException
	 */
	@RequestMapping(value="/toEdit")
	public String toEdit(HttpServletRequest request, HttpServletResponse response) throws SchedulerException {
		String jobName = request.getParameter("jobName");
		String jobGroup = request.getParameter("jobGroup");

		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		JobDetail jd = quartzScheduler.getJobDetail(jobKey);
		@SuppressWarnings("unchecked")
		List<CronTrigger> triggers = (List<CronTrigger>) quartzScheduler.getTriggersOfJob(jobKey);
		CronTrigger trigger = triggers.get(0);
		TriggerKey triggerKey = trigger.getKey();
		String cron = trigger.getCronExpression();

		Map<String, String> pd = new HashMap<String, String>();
		pd.put("jobName", jobKey.getName());
		pd.put("jobGroup", jobKey.getGroup());
		pd.put("triggerName", triggerKey.getName());
		pd.put("triggerGroupName", triggerKey.getGroup());
		pd.put("cron", cron);
		pd.put("clazz", jd.getJobClass().getCanonicalName());

		request.setAttribute("pd", pd);
		request.setAttribute("msg", "edit");

		return "quartz/editjob";
	}

	/**
	 * 编辑job
	 * 
	 * @return
	 * @throws SchedulerException
	 * @throws ClassNotFoundException 
	 */
	@RequestMapping(value="/edit",method= RequestMethod.POST)
	@ResponseBody
	public String edit(HttpServletRequest request, HttpServletResponse response) throws SchedulerException, ClassNotFoundException {
		String jobName = request.getParameter("jobName");
		String jobGroupName = request.getParameter("jobGroupName");
		String triggerName = request.getParameter("triggerName");
		String triggerGroupName = request.getParameter("triggerGroupName");
		String clazz = request.getParameter("clazz");
		Class cls = Class.forName(clazz);
		String cron = request.getParameter("cron");
		
		String oldjobName = request.getParameter("oldjobName");
		String oldjobGroup = request.getParameter("oldjobGroup");
		String oldtriggerName = request.getParameter("oldtriggerName");
		String oldtriggerGroup = request.getParameter("oldtriggerGroup");
		
		boolean result = quartzService.modifyJobTime(oldjobName, oldjobGroup, oldtriggerName, oldtriggerGroup,
				jobName, jobGroupName, triggerName, triggerGroupName, cron);

		JSONObject json = new JSONObject();
		if (result) {
			json.put("status", "success");
		} else {
			json.put("status", "error");
		}

		return json.toJSONString();
	}

	/**
	 * 暂停job
	 * @param jobName
	 * @param jobGroupName
	 * @return
	 */
	@RequestMapping(value="/pauseJob",method= RequestMethod.POST)
	@ResponseBody
	public String pauseJob(@RequestParam("jobName") String jobName, @RequestParam("jobGroupName") String jobGroupName){
		JSONObject json = new JSONObject();
		
		if(StringUtils.isEmpty(jobName) || StringUtils.isEmpty(jobGroupName)){
			json.put("status", "error");
		}else{
			quartzService.pauseJob(jobName, jobGroupName);
			json.put("status", "success");
		}
		
		return json.toJSONString();
	}

	/**
	 * 恢复job
	 * @param jobName
	 * @param jobGroupName
	 * @return
	 */
	@RequestMapping(value="/resumeJob",method= RequestMethod.POST)
	@ResponseBody
	public String resumeJob(@RequestParam("jobName") String jobName, @RequestParam("jobGroupName") String jobGroupName){
		JSONObject json = new JSONObject();
		
		if(StringUtils.isEmpty(jobName) || StringUtils.isEmpty(jobGroupName)){
			json.put("status", "error");
		}else{
			quartzService.resumeJob(jobName, jobGroupName);
			json.put("status", "success");
		}
		
		return json.toJSONString();
	}

	/**
	 * 删除job
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @return
	 */
	@RequestMapping(value="/deleteJob",method= RequestMethod.POST)
	@ResponseBody
	public String deleteJob(@RequestParam("jobName") String jobName, @RequestParam("jobGroupName") String jobGroupName,
                            @RequestParam("triggerName") String triggerName, @RequestParam("triggerGroupName") String triggerGroupName ){
		JSONObject json = new JSONObject();
		
		if(StringUtils.isEmpty(jobName) || StringUtils.isEmpty(jobGroupName) ||
				StringUtils.isEmpty(triggerName) || StringUtils.isEmpty(triggerGroupName) ){
			json.put("status", "error");
		}else{
			 quartzService.removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
			 json.put("status", "success");
		}
		
		return json.toJSONString();
	}

}
