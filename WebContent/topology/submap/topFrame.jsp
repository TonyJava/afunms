<%@page language="java" contentType="text/html;charset=GB2312"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="com.afunms.topology.dao.ManageXmlDao"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%
   String rootPath = request.getContextPath();
   User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
   //System.out.println(current_user.getBusinessids());
   String bids[] = current_user.getBusinessids().split(",");
   String fileName = (String)request.getParameter("fileName");
   if(fileName==null){fileName = (String)session.getAttribute(SessionConstant.CURRENT_SUBMAP_VIEW);}//
   ManageXmlDao dao = new ManageXmlDao();
   ManageXml xmlvo = (ManageXml)dao.findByXml(fileName);
   dao.close();
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<link href="<%=rootPath%>/resource/css/topo_style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="<%=rootPath%>/resource/css/top.css" type="text/css">
<title>topFrame</title>
<script type="text/javascript" src="js/profile.js"></script>
<script type="text/javascript" src="js/toolbar.js"></script>
<script type="text/javascript" src="js/global.js"></script>
<script type="text/javascript" src="js/disable.js"></script>
<script type="text/javascript" src="js/edit.js"></script>
<script type="text/javascript" src="js/customview.js"></script>
<%
	out.println("<script type=\"text/javascript\">");
	// 判断全屏显示状态
	String fullscreen = request.getParameter("fullscreen");	
	if (fullscreen.equals("0")) 
	   fullscreen = "0";
	else 
	{
		fullscreen = "1";
		out.println("viewWidth = 0;");
	}

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
<script type="text/javascript">
    window.parent.frames['mainFrame'].location.reload();//保存链路后刷新拓扑图
	var curTarget = "showMap.jsp?fullscreen=<%=fullscreen%>";
	var display = false;	    // 是否显示快捷列表
	var controller = false;		// 是否显示控制器
	
function searchNode()
{	
	var ip = window.prompt("请输入需要搜索的设备IP地址", "在此输入设备IP地址");
	if (ip == null)
		return true;
	else if (ip == "在此输入设备IP地址")
		return;

	if (!checkIPAddress(ip))
		searchNode();

	var coor = window.parent.mainFrame.getNodeCoor(ip);
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

// 保存拓扑图
function saveFile() {
	if (!admin) {
		window.alert("您没有保存视图的权限！");
		return;
	}
	parent.mainFrame.saveFile();
}

// 刷新拓扑图
function refreshFile() 
{
	if (window.confirm("“刷新”前是否需要保存当前拓扑图？")) {
		parent.topFrame.saveFile();
	}
	parent.mainFrame.location.reload();
}

// 全屏观看
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("index.jsp?submapXml=<%=fileName%>&fullscreen=yes&user=<%=user%>", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
}

// 切换视图
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
//创建实体链路
function createEntityLink(){
    var objLinkAry = new Array();
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
    var xml = "<%=fileName%>";
    var url="<%=rootPath%>/submap.do?action=readyAddLink&start_id="+start_id+"&end_id="+end_id+"&xml="+xml;
    showModalDialog(url,window,'dialogwidth:500px; dialogheight:400px; status:no; help:no;resizable:0');
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
    var xml = "<%=fileName%>";
    var lineArr = window.parent.frames['mainFrame'].demoLineMoveAry;
    if(lineArr!=null&&lineArr.length>0){
        alert("选中的两台设备已经存在示意链路!");
        return;
    }
    var start_x_y=objEntityAry[0].style.left+","+objEntityAry[0].style.top;
    var end_x_y=objEntityAry[1].style.left+","+objEntityAry[1].style.top;
    //alert(start_x_y+"="+end_x_y);
    var url="<%=rootPath%>/link.do?action=readyAddLine&xml="+xml+"&start_id="+start_id+"&end_id="+end_id+"&start_x_y="+start_x_y+"&end_x_y="+end_x_y;
    showModalDialog(url,window,'dialogwidth:510px; dialogheight:350px; status:no; help:no;resizable:0');
    //parent.mainFrame.location = "<%=rootPath%>/submap.do?action=addLines&xml="+xml+"&id1="+start_id+"&id2="+end_id;
    //alert("链路创建成功！");
    //parent.mainFrame.location.reload();
}
//拓扑图属性
function editSubMap(){
    if("<%=fileName%>"=="testData.jsp"){
        alert("请选择子图!");
    } else {
        var url="<%=rootPath%>/submap.do?action=readyEditSubMap&xml=<%=fileName%>";
        showModalDialog(url,window,'dialogwidth:500px; dialogheight:400px; status:no; help:no;resizable:0');
    }
    
}
function checkEntityLink(){
    var objLinkAry = new Array();
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
//删除拓扑图
function deleteSubMap(){
    if (window.confirm("确定删除该拓扑图吗？")) {
		window.location = "<%=rootPath%>/submap.do?action=deleteSubMap&xml=<%=fileName%>";
		alert("操作成功!");
		window.parent.parent.location = "../network/index.jsp";
	}
}
//重建拓扑图
function rebuild(){
    if (window.confirm("该操作会重新构建当前拓扑图链路，还继续吗？")) {
		window.location = "<%=rootPath%>/submap.do?action=reBuildSubMap&xml=<%=fileName%>";
		alert("操作成功!");
        parent.location.reload();
	}
}
//备份拓扑图
function backup(){
    var url="<%=rootPath%>/submap.do?action=readybackup&xml=<%=fileName%>";
    showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
}
//恢复拓扑图
function resume(){
    var url="<%=rootPath%>/submap.do?action=readyresume&xml=<%=fileName%>";
    showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
}
// 显示视图控制器
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

// 交换图片
function swapImage(imageID, imageSrc) {
	document.all(imageID).src = imageSrc;
}

//选择视图
function changeView()
{
	if(document.all.submapview.value == "")return;
	if(document.all.submapview.value == "network.jsp"){
	    window.parent.parent.location = "../network/index.jsp";
	}else{
	    window.parent.parent.location = "../submap/index.jsp?submapXml=" + document.all.submapview.value;
	}
	
	//
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
//新增示意图元
function createDemoObj(){
    //window.parent.mainFrame.ShowHide("1",null);拖拽方式
    
    var url="<%=rootPath%>/submap.do?action=readyAddHintMeta&xml=<%=fileName%>";
    var returnValue = showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
    //parent.mainFrame.location.reload();
}
function showToolBar(){
    //alert(document.all.checkbox.checked);
    var list = window.parent.mainFrame.rp_alarm_table;
    if(document.all.checkbox.checked){
        list.style.marginLeft="90px";
    } else {
        list.style.marginLeft="50px";
    }
    //alert(list.style);
}
</script>
</head>
<body topmargin="0" leftmargin="0" marginheight="0" marginwidth="0" bgcolor="#CEDFF6">
<form name="topviewForm">
<table width="100%" height="35" border="0" cellspacing="0" cellpadding="0" style="padding:0px;border-top:#CEDFF6 0px solid;border-left:#CEDFF6 0px solid;border-right:#CEDFF6 0px solid;border-bottom:#D6D5D9 0px solid;background-color:#F5F5F5;">
  <tr>
    <td>
	<table width="100%" height="35" border="0" cellpadding="0" cellspacing="0" background="<%=rootPath%>/common/images/menubg.jpg">
	  <tr>
	    <!--
	    <td align="left" width="32" onMouseUp="this.className='up'" onMouseDown="this.className='down'" onMouseOver="this.className='up'" onMouseOut="this.className='m'" onClick=cwin()>
	    <img id=pic height=32 src="<%=rootPath%>/resource/image/hide_menu.gif"></td>
		<td width="32"><input type="button" name="search" class="button_search_out" onmouseover="javascript:buttonSearchOver();" onmouseout="javascript:buttonSearchOut();" onclick="javascript:searchNode();" title="搜索" <%=disable%>/></td>
		<td width="32"><input type="button" name="save" class="button_save_out" onmouseover="javascript:buttonSaveOver();" onmouseout="javascript:buttonSaveOut();" onclick="javascript:saveFile();" title="保存当前拓扑图数据" <%=disable%>/></td>
		<td width="32"><input type="button" name="refresh" class="button_refresh_out" onmouseover="javascript:buttonRefreshOver();" onmouseout="javascript:buttonRefreshOut();" onclick="javascript:refreshFile();" title="刷新当前拓扑图数据"/></td>
		<td width="32"><input type="button" name="view" class="button_view_out" onmouseover="javascript:buttonViewOver();" onmouseout="javascript:buttonViewOut();" onclick="javascript:changeName();" title="改变设备名显示信息"/></td>
		<td width="32"><input type="button" name="editmap" class="button_editmap_out" onmouseover="javascript:buttonEditMapOver();" onmouseout="javascript:buttonEditMapOut();" onclick="javascript:editSubMap();" title="拓扑图属性"/></td>
		<td width="32"><input type="button" name="delmap" class="button_delmap_out" onmouseover="javascript:buttonDelMapOver();" onmouseout="javascript:buttonDelMapOut();" onclick="javascript:deleteSubMap();" title="删除拓扑图"/></td>
		<td width="32"><input type="button" name="create1" class="button_create1_out" onmouseover="javascript:buttonCreate1Over();" onmouseout="javascript:buttonCreate1Out();" onclick="javascript:createEntityLink();" title="创建实体链路"/></td>
		<td width="32"><input type="button" name="create2" class="button_create2_out" onmouseover="javascript:buttonCreate2Over();" onmouseout="javascript:buttonCreate2Out();" onclick="javascript:createDemoLink();" title="创建示意链路"/></td>
		<td width="32"><input type="button" name="create3" class="button_create3_out" onmouseover="javascript:buttonCreate3Over();" onmouseout="javascript:buttonCreate3Out();" onclick="javascript:createDemoObj();" title="创建示意图元"/></td>
		<td width="32"><input type="button" name="create5" class="button_create5_out" onmouseover="javascript:buttonCreate5Over();" onmouseout="javascript:buttonCreate5Out();" onclick="javascript:rebuild();" title="重建拓扑图"/></td>
		<td width="32"><input type="button" name="create6" class="button_create6_out" onmouseover="javascript:buttonCreate6Over();" onmouseout="javascript:buttonCreate6Out();" onclick="javascript:backup();" title="备份拓扑图"/></td>
		<td width="32"><input type="button" name="create7" class="button_create7_out" onmouseover="javascript:buttonCreate7Over();" onmouseout="javascript:buttonCreate7Out();" onclick="javascript:resume();" title="恢复拓扑图"/></td>
		<td width="32"><input type="button" name="create8" class="button_create1_out" onmouseover="javascript:buttonCreate1Over();" onmouseout="javascript:buttonCreate1Out();" onclick="javascript:checkEntityLink();" title="链路同步"/></td>
		<td width="32">
	<%//if (fullscreen.equals("0")) {%>
		<input type="button" name="fullscreen" class="button_fullscreen_out" onmouseover="javascript:buttonFullscreenOver();" onmouseout="javascript:buttonFullscreenOut();" onclick="javascript:gotoFullScreen();" title="全屏观看视图"/>
	<%//}else {%>
		<input type="button" name="fullscreen" class="button_fullscreen_out" onmouseover="javascript:buttonFullscreenOver();" onmouseout="javascript:buttonFullscreenOut();" onclick="javascript:window.parent.close();" title="关闭当前窗口"/>
	<%//}%>
		</td>-->
		<td width="100"><input type="checkbox" name="checkbox" onclick="javascript:showToolBar();" title="显示工具栏"/>显示工具栏</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td width="200"><strong><%=xmlvo.getTopoName()%></strong></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td width="100">
			<select name="submapview" onchange="changeView()">
			<option value="">--选择视图--</option>
<%
	dao = new ManageXmlDao();
	List list = dao.loadAll();
	for(int i=0; i<list.size(); i++)
	{
		ManageXml vo = (ManageXml)list.get(i);
		int tag = 0;
		if(bids!=null&&bids.length>0){
		    for(int j=0;j<bids.length;j++){
		        if(vo.getBid()!=null&&!"".equals(vo.getBid())&&!"".equals(bids[j])&&vo.getBid().indexOf(bids[j])!=-1){
		            tag++;
		        }
		    }
		}
		if(current_user.getRole()==0){
		    tag++;
		}
		if(tag>0&&!fileName.equals(vo.getXmlName())){
		    out.print("<option value='" + vo.getXmlName()+ "'>" + vo.getTopoName()+ "</option>");
		}
	}	
%>
			</select>
		</td>
	  </tr>
	</table>
	</td>
  </tr>
</table>
</form>
</body>
</html>
