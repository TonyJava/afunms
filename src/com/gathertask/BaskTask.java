package com.gathertask;

/**
 * 该程序是用来实现按时间执行各种承载点或拨测点协议
 */
import java.lang.reflect.Method;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.afunms.indicators.model.NodeGatherIndicators;

public class BaskTask extends TimerTask {

	Logger logger = Logger.getLogger(BaskTask.class);
	private String runclasspath = "";
	private String runtime = "";
	private String runtype = "";
	private String taskid = "";
	private String taskname = "";
	private String nodeid = "";
	private NodeGatherIndicators gather = new NodeGatherIndicators();

	/**
	 * 
	 * 任务主线程 采集任务是用方法
	 */
	public void run() {

		Object runner;
		Method mt;
		try {

			runner = Class.forName(runclasspath.trim()).newInstance();
			mt = runner.getClass().getMethod("collect_Data", NodeGatherIndicators.class);
			mt.invoke(runner, gather);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getRunclasspath() {
		return runclasspath;
	}

	public void setRunclasspath(String runclasspath) {
		this.runclasspath = runclasspath;
	}

	public String getRuntime() {
		return runtime;
	}

	public String getruntype() {
		return runtype;
	}

	public void setruntype(String runtype) {
		this.runtype = runtype;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getTaskname() {
		return taskname;
	}

	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public NodeGatherIndicators getGather() {
		return gather;
	}

	public void setGather(NodeGatherIndicators gather) {
		this.gather = gather;
	}

}
