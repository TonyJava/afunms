/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.cabinet.manage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.cabinet.dao.CabinetEquipmentDao;
import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.MachineCabinetDao;
import com.afunms.cabinet.model.CabinetEquipment;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.MachineCabinet;
import com.afunms.cabinet.model.OperCabinet;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.BusinessSystemDao;
import com.afunms.config.model.BusinessSystem;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.impl.IpResourceReport2;
import com.afunms.report.abstraction.ExcelReport2;
import com.afunms.report.base.AbstractionReport2;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.lowagie.text.DocumentException;

@SuppressWarnings("unchecked")
public class OperCabinetManager extends BaseManager implements ManagerInterface {

	public String opercabinetList() {
		String operid = getParaValue("operid");
		String opername = "";
		String contactname = "";
		String contactphone = "";
		BusinessSystemDao bDao = new BusinessSystemDao();
		try {
			BusinessSystem businessSystem = (BusinessSystem) bDao.findByID(operid);
			opername = businessSystem.getName();
			contactname = businessSystem.getContactname();
			contactphone = businessSystem.getContactphone();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bDao.close();
		}
		BusinessSystemDao businessDao = new BusinessSystemDao();
		List businesslist = new ArrayList();
		try {
			businesslist = businessDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			businessDao.close();
		}
		OperCabinet operCabinet;
		CabinetEquipmentDao dao1 = new CabinetEquipmentDao();
		EqpRoomDao eqproomdao;
		HostNodeDao hostNodeDao;
		MachineCabinetDao machineCabinetDao;
		List list = new ArrayList();
		try {
			List equipmentlist = dao1.findByEquipment(Integer.parseInt(operid));

			if (null != equipmentlist && equipmentlist.size() > 0) {
				for (int i = 0; i < equipmentlist.size(); i++) {
					operCabinet = new OperCabinet();
					eqproomdao = new EqpRoomDao();
					hostNodeDao = new HostNodeDao();
					machineCabinetDao = new MachineCabinetDao();
					CabinetEquipment cabinetEqp = (CabinetEquipment) equipmentlist.get(i);
					EqpRoom eqpRoom = (EqpRoom) eqproomdao.findByID(Integer.toString(cabinetEqp.getRoomid()));
					MachineCabinet machineCabinet = (MachineCabinet) machineCabinetDao.findByID(Integer.toString(cabinetEqp.getCabinetid()));
					HostNode hostNode = (HostNode) hostNodeDao.findByID(Integer.toString(cabinetEqp.getNodeid()));
					operCabinet.setId(cabinetEqp.getId());
					operCabinet.setRoomname(eqpRoom.getName());
					operCabinet.setCabinetname(machineCabinet.getName());
					operCabinet.setUseu(cabinetEqp.getUnmubers());
					if (hostNode != null) {
						operCabinet.setIpaddress(hostNode.getIpAddress());
					} else {
						operCabinet.setIpaddress("");
					}

					operCabinet.setEquipmentname(cabinetEqp.getNodename());
					operCabinet.setContactname(cabinetEqp.getContactname());
					operCabinet.setContactphone(cabinetEqp.getContactphone());
					list.add(operCabinet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao1.close();
		}
		session.setAttribute("operid", operid);
		request.setAttribute("list", list);
		session.setAttribute("opername", opername);
		session.setAttribute("contactname", contactname);
		session.setAttribute("contactphone", contactphone);
		request.setAttribute("businesslist", businesslist);
		return "/cabinet/cabinetbsreport/opercabinet.jsp";
	}

	/**
	 * 报表Excel
	 */

	private String downExlReport() {
		String opername = (String) session.getAttribute("opername");
		String contactname = (String) session.getAttribute("contactname");
		String contactphone = (String) session.getAttribute("contactphone");
		String operid = (String) session.getAttribute("operid");
		List equipmentlist = this.loaddate(operid);
		Hashtable reporthash = new Hashtable();
		// reporthash.put("room", room);
		if (equipmentlist != null) {
			reporthash.put("equipmentlist", equipmentlist);
		} else {
			equipmentlist = new ArrayList();
		}
		AbstractionReport2 report = new ExcelReport2(new IpResourceReport2(), reporthash);
		report.createReport_OperEquipment("temp" + File.separator + "operEquipment.xls", opername, contactname, contactphone);
		request.setAttribute("filename", report.getFileName());
		return "/cabinet/cabinetreport/download.jsp";
	}

	private List loaddate(String operid) {
		OperCabinet operCabinet;
		CabinetEquipmentDao dao1 = new CabinetEquipmentDao();
		EqpRoomDao eqproomdao;
		HostNodeDao hostNodeDao;
		MachineCabinetDao machineCabinetDao;
		List list = new ArrayList();
		try {
			List equipmentlist = dao1.findByEquipment(Integer.parseInt(operid));

			if (null != equipmentlist && equipmentlist.size() > 0) {
				for (int i = 0; i < equipmentlist.size(); i++) {
					operCabinet = new OperCabinet();
					eqproomdao = new EqpRoomDao();
					hostNodeDao = new HostNodeDao();
					machineCabinetDao = new MachineCabinetDao();
					CabinetEquipment cabinetEqp = (CabinetEquipment) equipmentlist.get(i);
					EqpRoom eqpRoom = (EqpRoom) eqproomdao.findByID(Integer.toString(cabinetEqp.getRoomid()));
					MachineCabinet machineCabinet = (MachineCabinet) machineCabinetDao.findByID(Integer.toString(cabinetEqp.getCabinetid()));
					HostNode hostNode = (HostNode) hostNodeDao.findByID(Integer.toString(cabinetEqp.getNodeid()));
					operCabinet.setId(cabinetEqp.getId());
					operCabinet.setRoomname(eqpRoom.getName());
					operCabinet.setCabinetname(machineCabinet.getName());
					operCabinet.setUseu(cabinetEqp.getUnmubers());
					if (hostNode != null) {
						operCabinet.setIpaddress(hostNode.getIpAddress());
					} else {
						operCabinet.setIpaddress("");
					}
					operCabinet.setEquipmentname(cabinetEqp.getNodename());
					operCabinet.setContactname(cabinetEqp.getContactname());
					operCabinet.setContactphone(cabinetEqp.getContactphone());
					list.add(operCabinet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao1.close();
		}
		return list;
	}

	/**
	 * 报表Word
	 */

	private String downWordReport() {
		String opername = (String) session.getAttribute("opername");
		String contactname = (String) session.getAttribute("contactname");
		String contactphone = (String) session.getAttribute("contactphone");
		String operid = (String) session.getAttribute("operid");
		List equipmentlist = this.loaddate(operid);
		Hashtable reporthash = new Hashtable();
		String file = "temp/operEquipment.doc";// 保存到项目文件夹下的指定文件夹
		String filename = ResourceCenter.getInstance().getSysPath() + file;
		// reporthash.put("room", room);
		if (equipmentlist != null) {
			reporthash.put("equipmentlist", equipmentlist);
		} else {
			equipmentlist = new ArrayList();
		}
		ExcelReport2 report = new ExcelReport2(new IpResourceReport2(), reporthash);

		try {
			report.createReport_OperEquipmentWord(filename, opername, contactname, contactphone);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", filename);
		return "/cabinet/cabinetreport/download.jsp";
	}

	/**
	 * 报表PDF
	 */

	private String downPdfReport() {
		String opername = (String) session.getAttribute("opername");
		String contactname = (String) session.getAttribute("contactname");
		String contactphone = (String) session.getAttribute("contactphone");
		String file = "temp/operEquipment.pdf";// 保存到项目文件夹下的指定文件夹
		String filename = ResourceCenter.getInstance().getSysPath() + file;
		String operid = (String) session.getAttribute("operid");
		List equipmentlist = this.loaddate(operid);
		Hashtable reporthash = new Hashtable();
		if (equipmentlist != null) {
			reporthash.put("equipmentlist", equipmentlist);
		} else {
			equipmentlist = new ArrayList();
		}
		ExcelReport2 report = new ExcelReport2(new IpResourceReport2(), reporthash);

		try {
			report.createReport_OperEquipmentPdf(filename, opername, contactname, contactphone);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", filename);
		return "/cabinet/cabinetreport/download.jsp";
	}

	private String queryList() {
		String operid = getParaValue("oper");
		String opername = "";
		String contactname = "";
		String contactphone = "";
		BusinessSystemDao bDao = new BusinessSystemDao();
		try {
			BusinessSystem businessSystem = (BusinessSystem) bDao.findByID(operid);
			opername = businessSystem.getName();
			contactname = businessSystem.getContactname();
			contactphone = businessSystem.getContactphone();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bDao.close();
		}
		BusinessSystemDao businessDao = new BusinessSystemDao();
		List businesslist = new ArrayList();
		try {
			businesslist = businessDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			businessDao.close();
		}
		OperCabinet operCabinet;
		CabinetEquipmentDao dao1 = new CabinetEquipmentDao();
		EqpRoomDao eqproomdao;
		HostNodeDao hostNodeDao;
		MachineCabinetDao machineCabinetDao;
		List list = new ArrayList();
		try {
			List equipmentlist = dao1.findByEquipment(Integer.parseInt(operid));

			if (null != equipmentlist && equipmentlist.size() > 0) {
				for (int i = 0; i < equipmentlist.size(); i++) {
					operCabinet = new OperCabinet();
					eqproomdao = new EqpRoomDao();
					hostNodeDao = new HostNodeDao();
					machineCabinetDao = new MachineCabinetDao();
					CabinetEquipment cabinetEqp = (CabinetEquipment) equipmentlist.get(i);
					EqpRoom eqpRoom = (EqpRoom) eqproomdao.findByID(Integer.toString(cabinetEqp.getRoomid()));
					MachineCabinet machineCabinet = (MachineCabinet) machineCabinetDao.findByID(Integer.toString(cabinetEqp.getCabinetid()));
					HostNode hostNode = (HostNode) hostNodeDao.findByID(Integer.toString(cabinetEqp.getNodeid()));
					operCabinet.setId(cabinetEqp.getId());
					operCabinet.setRoomname(eqpRoom.getName());
					operCabinet.setCabinetname(machineCabinet.getName());
					operCabinet.setUseu(cabinetEqp.getUnmubers());
					if (hostNode != null) {
						operCabinet.setIpaddress(hostNode.getIpAddress());
					} else {
						operCabinet.setIpaddress("");
					}

					operCabinet.setEquipmentname(cabinetEqp.getNodename());
					operCabinet.setContactname(cabinetEqp.getContactname());
					operCabinet.setContactphone(cabinetEqp.getContactphone());
					list.add(operCabinet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao1.close();
		}
		session.setAttribute("operid", operid);
		request.setAttribute("list", list);
		session.setAttribute("opername", opername);
		session.setAttribute("contactname", contactname);
		session.setAttribute("contactphone", contactphone);
		request.setAttribute("businesslist", businesslist);
		return "/cabinet/cabinetbsreport/opercabinet.jsp";
	}

	public String execute(String action) {
		if (action.equals("list")) {
			DaoInterface dao = new BusinessSystemDao();
			setTarget("/cabinet/cabinetbsreport/list.jsp");
			return list(dao);
		}
		if (action.equals("opercabinetlist")) {
			return opercabinetList();
		}
		if (action.equals("downexlreport")) {
			return downExlReport();
		}
		if (action.equals("querylist")) {
			return queryList();
		}
		if (action.equals("downwordreport")) {
			return downWordReport();
		}

		if (action.equals("downpdfreport")) {
			return downPdfReport();
		}
		return null;
	}
}
