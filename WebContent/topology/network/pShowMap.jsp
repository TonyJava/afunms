<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.topology.dao.ManageXmlDao"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="javax.imageio.ImageReader"%>
<%@page import="javax.imageio.stream.ImageInputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.afunms.common.util.SysLogger"%>
<%@page import="com.afunms.system.model.User"%>
<%       
	String rootPath = request.getContextPath();	
	String viewFile = (String)session.getAttribute(SessionConstant.CURRENT_TOPO_VIEW);  
	System.out.println(viewFile);
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
<title>网络拓扑图</title>
<link href="<%=rootPath%>/resource/css/topo_style.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/top.css" type="text/css">
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
<script type="text/javascript" src="js/profile.js"></script>
<script type="text/javascript" src="js/toolbar.js"></script>
<script type="text/javascript" src="js/edit.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/engine.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/util.js"></script>
<script type="text/javascript" src="<%=rootPath%>/dwr/interface/TopoRemoteService.js"></script>
<script type="text/javascript" src="<%=rootPath%>/dwr/interface/LinkRemoteService.js"></script>
<script type="text/javascript" src="<%=rootPath%>/dwr/interface/AlarmSummarize.js"></script>
<script type="text/javascript" src="js/topoutil.js"></script>
<script type="text/javascript" src="js/topology.js"></script>
<!-- DIV弹出层的js -->
<script type="text/javascript" src="<%=rootPath%>/common/js/jquery-1.4.1.min.js"></script>
<!-- 告警滑出导航栏 -->
<link rel="stylesheet" href="<%=rootPath%>/common/css/style2.css" type="text/css" media="screen" />
<script type="text/javascript" src="<%=rootPath%>/common/js/topo-tool-bar.js"></script>
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
	//openProcDlg();  //显示闪屏
	function savefile() {
		if (!admin) {
			window.alert("您没有保存视图的权限！");
			return;
		}
		parent.mainFrame.saveFile();
	}
	function saveFile() {
		resetProcDlg();
		var target = "showMap.jsp?filename=<%=viewFile%>&fullscreen=1";
		parent.topFrame.updateState(target);
		save();  //topoloty.js中的函数,用于保存图数据--->String串
	}

	function doInit()
	{
		loadXML("<%=rootPath%>/resource/xml/<%=viewFile%>");
		<%if (viewFile.equalsIgnoreCase("networkvlan.jsp")){ %>
			document.all("vlan").value = 1;
		<%
			}else{
		%>
			document.all("vlan").value = 0;
		<%
			}
		%>
		var autoR = setInterval(autoRefresh1,1000*30*1);
		autoRefresh1();
		var list = document.all.rp_alarm_table;
		if(window.parent.topFrame.topviewForm.checkbox.checked){
	        list.style.marginLeft="90px";
	    } else {
	        list.style.marginLeft="50px";
	    }
	}
	function autoRefresh2()
	{
	         LinkRemoteService.refreshLinkWidth("<%=viewFile%>",linkobjArray,{
				callback:function(data){
				   
					if(data){
						kkk(data);
					}
				}
			 });
	         TopoRemoteService.refreshLinkTxt(nodeobjArray,{
				callback:function(data){
				   
					if(data){
						fff(data);
					}
				}
			});
	}
	function autoRefresh1()
	{
	     var xml = "<%=viewFile%>";
	         LinkRemoteService.refreshLink(linkobjArray,{
				callback:function(data){
					if(data){
						tt(data);
					}
				}
			});
	         TopoRemoteService.refreshImage(xml,nodeobjArray,{
				callback:function(data){
					if(data){
						replaceNodeobj(data);
					}
				}
			});
	}
	function autoRefresh()
	{
        window.location = "showMap.jsp";
	}
	function deleteLink(id) {
	    if (window.confirm("确定删除该链路吗？")) {
	        window.location = "<%=rootPath%>/link.do?action=deleteLink&lineId="+id+"&xml=<%=viewFile%>";
	    }
    }   
    function editLink(id) {
	    var url="<%=rootPath%>/link.do?action=editLink&lineId="+id;
        showModalDialog(url,window,'dialogwidth:500px; dialogheight:430px; status:no; help:no;resizable:0');
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
    //告警确认
    function confirmAlarm(nodeid,nodeCategory){
        var xml = "<%=viewFile%>";
        if (window.confirm("此操作会将该设备的告警进行确认,确定吗？")) {
            TopoRemoteService.confirmAlarm(xml, nodeid, nodeCategory,{
				callback:function(data){
					if(data=="error"){
						alert("告警确认失败!");
					} else {
					    replaceNodePic(data);
					}
				}
			});
        }
    }
	//链路告警确认
    function confirmAlarmLink(lineId){
        var xml = "<%=viewFile%>";
        if (window.confirm("此操作会将该链路的告警进行消除,确定吗？")) {
            LinkRemoteService.confirmAlarm(xml, lineId,{
				callback:function(data){
					if(data=="error"){
						alert("告警确认失败!");
					} else {
					    replaceLinkPic(data);
					}
				}
			});
        }
    }
    //添加实体设备
    function addEquip(nodeid,nodeCategory){
        //window.location="<%=rootPath%>/submap.do?action=addEquipToMap&xml=<%=viewFile%>&node="+nodeid+"&category="+nodeCategory;
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
    //只从拓扑图移除实体设备
    function removeEquip(nodeid){
        if (window.confirm("此操作会将该设备从当前拓扑图删除,确定删除该设备吗？")) {
            window.location="<%=rootPath%>/submap.do?action=removeEquipFromSubMap&xml=<%=viewFile%>&node="+nodeid;
            alert("删除成功！");
            autoRefresh();   
        }
    }
    //服务器设备相关应用添加
    function addApplication(nodeid,ip){
        window.location="<%=rootPath%>/submap.do?action=addApplications&xml=<%=viewFile%>&node="+nodeid+"&ip="+ip;
        alert("获取该服务器相关应用成功！");
        autoRefresh();
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
    function getNodeInfo(id){
        alert(id);
    }
    function showalert(id) {
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
		//popTipsAlarm();
		var timer1;
		timer1=window.setInterval("getAlarmData();",200*60);	//2分钟更新一次DIV
	});
	
	function getAlarmData(){
		AlarmSummarize.getLastestEventList1('<%=current_user.getBusinessids()%>','<%=viewFile%>' , {
               callback:function(data){
                   if (data && data.length) {
                       var i = 0;
                       var length = data.length;
                       
                       var alarmStr="<marquee direction='up' scrollamount='2' onmouseover='this.stop()' onmouseout='this.start()'>";
                       while (i < length) {
                           var eventList = data[i];
                           var alarmLevel=eventList.alarmlevel;
                           var alarm_image = "alarm_level_1.gif";
                           if (alarmLevel == 1) {
                               alarm_image = "alarm_level_1.gif";
                           } else if (alarmLevel == 2) {
                               alarm_image = "alarm_level_2.gif";
                           } else if (alarmLevel == 3) {
                               alarm_image = "alert.gif";
                           } 
                           alarmStr=alarmStr+"<li><img src='<%=rootPath%>/resource/image/topo/"+alarm_image+"'><font color=#000000>"+eventList.content+"</font></li>";
                           i++;
                       }
                       alarmStr=alarmStr+"</marquee>";
                       var alarmInfo=document.getElementById("windown-content-alarm");
                       if(alarmInfo){
                           alarmInfo.innerHTML='<div class="mainlist1"><ul id="alarmInfo1">'+alarmStr+'</ul></div>';
                       }
                   }
               }   
           });
	}
</script>
<style>
v\:* {
	behavior: url(#default#VML);
}
body{
 SCROLLBAR-FACE-COLOR: #e8e7e7; 
 SCROLLBAR-HIGHLIGHT-COLOR: #ffffff; 
 SCROLLBAR-SHADOW-COLOR: #ffffff; 
 SCROLLBAR-3DLIGHT-COLOR: #cccccc; 
 SCROLLBAR-ARROW-COLOR: #03B7EC; 
 SCROLLBAR-TRACK-COLOR: #EFEFEF; 
 SCROLLBAR-DARKSHADOW-COLOR: #b2b2b2; 
 SCROLLBAR-BASE-COLOR: #000000;}
</style>
</head>
<!--画框选择时，用的上下左右四根彩线-->
<img src="<%=rootPath%>/resource/image/topo/line_top.gif" id="imgTop" class="tmpImg" style="width: 10; height: 10" />
<img src="<%=rootPath%>/resource/image/topo/line_left.gif" id="imgLeft" class="tmpImg" style="width: 10; height: 10" />
<img src="<%=rootPath%>/resource/image/topo/line_bottom.gif" id="imgBottom" class="tmpImg" style="width: 10; height: 10" />
<img src="<%=rootPath%>/resource/image/topo/line_right.gif" id="imgRight" class="tmpImg" style="width: 10; height: 10" />
<script type="text/javascript">

document.write('<body class="main_body"  onLoad="doInit();window.parent.changeFlags();" onmousewheel="window.parent.parent.document.body.scrollTop -= event.wheelDelta/2;" onmousedown="bodyDown()" onselectstart="return false" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">');	
document.write('<form name="frmMap" method="post" action="<%=rootPath%>/network.do?action=save">');

loadLinkLineInfo();			// 加载链路信息


//document.write('<table height="100%"><tr><td width="100%" align="left" height="100%">');
document.write('<div id="divDrag" style="background-color: #FFFFFF;width:100%;height:100%; top:0px;left:0px; position:absolute;" onmousedown="divLayerDown()" onclick="javascript:closeLineFrame();"></div>');
if(window.addEventListener){
	document.write('<svg xmlns="http://www.w3.org/2000/svg" style="background-position: center;background-attachment:fixed;background-repeat: no-repeat;width:1350px;height:629px;color:black;position:absolute;top:0px;left:0px;background-color:#FFFFFF;border:#FfFfFF; 1px solid;" onmousedown="divLayerDown(evt)" onclick="javascript:closeLineFrame();"><g  id="divLayer" transform="scale(0.5)"><image x="0" y="0" width="100%" height="100%" xlink:href="<%=rootPath%>/resource/image/bg/<%=bg%>"></image></g></svg>');//#000066

}else{
	document.write('<div id="divLayer" style="width:659px;height:100%; background:url(<%=rootPath%>/resource/image/bg/<%=bg%>) left top no-repeat; top:0px;left:0px; position:absolute;" onmousedown="divLayerDown(event)" onclick="javascript:closeLineFrame();"></div>');
	//document.write('<div id="divLayer" style="background-position: center;background-attachment:fixed;background-repeat: no-repeat;background-image:url(<%=rootPath%>/resource/image/bg/<%=bg%>);width:100%;height:100%;color:black;position:absolute;top:0px;left:0px;background-color:#FFFFFF;border:#FfFfFF; 1px solid;" onmousedown="divLayerDown(event)" onclick="javascript:closeLineFrame();"></div>');//#000066
}
	
//document.write('</td><td align="right" height="100%">');
//document.write('<div id="container-menu-bar" style="height:100%;width:200px;"></div>');
//document.write('</td></tr></table>');
document.write('<input type="hidden" name="hidXml"/>');
document.write('<input type="hidden" name="vlan"/>');
document.write('<input type="hidden" name="urlpath" value="' + urlpath + '"/>');
document.write('<input type="hidden" name="filename" value="' + filename + '"/>');
document.write('</form></body>');
</script>

<script type="text/javascript">
	function hideMenu(){
		var element = document.getElementById("container-menu-bar").parentElement;
		var display = element.style.display;
		if(display == "inline"){
			hideMenuBar();
		}else{
			showMenuBar();
		}
	}
	
	function showMenuBar(){
		var element = document.getElementById("container-menu-bar").parentElement;
		element.style.display = "inline";
		document.getElementById("container-menu-bar").innerHTML="<iframe src='indicatortree.jsp?treeflag=<%=viewflag%>&fromtopo=true&filename=<%=viewFile%>' height='100%' width='200px'/>";
	}
	
	function hideMenuBar(){
		var element = document.getElementById("container-menu-bar").parentElement;
		element.style.display = "none";
	}
    function cwin()
    {
	     if(parent.parent.search.cols!='230,*')
	     {
	        parent.parent.search.cols='230,*';
	        document.all.pic.style.backgroundImage = 'url("<%=rootPath%>/resource/image/hide_menu.gif")';
	        document.all.pic.title="隐藏树形";
	     }
	     else
	     {
	        parent.parent.search.cols='0,*';
	        document.all.pic.style.backgroundImage = 'url("<%=rootPath%>/resource/image/show_menu.gif")';
	        document.all.pic.title="显示树形";
	     }
    }
    function cwin()
    {
	     if(parent.parent.search.cols!='230,*')
	     {
	        parent.parent.search.cols='230,*';
	        document.all.pic.style.backgroundImage = 'url("<%=rootPath%>/resource/image/hide_menu.gif")';
	        document.all.pic.title="隐藏树形";
	     }
	     else
	     {
	        parent.parent.search.cols='0,*';
	        document.all.pic.style.backgroundImage = 'url("<%=rootPath%>/resource/image/show_menu.gif")';
	        document.all.pic.title="显示树形";
	     }
    }
    function searchNode()
	{	
		var ip = window.prompt("请输入需要搜索的设备IP地址", "在此输入设备IP地址");
		if (ip == null)
			return true;
		else if (ip == "在此输入设备IP地址")
			return;
	
		if (!checkIPAddress(ip))
			searchNode();
	
		var coor = getNodeCoor(ip);
		if (coor == null)
		{
			var msg = "没有在图中搜索到IP地址为 "+ ip +" 的设备。";
			window.alert(msg);
			return;
		}
		else if (typeof coor == "string")
		{
			window.alert(coor);
			return;
		}
	
		// 移动设备到中心标记处
		window.parent.mainFrame.moveMainLayer(coor);
	}
	// 刷新拓扑图
	function refreshFile() 
	{
		if (window.confirm("“刷新”前是否需要保存当前拓扑图？")) {
			savefile();
		}
		parent.mainFrame.location.reload();
	}
	var curTarget = "showMap.jsp?filename=<%=viewFile%>&fullscreen=<%=fullscreen%>";
	// 切换视图
	function changeName() 
	{
		// 之前提醒用户保存
		if (admin) {
			if (window.confirm("“切换视图”前是否需要保存当前拓扑图？")) {
				savefile();
			}
		}
		
		if (g_viewFlag == 0) {
			g_viewFlag = 1;
			window.parent.parent.leftFrame.location = "tree.jsp?treeflag=1";
			window.location = curTarget+"&viewflag=1";
		}
		else if (g_viewFlag == 1) {
			g_viewFlag = 0;
			window.parent.parent.leftFrame.location = "tree.jsp?treeflag=0";		
			window.location = curTarget+"&viewflag=0";
		}
		else {    
			window.alert("视图类型错误");
		}
	}
	//拓扑图属性
	function editMap(){
	    var url="<%=rootPath%>/submap.do?action=readyEditMap";
	    showModalDialog(url,window,'dialogwidth:500px; dialogheight:400px; status:no; help:no;resizable:0');
	}
	//创建实体链路
	function createEntityLink(){
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
	        alert("选中的两台设备已经存在示意链路!");
	        return;
	    }
	    var start_x_y=objEntityAry[0].style.left+","+objEntityAry[0].style.top;
	    var end_x_y=objEntityAry[1].style.left+","+objEntityAry[1].style.top;
	    var url="<%=rootPath%>/link.do?action=readyAddLine&xml="+xml+"&start_id="+start_id+"&end_id="+end_id+"&start_x_y="+start_x_y+"&end_x_y="+end_x_y;
	    showModalDialog(url,window,'dialogwidth:510px; dialogheight:350px; status:no; help:no;resizable:0');
	    //parent.mainFrame.location = "<%=rootPath%>/link.do?action=addDemoLink&xml="+xml+"&id1="+start_id+"&id2="+end_id;
	}
	
	//新增示意图元
	function createDemoObj(){
	    var url="<%=rootPath%>/submap.do?action=readyAddHintMeta&xml=<%=viewFile%>";
	    var returnValue = showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
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
	    var url="<%=rootPath%>/topology/network/linkAnalytics.jsp?start_id="
				+ start_id + "&end_id=" + end_id;
		showModalDialog(url, window,
				'dialogwidth:670px; dialogheight:370px; status:no; help:no;resizable:0');
	}
	// 显示视图控制器
	var controller = false; // 是否显示控制器
	function showControllerTool(flag) {
		var result;
		if (flag == false)
			controller = false;
		if (controller) {
			result = showController(controller);

			if (result == false) {
				window.alert("您没有选择视图，无控制器可用");
				return;
			}

			document.all.controller.title = "关闭显示框内的视图控制器";
			controller = false;
		} else {
			result = showController(controller);

			if (result == false) {
				window.alert("您没有选择视图，无控制器可用");
				return;
			}

			document.all.controller.title = "开启显示框内的视图控制器";
			controller = true;
		}
	}
	// 全屏观看
	function gotoFullScreen() {
		parent.mainFrame.resetProcDlg();
		var status = "toolbar=no,height=" + window.screen.height + ",";
		status += "width=" + (window.screen.width - 8) + ",scrollbars=no";
		status += "screenX=0,screenY=0";
		window.open("index.jsp?fullscreen=yes", "fullScreenWindow", status);
		parent.mainFrame.zoomProcDlg("out");
	}
</script>
<style>
a {
	text-decoration: none;
}

table.menu {
	font-size: 100%;
	position: absolute;
	visibility: hidden;
	background: #ECECEC;
	align: left;
	z-index: 1;
}
</style>


</html>
