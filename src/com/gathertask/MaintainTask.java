package com.gathertask;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimerTask;

import com.afunms.indicators.model.NodeGatherIndicators;
import com.gatherdb.nmsmemorydate;
import com.gathertask.dao.Taskdao;

/**
 * 
 * ά���ɼ����� 5���Ӽ��һ�βɼ����񣬼���Ѿ����ܵ������뱻ֹͣ��
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
		// ��ʼ���ڴ���е���Ϣ���бȽ�
		if (null != nlist && nlist.size() > 0) {
			// ���ݿ���б��бȽ��ڴ��б�
			NodeGatherIndicators gathertask;
			NodeGatherIndicators gathertask2;
			Enumeration it1 = nlist.elements();
			while (it1.hasMoreElements()) {
				gathertask = (NodeGatherIndicators) it1.nextElement();
				// �ڴ�����а����ж�Ӧ�Ķ�ʱ����
				if (nmsmemorydate.RunGatherLinst.containsKey(gathertask.getId() + "")) {
					String itime = gathertask.getPoll_interval();
					String itype = gathertask.getInterval_unit();
					gathertask2 = (NodeGatherIndicators) nmsmemorydate.RunGatherLinst.get(gathertask.getId() + "");
					if (itime.equals(gathertask2.getPoll_interval()) && itype.equals(gathertask2.getInterval_unit())) {
						// ���½���һ����ʱ����
					} else {
						tkmanager.createOneTask(gathertask);
					}
				} else {// �ڴ���û�ж�Ӧ�Ķ�ʱ����
					tkmanager.createOneTask(gathertask);
				}
			}

			// �����ڴ��б���ҵ�ǰ��ʱ������
			if (nmsmemorydate.RunGatherLinst.size() > 0) {
				it1 = nmsmemorydate.RunGatherLinst.elements();
				while (it1.hasMoreElements()) {
					gathertask2 = (NodeGatherIndicators) it1.nextElement();
					// �ڴ�����а����ж�Ӧ�Ķ�ʱ����
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
