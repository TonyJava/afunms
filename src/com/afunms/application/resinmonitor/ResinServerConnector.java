package com.afunms.application.resinmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.PropertyConfigurator;

@SuppressWarnings("unchecked")
public class ResinServerConnector {
	private String webServerHost = "localhost";
	private int webServerPort = 8080;
	private String statusPath = "/jkstatus";
	// 管理员用户名密码
	private String user = "admin";
	private String pass = "";

	private String qry;

	private HashMap mStream;

	// 取得stream
	public HashMap getMStream() {
		return mStream;
	}

	public String getPass() {
		return pass;
	}

	protected InputStream getPostStream(String qry) throws Exception {
		InputStream is = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://" + webServerHost + ":" + webServerPort + statusPath);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("j_username", user));
			nameValuePairs.add(new BasicNameValuePair("j_password", pass));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			Header[] header = response.getHeaders("location");
			if (header.length > 0) {
				httppost.releaseConnection();
				httppost = new HttpPost(header[0].getValue());
				response = httpclient.execute(httppost);
				entity = response.getEntity();
			}
			if (entity != null) {
				is = entity.getContent();
			}
			return is;
		} catch (IOException e) {
			return null;
		}
	}

	// 取得过滤器
	public String getQry() {
		return qry;
	}

	public String getStatusPath() {
		return statusPath;
	}

	public String getUser() {
		return user;
	}

	public String getWebServerHost() {
		return webServerHost;
	}

	public int getWebServerPort() {
		return webServerPort;
	}

	public void resininit() throws IOException {
		try {
			PropertyConfigurator.configure(getClass().getClassLoader().getResource("log4j.properties"));
			setMStream(null);
			streamToVector(getPostStream(getQry()));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void resinStart() throws IOException {
		resininit();
	}

	// 保存stream
	public void setMStream(HashMap mStream) {
		this.mStream = mStream;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	// 设置过滤器
	public void setQry(String qry) {
		this.qry = qry;
	}

	public void setStatusPath(String statusPath) {
		this.statusPath = statusPath;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setWebServerHost(String webServerHost) {
		this.webServerHost = webServerHost;
	}

	public void setWebServerPort(int webServerPort) {
		this.webServerPort = webServerPort;
	}

	public void streamToVector(InputStream is) {
		HashMap map = new HashMap();
		if (is != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			int i = 0;
			try {
				while ((line = br.readLine()) != null && "" != line) {
					map.put(String.valueOf(i), line);
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setMStream(map);
	}
}
