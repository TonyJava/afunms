package com.gathertask;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;

import com.afunms.indicators.model.NodeGatherIndicators;
import com.gatherdb.GatherDataAlarmsqlRun;
import com.gatherdb.GatherDatatempsqlRun;
import com.gatherdb.GathersqlRun;
import com.gatherdb.nmsmemorydate;
import com.gathertask.dao.Taskdao;

@SuppressWarnings("unchecked")
public class TaskManager {

	/**
	 * 建立所有的采集任务
	 * 
	 * @param taskinterval
	 *            任务的采集频率
	 * @param taskid
	 *            任务的id
	 * @param taskname
	 *            任务的名称
	 * @param tasktype
	 *            任务的类型
	 * @param tasksubtype
	 */
	public void createOneTask(NodeGatherIndicators nodeGatherIndicators) {
		Timer timer = null;
		BaskTask btask = null;
		if (null != nmsmemorydate.TaskList && nmsmemorydate.TaskList.size() > 0 && nmsmemorydate.TaskList.containsKey(nodeGatherIndicators.getId())) {
			// 停止原来的timer，列表并且从内存中删除对应的对象
			timer = (Timer) nmsmemorydate.TaskList.get(nodeGatherIndicators.getId());
			timer.cancel();
			nmsmemorydate.TaskList.remove(nodeGatherIndicators.getId());
			nmsmemorydate.RunGatherLinst.remove(nodeGatherIndicators.getId());
		} else {
			// 建立定时采集任务
			timer = new Timer();
			btask = new BaskTask();
			btask.setRunclasspath((String) nodeGatherIndicators.getClasspath());// 运行类得路径
			btask.setTaskid(nodeGatherIndicators.getId() + "");
			btask.setNodeid((String) nodeGatherIndicators.getNodeid());
			btask.setTaskname(nodeGatherIndicators.getName());
			btask.setRunclasspath(nodeGatherIndicators.getClasspath());
			btask.setGather(nodeGatherIndicators);

			long intervaltime = Integer.parseInt(nodeGatherIndicators.getPoll_interval());
			if (nodeGatherIndicators.getInterval_unit().equals("s")) {
				intervaltime = intervaltime * 1000;
			}
			if (nodeGatherIndicators.getInterval_unit().equals("m")) {
				intervaltime = intervaltime * 1000 * 60;
			}
			if (nodeGatherIndicators.getInterval_unit().equals("h")) {
				intervaltime = intervaltime * 1000 * 60 * 60;
			}
			if (nodeGatherIndicators.getInterval_unit().equals("d")) {
				intervaltime = intervaltime * 1000 * 60 * 60 * 24;
			}

			long in = 0;
			if (nmsmemorydate.TaskList.size() > 300) {
				in = (nmsmemorydate.TaskList.size() / 5) * 200;

			} else {
				in = nmsmemorydate.TaskList.size() * 200;
			}

			timer.schedule(btask, in, intervaltime);// 按分钟执行定时任务
			nmsmemorydate.TaskList.put(nodeGatherIndicators.getId() + "", timer);// 把TIMER对象到任务队里
			nmsmemorydate.RunGatherLinst.put(nodeGatherIndicators.getId() + "", nodeGatherIndicators);
		}

	}

	/**
	 * 
	 * 建立一个维护进程 5分钟定时检查一次timer是否需要运行，或是定时时间已经改变
	 * 
	 */
	public void CreateMaintainTask() {
		if (!nmsmemorydate.MaintainTaskStatus) {
			Timer timer = null;
			MaintainTask btask = null;
			timer = new Timer();
			btask = new MaintainTask();
			timer.schedule(btask, 1000, 1 * 1000 * 60);// 按分钟执行定时任务
			nmsmemorydate.MaintainTaskStatus = true;// 设置标记为启动
			nmsmemorydate.MaintainTasktimer = timer;
		}

	}

	/**
	 * 
	 * 数据分离模式入库定时任务 1分钟入库一次
	 * 
	 */
	public void CreateDataTempTask() {
		Timer timer = null;
		GatherDatatempsqlRun btask = null;
		timer = new Timer();
		btask = new GatherDatatempsqlRun();
		timer.schedule(btask, 20000, 10 * 1000);// 按分钟执行定时任务
		nmsmemorydate.GatherDatatempsqlTasktimer = timer;
	}

	/**
	 * 
	 * 垃圾回收
	 * 
	 */
	public void CreateGCTask() {
		Timer timer = null;
		GcTask btask = null;
		timer = new Timer();
		btask = new GcTask();
		timer.schedule(btask, 20000, 5 * 1000 * 60);// 按分钟执行定时任务
	}

	/**
	 * 
	 * 建立一个维护进程 5分钟定时检查一次timer是否需要运行，或是定时时间已经改变
	 * 
	 */
	public void CreateGahterSQLTask() {
		if (!nmsmemorydate.GathersqlTaskStatus) {
			Timer timer = null;
			GathersqlRun btask = null;
			timer = new Timer();
			btask = new GathersqlRun();
			timer.schedule(btask, 0, 5 * 1000);// 5秒钟入库一次
			nmsmemorydate.GathersqlTaskStatus = true;// 设置标记为启动
			nmsmemorydate.GathersqlTasktimer = timer;
		}

	}

	/**
	 * 
	 * 根据数据库表的记录建立采集任务
	 * 
	 */
	public void createAllTask() {
		Timer timer = null;
		BaskTask btask = null;
		Taskdao taskdao = new Taskdao();
		Hashtable runtask = taskdao.GetRunTaskList();
		if (null != runtask) {
			Enumeration allvalue = runtask.elements();
			while (allvalue.hasMoreElements()) {
				NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) allvalue.nextElement();
				if (null != nmsmemorydate.TaskList && nmsmemorydate.TaskList.size() > 0 && nmsmemorydate.TaskList.containsKey(nodeGatherIndicators.getId())) {
					// 停止原来的timer，列表并且从内存中删除对应的对象
					timer = (Timer) nmsmemorydate.TaskList.get(nodeGatherIndicators.getId());
					timer.cancel();
					nmsmemorydate.TaskList.remove(nodeGatherIndicators.getId());
					nmsmemorydate.RunGatherLinst.remove(nodeGatherIndicators.getId());
				} else {// 建立定时采集任务
					timer = new Timer();
					btask = new BaskTask();
					btask.setRunclasspath((String) nodeGatherIndicators.getClasspath());// 运行类得路径
					btask.setTaskid(nodeGatherIndicators.getId() + "");
					btask.setNodeid((String) nodeGatherIndicators.getNodeid());
					btask.setTaskname(nodeGatherIndicators.getName());
					btask.setRunclasspath(nodeGatherIndicators.getClasspath());
					btask.setGather(nodeGatherIndicators);
					long intervaltime = Integer.parseInt(nodeGatherIndicators.getPoll_interval());
					if (nodeGatherIndicators.getInterval_unit().equals("s")) {
						intervaltime = intervaltime * 1000;
					}
					if (nodeGatherIndicators.getInterval_unit().equals("m")) {

						intervaltime = intervaltime * 1000 * 60;
					}
					if (nodeGatherIndicators.getInterval_unit().equals("h")) {
						intervaltime = intervaltime * 1000 * 60 * 60;
					}
					if (nodeGatherIndicators.getInterval_unit().equals("d")) {
						intervaltime = intervaltime * 1000 * 60 * 60 * 24;
					}

					long in = 0;
					if (nmsmemorydate.TaskList.size() > 300) {
						in = (nmsmemorydate.TaskList.size() / 5) * 200;

					} else {
						in = nmsmemorydate.TaskList.size() * 200;
					}

					timer.schedule(btask, in + 1000, intervaltime);// 按分钟执行定时任务
					nmsmemorydate.TaskList.put(nodeGatherIndicators.getId() + "", timer);// 把TIMER对象到任务队里
					nmsmemorydate.RunGatherLinst.put(nodeGatherIndicators.getId() + "", nodeGatherIndicators);
				}
			}
		}
	}

	/**
	 * 
	 * 根据id把采集任务停止
	 * 
	 */
	public synchronized void cancelTask(String id) {
		if (null != nmsmemorydate.TaskList.get(id)) {
			((Timer) nmsmemorydate.TaskList.get(id + "")).cancel();// 注销该任务
			nmsmemorydate.TaskList.remove(id + "");
			nmsmemorydate.RunGatherLinst.remove(id + "");
		}
	}

	/**
	 * 
	 * 取消所有的采集任务
	 * 
	 */

	public void canceAlllTask() {
		if (nmsmemorydate.TaskList.size() > 0) {
			Enumeration allvalue = nmsmemorydate.TaskList.elements();
			Enumeration key = nmsmemorydate.TaskList.keys();
			while (allvalue.hasMoreElements()) {
				String id = (String) key.nextElement();
				if (null != (Timer) allvalue.nextElement()) {
					((Timer) nmsmemorydate.TaskList.get(id + "")).cancel();// 注销该任务
					nmsmemorydate.TaskList.remove(id);
					nmsmemorydate.RunGatherLinst.remove(id);
				}
			}
			nmsmemorydate.TaskList.clone();
			nmsmemorydate.RunGatherLinst.clone();
		}
	}

	/**
	 * 
	 * 建立告警数据入库，告警数据删除
	 * 
	 */
	public void CreateGahterAlarmSQLTask() {
		Timer timer = null;
		GatherDataAlarmsqlRun btask = null;
		timer = new Timer();
		btask = new GatherDataAlarmsqlRun();
		timer.schedule(btask, 500, 1000 * 30);// 按分钟执行定时任务
	}
}
