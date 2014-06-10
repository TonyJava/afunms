package com.afunms.cabinet.model;

import java.util.ArrayList;
import java.util.List;

import com.afunms.cabinet.dao.MachineCabinetDao;

@SuppressWarnings("unchecked")
public class DaoHangSerivce {
	public List<String> mcallId(String id) {
		List list = new ArrayList();
		MachineCabinetDao dao = new MachineCabinetDao();
		try {
			list = dao.loadId(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return list;
	}

}
