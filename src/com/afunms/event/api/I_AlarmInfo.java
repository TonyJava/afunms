package com.afunms.event.api;

import java.util.List;

import com.afunms.event.model.AlarmInfo;

@SuppressWarnings("unchecked")
public interface I_AlarmInfo {
	public boolean createAlarmInfo(AlarmInfo alarminfo) throws Exception;

	// ɾ��
	public boolean deleteIplimenconf(Integer[] allIplimenconfid) throws Exception;

	// ��ѯbysearchkey
	public List getBySearch(String searchfield, String searchkeyword) throws Exception;

	// ��ѯ
	public List getAll() throws Exception;

	public List getByTime(String starttime, String totime) throws Exception;
}