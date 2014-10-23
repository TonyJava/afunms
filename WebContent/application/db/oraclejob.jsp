<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>

<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>

<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.common.util.CreatePiePicture"%>



<%
	String rootPath = request.getContextPath();

	String id = (String)request.getAttribute("id");
	String _flag = (String)request.getAttribute("flag");
  	String mysid = (String)request.getAttribute("sid");
	double avgpingcon = (Double)request.getAttribute("avgpingcon");
	int percent1 = Double.valueOf(avgpingcon).intValue();
	int percent2 = 100-percent1;
	
	String lstrnStatu = (String)request.getAttribute("lstrnStatu");
	if("READ WRITE".equalsIgnoreCase(lstrnStatu)){lstrnStatu="已启动";}else{lstrnStatu="未启动";}
	Hashtable isArchive_h = (Hashtable)request.getAttribute("isArchive_h"); 
	String created = "";
	if(isArchive_h!=null){created=(String)isArchive_h.get("CREATED");}
	Hashtable memPerfValue = (Hashtable)request.getAttribute("memPerfValue"); 
	if(memPerfValue == null)memPerfValue = new Hashtable();
	String buffercache = "0";
	
DBVo vo  = (DBVo)request.getAttribute("db");
  String myip = vo.getIpAddress();
  String myport = vo.getPort();
  String myUser = vo.getUser();
  String myPassword = EncryptUtil.decode(vo.getPassword());
  String dbPage = "oraclejob";
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");
Vector jobv = (Vector)request.getAttribute("jobv");
if(jobv == null)jobv = new Vector();
String[] sysItem3={"HOST_NAME","DBNAME","VERSION","INSTANCE_NAME","STATUS","STARTUP_TIME","ARCHIVER"};
String[] sysItemch3={"主机名称","DB 名称","DB 版本","例程名","例程状态","例程开始时间","归档日志模式"};   
 Hashtable orasys = (Hashtable)request.getAttribute("sysvalue");    
 Hashtable cursors = (Hashtable)request.getAttribute("cursors");
if(cursors == null)cursors = new Hashtable();			  	   
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>

<%
 int intbuffercache = 0;
 String opencurstr = "0";
 String curconnectstr = "0";
 String dictionarycache = "0";
 String librarycache = "0";
 String pctmemorysorts = "";
 String pctbufgets = "0";
 String unpctmemorysorts = "0";
 String undictionarycache = "0";
 String unlibrarycache = "0";
 String unbuffercache = "0";
 String unpctbufgets = "0";
	if(memPerfValue!=null){
		if(memPerfValue.containsKey("buffercache") &&  memPerfValue.get("buffercache") != null)
			buffercache = (String)memPerfValue.get("buffercache");
																	                  		
		try{
			intbuffercache = Math.round(Float.parseFloat(buffercache));
		}catch(Exception e){
			e.printStackTrace();
		}
																
		if(memPerfValue.containsKey("dictionarycache") &&  memPerfValue.get("dictionarycache") != null)
			dictionarycache = (String)memPerfValue.get("dictionarycache");													
		if(memPerfValue.containsKey("librarycache") &&  memPerfValue.get("librarycache") != null)
			librarycache = (String)memPerfValue.get("librarycache");	
		if(memPerfValue.containsKey("pctmemorysorts") &&  memPerfValue.get("pctmemorysorts") != null)
			pctmemorysorts = (String)memPerfValue.get("pctmemorysorts");
		if(memPerfValue.containsKey("pctbufgets") &&  memPerfValue.get("pctbufgets") != null)
			pctbufgets = (String)memPerfValue.get("pctbufgets");
		if(cursors.containsKey("opencur") &&  cursors.get("opencur") != null)
			opencurstr = (String)cursors.get("opencur");
		if (cursors.containsKey("curconnect")&& cursors.get("curconnect") != null){
			curconnectstr = (String) cursors.get("curconnect");
		}
		if(pctmemorysorts.equals(""))
			pctmemorysorts = "0";
		unpctmemorysorts = String.valueOf(100 - Math.round(Float.parseFloat(pctmemorysorts)));
		if(dictionarycache.equals(""))
			dictionarycache = "0";
		undictionarycache = String.valueOf(100 - Integer.parseInt(dictionarycache));
		if(librarycache.equals(""))
			librarycache = "0";
		unlibrarycache = String.valueOf(100 - Integer.parseInt(librarycache));
		if(buffercache.equals(""))
			buffercache = "0";
		unbuffercache = String.valueOf(100 - Integer.parseInt(buffercache));
		if(pctbufgets.equals(""))
			pctbufgets = "0";
		unpctbufgets = String.valueOf(100 - Double.valueOf(pctbufgets));
	}
	
	
	String picip = CommonUtil.doip(myip);
	
    	//生成当天平均连通率图形
	CreatePiePicture _cpp = new CreatePiePicture();
	_cpp.createAvgPingPic(picip,avgpingcon); 
	

       //缓存利用率
	CreateMetersPic cmp = new CreateMetersPic();
	cmp.createpubliccpuPic(picip,intbuffercache,"缓存命中率");
	
	StringBuffer dataStr = new StringBuffer();
		dataStr.append("连通;").append(Math.round(avgpingcon)).append(
				";false;7CFC00\\n");
		dataStr.append("未连通;").append(100 - Math.round(avgpingcon))
				.append(";false;FF0000\\n");
		String avgdata = dataStr.toString();
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="utf-8" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script>
function createQueryString(){
		var user_name = encodeURI($("#user_name").val());
		var passwd = encodeURI($("#passwd").val());
		var queryString = {userName:user_name,passwd:passwd};
		return queryString;
	}

</script>


		<script language="javascript">	
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
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
	//go();
	setClass();
}

function setClass(){
	document.getElementById('oraDetailTitle-11').className='detail-data-title';
	document.getElementById('oraDetailTitle-11').onmouseover="this.className='detail-data-title'";
	document.getElementById('oraDetailTitle-11').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		document.getElementById("sid").value="<%=request.getAttribute("sid")%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
		<script type="text/javascript">
        var xmlHttp;
        var bar_color = '#00FA9A';      /*进度条颜色*/
        var span_id = "block";
        var clear = "&nbsp;&nbsp;&nbsp;"

        function createXMLHttpRequest() {
            if (window.ActiveXObject) {
                xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            } 
            else if (window.XMLHttpRequest) {
                xmlHttp = new XMLHttpRequest();                
            }
        }

        function go() {     /*请求ProgresBarJsp.jsp*/
            createXMLHttpRequest();
            checkDiv();
            var url = "<%=rootPath%>/application/db/ProgressBarJsp.jsp?task=create";
            var button = document.getElementById("go");
            button.disabled = true;
            xmlHttp.open("GET", url, true);
            xmlHttp.onreadystatechange = goCallback;     //请求响应后，调用的goCallback函数
            xmlHttp.send(null);
        }

        function goCallback() {     
            if (xmlHttp.readyState == 4) {
                if (xmlHttp.status == 200) {
                    setTimeout("pollServer()", 500);
                }
            }
        }
        
        function pollServer() {
            createXMLHttpRequest();
            var url = "<%=rootPath%>/application/db/ProgressBarJsp.jsp?task=poll";
            xmlHttp.open("GET", url, true);
            xmlHttp.onreadystatechange = pollCallback;
            xmlHttp.send(null);
        }
        
        function pollCallback() {
            if (xmlHttp.readyState == 4) {
                if (xmlHttp.status == 200) {
                    var percent_complete = xmlHttp.responseXML.getElementsByTagName("percent")[0].firstChild.data;                 
                    var index = processResult(percent_complete);                  
                    for (var i = 1; i <= index; i++) {
                        var elem = document.getElementById("block" + i);
                        if(i != 5){                        
                        elem.innerHTML = clear;
                        }
                        elem.style.backgroundColor = bar_color;
                        
                        //var next_cell = i + 1;
                        //if (next_cell > index && next_cell <= 9) {
                        //    document.getElementById("block" + next_cell).innerHTML = percent_complete + "%";
                        //}
                    }                    
                    if (index < 9) {
                        setTimeout("pollServer()", 500);   //每隔俩秒执行一次pollServer（）函数，只执行一次
                    } else {
                        setTimeout("go()", 500);            //隔1秒重新请求一次go函数，达到循环显示的目的
                        //document.getElementById("complete").innerHTML = "Complete!";
                        //document.getElementById("go").disabled = false;
                    }
                }
            }
        }
        
        function processResult(percent_complete) {
            var ind;
            if (percent_complete.length == 2) {
                ind = percent_complete.substring(0, 1);   /*取俩位数的第一位数*/
            } else {
                ind = 9;
            }
            return ind;
        }

        function checkDiv() {
            var progress_bar = document.getElementById("progressBar");
            //var rightbar = document.getElementById("rightBar");           
            if (progress_bar.style.visibility == "visible") {
                clearBar();
                document.getElementById("complete").innerHTML = "";
            } else {
                progress_bar.style.visibility = "visible";
                //rightbar.style.visibility = "visible";               
                //document.getElementById("rightb").style.backgroundColor = "#ECECEC";              
            }
        }
        
        function clearBar() {
            for (var i = 1; i < 10; i++) {
                var elem = document.getElementById("block" + i);
                elem.innerHTML = clear;
                elem.style.backgroundColor = "#ececec";                
                if(i == 5)
                {
                   elem.innerHTML = "<%=pctbufgets%>%";
                }
            }
        }
    </script>


  <script>
function gzmajax(){
$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/serverAjaxManager.ajax?action=ajaxUpdate_availability&tmp=<%=id%>&nowtime="+(new Date()),
			success:function(data){
				 $("#pingavg").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>pingavg.png?nowtime="+(new Date()), alt: "平均连通率" });
				$("#public").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>public.png?nowtime="+(new Date()), alt: "缓存命中率" }); 
			}
		});
}
$(document).ready(function(){
	//$("#testbtn").bind("click",function(){
	//	gzmajax();
	//});
setInterval(gzmajax,60000);
});
</script>



	</head>
	<body id="body" class="body" onload="initmenu();">
	<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="orderflag">
		<input type=hidden name="id">
		<input type="hidden" name="sid" id="sid" />
		<table id="body-container" class="body-container">
			<tr>
				<!-- 左右结构421-->
				<td class="td-container-menu-bar">
					<table id="container-menu-bar" class="container-menu-bar">
						<tr>
							<td>
								<%=menuTable%>
							</td>
						</tr>
					</table>
				</td>
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-application-detail">
								<table>
									<!-- 上下结构 -->
									<tr>
										<td>
											<!-- !!!!!! -->
											<table>
												<tr> 
														<td width=85%>
															<table id="container-main-application-detail"
																class="container-main-application-detail">
																<tr> 
																	<td>
																		<jsp:include page="/topology/includejsp/db_oracle.jsp">
																			<jsp:param name="Id" value="<%=vo.getId()%>" />
																			<jsp:param name="IpAddress" value="<%=vo.getIpAddress()%>" />
																			<jsp:param name="_flag" value="<%=_flag%>" />
																			<jsp:param name="Alias" value="<%=vo.getAlias()%>" />
																			<jsp:param name="Port" value="<%=vo.getPort()%>" />
																			<jsp:param name="DbName" value="<%=vo.getDbName()%>" />
																			<jsp:param name="dbtye" value="<%=dbtye%>" />
																			<jsp:param name="runstr" value="<%=runstr%>" />
																			<jsp:param name="created" value="<%=created%>" />
																			<jsp:param name="buffercache" value="<%=buffercache%>" />
																			<jsp:param name="lstrnStatu" value="<%=lstrnStatu%>" />
																			<jsp:param name="dictionarycache" value="<%=dictionarycache%>" />
																			<jsp:param name="unbuffercache" value="<%=unbuffercache%>" />
																			<jsp:param name="unpctmemorysorts" value="<%=unpctmemorysorts%>" />
																			<jsp:param name="librarycache" value="<%=librarycache%>" />
																			<jsp:param name="unlibrarycache" value="<%=unlibrarycache%>" />
																			<jsp:param name="pctmemorysorts" value="<%=pctmemorysorts%>" />
																			<jsp:param name="opencurstr" value="<%=opencurstr%>" />
																			<jsp:param name="picip" value="<%=picip%>" />
																			<jsp:param name="avgdata" value="<%=avgdata%>" />
																			<jsp:param name="memPerfValue" value="<%=memPerfValue%>" />
																			<jsp:param name="curconnectstr" value="<%=curconnectstr%>" />
																			<jsp:param name="managed" value="<%=managed%>" />
																			<jsp:param name="sid"  value="<%=mysid %>" />
																		</jsp:include> 
																	</td>
																</tr>
															</table>
														</td>
													<td class="td-container-main-tool" width="15%">
														<jsp:include page="/include/dbtoolbar.jsp">
															<jsp:param value="<%=myip%>" name="myip" />
															<jsp:param value="<%=myport%>" name="myport" />
															<jsp:param value="<%=myUser%>" name="myUser" />
															<jsp:param value="<%=myPassword%>" name="myPassword" />
															<jsp:param value="<%=mysid%>" name="sid" />
															<jsp:param value="<%=id%>" name="id" />
															<jsp:param value="<%=dbPage%>" name="dbPage" />
															<jsp:param value="Oracle" name="subtype" />
														</jsp:include>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td>
											<table id="container-main-application-detail"
												class="container-main-application-detail">
												<tr>
													<td width=70%>
														<table id="application-detail-content"
															class="application-detail-content">
															<tr>
																<td>
																	<table id="application-detail-data"
																		class="application-detail-data">
																		<tr>
																			<td class="detail-data-header">
																				<%=oraDetailTitleTable%>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table class="application-detail-data-body">
																					<tr>
																						<td>
																							<table width="96%" align="center" border="0"
																								cellspacing="0" cellpadding="0">
																								<tr bgcolor="#ECECEC" height="28">
																									<td align=center>
																										&nbsp;
																									</td>
																									<td align=center>
																										作业
																									</td>
																									<td align=center>
																										LOG用户
																									</td>
																									<td align=center>
																										最后日期
																									</td>
																									<td align=center>
																										状态
																									</td>
																								</tr>
																								<%
																									if (jobv != null) {
																										for (int i = 0; i < jobv.size(); i++) {
																											Hashtable ht2 = (Hashtable) jobv.get(i);
																											String job = ht2.get("job").toString().trim();
																											String loguser = ht2.get("loguser").toString().trim();
																											String lastdate = ht2.get("lastdate").toString().trim();
																											String failures = ht2.get("failures").toString().trim();
																								%>
																								<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
																									height="28">
																									<td class="application-detail-data-body-list"
																										align=center><%=i + 1%></td>
																									<td class="application-detail-data-body-list"
																										align=center>
																										&nbsp;<%=job%></td>
																									<td class="application-detail-data-body-list"
																										align=center>
																										&nbsp;<%=loguser%></td>
																									<td class="application-detail-data-body-list"
																										align=center>
																										&nbsp;<%=lastdate%></td>
																									<td class="application-detail-data-body-list"
																										align=center>
																										&nbsp;<%=failures%></td>
																								</tr>
																								<%
																									}
																									}
																								%>
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
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

	</form>
	<script>
		Ext.onReady(function()
		{  
		
		setTimeout(function(){
			        Ext.get('loading').remove();
			        Ext.get('loading-mask').fadeOut({remove:true});
			    }, 250);
			    Ext.get("process").on("click",function(){
		  
		  Ext.MessageBox.wait('数据加载中，请稍后.. ');   
		  mainForm.action = "<%=rootPath%>/oracle.do?action=sychronizeData&dbPage=<%=dbPage%>&id=<%=id%>&myip=<%=myip%>&myport=<%=myport%>&myUser=<%=myUser%>&myPassword=<%=myPassword%>&sid=<%=mysid%>&flag=<%=_flag%>";
		  mainForm.submit();
		 });    
		});
</script>
</BODY>
</HTML>