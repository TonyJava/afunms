/**
 * <p>Description:������ָ��(db)</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-06
 */

package com.afunms.monitor.item;

import com.afunms.initialize.ResourceCenter;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.topology.model.NodeMonitor;

public class ServiceItem extends MonitoredItem {
	private static final long serialVersionUID = 827391995595L;
	private int[] servicesStatus; // ÿ�������״̬1=����,0=������

	public ServiceItem() {
		int len = ResourceCenter.getInstance().getServiceList().size();
		servicesStatus = new int[len];
	}

	public int[] getServicesStatus() {
		return servicesStatus;
	}

	@Override
	public void loadSelf(NodeMonitor nm) {
		loadCommon(nm);
	}

	public void setServicesStatus(int[] servicesStatus) {
		this.servicesStatus = servicesStatus;
	}
}