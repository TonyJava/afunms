<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.util.TreeDS"%>
<% 
        TreeDS treeDS = new TreeDS();
		String treeJson = "";
		try {
			treeJson = treeDS.getTree();
			System.out.println("====="+treeJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println(treeJson);
%>