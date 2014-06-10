package com.afunms.initialize;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import com.afunms.polling.task.MonitorTask;
import com.afunms.polling.task.MonitorTimer;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version ����ʱ�䣺Jul 14, 2011 7:15:43 PM ��˵��
 */
public class TimerListener extends Thread {

	private MonitorTimer timer;// ������timer

	private TimerTask task;// �������ȵ�task

	private long period;// ����task��ʱ����

	public TimerListener() {
		super();
	}

	/**
	 * @param task
	 *            ��timer���ȵ�task����
	 * @param delay
	 *            ����taskʱ���ӳ�ʱ��
	 * @param period
	 *            Task�����ļ��ʱ�� ��λ������
	 */
	public TimerListener(TimerTask task, long delay, long period) {
		this.task = task;
		this.period = period;
	}

	/**
	 * @param timer
	 *            ��������timer
	 */
	public void addTimerListener(MonitorTimer timer) {
		this.timer = timer;
		this.start();
	}

	/**
	 * �Ƿ�����Ҫ������task
	 * 
	 * @param task
	 * @return
	 */
	public boolean isMonitorTask(TimerTask task) {
		boolean flag = false;
		if (task instanceof MonitorTask) {
			String taskName = task.getClass().getName();
			if (taskName.contains("M5Task")) {
				flag = true;
			} else if (taskName.contains("M10Task")) {
				flag = true;
			}// ����Task���...
		}
		return flag;
	}

	@Override
	public void run() {
		Date lastStartTime = null;
		Calendar calendar = null;
		boolean isMonitorTask = isMonitorTask(task);// ��task�Ƿ�Ϊ��Ҫ������task
		while (true) {
			calendar = Calendar.getInstance();
			if (isMonitorTask) {// ֻ����������ΪMonitorTask���͵�task
				// ���˵�û���趨recentlyStartTime��task
				lastStartTime = ((MonitorTask) task).getRecentlyStartTime();// ���һ������������
				if (lastStartTime == null) {
					continue;
				}
				long timeInterval = calendar.getTime().getTime() - lastStartTime.getTime();// ʵ��ʱ����
				long maxTimeInterval = period * 2 - 10 * 1000;// ���Ŀɽ��ܵ�ʱ���� =
				if (timeInterval > maxTimeInterval) {// ���ʱ�䳬���趨��task��ѯʱ����
					task.cancel();
					timer.purge();// �Ƴ������Ѿ���ͣ������
					timer.canclethis(true);
					try {
						task = (TimerTask) Class.forName(task.getClass().getName()).newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					timer = new MonitorTimer();// �ؽ�timer
					timer.schedule(task, 0, period);// �½�Timer ������Task���� �����Ӽ���
					break;// �˳���while-trueѭ����
				}
				try {
					Thread.sleep(period);// ͣ��һ��ʱ��֮�� ����Task�Ƿ�������ѵ�ж�
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
	}
}
