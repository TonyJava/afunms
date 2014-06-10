
package com.afunms.detail.net.remote;

import java.util.List;

import com.afunms.detail.net.service.FibreNetService;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.reomte.model.FibreCapabilityInfo;
import com.afunms.detail.reomte.model.FibreConfigInfo;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.detail.reomte.model.LightInfo;
import com.afunms.detail.reomte.model.ProcessInfo;
import com.afunms.event.model.EventList;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.RouterNodeTemp;
import com.afunms.topology.model.HostNode;

/**
 * 此类用于网络设备详细信息页面远程调用
 */

public class FibreNetRemote extends NetRemote {

	@Override
	public List<EventList> getAlarmDetailInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String eventlocation,
			String subentity, String status) {
		return new FibreNetService(nodeid, type, subtype).getAlarmDetailInfo(startdateValue, todateValue, level1, eventlocation, subentity, status);
	}

	@Override
	public List<Object> getAlarmInfo(String nodeid, String type, String subtype, String startdateValue, String todateValue, String level1, String status) {
		return new FibreNetService(nodeid, type, subtype).getAlarmInfo(startdateValue, todateValue, level1, status);
	}

	@Override
	public String getCategoryInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getCategoryInfo();
	}

	@Override
	public String getCurrCpuAvgInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getCurrCpuAvgInfo();
	}

	@Override
	public String getCurrDayPingAvgInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getCurrDayPingAvgInfo();
	}

	public List<FibreCapabilityInfo> getFibreCapabilityInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getFibreCapabilityInfo();
	}

	public List<FibreConfigInfo> getFibreConfigInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getFibreConfigInfo();
	}

	@Override
	public HostNode getHostNodeInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getHostNode();
	}

	@Override
	public List<InterfaceInfo> getInterfaceInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getInterfaceInfo();
	}

	public List<LightInfo> getLightInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getLightInfo();
	}

	public List<ProcessInfo> getProcessInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getProcessInfo();
	}

	@Override
	public List<RouterNodeTemp> getRouterInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getRouterInfo();
	}

	@Override
	public String getStautsInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getStautsInfo();
	}

	@Override
	public String getSupperInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getSupperInfo();
	}

	@Override
	public List<NodeTemp> getSystemInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getSystemInfo();
	}

	@Override
	public List<DetailTabRemote> getTabInfo(String nodeid, String type, String subtype) {
		return new FibreNetService(nodeid, type, subtype).getTabInfo();
	}

}
