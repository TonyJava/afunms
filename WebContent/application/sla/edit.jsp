<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String rootPath = request.getContextPath();
  BusinessDao bussdao = new BusinessDao();
  SlaNodeConfig vo = (SlaNodeConfig)request.getAttribute("vo");
 String menuTable = (String)request.getAttribute("menuTable");;
 String intervalstr = vo.getIntervalunit();
 String showstr = "分钟";
 if("m".equals(intervalstr)){
 	showstr = "分钟";
 }else if("h".equals(intervalstr)){
 	showstr = "小时";
 }else if("d".equals(intervalstr)){
 	showstr = "天";
 }else if("w".equals(intervalstr)){
 	showstr = "周";
 }else if("mt".equals(intervalstr)){
 	showstr = "月";
 }else if("y".equals(intervalstr)){
 	showstr = "年";
 }
%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
     //var chk1 = checkinput("alias","string","名称",15,false);
     //var chk2 = checkinput("str","string","路径",30,false);
     //var chk3 = checkinput("ipaddress","ip","IP地址",30,false);
     //var chk4 = checkinput("timeout","number","超时",30,false);
     
     //if(chk1&&chk2&&chk3&&chk4)
     {
     
        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
        //msg.style.display="block";
        mainForm.action = "<%=rootPath%>/ciscosla.do?action=update";
        mainForm.submit();
     }  
       // mainForm.submit();
 });	
	
});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
//-- nielin modify at 2010-01-04 end ----------------


//-- nielin add at 2010-07-27 start ----------------
function setBid(eventTextId , eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/business.do?action=setBid&event='+event.id+'&value='+event.value + '&eventText=' + eventTextId);
}
//-- nielin add at 2010-07-27 end ----------------




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
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">
<form method="post" name="mainForm">
<input type=hidden name="id" value="<%=vo.getId()%>">
<table id="body-container" class="body-container">
	<tr>
		<td width="200" valign=top align=center>
			<%=menuTable%>
		
		</td>
            <td align="center" valign=top>
			<table style="width:98%"  cellpadding="0" cellspacing="0" algin="center">
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%">
				<table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="content-title">服务质量 >> SLA管理 >> SLA 监视编辑</td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>										
		<tr >
			<td >
			
					<table id="detail-content-body" class="detail-content-body">
								
						<tr >						
							<TD nowrap align="right" height="24" width="10%">名称&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="name" maxlength="50" size="30" class="formStyle" value="<%=vo.getName()%>"><font color="red">*&nbsp;</font>
							</TD>
							<TD nowrap align="right" height="24" width="10%">描述&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="descr" maxlength="50" size="30" class="formStyle" value="<%=vo.getDescr()%>"><font color="red">*&nbsp;</font>
							</TD>
						</tr>
						<tr style="background-color: #ECECEC;">	
						<TD nowrap align="right" height="24" width="10%">入口号&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=vo.getEntrynumber()%>
							<input type="hidden" name="entrynumber" value=<%=vo.getEntrynumber()%>>
							</TD>					
							<TD nowrap align="right" height="24" width="10%">SLA类型&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
																						<select id="slatype" name="slatype">
																								<option value="<%=vo.getSlatype()%>"><%=vo.getSlatype()%></option>
																								<option value="icmp">ICMP</option>
																								<option value="icmppath">ICMP Path</option>
																								<option value="udp">UDP</option>
																								<option value="jitter">JITTER(抖动)</option>
																								<option value="tcpconnectwithresponder">TCP Connect(启用Responder)</option>
																								<option value="tcpconnectnoresponder">TCP Connect(未启用Responder)</option>
																								<option value="http">HTTP</option>
																								<option value="dns">DNS</option>
																						</select><font color="red">*&nbsp;</font>
							</TD>
							<tr >
							<TD nowrap align="right" height="24" width="10%">采集间隔&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
																						<select id="poll_interval" name="poll_interval">
																								<option value="<%=vo.getIntervals()%>-<%=vo.getIntervalunit()%>" selected><%=vo.getIntervals()%><%=showstr%></option>
																								<option value="1-m">1分钟</option>
																								<option value="2-m">2分钟</option>
																								<option value="3-m">3分钟</option>
																								<option value="4-m">4分钟</option>
																								<option value="5-m">5分钟</option>
																								<option value="10-m">10分钟</option>
																								<option value="30-m">30分钟</option>
																								<option value="1-h">1小时</option>
																								<option value="4-h">4小时</option>
																								<option value="8-h">8小时</option>
																								<option value="12-h">12小时</option>
																								<option value="1-d">1天</option>
																								<option value="1-w">1周</option>
																								<option value="1-mt">1月</option>
																								<option value="1-y">1年</option>
																						</select><font color="red">*&nbsp;</font>
							</TD>																		      		
							<TD nowrap align="right" height="24" width="10%">是否采集&nbsp;</TD>				
							<TD nowrap width="40%" colspan=3>&nbsp;
								<select   name="mon_flag"  class="formStyle">
								<%
									if(vo.getMon_flag() == 0){
								%>
									<option value=0 selected>否</option>
									<option value=1>是</option>
								<%
									}else{
								%>
									<option value=0 >否</option>
									<option value=1 selected>是</option>								
								<%
									}
								%>
								</select>
							</TD>																						
						</tr>
						<tr style="background-color: #ECECEC;">						
							<TD nowrap align="right" height="24" width="10%">采集方式&nbsp;</TD>				
							<TD nowrap width="40%" colspan=3>&nbsp;
																			    		<select id="collecttype" name="collecttype">
																			    				<option value="<%=vo.getCollecttype()%>"><%=vo.getCollecttype()%></option>
																								<option value="snmp">SNMP</option>
																								<option value="telnet">Telnet</option>
																						</select><font color="red">*&nbsp;</font>
							</TD>
						</tr>												
															<tr>
																<!-- nielin add (for timeShareConfig) start 2010-01-03 -->
																<td><input type="hidden" id="rowNum" name="rowNum"></td>
																<!-- nielin add (for timeShareConfig) end 2010-01-03 -->
															
															</tr>
															<tr>
																<TD nowrap colspan="4" align=center>
																<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																	<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
																</TD>	
															</tr>	
				        								</table>
			</td>
		</tr>
	</table>
</td>
			</tr>
		</table>
</BODY>
</HTML>
