package com.afunms.temp.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.DeviceCollectEntity;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.ServiceCollectEntity;
import com.afunms.polling.om.SoftwareCollectEntity;
import com.afunms.polling.om.StorageCollectEntity;
import com.afunms.polling.om.Tempcollectdata;
import com.afunms.temp.dao.ArpTempDao;
import com.afunms.temp.dao.ChannelTempDao;
import com.afunms.temp.dao.CpuTempDao;
import com.afunms.temp.dao.DeviceTempDao;
import com.afunms.temp.dao.DiskPerfTempDao;
import com.afunms.temp.dao.DiskTempDao;
import com.afunms.temp.dao.EnvironmentTempDao;
import com.afunms.temp.dao.FbconfigTempDao;
import com.afunms.temp.dao.FdbTempDao;
import com.afunms.temp.dao.FlashTempDao;
import com.afunms.temp.dao.InterfaceTempDao;
import com.afunms.temp.dao.IpTempDao;
import com.afunms.temp.dao.LightTempDao;
import com.afunms.temp.dao.MemoryTempDao;
import com.afunms.temp.dao.OthersTempDao;
import com.afunms.temp.dao.ProcessTempDao;
import com.afunms.temp.dao.RouteTempDao;
import com.afunms.temp.dao.ServiceTempDao;
import com.afunms.temp.dao.SoftwareTempDao;
import com.afunms.temp.dao.StorageTempDao;
import com.afunms.temp.dao.SystemTempDao;
import com.afunms.temp.dao.UserTempDao;
import com.afunms.temp.model.DeviceNodeTemp;
import com.afunms.temp.model.FdbNodeTemp;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.RouterNodeTemp;
import com.afunms.temp.model.ServiceNodeTemp;
import com.afunms.temp.model.SoftwareNodeTemp;
import com.afunms.temp.model.StorageNodeTemp;

@SuppressWarnings("unchecked")
public class TempDataService {

	private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String[] iproutertype = { "", "", "", "direct(3)", "indirect(4)" };
	private static String[] iprouterproto = { "", "other(1)", "local(2)", "netmgmt(3)", "icmp(4)", "egp(5)", "ggp(6)", "hello(7)", "rip(8)", "is-is(9)", "es-is(10)",
			"ciscoIgrp(11)", "bbnSpfIgp(12)", "ospf(13)", "bgp(14)" };

	public void collectData(String ip, Object data, String type, String subtype) {
		Tempcollectdata tempdata = new Tempcollectdata();
		try {
			BeanUtils.copyProperties(tempdata, data);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		Calendar tempCal = (Calendar) tempdata.getCollecttime();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		NodeTemp nodeTemp = new NodeTemp();
		nodeTemp.setNodeid(node.getId() + "");
		nodeTemp.setIp(ip);
		nodeTemp.setType(type);
		nodeTemp.setSubtype(subtype);
		nodeTemp.setEntity(tempdata.getCategory());
		nodeTemp.setSubentity(tempdata.getEntity());
		nodeTemp.setSindex(tempdata.getSubentity());
		nodeTemp.setThevalue(tempdata.getThevalue());
		nodeTemp.setChname(tempdata.getChname());
		nodeTemp.setRestype(tempdata.getRestype());
		nodeTemp.setCollecttime(time);
		nodeTemp.setUnit(tempdata.getUnit());
		nodeTemp.setBak(tempdata.getBak());
		if (tempdata.getThevalue() != null && !"".equals(tempdata.getThevalue()) && !"null".equals(tempdata.getThevalue())) {
			saveTempData(tempdata.getCategory(), nodeTemp);
		}
	}

	public NodeTemp DataCopy(String ip, Object data, String type, String subtype) {
		Tempcollectdata tempdata = new Tempcollectdata();
		try {
			BeanUtils.copyProperties(tempdata, data);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		Calendar tempCal = (Calendar) tempdata.getCollecttime();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		NodeTemp nodeTemp = new NodeTemp();
		nodeTemp.setNodeid(node.getId() + "");
		nodeTemp.setIp(ip);
		nodeTemp.setType(type);
		nodeTemp.setSubtype(subtype);
		nodeTemp.setEntity(tempdata.getCategory());
		nodeTemp.setSubentity(tempdata.getEntity());
		nodeTemp.setSindex(tempdata.getSubentity());
		nodeTemp.setThevalue(tempdata.getThevalue());
		nodeTemp.setChname(tempdata.getChname());
		nodeTemp.setRestype(tempdata.getRestype());
		nodeTemp.setCollecttime(time);
		nodeTemp.setUnit(tempdata.getUnit());
		nodeTemp.setBak(tempdata.getBak());
		return nodeTemp;
	}

	// 保存路由表数据
	public void collectRouteData(String ip, IpRouter iprouter, String type, String subtype) {
		Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		Calendar date = Calendar.getInstance();
		String time = sdf.format(date.getTime());
		RouterNodeTemp nodeTemp = new RouterNodeTemp();
		nodeTemp.setNodeid(node.getId() + "");
		nodeTemp.setIp(ip);
		nodeTemp.setType(type);
		nodeTemp.setSubtype(subtype);
		nodeTemp.setRtype(iproutertype[Integer.parseInt(iprouter.getType().longValue() + "")]);
		nodeTemp.setProto(iprouterproto[Integer.parseInt(iprouter.getProto().longValue() + "")]);
		nodeTemp.setPhysaddress(iprouter.getPhysaddress());
		nodeTemp.setNexthop(iprouter.getNexthop());
		nodeTemp.setMask(iprouter.getMask());
		nodeTemp.setCollecttime(time);
		nodeTemp.setIfindex(iprouter.getIfindex());
		nodeTemp.setDest(iprouter.getDest());
		try {
			saveRouteTempData(nodeTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 保存fdb表当前数据
	public void collectFdbData(String ip, IpMac ipmac, String type, String subtype) {
		Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		Calendar tempCal = (Calendar) ipmac.getCollecttime();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		FdbNodeTemp nodeTemp = new FdbNodeTemp();
		nodeTemp.setNodeid(node.getId() + "");
		nodeTemp.setIp(ip);
		nodeTemp.setType(type);
		nodeTemp.setSubtype(subtype);
		nodeTemp.setIfindex(ipmac.getIfindex());
		nodeTemp.setIfband(ipmac.getIfband());
		nodeTemp.setIfsms(ipmac.getIfsms());
		nodeTemp.setIpaddress(ipmac.getIpaddress());
		nodeTemp.setMac(ipmac.getMac());
		nodeTemp.setCollecttime(time);
		try {
			saveFdbTempData(nodeTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 保存windows软件信息
	public void collectSoftWareData(String ip, SoftwareCollectEntity sof, String type, String subtype) {
		Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		SoftwareNodeTemp nodeTemp = new SoftwareNodeTemp();
		nodeTemp.setNodeid(node.getId() + "");
		nodeTemp.setIp(ip);
		nodeTemp.setType(type);
		nodeTemp.setSubtype(subtype);
		nodeTemp.setInsdate(sof.getInsdate());
		nodeTemp.setName(sof.getName());
		nodeTemp.setStype(sof.getType());
		nodeTemp.setSwid(sof.getSwid());
		nodeTemp.setCollecttime(time);
		try {
			saveSoftWareTempData(nodeTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 保存windows服务信息
	public void collectServiceData(String ip, ServiceCollectEntity service, String type, String subtype) {
		Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		ServiceNodeTemp nodeTemp = new ServiceNodeTemp();
		nodeTemp.setNodeid(node.getId() + "");
		nodeTemp.setIp(ip);
		nodeTemp.setType(type);
		nodeTemp.setSubtype(subtype);
		nodeTemp.setName(service.getName());
		nodeTemp.setInstate(service.getInstate());
		nodeTemp.setOpstate(service.getOpstate());
		nodeTemp.setPaused(service.getPaused());
		nodeTemp.setUninst(service.getUninst());
		nodeTemp.setCollecttime(time);
		try {
			saveServiceTempData(nodeTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 保存windows存储信息
	public void collectStorageData(String ip, StorageCollectEntity stor, String type, String subtype) {
		Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		StorageNodeTemp nodeTemp = new StorageNodeTemp();
		nodeTemp.setNodeid(node.getId() + "");
		nodeTemp.setIp(ip);
		nodeTemp.setType(type);
		nodeTemp.setSubtype(subtype);
		nodeTemp.setName(stor.getName());
		nodeTemp.setStype(stor.getType());
		nodeTemp.setCap(stor.getCap());
		nodeTemp.setStorageindex(stor.getStorageindex());
		nodeTemp.setCollecttime(time);
		try {
			saveStorTempData(nodeTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 保存windows设备信息
	public void collectDeviceData(String ip, DeviceCollectEntity dev, String type, String subtype) {
		Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		DeviceNodeTemp nodeTemp = new DeviceNodeTemp();
		nodeTemp.setNodeid(node.getId() + "");
		nodeTemp.setIp(ip);
		nodeTemp.setType(type);
		nodeTemp.setSubtype(subtype);
		nodeTemp.setName(dev.getName());
		nodeTemp.setDeviceindex(dev.getDeviceindex());
		nodeTemp.setDtype(dev.getType());
		nodeTemp.setStatus(dev.getStatus());
		nodeTemp.setCollecttime(time);
		try {
			saveDeviceTempData(nodeTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 保存aix系统和linux系统的信息
	public void collectHashData(String ip, Hashtable hash, String type, String subtype, String category, int i) {
		if (hash != null && hash.size() > 0) {
			Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			Enumeration en = hash.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String value = (String) hash.get(key);
				NodeTemp nodeTemp = new NodeTemp();
				nodeTemp.setNodeid(node.getId() + "");
				nodeTemp.setIp(ip);
				nodeTemp.setType(type);
				nodeTemp.setSubtype(subtype);
				nodeTemp.setEntity(category);
				nodeTemp.setSubentity(key);
				nodeTemp.setSindex(i + "");
				nodeTemp.setThevalue(value);
				nodeTemp.setChname(key);
				nodeTemp.setRestype("static");
				nodeTemp.setCollecttime(time);
				nodeTemp.setUnit("");
				saveTempData(category, nodeTemp);
			}
		}
	}

	private void saveTempData(String category, NodeTemp nodeTemp) {
		if ("CPU".equalsIgnoreCase(category)) {
			deleteCpuTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveCpuTempData(nodeTemp);
		} else if ("Memory".equalsIgnoreCase(category)) {
			deleteMemoryTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveMemoryTempData(nodeTemp);
		} else if ("Flash".equalsIgnoreCase(category)) {
			deleteFlashTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveFlashTempData(nodeTemp);
		} else if ("Interface".equalsIgnoreCase(category)) {
			deleteInterfaceTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveInterfaceTempData(nodeTemp);
		} else if ("System".equalsIgnoreCase(category)) {
			deleteSystemTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveSystemTempData(nodeTemp);
		} else if ("Power".equalsIgnoreCase(category)) {
			deleteEnvironmentTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveEnvironmentTempData(nodeTemp);
		} else if ("Voltage".equalsIgnoreCase(category)) {
			deleteEnvironmentTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveEnvironmentTempData(nodeTemp);
		} else if ("Fan".equalsIgnoreCase(category)) {
			deleteEnvironmentTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveEnvironmentTempData(nodeTemp);
		} else if ("Temperature".equalsIgnoreCase(category)) {
			deleteEnvironmentTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveEnvironmentTempData(nodeTemp);
		} else if ("Ip".equalsIgnoreCase(category)) {// topo_ipalias表

		} else if ("Arp".equalsIgnoreCase(category)) {// ipmac表

		} else if ("fbconfig".equalsIgnoreCase(category)) {
			saveFbTempData(nodeTemp);
		} else if ("channel".equalsIgnoreCase(category)) {
			saveChannelTempData(nodeTemp);
		} else if ("Light".equalsIgnoreCase(category)) {
			saveLightTempData(nodeTemp);
		} else if ("Process".equalsIgnoreCase(category)) {
			deleteProcessTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveProcessTempData(nodeTemp);
		} else if ("Disk".equalsIgnoreCase(category)) {
			deleteDiskTempData(nodeTemp.getNodeid(), nodeTemp.getSindex(), nodeTemp.getSubentity());
			saveDiskTempData(nodeTemp);
		} else if ("User".equalsIgnoreCase(category)) {
			saveUserTempData(nodeTemp);
		} else if ("alldiskperf".equalsIgnoreCase(category)) {
			saveDiskPerfTempData(nodeTemp);
		} else if ("cpuperflist".equalsIgnoreCase(category)) {
			saveOtherTempData(nodeTemp);
		} else if ("pagehash".equalsIgnoreCase(category)) {
			saveOtherTempData(nodeTemp);
		} else if ("netmedialist".equalsIgnoreCase(category)) {
			saveOtherTempData(nodeTemp);
		} else if ("paginghash".equalsIgnoreCase(category)) {
			saveOtherTempData(nodeTemp);
		}
	}

	public void deleteNetTempDataByIp(String ip) {
		deleteCpuTempData(ip);
		deleteMemoryTempData(ip);
		deleteInterfaceTempData(ip);
		deleteArpTempData(ip);
		deleteFdbTempData(ip);
		deleteSystemTempData(ip);
		deleteRouteTempData(ip);
		deleteIpTempData(ip);
		deleteLightTempData(ip);
		deleteFbTempData(ip);
		deleteChannelTempData(ip);
		deleteProcessTempData(ip);
		deleteFlashTempData(ip);
		deleteEnvironmentTempData(ip);
	}

	public void deleteHostTempDataByIp(String ip) {
		deleteCpuTempData(ip);
		deleteMemoryTempData(ip);
		deleteInterfaceTempData(ip);
		deleteArpTempData(ip);
		deleteFdbTempData(ip);
		deleteSystemTempData(ip);
		deleteRouteTempData(ip);
		deleteIpTempData(ip);
		deleteProcessTempData(ip);
		deleteDiskTempData(ip);
		deleteSoftWareTempData(ip);
		deleteServiceTempData(ip);
		deleteDeviceTempData(ip);
		deleteStorageTempData(ip);
		deleteUserTempData(ip);
		deleteDiskPerfTempData(ip);
		deleteOtherTempData(ip);
	}

	// 保存当前数据表
	private void saveCpuTempData(NodeTemp nodeTemp) {
		CpuTempDao cpuTempDao = new CpuTempDao();
		cpuTempDao.save(nodeTemp);
	}

	private void saveMemoryTempData(NodeTemp nodeTemp) {
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		memoryTempDao.save(nodeTemp);
	}

	private void saveInterfaceTempData(NodeTemp nodeTemp) {
		InterfaceTempDao intTempDao = new InterfaceTempDao();
		intTempDao.save(nodeTemp);
	}

	private void saveFdbTempData(FdbNodeTemp nodeTemp) {
		FdbTempDao fdbTempDao = new FdbTempDao();
		fdbTempDao.save(nodeTemp);
	}

	private void saveSystemTempData(NodeTemp nodeTemp) {
		SystemTempDao sysTempDao = new SystemTempDao();
		sysTempDao.save(nodeTemp);
	}

	private void saveRouteTempData(RouterNodeTemp nodeTemp) {
		RouteTempDao routeTempDao = new RouteTempDao();
		routeTempDao.save(nodeTemp);
	}

	private void saveFbTempData(NodeTemp nodeTemp) {
		FbconfigTempDao fbTempDao = new FbconfigTempDao();
		fbTempDao.save(nodeTemp);
	}

	private void saveLightTempData(NodeTemp nodeTemp) {
		LightTempDao lightTempDao = new LightTempDao();
		lightTempDao.save(nodeTemp);
	}

	private void saveChannelTempData(NodeTemp nodeTemp) {
		ChannelTempDao chTempDao = new ChannelTempDao();
		chTempDao.save(nodeTemp);
	}

	private void saveProcessTempData(NodeTemp nodeTemp) {
		ProcessTempDao processTempDao = new ProcessTempDao();
		processTempDao.save(nodeTemp);
	}

	private void saveSoftWareTempData(SoftwareNodeTemp nodeTemp) {
		SoftwareTempDao softwareTempDao = new SoftwareTempDao();
		softwareTempDao.save(nodeTemp);
	}

	private void saveStorTempData(StorageNodeTemp nodeTemp) {
		StorageTempDao storageTempDao = new StorageTempDao();
		storageTempDao.save(nodeTemp);
	}

	private void saveDeviceTempData(DeviceNodeTemp nodeTemp) {
		DeviceTempDao deviceTempDao = new DeviceTempDao();
		deviceTempDao.save(nodeTemp);
	}

	private void saveServiceTempData(ServiceNodeTemp nodeTemp) {
		ServiceTempDao serviceTempDao = new ServiceTempDao();
		serviceTempDao.save(nodeTemp);
	}

	private void saveDiskTempData(NodeTemp nodeTemp) {
		DiskTempDao diskTempDao = new DiskTempDao();
		diskTempDao.save(nodeTemp);
	}

	private void saveUserTempData(NodeTemp nodeTemp) {
		UserTempDao userTempDao = new UserTempDao();
		userTempDao.save(nodeTemp);
	}

	private void saveDiskPerfTempData(NodeTemp nodeTemp) {
		DiskPerfTempDao diskPerfTempDao = new DiskPerfTempDao();
		diskPerfTempDao.save(nodeTemp);
	}

	private void saveOtherTempData(NodeTemp nodeTemp) {
		OthersTempDao othersTempDao = new OthersTempDao();
		othersTempDao.save(nodeTemp);
	}

	public void saveFlashTempData(NodeTemp nodeTemp) {
		FlashTempDao flashTempDao = new FlashTempDao();
		flashTempDao.save(nodeTemp);
	}

	public void saveEnvironmentTempData(NodeTemp nodeTemp) {
		EnvironmentTempDao envTempDao = new EnvironmentTempDao();
		envTempDao.save(nodeTemp);
	}

	// //删除当前数据表
	public void deleteEnvironmentTempData(String nodeid, String sindex, String subentity) {
		EnvironmentTempDao envTempDao = new EnvironmentTempDao();
		envTempDao.deleteByNodeIdSindex(nodeid, sindex, subentity);
	}

	public void deleteEnvironmentTempData(String ip) {
		EnvironmentTempDao envTempDao = new EnvironmentTempDao();
		envTempDao.deleteByIp(ip);
	}

	public void deleteCpuTempData(String nodeid, String sid, String subentity) {
		CpuTempDao cpuTempDao = new CpuTempDao();
		cpuTempDao.deleteByNodeIdSindex(nodeid, sid, subentity);
	}

	public void deleteCpuTempData(String ip) {
		CpuTempDao cpuTempDao = new CpuTempDao();
		cpuTempDao.deleteByIp(ip);
	}

	public void deleteMemoryTempData(String nodeid, String sid, String subentity) {
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		memoryTempDao.deleteByNodeIdSindex(nodeid, sid, subentity);
	}

	public void deleteMemoryTempData(String ip) {
		MemoryTempDao memoryTempDao = new MemoryTempDao();
		memoryTempDao.deleteByIp(ip);
	}

	public void deleteFlashTempData(String ip) {
		FlashTempDao flashTempDao = new FlashTempDao();
		flashTempDao.deleteByIp(ip);
	}

	public void deleteFlashTempData(String nodeid, String sid, String subentity) {
		FlashTempDao flashTempDao = new FlashTempDao();
		flashTempDao.deleteByNodeIdSindex(nodeid, sid, subentity);
	}

	public void deleteInterfaceTempData(String nodeid, String sid, String subentity) {
		InterfaceTempDao intTempDao = new InterfaceTempDao();
		intTempDao.deleteByNodeIdSindex(nodeid, sid, subentity);
	}

	public void deleteInterfaceTempData(String ip) {
		InterfaceTempDao intTempDao = new InterfaceTempDao();
		intTempDao.deleteByIp(ip);
	}

	public void deleteArpTempData(String ip) {
		ArpTempDao arpTempDao = new ArpTempDao();
		arpTempDao.deleteByIp(ip);
	}

	public void deleteFdbTempData(String ip) {
		FdbTempDao fdbTempDao = new FdbTempDao();
		fdbTempDao.deleteByIp(ip);
	}

	public void deleteSystemTempData(String nodeid, String sid, String subentity) {
		SystemTempDao sysTempDao = new SystemTempDao();
		sysTempDao.deleteByNodeIdSindex(nodeid, sid, subentity);
	}

	public void deleteSystemTempData(String ip) {
		SystemTempDao sysTempDao = new SystemTempDao();
		sysTempDao.deleteByIp(ip);
	}

	public void deleteRouteTempData(String ip) {
		RouteTempDao routeTempDao = new RouteTempDao();
		routeTempDao.deleteByIp(ip);
	}

	public void deleteIpTempData(String ip) {
		IpTempDao ipTempDao = new IpTempDao();
		ipTempDao.deleteByIp(ip);
	}

	public void deleteLightTempData(String ip) {
		LightTempDao lightTempDao = new LightTempDao();
		lightTempDao.deleteByIp(ip);
	}

	public void deleteFbTempData(String ip) {
		FbconfigTempDao fbTempDao = new FbconfigTempDao();
		fbTempDao.deleteByIp(ip);
	}

	public void deleteChannelTempData(String ip) {
		ChannelTempDao chTempDao = new ChannelTempDao();
		chTempDao.deleteByIp(ip);
	}

	private void deleteProcessTempData(String nodeid, String sindex, String subentity) {
		ProcessTempDao processTempDao = new ProcessTempDao();
		processTempDao.deleteByNodeIdSindex(nodeid, sindex, subentity);
	}

	private void deleteProcessTempData(String ip) {
		ProcessTempDao processTempDao = new ProcessTempDao();
		processTempDao.deleteByIp(ip);
	}

	private void deleteDiskTempData(String nodeid, String sindex, String subentity) {
		DiskTempDao diskTempDao = new DiskTempDao();
		diskTempDao.deleteByNodeIdSindex(nodeid, sindex, subentity);
	}

	private void deleteDiskTempData(String ip) {
		DiskTempDao diskTempDao = new DiskTempDao();
		diskTempDao.deleteByIp(ip);
	}

	public void deleteSoftWareTempData(String ip) {
		SoftwareTempDao softwareTempDao = new SoftwareTempDao();
		softwareTempDao.deleteByIp(ip);
	}

	public void deleteServiceTempData(String ip) {
		ServiceTempDao serviceTempDao = new ServiceTempDao();
		serviceTempDao.deleteByIp(ip);
	}

	public void deleteDeviceTempData(String ip) {
		DeviceTempDao deviceTempDao = new DeviceTempDao();
		deviceTempDao.deleteByIp(ip);
	}

	public void deleteStorageTempData(String ip) {
		StorageTempDao storageTempDao = new StorageTempDao();
		storageTempDao.deleteByIp(ip);
	}

	public void deleteUserTempData(String ip) {
		UserTempDao userTempDao = new UserTempDao();
		userTempDao.deleteByIp(ip);
	}

	public void deleteDiskPerfTempData(String ip) {
		DiskPerfTempDao diskPerfTempDao = new DiskPerfTempDao();
		diskPerfTempDao.deleteByIp(ip);
	}

	public void deleteCpuPerfTempData(String ip, String entity) {

	}

	public void deleteOtherTempData(String ip, String entity) {
		OthersTempDao othersTempDao = new OthersTempDao();
		othersTempDao.deleteByIpEntity(ip, entity);
	}

	public void deleteOtherTempData(String ip) {
		OthersTempDao othersTempDao = new OthersTempDao();
		othersTempDao.deleteByIp(ip);
	}
}
