package com.afunms.application.ajaxManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.DBManager;

public class TableSpaceThresHoldAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {
		if (action.equals("getTableSpaceThresHoldList")) {
			getTableSpaceThresHoldList();
		} else if (action.equals("beforeEditTableSpaceThresHold")) {
			beforeEditTableSpaceThresHold();
		} else if (action.equals("editTableSpaceThresHold")) {
			editTableSpaceThresHold();
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
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_oraspaceconf set sms=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("启用失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("启用成功");
		out.flush();
	}

	private void batchDisable() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_oraspaceconf set sms=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("禁用失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("禁用成功");
		out.flush();
	}

	private void batchReport() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_oraspaceconf set reportflag=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("启用显示报表失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("启用显示报表成功");
		out.flush();
	}

	private void batchDisReport() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_oraspaceconf set reportflag=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("显示报表禁用失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("显示报表禁用成功");
		out.flush();
	}

	private void editTableSpaceThresHold() {
		Oraspaceconfig vo = new Oraspaceconfig();
		OraspaceconfigDao dao = new OraspaceconfigDao();
		StringBuffer jsonString = new StringBuffer("修改");
		try {
			String id = getParaValue("tableSpaceThresHoldId");
			if (id != null) {
				vo = (Oraspaceconfig) dao.findByID(id);
			}
			vo.setSms(getParaIntValue("isA"));
			vo.setReportflag(getParaIntValue("isRPT"));
			vo.setBak(getParaValue("remark"));

			vo.setAlarmvalue(getParaIntValue("alarmValue"));

			dao = new OraspaceconfigDao();
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

	private void beforeEditTableSpaceThresHold() {
		String tableSpaceThresHoldId = getParaValue("tableSpaceThresHoldId");
		if (null == tableSpaceThresHoldId || "".equals(tableSpaceThresHoldId)) {
			return;
		}
		OraspaceconfigDao oraSpaceConfigDao = new OraspaceconfigDao();
		Oraspaceconfig vo = null;
		try {
			vo = (Oraspaceconfig) oraSpaceConfigDao.findByID(tableSpaceThresHoldId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			oraSpaceConfigDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"tableSpaceThresHoldId\":\"");
		jsonString.append(vo.getId());
		jsonString.append("\",");

		jsonString.append("\"tableSpaceName\":\"");
		jsonString.append(vo.getSpacename());
		jsonString.append("\",");

		jsonString.append("\"isA\":\"");
		jsonString.append(vo.getSms());
		jsonString.append("\",");

		jsonString.append("\"isRPT\":\"");
		jsonString.append(vo.getReportflag());
		jsonString.append("\",");

		jsonString.append("\"alarmValue\":\"");
		jsonString.append(vo.getAlarmvalue());
		jsonString.append("\",");

		jsonString.append("\"remark\":\"");
		jsonString.append(vo.getBak());
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getTableSpaceThresHoldList() {
		// 初始化
		InitializableTableSpaceThresHold();
		OraspaceconfigDao oraSpaceConfigDao = new OraspaceconfigDao();
		DBDao dao = new DBDao();
		List tableSpaceThresHoldList = new ArrayList();
		List oracleNodeList = new ArrayList();
		try {
			tableSpaceThresHoldList = oraSpaceConfigDao.loadAll();
			oracleNodeList = dao.getDbByType(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != oraSpaceConfigDao) {
				oraSpaceConfigDao.close();
			}
			if (null != dao) {
				dao.close();
			}
		}

		Hashtable<String, DBVo> dbNnodeHt = new Hashtable<String, DBVo>();
		DBVo dbNodeVo = null;
		if (null != oracleNodeList && oracleNodeList.size() > 0) {
			for (int i = 0; i < oracleNodeList.size(); i++) {
				dbNodeVo = (DBVo) oracleNodeList.get(i);
				dbNnodeHt.put(IpTranslation.formIpToHex(dbNodeVo.getIpAddress()) + ":" + dbNodeVo.getId(), dbNodeVo);
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != tableSpaceThresHoldList && tableSpaceThresHoldList.size() > 0) {
			Oraspaceconfig vo = null;
			for (int i = 0; i < tableSpaceThresHoldList.size(); i++) {
				vo = (Oraspaceconfig) tableSpaceThresHoldList.get(i);
				jsonString.append("{\"tableSpaceThresHoldId\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				if (null != dbNnodeHt.get(vo.getIpaddress())) {
					jsonString.append(dbNnodeHt.get(vo.getIpaddress()).getIpAddress());
				} else {
					jsonString.append("未知");
				}
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				if (null != dbNnodeHt.get(vo.getIpaddress())) {
					jsonString.append(dbNnodeHt.get(vo.getIpaddress()).getAlias());
				} else {
					jsonString.append("未知");
				}
				jsonString.append("\",");

				jsonString.append("\"tableSpaceName\":\"");
				jsonString.append(vo.getSpacename());
				jsonString.append("\",");

				jsonString.append("\"isA\":\"");
				jsonString.append(vo.getSms());
				jsonString.append("\",");

				jsonString.append("\"isRPT\":\"");
				jsonString.append(vo.getReportflag());
				jsonString.append("\",");

				jsonString.append("\"alarmValue\":\"");
				jsonString.append(vo.getAlarmvalue());
				jsonString.append("\",");

				jsonString.append("\"remark\":\"");
				jsonString.append(vo.getBak());
				jsonString.append("\"}");

				if (i != tableSpaceThresHoldList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + tableSpaceThresHoldList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void InitializableTableSpaceThresHold() {
		OraspaceconfigDao configdao = new OraspaceconfigDao();
		try {
			configdao.fromLastToOraspaceconfig();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (configdao != null)
				configdao.close();
		}
	}
}
