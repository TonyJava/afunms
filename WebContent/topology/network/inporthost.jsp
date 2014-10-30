<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>
<%@ page import="com.afunms.polling.om.ARP"%>
<%@ page import="com.afunms.topology.model.*"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
	String rootPath = request.getContextPath();
	List arpList = (List)request.getAttribute("arpList");
     String equipName = (String)request.getAttribute("equipName");
     String nodeId = (String)request.getAttribute("nodeId");
     String type = (String)request.getAttribute("type");
     String galleryPanel = (String)request.getAttribute("galleryPanel");
     String gallery = (String)request.getAttribute("gallery");
     String category = (String)request.getAttribute("category");	
%>
<html>
<head>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
		<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>


<script language="JavaScript" type="text/javascript" fptype="dynamicoutline">
function valid(){
 		var nodeid;
		var nodename;
		var nodedescr;
		var formItem=document.forms["mainForm"];
		var formElms=formItem.elements;
		var l=formElms.length;
		var flag = false;
		while(l--){
			if(formElms[l].type=="checkbox"){
				var checkbox=formElms[l];
				if(checkbox.name == "checkbox" && checkbox.checked==true){
					flag = true;
					break;
					//alert(checkbox.value);
 				}
			}
		}
		mainForm.action = "<%=rootPath%>/submap.do?action=doinporthost";
     	mainForm.submit();
		window.close();
	}	 
</script>
<script type="text/javascript" src="<%=rootPath%>/topology/network/js/wincloseoverride.js"></script>
</head>
<body id="body" class="body" >

	<!-- 右键菜单结束-->
   <form name="mainForm" method="post">
   <input type=hidden name="nodeId" value=<%=nodeId%>>
		<table id="body-container" class="body-container">
			<tr>
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td>
											<table id="add-content" class="add-content">
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">选择服务器</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																		<TBODY>
																			<tr align="right" > 
							  													<td colspan=10 align=left height=18>
							  															<table width="100%" cellSpacing="0"  cellPadding="0" border=0  class="noprint">
																							  <tr >
																							  <td align="right" height=18>
																							  		&nbsp;  
																							    </td> 
																							    </tr> 
																						</table>
																			    </td>
																			  </tr>
							  <tr align="center">
							  <td style="align:center">
							  		<table style="width:90%;align:center">
							  			<tr bgcolor=gray>
							  				<td class="report-data-body-title" width=10% style="bgcolor:gray"><INPUT type="checkbox" name="checkall" onclick="javascript:chkall()">序号</td>
							  				<td class="report-data-body-title" width=30% bgcolor=gray>IP地址</td>
							  				<td class="report-data-body-title" width=30% bgcolor=gray>MAC</td>
							  				<td class="report-data-body-title" width=20% bgcolor=gray>上联端口</td>
							  				<td class="report-data-body-title" width=10% bgcolor=gray>是否开启SNMP</td>
							  			</tr>
							  			<%
							  				ARP vo = null;
							  				for(int i=0;i<arpList.size();i++)
							  				{
							  					vo = (ARP)arpList.get(i);
							  					String snmp = "否";
							  					if(vo.getMonflag() == 1)snmp="是";
							  					if(i%2==0){
							  			%>
							  			<tr>
							  				<td class="report-data-body-list" height="20" bgcolor=#C0C0C0 align=center><input type=checkbox name=checkbox value='<%=vo.getId()%>' class=noborder ></td>
							  				<td class="report-data-body-list" height="20" bgcolor=#C0C0C0 align=center><%=vo.getIpaddress()%></td>
							  				<td class="report-data-body-list" height="20" bgcolor=#C0C0C0 align=center><%=vo.getPhysaddress()%></td>
							  				<td class="report-data-body-list" height="20" bgcolor=#C0C0C0 align=center><%=vo.getIfindex()%></td>
							  				<td class="report-data-body-list" height="20" bgcolor=#C0C0C0 align=center><%=snmp%></td>
							  			</tr>
							  			<%
							  			}else{
							  			%>		
							  			<tr>
							  				<td class="report-data-body-list" height="20" align=center><input type=checkbox name=checkbox value='<%=vo.getId()%>' class=noborder ></td>
							  				<td class="report-data-body-list" height="20" align=center><%=vo.getIpaddress()%></td>
							  				<td class="report-data-body-list" height="20" align=center><%=vo.getPhysaddress()%></td>
							  				<td class="report-data-body-list" height="20" align=center><%=vo.getIfindex()%></td>
							  				<td class="report-data-body-list" height="20" align=center><%=snmp%></td>
							  			</tr>
							  			<%
							  			}
							  			}
							  			 %>					  			
							  		</table>
							  </td>
							  </tr>
																			<tr align="right" > 
							  													<td colspan=10 align=left height=18>
							  														<br>
							  															<table width="100%" cellSpacing="0"  cellPadding="0" border=0  class="noprint">
																							  <tr >
																							  <td align="center" height=18>
																							  		<input type="button" value="确 定"  id=button2  name="button2" class=button onclick="return valid()">
																							<!--<input type="button" value="确 定" style="width:50" id="process" onclick="#">-->
																									<INPUT type="button" value="关 闭" id=button1 name=button1 onclick="window.close()" class="button"> 
																							           
																							    </td> 
																							    </tr> 
																						</table>
																			    </td>
																			  </tr>							  
            					</tbody>
            					</table>
            					<br>
			</TD>																			
			</tr>			
														
																 							
										 							
				        								</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-footer" class="detail-content-footer">
				        									<tr>
				        										<td>
				        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
											                  			<tr>
											                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
											                    			<td></td>
											                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
											                  			</tr>
											              			</table>
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        					</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		
	</form>
</BODY>

</HTML>