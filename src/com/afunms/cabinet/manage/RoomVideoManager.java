package com.afunms.cabinet.manage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.util.ReportExport;
import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.RoomVideoDao;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.RoomVideo;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

@SuppressWarnings("unchecked")
public class RoomVideoManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		} else if (action.equals("listView")) {
			return listView();
		} else if (action.equals("ready_add")) {
			return ready_add();

		} else if (action.equals("add")) {
			return add();
		} else if (action.equals("delete")) {
			return delete();
		} else if (action.equals("ready_edit")) {

			return ready_edit();
		} else if (action.equals("update")) {
			return update();
		} else if (action.equals("find")) {
			return find();
		} else if (action.equals("findView")) {
			return findView();
		} else if (action.equals("download")) {
			return download();
		} else if (action.equals("downloadReport")) {
			return downloadReport();
		}
		return null;
	}

	private String ready_edit() {
		RoomVideoDao dao = new RoomVideoDao();
		setTarget("/cabinet/roomvideo/edit.jsp");
		EqpRoomDao roomdao = new EqpRoomDao();
		List roomList = new ArrayList();
		try {
			roomList = roomdao.loadAll();
		} catch (Exception e) {

		} finally {
			roomdao.close();
		}
		request.setAttribute("roomList", roomList);
		return readyEdit(dao);
	}

	@SuppressWarnings("deprecation")
	private String download() {
		String realPath = request.getRealPath("");
		String name = getParaValue("fileName");
		String path = realPath + "/cabinet/roomvideo/flv/" + name;
		request.setAttribute("filename", path);
		return "/cabinet/roomvideo/download.jsp";
	}

	private String downloadReport() {
		String type = getParaValue("type");
		String exportType = getParaValue("exportType");
		int cabinetid = getParaIntValue("cabinetid");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		String filePath = ResourceCenter.getInstance().getSysPath() + "/cabinet/roomvideo/flv/roomReport(" + time + ")." + exportType;
		String startTime = getParaValue("startdate");
		String toTime = getParaValue("todate");
		ReportExport export = new ReportExport();
		export.exportRoomReport(type, filePath, cabinetid, startTime, toTime, exportType);
		request.setAttribute("filename", filePath);
		return "/capreport/net/download.jsp";
	}

	private String list() {
		RoomVideoDao dao = new RoomVideoDao();
		setTarget("/cabinet/roomvideo/list.jsp");
		List<EqpRoom> eqpRoomlist = new ArrayList<EqpRoom>();
		Hashtable<Integer, EqpRoom> eqpRoomHash = new Hashtable<Integer, EqpRoom>();
		UserDao userdao = new UserDao();
		List userlist = null;
		EqpRoomDao eqpRoomDao = null;
		try {
			userlist = userdao.loadAll();
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
			userdao.close();
		}
		Hashtable userHash = new Hashtable();
		if (userlist != null && userlist.size() > 0) {
			for (int i = 0; i < userlist.size(); i++) {
				User user = (User) userlist.get(i);
				userHash.put(user.getId(), user);
			}
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

		request.setAttribute("userHash", userHash);
		request.setAttribute("eqpRoomHash", eqpRoomHash);

		return list(dao, where + " order by dotime desc");
	}

	private String listView() {
		RoomVideoDao dao = new RoomVideoDao();
		setTarget("/cabinet/roomvideo/listView.jsp");
		List<EqpRoom> eqpRoomlist = new ArrayList<EqpRoom>();
		Hashtable<Integer, EqpRoom> eqpRoomHash = new Hashtable<Integer, EqpRoom>();
		EqpRoomDao eqpRoomDao = null;
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
		return list(dao, where + " order by dotime desc");
	}

	private String find() {
		RoomVideoDao dao = new RoomVideoDao();
		setTarget("/cabinet/roomvideo/list.jsp");
		String where = "";
		int bsid = getParaIntValue("bsid");
		if (bsid != -1) {
			where = where + " where bsid = " + bsid;
		}
		return list(dao, where);
	}

	private String findView() {
		RoomVideoDao dao = new RoomVideoDao();
		setTarget("/cabinet/roomvideo/listView.jsp");
		String where = " where ";
		int bsid = getParaIntValue("bsid");
		if (bsid != -1 && bsid != 0) {
			where = where + " bsid = " + bsid;
		} else {
			where = "";
		}
		return list(dao, where);
	}

	private String add() {
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		RoomVideo vo = new RoomVideo();
		RoomVideoDao dao = new RoomVideoDao();
		String fname = (String) session.getAttribute("fname");
		vo.setName(getParaValue("name"));
		vo.setFilename(fname);
		vo.setDescription(getParaValue("description"));
		vo.setCabinetid(getParaIntValue("cabinetid"));
		vo.setUserid(current_user.getId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		String time = sdf.format(date.getTime());
		vo.setDotime(time);
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return list();
	}

	private String delete() {
		String realPath = ResourceCenter.getInstance().getSysPath();
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
			RoomVideoDao dao = new RoomVideoDao();
			List<String> list = null;
			boolean flag = false;
			try {
				list = dao.findByIds("where id in(" + id + ")");
				flag = dao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if (flag && list != null) {
				for (int i = 0; i < list.size(); i++) {
					String path = realPath + "cabinet\\roomvideo\\flv\\" + list.get(i);
					File file = null;
					try {
						file = new File(path);
						if (file.exists()) {
							file.delete();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
		return list();
	}

	public String update() {
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		RoomVideo vo = new RoomVideo();
		RoomVideoDao dao = new RoomVideoDao();
		vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setFilename(getParaValue("extraFile"));
		vo.setDescription(getParaValue("description"));
		vo.setCabinetid(getParaIntValue("cabinetid"));
		vo.setUserid(current_user.getId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		String time = sdf.format(date.getTime());
		vo.setDotime(time);
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return list();
	}

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
		return "/cabinet/roomvideo/add.jsp";
	}
}
