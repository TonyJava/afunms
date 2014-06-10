package com.afunms.topology.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;

@SuppressWarnings("unchecked")
public class SaveImageManager extends BaseManager implements ManagerInterface {

	private String list() {
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadNetwork(0));
		return "/topology/network/list.jsp";
	}

	private String read() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/read.jsp");
		return readyEdit(dao);
	}

	private String readyEdit() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/edit.jsp");
		return readyEdit(dao);
	}

	private String update() {
		HostNode vo = new HostNode();
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));

		// 更新内存
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		host.setAlias(vo.getAlias());

		// 更新数据库
		DaoInterface dao = new HostNodeDao();
		setTarget("/network.do?action=list");
		return update(dao, vo);
	}

	private String refreshsysname() {
		HostNodeDao dao = new HostNodeDao();
		String sysName = "";
		sysName = dao.refreshSysName(getParaIntValue("id"));

		// 更新内存
		Host host = (Host) PollingEngine.getInstance().getNodeByID(getParaIntValue("id"));
		if (host != null) {
			host.setSysName(sysName);
			host.setAlias(sysName);
		}

		return "/network.do?action=list";
	}

	private String delete() {
		String id = getParaValue("radio");

		PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));
		HostNodeDao dao = new HostNodeDao();
		dao.delete(id);
		return "/network.do?action=list";
	}

	private String add() {
		String ipAddress = getParaValue("ip_address");
		String alias = getParaValue("alias");
		String community = getParaValue("community");
		String writecommunity = getParaValue("writecommunity");
		int type = getParaIntValue("type");

		TopoHelper helper = new TopoHelper(); // 包括更新数据库和更新内存
		int addResult = helper.addHost(ipAddress, alias, community, writecommunity, type); // 加入一台服务器
		if (addResult == 0) {
			setErrorCode(ErrorMessage.ADD_HOST_FAILURE);
			return null;
		}
		if (addResult == -1) {
			setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
			return null;
		}
		if (addResult == -2) {
			setErrorCode(ErrorMessage.PING_FAILURE);
			return null;
		}
		if (addResult == -3) {
			setErrorCode(ErrorMessage.SNMP_FAILURE);
			return null;
		}

		// 2.更新xml
		XmlOperator opr = new XmlOperator();
		opr.setFile("network.jsp");
		opr.init4updateXml();
		opr.addNode(helper.getHost());
		opr.writeXml();

		return "/network.do?action=list";
	}

	private String find() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.findByCondition(key, value));

		return "/topology/network/find.jsp";
	}

	private String save() {
		String xmlString = request.getParameter("hidXml");
		String vlanString = request.getParameter("vlan");
		xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		XmlOperator xmlOpr = new XmlOperator();
		if (vlanString != null && vlanString.equals("1")) {
			xmlOpr.setFile("networkvlan.jsp");
		} else
			xmlOpr.setFile("network.jsp");
		xmlOpr.saveImage(xmlString);

		return "/topology/network/save.jsp";
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("read"))
			return read();
		if (action.equals("ready_edit"))
			return readyEdit();
		if (action.equals("update"))
			return update();
		if (action.equals("refreshsysname"))
			return refreshsysname();
		if (action.equals("delete"))
			return delete();
		if (action.equals("find"))
			return find();
		if (action.equals("ready_add"))
			return "/topology/network/add.jsp";
		if (action.equals("add"))
			return add();
		if (action.equals("save"))
			return save();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
