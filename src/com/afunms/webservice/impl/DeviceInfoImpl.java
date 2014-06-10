package com.afunms.webservice.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.jws.WebService;

import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.webservice.dao.CiTypeDao;
import com.afunms.webservice.dao.RelationDao;
import com.afunms.webservice.iface.DeviceInfo;
import com.afunms.webservice.model.Attribute;
import com.afunms.webservice.model.AttributeValue;
import com.afunms.webservice.model.CiType;
import com.afunms.webservice.model.DeviceBaseInfo;
import com.afunms.webservice.model.DeviceHardwareInfo;
import com.afunms.webservice.model.DeviceInterfaceGroup;
import com.afunms.webservice.model.DeviceInterfaceInfo;
import com.afunms.webservice.model.MoAndCiRelation;

/**
 * @description 接口的具体实现类
 * @author wangxiangyong
 * @date Feb 4, 2012 9:28:06 AM
 */

@SuppressWarnings("unchecked")
@WebService(endpointInterface = "com.afunms.webservice.iface.DeviceInfo")
public class DeviceInfoImpl implements DeviceInfo {
	/**
	 * 获取所有设备的基本信息(通用方法)
	 * 
	 * @return
	 */
	public List<DeviceBaseInfo> getAllDeviceBaseInfo() {
		List<DeviceBaseInfo> deviceInfoList = new ArrayList<DeviceBaseInfo>();
		HostNodeDao dao = new HostNodeDao();
		List<HostNode> list = null;
		try {
			list = dao.loadall();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		DeviceBaseInfo deviceInfo = null;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				HostNode node = list.get(i);
				deviceInfo = new DeviceBaseInfo();
				deviceInfo.setId(node.getId() + "");
				deviceInfo.setIpAddress(node.getIpAddress());
				deviceInfo.setSysName(node.getSysName());
				deviceInfo.setAlias(node.getAlias());
				deviceInfo.setNetMask(node.getNetMask());
				deviceInfo.setSysDescr(node.getSysDescr());
				deviceInfo.setSysOid(node.getSysOid());
				deviceInfo.setCategory(node.getCategory() + "");
				deviceInfo.setOsType(node.getOstype() + "");
				deviceInfo.setStatus(node.getStatus() + "");
				deviceInfoList.add(deviceInfo);
			}
		}
		return deviceInfoList;
	}

	/**
	 * 根据IP获取设备的基本信息(通用方法)
	 * 
	 * @return
	 */
	public DeviceBaseInfo getDeviceBaseInfoByIp(String ip) {
		HostNodeDao dao = new HostNodeDao();
		DeviceBaseInfo deviceInfo = null;
		try {
			HostNode node = (HostNode) dao.findByIpaddress(ip);

			if (node != null) {
				deviceInfo = new DeviceBaseInfo();
				deviceInfo.setId(node.getId() + "");
				deviceInfo.setIpAddress(node.getIpAddress());
				deviceInfo.setSysName(node.getSysName());
				deviceInfo.setAlias(new String(node.getAlias()));
				deviceInfo.setNetMask(node.getNetMask());
				deviceInfo.setSysDescr(node.getSysDescr());
				deviceInfo.setSysOid(node.getSysOid());
				deviceInfo.setCategory(node.getCategory() + "");
				deviceInfo.setOsType(node.getOstype() + "");
				deviceInfo.setStatus(node.getStatus() + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return deviceInfo;
	}

	/**
	 * 获取所有设备接口的基本信息(通用方法)
	 * 
	 * @return
	 */
	public List<DeviceInterfaceInfo> getAllDeviceInterfaceInfo() {
		return null;
	}

	/**
	 * 根据IP获取设备接口的基本信息(通用方法)
	 * 
	 * @return
	 */
	public DeviceInterfaceGroup getDeviceInterfaceInfoByIp(String ip) {
		DeviceInfoImplHelper helper = new DeviceInfoImplHelper();
		DeviceInterfaceGroup group = helper.getDeviceInterface(ip);
		return group;
	}

	/**
	 * 根据IP获取设备硬件的基本信息(通用方法)
	 * 
	 * @return
	 */
	public DeviceHardwareInfo getDeviceHardwareByIp(String ip) {
		DeviceInfoImplHelper helper = new DeviceInfoImplHelper();
		DeviceHardwareInfo hardwareInfo = helper.getDeviceHardwareInfo(ip);
		return hardwareInfo;
	}

	/**
	 * 获取配置项ID列表方法（标准方法）
	 */

	/**
	 * 获取监控对象的指标值（标准方法）
	 * 
	 */
	public AttributeValue[] getMoAttributeValue(Attribute[] attributes) {
		Vector<String> vector = new Vector<String>();
		AttributeValue[] values = null;
		Hashtable<Integer, String> moIdTable = new Hashtable<Integer, String>();
		if (attributes != null && attributes.length > 0) {
			List<MoAndCiRelation> list = null;
			RelationDao relationDao = new RelationDao();
			try {
				list = relationDao.loadAll();
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						MoAndCiRelation relation = list.get(i);
						if (relation != null) {
							moIdTable.put(relation.getMoId(), relation.getCiType());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				relationDao.close();
			}
			values = new AttributeValue[attributes.length];
			for (int i = 0; i < attributes.length; i++) {
				Attribute attribute = attributes[i];
				if (attribute == null)
					continue;
				if (!vector.contains(attribute.getMoId())) {
					vector.add(attribute.getMoId());
				}
			}
			DeviceInfoImplHelper helper = new DeviceInfoImplHelper();
			Hashtable<String, String> hashTable = new Hashtable<String, String>();
			helper.getDeviceInterface(vector, hashTable);// 查询接口信息，并放入集合对象中
			helper.getAllDeviceBaseInfo(hashTable);// 获取设备的基本信息，并放入集合对象中
			helper.getHardwareInfo(vector, hashTable);// 获取硬件信息，并放入集合对象中
			helper.getOsInfo(vector, hashTable); // 获取操作系统信息，并放入集合对象中
			helper.getSoftwareInfo(vector, hashTable);// 获取软件信息，并放入集合对象中
			helper.getAllDbInfo(hashTable);// 获取所有数据库的信息，并放入集合对象中
			helper.getAllTomcatInfo(hashTable);// 获取tomcat节点信息,并放入集合对象中
			helper.getAllMQInfo(hashTable);// 获取MQ节点信息,并放入集合对象中
			AttributeValue value = null;
			for (int i = 0; i < attributes.length; i++) {
				Attribute attribute = attributes[i];
				value = new AttributeValue();
				String key = attribute.getMoId() + "_" + attribute.getAttributeName();

				if (hashTable.containsKey(key)) {
					value.setAttributeName(attribute.getAttributeName());
					value.setMoid(attribute.getMoId() + "");
					value.setValue(hashTable.get(key));
				}
				values[i] = value;

			}

		}

		return values;
	}

	/**
	 * 这是一个拓展的webservice接口方法，将返回自定义类型码、属性名等信息
	 * 
	 */
	public CiType[] getMoAttributeName() {
		List<CiType> list = null;
		CiTypeDao dao = new CiTypeDao();

		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

		CiType[] types = null;
		if (list != null && list.size() > 0) {
			types = new CiType[list.size()];
			for (int i = 0; i < list.size(); i++) {
				types[i] = list.get(i);
			}
		}
		return types;
	}

	public String[] getMoIds(String ciType) {
		DeviceInfoImplHelper helper = new DeviceInfoImplHelper();
		helper.synchronousData();// 同步
		RelationDao dao = new RelationDao();
		if (ciType == null) {
			ciType = "";
		}
		String[] moIds = null;
		try {
			moIds = dao.getMoIdsByCiType(ciType);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return moIds;
	}

}
