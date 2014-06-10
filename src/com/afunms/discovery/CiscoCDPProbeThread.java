/**
 * <p>Description:probe the router table</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.discovery;

import java.util.List;

import com.afunms.polling.task.ThreadPool;

public class CiscoCDPProbeThread extends BaseThread {
	private Host node;

	public CiscoCDPProbeThread(Host node) {
		this.node = node;
	}

	@SuppressWarnings( { "static-access", "unchecked" })
	public void run() {
		if (DiscoverEngine.getInstance().getStopStatus() == 1)
			return;
		// 找出合法的CDP列表
		List cdpList = node.getCdpList();

		if (cdpList == null || cdpList.size() == 0) {
			DiscoverEngine.getInstance().addDiscoverdcount();
			node.updateCount(1);
			setCompleted(true);
			return;
		}

		// 生成线程池
		ThreadPool threadPool = new ThreadPool(cdpList.size());
		for (int i = 0; i < cdpList.size(); i++) {
			try {
				CdpCachEntryInterface cdp = (CdpCachEntryInterface) cdpList.get(i);
				threadPool.runTask(CDPSubThread.createTask(cdp, node));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		threadPool.join();
		threadPool.close();
		threadPool = null;
		DiscoverEngine.getInstance().addDiscoverdcount();
		node.updateCount(1);
		setCompleted(true);
		return;
	}// end_run
}