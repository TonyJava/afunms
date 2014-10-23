<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@ include file="/include/globe.inc"%>

<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.polling.base.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="java.util.*"%>

<%
  List tomcatlist = (List)request.getAttribute("tomcatlist");
  int tomcatrc = tomcatlist.size();
 List mqlist = (List)request.getAttribute("mqlist");
  int mqtrc = mqlist.size();
 List dominolist = (List)request.getAttribute("dominolist");
List dnslist = (List)request.getAttribute("dnslist");
String state = (String)request.getAttribute("state");
List cicslist = (List)request.getAttribute("cicslist");
List iislist = (List)request.getAttribute("iislist");
List weblogiclist = (List)request.getAttribute("weblogiclist");
if(weblogiclist == null)weblogiclist = new ArrayList();
List waslist = (List)request.getAttribute("waslist");
 if(waslist == null)waslist = new ArrayList();


  String rootPath = request.getContextPath();  
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript">

	//公共变量
	var node="";
	var ipaddress="";
	var operate="";
	/**
	*根据传入的id显示右键菜单
	*/
	function showMenu(ser,id,nodeid,ip)
	{	
		if(ser == "mq"){
			document.getElementById('itemMenu').innerHTML="<table border='1' width='100%' height='100%' bgcolor='#F1F1F1'"+
		"style='border: thin;font-size: 12px' cellspacing='0'>"+
		"<tr>"+
			"<td style='cursor: default; border: outset 1;' align='center'"+
				"onclick='parent.mqedit()'>修改信息</td>"+
		"</tr>"+
		"<tr>"+
			"<td style='cursor: default; border: outset 1;' align='center'"+
				"onclick='parent.mqdetail();'>监视信息</td>"+
		"</tr>"+
		"<tr>"+
			"<td style='cursor: default; border: outset 1;' align='center'"+
				"onclick='parent.mqcancelmanage()'>取消监视</td>"+
		"</tr>"+
		"<tr>"+
			"<td style='cursor: default; border: outset 1;' align='center'"+
				"onclick='parent.mqaddmanage()'>添加监视</td>"+
		"</tr>"+	
	"</table>";
		}else if(ser == "domino"){
		document.getElementById('itemMenu').innerHTML="<table border='1' width='100%' height='100%' bgcolor='#F1F1F1'"+
			"style='border: thin;font-size: 12px' cellspacing='0'>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.dominoedit()'>修改信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.dominodetail();'>监视信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.dominocancelmanage()'>取消监视</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.dominoaddmanage()'>添加监视</td>"+
			"</tr>"+
		"</table>";
		}else if(ser == "was"){
			document.getElementById('itemMenu').innerHTML="<table border='1' width='100%' height='100%' bgcolor='#F1F1F1'"+
			"style='border: thin;font-size: 12px' cellspacing='0'>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.wasedit()'>修改信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.wasdetail();'>监视信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.wascancelalert()'>取消监视</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.wasaddalert()'>添加监视</td>"+
			"</tr>"+
		"</table>";
		}else if(ser == "weblogic"){
		document.getElementById('itemMenu').innerHTML="<table border='1' width='100%' height='100%' bgcolor='#F1F1F1'"+
			"style='border: thin;font-size: 12px' cellspacing='0'>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.weblogicedit()'>修改信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.weblogicdetail();'>监视信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.weblogiccancelalert()'>取消监视</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.weblogicaddalert()'>添加监视</td>"+
			"</tr>"+
		"</table>";
		}else if(ser == "dns"){
		document.getElementById('itemMenu').innerHTML="<table border='1' width='100%' height='100%' bgcolor='#F1F1F1'"+
			"style='border: thin;font-size: 12px' cellspacing='0'>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.dnsedit()'>修改信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.dnsdetail();'>监视信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.dnscancelmanage()'>取消监视</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.dnsaddmanage()'>添加监视</td>"+
			"</tr>"+
		"</table>";
		}else if(ser == "iis"){
		document.getElementById('itemMenu').innerHTML="<table border='1' width='100%' height='100%' bgcolor='#F1F1F1'"+
			"style='border: thin;font-size: 12px' cellspacing='0'>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.iisedit()'>修改信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.iisdetail();'>监视信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.iiscancelmanage()'>取消监视</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.iisaddmanage()'>添加监视</td>"+
			"</tr>"+
		"</table>";
		}else if(ser == "cics"){
		document.getElementById('itemMenu').innerHTML="<table border='1' width='100%' height='100%' bgcolor='#F1F1F1'"+
			"style='border: thin;font-size: 12px' cellspacing='0'>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.cicsedit()'>修改信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.cicsdetail();'>监视信息</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.cicscancelmanage()'>取消监视</td>"+
			"</tr>"+
			"<tr>"+
				"<td style='cursor: default; border: outset 1;' align='center'"+
					"onclick='parent.cicsaddmanage()'>添加监视</td>"+
			"</tr>"+
		"</table>";
		}
		
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
	function cicsdetail()
	{
	    location.href="<%=rootPath%>/cics.do?action=detail&id="+node;
	}
	function cicsedit()
	{
		location.href="<%=rootPath%>/cics.do?action=ready_edit&id="+node;
	}
	function cicscancelmanage()
	{
		location.href="<%=rootPath%>/cics.do?action=cancelalert&id="+node;
	}
	function cicsaddmanage()
	{
		location.href="<%=rootPath%>/cics.do?action=addalert&id="+node;
	}	


	function dnsdetail()
	{
	    location.href="<%=rootPath%>/dns.do?action=detail&id="+node;
	}
	function dnsedit()
	{
		location.href="<%=rootPath%>/dns.do?action=ready_edit&id="+node;
	}
	function dnscancelmanage()
	{
		location.href="<%=rootPath%>/dns.do?action=cancelalert&id="+node;
	}
	function dnsaddmanage()
	{
		location.href="<%=rootPath%>/dns.do?action=addalert&id="+node;
	}	


    function dominodetail()
	{
	    location.href="<%=rootPath%>/domino.do?action=detail&id="+node;
	}
	function dominoedit()
	{
		location.href="<%=rootPath%>/domino.do?action=ready_edit&id="+node;
	}
	function dominocancelmanage()
	{
		location.href="<%=rootPath%>/domino.do?action=cancelalert&id="+node;
	}
	function dominoaddmanage()
	{
		location.href="<%=rootPath%>/domino.do?action=addalert&id="+node;
	}	

    function iisdetail()
	{
	    location.href="<%=rootPath%>/iis.do?action=detail&id="+node;
	}
	function iisedit()
	{
		location.href="<%=rootPath%>/iis.do?action=ready_edit&id="+node;
	}
	function iiscancelmanage()
	{
		location.href="<%=rootPath%>/iis.do?action=cancelalert&id="+node;
	}
	function iisaddmanage()
	{
		location.href="<%=rootPath%>/iis.do?action=addalert&id="+node;
	}	

    function mqdetail()
	{
	    location.href="<%=rootPath%>/mq.do?action=detail&id="+node;
	}
	function mqedit()
	{
		location.href="<%=rootPath%>/mq.do?action=ready_edit&id="+node;
	}
	function mqcancelmanage()
	{
		location.href="<%=rootPath%>/mq.do?action=cancelalert&id="+node;
	}
	function mqaddmanage()
	{
		location.href="<%=rootPath%>/mq.do?action=addalert&id="+node;
	}	

    function wasdetail()
	{
	    location.href="<%=rootPath%>/was.do?action=detail&id="+node;
	}
	function wasedit()
	{
		location.href="<%=rootPath%>/was.do?action=ready_edit&id="+node;
	}
	function wascancelalert()
	{
		location.href="<%=rootPath%>/was.do?action=cancelalert&id="+node;
	}
	function wasaddalert()
	{
		location.href="<%=rootPath%>/was.do?action=addalert&id="+node;
	}	

    function weblogicdetail()
	{
	    location.href="<%=rootPath%>/weblogic.do?action=detail&id="+node;
	}
	function weblogicedit()
	{
		location.href="<%=rootPath%>/weblogic.do?action=ready_edit&id="+node;
	}
	function weblogiccancelalert()
	{
		location.href="<%=rootPath%>/weblogic.do?action=cancelalert&id="+node;
	}
	function weblogicaddalert()
	{
		location.href="<%=rootPath%>/weblogic.do?action=addalert&id="+node;
	}	


	function cicsclickMenu()
	{
	}
</script>
<script language="JavaScript" type="text/JavaScript">
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
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa"  onload="initmenu();">
<div id="itemMenu" style="display: none";>
	</div>
<form name="mainForm" method="post">
<table id="body-container" class="body-container">
	<tr>
		<td width="200" valign=top align=center>
			<%=menuTable%>
		
		</td>
		<td align="center" valign=top bgcolor="#ababab">
			<table style="width:98%"  cellpadding="0" cellspacing="0" algin="center">
				<tr>
					<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%">
						<table width="100%" cellspacing="0" cellpadding="0">
		                  <tr>
		                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
		                    <td class="layout_title"></td>
		                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
		                  </tr>
		              </table>
				  	</td>
				 </tr>				
	
				<tr>
					<td>
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr>
								<td height="28" align="left" bordercolor="#ffffff" bgcolor="#ffffff" class="txtGlobalBold" colspan="7">&nbsp;&nbsp;<font color="#397DBD">Tomcat监视列表</font></td>
							</tr>
							
										<tr class="microsoftLook0" height=28 align="center">
					    					<th width='10%' align="center" class="report-data-body-title"><font color="#397DBD">序号</font></th>
					    					<th width='15%' align="center" class="report-data-body-title"><font color="#397DBD">名称</font></th>
					    					<th width='20%' align="center"  class="report-data-body-title"><font color="#397DBD">IP地址</font></th>
					    					<th width='10%' align="center" class="report-data-body-title"><font color="#397DBD">端口</font></th>
					    					<th width='15%' align="center" class="report-data-body-title"><font color="#397DBD">当前状态</font></th>
					    					<th width='20%' align="center" class="report-data-body-title"><font color="#397DBD">详细</font></th>
					    					<th width='20%' align="center" class="report-data-body-title"><font color="#397DBD">编辑</font></th>
										</tr>
										<%
										    for(int i=0;i<tomcatrc;i++)
										    {
										       Tomcat tomcatvo = (Tomcat)tomcatlist.get(i);
										       Node node = PollingEngine.getInstance().getTomcatByID(tomcatvo.getId());
										       String alarmmessage = "";
										       if(node != null){
										       		tomcatvo.setStatus(1);
										       		//System.out.println(vo.getIpAddress()+"====================="+node.isAlarm());
										       		if(node.isAlarm())tomcatvo.setStatus(3);
										       		List alarmlist = node.getAlarmMessage();
										       		if(alarmlist!= null && alarmlist.size()>0){
													for(int k=0;k<alarmlist.size();k++){
														alarmmessage = alarmmessage+alarmlist.get(k).toString();
													}
												}
										       }       
										%>
								       	<tr bgcolor="FFFFFF" class="microsoftLook" height="25">
									    	<td  class="report-data-body-list"><font color='blue'><%=1 + i%></font></td>
											<td  class="report-data-body-list"><%=tomcatvo.getAlias()%></td> 
											<td  class="report-data-body-list"><%=tomcatvo.getIpAddress()%></td> 
											<td  class="report-data-body-list"><%=tomcatvo.getPort()%></td>		
											<td  class="report-data-body-list" align="center" ><img src="<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(tomcatvo.getStatus())%>" border="0"/></td>		
											<td  class="report-data-body-list" align='center' ><a href="#" onclick='detail(<%=tomcatvo.getId()%>)'>
											<img src="<%=rootPath%>/resource/image/detail.jpg" border="0"/></a></td>		
											<td  class="report-data-body-list" ><a href="<%=rootPath%>/tomcat.do?action=ready_edit&id=<%=tomcatvo.getId()%>">
											<img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>
								  		</tr>
										<%}%>  	  				
									
						</table>
					</td>
				</tr>
			<!--MQ  -->		
			    
			    <tr>
					<td>
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr>
								<td height="28" align="left" bordercolor="#ffffff" bgcolor="#ffffff" class="txtGlobalBold" colspan="8">&nbsp;&nbsp;<font color="#397DBD">MQ监视列表</font></td>
							</tr>
						
									<tr class="microsoftLook0" height=28>
				    					<th width='5%' class="report-data-body-title"><font color="#397DBD">序号</font></th>
				      					<th width='10%' class="report-data-body-title"><font color="#397DBD">MQ名称</font></th>
				      					<th width='20%' class="report-data-body-title"><font color="#397DBD">IP地址</font></th>
				      					<th width='20%' class="report-data-body-title"><font color="#397DBD">队列管理器名称</font></th>
				      					<th width='10%' class="report-data-body-title"><font color="#397DBD">端口</font></th> 
				      					<th width='10%' class="report-data-body-title"><font color="#397DBD">当前状态</font></th>     
				      					<th width='10%' class="report-data-body-title"><font color="#397DBD">是否监控</font></th>
				    					<th width='10%' class="report-data-body-title"><font color="#397DBD">操作</font></th>
				          </tr>
				<%
				if(mqtrc > 0){
				    for(int i=0;i<mqtrc;i++)
				    {
				       MQConfig mqvo = (MQConfig)mqlist.get(i);
				       Node node = PollingEngine.getInstance().getMqByID(mqvo.getId());
				       String alarmmessage = "";
				       if(node != null){
				       		mqvo.setStatus(1);
				       		//System.out.println(mqvo.getIpaddress()+"====================="+node.isAlarm());
				       		if(node.isAlarm())mqvo.setStatus(3);
				       		List alarmlist = node.getAlarmMessage();
				       		if(alarmlist!= null && alarmlist.size()>0){
							for(int k=0;k<alarmlist.size();k++){
								alarmmessage = alarmmessage+alarmlist.get(k).toString();
							}
						}
				       }        
				       
				%>
				       <tr bgcolor="FFFFFF" class="microsoftLook">
				    		<td height=25 class="report-data-body-list">&nbsp;<font color='blue'><%=1 + i%></font></td>
						<td height=25 class="report-data-body-list">&nbsp;<%=mqvo.getName()%></td> 
						<td height=25 class="report-data-body-list">&nbsp;<%=mqvo.getIpaddress()%></td> 
						<td height=25 class="report-data-body-list">&nbsp;<%=mqvo.getManagername()%></td> 
						<td height=25 class="report-data-body-list">&nbsp;<%=mqvo.getPortnum()%></td> 
						<td align='center' class="report-data-body-list"><img src="<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(mqvo.getStatus())%>" border="0" alt=<%=alarmmessage%>></td>		
						<td height=25 class="report-data-body-list">
							<%
								if(mqvo.getMon_flag() == 0){
							%>
							&nbsp;未监视
							<%
								}else{
							%>	
							&nbsp;已监视
							<%
								}
							%>	
						</td>	
						<td height=25 class="report-data-body-list">&nbsp;
						<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('mq','2','<%=mqvo.getId()%>','<%=mqvo.getIpaddress()%>')>
						</td>
				  	</tr>
				<%	}
				}
				
				%>				
							
						</table>
					</td>
				</tr>
        	  <!-- Domino监视列表 -->	
        			
        			   
			    <tr>
					<td>
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr>
								<td height="28" align="left" bordercolor="#ffffff" bgcolor="#ffffff" class="txtGlobalBold" colspan="6">&nbsp;&nbsp;<font color="#397DBD">Domino监视列表</font></td>
							</tr>
							
								<tr height=28 class="microsoftLook0">
			    					<th width='5%' class="report-data-body-title"><font color="#397DBD">序号</font></th>
			      					<th width='20%' class="report-data-body-title"><font color="#397DBD">名称</font></th>
			      					<th width='20%' class="report-data-body-title"><font color="#397DBD">IP地址</font></th>
			      					<th width='20%' class="report-data-body-title"><font color="#397DBD">团体名称</font></th>     
			      					<th width='10%' class="report-data-body-title"><font color="#397DBD">是否监控</font></th>
			    					<th width='10%' class="report-data-body-title"><font color="#397DBD">操作</font></th>
			</tr>
			<%
			if(dominolist.size() > 0){
			    for(int i=0;i<dominolist.size();i++)
			    {
			       DominoConfig dominovo = (DominoConfig)dominolist.get(i);
			       
			%>
			       <tr bgcolor="FFFFFF"  class="microsoftLook">
			    		
			    		<td height=25 class="report-data-body-list">&nbsp;<font color='blue'><%=1 + i%></font></td>
					<td height=25 class="report-data-body-list">&nbsp;<%=dominovo.getName()%></td> 
					<td height=25 class="report-data-body-list">&nbsp;<%=dominovo.getIpaddress()%></td> 
					<td height=25 class="report-data-body-list">&nbsp;<%=dominovo.getCommunity()%></td> 	
					<td height=25 class="report-data-body-list">
						<%
							if(dominovo.getMon_flag() == 0){
						%>
						&nbsp;未监视
						<%
							}else{
						%>	
						&nbsp;已监视
						<%
							}
						%>	
					</td>	
					<td height=25 class="report-data-body-list">&nbsp;
					<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('domino','2','<%=dominovo.getId()%>','<%=dominovo.getIpaddress()%>')>
					</td>
			  	</tr>
			<%	}
			}
			
			%>				
						
						</table>
					</td>
				</tr>
        
        <!-- Webphere监视列表 -->	
        			
        	  <tr>
					<td>
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr>
								<td height="28" align="left" bordercolor="#ffffff" bgcolor="#ffffff" class="txtGlobalBold" colspan="5">&nbsp;&nbsp;<font color="#397DBD">Webphere监视列表</font></td>
							</tr>
							
							<tr height=28 class="microsoftLook0">
		    					<th width='5%' class="report-data-body-title"><font color="#397DBD">序号</font></th>
		      					<th width='30%' class="report-data-body-title"><font color="#397DBD">名称</font></th>
		      					<th width='30%' class="report-data-body-title"><font color="#397DBD">IP地址</font></th>    
		      					<th width='15%' class="report-data-body-title"><font color="#397DBD">是否监控</font></th>
		    					<th width='15%' class="report-data-body-title"><font color="#397DBD">操作</font></th>
		</tr>
		<%
		if(waslist.size() > 0){
		    for(int i=0;i<waslist.size();i++)
		    {
		       WasConfig wasconfigvo = (WasConfig)waslist.get(i);
		       
		%>
		       <tr bgcolor="FFFFFF" class="microsoftLook">
		    		
		    	<td height=25 class="report-data-body-list">&nbsp;<font color='blue'><%=1 + i%></font></td>
				<td height=25 class="report-data-body-list">&nbsp;<%=wasconfigvo.getName()%></td> 
				<td height=25 class="report-data-body-list">&nbsp;<%=wasconfigvo.getIpaddress()%></td> 	
				<td height=25 class="report-data-body-list">
					<%
						if(wasconfigvo.getMon_flag() == 0){
					%>
					&nbsp;未监视
					<%
						}else{
					%>	
					&nbsp;已监视
					<%
						}
					%>	
				</td>	
				<td height=25 class="report-data-body-list">&nbsp;
				<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('was','2','<%=wasconfigvo.getId()%>','<%=wasconfigvo.getIpaddress()%>')>
				</td>
		  	</tr>
		<%	}
		}
		
		%>				
					
						</table>
					</td>
				</tr>
				
        		<!-- Weblogic监视列表 -->
        		
        		<tr>
					<td>
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr>
								<td height="28" align="left" bordercolor="#ffffff" bgcolor="#ffffff" class="txtGlobalBold" colspan="7">&nbsp;&nbsp;<font color="#397DBD">Weblogic监视列表</font></td>
							</tr>
							
							<tr height=28 class="microsoftLook0">
		    					<th width='5%'  class="report-data-body-title"><font color="#397DBD">序号</font></th>
		      					<th width='20%' class="report-data-body-title"><font color="#397DBD">名称</font></th>
		      					<th width='20%' class="report-data-body-title"><font color="#397DBD">IP地址</font></th>
		      					<th width='20%' class="report-data-body-title"><font color="#397DBD">团体名称</font></th> 
		      					<th width='10%' class="report-data-body-title"><font color="#397DBD">当前状态</font></th>    
		      					<th width='10%' class="report-data-body-title"><font color="#397DBD">是否监控</font></th>
		    					<th width='10%' class="report-data-body-title"><font color="#397DBD">操作</font></th>
							</tr>
					<%
					if(weblogiclist.size() > 0){
					    for(int i=0;i<weblogiclist.size();i++)
					    {
					       WeblogicConfig weblogicvo = (WeblogicConfig)weblogiclist.get(i);
					       Node node = PollingEngine.getInstance().getWeblogicByID(weblogicvo.getId());
					       String alarmmessage = "";
					       if(node != null){
					       		weblogicvo.setStatus(1);
					       		if(node.isAlarm())weblogicvo.setStatus(3);
					       		List alarmlist = node.getAlarmMessage();
					       		if(alarmlist!= null && alarmlist.size()>0){
								for(int k=0;k<alarmlist.size();k++){
									alarmmessage = alarmmessage+alarmlist.get(k).toString();
								}
							}
					       }
					       
					%>
		       <tr bgcolor="FFFFFF" class="microsoftLook">
		    		
		    		<td height=25 class="report-data-body-list">&nbsp;<font color='blue'><%=1 + i%></font></td>
				<td height=25 class="report-data-body-list">&nbsp;<%=weblogicvo.getAlias()%></td> 
				<td height=25 class="report-data-body-list">&nbsp;<%=weblogicvo.getIpAddress()%></td> 
				<td height=25 class="report-data-body-list">&nbsp;<%=weblogicvo.getCommunity()%></td> 
				<td align='center' class="report-data-body-list"><img src="<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(weblogicvo.getStatus())%>" border="0"/></td>	
				<td height=25 class="report-data-body-list">
					<%
						if(weblogicvo.getMon_flag() == 0){
					%>
					&nbsp;未监视
					<%
						}else{
					%>	
					&nbsp;已监视
					<%
						}
					%>	
				</td>	
				<td height=25 class="report-data-body-list">&nbsp;
				<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('weblogic','2','<%=weblogicvo.getId()%>','<%=weblogicvo.getIpAddress()%>')>
				</td>
		  	</tr>
		<%	}
		}
		
		%>				
					
						</table>
					</td>
				</tr>	
				
				
	       <!-- IIS监视列表 --> 		
        		
        		<tr>
					<td>
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr>
								<td height="28" align="left" bordercolor="#ffffff" bgcolor="#ffffff" class="txtGlobalBold" colspan="6">&nbsp;&nbsp;<font color="#397DBD">IIS监视列表</font></td>
							</tr>
							
							<tr height=28 class="microsoftLook0">
		    					<th width='5%' class="report-data-body-title"><font color="#397DBD">序号</font></th>
		      					<th width='20%' class="report-data-body-title"><font color="#397DBD">名称</font></th>
		      					<th width='20%' class="report-data-body-title"><font color="#397DBD">IP地址</font></th>
		      					<th width='20%' class="report-data-body-title"><font color="#397DBD">团体名称</font></th>     
		      					<th width='10%' class="report-data-body-title"><font color="#397DBD">是否监控</font></th>
		    					<th width='10%' class="report-data-body-title"><font color="#397DBD">操作</font></th>
		</tr>
		<%
		if(iislist.size() > 0){
		    for(int i=0;i<iislist.size();i++)
		    {
		       IISConfig iisconfigvo = (IISConfig)iislist.get(i);
		       
		%>
		       <tr  bgcolor="FFFFFF" class="microsoftLook">
		    		<td height=25 class="report-data-body-list">&nbsp;<font color='blue'><%=1 + i%></font></td>
				<td height=25 class="report-data-body-list">&nbsp;<%=iisconfigvo.getName()%></td> 
				<td height=25 class="report-data-body-list">&nbsp;<%=iisconfigvo.getIpaddress()%></td> 
				<td height=25 class="report-data-body-list">&nbsp;<%=iisconfigvo.getCommunity()%></td> 	
				<td height=25 class="report-data-body-list">
					<%
						if(iisconfigvo.getMon_flag() == 0){
					%>
					&nbsp;未监视
					<%
						}else{
					%>	
					&nbsp;已监视
					<%
						}
					%>	
				</td>	
				<td height=25 class="report-data-body-list">&nbsp;
				<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('iis','2','<%=iisconfigvo.getId()%>','<%=iisconfigvo.getIpaddress()%>')>
				</td>
		  	</tr>
		<%	}
		}
		
		%>				
					
						</table>
					</td>
				</tr>		
        			
      <!-- CICS服务列表 -->  			
        			
        		<tr>
					<td>
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr>
								<td height="28" align="left" bordercolor="#ffffff" bgcolor="#ffffff" class="txtGlobalBold" colspan="9">&nbsp;&nbsp;<font color="#397DBD">CICS服务列表</font></td>
							</tr>
							
	  						<tr height=28 class="microsoftLook0">
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">序号</font></th>				
      								<th width='15%' class="report-data-body-title"><font color="#397DBD">服务名称</font></th>
      								<th width='15%' class="report-data-body-title"><font color="#397DBD">主机名或IP地址</font></th>
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">网络协议</font></th>      
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">监听端口</font></th>
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">是否监控</font></th>
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">当前状态</font></th>
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">Gateway</font></th>
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">操作</font></th>
							</tr>
			<%
    			CicsConfig cicsconfigvo = null;
   				 for(int i=0;i<cicslist.size();i++)
    			{
       				cicsconfigvo = (CicsConfig)cicslist.get(i);
       				Node node = PollingEngine.getInstance().getCicsByID(cicsconfigvo.getId());
       				String alarmmessage = "";
				       if(node != null){
				       		cicsconfigvo.setStatus(1);
				       		//System.out.println(vo.getIpaddress()+"====================="+node.isAlarm());
				       		if(node.isAlarm())cicsconfigvo.setStatus(3);
				       		List alarmlist = node.getAlarmMessage();
				       		if(alarmlist!= null && alarmlist.size()>0){
							for(int k=0;k<alarmlist.size();k++){
								alarmmessage = alarmmessage+alarmlist.get(k).toString();
							}
						}
				       } else {
				           //System.out.println("=====================");
				       }
			%>
   										<tr bgcolor="FFFFFF" class="microsoftLook">  		
    											<td><font color='blue' class="report-data-body-list"><font color="#397DBD"><%=1 + i%></font></td>
    											<td  align='center'  class="report-data-body-list"><%=cicsconfigvo.getRegion_name()%></td>
    											<td  align='center' class="report-data-body-list"><font color="#397DBD"><%= cicsconfigvo.getIpaddress()%></font></td>
    											<td  align='center' class="report-data-body-list"><%=cicsconfigvo.getNetwork_protocol()%></td>
    											<td  align='center' class="report-data-body-list"><%=cicsconfigvo.getPort_listener()%></td>
    											<%
												if(cicsconfigvo.getFlag()==0){
											%>
											<td  align='center' class="report-data-body-list">否</td>
											<% 
												//if(vo.getFlag()==1);
												}else{
											%>
											 <td  align='center' class="report-data-body-list">是</td>
											 <%
											 	}
											 %>
											 <td  align='center'  class="report-data-body-list"><img src="<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(cicsconfigvo.getStatus())%>" border="0" alt="<%=alarmmessage%>"/></td>
											 <td  align='center'  class="report-data-body-list"><%=cicsconfigvo.getGateway()%></td>
											<td height=25 class="report-data-body-list">&nbsp;
												<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('cics','2','<%=cicsconfigvo.getId()%>','')>
											</td>											 
  										</tr>			
								<%;} %>
									
						</table>
					</td>
				</tr>	
				
        		<!--DNS服务列表  -->	
        			 <tr>
					<td>
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr>
								<td height="28" align="left" bordercolor="#ffffff" bgcolor="#ffffff" class="txtGlobalBold" colspan="7">&nbsp;&nbsp;<font color="#397DBD">DNS服务列表</font></td>
							</tr>
							
	  						<tr height=28 class="microsoftLook0">
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">序号</font></th>				
      								<th width='25%' class="report-data-body-title"><font color="#397DBD">DNS主机</font></th>
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">DNS接口</font></th>
      								<th width='25%' class="report-data-body-title"><font color="#397DBD">域名</font></th>
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">状态</font></th>      
      								<th width='10%' class="report-data-body-title"><font color="#397DBD">是否监控</font></th>
      								<th width='10%' class="report-data-body-title""><font color="#397DBD">操作</font></th>
							</tr>
						<%
			    			DnsConfig dnsvo = null;
			   				 for(int i=0;i<dnslist.size();i++)
			    			{
			       				dnsvo = (DnsConfig)dnslist.get(i);
						%>
   										<tr bgcolor="#ffffff"  height=25>  
    											
    											<td><font color='blue' class="report-data-body-list"><%=1 + i%></font></td>
    											<td  align='center'  class="report-data-body-list"><%=dnsvo.getHostip()%></td>
    											<td  align='center'  class="report-data-body-list"><%= dnsvo.getHostinter()%></td>
    											<td  align='center' class="report-data-body-list"><%=dnsvo.getDns()%></td>
    											<td  align='center' class="report-data-body-list">
    											<%if(state==null)
    											{ %>
    											<img src="/afunms/resource/image/topo/status_ok.gif" alt="" border="0"/>
    											<%} else {%>
    											<img src="/afunms/resource/image/topo/alert.gif" alt="" border="0"/>
    											<%} %>
                                               </td>
											<%
												if(dnsvo.getFlag()==0){
											%>
											<td  align='center' class="report-data-body-list">否</td>
											<% 
												//if(vo.getFlag()==1);
												}else{
											%>
											 <td  align='center' class="report-data-body-list">是</td>
											 <%
											 	}
											 %>
											<td height=25 class="report-data-body-list">&nbsp;
												<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('dns','2','<%=dnsvo.getId()%>','')>
											</td>											 
  										</tr>			
								<%;} %>
									
						</table>
					</td>
				</tr>	
        			 
        			 
        			 
        			 
        			 
        			
        			
        			
        			
        			
        			
        			
        							
				<tr>
		              <td background="<%=rootPath%>/common/images/right_b_02.jpg" >
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
			    </form>

</BODY>
</HTML>