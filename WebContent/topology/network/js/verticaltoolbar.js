    //window.parent.frames['mainFrame'].location.reload();//保存链路后刷新拓扑图
	var curTarget = "showMap.jsp?filename=<%=viewFile%>&fullscreen=<%=fullscreen%>";
	var display = false;	    // 是否显示快捷列表
	var controller = false;		// 是否显示控制器
	//转换目标(jsp)文件
	function updateState(target) {
		curTarget = target;

	}
function searchIPNODE()
{	
	var ip = document.getElementsByName("searchIPTxt")[0].value;
	//alert(ip);
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
/*function saveFile() {
	if (!admin) {
		window.alert("您没有保存视图的权限！");
		return;
	}
	parent.mainFrame.saveFile();
}*/
function savefile() {
	console.log('savefile');
	
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
		saveFile();
	}
	window.location.reload();
}

// 全屏观看
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
        alert("选中的两台设备已经存在示意链路!");
        return;
    }
    var start_x_y=objEntityAry[0].style.left+","+objEntityAry[0].style.top;
    var end_x_y=objEntityAry[1].style.left+","+objEntityAry[1].style.top;
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
	//parent.location = "../submap/submap.jsp?submapXml=" + document.all.submapview.value;
	window.parent.parent.location = "../submap/index.jsp?submapXml=" + document.all.submapview.value;
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