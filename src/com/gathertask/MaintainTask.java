package com.gathertask;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimerTask;

import com.afunms.indicators.model.NodeGatherIndicators;
import com.gatherdb.nmsmemorydate;
import com.gathertask.dao.Taskdao;

/**
 * 
 * 维护采集任务 5分钟检查一次采集任务，检查已经在跑的任务与被停止的
 * 
 * 
 */

@SuppressWarnings("unchecked")
public class MaintainTask extends TimerTask {

	@Override
	public void run() {
		Taskdao taskdao = new Taskdao();
		Hashtable nlist = taskdao.GetRunTaskList();
		TaskManager tkmanager = new TaskManager();
		// 开始对内存队列的信息进行比较
		if (null != nlist && nlist.size() > 0) {
			// 数据库的列表中比较内存列表
			NodeGatherIndicators gathertask;
			NodeGatherIndicators gathertask2;
			Enumeration it1 = nlist.elements();
			while (it1.hasMoreElements()) {
				gathertask = (NodeGatherIndicators) it1.nextElement();
				// 内存队列中包含有对应的定时任务
				if (nmsmemorydate.RunGatherLinst.containsKey(gathertask.getId() + "")) {
					String itime = gathertask.getPoll_interval();
					String itype = gathertask.getInterval_unit();
					gathertask2 = (NodeGatherIndicators) nmsmemorydate.RunGatherLinst.get(gathertask.getId() + "");
					if (itime.equals(gathertask2.getPoll_interval()) && itype.equals(gathertask2.getInterval_unit())) {
						// 重新建立一个定时任务
					} else {
						tkmanager.createOneTask(gathertask);
					}
				} else {// 内存中没有对应的定时任务
					tkmanager.createOneTask(gathertask);
				}
			}

			// 迭代内存列表查找当前定时的任务
			if (nmsmemorydate.RunGatherLinst.size() > 0) {
				it1 = nmsmemorydate.RunGatherLinst.elements();
				while (it1.hasMoreElements()) {
					gathertask2 = (NodeGatherIndicators) it1.nextElement();
					// 内存队列中包含有对应的定时任务
					if (!nlist.containsKey(gathertask2.getId() + "")) {
						tkmanager.cancelTask(gathertask2.getId() + "");
					}
				}
			}
		} else {
			tkmanager.canceAlllTask();
		}

	}
}
