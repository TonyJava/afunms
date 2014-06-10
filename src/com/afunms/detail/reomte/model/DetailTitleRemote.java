package com.afunms.detail.reomte.model;

public class DetailTitleRemote {

	/**
	 * 标题 id
	 */
	private String id;

	/**
	 * 标题内容
	 */
	private String content;

	/**
	 * 标题所占列数
	 */
	private String cols;

	/**
	 * 标题所在行号
	 */
	private String rowNum;

	/**
	 * @return the cols
	 */
	public String getCols() {
		return cols;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the rowNum
	 */
	public String getRowNum() {
		return rowNum;
	}

	/**
	 * @param cols
	 *            the cols to set
	 */
	public void setCols(String cols) {
		this.cols = cols;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param rowNum
	 *            the rowNum to set
	 */
	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}

}
