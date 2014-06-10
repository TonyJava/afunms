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
	 * 得到部门selectbox
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
	 * 得到职务selectbox
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
	 * 得到角色selectbox
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
	 * 得到角色selectbox 当修改admin 用户的时候 选择框为不可用 konglq
	 */
	public String getRoleBox(int index, int role) {
		StringBuffer sb = new StringBuffer(1000);

		// 当前用户不能 修改大于等于的自己权限角色
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
	 * 得到性别selectbox
	 */
	public String getSexBox(int index) {
		StringBuffer sb = new StringBuffer(500);
		sb.append("<select size=1 name='sex' style='width:108px;'>");
		if (index == 1) {
			sb.append("<option value=1 selected>男</option>");
			sb.append("<option value=2>女</option>");
		} else {
			sb.append("<option value=1>男</option>");
			sb.append("<option value=2 selected>女</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

}
