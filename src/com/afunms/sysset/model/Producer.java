package com.afunms.sysset.model;

import com.afunms.common.base.BaseVo;

public class Producer extends BaseVo {
	private int id;
	private String producer;
	private String enterpriseOid;
	private String website;

	public Producer() {
	}

	public Producer(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Producer)) {
			return false;
		}

		Producer that = (Producer) obj;
		if (this.id == that.id) {
			return true;
		} else {
			return false;
		}
	}

	public String getEnterpriseOid() {
		return enterpriseOid;
	}

	public int getId() {
		return id;
	}

	public String getProducer() {
		return producer;
	}

	public String getWebsite() {
		return website;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = result * 31 + this.id;
		return result;
	}

	public void setEnterpriseOid(String enterpriseOid) {
		this.enterpriseOid = enterpriseOid;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
}
