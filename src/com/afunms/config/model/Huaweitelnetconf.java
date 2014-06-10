package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

/**
 * 
 * topo_node_telnetconfig ���Ӧ��model
 * 
 * @author konglq
 * 
 */
public class Huaweitelnetconf extends BaseVo {
	private int id;// id
	private String ipaddress;// ip��ַ
	private String user;// �û�
	private String password;// ����
	private String suuser;// su�û�
	private String supassword;// su����
	private int port;// �˿�
	private String defaultpromtp;// ϵͳ��ʾ����
	private int enablevpn; // �Ƿ�����vpn������Ϣ�ɼ�
	private int isSynchronized;// �Ƿ�ͬ�� 1ͬ�� 0��ͬ��
	private String deviceRender;// �豸�ṩ�� h3c cisco
	private String threeA;// 3A��֤
	private int encrypt;// �Ƿ���� 1���� 2������
	private String ostype;
	private int connecttype; // ��½��ʽ 0:telnet 1:ssh

	public int getConnecttype() {
		return connecttype;
	}

	public String getDefaultpromtp() {
		return defaultpromtp;
	}

	public String getDeviceRender() {
		return deviceRender;
	}

	public int getEnablevpn() {
		return enablevpn;
	}

	public int getEncrypt() {
		return encrypt;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public int getIsSynchronized() {
		return isSynchronized;
	}

	public String getOstype() {
		return ostype;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public String getSupassword() {
		return supassword;
	}

	public String getSuuser() {
		return suuser;
	}

	public String getThreeA() {
		return threeA;
	}

	public String getUser() {
		return user;
	}

	public void setConnecttype(int connecttype) {
		this.connecttype = connecttype;
	}

	public void setDefaultpromtp(String defaultpromtp) {
		this.defaultpromtp = defaultpromtp;
	}

	public void setDeviceRender(String deviceRender) {
		this.deviceRender = deviceRender;
	}

	public void setEnablevpn(int enablevpn) {
		this.enablevpn = enablevpn;
	}

	public void setEncrypt(int encrypt) {
		this.encrypt = encrypt;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setIsSynchronized(int isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSupassword(String supassword) {
		this.supassword = supassword;
	}

	public void setSuuser(String suuser) {
		this.suuser = suuser;
	}

	public void setThreeA(String threeA) {
		this.threeA = threeA;
	}

	public void setUser(String user) {
		this.user = user;
	}
}