package com.afunms.cabinet.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.util.ReportExport;
import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.RoomGuestDao;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.RoomGuest;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.initialize.ResourceCenter;

/**
 * @description 机房来宾管理类
 * @author wangxiangyong
 * @date Dec 23, 2011
 */

@SuppressWarnings("unchecked")
public class RoomGuestManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("ready_add")) {
			return ready_add();
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("detail")) {
			return detail();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("downloadReport")) {
			return downloadReport();
		}
		return null;
	}

	private String list() {
		Hashtable<Integer, EqpRoom> eqpRoomHash = new Hashtable<Integer, EqpRoom>();
		EqpRoomDao eqpRoomDao = null;
		List<EqpRoom> eqpRoomlist = new ArrayList<EqpRoom>();
		try {
			eqpRoomDao = new EqpRoomDao();
			eqpRoomlist = eqpRoomDao.loadAll();
			if (eqpRoomlist != null && eqpRoomlist.size() > 0) {
				for (int i = 0; i < eqpRoomlist.size(); i++) {
					EqpRoom eqpRoom = (EqpRoom) eqpRoomlist.get(i);
					eqpRoomHash.put(eqpRoom.getId(), eqpRoom);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eqpRoomDao.close();
		}
		int cabinetid = getParaIntValue("cabinetid");// 操作时间
		String startdate = getParaValue("startdate");// 开始日期
		String todate = getParaValue("todate");// 
		StringBuffer where = new StringBuffer();

		where.append(" where 1=1");

		if (cabinetid != -1) {
			where.append(" and cabinetid=" + cabinetid);
		}

		if (startdate != null && todate != null && !"".equals(startdate) && !"".equals(todate) && !"null".equals(startdate) && !"null".equals(todate)) {
			where.append(" and dotime>'" + startdate + " 00:00:00' and dotime<'" + todate + " 23:59:59'");
		} else {
			String currentDateString = "";// 初始页面开始日期为空
			String perWeekDateString = "";// 初始页面结束日期为空
			todate = currentDateString;
			startdate = perWeekDateString;
		}
		request.setAttribute("cabinetid", cabinetid);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("eqpRoomHash", eqpRoomHash);
		setTarget("/cabinet/roomguest/list.jsp");
		RoomGuestDao dao = new RoomGuestDao();

		return list(dao, where + " order by dotime desc");

	}

	/**
	 * 编辑
	 * 
	 * @return String
	 */
	public String ready_add() {
		EqpRoomDao dao = new EqpRoomDao();
		List roomList = new ArrayList();
		try {
			roomList = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("roomList", roomList);
		return "/cabinet/roomguest/add.jsp";
	}

	/**
	 * @parms
	 * @return String
	 */
	public String add() {
		String name = getParaValue("name");
		int cabinetid = getParaIntValue("cabinetid");
		String unit = getParaValue("unit");
		String phone = getParaValue("phone");
		String mail = getParaValue("mail");
		String inTime = getParaValue("inTime");
		String outTime = getParaValue("outTime");
		String reason = getParaValue("reason");
		String audit = getParaValue("audit");
		String bak = getParaValue("bak");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		RoomGuest guest = new RoomGuest();
		guest.setName(name);
		guest.setCabinetid(cabinetid);
		guest.setUnit(unit);
		guest.setPhone(phone);
		guest.setMail(mail);
		guest.setInTime(inTime);
		guest.setOutTime(outTime);
		guest.setReason(reason);
		guest.setAudit(audit);
		guest.setDotime(time);
		guest.setBak(bak);
		return list();
	}

	public String ready_edit() {
		int id = getParaIntValue("id");
		RoomGuestDao roomGuestDao = null;
		RoomGuest roomGuest = null;
		EqpRoomDao dao = new EqpRoomDao();
		List roomList = new ArrayList();
		try {
			roomList = dao.loadAll();
			roomGuestDao = new RoomGuestDao();
			roomGuest = (RoomGuest) roomGuestDao.findByID(id + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			roomGuestDao.close();
		}

		request.setAttribute("roomList", roomList);
		request.setAttribute("roomGuest", roomGuest);
		return "/cabinet/roomguest/edit.jsp";
	}

	/**
	 * 来宾登记详情
	 * 
	 * @return String
	 */
	public String detail() {
		int id = getParaIntValue("id");
		RoomGuestDao roomGuestDao = null;
		RoomGuest roomGuest = null;
		EqpRoomDao dao = new EqpRoomDao();
		List roomList = new ArrayList();
		try {
			roomList = dao.loadAll();
			roomGuestDao = new RoomGuestDao();
			roomGuest = (RoomGuest) roomGuestDao.findByID(id + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			roomGuestDao.close();
		}

		request.setAttribute("roomList", roomList);
		request.setAttribute("roomGuest", roomGuest);
		return "/cabinet/roomguest/detail.jsp";
	}

	public String update() {
		int id = getParaIntValue("id");
		String name = getParaValue("name");
		int cabinetid = getParaIntValue("cabinetid");
		String unit = getParaValue("unit");
		String phone = getParaValue("phone");
		String mail = getParaValue("mail");
		String inTime = getParaValue("inTime");
		String outTime = getParaValue("outTime");
		String reason = getParaValue("reason");
		String audit = getParaValue("audit");
		String bak = getParaValue("bak");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		RoomGuest guest = new RoomGuest();
		guest.setId(id);
		guest.setName(name);
		guest.setCabinetid(cabinetid);
		guest.setUnit(unit);
		guest.setPhone(phone);
		guest.setMail(mail);
		guest.setInTime(inTime);
		guest.setOutTime(outTime);
		guest.setReason(reason);
		guest.setAudit(audit);
		guest.setDotime(time);
		guest.setBak(bak);
		return list();
	}

	/**
	 * 删除
	 * 
	 * @return String
	 */
	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			StringBuffer id = new StringBuffer();
			for (int i = 0; i < ids.length; i++) {
				if (i != ids.length - 1) {
					id.append(ids[i]).append(",");
				} else {
					id.append(ids[i]);
				}
			}
		}
		return list();
	}

	private String downloadReport() {
		String type = getParaValue("type");
		String exportType = getParaValue("exportType");
		int cabinetid = getParaIntValue("cabinetid");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		String filePath = ResourceCenter.getInstance().getSysPath() + "/cabinet/roomguest/report/roomGuest(" + time + ")." + exportType;
		String startTime = getParaValue("startdate");
		String toTime = getParaValue("todate");
		ReportExport export = new ReportExport();
		export.exportRoomReport(type, filePath, cabinetid, startTime, toTime, exportType);
		request.setAttribute("filename", filePath);
		return "/capreport/net/download.jsp";
	}
}
