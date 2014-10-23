<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.model.Business"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<% 
	String rootPath = request.getContextPath(); 
	List allbusiness = (List)request.getAttribute("allbusiness"); 
	List bidIsSelected = (List)request.getAttribute("bidIsSelected");
	String eventText = (String)request.getAttribute("eventText");
	String event = (String)request.getAttribute("event");

%>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>Insert title here</title>

<link type="text/css" href="<%=rootPath%>/config/business/businessTree.css" rel="stylesheet"/>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/config/business/checkboxTree.js"></script>

<script>
	var tree;
	var dataArray = new Array();
	<%
		if(allbusiness != null ){
			for(int i = 0 ; i < allbusiness.size() ; i++){
				Business business = (Business)allbusiness.get(i);
				boolean isSelected = false;
				if(bidIsSelected!=null && bidIsSelected.contains(business.getId())){
					isSelected = true;
				}
				
				%>
				dataArray[<%=i%>] = ['<%=business.getId()%>' , '<%=business.getName()%>' , <%=business.getPid()%> ,  <%=isSelected%>];
				<%
			}
		}
	
	%>
	
	function showTree(){
		tree = new Tree();
		tree.init(dataArray);
		tree.show("businessTree");
	}

	function allChoose(){
		tree.allChoose();
	}

	function allNotChoose(){
		tree.allNotChoose();
	}
	
	function antiChoose(){
		tree.antiChoose();
	}

	function confirm(){
		var isSelectedNodeArray = tree.getIsSelectedNode(true);
		var eventTextvalue = '';
		var eventValue = '';
		if(isSelectedNodeArray){
			for(var i = 0 ; i< isSelectedNodeArray.length ; i++){
				var pernode = isSelectedNodeArray[i];
				eventValue = eventValue + ','  + pernode.id + ',';
				eventTextvalue = eventTextvalue + ',' + pernode.name+ ',';
			}
		}
		var event = parent.opener.document.getElementById("<%=event%>");
		var eventText = parent.opener.document.getElementById("<%=eventText%>");
 		event.value=eventValue; 
 		eventText.value = eventTextvalue;   
		window.close();
	}
	
	function cancel(){
		window.close();
	}
	
	
	function reset(){
		tree.reset();
	}
</script>
<%
	String imgpath = com.afunms.common.util.CommonAppUtil.getSkinPath();
	if(imgpath == null){
		imgpath="images";
	} else {
	  if( "null".equals(imgpath) || "".equals(imgpath))imgpath="images";
	 }
	 //System.out.println(imgpath);
%>
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/common/<%=imgpath %>/menubg_report.jpg);
TEXT-ALIGN: center; 
}
-->
</style>
</head>
<body onload="showTree()" style="margin:0px">
<!--	<div id="div"><input type="button" value="showTree" onclick="show('businessTree')"></div>-->
	<div>
		<div style="margin-bottom: 15px">
		<br>
			<input type="button" value="全  选" onclick="allChoose()">
			<input type="button" value="全不选" onclick="allNotChoose()">
			<input type="button" value="反  选" onclick="antiChoose()">
			<input type="button" value="确  定" onclick="confirm()">
			<input type="button" value="取  消" onclick="cancel()">
			<input type="button" value="重  置" onclick="reset()">
		</div>
		<div id="businessTree"  class="businessTree" style="background-color: white; width: 575px; height: 330px; margin-left: 2px;margin-bottom: 0px;TEXT-ALIGN: left;"></div>
		
	</div>
</body>
</html>