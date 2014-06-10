package com.afunms.polling.api;

import java.util.Hashtable;

import com.afunms.system.model.User;

@SuppressWarnings("unchecked")
public interface I_HostCollectDataHour {
	public boolean schemeTask();

	public String[] gethourHis(String ip, String category, String entity, String subentity, String starttime, String totime) throws Exception;

	public Hashtable gethourHis1(String ip, String category, String entity, String subentity, String starttime, String totime) throws Exception;

	public Hashtable getmultiHis(String ip, String category, String starttime, String totime) throws Exception;

	public Hashtable getmultiHis(String ip, String category, String subenity, String[] bandkey, String[] bandch, String starttime, String totime) throws Exception;

	public Hashtable getmultiHis(String ip, String category, String subenity, String[] bandkey, String[] bandch, String starttime, String totime, String tablename)
			throws Exception;

	public Hashtable[] getMemory_month(String ip, String category, String starttime, String endtime) throws Exception;

	public boolean hostreportAll(User user);

	public boolean netreportAll(User user);
}
