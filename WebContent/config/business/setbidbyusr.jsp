<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="com.afunms.config.dao.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<% 
	String rootPath = request.getContextPath(); 
	List alluser = (List)request.getAttribute("alluser"); 
	List bidIsSelected = (List)request.getAttribute("bidIsSelected");
	String eventText = (String)request.getAttribute("eventText");
	String event = (String)request.getAttribute("event");

%>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>业务选择窗口</title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<link type="text/css" href="<%=rootPath%>/config/business/businessTree.css" rel="stylesheet"/>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/config/business/checkboxTree.js"></script>

<script>
	var tree;
	var dataArray = new Array();
	<%
		if(alluser != null ){
			for(int i = 0 ; i < alluser.size() ; i++){
				String usrid = (String)alluser.get(i);
				boolean isSelected = false;
				if(bidIsSelected!=null && bidIsSelected.contains(usrid)){
					isSelected = true;
				}
				if(usrid.trim()!=null&&usrid.trim()!=""){
				%>
				dataArray[<%=i%>] = ['<%=usrid.trim()%>' , '<%=usrid.trim()%>' , '1' ,  <%=isSelected%>];
				<%
				}
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
		var oids ="";  
		var formItem=document.forms["mainForm"];  
		var formElms=formItem.elements;  
		var l=formElms.length;  
		while(l--){   
			if(formElms[l].type=="checkbox"){    
				var checkbox=formElms[l];    
				if(checkbox.name == "checkbox" && checkbox.checked==true){      
					if (oids==""){       
						oids=","+checkbox.value+",";      
					}else{       
						oids=oids+","+checkbox.value;      
					}     
				}   
			}  
		}  
		var event = parent.opener.document.getElementById("bidtext"); 
		event.value=oids;        
		window.close();
	}
	
	function cancel(){
		window.close();
	}
	
	
	function reset(){
		tree.reset();
	}
</script>

<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/common/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>/menubg_report.jpg);
TEXT-ALIGN: center; 
}
-->
</style>
</head>
<body style="margin:10px">
<!--	<div id="div"><input type="button" value="showTree" onclick="show('businessTree')"></div>-->
		
		<form id="mainForm" method="post" name="mainForm">
		<table width=98% bgcolor=#ffffff>
		<tr>
		<td>
		<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title" align=left> >> 业务列表 </td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
		</td>
		</tr>
		
		<%
		if(alluser != null ){
			for(int i = 0 ; i < alluser.size() ; i++){
				String usrid = (String)alluser.get(i);
				BusinessDao bdao = new BusinessDao();
				Business bvo = (Business)bdao.loadBidbyID(usrid);
			    String bidname = bvo.getName();
				if(usrid.trim()!=null&&usrid.trim()!=""){
				%>
				<tr>
				<td>
				<div id="businessTree"  class="businessTree" style=" margin-left: 10%; margin-top: 10px; width: 200px; height: 30px;margin-bottom: 0px;TEXT-ALIGN: left;"><INPUT type="checkbox" name=checkbox value="<%=usrid%>"><%=bidname%><br></div>
				</td>
				</tr>
				<%
				}
			}
		}
	
	%>
		<tr><td>
		<div style="margin-bottom: 5px">
		<br>
			<input type="button" value="确  定" onclick="confirm()">&nbsp;&nbsp;
			<input type="button" value="取  消" onclick="cancel()">
		</div>
		</tr></td>
		</table>
	</form>
</body>
</html>