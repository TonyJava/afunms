package com.afunms.webservice.impl;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.webservice.dao.RelationDao;
import com.afunms.webservice.model.Attribute;
import com.afunms.webservice.model.MoAndCiRelation;

@SuppressWarnings("unchecked")
public class WebserviceManger extends BaseManager implements ManagerInterface {

	public String execute(String action) {

		if (action.equals("synchronousData")) {
			return synchronousData();
		}
		if (action.equals("list")) {
			return list();
		}
		return null;
	}

	public String synchronousData() {
		DeviceInfoImpl impl = new DeviceInfoImpl();
		Attribute attribute = new Attribute();
		attribute.setMoId("2");
		attribute.setAttributeName("statupTime");
		Attribute attribute0 = new Attribute();
		attribute0.setMoId("2");
		attribute0.setAttributeName("softwareName");
		Attribute attribute1 = new Attribute();
		attribute1.setMoId("2");
		attribute1.setAttributeName("diskName");
		Attribute attribute2 = new Attribute();
		attribute2.setMoId("6");
		attribute2.setAttributeName("dbName");
		Attribute[] attributes = { attribute0, attribute, attribute1, attribute2 };
		impl.getMoAttributeValue(attributes);
		return "/webservice/webservice.jsp";
	}

	public String sendChangedMoIds() {
		RelationDao dao = new RelationDao();
		List list = dao.loadAll();
		String[] moIds = null;
		if (list != null && list.size() > 0) {
			moIds = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				MoAndCiRelation model = (MoAndCiRelation) list.get(i);
				moIds[i] = model.getMoId() + "";
			}
		}
		return "/webservice/webservice.jsp";
	}

	public String list() {
		return "/webservice/webservice.jsp";
	}
}
