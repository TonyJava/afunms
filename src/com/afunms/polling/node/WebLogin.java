/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class WebLogin extends Application {

	private int id;
	private String url;
	private int outflag;
	private String outurl;
	private String alias;
	private String user_name;
	private String user_password;
	private String user_code;
	private String name_flag;
	private String password_flag;
	private String code_flag;
	private String timeout;
	private String bid;
	private int flag;
	private String keyword;

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public String getBid() {
		return bid;
	}

	public String getCode_flag() {
		return code_flag;
	}

	public int getFlag() {
		return flag;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getName_flag() {
		return name_flag;
	}

	public int getOutflag() {
		return outflag;
	}

	public String getOuturl() {
		return outurl;
	}

	public String getPassword_flag() {
		return password_flag;
	}

	public String getTimeout() {
		return timeout;
	}

	public String getUrl() {
		return url;
	}

	public String getUser_code() {
		return user_code;
	}

	public String getUser_name() {
		return user_name;
	}

	public String getUser_password() {
		return user_password;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public void setBid(String bid) {
		this.bid = bid;
	}

	public void setCode_flag(String code_flag) {
		this.code_flag = code_flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setName_flag(String name_flag) {
		this.name_flag = name_flag;
	}

	public void setOutflag(int outflag) {
		this.outflag = outflag;
	}

	public void setOuturl(String outurl) {
		this.outurl = outurl;
	}

	public void setPassword_flag(String password_flag) {
		this.password_flag = password_flag;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}

}