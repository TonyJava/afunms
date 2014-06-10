package com.afunms.application.ajaxManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.dao.AlarmWayDetailDao;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.alarm.util.AlarmWayUtil;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.base.DaoInterface;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.afunms.topology.util.KeyGenerator;

public class AlarmWayAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {

		if (action.equals("getAlarmWayList")) {
			getAlarmWayList();
		} else if (action.equals("deleteAlarmWayConfig")) {
			deleteAlarmWayConfig();
		} else if (action.equals("choseUser")) {
			choseUser();
		} else if (action.equals("addAlarmWay")) {
			addAlarmWay();
		} else if (action.equals("beforeEditAlarmWay")) {
			beforeEditAlarmWay();
		} else if (action.equals("editAlarmWay")) {
			editAlarmWay();
		}
	}

	private void editAlarmWay() {
		StringBuffer sb = new StringBuffer("操作");
		AlarmWay alarmWay = new AlarmWay();
		alarmWay.setId(getParaIntValue("alarmWayId"));
		alarmWay.setName(getParaValue("name"));
		alarmWay.setDescription(getParaValue("remark"));
		alarmWay.setIsDefault(getParaValue("isDefault"));
		alarmWay.setIsPageAlarm(getParaValue("isSystem"));
		alarmWay.setIsSoundAlarm(checkValue(getParaValue("isSound")));
		alarmWay.setIsSMSAlarm(checkValue(getParaValue("isSms")));
		alarmWay.setIsMailAlarm(checkValue(getParaValue("isMail")));
		alarmWay.setIsPhoneAlarm("0");
		alarmWay.setIsDesktopAlarm("0");

		JSONArray mailConfigJsonArray = JSONArray.fromObject(getParaValue("mailConfigJson"));
		JSONArray smsConfigJsonArray = JSONArray.fromObject(getParaValue("smsConfigJson"));
		JSONArray soundConfigJsonArray = JSONArray.fromObject(getParaValue("soundConfigJson"));
		JSONObject obj = null;
		List alarmWayDetailList = new ArrayList();
		AlarmWayDetail alarmWayDetail = null;
		for (int i = 0; i < mailConfigJsonArray.size(); i++) {
			obj = (JSONObject) mailConfigJsonArray.get(i);
			alarmWayDetail = new AlarmWayDetail();
			alarmWayDetail.setAlarmCategory("mail");
			alarmWayDetail.setDateType(obj.get("dateType").toString());
			alarmWayDetail.setSendTimes(obj.get("times").toString());
			alarmWayDetail.setStartDate(obj.get("startDate").toString());
			alarmWayDetail.setEndDate(obj.get("endDate").toString());
			alarmWayDetail.setStartTime(obj.get("startTime").toString());
			alarmWayDetail.setEndTime(obj.get("endTime").toString());
			alarmWayDetail.setUserIds(obj.get("userId").toString());
			alarmWayDetailList.add(alarmWayDetail);
		}

		for (int i = 0; i < smsConfigJsonArray.size(); i++) {
			obj = (JSONObject) smsConfigJsonArray.get(i);
			alarmWayDetail = new AlarmWayDetail();
			alarmWayDetail.setAlarmCategory("sms");
			alarmWayDetail.setDateType(obj.get("dateType").toString());
			alarmWayDetail.setSendTimes(obj.get("times").toString());
			alarmWayDetail.setStartDate(obj.get("startDate").toString());
			alarmWayDetail.setEndDate(obj.get("endDate").toString());
			alarmWayDetail.setStartTime(obj.get("startTime").toString());
			alarmWayDetail.setEndTime(obj.get("endTime").toString());
			alarmWayDetail.setUserIds(obj.get("userId").toString());
			alarmWayDetailList.add(alarmWayDetail);
		}

		for (int i = 0; i < soundConfigJsonArray.size(); i++) {
			obj = (JSONObject) soundConfigJsonArray.get(i);
			alarmWayDetail = new AlarmWayDetail();
			alarmWayDetail.setAlarmCategory("sound");
			alarmWayDetail.setDateType(obj.get("dateType").toString());
			alarmWayDetail.setSendTimes(obj.get("times").toString());
			alarmWayDetail.setStartDate(obj.get("startDate").toString());
			alarmWayDetail.setEndDate(obj.get("endDate").toString());
			alarmWayDetail.setStartTime(obj.get("startTime").toString());
			alarmWayDetail.setEndTime(obj.get("endTime").toString());
			alarmWayDetail.setUserIds(obj.get("userId").toString());
			alarmWayDetailList.add(alarmWayDetail);
		}
		boolean result = false;
		AlarmWayUtil alarmWayUtil = new AlarmWayUtil();
		try {
			result = alarmWayUtil.updateAlarmWay(alarmWay);
			if (result) {
				alarmWayUtil.saveAlarmWayDetail(alarmWay, alarmWayDetailList);
				sb.append("成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("失败");
		}
		out.print(sb.toString());
		out.flush();

	}

	private void beforeEditAlarmWay() {
		UserDao userDao = new UserDao();
		User userVo = null;
		List userList = new ArrayList();
		Hashtable<String, String> userHt = new Hashtable<String, String>();
		try {
			userList = userDao.loadAll();
			if (null != userList && userList.size() > 0) {
				for (int i = 0; i < userList.size(); i++) {
					userVo = (User) userList.get(i);
					userHt.put(String.valueOf(userVo.getId()), userVo.getUserid());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != userDao) {
				userDao.close();
			}
		}
		String id = getParaValue("id");
		AlarmWay alarmWay = null;
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			alarmWay = (AlarmWay) alarmWayDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmWayDao.close();
		}
		StringBuffer jsonString = new StringBuffer("[{");
		if (null != alarmWay) {
			jsonString.append("alarmWay:[");
			jsonString.append("{\"alarmWayId\":\"");
			jsonString.append(alarmWay.getId());
			jsonString.append("\",");

			jsonString.append("\"name\":\"");
			jsonString.append(alarmWay.getName());
			jsonString.append("\",");

			jsonString.append("\"remark\":\"");
			jsonString.append(alarmWay.getDescription());
			jsonString.append("\",");

			jsonString.append("\"isDefault\":\"");
			jsonString.append(alarmWay.getIsDefault());
			jsonString.append("\",");

			jsonString.append("\"isSystem\":\"");
			jsonString.append(alarmWay.getIsPageAlarm());
			jsonString.append("\",");

			jsonString.append("\"isSound\":\"");
			jsonString.append(alarmWay.getIsSoundAlarm());
			jsonString.append("\",");

			jsonString.append("\"isMail\":\"");
			jsonString.append(alarmWay.getIsMailAlarm());
			jsonString.append("\",");

			jsonString.append("\"isSms\":\"");
			jsonString.append(alarmWay.getIsSMSAlarm());
			jsonString.append("\"}]");
		}
		List alarmWayDetailList = new ArrayList();
		AlarmWayDetail vo = null;
		AlarmWayDetailDao alarmWayDetailDao = new AlarmWayDetailDao();
		try {
			alarmWayDetailList = alarmWayDetailDao.findByAlarmWayId(id);
			if (null != alarmWayDetailList && alarmWayDetailList.size() > 0) {
				String tempString = null;
				StringBuffer mailConfigJson = new StringBuffer("mailConfigJson:[{Rows:[");
				StringBuffer smsConfigJson = new StringBuffer("smsConfigJson:[{Rows:[");
				StringBuffer soundConfigJson = new StringBuffer("soundConfigJson:[{Rows:[");
				for (int i = 0; i < alarmWayDetailList.size(); i++) {
					vo = (AlarmWayDetail) alarmWayDetailList.get(i);
					if (vo.getAlarmCategory().equals("mail")) {
						mailConfigJson.append(assembString(vo, userHt));
					} else if (vo.getAlarmCategory().equals("sms")) {
						smsConfigJson.append(assembString(vo, userHt));
					} else if (vo.getAlarmCategory().equals("sound")) {
						soundConfigJson.append(assembString(vo, userHt));
					}
				}
				jsonString.append(",");
				jsonString.append(removeLastComma(mailConfigJson));
				jsonString.append("]}]");

				jsonString.append(",");
				jsonString.append(removeLastComma(smsConfigJson));
				jsonString.append("]}]");

				jsonString.append(",");
				jsonString.append(removeLastComma(soundConfigJson));
				jsonString.append("]}]");
			}
			jsonString.append("}]");
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			alarmWayDetailDao.close();
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private String removeLastComma(StringBuffer sb) {
		String rt = null;
		if (sb.indexOf(",") > 0) {
			rt = sb.substring(0, sb.length() - 1);
		} else {
			rt = sb.toString();
		}
		return rt;
	}

	private StringBuffer assembString(AlarmWayDetail vo, Hashtable<String, String> userHt) {
		String userIdString = null;
		String userNameString = "";
		String[] userIdArray = null;
		StringBuffer sb = new StringBuffer();
		sb.append("{\"dateType\":\"");
		sb.append(vo.getDateType());
		sb.append("\",");

		sb.append("\"times\":\"");
		sb.append(vo.getSendTimes());
		sb.append("\",");

		sb.append("\"startDate\":\"");
		sb.append(vo.getStartDate());
		sb.append("\",");

		sb.append("\"endDate\":\"");
		sb.append(vo.getEndDate());
		sb.append("\",");

		sb.append("\"startTime\":\"");
		sb.append(vo.getStartTime());
		sb.append("\",");

		sb.append("\"endTime\":\"");
		sb.append(vo.getEndTime());
		sb.append("\",");

		sb.append("\"userId\":\"");
		sb.append(vo.getUserIds());
		sb.append("\",");

		userIdString = vo.getUserIds();
		userIdArray = userIdString.split(",");
		if (null != userIdArray && userIdArray.length > 0) {
			for (int c = 0; c < userIdArray.length; c++) {
				userNameString += userHt.get(userIdArray[c]) + ",";
			}
		}
		sb.append("\"userName\":\"");
		sb.append(userNameString);
		sb.append("\"},");
		return sb;
	}

	private void addAlarmWay() {
		StringBuffer sb = new StringBuffer("操作");
		AlarmWay alarmWay = new AlarmWay();
		alarmWay.setId(KeyGenerator.getInstance().getNextKey());
		alarmWay.setName(getParaValue("name"));
		alarmWay.setDescription(getParaValue("remark"));
		alarmWay.setIsDefault(getParaValue("isDefault"));
		alarmWay.setIsPageAlarm(getParaValue("isSystem"));
		alarmWay.setIsSoundAlarm(checkValue(getParaValue("isSound")));
		alarmWay.setIsSMSAlarm(checkValue(getParaValue("isSms")));
		alarmWay.setIsMailAlarm(checkValue(getParaValue("isMail")));
		alarmWay.setIsPhoneAlarm("0");
		alarmWay.setIsDesktopAlarm("0");

		JSONArray mailConfigJsonArray = JSONArray.fromObject(getParaValue("mailConfigJson"));
		JSONArray smsConfigJsonArray = JSONArray.fromObject(getParaValue("smsConfigJson"));
		JSONArray soundConfigJsonArray = JSONArray.fromObject(getParaValue("soundConfigJson"));
		JSONObject obj = null;
		List alarmWayDetailList = new ArrayList();
		AlarmWayDetail alarmWayDetail = null;
		for (int i = 0; i < mailConfigJsonArray.size(); i++) {
			obj = (JSONObject) mailConfigJsonArray.get(i);
			alarmWayDetail = new AlarmWayDetail();
			alarmWayDetail.setAlarmCategory("mail");
			alarmWayDetail.setDateType(obj.get("dateType").toString());
			alarmWayDetail.setSendTimes(obj.get("times").toString());
			alarmWayDetail.setStartDate(obj.get("startDate").toString());
			alarmWayDetail.setEndDate(obj.get("endDate").toString());
			alarmWayDetail.setStartTime(obj.get("startTime").toString());
			alarmWayDetail.setEndTime(obj.get("endTime").toString());
			alarmWayDetail.setUserIds(obj.get("userId").toString());
			alarmWayDetailList.add(alarmWayDetail);
		}

		for (int i = 0; i < smsConfigJsonArray.size(); i++) {
			obj = (JSONObject) smsConfigJsonArray.get(i);
			alarmWayDetail = new AlarmWayDetail();
			alarmWayDetail.setAlarmCategory("sms");
			alarmWayDetail.setDateType(obj.get("dateType").toString());
			alarmWayDetail.setSendTimes(obj.get("times").toString());
			alarmWayDetail.setStartDate(obj.get("startDate").toString());
			alarmWayDetail.setEndDate(obj.get("endDate").toString());
			alarmWayDetail.setStartTime(obj.get("startTime").toString());
			alarmWayDetail.setEndTime(obj.get("endTime").toString());
			alarmWayDetail.setUserIds(obj.get("userId").toString());
			alarmWayDetailList.add(alarmWayDetail);
		}

		for (int i = 0; i < soundConfigJsonArray.size(); i++) {
			obj = (JSONObject) soundConfigJsonArray.get(i);
			alarmWayDetail = new AlarmWayDetail();
			alarmWayDetail.setAlarmCategory("sound");
			alarmWayDetail.setDateType(obj.get("dateType").toString());
			alarmWayDetail.setSendTimes(obj.get("times").toString());
			alarmWayDetail.setStartDate(obj.get("startDate").toString());
			alarmWayDetail.setEndDate(obj.get("endDate").toString());
			alarmWayDetail.setStartTime(obj.get("startTime").toString());
			alarmWayDetail.setEndTime(obj.get("endTime").toString());
			alarmWayDetail.setUserIds(obj.get("userId").toString());
			alarmWayDetailList.add(alarmWayDetail);
		}
		boolean result = false;
		AlarmWayUtil alarmWayUtil = new AlarmWayUtil();
		try {
			result = alarmWayUtil.saveAlarmWay(alarmWay);
			if (result) {
				alarmWayUtil.saveAlarmWayDetail(alarmWay, alarmWayDetailList);
				sb.append("成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("失败");
		}
		out.print(sb.toString());
		out.flush();
	}

	private String checkValue(String arg) {
		String rt = "0";
		if (null != arg) {
			if (arg.equals("true")) {
				rt = "1";
			}
		}
		return rt;
	}

	private void choseUser() {
		DaoInterface dao = new UserDao();
		List allUserList = new ArrayList();
		try {
			allUserList = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		User vo = null;
		if (null != allUserList && allUserList.size() > 0) {
			for (int i = 0; i < allUserList.size(); i++) {
				vo = (User) allUserList.get(i);
				jsonString.append("{\"id\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"userId\":\"");
				jsonString.append(vo.getUserid());
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(vo.getName());
				jsonString.append("\",");

				jsonString.append("\"phone\":\"");
				jsonString.append(vo.getMobile());
				jsonString.append("\",");

				jsonString.append("\"mail\":\"");
				jsonString.append(vo.getEmail());
				jsonString.append("\"}");

				if (i != allUserList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + allUserList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteAlarmWayConfig() {
		StringBuffer jsonString = new StringBuffer("删除告警方式配置");
		boolean result = false;
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			result = alarmWayDao.delete(ids);
			if (result) {
				AlarmWayUtil alarmWayUtil = new AlarmWayUtil();
				alarmWayUtil.deleteAlarmWayDetail(ids);
				jsonString.append("成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonString.append("失败");
		} finally {
			alarmWayDao.close();
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private void getAlarmWayList() {
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		AlarmWay alarmWayVo = null;
		List alarmWayList = new ArrayList();
		try {
			alarmWayList = alarmWayDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != alarmWayList && alarmWayList.size() > 0) {
			for (int i = 0; i < alarmWayList.size(); i++) {
				alarmWayVo = (AlarmWay) alarmWayList.get(i);
				jsonString.append("{\"alarmWayId\":\"");
				jsonString.append(alarmWayVo.getId());
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(alarmWayVo.getName());
				jsonString.append("\",");

				jsonString.append("\"isDefault\":\"");
				jsonString.append(alarmWayVo.getIsDefault());
				jsonString.append("\",");

				jsonString.append("\"isSystem\":\"");
				jsonString.append(alarmWayVo.getIsPageAlarm());
				jsonString.append("\",");

				jsonString.append("\"isSound\":\"");
				jsonString.append(alarmWayVo.getIsSoundAlarm());
				jsonString.append("\",");

				jsonString.append("\"isSM\":\"");
				jsonString.append(alarmWayVo.getIsSMSAlarm());
				jsonString.append("\",");

				jsonString.append("\"remark\":\"");
				jsonString.append(alarmWayVo.getDescription());
				jsonString.append("\",");

				jsonString.append("\"isMail\":\"");
				jsonString.append(alarmWayVo.getIsMailAlarm());
				jsonString.append("\"}");

				if (i != alarmWayList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + alarmWayList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}
}
