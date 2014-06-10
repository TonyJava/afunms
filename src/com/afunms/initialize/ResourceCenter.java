package com.afunms.initialize;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class ResourceCenter {
	private static ResourceCenter instance = new ResourceCenter();

	public static ResourceCenter getInstance() {
		return instance;
	}

	public static void setInstance(ResourceCenter instance) {
		ResourceCenter.instance = instance;
	}

	private Hashtable managerMap;
	private Hashtable ajaxManagerMap;
	private Hashtable menuMap; // �˵�
	private Hashtable actionMap;
	private String sysPath; // ϵͳ·��
	private String appServer; // ϵͳ������
	private String jndi; // ���ݿ����ӳ�JNDI
	private String snmpversion; // snmp�汾
	private Hashtable chartStorage;
	private Hashtable monitorMap;
	private Set showMoidsSet; // ��Ҫ����������ʾ��ָ��
	private int perThreadNodes; // ÿ���̴߳������Ӷ���ĸ���
	private int maxThreads; // ����߳���
	private boolean logInfo; // �Ƿ��ӡ������Ϣ
	private boolean logError; // �Ƿ��ӡ������Ϣ
	private boolean startPolling; // �Ƿ�������ѯ
	private List serviceList;
	private int pollingThreadInterval; // �߳�����ʱ��ʱ��
	private boolean hasDiscovered; // ���ֹ�����
	private int ipIdleDays; // IP���е�����

	private Hashtable alarmHashtable;// ��Ÿ澯��

	private Hashtable cfgHash;

	private String dbtype; // ���ݿ�����

	// new add
	private Hashtable hardware;

	/**
	 * ָ��仯��Ϣ����
	 */
	private Hashtable indicatorsChangeInfoHash = new Hashtable();

	private ResourceCenter() {
		logInfo = true;
		logError = true;
		ipIdleDays = 30; // Ĭ��Ϊ30��
		chartStorage = new Hashtable();
		dbtype = "mysql";
	}

	public Hashtable getActionMap() {
		return actionMap;
	}

	public Hashtable getAjaxManagerMap() {
		return ajaxManagerMap;
	}

	public Hashtable getAlarmHashtable() {
		return alarmHashtable;
	}

	public String getAppServer() {
		return appServer;
	}

	public Hashtable getCfgHash() {
		return cfgHash;
	}

	public Hashtable getChartStorage() {
		return chartStorage;
	}

	public String getDbtype() {
		return dbtype;
	}

	public Hashtable getHardware() {
		return hardware;
	}

	public Hashtable getIndicatorsChangeInfoHash() {
		return indicatorsChangeInfoHash;
	}

	public int getIpIdleDays() {
		return ipIdleDays;
	}

	public String getJndi() {
		return jndi;
	}

	public Hashtable getManagerMap() {
		return managerMap;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * @return the menuMap
	 */
	public Hashtable getMenuMap() {
		return menuMap;
	}

	public Hashtable getMonitorMap() {
		return monitorMap;
	}

	public int getPerThreadNodes() {
		return perThreadNodes;
	}

	public int getPollingThreadInterval() {
		return pollingThreadInterval;
	}

	public List getServiceList() {
		return serviceList;
	}

	public Set getShowMoidsSet() {
		return showMoidsSet;
	}

	public String getSnmpversion() {
		return snmpversion;
	}

	public String getSysPath() {
		return sysPath;
	}

	public boolean hasDiscovered() {
		return hasDiscovered;
	}

	public boolean isLogError() {
		return logError;
	}

	public boolean isLogInfo() {
		return logInfo;
	}

	public boolean isStartPolling() {
		return startPolling;
	}

	public void setActionMap(Hashtable actionMap) {
		this.actionMap = actionMap;
	}

	public void setAjaxManagerMap(Hashtable ajaxManagerMap) {
		this.ajaxManagerMap = ajaxManagerMap;
	}

	public void setAlarmHashtable(Hashtable alarmHashtable) {

		this.alarmHashtable = alarmHashtable;
	}

	public void setAppServer(String appServer) {
		this.appServer = appServer;
	}

	public void setCfgHash(Hashtable cfgHash) {
		this.cfgHash = cfgHash;
	}

	public void setChartStorage(Hashtable chartStorage) {
		this.chartStorage = chartStorage;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	public void setHardware(Hashtable hardware) {
		this.hardware = hardware;
	}

	public void setHasDiscovered(boolean hasDiscovered) {
		this.hasDiscovered = hasDiscovered;
	}

	public void setIndicatorsChangeInfoHash(Hashtable indicatorsChangeInfoHash) {
		this.indicatorsChangeInfoHash = indicatorsChangeInfoHash;
	}

	public void setIpIdleDays(int ipIdleDays) {
		this.ipIdleDays = ipIdleDays;
	}

	public void setJndi(String jndi) {
		this.jndi = jndi;
	}

	public void setLogError(boolean logError) {
		this.logError = logError;
	}

	public void setLogInfo(boolean logInfo) {
		this.logInfo = logInfo;
	}

	public void setManagerMap(Hashtable managerMap) {
		this.managerMap = managerMap;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * @param menuMap
	 *            the menuMap to set
	 */
	public void setMenuMap(Hashtable menuMap) {
		this.menuMap = menuMap;
	}

	public void setMonitorMap(Hashtable monitorMap) {
		this.monitorMap = monitorMap;
	}

	public void setPerThreadNodes(int perThreadNodes) {
		this.perThreadNodes = perThreadNodes;
	}

	public void setPollingThreadInterval(int pollingThreadInterval) {
		this.pollingThreadInterval = pollingThreadInterval;
	}

	public void setServiceList(List serviceList) {
		this.serviceList = serviceList;
	}

	public void setShowMoidsSet(Set showMoidsSet) {
		this.showMoidsSet = showMoidsSet;
	}

	public void setSnmpversion(String snmpversion) {
		this.snmpversion = snmpversion;
	}

	public void setStartPolling(boolean startPolling) {
		this.startPolling = startPolling;
	}

	public void setSysPath(String sysPath) {
		this.sysPath = sysPath;
	}
}
