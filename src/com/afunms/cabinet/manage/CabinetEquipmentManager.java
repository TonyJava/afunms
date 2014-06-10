package com.afunms.cabinet.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.cabinet.dao.CabinetEquipmentDao;
import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.MachineCabinetDao;
import com.afunms.cabinet.model.CabinetEquipment;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.MachineCabinet;
import com.afunms.cabinet.util.CabinetXML;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.BusinessSystemDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.polling.snmp.interfaces.SnmpSet;
import com.afunms.system.dao.UserDao;
import com.afunms.topology.dao.HostNodeDao;

@SuppressWarnings("unchecked")
public class CabinetEquipmentManager extends BaseManager implements ManagerInterface {

	private String list() {
		CabinetEquipmentDao dao = new CabinetEquipmentDao();
		String isTreeView = request.getParameter("isTreeView");
		request.setAttribute("isTreeView", isTreeView);
		List ips = null;

		MachineCabinetDao cabinetdao = new MachineCabinetDao();
		List cabinetlist = null;
		try {
			cabinetlist = cabinetdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cabinetdao.close();
		}
		Hashtable cabinethash = new Hashtable();
		if (cabinetlist != null && cabinetlist.size() > 0) {
			for (int i = 0; i < cabinetlist.size(); i++) {
				MachineCabinet vo = (MachineCabinet) cabinetlist.get(i);
				cabinethash.put(vo.getMotorroom() + ":" + vo.getId(), vo.getName());
			}
		}

		EqpRoomDao roomdao = new EqpRoomDao();
		List roomlist = null;
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomdao.close();
		}
		Hashtable roomhash = new Hashtable();
		if (roomlist != null && roomlist.size() > 0) {
			EqpRoom room = null;
			for (int i = 0; i < roomlist.size(); i++) {
				room = (EqpRoom) roomlist.get(i);
				roomhash.put(room.getId(), room.getName());
			}
		}

		// 需要增加机房的列表\机柜的列表
		request.setAttribute("ips", ips);
		request.setAttribute("cabinethash", cabinethash);
		request.setAttribute("roomhash", roomhash);
		dao = new CabinetEquipmentDao();
		setTarget("/cabinet/cabinetequipment/list.jsp");
		return list(dao);
	}

	private String find() {

		String roomid = getParaValue("eqproom");
		String cabinetid = getParaValue("cabinet");

		CabinetEquipmentDao dao = new CabinetEquipmentDao();
		MachineCabinetDao cabinetdao = new MachineCabinetDao();
		List cabinetlist = null;
		try {
			cabinetlist = cabinetdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cabinetdao.close();
		}

		Hashtable cabinethash = new Hashtable();
		if (cabinetlist != null && cabinetlist.size() > 0) {
			for (int i = 0; i < cabinetlist.size(); i++) {
				MachineCabinet vo = (MachineCabinet) cabinetlist.get(i);
				cabinethash.put(vo.getMotorroom() + ":" + vo.getId(), vo.getName());
			}
		}

		EqpRoomDao roomdao = new EqpRoomDao();
		List roomlist = null;
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomdao.close();
		}
		Hashtable roomhash = new Hashtable();
		if (roomlist != null && roomlist.size() > 0) {
			EqpRoom room = null;
			for (int i = 0; i < roomlist.size(); i++) {
				room = (EqpRoom) roomlist.get(i);
				roomhash.put(room.getId(), room.getName());
			}
		}

		List list2 = new ArrayList();
		cabinetdao = new MachineCabinetDao();

		try {
			list2 = cabinetdao.selectById(roomid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		request.setAttribute("list2", list2);

		// 需要增加机房的列表\机柜的列表
		request.setAttribute("room_id", roomid);
		request.setAttribute("cabinet_id", cabinetid);
		request.setAttribute("cabinethash", cabinethash);
		request.setAttribute("roomhash", roomhash);
		dao = new CabinetEquipmentDao();
		setTarget("/cabinet/cabinetequipment/list.jsp");
		return list(dao, roomid, cabinetid);

	}

	protected String list(BaseDao dao, String roomid, String cabinetid) {
		String where = null;
		String targetJsp = null;
		int perpage = getPerPagenum();

		if ("-1".equals(roomid) & "-1".equals(cabinetid)) {
			where = "";
		} else if ("-1".equals(roomid)) {
			where = "where cabinetid = " + cabinetid;
		} else if ("-1".equals(cabinetid)) {
			where = "where roomid = " + roomid;
		} else {
			where = "where roomid =" + roomid + " and cabinetid = " + cabinetid;
		}

		List list = dao.listByPage(getCurrentPage(), where, perpage);
		if (list == null)
			return null;

		request.setAttribute("page", dao.getPage());
		request.setAttribute("list", list);
		targetJsp = getTarget();
		return targetJsp;
	}

	private String monitornodelist() {
		PortconfigDao dao = new PortconfigDao();
		setTarget("/config/portconfig/portconfiglist.jsp");
		return list(dao, " where managed=1");
	}

	private String save() {
		CabinetEquipment vo = new CabinetEquipment();
		vo.setCabinetid(getParaIntValue("cabinetid"));
		vo.setRoomid(getParaIntValue("roomid"));
		vo.setUnmubers(getParaValue("unumbers"));
		String operid = getParaValue("operid");
		String nodeid = getParaValue("nodeid");
		int businessid = getParaIntValue("bid");
		String businessName = getParaValue("businessName");
		if (operid != null && operid.trim().length() > 0) {
			vo.setOperid(Integer.parseInt(operid));
		} else {
			vo.setOperid(-1);
		}
		if (nodeid != null && nodeid.trim().length() > 0) {
			vo.setNodeid(Integer.parseInt(nodeid));
		} else {
			vo.setNodeid(-1);
		}

		vo.setContactname(getParaValue("contactname"));
		vo.setContactphone(getParaValue("contactphone"));
		vo.setContactemail(getParaValue("contactemail"));
		vo.setBusinessid(businessid);
		vo.setBusinessName(businessName);
		vo.setNodename(getParaValue("nodename"));
		vo.setNodedescr(getParaValue("nodedescr"));

		CabinetEquipmentDao dao = new CabinetEquipmentDao();
		boolean result = false;
		try {
			result = dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (result == false) {
			setErrorCode(ErrorMessage.USER_EXIST);
		}
		CabinetXML cxml = new CabinetXML();
		cxml.CreateCabinetXML();
		return list();
	}

	private String readyEdit() {
		CabinetEquipmentDao dao = new CabinetEquipmentDao();

		MachineCabinetDao cabinetdao = new MachineCabinetDao();
		List cabinetlist = null;
		try {
			cabinetlist = cabinetdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cabinetdao.close();
		}

		Hashtable cabinethash = new Hashtable();
		if (cabinetlist != null && cabinetlist.size() > 0) {
			for (int i = 0; i < cabinetlist.size(); i++) {
				MachineCabinet vo = (MachineCabinet) cabinetlist.get(i);
				cabinethash.put(vo.getMotorroom() + ":" + vo.getId(), vo.getName());
			}
		}

		EqpRoomDao roomdao = new EqpRoomDao();
		List roomlist = null;
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomdao.close();
		}
		Hashtable roomhash = new Hashtable();
		if (roomlist != null && roomlist.size() > 0) {
			EqpRoom room = null;
			for (int i = 0; i < roomlist.size(); i++) {
				room = (EqpRoom) roomlist.get(i);
				roomhash.put(room.getId(), room.getName());
			}
		}

		// 需要增加机房的列表\机柜的列表
		request.setAttribute("cabinethash", cabinethash);
		request.setAttribute("roomhash", roomhash);

		CabinetEquipment vo = new CabinetEquipment();
		try {
			vo = (CabinetEquipment) dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		return "/cabinet/cabinetequipment/edit.jsp";
	}

	public String edit() {
		CabinetEquipment vo = new CabinetEquipment();
		vo.setId(getParaIntValue("id"));
		vo.setCabinetid(getParaIntValue("cabinetid"));
		vo.setRoomid(getParaIntValue("roomid"));
		vo.setUnmubers(getParaValue("unumbers"));
		String operid = getParaValue("operid");
		String nodeid = getParaValue("nodeid");
		vo.setBusinessid(getParaIntValue("bid"));
		vo.setBusinessName(getParaValue("businessName"));
		if (operid != null && operid.trim().length() > 0) {
			vo.setOperid(Integer.parseInt(operid));
		} else {
			vo.setOperid(-1);
		}
		if (nodeid != null && nodeid.trim().length() > 0) {
			vo.setNodeid(Integer.parseInt(nodeid));
		} else {
			vo.setNodeid(-1);
		}

		vo.setContactname(getParaValue("contactname"));
		vo.setContactphone(getParaValue("contactphone"));
		vo.setContactemail(getParaValue("contactemail"));

		vo.setNodename(getParaValue("nodename"));
		vo.setNodedescr(getParaValue("nodedescr"));

		CabinetEquipmentDao dao = new CabinetEquipmentDao();
		boolean result = false;
		try {
			result = dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (result == false) {
			setErrorCode(ErrorMessage.USER_EXIST);
		}
		CabinetXML cxml = new CabinetXML();
		cxml.CreateCabinetXML();
		return list();
	}

	private String setunumber() {
		int roomid = getParaIntValue("roomid");
		int cabinetid = getParaIntValue("cabinetid");

		CabinetEquipmentDao dao = new CabinetEquipmentDao();
		List ulist = new ArrayList();
		try {
			ulist = dao.findByRoomidAndCabinetid(roomid, cabinetid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		MachineCabinetDao cabinetdao = new MachineCabinetDao();
		MachineCabinet cabinet = null;
		try {
			cabinet = (MachineCabinet) cabinetdao.findByID(cabinetid + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cabinetdao.close();
		}
		request.setAttribute("usum", Integer.parseInt(cabinet.getUselect()));
		request.setAttribute("ulist", ulist);
		return "/cabinet/cabinetequipment/setu.jsp";
	}

	private String setNode() {

		HostNodeDao dao = new HostNodeDao();
		List nodelist = null;
		try {
			nodelist = dao.getOrderByIP();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("nodelist", nodelist);
		return "/cabinet/cabinetequipment/setnode.jsp";

	}

	private String setUser() {

		UserDao dao = new UserDao();
		List allUser = null;
		try {
			allUser = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("allUser", allUser);
		return "/cabinet/cabinetequipment/setUser.jsp";

	}

	private String setBusinessSystem() {
		BusinessSystemDao businessSystemDao = new BusinessSystemDao();
		List allbusiness = null;
		try {
			allbusiness = businessSystemDao.loadAll();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		request.setAttribute("allbis", allbusiness);

		return "/cabinet/cabinetequipment/setBusinessSystem.jsp";
	}

	private String update() {
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");

		PortconfigDao dao = null;

		dao = new PortconfigDao();
		try {
			vo = dao.loadPortconfig(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (sms > -1)
			vo.setSms(sms);
		if (reportflag > -1)
			vo.setReportflag(reportflag);
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		dao = new PortconfigDao();
		List ips = null;
		try {
			ips = dao.getIps();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("ips", ips);
		return "/portconfig.do?action=list";
	}

	private String updatenodeport() {
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		String sflag = getParaValue("sflag");
		String jp = getParaValue("jp");

		PortconfigDao dao = null;

		dao = new PortconfigDao();
		try {
			vo = dao.loadPortconfig(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (sms > -1)
			vo.setSms(sms);
		if (reportflag > -1)
			vo.setReportflag(reportflag);
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		if (sflag != null && "1".equalsIgnoreCase(sflag)) {
			return "/portconfig.do?action=list&flag=0&jp=" + jp;
		} else
			return "/portconfig.do?action=nodeportlist&ipaddress=" + vo.getIpaddress();
	}

	private String updateport() {
		Portconfig vo = new Portconfig();
		String id = getParaValue("id");
		PortconfigDao portconfigDao = new PortconfigDao();
		try {
			vo = portconfigDao.loadPortconfig(Integer.parseInt(id));
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally {
			portconfigDao.close();
		}
		String linkuse = getParaValue("linkuse");
		if (linkuse != null) {
			vo.setLinkuse(linkuse);
		}
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		if (sms > -1) {
			vo.setSms(sms);
		}
		if (reportflag > -1) {
			vo.setReportflag(reportflag);
		}
		String inportalarm = getParaValue("inportalarm");
		if (inportalarm != null) {
			vo.setInportalarm(inportalarm);
		}
		String outportalarm = getParaValue("outportalarm");
		if (outportalarm != null) {
			vo.setOutportalarm(outportalarm);
		}
		PortconfigDao dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/portconfig.do?action=list";
	}



	private String nodeportlist() {
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");
		PortconfigDao dao = new PortconfigDao();
		request.setAttribute("id", id);
		List list = new ArrayList();
		try {
			list = dao.loadByIpaddress(ipaddress);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		return "//config/portconfig/nodeportlist.jsp";
	}

	public String upordownPort() {
		String ip = getParaValue("ip");
		String oid = ".1.3.6.1.2.1.2.2.1.8" + "." + getParaValue("index");
		String writeCommunity = getParaValue("writecommunity");
		int port_status = getParaIntValue("portflag");
		SnmpSet setPort = new SnmpSet(ip, writeCommunity, oid, port_status);
		setPort.snmpSetPort();
		return "/perform.do?action=monitornodelist";
	}

	public String setBid() {
		EqpRoomDao roomdao = new EqpRoomDao();
		List roomlist = null;
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {

		} finally {
			roomdao.close();
		}
		Hashtable roomhash = new Hashtable();
		if (roomlist != null && roomlist.size() > 0) {
			EqpRoom room = null;
			for (int i = 0; i < roomlist.size(); i++) {
				room = (EqpRoom) roomlist.get(i);
				roomhash.put(room.getId() + "", new ArrayList());
			}
		}

		MachineCabinetDao cabinetdao = new MachineCabinetDao();
		List cabinetlist = null;
		try {
			cabinetlist = cabinetdao.loadAll();
		} catch (Exception e) {

		} finally {
			cabinetdao.close();
		}

		if (cabinetlist != null && cabinetlist.size() > 0) {
			MachineCabinet machineCabinet = null;
			for (int i = 0; i < cabinetlist.size(); i++) {
				machineCabinet = null;
				machineCabinet = (MachineCabinet) cabinetlist.get(i);
				List cabinetlists = (List) roomhash.get(machineCabinet.getMotorroom());
				cabinetlists.add(machineCabinet);
				roomhash.put(machineCabinet.getMotorroom(), cabinetlists);

			}
		}

		BusinessDao businessDao = new BusinessDao();
		List allbusiness = null;
		try {
			allbusiness = businessDao.loadAll();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		request.setAttribute("allbusiness", allbusiness);

		String value = getParaValue("value");
		List bidIsSelected = new ArrayList();
		if (value != null) {
			String[] bids = value.split(",");
			if (bids != null && !"".equals(bids)) {
				for (int i = 0; i < bids.length; i++) {
					bidIsSelected.add(bids[i]);
				}
			}
		}
		request.setAttribute("bidIsSelected", bidIsSelected);

		request.setAttribute("event", getParaValue("event"));
		request.setAttribute("roomhash", roomhash);
		request.setAttribute("roomlist", roomlist);

		request.setAttribute("eventText", getParaValue("eventText"));
		return "/cabinet/cabinetequipment/setbid.jsp";
	}

	public String execute(String action) {
		if (action.equals("loadByCabinetID")) {
			return loadByCabinetID();
		}
		if (action.equals("list"))
			return list();
		if (action.equals("monitornodelist"))
			return monitornodelist();
		if (action.equals("showedit"))
			return readyEdit();
		if (action.equals("edit"))
			return edit();
		if (action.equals("update"))
			return update();
		if (action.equals("updatenodeport")) {
			return updatenodeport();
		}
		if (action.equals("updateport"))
			return updateport();
		if (action.equals("find"))
			return find();
		if (action.equals("nodeportlist")) {
			return nodeportlist();
		}
		if (action.equals("setunumber"))
			return setunumber();
		if (action.equals("setNode"))
			return setNode();
		if (action.equals("setUser"))
			return setUser();
		if (action.equals("setBis"))
			return setBusinessSystem();
		if (action.equals("ready_add")) {
			String isTreeView = request.getParameter("isTreeView");
			request.setAttribute("isTreeView", isTreeView);
			return "/cabinet/cabinetequipment/add.jsp";
		}
		if (action.equals("add"))
			return save();
		if (action.equals("delete")) {
			DaoInterface dao = new CabinetEquipmentDao();
			CabinetXML cxml = new CabinetXML();
			cxml.CreateCabinetXML();
			setTarget("/cabinetequipment.do?action=list");
			return delete(dao);
		}
		if ("setBid".equals(action)) {
			return setBid();
		}
		if (action.equals("upordownPort"))
			return upordownPort();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String loadByCabinetID() {
		CabinetEquipmentDao dao = new CabinetEquipmentDao();
		List ips = null;

		MachineCabinetDao cabinetdao = new MachineCabinetDao();
		List cabinetlist = null;
		try {
			cabinetlist = cabinetdao.loadAll();
		} catch (Exception e) {

		} finally {
			cabinetdao.close();
		}
		Hashtable cabinethash = new Hashtable();
		if (cabinetlist != null && cabinetlist.size() > 0) {
			for (int i = 0; i < cabinetlist.size(); i++) {
				MachineCabinet vo = (MachineCabinet) cabinetlist.get(i);
				cabinethash.put(vo.getMotorroom() + ":" + vo.getId(), vo.getName());
			}
		}

		EqpRoomDao roomdao = new EqpRoomDao();
		List roomlist = null;
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomdao.close();
		}
		Hashtable roomhash = new Hashtable();
		if (roomlist != null && roomlist.size() > 0) {
			EqpRoom room = null;
			for (int i = 0; i < roomlist.size(); i++) {
				room = (EqpRoom) roomlist.get(i);
				roomhash.put(room.getId(), room.getName());
			}
		}

		// 需要增加机房的列表\机柜的列表
		request.setAttribute("ips", ips);
		request.setAttribute("cabinethash", cabinethash);
		request.setAttribute("roomhash", roomhash);
		dao = new CabinetEquipmentDao();
		String id = request.getParameter("id");
		request.setAttribute("id", id);
		String isTreeView = request.getParameter("isTreeView");
		request.setAttribute("isTreeView", isTreeView);
		setTarget("/cabinet/cabinetequipment/list.jsp");
		return list(dao, "where cabinetid=" + id);
	}
}
