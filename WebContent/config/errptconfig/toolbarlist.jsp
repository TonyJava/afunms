<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.indicators.model.NodeGatherIndicators"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.config.model.Errptconfig"%>
<%@page import="com.afunms.alarm.model.*"%>
<%@page import="com.afunms.alarm.dao.*"%>

<%
	String rootPath = request.getContextPath();
  	Errptconfig errptconfig = (Errptconfig)request.getAttribute("errptconfig");
  	if(errptconfig == null)errptconfig = new Errptconfig();
		String tstr0="";
		String tstr1="";
		String tstr2="";
		String tstr3="";
		String tstr4="";
		String tstr5="";
  		String nodeid = (String)request.getAttribute("nodeid");
		String errpttpye = errptconfig.getErrpttype();
		String alarmwayids = errptconfig.getAlarmwayid();
		String alarmwayname="";
		String allalarmwayname="";
		
		if(alarmwayids != null && alarmwayids.trim().length()>0){
			String alarmwayidArray[]=alarmwayids.split(",");
			AlarmWayDao alarmWayDao = new AlarmWayDao();
			try{
				for(int i=0;i<alarmwayidArray.length;i++){
					String alarmwayid=alarmwayidArray[i];
					AlarmWay alarmWay0 = (AlarmWay)alarmWayDao.findByID(alarmwayid);
					if(alarmWay0!=null){
						alarmwayname = alarmWay0.getName();
						allalarmwayname=allalarmwayname+alarmwayname+",";
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				alarmWayDao.close();
			}				
		}
		
		
		if(errpttpye == null)errpttpye = "";
		String[] errpts = errpttpye.split(",");
		Hashtable errpthash = new Hashtable();
		try{
		if(errpts != null && errpts.length>0){
			for(int i=0;i<errpts.length;i++){
				if(errpts[i]!= null && errpts[i].trim().length()>0){
					errpthash.put(errpts[i], errpts[i]);
				}
			}
		}
		if(errpthash != null && errpthash.size()>0){
			if(errpthash.containsKey("pend"))tstr0="checked";
			if(errpthash.containsKey("perf"))tstr1="checked";
			if(errpthash.containsKey("perm"))tstr2="checked";
			if(errpthash.containsKey("temp"))tstr3="checked";
			if(errpthash.containsKey("info"))tstr4="checked";
			if(errpthash.containsKey("unkn"))tstr5="checked";
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String cstr0="";
		String cstr1="";
		String cstr2="";
		String cstr3="";
		String errptclass = errptconfig.getErrptclass();
		if(errptclass == null)errptclass = "";
		String[] errptclasses = errptclass.split(",");
		Hashtable errptclasshash = new Hashtable();
		try{
		if(errptclasses != null && errptclasses.length>0){
			for(int i=0;i<errptclasses.length;i++){
				if(errptclasses[i]!= null && errptclasses[i].trim().length()>0){
					errptclasshash.put(errptclasses[i], errptclasses[i]);
					//System.out.println(errptclasses[i]+"---------------");
				}
			}
		}
		if(errptclasshash != null && errptclasshash.size()>0){
			if(errptclasshash.containsKey("h"))cstr0="checked";
			if(errptclasshash.containsKey("s"))cstr1="checked";
			if(errptclasshash.containsKey("o"))cstr2="checked";
			if(errptclasshash.containsKey("u"))cstr3="checked";
		}
		}catch(Exception e){
			e.printStackTrace();
		}
%>


<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
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
			
  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/errpt.do?action=save";
      mainForm.submit();
  }
  
	//-- nielin modify at 2010-01-04 start ----------------
	function CreateWindow(url)
	{
		msgWindow=window.open(url,"protypeWindow","toolbar=no,width=1000,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
	}   
			
	function chooseAlarmWay(value,alarmWayIdEvent,alarmWayNameEvent){
	//alert(alarmWayIdEvent+"============"+alarmWayNameEvent);
		CreateWindow("/afunms/alarmWay.do?action=chooselist&jp=1&alarmWayNameEvent=" + alarmWayNameEvent + "&alarmWayIdEvent=" + alarmWayIdEvent);
	}
			
			  
			
		</script>
		<script type="text/javascript">
  			var delAction = "<%=rootPath%>/nodeGatherIndicators.do?action=delete";
  			var listAction = "<%=rootPath%>/nodeGatherIndicators.do?action=list";
		</script>
	</head>
	<body id="body" class="body"">
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
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
									                	<td class="content-title"> <b>告警 >> ERRPT管理 >> ERRPT过滤规则</b> </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
													<tr>
														<td>
															<table width="100%" cellPadding=0 cellspacing="1" algin="center">
									                    					<tr algin="left" valign="center">                      														
									                      						<td height="28" bgcolor="#ECECEC" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;<b>错误级别<%=alarmwayname%></b></td>
									                    					</tr>
									                    					<tr align="left" valign="center" bgcolor=#ffffff> 
									                    						<td height="28" align="right" width=5%>&nbsp;&nbsp;<INPUT type="checkbox" class=noborder value="pend" <%=tstr0%> name="checkbox"></td>
									                    						<td height="28" align="left" width=25%>&nbsp;&nbsp;设备或功能组件可能丢失</td>
									                    						<td height="28" align="right" width=5%%><INPUT type="checkbox" class=noborder value="perf" <%=tstr1%> name="checkbox"></td>
									                    						<td height="28" align="left" width=25%>&nbsp;&nbsp;性能严重下降</td> 
									                    						<td height="28" align="right" width=5%><INPUT type="checkbox" class=noborder value="perm" <%=tstr2%> name="checkbox"></td>
									                    						<td height="28" align="left" width=25%>&nbsp;&nbsp;硬件设备或软件模块损坏</td> 
									                    						
									                    					</tr> 
									                    					<tr align="left" valign="center" bgcolor=#ffffff> 
									                    						<td height="28" align="right" width=5%>&nbsp;&nbsp;<INPUT type="checkbox" class=noborder value="info" <%=tstr4%> name="checkbox"></td>
									                    						<td height="28" align="left" width=25%>&nbsp;&nbsp;一般消息，不是错误</td>
									                    						<td height="28" align="right" width=5%><INPUT type="checkbox" class=noborder value="unkn" <%=tstr5%> name="checkbox"></td>
									                    						<td height="28" align="left" width=25%>&nbsp;&nbsp;不能确定错误的严重性</td>
									                    						<td height="28" align="right" width=5%><INPUT type="checkbox" class=noborder value="temp" <%=tstr3%> name="checkbox"></td>
									                    						<td height="28" align="left" width=25%>&nbsp;&nbsp;临时性错误，经过重试后已经恢复正常</td>
									                    					</tr>                     					                    					                    					                     		                   										                 										                      								
									            					</table>
									            					<table width="100%" cellPadding=0 cellspacing="1" algin="center">
									                    					<tr algin="left" valign="center">                      														
									                      						<td height="28" bgcolor="#ECECEC" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;<b>错误种类</b></td>
									                    					</tr>
									                    					<tr align="left" valign="center" bgcolor=#ffffff> 
									                    						<td height="28" align="right" width=5%>&nbsp;&nbsp;&nbsp;&nbsp;<INPUT type="checkbox" class=noborder value="h" <%=cstr0%> name="ccheckbox"></td>
									                    						<td height="28" align="left" width=20%>&nbsp;&nbsp;硬件或介质故障</td>
									                    						<td height="28" align="right" width=5%%><INPUT type="checkbox" class=noborder value="s" <%=cstr1%> name="ccheckbox"></td>
									                    						<td height="28" align="left" width=20%>&nbsp;&nbsp;软件故障</td> 
									                    						<td height="28" align="right" width=5%><INPUT type="checkbox" class=noborder value="o" <%=cstr2%> name="ccheckbox"></td>
									                    						<td height="28" align="left" width=20%>&nbsp;&nbsp;人为错误</td> 
									                    						<td height="28" align="right" width=5%><INPUT type="checkbox" class=noborder value="u" <%=cstr3%> name="ccheckbox"></td>
									                    						<td height="28" align="left" width=20%>&nbsp;&nbsp;不能确定</td>
									                    						
									                    					</tr>                     					                    					                    					                     		                   										                 										                      								
									            					</table>
	            													<table width="100%" cellPadding=0 cellspacing="1" algin="center">
									                    					<tr algin="left" valign="center">                      														
									                      						<td height="28" bgcolor="#ECECEC" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;<b>告警方式设置</b></td>
									                    					</tr>
									                    					<tr align="left" valign="center" bgcolor=#ffffff> 
									                    						<td height="28" align="left" width=5%>&nbsp;&nbsp;&nbsp;&nbsp;
									                    									<input type="text" disabled="disabled" value="<%=allalarmwayname%>" name="way0-name" id="way0-name">
																				<input type="hidden" value="<%=alarmwayids%>" name="way0-id" id="way0-id">
																				<a href="#" onclick="chooseAlarmWay('','way0-id','way0-name')">浏览</a>

									                    						</td>
									                    						
									                    					</tr>                     					                    					                    					                     		                   										                 										                      								
									            					</table>									            					
														</td>
													</tr>
													<tr>
																			<TD nowrap colspan="20" align=center><br>
																			<input type="button" value="确 定" style="width:50" class="formStylebutton" onclick="toAdd()">&nbsp;&nbsp;
																			<input type="button" value="关 闭" style="width:50" onclick="window.close()">
																			</TD>	
																		</tr>
		        								</table>
		        							</td>
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
