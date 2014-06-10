package com.afunms.vmware.vim25.vo;

import com.afunms.common.base.BaseVo;

public class VMWareVO extends BaseVo {
	
	private long id;
	private String name;
	private String vid;
	private String model;//设备型号
	private String cpunum;//cpu核数
	private String netnum;//网卡个数
	private String memory;//内存
	private String ghz;//主频
	private String hostpower;//物理机电源
	private String fullname;//虚拟机全名
	private String cpu;// 虚拟机的cpu配置是  eg:2颗*3核
	private String memoryuse;//内存分配
	private String vmpower;
	private String hoid;//物理机vid
	private String disk;//磁盘
	private String cpuuse;//cpu使用频率
	private String hostnum;//云资源的主机数量
	private String mem;//总内存
	private String store;//总存储
	private String unusedstore;//未使用的存储
	private String dcid;//数据中心Id
	private String crid;//集群id
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCpunum() {
		return cpunum;
	}
	public void setCpunum(String cpunum) {
		this.cpunum = cpunum;
	}
	public String getNetnum() {
		return netnum;
	}
	public void setNetnum(String netnum) {
		this.netnum = netnum;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public String getGhz() {
		return ghz;
	}
	public void setGhz(String ghz) {
		this.ghz = ghz;
	}
	public String getHostpower() {
		return hostpower;
	}
	public void setHostpower(String hostpower) {
		this.hostpower = hostpower;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public String getMemoryuse() {
		return memoryuse;
	}
	public void setMemoryuse(String memoryuse) {
		this.memoryuse = memoryuse;
	}
	public String getVmpower() {
		return vmpower;
	}
	public void setVmpower(String vmpower) {
		this.vmpower = vmpower;
	}
	public String getHoid() {
		return hoid;
	}
	public void setHoid(String hoid) {
		this.hoid = hoid;
	}
	public String getDisk() {
		return disk;
	}
	public void setDisk(String disk) {
		this.disk = disk;
	}
	public String getCpuuse() {
		return cpuuse;
	}
	public void setCpuuse(String cpuuse) {
		this.cpuuse = cpuuse;
	}
	public String getHostnum() {
		return hostnum;
	}
	public void setHostnum(String hostnum) {
		this.hostnum = hostnum;
	}
	public String getMem() {
		return mem;
	}
	public void setMem(String mem) {
		this.mem = mem;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public String getUnusedstore() {
		return unusedstore;
	}
	public void setUnusedstore(String unusedstore) {
		this.unusedstore = unusedstore;
	}
	public String getDcid() {
		return dcid;
	}
	public void setDcid(String dcid) {
		this.dcid = dcid;
	}
	public String getCrid() {
		return crid;
	}
	public void setCrid(String crid) {
		this.crid = crid;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	

}
