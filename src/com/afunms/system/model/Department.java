/**
 * <p>Description:mapping table NMS_DEPARTMENT</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class Department extends BaseVo {
	private int id;
	private String dept;
	private String tel;
	private String man;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Department)) {
			return false;
		}

		Department that = (Department) obj;
		if (this.id == that.id) {
			return true;
		} else {
			return false;
		}
	}

	public String getDept() {
		return dept;
	}

	public int getId() {
		return id;
	}

	public String getMan() {
		return man;
	}

	public String getTel() {
		return tel;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = result * 31 + this.id;
		return result;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMan(String man) {
		this.man = man;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
}
