    //window.parent.frames['mainFrame'].location.reload();//������·��ˢ������ͼ
	var curTarget = "showMap.jsp?filename=<%=viewFile%>&fullscreen=<%=fullscreen%>";
	var display = false;	    // �Ƿ���ʾ����б�
	var controller = false;		// �Ƿ���ʾ������
	//ת��Ŀ��(jsp)�ļ�
	function updateState(target) {
		curTarget = target;

	}
function searchIPNODE()
{	
	var ip = document.getElementsByName("searchIPTxt")[0].value;
	//alert(ip);
	if (ip == null)
		return true;
	else if (ip == "�ڴ������豸IP��ַ")
		return;

	if (!checkIPAddress(ip))
		searchNode();

	var coor = window.parent.mainFrame.getNodeCoor(ip);
	if (coor == null)
	{
		var msg = "û����ͼ��������IP��ַΪ "+ ip +" ���豸��";
		window.alert(msg);
		return;
	}
	else if (typeof coor == "string")
	{
		window.alert(coor);
		return;
	}

	// �ƶ��豸�����ı�Ǵ�
	window.parent.mainFrame.moveMainLayer(coor);
}
function searchNode()
{	
	var ip = window.prompt("��������Ҫ�������豸IP��ַ", "�ڴ������豸IP��ַ");
	if (ip == null)
		return true;
	else if (ip == "�ڴ������豸IP��ַ")
		return;

	if (!checkIPAddress(ip))
		searchNode();

	var coor = window.parent.mainFrame.getNodeCoor(ip);
	if (coor == null)
	{
		var msg = "û����ͼ��������IP��ַΪ "+ ip +" ���豸��";
		window.alert(msg);
		return;
	}
	else if (typeof coor == "string")
	{
		window.alert(coor);
		return;
	}

	// �ƶ��豸�����ı�Ǵ�
	window.parent.mainFrame.moveMainLayer(coor);
}

// ��������ͼ
/*function saveFile() {
	if (!admin) {
		window.alert("��û�б�����ͼ��Ȩ�ޣ�");
		return;
	}
	parent.mainFrame.saveFile();
}*/
function savefile() {
	console.log('savefile');
	
	if (!admin) {
		window.alert("��û�б�����ͼ��Ȩ�ޣ�");
		return;
	}
	parent.mainFrame.saveFile();
}




// ˢ������ͼ
function refreshFile() 
{
	if (window.confirm("��ˢ�¡�ǰ�Ƿ���Ҫ���浱ǰ����ͼ��")) {
		saveFile();
	}
	window.location.reload();
}

// ȫ���ۿ�
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("index.jsp?fullscreen=yes", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
}


//����ʵ����·
function createEntityLink(){
	alert('createEntityLink');
    var objLinkAry = new Array();
    var xml = "<%=viewFile%>";
    if(window.parent.frames['mainFrame'].objMoveAry!=null&&window.parent.frames['mainFrame'].objMoveAry.length>0){//��ѡ
        objLinkAry = window.parent.frames['mainFrame'].objMoveAry;
    }
    if(window.parent.frames['mainFrame'].objEntityAry!=null&&window.parent.frames['mainFrame'].objEntityAry.length>0){//ctrlѡ
        objLinkAry = window.parent.frames['mainFrame'].objEntityAry;
    }
    if(objLinkAry==null||objLinkAry.length!=2){
        alert("��ѡ�������豸lllllllllllllll��");
        return;
    }
    if(objLinkAry[0].name.substring(objLinkAry[0].name.lastIndexOf(",")+1)=="1"){
        alert("��ѡ���ʾ���豸!");
        return;
    }
    var start_id = objLinkAry[0].id.replace("node_","");
    
    if(objLinkAry[1].name.substring(objLinkAry[1].name.lastIndexOf(",")+1)=="1"){
        alert("��ѡ���ʾ���豸!");
        return;
    }
    var end_id = objLinkAry[1].id.replace("node_","");     
    
    if(start_id.indexOf("net")==-1||end_id.indexOf("net")==-1){
        alert("��ѡ�������豸!");
        return;
    }
    var url="<%=rootPath%>/link.do?action=addLink&start_id="+start_id+"&end_id="+end_id+"&xml="+xml;
    showModalDialog(url,window,'dialogwidth:500px; dialogheight:400px; status:no; help:no;resizable:0');
}
//������ͼ
function createSubMap(){
    var objEntityAry = new Array();
    if(window.parent.frames['mainFrame'].objMoveAry!=null&&window.parent.frames['mainFrame'].objMoveAry.length>0){//��ѡ
        objEntityAry = window.parent.frames['mainFrame'].objMoveAry;
    }
    if(window.parent.frames['mainFrame'].objEntityAry!=null&&window.parent.frames['mainFrame'].objEntityAry.length>0){//ctrlѡ
        objEntityAry = window.parent.frames['mainFrame'].objEntityAry;
    }
    var lineArr = window.parent.frames['mainFrame'].lineMoveAry; 
    var asslineArr = window.parent.frames['mainFrame'].assLineMoveAry; 
    var objEntityStr = "";//�ڵ���Ϣ
    var linkStr = "";//��·��Ϣ
    var asslinkStr = "";//��·��Ϣ
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
//����ʾ����·
function createDemoLink(){
    var objEntityAry = new Array();
    if(window.parent.frames['mainFrame'].objMoveAry!=null&&window.parent.frames['mainFrame'].objMoveAry.length>0){//��ѡ
        objEntityAry = window.parent.frames['mainFrame'].objMoveAry;
    }
    if(window.parent.frames['mainFrame'].objEntityAry!=null&&window.parent.frames['mainFrame'].objEntityAry.length>0){//ctrlѡ
        objEntityAry = window.parent.frames['mainFrame'].objEntityAry;
    } 
    if(objEntityAry==null||objEntityAry.length!=2){
        alert("��ѡ�������豸��");
        return;
    }
    
    var start_id = objEntityAry[0].id.replace("node_","");
    var end_id = objEntityAry[1].id.replace("node_","");
    var xml = "<%=viewFile%>";
    var lineArr = window.parent.frames['mainFrame'].demoLineMoveAry;
    if(lineArr!=null&&lineArr.length>0){
        alert("ѡ�е���̨�豸�Ѿ�����ʾ����·!");
        return;
    }
    var start_x_y=objEntityAry[0].style.left+","+objEntityAry[0].style.top;
    var end_x_y=objEntityAry[1].style.left+","+objEntityAry[1].style.top;
    //alert(start_x_y+"="+end_x_y);
    var url="<%=rootPath%>/link.do?action=readyAddLine&xml="+xml+"&start_id="+start_id+"&end_id="+end_id+"&start_x_y="+start_x_y+"&end_x_y="+end_x_y;
    showModalDialog(url,window,'dialogwidth:510px; dialogheight:350px; status:no; help:no;resizable:0');
    //parent.mainFrame.location = "<%=rootPath%>/link.do?action=addDemoLink&xml="+xml+"&id1="+start_id+"&id2="+end_id;
    //alert("��·�����ɹ���");
    //parent.mainFrame.location.reload();
}

//����ʾ��ͼԪ
function createDemoObj(){
    //window.parent.mainFrame.ShowHide("1",null);��ק��ʽ
    var url="<%=rootPath%>/submap.do?action=readyAddHintMeta&xml=<%=viewFile%>";
    var returnValue = showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
    //parent.mainFrame.location.reload();
}
//�ؽ�����ͼ
function rebuild(){
    if (window.confirm("ע��ò��������¹�������ͼ���ݣ�ԭ����ͼ���ݻᶪʧ����������")) {
		window.location = "<%=rootPath%>/submap.do?action=reBuild&xml=<%=viewFile%>";
		alert("�����ɹ�!");
        parent.location.reload();
	}
}

//��������ͼ
function backup(){
    var url="<%=rootPath%>/submap.do?action=readybackup&xml=<%=viewFile%>";
    showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
}
//�ָ�����ͼ
function resume(){
    var url="<%=rootPath%>/submap.do?action=readyresume&xml=<%=viewFile%>";
    showModalDialog(url,window,'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');
}
function checkEntityLink(){
    var objLinkAry = new Array();
    var xml = "<%=viewFile%>";
    if(window.parent.frames['mainFrame'].objMoveAry!=null&&window.parent.frames['mainFrame'].objMoveAry.length>0){//��ѡ
        objLinkAry = window.parent.frames['mainFrame'].objMoveAry;
    }
    if(window.parent.frames['mainFrame'].objEntityAry!=null&&window.parent.frames['mainFrame'].objEntityAry.length>0){//ctrlѡ
        objLinkAry = window.parent.frames['mainFrame'].objEntityAry;
    }
    if(objLinkAry==null||objLinkAry.length!=2){
        alert("��ѡ�������豸��");
        return;
    }
    if(objLinkAry[0].name.substring(objLinkAry[0].name.lastIndexOf(",")+1)=="1"){
        alert("��ѡ���ʾ���豸!");
        return;
    }
    var start_id = objLinkAry[0].id.replace("node_","");
    
    if(objLinkAry[1].name.substring(objLinkAry[1].name.lastIndexOf(",")+1)=="1"){
        alert("��ѡ���ʾ���豸!");
        return;
    }
    var end_id = objLinkAry[1].id.replace("node_","");     
    
    if(start_id.indexOf("net")==-1||end_id.indexOf("net")==-1){
        alert("��ѡ�������豸!");
        return;
    }
    var url="<%=rootPath%>/topology/network/linkAnalytics.jsp?start_id="+start_id+"&end_id="+end_id;
    showModalDialog(url,window,'dialogwidth:670px; dialogheight:370px; status:no; help:no;resizable:0');
}
// �л���ͼ
function changeName() 
{
	// ֮ǰ�����û�����
	if (admin) {
		if (window.confirm("���л���ͼ��ǰ�Ƿ���Ҫ���浱ǰ����ͼ��")) {
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
		window.alert("��ͼ���ʹ���");
	}
}

// ��ʾ��ͼ������
function showController(flag) {

	var result;
	if (flag == false)
		controller = false;
	if (controller) {
		result = parent.mainFrame.showController(controller);
		
		if (result == false) {
			window.alert("��û��ѡ����ͼ���޿���������");
			return;
		}
			
		//document.all.controller.value = "�رտ�����";
		document.all.controller.title = "�ر���ʾ���ڵ���ͼ������";
		controller = false;
	}
	else {
		result = parent.mainFrame.showController(controller);
		
		if (result == false) {
			window.alert("��û��ѡ����ͼ���޿���������");
			return;
		}

		//document.all.controller.value = "����������";
		document.all.controller.title = "������ʾ���ڵ���ͼ������";
		controller = true;
	}
}
	function autoRefresh() 
	{
		window.clearInterval(freshTimer);
		freshTimer = window.setInterval("refreshFile()",60000);
	}

// ����ͼƬ
function swapImage(imageID, imageSrc) {
	document.all(imageID).src = imageSrc;
}
//ѡ����ͼ
function changeView()
{
	if(document.all.submapview.value == "")return;
	//parent.location = "../submap/submap.jsp?submapXml=" + document.all.submapview.value;
	window.parent.parent.location = "../submap/index.jsp?submapXml=" + document.all.submapview.value;
}
//����ͼ����
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
        document.all.pic.title="��������";
     }
     
     else
     {
        parent.parent.search.cols='0,*';
        document.all.pic.src ="<%=rootPath%>/resource/image/show_menu.gif";
        document.all.pic.title="��ʾ����";
     }
  }