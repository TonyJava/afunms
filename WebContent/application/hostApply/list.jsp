<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.alarm.model.AlarmIndicators"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.alarm.model.AlarmIndicatorsNode"%>
<%@page import="com.afunms.application.model.HostApplyModel"%>
<%@page import="com.afunms.application.model.HostApply" %>
<%@page import="com.afunms.polling.node.Host"%>
<%@page import="com.afunms.application.manage.HostApplyManager"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.alarm.service.NodeAlarmService"%>
<%@ include file="/include/globe.inc"%>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  String nodeid = (String)request.getAttribute("nodeid");
  String type = (String)request.getAttribute("type");
  String subtype = (String)request.getAttribute("subtype");
  String ipaddress=(String)request.getAttribute("ipaddress");
  Hashtable<String,HostApply> hostApplyHash = (Hashtable<String,HostApply>)request.getAttribute("hostApplyHash");
%>


<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<style type="text/css">
			#全局表格样式
			.totalTable{
				background-color: white;
				width: 100%;
				height: 100%;
			}
		.data_content{
			height:20px;
			border-top:1px dotted #999999;
			color:#333333;
			padding:2px;
		}
		.data{
			border:1px solid #679AD4;
			width:100%;
		}
		</style>
		<script type="text/javascript">
		 	
			var show = true;
			var hide = false;
			//修改菜单的上下箭头符号
			function my_on(head,body)
			{
				var tag_a;
				for(var i=0;i<head.childNodes.length;i++)
				{
					if (head.childNodes[i].nodeName=="A")
					{
						tag_a=head.childNodes[i];
						break;
					}
				}
				tag_a.className="on";
			}
			function my_off(head,body)
			{
				var tag_a;
				for(var i=0;i<head.childNodes.length;i++)
				{
					if (head.childNodes[i].nodeName=="A")
					{
						tag_a=head.childNodes[i];
						break;
					}
				}
				tag_a.className="off";
			}
			//添加菜单	
			function initmenu()
			{
				var idpattern=new RegExp("^menu");
				var menupattern=new RegExp("child$");
				var tds = document.getElementsByTagName("div");
				for(var i=0,j=tds.length;i<j;i++){
					var td = tds[i];
					if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
						menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
						menu.init();		
					}
				}
			
			}
		</script>
		<script language="JavaScript">

			//公共变量
			var node="";
			var ipaddress="";
			var operate="";
			/**
			*根据传入的id显示右键菜单
			*/
			function showMenu(id,nodeid,ip)
			{	
				ipaddress=ip;
				node=nodeid;
				//operate=oper;
			    if("" == id)
			    {
			        popMenu(itemMenu,100,"100");
			    }
			    else
			    {
			        popMenu(itemMenu,100,"1111");
			    }
			    event.returnValue=false;
			    event.cancelBubble=true;
			    return false;
			}
			/**
			*显示弹出菜单
			*menuDiv:右键菜单的内容
			*width:行显示的宽度
			*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
			*/
			function popMenu(menuDiv,width,rowControlString)
			{
			    //创建弹出菜单
			    var pop=window.createPopup();
			    //设置弹出菜单的内容
			    pop.document.body.innerHTML=menuDiv.innerHTML;
			    var rowObjs=pop.document.body.all[0].rows;
			    //获得弹出菜单的行数
			    var rowCount=rowObjs.length;
			    //alert("rowCount==>"+rowCount+",rowControlString==>"+rowControlString);
			    //循环设置每行的属性
			    for(var i=0;i<rowObjs.length;i++)
			    {
			        //如果设置该行不显示，则行数减一
			        var hide=rowControlString.charAt(i)!='1';
			        if(hide){
			            rowCount--;
			        }
			        //设置是否显示该行
			        rowObjs[i].style.display=(hide)?"none":"";
			        //设置鼠标滑入该行时的效果
			        rowObjs[i].cells[0].onmouseover=function()
			        {
			            this.style.background="#397DBD";
			            this.style.color="white";
			        }
			        //设置鼠标滑出该行时的效果
			        rowObjs[i].cells[0].onmouseout=function(){
			            this.style.background="#F1F1F1";
			            this.style.color="black";
			        }
			    }
			    //屏蔽菜单的菜单
			    pop.document.oncontextmenu=function()
			    {
			            return false; 
			    }
			    //选择右键菜单的一项后，菜单隐藏
			    pop.document.onclick=function()
			    {
			        pop.hide();
			    }
			    //显示菜单
			    pop.show(event.clientX-1,event.clientY,width,rowCount*25,document.body);
			    return true;
			}
			
			 /***********************************************************************************************************
			修改服务器应用系统的isShow标志位
			 参数：
			 	modifyFlag isShow标志位 true/false
			 ***********************************************************************************************************/
			function show(modifyFlag){
				/*
				var eventids = ''; 
				var formItem=document.forms["mainForm"];
				var formElms=formItem.elements;
				var l=formElms.length;
				while(l--){
					if(formElms[l].type=="checkbox"){
						var checkbox=formElms[l];
						if(checkbox.name == "checkbox" && checkbox.checked==true){
			 				if (eventids==""){
			 					eventids=checkbox.value;
			 				}else{
			 					eventids=eventids+","+checkbox.value;
			 				}
		 				}
					}
				}
		        if(eventids == ""){
		        	alert("未选中");
		        	return ;
		        }*/
		        mainForm.action = "<%=rootPath%>/hostApply.do?action=show&modifyFlag="+modifyFlag;
		        mainForm.submit();
		     }
		</script>
		<script type="text/javascript" src="<%=rootPath%>/topology/network/js/wincloseoverride.js"></script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
			<input type="hidden" name="type" id="type" value="<%=type%>">
			<input type="hidden" name="subtype" id="subtype" value="<%=subtype%>">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 服务器>> 应用配置 >> 应用列表 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>  
		        								<table id="content-body" class="content-body">  
													<tr>
	        											<td class="body-data-title" style="text-align:right" height="21" align="right" valign="bottom">
															<font class="title">
															  [<a href="#" onclick="show('true')">设置</a>]&nbsp;&nbsp;&nbsp;
										                                            紧急告警：<img src="<%=HostApplyManager.getCurrentStatusImage(3)%>" alt="严重警告">&nbsp;
										                                            严重告警：<img src="<%=HostApplyManager.getCurrentStatusImage(2)%>" alt="严重告警">&nbsp;
										                                            普通告警：<img src="<%=HostApplyManager.getCurrentStatusImage(1)%>" alt="普通告警">&nbsp;
										                                            正常状态：<img src="<%=HostApplyManager.getCurrentStatusImage(0)%>" alt="正常状态">
										                    </font>&nbsp;&nbsp;
										                    <input type="hidden" name="type" value="<%=request.getAttribute("type") %>"/>
										                    <input type="hidden" name="subtype" value="<%=request.getAttribute("subtype") %>"/>
										                    <input type="hidden" name="ipaddress" value="<%=request.getAttribute("ipaddress") %>"/>
										                    <input type="hidden" name="nodeid" value="<%=request.getAttribute("nodeid") %>"/>
														</td>
	        										</tr>
		        								</table>
		        							</td>
		        						</tr>
		        						<tr>
							        		<td class="content-body" style="padding-bottom: 0;padding-top: 0;">&nbsp;</td>
							        	</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
													<tr>
														<td width="10px;"></td>
														<td>
															<table id="table_asdf" class="data" >
																<tr>
																<td class="body-data-title" width=5%>
																	<INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()" class=noborder>全选</td>
									    							<td class="body-data-title">服务器IP地址</td>
									    							<td class="body-data-title">名称</td> 
                                                                    <td class="body-data-title">状态</td> 
													    			<td class="body-data-title">Oracle</td>
													    			<td class="body-data-title">SQLServer</td>
													    			<td class="body-data-title">DB2</td>
													    			<td class="body-data-title">Sybase</td>
													    			<td class="body-data-title">MySQL</td>
													    			<td class="body-data-title">Informix</td>
													    			<td class="body-data-title">FTP</td>
													    			<td class="body-data-title">Email</td>
													    			<td class="body-data-title">MQ</td>
													    			<td class="body-data-title">Domino</td>
													    			<td class="body-data-title">WAS</td>
													    			<td class="body-data-title">Weblogic</td>
													    			<td class="body-data-title">Tomcat</td>
													    			<td class="body-data-title">IIS</td>
													    			<td class="body-data-title">CICS</td>
													    			<td class="body-data-title">DNS</td>
													    			<td class="body-data-title">JBOSS</td>
													    			<td class="body-data-title">Apache</td>
													    			<td class="body-data-title">Tuxdeo</td>
							        							</tr>
							        							<%
							        							if(hostApplyHash != null && !hostApplyHash.isEmpty()){
                                                                    NodeAlarmService service = new NodeAlarmService();
							        								Iterator<String> iterator = hostApplyHash.keySet().iterator();
							        								while(iterator.hasNext()){
							        									String ip = iterator.next();
							        									//根据IP地址得到Host
							        									Host node = (Host)PollingEngine.getInstance().getNodeByIP(ip);
                                                                        String nodeImgPath = "";
                                                                        if (node.isManaged()) {
                                                                            int nodeStatus = service.getMaxAlarmLevel(node);
                                                                            nodeImgPath = HostApplyManager.getCurrentStatusImage(nodeStatus);
                                                                        }
							        									
							        									HostApply hostApply = hostApplyHash.get(ip);
							        									String oracleStatus = hostApply.getOracleStatus() == null ?  "-1" : hostApply.getOracleStatus();
							        									String oracleImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(oracleStatus));
							        									
							        									String sqlserverStatus = hostApply.getSqlserverStatus() == null ?  "-1" : hostApply.getSqlserverStatus();
							        									String sqlserverImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(sqlserverStatus));
							        									
							        									String db2Status = hostApply.getDb2Status() == null ?  "-1" : hostApply.getDb2Status();
							        									String db2ImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(db2Status));
							        									
							        									String sybaseStatus = hostApply.getSybaseStatus() == null ?  "-1" : hostApply.getSybaseStatus();
							        									String sybaseImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(sybaseStatus));
							        									
							        									String mysqlStatus = hostApply.getMysqlStatus() == null ? "-1" : hostApply.getMysqlStatus();
							        									String mysqlImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(mysqlStatus));
							        									
							        									String informixStatus = hostApply.getInformixStatus() == null ? "-1" : hostApply.getInformixStatus();
							        									String informixImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(informixStatus));
							        									
							        									String ftpStatus = hostApply.getFtpStatus() == null ? "-1" : hostApply.getFtpStatus();
							        									String ftpImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(ftpStatus));
							        									
							        									String emailStatus = hostApply.getEmailStatus() == null ? "-1" : hostApply.getEmailStatus();
							        									String emailImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(emailStatus));
							        									
							        									String mqStatus = hostApply.getMqStatus() == null ? "-1" : hostApply.getMqStatus();
							        									String mqImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(mqStatus));
							        									
							        									String dominoStatus = hostApply.getDominoStatus() == null ? "-1" : hostApply.getDominoStatus();
							        									String dominoImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(dominoStatus));
							        									
							        									String wasStatus = hostApply.getWasStatus() == null ? "-1" : hostApply.getWasStatus();
							        									String wasImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(wasStatus));
							        									
							        									String weblogicStatus = hostApply.getWeblogicStatus() == null ? "-1" : hostApply.getWeblogicStatus();
							        									String weblogicImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(weblogicStatus));
							        									
							        									String tomcatStatus = hostApply.getTomcatStatus() == null ? "-1" : hostApply.getTomcatStatus();
							        									String tomcatImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(tomcatStatus));
							        									
							        									String iisStatus = hostApply.getIisStatus() == null ? "-1" : hostApply.getIisStatus();
							        									String iisImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(iisStatus));
							        									
							        									String cicsStatus = hostApply.getCicsStatus() == null ? "-1" : hostApply.getCicsStatus();
							        									String cicsImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(cicsStatus));
							        									
							        									String dnsStatus = hostApply.getDnsStatus() == null ? "-1" : hostApply.getDnsStatus();
							        									String dnsImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(dnsStatus));
							        									
							        									String jbossStatus = hostApply.getJbossStatus() == null ? "-1" : hostApply.getJbossStatus();
							        									String jbossImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(jbossStatus));
							        									
							        									String apacheStatus = hostApply.getApacheStatus() == null ? "-1" : hostApply.getApacheStatus();
							        									String apacheImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(apacheStatus));
							        									 
							        									String tuxedoStatus = hostApply.getTuxedoStatus() == null ? "-1" : hostApply.getTuxedoStatus();
							        									String tuxedoImgPath = HostApplyManager.getCurrentStatusImage(Integer.parseInt(tuxedoStatus));
							        							%> 
							        										
				        										<tr <%=onmouseoverstyle%>>
				        											<td class="data_content center">&nbsp;</td>
					       											<td class="data_content center"><%=ip %></td>
					       											<td class="data_content center">
					       												<%
					       													if(node != null){//设备不为空(服务器已经被监控)
					       												%>
					       												<a href="#" onclick="toDetailPage('<%=node.getId() %>')"><%=node.getAlias() %></a>
					       												<%
					       													}else{//服务器未监控
					       												%>
					       												<%
					       													}
					       												%>
					       											</td>
                                                                    <td class="data_content center">
                                                                        <%
                                                                            if(node != null && node.isManaged()){//设备不为空(服务器已经被监控)
                                                                        %>
                                                                        <img src='<%=nodeImgPath %>'/>
                                                                        <%
                                                                            }else{//服务器未监控
                                                                        %>
                                                                                未监控
                                                                        <%
                                                                            }
                                                                        %>
                                                                    </td>
													    			<td class="data_content center">
													    				<%
													    					if(oracleImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":oracle" %>"  <%=hostApply.isOracleIsShow() ? "checked" : "" %>><img src='<%=oracleImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(sqlserverImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":sqlserver" %>"  <%=hostApply.isSqlserverIsShow() ? "checked" : "" %>><img src='<%=sqlserverImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td> 
													    			<td class="data_content center">
													    				<%
													    					if(db2ImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":db2" %>"  <%=hostApply.isDb2IsShow() ? "checked" : "" %>><img src='<%=db2ImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(sybaseImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":sybase" %>" <%=hostApply.isSybaseIsShow() ? "checked" : "" %>><img src='<%=sybaseImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(mysqlImgPath != null){
													    						System.out.println("hostApply.isMysqlIsShow()==="+hostApply.isMysqlIsShow());
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":mysql" %>" <%=hostApply.isMysqlIsShow() ? "checked" : "" %>><img src='<%=mysqlImgPath %>'/>
													    				<%
													    					}
													    				%>		
													    			</td>  
													    			<td class="data_content center">
													    				<%
													    					if(informixImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":informix" %>" <%=hostApply.isInformixIsShow() ? "checked" : "" %>><img src='<%=informixImgPath %>'/>
													    				<%
													    					}
													    				%>
													    			</td>  
													    			<td class="data_content center">
													    				<%
													    					if(ftpImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":ftp" %>"  <%=hostApply.isFtpIsShow() ? "checked" : "" %>><img src='<%=ftpImgPath %>'/>
													    				<%
													    					}
													    				%>
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(emailImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":email" %>" <%=hostApply.isEmailShow() ? "checked" : "" %>><img src='<%=emailImgPath %>'/>
													    				<%
													    					}
													    				%>
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(mqImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":mq" %>"  <%=hostApply.isMqIsShow() ? "checked" : "" %>><img src='<%=mqImgPath %>'/>
													    				<%
													    					}
													    				%>		
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(dominoImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":domino" %>"  <%=hostApply.isDominoIsShow() ? "checked" : "" %>><img src='<%=dominoImgPath %>'/>
													    				<%
													    					}
													    				%>		
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(wasImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":was" %>"  <%=hostApply.isWasIsShow() ? "checked" : "" %>><img src='<%=wasImgPath %>'/>
													    				<%
													    					}
													    				%>		
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(weblogicImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":weblogic" %>"  <%=hostApply.isWeblogicIsShow() ? "checked" : "" %>><img src='<%=weblogicImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(tomcatImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":tomcat" %>" <%=hostApply.isTomcatIsShow() ? "checked" : "" %>><img src='<%=tomcatImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(iisImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":iis" %>"  <%=hostApply.isIisIsShow() ? "checked" : "" %>><img src='<%=iisImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(cicsImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":cics" %>"  <%=hostApply.isCicsIsShow() ? "checked" : "" %>><img src='<%=cicsImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(dnsImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":dns" %>"  <%=hostApply.isDnsIsShow() ? "checked" : "" %>><img src='<%=dnsImgPath %>'/>
													    				<%
													    					}
													    				%>
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(jbossImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":jboss" %>"  <%=hostApply.isJbossIsShow() ? "checked" : "" %>><img src='<%=jbossImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">
													    				<%
													    					if(apacheImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":apache" %>"  <%=hostApply.isApacheIsShow() ? "checked" : "" %>><img src='<%=apacheImgPath %>'/>
													    				<%
													    					}
													    				%>	
													    			</td>
													    			<td class="data_content center">  
													    				<%
													    					if(tuxedoImgPath != null){
													    				%>
													    				<INPUT type="checkbox"  name=checkbox value="<%=ip+":tuxedo" %>"  <%=hostApply.isTuxedoIsShow() ? "checked" : "" %>><img src='<%=tuxedoImgPath %>'/>
													    				<%
													    					}
													    				%>		
													    			</td>
							        							</tr>
						        								<%	     
							        								}
							        							}
						        								%>
															</table>
														</td>
														<td width="10px;"></td>
													</tr>
		        								</table>
		        							</td>
		        						</tr>
        								<tr>
											<td class="content-body" style="padding-bottom: 0;padding-top: 0;text-align: center;">
												<input type="button" value="关 闭" style="width:50" onclick="window.close()">
											</TD>	
										</tr>
		        						<tr>
		        							<td>
		        								<table id="content-footer" class="content-footer">
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
		</form>
	</body>
</html>
