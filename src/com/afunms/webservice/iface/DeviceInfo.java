package com.afunms.webservice.iface;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.afunms.webservice.model.Attribute;
import com.afunms.webservice.model.AttributeValue;
import com.afunms.webservice.model.CiType;
import com.afunms.webservice.model.DeviceBaseInfo;
import com.afunms.webservice.model.DeviceHardwareInfo;
import com.afunms.webservice.model.DeviceInterfaceGroup;
import com.afunms.webservice.model.DeviceInterfaceInfo;

/**
 * @description webservice所发布的方法
 * @author wangxiangyong
 * @date Feb 4, 2012 9:29:07 AM
 */
@WebService
public interface DeviceInfo {
	/**
	 * 获取所有设备的基本信息（通用）
	 * 
	 * @return
	 */
	@WebMethod
	public List<DeviceBaseInfo> getAllDeviceBaseInfo();

	/**
	 * 根据IP获取设备的基本信息（通用）
	 * 
	 * @return
	 */
	@WebMethod
	public DeviceBaseInfo getDeviceBaseInfoByIp(@WebParam(name = "ip", targetNamespace = "http://iface.webservice.afunms.com/")
	String ip);

	/**
	 * 获取所有设备接口的基本信息（通用）
	 * 
	 * @return
	 */
	@WebMethod
	public List<DeviceInterfaceInfo> getAllDeviceInterfaceInfo();

	/**
	 * 根据IP获取设备接口的基本信息（通用）
	 * 
	 * @return
	 */
	@WebMethod
	public DeviceInterfaceGroup getDeviceInterfaceInfoByIp(@WebParam(name = "ip", targetNamespace = "http://iface.webservice.afunms.com/")
	String ip);

	/**
	 * 根据IP获取设备硬件的基本信息（通用）
	 * 
	 * @return
	 */
	@WebMethod
	public DeviceHardwareInfo getDeviceHardwareByIp(@WebParam(name = "ip", targetNamespace = "http://iface.webservice.afunms.com/")
	String ip);

	/**
	 * 根据类型码获取ID集合（标准）
	 * 
	 * @return
	 */
	@WebMethod
	public String[] getMoIds(@WebParam(name = "ciType", targetNamespace = "http://iface.webservice.afunms.com/")
	String ciType);

	/**
	 * 获取监控对象的配置指标值（标准）
	 * 
	 * @return
	 */
	@WebMethod
	public AttributeValue[] getMoAttributeValue(@WebParam(name = "attributes", targetNamespace = "http://iface.webservice.afunms.com/")
	Attribute[] attributes);

	/**
	 * 获取监控对象的指标值（拓展）
	 * 
	 * @return
	 */
	@WebMethod
	public CiType[] getMoAttributeName();

}
