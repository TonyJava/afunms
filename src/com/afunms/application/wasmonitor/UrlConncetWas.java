package com.afunms.application.wasmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

@SuppressWarnings("unchecked")
public class UrlConncetWas {
	public Hashtable ConncetWas(String ip, String port, String username, String password, String version) {
		Hashtable washst = new Hashtable();
		if (!"".equalsIgnoreCase(username)) {
			// 如果对perfServletApp设置了访问权限，需要利用Authenticator类来保存访问所需要的用户名和密码，如果对全部用户开放访问权限，则不需要此步骤
			Authenticator.setDefault(new MyAuthenticators(username, password));
		}
		StringBuffer sb = new StringBuffer();
		BufferedReader stdIn = null;
		InputStream input = null;
		try {
			String urlstr = "http://" + ip + ":" + port + "/wasPerfTool/servlet/perfservlet?refreshConfig=true";
			URL url = new URL(urlstr);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setConnectTimeout(30000);// 设置超时
			// 打开到此 URL 的连接并返回一个用于从该连接读入的 InputStream。
			input = urlCon.getInputStream();
			stdIn = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String strLine;
			while ((strLine = stdIn.readLine()) != null) {
				if (strLine.indexOf("performancemonitor.dtd") != -1)
					continue;
				sb.append(strLine);

			}

			Document docRoot = getDocumentFromXML(sb.toString());
			if (version.indexOf("V5") != -1) {

				washst = getWebsphere5XML(ip, docRoot, version);
				return washst;
			} else if (version.indexOf("V6") != -1) {
				washst = getWebsphere61XML(ip, docRoot, version);
				return washst;
			} else {
				washst = getWebsphere7XML(ip, docRoot, version);
				return washst;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stdIn != null) {
				try {
					stdIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return washst;
	}

	public Hashtable ConncetWas(String ip, String port, String username, String password, String version, Hashtable gatherhash) {
		Hashtable<String, String> washst = new Hashtable<String, String>();
		if (!"".equalsIgnoreCase(username)) {
			// 如果对perfServletApp设置了访问权限，需要利用Authenticator类来保存访问所需要的用户名和密码，如果对全部用户开放访问权限，则不需要此步骤
			Authenticator.setDefault(new MyAuthenticators(username, password));
		}
		StringBuffer sb = new StringBuffer();
		BufferedReader stdIn = null;
		InputStream input = null;
		try {
			String urlstr = "http://" + ip + ":" + port + "/wasPerfTool/servlet/perfservlet?refreshConfig=true";
			URL url = new URL(urlstr);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setConnectTimeout(30000);// 设置超时
			// 打开到此 URL 的连接并返回一个用于从该连接读入的 InputStream。
			input = urlCon.getInputStream();
			stdIn = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String strLine;
			while ((strLine = stdIn.readLine()) != null) {
				if (strLine.indexOf("performancemonitor.dtd") != -1)
					continue;
				sb.append(strLine);
			}
			Document docRoot = getDocumentFromXML(sb.toString());

			if (version.indexOf("V5") != -1) {
				washst = getWebsphere5XML(ip, docRoot, version, gatherhash);
			} else if (version.indexOf("V6") != -1) {
				washst = getWebsphere6XML(ip, docRoot, version, gatherhash);
			} else {
				washst = getWebsphere7XML(ip, docRoot, version, gatherhash);
			}
			return washst;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stdIn != null) {
				try {
					stdIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return washst;
	}

	public void parsexml(String ip, String xmlView, String version) {
		Document docRoot = getDocumentFromXML(xmlView);
		// 1:首先取得版本号
		// 如果版本是5.x,则调用5版本的XML文件解析方法,其它情况暂时调用6版本的XML文件解析方法
		if (version.indexOf("V5") != -1) {
			getWebsphere5XML(ip, docRoot, version);
		} else {
			getWebsphere7XML(ip, docRoot, version);
		}
	}

	private Document getDocumentFromXML(String xmlView) {
		if (xmlView == null)
			return null;
		Document resultXMLDoc = null;
		SAXReader saxReader = new SAXReader();
		try {
			resultXMLDoc = saxReader.read(new StringReader(xmlView));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return resultXMLDoc;
	}

	private Hashtable getWebsphere61XML(String ip, Document doc, String version) {
		Hashtable<String, Hashtable<?, ?>> was6hst = new Hashtable();
		Hashtable jvm7hst = new Hashtable();
		Hashtable jdbc7hst = new Hashtable();
		Hashtable thread7hst = new Hashtable();
		Hashtable servlet7hst = new Hashtable();
		Hashtable system7hst = new Hashtable();
		Hashtable trans7hst = new Hashtable();
		Hashtable extension7hst = new Hashtable();

		List<Node> listNodes = doc.selectNodes("//Node");

		if (listNodes == null || listNodes.size() == 0) {
			return null;
		}
		for (Node nodeTmp : listNodes) {
			String nodeName = nodeTmp.valueOf("@name");// 获取到节点的名称
			String xpathServer = "/PerformanceMonitor/" + "Node[@name='" + nodeName + "']/Server/following-sibling::*" + "|/PerformanceMonitor/" + "Node[@name='" + nodeName
					+ "']/Server/self::*";

			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0) {
				// 如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for (Node listServer : listServers) {
				String serverName = listServer.valueOf("@name");// 得到了serverName,其父节点为Node;

				Websphere6XMLParse xml6Parse = new Websphere6XMLParse();
				jvm7hst = xml6Parse.getJVMConfAndPerf(ip, listServer, nodeName, serverName, version);
				jdbc7hst = xml6Parse.getJDBCConfAndPerf(ip, listServer, nodeName, serverName, version);
				thread7hst = xml6Parse.getThreadConfAndPerf(ip, listServer, nodeName, serverName, version);
				servlet7hst = xml6Parse.getServletConfAndPerf(ip, listServer, nodeName, serverName, version);
				system7hst = xml6Parse.getSystemDataConfAndPerf(ip, listServer, nodeName, serverName, version);
				trans7hst = xml6Parse.getTranscationConfAndPerf(ip, listServer, nodeName, serverName, version);
				extension7hst = xml6Parse.getExtensionConfAndPerf(ip, listServer, nodeName, serverName, version);

			}
			was6hst.put("jvm7hst", jvm7hst);
			was6hst.put("jdbc7hst", jdbc7hst);
			was6hst.put("thread7hst", thread7hst);
			was6hst.put("servlet7hst", servlet7hst);
			was6hst.put("system7hst", system7hst);
			was6hst.put("trans7hst", trans7hst);
			was6hst.put("extension7hst", extension7hst);
		}
		return was6hst;

	}

	private Hashtable getWebsphere7XML(String ip, Document doc, String version, Hashtable gatherhash) {
		Hashtable<String, Hashtable<?, ?>> was7hst = new Hashtable();
		Hashtable jvm7hst = new Hashtable();
		Hashtable jdbc7hst = new Hashtable();
		Hashtable thread7hst = new Hashtable();
		Hashtable servlet7hst = new Hashtable();
		Hashtable system7hst = new Hashtable();
		Hashtable trans7hst = new Hashtable();
		Hashtable extension7hst = new Hashtable();

		List<Node> listNodes = doc.selectNodes("//Node");// 所有Node元素
		if (listNodes == null || listNodes.size() == 0) {
			return null;
		}
		for (Node nodeTmp : listNodes) {
			String nodeName = nodeTmp.valueOf("@name");// 获取到节点的名称
			String xpathServer = "/PerformanceMonitor/" + "Node[@name='" + nodeName + "']/Server/following-sibling::*" + "|/PerformanceMonitor/" + "Node[@name='" + nodeName
					+ "']/Server/self::*";

			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0) {
				// 如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for (Node listServer : listServers) {
				String serverName = listServer.valueOf("@name");// 得到了serverName,其父节点为Node;
				Websphere7XMLParse xml7Parse = new Websphere7XMLParse();
				jvm7hst = xml7Parse.getJVMConfAndPerf(ip, listServer, nodeName, serverName, version);
				jdbc7hst = xml7Parse.getJDBCConfAndPerf(ip, listServer, nodeName, serverName, version);
				thread7hst = xml7Parse.getThreadConfAndPerf(ip, listServer, nodeName, serverName, version);
				servlet7hst = xml7Parse.getServletConfAndPerf(ip, listServer, nodeName, serverName, version);
				system7hst = xml7Parse.getSystemDataConfAndPerf(ip, listServer, nodeName, serverName, version);
				trans7hst = xml7Parse.getTranscationConfAndPerf(ip, listServer, nodeName, serverName, version);
				extension7hst = xml7Parse.getExtensionConfAndPerf(ip, listServer, nodeName, serverName, version);

			}
			was7hst.put("jvm7hst", jvm7hst);
			was7hst.put("jdbc7hst", jdbc7hst);
			was7hst.put("thread7hst", thread7hst);
			was7hst.put("servlet7hst", servlet7hst);
			was7hst.put("system7hst", system7hst);
			was7hst.put("trans7hst", trans7hst);
			was7hst.put("extension7hst", extension7hst);
		}
		return was7hst;
	}

	private Hashtable getWebsphere5XML(String ip, Document doc, String version) {
		Hashtable was5hst = new Hashtable();
		Hashtable cachehst = null;
		Hashtable systemDatahst = null;
		Hashtable servlethst = null;
		Hashtable threadhst = null;
		Hashtable transhst = null;
		Hashtable jvmhst = null;
		Hashtable jdbchst = null;

		List<Node> list = doc.selectNodes("//Node");

		if (list == null || list.size() == 0)
			return null;
		for (Node nodeTmp : list) {
			String nodeName = nodeTmp.valueOf("@name");// 获取到节点的名称
			String xpathServer = "/PerformanceMonitor/" + "Node[@name='" + nodeName + "']/Server/following-sibling::*" + "|/PerformanceMonitor/" + "Node[@name='" + nodeName
					+ "']/Server/self::*";
			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0) {
				// 如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for (Node listServer : listServers) {
				String serverName = listServer.valueOf("@name");// 得到了serverName,其父节点为Node;
				Websphere5XMLParse webpshere5XML = new Websphere5XMLParse();
				systemDatahst = webpshere5XML.getSystemData5ConfAndPerf(ip, listServer, nodeName, serverName, version);
				servlethst = webpshere5XML.getServlet5ConfAndPerf(ip, listServer, nodeName, serverName, version);
				threadhst = webpshere5XML.getThread5ConfAndPerf(ip, listServer, nodeName, serverName, version);
				cachehst = webpshere5XML.getCache5ConfAndPerf(ip, listServer, nodeName, serverName, version);
				transhst = webpshere5XML.getTranscation5ConfAndPerf(ip, listServer, nodeName, serverName, version);
				jvmhst = webpshere5XML.getJVM5ConfAndPerf(ip, listServer, nodeName, serverName, version);
				jdbchst = webpshere5XML.getJDBC5ConfAndPerf(ip, listServer, nodeName, serverName, version);
			}
			was5hst.put("cachehst", cachehst);
			was5hst.put("systemDatahst", systemDatahst);
			was5hst.put("servlethst", servlethst);
			was5hst.put("threadhst", threadhst);
			was5hst.put("transhst", transhst);
			was5hst.put("jvmhst", jvmhst);
			was5hst.put("jdbchst", jdbchst);
		}

		return was5hst;

	}

	private Hashtable getWebsphere5XML(String ip, Document doc, String version, Hashtable gatherhash) {
		Hashtable was5hst = new Hashtable();
		Hashtable cachehst = null;
		Hashtable systemDatahst = null;
		Hashtable servlethst = null;
		Hashtable threadhst = null;
		Hashtable transhst = null;
		Hashtable jvmhst = null;
		Hashtable jdbchst = null;

		List<Node> list = doc.selectNodes("//Node");

		if (list == null || list.size() == 0)
			return null;
		for (Node nodeTmp : list) {
			String nodeName = nodeTmp.valueOf("@name");// 获取到节点的名称
			String xpathServer = "/PerformanceMonitor/" + "Node[@name='" + nodeName + "']/Server/following-sibling::*" + "|/PerformanceMonitor/" + "Node[@name='" + nodeName
					+ "']/Server/self::*";
			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0) {
				// 如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for (Node listServer : listServers) {
				String serverName = listServer.valueOf("@name");// 得到了serverName,其父节点为Node;
				Websphere5XMLParse webpshere5XML = new Websphere5XMLParse();

				systemDatahst = webpshere5XML.getSystemData5ConfAndPerf(ip, listServer, nodeName, serverName, version);// system
				jdbchst = webpshere5XML.getJDBC5ConfAndPerf(ip, listServer, nodeName, serverName, version);// jdbc
				servlethst = webpshere5XML.getServlet5ConfAndPerf(ip, listServer, nodeName, serverName, version);// session
				jvmhst = webpshere5XML.getJVM5ConfAndPerf(ip, listServer, nodeName, serverName, version);// jvm
				cachehst = webpshere5XML.getCache5ConfAndPerf(ip, listServer, nodeName, serverName, version);// cache
				threadhst = webpshere5XML.getThread5ConfAndPerf(ip, listServer, nodeName, serverName, version);// thread
				transhst = webpshere5XML.getTranscation5ConfAndPerf(ip, listServer, nodeName, serverName, version);// orb
			}

			was5hst.put("cachehst", cachehst);
			was5hst.put("systemDatahst", systemDatahst);
			was5hst.put("servlethst", servlethst);
			was5hst.put("threadhst", threadhst);
			was5hst.put("transhst", transhst);
			was5hst.put("jvmhst", jvmhst);
			was5hst.put("jdbchst", jdbchst);
		}
		return was5hst;

	}

	private Hashtable getWebsphere7XML(String ip, Document doc, String version) {
		Hashtable<String, Hashtable<?, ?>> was7hst = new Hashtable();
		Hashtable jvm7hst = new Hashtable();
		Hashtable jdbc7hst = new Hashtable();
		Hashtable thread7hst = new Hashtable();
		Hashtable servlet7hst = new Hashtable();
		Hashtable system7hst = new Hashtable();
		Hashtable trans7hst = new Hashtable();
		Hashtable extension7hst = new Hashtable();

		List<Node> listNodes = doc.selectNodes("//Node");

		if (listNodes == null || listNodes.size() == 0) {
			return null;
		}
		for (Node nodeTmp : listNodes) {
			String nodeName = nodeTmp.valueOf("@name");// 获取到节点的名称
			String xpathServer = "/PerformanceMonitor/" + "Node[@name='" + nodeName + "']/Server/following-sibling::*" + "|/PerformanceMonitor/" + "Node[@name='" + nodeName
					+ "']/Server/self::*";

			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0) {
				// 如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for (Node listServer : listServers) {
				String serverName = listServer.valueOf("@name");// 得到了serverName,其父节点为Node;
				Websphere6XMLParse xml6Parse = new Websphere6XMLParse();
				jvm7hst = xml6Parse.getJVMConfAndPerf(ip, listServer, nodeName, serverName, version);
				jdbc7hst = xml6Parse.getJDBCConfAndPerf(ip, listServer, nodeName, serverName, version);
				thread7hst = xml6Parse.getThreadConfAndPerf(ip, listServer, nodeName, serverName, version);
				servlet7hst = xml6Parse.getServletConfAndPerf(ip, listServer, nodeName, serverName, version);
				system7hst = xml6Parse.getSystemDataConfAndPerf(ip, listServer, nodeName, serverName, version);
				trans7hst = xml6Parse.getTranscationConfAndPerf(ip, listServer, nodeName, serverName, version);
				extension7hst = xml6Parse.getExtensionConfAndPerf(ip, listServer, nodeName, serverName, version);

			}
			was7hst.put("jvm7hst", jvm7hst);
			was7hst.put("jdbc7hst", jdbc7hst);
			was7hst.put("thread7hst", thread7hst);
			was7hst.put("servlet7hst", servlet7hst);
			was7hst.put("system7hst", system7hst);
			was7hst.put("trans7hst", trans7hst);
			was7hst.put("extension7hst", extension7hst);
		}
		return was7hst;
	}

	private Hashtable getWebsphere6XML(String ip, Document doc, String version, Hashtable gatherhash) {
		Hashtable<String, Hashtable<?, ?>> was7hst = new Hashtable();
		Hashtable jvm7hst = new Hashtable();
		Hashtable jdbc7hst = new Hashtable();
		Hashtable thread7hst = new Hashtable();
		Hashtable servlet7hst = new Hashtable();
		Hashtable system7hst = new Hashtable();
		Hashtable trans7hst = new Hashtable();
		Hashtable extension7hst = new Hashtable();

		List<Node> listNodes = doc.selectNodes("//Node");

		if (listNodes == null || listNodes.size() == 0) {
			return null;
		}
		for (Node nodeTmp : listNodes) {
			String nodeName = nodeTmp.valueOf("@name");// 获取到节点的名称

			String xpathServer = "/PerformanceMonitor/" + "Node[@name='" + nodeName + "']/Server/following-sibling::*" + "|/PerformanceMonitor/" + "Node[@name='" + nodeName
					+ "']/Server/self::*";

			List<Node> listServers = nodeTmp.getDocument().selectNodes(xpathServer);
			if (listServers == null || listServers.size() == 0) {
				// 如果得到的server为Null,或者得到的个数为0,不要直接返回Null,还需把节点的名称返回过去，可以考虑返回null.
				return null;
			}
			for (Node listServer : listServers) {
				String serverName = listServer.valueOf("@name");// 得到了serverName,其父节点为Node;
				Websphere6XMLParse xml6Parse = new Websphere6XMLParse();
				jvm7hst = xml6Parse.getJVMConfAndPerf(ip, listServer, nodeName, serverName, version);
				jdbc7hst = xml6Parse.getJDBCConfAndPerf(ip, listServer, nodeName, serverName, version);
				thread7hst = xml6Parse.getThreadConfAndPerf(ip, listServer, nodeName, serverName, version);
				servlet7hst = xml6Parse.getServletConfAndPerf(ip, listServer, nodeName, serverName, version);
				system7hst = xml6Parse.getSystemDataConfAndPerf(ip, listServer, nodeName, serverName, version);
				trans7hst = xml6Parse.getTranscationConfAndPerf(ip, listServer, nodeName, serverName, version);
				extension7hst = xml6Parse.getExtensionConfAndPerf(ip, listServer, nodeName, serverName, version);

			}
			was7hst.put("jvm7hst", jvm7hst);
			was7hst.put("jdbc7hst", jdbc7hst);
			was7hst.put("thread7hst", thread7hst);
			was7hst.put("servlet7hst", servlet7hst);
			was7hst.put("system7hst", system7hst);
			was7hst.put("trans7hst", trans7hst);
			was7hst.put("extension7hst", extension7hst);
		}
		return was7hst;
	}

	public boolean connectWasIsOK(String ip, int port) {

		String urlstr = "http://" + ip + ":" + port + "/wasPerfTool/servlet/perfservlet?refreshConfig=true";
		try {
			URL url = new URL(urlstr);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setConnectTimeout(30000);// 设置超时
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
