package com.afunms.polling.task;

public class TaskFactory {
	public TaskFactory() {
		super();
	}
	public MonitorTask getInstance(String tasktype) {
		if (tasktype.equals("hostcollectdatahourtask"))
			return new HostCollectHourTask();
		if (tasktype.equals("hostcollectdatadaytask"))
			return new HostCollectDayTask();
		if (tasktype.equals("checklinktask"))
			return new CheckLinkTask();
		if (tasktype.equals("updatexmltask"))
			return new UpdateXmlTask();
		if (tasktype.equals("m5slatelnettask"))
			return new M5SLATelnetTask();
		if (tasktype.equals("m_30_backupTelnetConfigTask"))
			return new M30BackupTelnetConfigTask();
		if (tasktype.equals("m_30_passwdChangeHintTask"))
			return new M30PasswdBackupTelnetConfigTask();
		if (tasktype.equals("m5telnetConfgTask"))
			return new M5TelnetConfigTask();
		if (tasktype.equals("h1Acltask"))
			return new H1AclTask();
		if (tasktype.equals("cabinettask"))
			return new CabinetTask();
		return null;
	}

}