package com.afunms.ip.stationtype.model;

import com.afunms.common.base.BaseVo;

public class ip_select extends BaseVo {

	private int id;
	private String name;// ��վ��
	private String ids;// �Ǹɽ����id,�ö��ŷָ�
	private String routeName;// ·��������
	private String routeType;// ·�����ͺ�
	private String s1Name;// ·����̨��
	private String s1Type;// ����������
	private String s2Type;// �������ͺ�
	private String s2Name;// ������̨��
	private String deviceType;// ���������豸����
	private String deviceCount;// ���������豸����̨��
	private String hasCabinet;// ���޻���0���ޣ�1����
	private String location;// �ص�
	private String debugUnit;// ���Ե�λ
	private String flag;// �Ƿ��Ѵ��� 0��δ����1���Ѵ���
	private String rFactory;
	private String s1Factory;
	private String s2Factory;

	public String getDebugUnit() {
		return debugUnit;
	}

	public String getDeviceCount() {
		return deviceCount;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public String getFlag() {
		return flag;
	}

	public String getHasCabinet() {
		return hasCabinet;
	}

	public int getId() {
		return id;
	}

	public String getIds() {
		return ids;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getRFactory() {
		return rFactory;
	}

	public String getRouteName() {
		return routeName;
	}

	public String getRouteType() {
		return routeType;
	}

	public String getS1Factory() {
		return s1Factory;
	}

	public String getS1Name() {
		return s1Name;
	}

	public String getS1Type() {
		return s1Type;
	}

	public String getS2Factory() {
		return s2Factory;
	}

	public String getS2Name() {
		return s2Name;
	}

	public String getS2Type() {
		return s2Type;
	}

	public void setDebugUnit(String debugUnit) {
		this.debugUnit = debugUnit;
	}

	public void setDeviceCount(String deviceCount) {
		this.deviceCount = deviceCount;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setHasCabinet(String hasCabinet) {
		this.hasCabinet = hasCabinet;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRFactory(String factory) {
		rFactory = factory;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public void setRouteType(String routeType) {
		this.routeType = routeType;
	}

	public void setS1Factory(String factory) {
		s1Factory = factory;
	}

	public void setS1Name(String name) {
		s1Name = name;
	}

	public void setS1Type(String type) {
		s1Type = type;
	}

	public void setS2Factory(String factory) {
		s2Factory = factory;
	}

	public void setS2Name(String name) {
		s2Name = name;
	}

	public void setS2Type(String type) {
		s2Type = type;
	}

}
