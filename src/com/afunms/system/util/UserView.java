package com.afunms.system.util;

import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.system.dao.PositionDao;
import com.afunms.system.dao.RoleDao;
import com.afunms.system.model.Department;
import com.afunms.system.model.Position;
import com.afunms.system.model.Role;

@SuppressWarnings("unchecked")
public class UserView {
	private List deptList;
	private List positionList;
	private List roleList;

	public UserView() {
		BaseDao dao = null;
		dao = new DepartmentDao();
		deptList = dao.loadAll();

		dao = new PositionDao();
		positionList = dao.loadAll();

		RoleDao rd = new RoleDao();
		roleList = rd.loadAll(true);
	}

	public String getDept(int id) {
		Department tmpObj = new Department();
		tmpObj.setId(id);

		int index = deptList.indexOf(tmpObj);
		if (index == -1) {
			return "";
		}
		return ((Department) deptList.get(index)).getDept();
	}

	public String getDeptBox() {
		return getDeptBox(0);
	}

	/**
	 * �õ�����selectbox
	 */
	public String getDeptBox(int index) {
		StringBuffer sb = new StringBuffer(1000);
		sb.append("<select size=1 name='dept' style='width:108px;'>");

		Department vo = null;
		for (int i = 0; i < deptList.size(); i++) {
			vo = (Department) deptList.get(i);
			if (index == vo.getId()) {
				sb.append("<option value='" + vo.getId() + "' selected>");
			} else {
				sb.append("<option value='" + vo.getId() + "'>");
			}
			sb.append(vo.getDept());
			sb.append("</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

	public String getPosition(int id) {
		Position tmpObj = new Position();
		tmpObj.setId(id);

		int index = positionList.indexOf(tmpObj);
		if (index == -1) {
			return "";
		}
		return ((Position) positionList.get(index)).getName();
	}

	public String getPositionBox() {
		return getPositionBox(0);
	}

	/**
	 * �õ�ְ��selectbox
	 */
	public String getPositionBox(int index) {
		StringBuffer sb = new StringBuffer(1000);
		sb.append("<select size=1 name='position' style='width:108px;'>");

		Position vo = null;
		for (int i = 0; i < positionList.size(); i++) {
			vo = (Position) positionList.get(i);
			if (index == vo.getId()) {
				sb.append("<option value='" + vo.getId() + "' selected>");
			} else {
				sb.append("<option value='" + vo.getId() + "'>");
			}
			sb.append(vo.getName());
			sb.append("</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

	public String getRole(int id) {

		Role tmpObj = new Role();
		tmpObj.setId(id);

		int index = roleList.indexOf(tmpObj);
		if (index == -1) {
			return "";
		}
		return ((Role) roleList.get(index)).getRole();
	}

	public String getRoleBox() {
		return getRoleBox(0);
	}

	/**
	 * �õ���ɫselectbox
	 */
	public String getRoleBox(int index) {
		StringBuffer sb = new StringBuffer(1000);
		sb.append("<select size=1 name='role' style='width:108px;'>");

		Role vo = null;
		for (int i = 0; i < roleList.size(); i++) {
			vo = (Role) roleList.get(i);
			if (index == vo.getId()) {
				sb.append("<option value='" + vo.getId() + "' selected>");
			} else {
				sb.append("<option value='" + vo.getId() + "'>");
			}
			sb.append(vo.getRole());
			sb.append("</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * �õ���ɫselectbox ���޸�admin �û���ʱ�� ѡ���Ϊ������ konglq
	 */
	public String getRoleBox(int index, int role) {
		StringBuffer sb = new StringBuffer(1000);

		// ��ǰ�û����� �޸Ĵ��ڵ��ڵ��Լ�Ȩ�޽�ɫ
		if (role >= index) {
			sb.append("<input name='role' value='" + index + "' type='hidden' />");
			sb.append(getRole(index));
			return sb.toString();
		}

		if (role == 0) {
			sb.append(getRoleBox(index));
		} else {

			sb.append(getRole(index));
		}

		return sb.toString();
	}

	public String getSexBox() {
		return getSexBox(1);
	}

	/**
	 * �õ��Ա�selectbox
	 */
	public String getSexBox(int index) {
		StringBuffer sb = new StringBuffer(500);
		sb.append("<select size=1 name='sex' style='width:108px;'>");
		if (index == 1) {
			sb.append("<option value=1 selected>��</option>");
			sb.append("<option value=2>Ů</option>");
		} else {
			sb.append("<option value=1>��</option>");
			sb.append("<option value=2 selected>Ů</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

}
