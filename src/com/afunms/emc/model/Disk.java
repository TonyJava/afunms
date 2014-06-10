package com.afunms.emc.model;

import com.afunms.common.base.BaseVo;

public class Disk extends BaseVo {
	private String name;// Bus 0 Enclosure 0 Disk 0
	private String rid;
	private String did;
	private String id;
	private String rgid;// ������RAID��ID
	private String revision;// �汾��
	private String lun; // ����������LUN���
	private String type; // ����
	private String state; // ״̬
	private String hotSpare; // �ȱ���״̬
	private String prctRebuilt; // �����ؽ��ٷֱ�
	private String prctBound; // ���̰󶨰ٷֱ�
	private String serialNumber; // ���к�
	private String capacity; // ����
	private String hardReadErrors; // Ӳ����������
	private String hardWriteErrors; // Ӳ��д������
	private String softReadErrors; // �����������
	private String softWriteErrors; // Ӳ��д������
	private String numberofReads; // ��������
	private String numberofWrites; // д������
	private String numberofLuns; // �����˴��̵�LUN��
	private String raidGroupID; // RAID����
	private String kbytesRead; // ��KB
	private String kbytesWritten; // дKB
	private String driveType; // ������������
	private String idleTicks; // ��״̬ʱ����
	private String busyTicks; // æ״̬ʱ����
	private String currentSpeed; // ���̵�ǰ�ٶ�
	private String maximumSpeed; // ���̿����е�����ٶ�

	public String getBusyTicks() {
		return busyTicks;
	}

	public String getCapacity() {
		return capacity;
	}

	public String getCurrentSpeed() {
		return currentSpeed;
	}

	public String getDid() {
		return did;
	}

	public String getDriveType() {
		return driveType;
	}

	public String getHardReadErrors() {
		return hardReadErrors;
	}

	public String getHardWriteErrors() {
		return hardWriteErrors;
	}

	public String getHotSpare() {
		return hotSpare;
	}

	public String getId() {
		return id;
	}

	public String getIdleTicks() {
		return idleTicks;
	}

	public String getKbytesRead() {
		return kbytesRead;
	}

	public String getKbytesWritten() {
		return kbytesWritten;
	}

	public String getLun() {
		return lun;
	}

	public String getMaximumSpeed() {
		return maximumSpeed;
	}

	public String getName() {
		return name;
	}

	public String getNumberofLuns() {
		return numberofLuns;
	}

	public String getNumberofReads() {
		return numberofReads;
	}

	public String getNumberofWrites() {
		return numberofWrites;
	}

	public String getPrctBound() {
		return prctBound;
	}

	public String getPrctRebuilt() {
		return prctRebuilt;
	}

	public String getRaidGroupID() {
		return raidGroupID;
	}

	public String getRevision() {
		return revision;
	}

	public String getRgid() {
		return rgid;
	}

	public String getRid() {
		return rid;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getSoftReadErrors() {
		return softReadErrors;
	}

	public String getSoftWriteErrors() {
		return softWriteErrors;
	}

	public String getState() {
		return state;
	}

	public String getType() {
		return type;
	}

	public void setBusyTicks(String busyTicks) {
		this.busyTicks = busyTicks;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public void setCurrentSpeed(String currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public void setDriveType(String driveType) {
		this.driveType = driveType;
	}

	public void setHardReadErrors(String hardReadErrors) {
		this.hardReadErrors = hardReadErrors;
	}

	public void setHardWriteErrors(String hardWriteErrors) {
		this.hardWriteErrors = hardWriteErrors;
	}

	public void setHotSpare(String hotSpare) {
		this.hotSpare = hotSpare;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdleTicks(String idleTicks) {
		this.idleTicks = idleTicks;
	}

	public void setKbytesRead(String kbytesRead) {
		this.kbytesRead = kbytesRead;
	}

	public void setKbytesWritten(String kbytesWritten) {
		this.kbytesWritten = kbytesWritten;
	}

	public void setLun(String lun) {
		this.lun = lun;
	}

	public void setMaximumSpeed(String maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeid(String nodeid) {
	}

	public void setNumberofLuns(String numberofLuns) {
		this.numberofLuns = numberofLuns;
	}

	public void setNumberofReads(String numberofReads) {
		this.numberofReads = numberofReads;
	}

	public void setNumberofWrites(String numberofWrites) {
		this.numberofWrites = numberofWrites;
	}

	public void setPrctBound(String prctBound) {
		this.prctBound = prctBound;
	}

	public void setPrctRebuilt(String prctRebuilt) {
		this.prctRebuilt = prctRebuilt;
	}

	public void setRaidGroupID(String raidGroupID) {
		this.raidGroupID = raidGroupID;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public void setRgid(String rgid) {
		this.rgid = rgid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setSoftReadErrors(String softReadErrors) {
		this.softReadErrors = softReadErrors;
	}

	public void setSoftWriteErrors(String softWriteErrors) {
		this.softWriteErrors = softWriteErrors;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setType(String type) {
		this.type = type;
	}

}
