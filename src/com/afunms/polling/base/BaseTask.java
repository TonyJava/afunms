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
	 * 时间限制,子类可覆盖这个方法
	 */
	public boolean timeRestricted() {
		return true;
	}

	public abstract void executeTask();
}