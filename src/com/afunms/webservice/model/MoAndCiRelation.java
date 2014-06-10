package com.afunms.webservice.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/**
 * @description 对象节点ID和类型代码的关系
 * @author wangxiangyong
 * @date Jan 17, 2012 3:35:36 PM
 */
public class MoAndCiRelation extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6785396127525579378L;
	private int id;// 唯一标识ID
	private int moId;// 对象节点ID,相当于网管系统的设备节点ID,注意：接口的id类型和接口属性的Model类型不一致
	private String ciType;// 类型代码（此处的代码不一定对应nms_webservice_ciType,此处只做辨别）

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMoId() {
		return moId;
	}

	public void setMoId(int moId) {
		this.moId = moId;
	}

	public String getCiType() {
		return ciType;
	}

	public void setCiType(String ciType) {
		this.ciType = ciType;
	}

}
