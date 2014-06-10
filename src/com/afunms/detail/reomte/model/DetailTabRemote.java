
package com.afunms.detail.reomte.model;

import java.util.List;

/**
 * 
 * 该类用于封装详细信息标签
 * 
 * @author 聂林
 * @date 2010-12-10
 * 
 */
public class DetailTabRemote {

	private String id;

	private String name;

	private String action;

	private List<DetailTitleRemote> titleList;

	/**
	 * @return the action
	 */
	public final String getAction() {
		return action;
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the titleList
	 */
	public final List<DetailTitleRemote> getTitleList() {
		return titleList;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public final void setAction(String action) {
		this.action = action;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(String id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @param titleList
	 *            the titleList to set
	 */
	public final void setTitleList(List<DetailTitleRemote> titleList) {
		this.titleList = titleList;
	}

}
