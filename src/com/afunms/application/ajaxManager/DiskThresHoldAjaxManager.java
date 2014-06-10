package com.afunms.application.ajaxManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.DiskConfigDao;
import com.afunms.config.model.Diskconfig;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class DiskThresHoldAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {
		if (action.equals("getDiskThresHoldList")) {
			getDiskThresHoldList();
		} else if (action.equals("deleteDiskThresHolds")) {
			deleteDiskThresHolds();
		} else if (action.equals("beforeEditDiskThresHold")) {
			beforeEditDiskThresHold();
		} else if (action.equals("editDiskThresHold")) {
			editDiskThresHold();
		} else if (action.equals("batchEable")) {
			batchEable();
		} else if (action.equals("batchDisable")) {
			batchDisable();
		} else if (action.equals("batchReport")) {
			batchReport();
		} else if (action.equals("batchDisReport")) {
			batchDisReport();
		}
	}

	private void batchEable() {
		StringBuffer sb = new StringBuffer("启用");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update nms_diskconfig set monflag=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void batchDisable() {
		StringBuffer sb = new StringBuffer("禁用");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update nms_diskconfig set monflag=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void batchReport() {
		StringBuffer sb = new StringBuffer("启用显示报表");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update nms_diskconfig set reportflag=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void batchDisReport() {
		StringBuffer sb = new StringBuffer("显示报表禁用");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update nms_diskconfig set reportflag=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void editDiskThresHold() {
		Diskconfig vo = new Diskconfig();
		DiskConfigDao dao = new DiskConfigDao();
		StringBuffer jsonString = new StringBuffer("修改");
		try {
			int id = getParaIntValue("diskThresHoldId");
			if (id != -1) {
				vo = dao.loadDiskconfig(id);
			}
			vo.setMonflag(getParaIntValue("isA"));
			vo.setReportflag(getParaIntValue("isRPT"));
			vo.setBak(getParaValue("remark"));

			vo.setLimenvalue(getParaIntValue("firstLevelValue"));
			vo.setSms1(getParaIntValue("firstIsSM"));

			vo.setLimenvalue1(getParaIntValue("secondLevelValue"));
			vo.setSms2(getParaIntValue("secondIsSM"));

			vo.setLimenvalue2(getParaIntValue("thirdLevelValue"));
			vo.setSms3(getParaIntValue("thirdIsSM"));

			dao = new DiskConfigDao();
			dao.update(vo);
			jsonString.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			jsonString.append("失败");
		} finally {
			dao.close();
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private void beforeEditDiskThresHold() {
		String diskThresHoldId = getParaValue("diskThresHoldId");

		DiskConfigDao diskConfigDao = new DiskConfigDao();
		Diskconfig vo = null;

		try {
			vo = (Diskconfig) diskConfigDao.findByID(diskThresHoldId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			diskConfigDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"diskThresHoldId\":\"");
		jsonString.append(vo.getId());
		jsonString.append("\",");

		jsonString.append("\"ip\":\"");
		jsonString.append(vo.getIpaddress());
		jsonString.append("\",");

		jsonString.append("\"diskName\":\"");
		jsonString.append(vo.getName());
		jsonString.append("\",");

		jsonString.append("\"isA\":\"");
		jsonString.append(vo.getMonflag());
		jsonString.append("\",");

		jsonString.append("\"isRPT\":\"");
		jsonString.append(vo.getReportflag());
		jsonString.append("\",");

		jsonString.append("\"firstLevelValue\":\"");
		jsonString.append(vo.getLimenvalue());
		jsonString.append("\",");

		jsonString.append("\"firstIsSM\":\"");
		jsonString.append(vo.getSms1());
		jsonString.append("\",");

		jsonString.append("\"secondLevelValue\":\"");
		jsonString.append(vo.getLimenvalue1());
		jsonString.append("\",");

		jsonString.append("\"secondIsSM\":\"");
		jsonString.append(vo.getSms2());
		jsonString.append("\",");

		jsonString.append("\"thirdLevelValue\":\"");
		jsonString.append(vo.getLimenvalue2());
		jsonString.append("\",");

		jsonString.append("\"thirdIsSM\":\"");
		jsonString.append(vo.getSms3());
		jsonString.append("\",");

		jsonString.append("\"remark\":\"");
		jsonString.append(vo.getBak());
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteDiskThresHolds() {
		StringBuffer jsonString = new StringBuffer("删除");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DiskConfigDao diskConfigDao = new DiskConfigDao();
		try {
			diskConfigDao.delete(ids);
			jsonString.append("成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			jsonString.append("失败");
		} finally {
			diskConfigDao.close();
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private void getDiskThresHoldList() {
		// 初始化
		InitializableDiskThresHold();
		DiskConfigDao diskConfigDao = new DiskConfigDao();
		HostNodeDao nodeDao = new HostNodeDao();
		List diskThresHoldList = new ArrayList();
		List nodeList = new ArrayList();
		try {
			diskThresHoldList = diskConfigDao.loadAll();
			nodeList = nodeDao.loadall();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != diskConfigDao) {
				diskConfigDao.close();
			}
		}
		Hashtable<String, HostNode> nodeHt = new Hashtable<String, HostNode>();
		HostNode nodeVo = null;
		if (null != nodeList && nodeList.size() > 0) {
			for (int i = 0; i < nodeList.size(); i++) {
				nodeVo = (HostNode) nodeList.get(i);
				nodeHt.put(nodeVo.getIpAddress(), nodeVo);
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != diskThresHoldList && diskThresHoldList.size() > 0) {
			Diskconfig vo = null;
			for (int i = 0; i < diskThresHoldList.size(); i++) {
				vo = (Diskconfig) diskThresHoldList.get(i);
				jsonString.append("{\"diskThresHoldId\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				jsonString.append(vo.getIpaddress());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				if (null != nodeHt.get(vo.getIpaddress())) {
					jsonString.append(nodeHt.get(vo.getIpaddress()).getAlias());
				} else {
					jsonString.append("未知");
				}
				jsonString.append("\",");

				jsonString.append("\"diskName\":\"");
				jsonString.append(vo.getName());
				jsonString.append("\",");

				jsonString.append("\"isA\":\"");
				jsonString.append(vo.getMonflag());
				jsonString.append("\",");

				jsonString.append("\"isRPT\":\"");
				jsonString.append(vo.getReportflag());
				jsonString.append("\",");

				jsonString.append("\"firstLevelValue\":\"");
				jsonString.append(vo.getLimenvalue());
				jsonString.append("\",");

				jsonString.append("\"firstIsSM\":\"");
				jsonString.append(vo.getSms1());
				jsonString.append("\",");

				jsonString.append("\"secondLevelValue\":\"");
				jsonString.append(vo.getLimenvalue1());
				jsonString.append("\",");

				jsonString.append("\"secondIsSM\":\"");
				jsonString.append(vo.getSms2());
				jsonString.append("\",");

				jsonString.append("\"thirdLevelValue\":\"");
				jsonString.append(vo.getLimenvalue2());
				jsonString.append("\",");

				jsonString.append("\"thirdIsSM\":\"");
				jsonString.append(vo.getSms3());
				jsonString.append("\",");

				jsonString.append("\"remark\":\"");
				jsonString.append(vo.getBak());
				jsonString.append("\"}");

				if (i != diskThresHoldList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + diskThresHoldList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void InitializableDiskThresHold() {
		DiskConfigDao dao = new DiskConfigDao();
		try {
			dao.fromLastToDiskconfig();
			dao = new DiskConfigDao();
			Hashtable allDiskAlarm = (Hashtable) dao.getByAlarmflag(new Integer(99));
			ShareData.setAlldiskalarmdata(allDiskAlarm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
	}
}
