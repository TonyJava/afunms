<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant" %>
<%@page import="com.afunms.topology.dao.ManageXmlDao" %>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="com.afunms.system.model.User"%>     
<%
	String rootPath = request.getContextPath();	
	String viewFile = (String)session.getAttribute(SessionConstant.CURRENT_TOPO_VIEW);  
	User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);   
	ManageXmlDao dao = new ManageXmlDao();
	ManageXml vo = (ManageXml)dao.findByXml(viewFile);
	String bg = "";
	String Title = "";
	if(vo!=null){
	    bg = vo.getTopoBg();
	    Title = vo.getTopoTitle();
	}
	
	String width = "100%";
	String height = "100%";
    out.println("<script type=\"text/javascript\">");
    // 取得用户权限---用来限制保存、刷新、编辑等操作
	boolean admin = false;
	String user = "admin";

	if (user.equalsIgnoreCase("admin") || user.equalsIgnoreCase("superuser")) {
		out.println("var admin = true;"); //为了－－编辑－－能正常使用
		admin = true;
	}
	else {
		out.println("var admin = false;");	
		admin = false;
	}
	out.println("</script>");
	
	String disable = "";//控制按钮是否激活
	if (!admin) {
		disable = "disabled=\"disabled\"";
	}
	
%> 
<html xmlns:v="urn:schemas-microsoft-com:vml">
<head>
<meta http-equiv="content-type" content="text/html; charset=gb2312" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<title>显示服务器视图</title>   
<link href="<%=rootPath%>/resource/css/topo_style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="js/profile.js"></script>
<%
	//-----------判断全屏显示状态----------------
	String fullscreen = request.getParameter("fullscreen");
	if (fullscreen == null || fullscreen.equals("0")) {
		out.println("<script type=\"text/javascript\">var fullscreen = 0;</script>");
	}
	else {
	// 如果是全屏显示，修改 viewWidth
		out.println("<script type=\"text/javascript\">var fullscreen = 1;");
		out.println("viewWidth = window.screen.width;</script>");
	}
%>
<script type="text/javascript" src="js/global.js"></script>
<script type="text/javascript" src="js/disable.js"></script>
<script type="text/javascript" src="js/menu.js"></script>
<script type="text/javascript" src="js/map.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/engine.js"></script> 
<script type="text/javascript" src="<%=rootPath%>/js/util.js"></script> 
<script type="text/javascript" src="<%=rootPath%>/dwr/interface/TopoRemoteService.js"></script>
<script type="text/javascript" src="<%=rootPath%>/dwr/interface/LinkRemoteService.js"></script>
<script type="text/javascript" src="js/topoutil.js"></script>
<script type="text/javascript" src="js/topology.js"></script>
<script type="text/javascript" src="js/toolbar.js"></script>
<script type="text/javascript" src="js/edit.js"></script>
<!-- DIV弹出层的js -->
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/common/css/styleDIV.css" />
<script type="text/javascript" src="<%=rootPath%>/common/js/jquery-1.4.1.min.js"></script>
<script type="text/javascript" src="<%=rootPath%>/common/js/alarmtipswindown.js"></script>
<!-- 告警滑出导航栏 -->
<link rel="stylesheet" href="<%=rootPath%>/common/css/style2.css" type="text/css" media="screen"/>
<script type="text/javascript" src="<%=rootPath%>/common/js/topo-tool-bar.js"></script>
<!-- <script type="text/javascript" src="js/verticaltoolbar.js"></script> 
 -->
<%
	//它控制设备名称显示信息（IP还是名字text）
	//g_viewflag在global.js中定义，默认为0，所以要在其后
	String viewflag = request.getParameter("viewflag");	
	if (viewflag == null) 
		out.print("<script type=\"text/javascript\">g_viewFlag = 0;</script>");
	else 
		out.print("<script type=\"text/javascript\">g_viewFlag = " + viewflag + ";</script>");	
	
%>
<script type="text/javascript">
	window.onerror = new Function('return true;');		// 容错
	var fatherXML = "<%=viewFile%>";//yangjun add 关联拓扑图时获得父页xml
	
	function doInit()
	{
		loadXML("<%=rootPath%>/resource/xml/<%=viewFile%>");
		<%if (viewFile.equalsIgnoreCase("networkvlan.jsp")){ %>
			document.getElementById("vlan").value = 1;
		<%
			}else{
		%>
			document.getElementById("vlan").value = 0;
		<%
			}
		%>
		//var autoR = setInterval(autoRefresh,1000*60*3);
	}
	
	
	function deleteLink(id) {
	    if (window.confirm("确定删除该链路吗？")) {
	        window.location = "<%=rootPath%>/link.do?action=deleteLink&lineId="+id+"&xml=<%=viewFile%>";
	    }
    }
    function editLink(id) {
	    var url="<%=rootPath%>/link.do?action=editLink&lineId="+id;
        showModalDialog(url,window,'dialogwidth:500px; dialogheight:360px; status:no; help:no;resizable:0');
    }
    //删除示意链路
    function deleteLine(id){
        window.location = "<%=rootPath%>/link.do?action=deleteDemoLink&id="+id+"&xml=<%=viewFile%>";
        //alert("删除成功！");
        //autoRefresh();
    }
    //删除示意设备
    function deleteHintMeta(id) {
        var xml = "<%=viewFile%>";
        if (window.confirm("确定删除该设备吗？")) {
            window.location = "<%=rootPath%>/submap.do?action=deleteHintMeta&nodeId="+id+"&xml="+xml;
            alert("删除成功！");
	        autoRefresh();
	    }
    }
    //添加实体设备
    function addEquip(nodeid,nodeCategory){
        var xml = "<%=viewFile%>";
        var url = "<%=rootPath%>/resource/xml/<%=viewFile%>";
        TopoRemoteService.addEquipToMap(xml, nodeid, nodeCategory,{
				callback:function(data){
					if(data){
						addNode(nodeid,url);
					}
				}
			});
    }
    //删除实体设备
    function deleteEquip(nodeid,category){
        if (window.confirm("此操作会将该设备从系统彻底删除,确定删除该设备吗？")) {
            window.location="<%=rootPath%>/submap.do?action=deleteEquipFromSubMap&xml=<%=viewFile%>&node="+nodeid+"&category="+category;
            alert("删除成功！");
            autoRefresh();
        }
    }
    function autoRefresh()
	{
	    window.location = "showMap.jsp";
	}
    //只从拓扑图移除实体设备
    function removeEquip(nodeid){
        if (window.confirm("此操作会将该设备从当前拓扑图删除,确定删除该设备吗？")) {
        	//window.location = "../../submap.do?action=removeEquipFromSubMap&xml="+jsp.replace(/(\.jsp)*\b/ig,'')+"&node="+nodeid;
           window.location = "<%=rootPath%>/submap.do?action=removeEquipFromSubMap&xml=<%=viewFile%>&node="+nodeid;
           alert("删除成功！");
           /* 
           //TODO 删除节点后  转入topo页 需停顿一段短暂时间 ，目的是为了等待xml文件更新
           由于网络情况补丁，会产生问题。
           *测试下载和脚本执行的异步性
           
           function timer(){
        	   console.log(timer.count++);
        	   setTimeout(timer,1000)
           }
           timer.count = 0;
           setTimeout(timer,1000); */
          
           //autoRefresh();
        }
    }
    //服务器设备相关应用添加
    function addApplication(nodeid,ip){
        //alert(nodeid+"_"+ip);
        window.location="<%=rootPath%>/submap.do?action=addApplications&xml=<%=viewFile%>&node="+nodeid+"&ip="+ip;
        alert("获取该服务器相关应用成功！");
       
    }
    //查看设备面板图
    function showpanel(ip,width,height){
        window.open("<%=rootPath%>/submap.do?action=showpanel&ip="+ip,"panelfullScreenWindow", "toolbar=no,height="+height+",width="+width + ",scrollbars=no,"+"screenX=0,screenY=0");
    }
    
  //创建实体链路
    function addLink(direction1,linkName, maxSpeed, maxPer, xml, start_id, start_index, end_id, end_index,linetext,interf){
        var url = "<%=rootPath%>/resource/xml/<%=viewFile%>";
        LinkRemoteService.addLink(direction1,linkName, maxSpeed, maxPer, xml, start_id, start_index, end_id, end_index,linetext,interf, {
				callback:function(data){
					if(data=="error"){
						alert("实体链路创建失败！");
					} else if(data=="error1"){
					    alert("实体链路创建失败:相同端口的链路已经存在!");
					} else if(data=="error2"){
					    alert("实体链路创建失败:已经创建双链路!");
					} else {
					    if(data){
					        var arr=data.split(":");
					        if(arr[1]=="0"){
					            addlink(arr[0],url);
					        } else {
					            addAssLink(arr[0],url)
					        }
					    }
					}
				}
			});
    }
    //创建示意链路
    function addline(direction1,xml,line_name,link_width,start_id,start_x_y,s_alias,end_id,end_x_y,e_alias){
        var url = "<%=rootPath%>/resource/xml/<%=viewFile%>";
        LinkRemoteService.addDemoLink(direction1,xml,line_name, link_width, start_id, start_x_y, s_alias, end_id, end_x_y, e_alias, {
				callback:function(data){
					if(data=="error"){
						alert("示意链路创建失败！");
					} else {
					    if(data){
					        addLine(data,url);
					    }
					}
				}
			});
    }
    //添加示意设备
    function addHintMeta(setting){
        var url = "<%=rootPath%>/resource/xml/<%=viewFile%>";
        TopoRemoteService.addHintMeta(setting,{
				callback:function(data){
				    if(data=="error"){
						alert("添加示意图元失败！");
					} else {
						addNode(data,url);
					}
				}
			});
    }
    
    function showalert(id) {
		//window.parent.parent.opener.location="/afunms/detail/dispatcher.jsp?id="+id;
		window.parent.parent.opener.parent.window.document.getElementById('mainFrame').src="/afunms/detail/dispatcher.jsp?id="+id+"&fromtopo=true";
	}
  //弹出层
	function showAlarmTipsWindown(title,id,width,height){
		alarmtipsWindown(title,"id:"+id,width,height,"true","","false",id);
	}
	//弹出层调用
	function popTipsAlarm(){
		showAlarmTipsWindown("告警信息", 'simTestContentAlarm', 250, 55);
	}
	
	 $(document).ready(function(){
		popTipsAlarm();
		var timer1;
		timer1=window.setInterval("getAlarmData();",200*60);	//2分钟更新一次DIV
	});
	

		function getAlarmData(){
		    //popTips();
			AlarmSummarize.getLastestEventList1('<%=current_user.getBusinessids()%>','<%=viewFile%>' , {
	               callback:function(data){
	                   //alert(data.length);
	                   if (data && data.length) {
	                       var i = 0;
	                       var length = data.length;
	                       
	                       var alarmStr="<marquee direction='up' scrollamount='2' onmouseover='this.stop()' onmouseout='this.start()'>";
	                       while (i < length) {
	                           var eventList = data[i];
	                           var alarmLevel=eventList.alarmlevel;
	                           var alarm_image = "alarm_level_1.gif";
	                           //alert("alarmLevel==="+alarmLevel);
	                           if (alarmLevel == 1) {
	                               alarm_image = "alarm_level_1.gif";
	                           } else if (alarmLevel == 2) {
	                               alarm_image = "alarm_level_2.gif";
	                           } else if (alarmLevel == 3) {
	                               alarm_image = "alert.gif";
	                           } 
	                           //alert("<%=rootPath%>/resource/image/topo/"+alarm_image);
	                           alarmStr=alarmStr+"<li><img src='<%=rootPath%>/resource/image/topo/"+alarm_image+"'><font color=#000000>"+eventList.content+"</font></li>";
	                           i++;
	                       }
	                       alarmStr=alarmStr+"</marquee>";
	                       var alarmInfo=document.getElementById("windown-content-alarm");
	                       //alert(alarmInfo.innerHTML);
	                       if(alarmInfo){
	                           alarmInfo.innerHTML='<div class="mainlist1"><ul id="alarmInfo1">'+alarmStr+'</ul></div>';
	                       }
	                   }
	               }   
	           });
		}
</script>

<script type="text/javascript">
//window.parent.frames['mainFrame'].location.reload();//保存链路后刷新拓扑图
var curTarget = "showMap.jsp?filename=<%=viewFile%>&fullscreen=<%=fullscreen%>";
var display = false;	    // 是否显示快捷列表
var controller = false;		// 是否显示控制器
//转换目标(jsp)文件
function updateState(target) {
	curTarget = target;

}

	function searchNode() {

		var ip = window.prompt("请输入需要搜索的设备IP地址", "在此输入设备IP地址");
		if (ip == null)
			return true;
		else if (ip == "在此输入设备IP地址")
			return;

		if (!checkIPAddress(ip))
			searchNode();

		var coor = window.parent.mainFrame.getNodeCoor(ip);
		if (coor == null) {
			var msg = "没有在图中搜索到IP地址为 " + ip + " 的设备。";
			window.alert(msg);
			return;
		} else if (typeof coor == "string") {
			window.alert(coor);
			return;
		}

		// 移动设备到中心标记处
		window.parent.mainFrame.moveMainLayer(coor);
	}

	//检测用户是否具有保存拓扑图的权限
	function savefile() {
		console.log('savefile');

		if (!admin) {
			window.alert("您没有保存视图的权限！");
			return;
		}
		parent.mainFrame.saveFile();
	}
	//保存拓扑图
	function saveFile() {
		//不选中   显示工具栏  复选框
		parent.topFrame.document.getElementsByName('checkbox')[0].checked = false;
		//resetProcDlg();
		//var target = "showMap.jsp?filename=<%=viewFile%>&fullscreen=" + fullscreen;
		var target = "showMap.jsp?filename=<%=viewFile%>&fullscreen=1";
		updateState(target);
		save();  //topoloty.js中的函数,用于保存图数据--->String串
	}


//刷新拓扑图
function refreshFile() 
{
	parent.topFrame.document.getElementsByName('checkbox')[0].checked = false;
	if (window.confirm("“刷新”前是否需要保存当前拓扑图？")) {
		saveFile();
		return;
	}
	window.location.reload();
}

//全屏观看
function gotoFullScreen() {
parent.mainFrame.resetProcDlg();
var status = "toolbar=no,height="+ window.screen.height + ",";
status += "width=" + (window.screen.width-8) + ",scrollbars=no";
status += "screenX=0,screenY=0";
window.open("index.jsp?fullscreen=yes", "fullScreenWindow", status);
parent.mainFrame.zoomProcDlg("out");
}


//创建实体链路
function createEntityLink(){
alert('createEntityLink');
var objLinkAry = new Array();
var xml = "<%=viewFile%>";
if(window.parent.frames['mainFrame'].objMoveAry!=null&&window.parent.frames['mainFrame'].objMoveAry.length>0){//框选
    objLinkAry = window.parent.frames['mainFrame'].objMoveAry;
}
if(window.parent.frames['mainFrame'].objEntityAry!=null&&window.parent.frames['mainFrame'].objEntityAry.length>0){//ctrl选
    objLinkAry = window.parent.frames['mainFrame'].objEntityAry;
}
if(objLinkAry==null||objLinkAry.length!=2){
    alert("请选择两个设备lllllllllllllll！");
    return;
}
if(objLinkAry[0].name.substring(objLinkAry[0].name.lastIndexOf(",")+1)=="1"){
    alert("请选择非示意设备!");
    return;
}
var start_id = objLinkAry[0].id.replace("node_","");

if(objLinkAry[1].name.substring(objLinkAry[1].name.lastIndexOf(",")+1)=="1"){
    alert("请选择非示意设备!");
    return;
}
var end_id = objLinkAry[1].id.replace("node_","");     

if(start_id.indexOf("net")==-1||end_id.indexOf("net")==-1){
    alert("请选择网络设备!");
    return;
}
var url="<%=rootPath%>/link.do?action=addLink&start_id="+start_id+"&end_id="+end_id+"&xml="+xml;
showModalDialog(url,window,'dialogwidth:500px; dialogheight:400px; status:no; help:no;resizable:0');
}
//创建子图
function createSubMap(){
var objEntityAry = new Array();
if(window.parent.frames['mainFrame'].objMoveAry!=null&&window.parent.frames['mainFrame'].objMoveAry.length>0){//框选
    objEntityAry = window.parent.frames['mainFrame'].objMoveAry;
}
if(window.parent.frames['mainFrame'].objEntityAry!=null&&window.parent.frames['mainFrame'].objEntityAry.length>0){//ctrl选
    objEntityAry = window.parent.frames['mainFrame'].objEntityAry;
}
var lineArr = window.parent.frames['mainFrame'].lineMoveAry; 
var asslineArr = window.parent.frames['mainFrame'].assLineMoveAry; 
var objEntityStr = "";//节点信息
var linkStr = "";//链路信息
var asslinkStr = "";//链路信息
if(objEntityAry!=null&&objEntityAry.length>0){
    for(var i=0;i<objEntityAry.length;i++){
        objEntityStr += objEntityAry[i].id.replace("node_","") +",";
    }
}
if(lineArr!=null&&lineArr.length>0){
    for(var i=0;i<lineArr.length;i++){
        linkStr += lineArr[i].id.replace("line_","") + "," + lineArr[i].lineid + ";";
    }
}
if(asslineArr!=null&&asslineArr.length>0){
    for(var i=0;i<asslineArr.length;i++){
        asslinkStr += asslineArr[i].id.split("#")[0].replace("line_","") + "," + asslineArr[i].lineid + ";";
    }
}
var url="<%=rootPath%>/submap.do?action=createSubMap&objEntityStr="+objEntityStr+"&linkStr="+linkStr+"&asslinkStr="+asslinkStr;
showModalDialog(url,window,'dialogwidth:500px; dialogheight:350px; status:no; help:no;resizable:0');
}
//创建示意链路
function createDemoLink(){
var objEntityAry = new Array();
if(window.parent.frames['mainFrame'].objMoveAry!=null&&window.parent.frames['mainFrame'].objMoveAry.length>0){//框选
    objEntityAry = window.parent.frames['mainFrame'].objMoveAry;
}
if(window.parent.frames['mainFrame'].objEntityAry!=null&&window.parent.frames['mainFrame'].objEntityAry.length>0){//ctrl选
    objEntityAry = window.parent.frames['mainFrame'].objEntityAry;
} 
if(objEntityAry==null||objEntityAry.length!=2){
    alert("请选择两个设备！");
    return;
}

var start_id = objEntityAry[0].id.replace("node_","");
var end_id = objEntityAry[1].id.replace("node_","");
var xml = "<%=viewFile%>";
var lineArr = window.parent.frames['mainFrame'].demoLineMoveAry;
if(lineArr!=null&&lineArr.length>0){
	if(isExistLineBetween(objEntityAry,lineArr)){
	    alert("选中的两台设备已经存在示意链路!");
	    return;
	}
}

var pos0 = getImagePropertiesBy(objEntityAry[0],{'x':1,'y':1});
var pos1 = getImagePropertiesBy(objEntityAry[1],{'x':1,'y':1});

var start_x_y = pos0.x+","+pos0.y;
var end_x_y = pos1.x+","+pos1.y;

/* var start_x_y=objEntityAry[0].style.left+","+objEntityAry[0].style.top;
var end_x_y=objEntityAry[1].style.left+","+objEntityAry[1].style.top;
 */
//alert(start_x_y+"="+end_x_y);
var url="<%=rootPath%>/link.do?action=readyAddLine&xml="+xml+"&start_id="+start_id+"&end_id="+end_id+"&start_x_y="+start_x_y+"&end_x_y="+end_x_y;
showModalDialog(url,window,'dialogwidth:510px; dialogheight:350px; status:no; help:no;resizable:0');
//parent.mainFrame.location = "<%=rootPath%>/link.do?action=addDemoLink&xml="+xml+"&id1="+start_id+"&id2="+end_id;
//alert("链路创建成功！");
//parent.mainFrame.location.reload();
}

//新增示意图元
function createDemoObj(){
//window.parent.mainFrame.ShowHide("1",null);拖拽方式
var url="<%=rootPath%>/submap.do?action=readyAddHintMeta&xml=<%=viewFile%>";
var returnValue = showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
//parent.mainFrame.location.reload();
}
//重建拓扑图
function rebuild(){
if (window.confirm("注意该操作会重新构建拓扑图数据，原拓扑图数据会丢失，还继续吗？")) {
	window.location = "<%=rootPath%>/submap.do?action=reBuild&xml=<%=viewFile%>";
	alert("操作成功!");
    parent.location.reload();
}
}

//备份拓扑图
function backup(){
var url="<%=rootPath%>/submap.do?action=readybackup&xml=<%=viewFile%>";
showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
}
//恢复拓扑图
function resume(){
var url="<%=rootPath%>/submap.do?action=readyresume&xml=<%=viewFile%>";
showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
}
function checkEntityLink(){
var objLinkAry = new Array();
var xml = "<%=viewFile%>";
if(window.parent.frames['mainFrame'].objMoveAry!=null&&window.parent.frames['mainFrame'].objMoveAry.length>0){//框选
    objLinkAry = window.parent.frames['mainFrame'].objMoveAry;
}
if(window.parent.frames['mainFrame'].objEntityAry!=null&&window.parent.frames['mainFrame'].objEntityAry.length>0){//ctrl选
    objLinkAry = window.parent.frames['mainFrame'].objEntityAry;
}
if(objLinkAry==null||objLinkAry.length!=2){
    alert("请选择两个设备！");
    return;
}
if(objLinkAry[0].name.substring(objLinkAry[0].name.lastIndexOf(",")+1)=="1"){
    alert("请选择非示意设备!");
    return;
}
var start_id = objLinkAry[0].id.replace("node_","");

if(objLinkAry[1].name.substring(objLinkAry[1].name.lastIndexOf(",")+1)=="1"){
    alert("请选择非示意设备!");
    return;
}
var end_id = objLinkAry[1].id.replace("node_","");     

if(start_id.indexOf("net")==-1||end_id.indexOf("net")==-1){
    alert("请选择网络设备!");
    return;
}
var url="<%=rootPath%>/topology/network/linkAnalytics.jsp?start_id="+start_id+"&end_id="+end_id;
showModalDialog(url,window,'dialogwidth:670px; dialogheight:370px; status:no; help:no;resizable:0');
}
//切换视图
function changeName() 
{
// 之前提醒用户保存
if (admin) {
	if (window.confirm("“切换视图”前是否需要保存当前拓扑图？")) {
		saveFile();
	}
}

if (g_viewFlag == 0) {
	g_viewFlag = 1;
	window.parent.parent.leftFrame.location = "tree.jsp?treeflag=1";
	parent.mainFrame.location = curTarget+"&viewflag=1";
}
else if (g_viewFlag == 1) {
	g_viewFlag = 0;
	window.parent.parent.leftFrame.location = "tree.jsp?treeflag=0";		
	parent.mainFrame.location = curTarget+"&viewflag=0";
}
else {
	window.alert("视图类型错误");
}
}

//显示视图控制器
function showController(flag) {

var result;
if (flag == false)
	controller = false;
if (controller) {
	result = parent.mainFrame.showController(controller);
	
	if (result == false) {
		window.alert("您没有选择视图，无控制器可用");
		return;
	}
		
	//document.all.controller.value = "关闭控制器";
	document.all.controller.title = "关闭显示框内的视图控制器";
	controller = false;
}
else {
	result = parent.mainFrame.showController(controller);
	
	if (result == false) {
		window.alert("您没有选择视图，无控制器可用");
		return;
	}

	//document.all.controller.value = "开启控制器";
	document.all.controller.title = "开启显示框内的视图控制器";
	controller = true;
}
}
function autoRefresh() 
{
	window.clearInterval(freshTimer);
	freshTimer = window.setInterval("refreshFile()",60000);
}

//选择视图,由顶部  框架获得
function changeView()
{
		if(parent.topFrame.document.getElementById('submapview').value == "")	return;
		//parent.location = "../submap/submap.jsp?submapXml=" + document.all.submapview.value;
		window.parent.parent.location = "../submap/index.jsp?submapXml=" + parent.topFrame.document.getElementById('submapview').value;
}
//拓扑图属性
function editMap(){
	var url="<%=rootPath%>/submap.do?action=readyEditMap";
	showModalDialog(url,window,'dialogwidth:500px; dialogheight:400px; status:no; help:no;resizable:0');
}
function cwin()
{
	 if(parent.parent.search.cols!='230,*')
	 {
	    parent.parent.search.cols='230,*';
	    document.all.pic.src ="<%=rootPath%>/resource/image/hide_menu.gif";
	    document.all.pic.title="隐藏树形";
	 }
	 
	 else
	 {
	    parent.parent.search.cols='0,*';
	    document.all.pic.src ="<%=rootPath%>/resource/image/show_menu.gif";
	    document.all.pic.title="显示树形";
	 }
}

</script>
<style>
v\:*{ behavior:url(#default#VML); }
</style>
</head>

<!--画框选择时，用的上下左右四根彩线-->
<img src="<%=rootPath%>/resource/image/topo/line_top.gif" id="imgTop" class="tmpImg" style="width:10; height:10 " />
<img src="<%=rootPath%>/resource/image/topo/line_left.gif" id="imgLeft" class="tmpImg" style="width:10; height:10 "/>
<img src="<%=rootPath%>/resource/image/topo/line_bottom.gif" id="imgBottom" class="tmpImg" style="width:10; height:10 "/>
<img src="<%=rootPath%>/resource/image/topo/line_right.gif" id="imgRight" class="tmpImg" style="width:10; height:10 "/>

<script type="text/javascript">

document.write('<form name="frmMap" method="post" action="<%=rootPath%>/network.do?action=save">');
document.write('<body class="main_body"  onLoad="doInit();window.parent.changeFlags();" onmousewheel="window.parent.parent.document.body.scrollTop -= event.wheelDelta/2;" onmousedown="bodyDown()" onselectstart="return false" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">');	

loadMoveController();		// 加载移动控制器
loadSizeController();		// 加载大小控制器
loadLinkLineInfo();			// 加载链路信息


document.write('<div id="processing" style="position:absolute;border:#000000 1px solid;font-size:14px;font-weight:bold;text-align:center;background-color:#F5F5F5;color:#000000;');
document.write('height:' + procDlgHeight + 'px;top:' + (document.body.clientHeight - procDlgHeight)/2 + 'px;');
document.write('width:' + procDlgWidth + 'px;left:'+ (document.body.clientWidth - procDlgWidth)/2 +'px;');
document.write('visibility:hidden;z-index:999;"><br/>正在处理数据，请稍候 ...</div>');
 



//loadLinkLineTip();			// 加载链路提示信息
//document.write('<div id="divTitle" align="center" style="font:oblique small-caps 900 29pt 黑体;"><%=Title%></div>');
if(window.addEventListener){
	document.write('<svg xmlns="http://www.w3.org/2000/svg"  id="divLayer" style="background-position: center;background-attachment:fixed;background-repeat: no-repeat;background-image:url(<%=rootPath%>/resource/image/bg/<%=bg%>);width:100%;height:100%;color:black;position:absolute;top:0px;left:0px;background-color:#FFFFFF;border:#FfFfFF; 1px solid;" onmousedown="divLayerDown(evt)" onclick="javascript:closeLineFrame();"></svg>');//#000066

}else{
	document.write('<div id="divLayer" style="background-position: center;background-attachment:fixed;background-repeat: no-repeat;background-image:url(<%=rootPath%>/resource/image/bg/<%=bg%>);width:100%;height:100%;color:black;position:absolute;top:0px;left:0px;background-color:#FFFFFF;border:#FfFfFF; 1px solid;" onmousedown="divLayerDown(event)" onclick="javascript:closeLineFrame();"></div>');//#000066
}
document.write('<input type="hidden" name="hidXml" id="hidXml"/>');
document.write('<input type="hidden" name="vlan" id="vlan"/>');
document.write('<input type="hidden" name="urlpath" id="urlpath" value="' + urlpath + '"/>');
document.write('<input type="hidden" name="filename" id="filename" value="' + filename + '"/>');
document.write('</body></form>');

</script>

<script type="text/javascript">
<!--
// 调整 divLayer 大小
/* function resizeTopDiv() {
		//document.all.divLayer.style.width = maxWidth + 800;
		//document.all.divLayer.style.height = maxHeight + 650;
		zoomProcDlg("out");
} */
	
	//setTimeout("zoomProcDlg('out')", 1000);
	zoomProcDlg('out');
	parent.topFrame.showController(false);
	function showDevice(action) {
		parent.location = action;
	}
//-->   
</script>
<div id="rp_list" class="rp_list" align="right" style="background:#ECECEC;">
	<ul>
		<li>
			<div id="rp_alarm_table">
				<table width="100%" height="100%" id="alarm-bar">
				    <tr>
						<td align="left">
						    <a href="#" onClick="javascript:cwin();">&nbsp;&nbsp;&nbsp;显示树形</a>
						</td>
						<td align="right">
							<input id="pic" type="button" name="showtree" class="button_showtree_out" onmouseover="javascript:buttonShowTreeOver();" onmouseout="javascript:buttonShowTreeOut();" onclick="javascript:cwin();" title="显示树形"/>
						</td>
					</tr>
					<tr>
					    <td align="left">
						    <a href="#">&nbsp;&nbsp;&nbsp;搜索</a>
						</td>
						<td align="right">
						    <input type="button" name="search" class="button_search_out" onmouseover="javascript:buttonSearchOver();" onmouseout="javascript:buttonSearchOut();" onclick="javascript:searchNode();" title="搜索"/>
						</td>
					</tr>
					<tr>
              <td align="left">
                  <a href="#" onClick="javascript:setSelect();">&nbsp;&nbsp;&nbsp;选择设备</a>
              </td>
              <td align="right">
                  <input type="button" name="select" class="button_select_out" onmouseover="javascript:buttonSelectOver();" onmouseout="javascript:buttonSelectOut();" onclick="javascript:setSelect();" title="选择设备"/>
              </td>
          	</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:saveFile();">&nbsp;&nbsp;&nbsp;保存拓扑图</a>
						</td>
						<td align="right">
							<input type="button" name="save" class="button_save_out" onmouseover="javascript:buttonSaveOver();" onmouseout="javascript:buttonSaveOut();" onclick="javascript:saveFile();" title="保存当前拓扑图数据"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:refreshFile();">&nbsp;&nbsp;&nbsp;刷新拓扑图</a>
						</td>
						<td align="right">
							<input type="button" name="refresh" class="button_refresh_out" onmouseover="javascript:buttonRefreshOver();" onmouseout="javascript:buttonRefreshOut();" onclick="javascript:refreshFile();" title="刷新当前拓扑图数据"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:changeName();">&nbsp;&nbsp;&nbsp;改变设备名</a>
						</td>
						<td align="right">
							<input type="button" name="view" class="button_view_out" onmouseover="javascript:buttonViewOver();" onmouseout="javascript:buttonViewOut();" onclick="javascript:changeName();" title="改变设备名显示信息"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:editMap();">&nbsp;&nbsp;&nbsp;拓扑图属性</a>
						</td>
						<td align="right">
							<input type="button" name="editmap" class="button_editmap_out" onmouseover="javascript:buttonEditMapOver();" onmouseout="javascript:buttonEditMapOut();" onclick="javascript:editMap();" title="拓扑图属性"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:createEntityLink();">&nbsp;&nbsp;&nbsp;创建实体链路</a>
						</td>
						<td align="right">
							<input type="button" name="create1" class="button_create1_out" onmouseover="javascript:buttonCreate1Over();" onmouseout="javascript:buttonCreate1Out();" onclick="javascript:createEntityLink();" title="创建实体链路"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:createDemoLink();">&nbsp;&nbsp;&nbsp;创建示意链路</a>
						</td>
						<td align="right">
							<input type="button" name="create2" class="button_create2_out" onmouseover="javascript:buttonCreate2Over();" onmouseout="javascript:buttonCreate2Out();" onclick="javascript:createDemoLink();" title="创建示意链路"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:createDemoObj();">&nbsp;&nbsp;&nbsp;创建示意图元</a>
						</td>
						<td align="right">
							<input type="button" name="create3" class="button_create3_out" onmouseover="javascript:buttonCreate3Over();" onmouseout="javascript:buttonCreate3Out();" onclick="javascript:createDemoObj();" title="创建示意图元"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:createSubMap();">&nbsp;&nbsp;&nbsp;创建子图</a>
						</td>
						<td align="right">
							<input type="button" name="create4" class="button_create4_out" onmouseover="javascript:buttonCreate4Over();" onmouseout="javascript:buttonCreate4Out();" onclick="javascript:createSubMap();" title="创建子图"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:rebuild();">&nbsp;&nbsp;&nbsp;重建拓扑图</a>
						</td>
						<td align="right">
							<input type="button" name="create5" class="button_create5_out" onmouseover="javascript:buttonCreate5Over();" onmouseout="javascript:buttonCreate5Out();" onclick="javascript:rebuild();" title="重建拓扑图"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:backup();">&nbsp;&nbsp;&nbsp;备份拓扑图</a>
						</td>
						<td align="right">
							<input type="button" name="create6" class="button_create6_out" onmouseover="javascript:buttonCreate6Over();" onmouseout="javascript:buttonCreate6Out();" onclick="javascript:backup();" title="备份拓扑图"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:resume();">&nbsp;&nbsp;&nbsp;恢复拓扑图</a>
						</td>
						<td align="right">
							<input type="button" name="create7" class="button_create7_out" onmouseover="javascript:buttonCreate7Over();" onmouseout="javascript:buttonCreate7Out();" onclick="javascript:resume();" title="恢复拓扑图"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:checkEntityLink();">&nbsp;&nbsp;&nbsp;链路同步</a>
						</td>
						<td align="right">
							<input type="button" name="create8" class="button_create1_out" onmouseover="javascript:buttonCreate1Over();" onmouseout="javascript:buttonCreate1Out();" onclick="javascript:checkEntityLink();" title="链路同步"/>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#">&nbsp;&nbsp;&nbsp;全屏观看</a>
						</td>
						<td align="right">
							<%if (fullscreen == null || fullscreen.equals("0")) {%>
								<input type="button" name="fullscreen" class="button_fullscreen_out" onmouseover="javascript:buttonFullscreenOver();" onmouseout="javascript:buttonFullscreenOut();" onclick="javascript:gotoFullScreen();" title="全屏观看视图"/>
							<%} else {%>
								<input type="button" name="fullscreen" class="button_fullscreen_out" onmouseover="javascript:buttonFullscreenOver();" onmouseout="javascript:buttonFullscreenOut();" onclick="javascript:window.parent.close();" value="关闭" title="关闭当前窗口"/>
							<%}%>
						</td>
					</tr>
					<tr>
						<td align="left">
						    <a href="#" onClick="javascript:showControllerTool();">&nbsp;&nbsp;&nbsp;关闭视图控制器</a>
						</td>
						<td align="right">
							<input type="button" name="controller" class="button_controller_out" onmouseover="javascript:buttonControllerOver();" onmouseout="javascript:buttonControllerOut();" onclick="javascript:showControllerTool();" title="关闭显示框内的视图控制器"/>
						</td>
					</tr>
				</table>
			</div>
		</li>
	</ul>
</div>
</html>
