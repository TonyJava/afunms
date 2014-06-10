package com.afunms.cabinet.manage;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.cabinet.dao.CabinetEquipmentDao;
import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.MachineCabinetDao;
import com.afunms.cabinet.model.Cabinet;
import com.afunms.cabinet.model.CabinetEquipment;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.MachineCabinet;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;

@SuppressWarnings("unchecked")
public class CabinetReportManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list")) {
			DaoInterface dao = new EqpRoomDao();
			setTarget("/cabinet/cabinetreport/list.jsp");
			return list(dao);
		}
		if (action.equals("reportlist")) {
			return reportList();
		}
		if (action.equals("querylist")) {
			return queryList();
		}
		if (action.equals("downexlreport")) {
			return downExlReport();
		}
		return null;
	}

	private String reportList() {
		String roomid = getParaValue("id");

		if (roomid != null && "" != roomid) {
			EqpRoom eqproom;
			EqpRoomDao rdao = new EqpRoomDao();
			try {
				eqproom = (EqpRoom) rdao.findByID(roomid);
				String rname = eqproom.getName();
				session.setAttribute("name", rname);
			} catch (Exception e) {

			} finally {
				rdao.close();
			}
		}

		// reportlist页面机房下拉框的数据
		EqpRoomDao roomdao = new EqpRoomDao();
		List roomlist = new ArrayList();
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomdao.close();
		}

		CabinetEquipmentDao eqpdao = new CabinetEquipmentDao();
		Hashtable equHash = new Hashtable();
		List eqplist = new ArrayList();
		try {
			eqplist = eqpdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eqpdao.close();
		}
		if (eqplist != null && eqplist.size() > 0) {
			for (int i = 0; i < eqplist.size(); i++) {
				CabinetEquipment equ = (CabinetEquipment) eqplist.get(i);
				if (equHash.containsKey(equ.getCabinetid())) {
					List equlist = (List) equHash.get(equ.getCabinetid());
					equlist.add(equ);
					equHash.put(equ.getCabinetid(), equlist);
				} else {
					List equlist = new ArrayList();
					equlist.add(equ);
					equHash.put(equ.getCabinetid(), equlist);
				}
			}
		}
		Cabinet cabinet;
		List list = new ArrayList();
		MachineCabinetDao dao = new MachineCabinetDao();
		try {
			List machineCabinetlist = dao.loadId(roomid);
			if (null != machineCabinetlist && machineCabinetlist.size() > 0) {
				for (int i = 0; i < machineCabinetlist.size(); i++) {
					cabinet = new Cabinet();
					MachineCabinet machineCabinet = (MachineCabinet) machineCabinetlist.get(i);
					int usedUnit = 0;
					if (equHash.containsKey(machineCabinet.getId())) {
						List equlist = (List) equHash.get(machineCabinet.getId());
						if (equlist != null && equlist.size() > 0) {
							for (int k = 0; k < equlist.size(); k++) {
								CabinetEquipment equ = (CabinetEquipment) equlist.get(k);
								String unumbers = equ.getUnmubers();
								if (unumbers != null && unumbers.trim().length() > 0) {
									String[] usplit = unumbers.split(",");
									usedUnit = usedUnit + usplit.length;
								}
							}
						}
					}
					int temp = Integer.parseInt(machineCabinet.getUselect()) - usedUnit;
					DecimalFormat df = new DecimalFormat("0.00");
					double rateu = (double) usedUnit / (double) Integer.parseInt(machineCabinet.getUselect()) * 100;
					String rate = df.format(rateu) + "%";
					cabinet.setId(machineCabinet.getId());
					cabinet.setName(machineCabinet.getName());
					cabinet.setAllu(machineCabinet.getUselect());
					cabinet.setUseu(usedUnit + "");
					cabinet.setTempu(Integer.toString(temp));
					cabinet.setRateu(rate);
					list.add(cabinet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		session.setAttribute("roomid", roomid);
		request.setAttribute("list", list);
		request.setAttribute("roomlist", roomlist);
		return "/cabinet/cabinetreport/reportlist.jsp";
	}

	private String queryList() {
		String room = getParaValue("room");

		// 查询时根据页面传过来的的room值，找到所对应的name值，放到request里
		if (room != null) {
			EqpRoom eqproom;
			EqpRoomDao roomdao1 = new EqpRoomDao();
			try {
				eqproom = (EqpRoom) roomdao1.findByID(room);
				String name1 = eqproom.getName();
				session.setAttribute("name", name1);
			} catch (Exception e) {

			} finally {
				roomdao1.close();
			}
		}
		EqpRoomDao roomdao = new EqpRoomDao();
		List roomlist = new ArrayList();
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomdao.close();
		}
		// reportlist页面机房下拉框的数据
		CabinetEquipmentDao eqpdao = new CabinetEquipmentDao();
		Hashtable equHash = new Hashtable();
		List eqplist = new ArrayList();
		try {
			eqplist = eqpdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eqpdao.close();
		}
		if (eqplist != null && eqplist.size() > 0) {
			for (int i = 0; i < eqplist.size(); i++) {
				CabinetEquipment equ = (CabinetEquipment) eqplist.get(i);
				if (equHash.containsKey(equ.getCabinetid())) {
					List equlist = (List) equHash.get(equ.getCabinetid());
					equlist.add(equ);
					equHash.put(equ.getCabinetid(), equlist);
				} else {
					List equlist = new ArrayList();
					equlist.add(equ);
					equHash.put(equ.getCabinetid(), equlist);
				}
			}
		}
		Cabinet cabinet;
		List list = new ArrayList();
		MachineCabinetDao dao = new MachineCabinetDao();
		try {
			List machineCabinetlist = dao.loadId(room);
			if (null != machineCabinetlist && machineCabinetlist.size() > 0) {
				for (int i = 0; i < machineCabinetlist.size(); i++) {
					cabinet = new Cabinet();
					MachineCabinet machineCabinet = (MachineCabinet) machineCabinetlist.get(i);
					int usedUnit = 0;
					if (equHash.containsKey(machineCabinet.getId())) {
						List equlist = (List) equHash.get(machineCabinet.getId());
						if (equlist != null && equlist.size() > 0) {
							for (int k = 0; k < equlist.size(); k++) {
								CabinetEquipment equ = (CabinetEquipment) equlist.get(k);
								String unumbers = equ.getUnmubers();
								if (unumbers != null && unumbers.trim().length() > 0) {
									String[] usplit = unumbers.split(",");
									usedUnit = usedUnit + usplit.length;
								}
							}
						}
					}
					int temp = Integer.parseInt(machineCabinet.getUselect()) - usedUnit;
					DecimalFormat df = new DecimalFormat("0.00");
					double rateu = (double) usedUnit / (double) Integer.parseInt(machineCabinet.getUselect()) * 100;
					String rate = df.format(rateu) + "%";
					cabinet.setId(machineCabinet.getId());
					cabinet.setName(machineCabinet.getName());
					cabinet.setAllu(machineCabinet.getUselect());
					cabinet.setUseu(usedUnit + "");
					cabinet.setTempu(Integer.toString(temp));
					cabinet.setRateu(rate);
					list.add(cabinet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		session.setAttribute("room", room);
		request.setAttribute("list", list);
		request.setAttribute("roomlist", roomlist);
		return "/cabinet/cabinetreport/reportlist.jsp";
	}

	/**
	 * 报表Excel
	 */

	private String downExlReport() {
		String id = (String) getParaValue("id");
		String room = (String) session.getAttribute("name");
		List cabinetlist = this.loaddate(id);
		Hashtable reporthash = new Hashtable();
		reporthash.put("room", room);
		if (cabinetlist != null) {
			reporthash.put("cabinetlist", cabinetlist);
		} else {
			cabinetlist = new ArrayList();
			reporthash.put("cabinetlist", cabinetlist);
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);

		report.createReport_cabinet("temp" + File.separator + "cabinet.xls", room);
		request.setAttribute("filename", report.getFileName());
		return "/cabinet/cabinetreport/download.jsp";
	}

	/**
	 * 报表数据
	 */

	private List loaddate(String id) {
		MachineCabinetDao dao = new MachineCabinetDao();
		Cabinet cabinet;
		List cabinetlist = new ArrayList();
		CabinetEquipmentDao eqpdao = new CabinetEquipmentDao();
		Hashtable equHash = new Hashtable();
		List eqplist = new ArrayList();
		try {
			eqplist = eqpdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eqpdao.close();
		}
		if (eqplist != null && eqplist.size() > 0) {
			for (int i = 0; i < eqplist.size(); i++) {
				CabinetEquipment equ = (CabinetEquipment) eqplist.get(i);
				if (equHash.containsKey(equ.getCabinetid())) {
					List equlist = (List) equHash.get(equ.getCabinetid());
					equlist.add(equ);
					equHash.put(equ.getCabinetid(), equlist);
				} else {
					List equlist = new ArrayList();
					equlist.add(equ);
					equHash.put(equ.getCabinetid(), equlist);
				}
			}
		}
		try {
			List machineCabinetlist = dao.loadId(id);
			if (null != machineCabinetlist && machineCabinetlist.size() > 0) {
				for (int i = 0; i < machineCabinetlist.size(); i++) {
					cabinet = new Cabinet();
					MachineCabinet machineCabinet = (MachineCabinet) machineCabinetlist.get(i);
					int usedUnit = 0;
					if (equHash.containsKey(machineCabinet.getId())) {
						List equlist = (List) equHash.get(machineCabinet.getId());
						if (equlist != null && equlist.size() > 0) {
							for (int k = 0; k < equlist.size(); k++) {
								CabinetEquipment equ = (CabinetEquipment) equlist.get(k);
								String unumbers = equ.getUnmubers();
								if (unumbers != null && unumbers.trim().length() > 0) {
									String[] usplit = unumbers.split(",");
									usedUnit = usedUnit + usplit.length;
								}
							}
						}
					}
					int temp = Integer.parseInt(machineCabinet.getUselect()) - usedUnit;
					DecimalFormat df = new DecimalFormat("0.00");
					double rateu = (double) usedUnit / (double) Integer.parseInt(machineCabinet.getUselect()) * 100;
					String rate = df.format(rateu) + "%";
					cabinet.setId(machineCabinet.getId());
					cabinet.setName(machineCabinet.getName());
					cabinet.setAllu(machineCabinet.getUselect());
					cabinet.setUseu(usedUnit + "");
					cabinet.setTempu(Integer.toString(temp));
					cabinet.setRateu(rate);
					cabinetlist.add(cabinet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return cabinetlist;
	}
}