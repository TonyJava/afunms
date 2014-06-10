package com.afunms.polling.base;


public abstract class BaseTask implements Runnable {
	protected String descr;

	public BaseTask() {
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void run() {
		if (timeRestricted()) {
			executeTask();
		}
	}

	/**
	 * ʱ������,����ɸ����������
	 */
	public boolean timeRestricted() {
		return true;
	}

	public abstract void executeTask();
}