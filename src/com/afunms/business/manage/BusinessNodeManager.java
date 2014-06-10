package com.afunms.business.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.business.dao.BusinessNodeDao;
import com.afunms.business.model.BusinessNode;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.EventListDao;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Interface;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.util.KeyGenerator;

@SuppressWarnings("unchecked")
public class BusinessNodeManager extends BaseManager implements ManagerInterface {

	public String list() {
		BusinessNodeDao dao = new BusinessNodeDao();
		setTarget("/business/businessnode/list.jsp");
		return list(dao);
	}

	private String update() {
		BusinessNode vo = new BusinessNode();
		vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setBid(getParaIntValue("managexmlid"));
		vo.setCollecttype(getParaIntValue("collecttype"));
		vo.setMethod(getParaValue("method"));
		vo.setDesc(getParaValue("desc"));
		BusinessNodeDao dao = new BusinessNodeDao();
		String target = null;
		if (dao.update(vo))
			target = "/businessNode.do?action=list";
		return target;
	}

	private String save() {
		BusinessNode vo = new BusinessNode();
		vo.setName(getParaValue("name"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setCollecttype(getParaIntValue("collecttype"));
		vo.setBid(getParaIntValue("managexmlid"));
		vo.setMethod(getParaValue("method"));
		vo.setDesc(getParaValue("desc"));
		vo.setId(KeyGenerator.getInstance().getNextKey());
		BusinessNodeDao dao = new BusinessNodeDao();
		dao.save(vo);
		// 自动生成表,表名称为bnodeBID_ID
		Interface intfce = new Interface();
		intfce.setId(vo.getId());
		intfce.setFid(vo.getBid());
		intfce.setIpAddress(vo.getDesc());
		intfce.setName(vo.getName());
		intfce.setAlias(vo.getName());
		intfce.setMethod(vo.getMethod());
		intfce.setCategory(81);
		intfce.setStatus(0);
		intfce.setType("业务接口");
		PollingEngine.getInstance().addIntface(intfce);

		return "/businessNode.do?action=list&jp=1";
	}

	/**
	 * 发送更新 businessnode 的页面
	 * 
	 * @return
	 */
	private String readyEdit() {
		BusinessNodeDao businessdao = null;
		String targetJsp = null;
		try {
			businessdao = new BusinessNodeDao();
			setTarget("/business/businessnode/edit.jsp");
			targetJsp = readyEdit(businessdao);
			ManageXmlDao xmldao = new ManageXmlDao();
			List xmllist = new ArrayList();
			try {
				xmllist = xmldao.findByTopoType(1);
			} catch (Exception e) {

			}
			request.setAttribute("xmllist", xmllist);
		} catch (Exception ex) {
			SysLogger.error("BusinessManager.readyEdit()", ex);
		} finally {
			if (businessdao != null) {
				businessdao.close();
			}
		}
		return targetJsp;
	}

	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				BusinessNodeDao bnodedao = new BusinessNodeDao();
				BusinessNode bnode = null;
				try {
					bnode = (BusinessNode) bnodedao.findByID(id);
					bnodedao.delete(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					bnodedao.close();
				}
				String allipstr = bnode.getBid() + "_" + bnode.getId();

				CreateTableManager ctable = new CreateTableManager();
				try {
					// 删除数据采集表
					ctable.deleteTable("bnode", allipstr, "bnode");// Ping
					ctable.deleteTable("bnodehour", allipstr, "bnodehour");// Ping
					ctable.deleteTable("bnodeday", allipstr, "bnodeday");// Ping

					// 删除事件表
					EventListDao eventdao = new EventListDao();
					try {
						// 同时删除事件表里的相关数据
						// eventdao.delete(host.getId(), "network");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						eventdao.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return "/businessNode.do?action=list&jp=1";

	}

	private String search() {
		String managexmlid = getParaValue("managexmlid");
		String value = getParaValue("value");
		BusinessNodeDao dao = new BusinessNodeDao();
		request.setAttribute("list", dao.findByCondition(managexmlid, value));

		return "/business/businessnode/find.jsp";

	}

	public String execute(String action) {
		if (action.equals("list")) {
			ManageXmlDao xmldao = new ManageXmlDao();
			List xmllist = new ArrayList();
			try {
				xmllist = xmldao.findByTopoType(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("xmllist", xmllist);
			return list();
		}

		if (action.equals("add"))
			return save();

		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("ready_add")) {
			ManageXmlDao xmldao = new ManageXmlDao();
			List xmllist = new ArrayList();
			try {
				xmllist = xmldao.findByTopoType(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("xmllist", xmllist);
			return "/business/businessnode/add.jsp";
		}

		if (action.equals("ready_edit")) {
			return readyEdit();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("cancelmanage")) {
			return cancelmanage();
		}
		if (action.equals("addmanage")) {
			return addmanage();
		}
		if (action.equals("search"))
			return search();
		return null;
	}

	private String cancelmanage() {
		BusinessNode vo = new BusinessNode();
		BusinessNodeDao dao = new BusinessNodeDao();
		try {
			vo = (BusinessNode) dao.findByID(getParaValue("id"));
			vo.setFlag(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		dao = new BusinessNodeDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (PollingEngine.getInstance().getNodeByID(vo.getId()) != null) {
			PollingEngine.getInstance().deleteDbByID(vo.getId());
		}

		return "/businessNode.do?action=list";
	}

	private String addmanage() {
		BusinessNode vo = new BusinessNode();
		BusinessNodeDao dao = new BusinessNodeDao();
		try {
			vo = (BusinessNode) dao.findByID(getParaValue("id"));
			vo.setFlag(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		dao = new BusinessNodeDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (PollingEngine.getInstance().getNodeByID(vo.getId()) != null) {
			vo.setFlag(1);
		}

		return "/businessNode.do?action=list";
	}
}
