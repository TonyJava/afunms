
package com.afunms.detail.net.remote;

import java.util.List;

import com.afunms.config.model.IpAlias;
import com.afunms.detail.net.service.NetService;
import com.afunms.detail.reomte.DetailReomte;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.event.model.EventList;
import com.afunms.polling.om.IpMac;
import com.afunms.temp.model.FdbNodeTemp;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.RouterNodeTemp;
import com.afunms.topology.model.HostNode;

/**
 * 此类用于网络设备详细信息页面远程调用
 */

public class NetRemote extends DetailReomte {

	public List<EventList> getAlarmDetailInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String eventlocation,
			String subentity, String status) {
		return new NetService(nodeid, type, subtype).getAlarmDetailInfo(startdateValue, todateValue, level1, eventlocation, subentity, status);
	}

	public List<Object> getAlarmInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String status) {
		return new NetService(nodeid, type, subtype).getAlarmInfo(startdateValue, todateValue, level1, status);
	}

	public List<IpMac> getARPInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getARPInfo();
	}

	public String getCategoryInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getCategoryInfo();
	}

	public String getCurrCpuAvgInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getCurrCpuAvgInfo();
	}

	public String getCurrDayPingAvgInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getCurrDayPingAvgInfo();
	}

	public List<FdbNodeTemp> getFDBInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getFDBInfo();
	}

	public HostNode getHostNodeInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getHostNode();
	}

	public List<InterfaceInfo> getInterfaceInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getInterfaceInfo();
	}

	public List<IpAlias> getIpListInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getIpListInfo();
	}

	public List<RouterNodeTemp> getRouterInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getRouterInfo();
	}

	public String getStautsInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getStautsInfo();
	}

	public String getSupperInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getSupperInfo();
	}

	public List<NodeTemp> getSystemInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getSystemInfo();
	}

	public List<DetailTabRemote> getTabInfo(String nodeid, String type, String subtype) {
		return new NetService(nodeid, type, subtype).getTabInfo();
	}

}
