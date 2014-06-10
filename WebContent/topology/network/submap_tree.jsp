<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.dao.TreeNodeDao"%>
<%@page import="com.afunms.topology.model.TreeNode"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.config.dao.BusinessDao"%>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.indicators.util.NodeUtil"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%@page import="com.afunms.event.dao.CheckEventDao"%>
<%@page import="com.afunms.indicators.util.Constant"%>
<%@page import="com.afunms.system.dao.SystemConfigDao"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.topology.dao.HostNodeDao"%>
<%@page import="com.afunms.common.util.ShareData"%>
<%@page import="com.afunms.topology.dao.ManageXmlDao"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="com.afunms.topology.dao.NodeDependDao"%>
<%@page import="com.afunms.topology.model.NodeDepend"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.polling.base.Node"%>
<%@page import="com.afunms.topology.util.XmlOperator"%>
<%
	String rootPath = request.getContextPath();
	String treeFlag = request.getParameter("treeflag");
	if (treeFlag == null)
		treeFlag = "0";
	User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	String bids = "-1";
	if (current_user.getRole() != 0) {
		bids = current_user.getBusinessids();
	}
	//��ȡ����ҵ����ͼ�ڵ�
	ManageXmlDao manageXmlDao = new ManageXmlDao();
	List xmlList = new ArrayList();
	try {
		xmlList = manageXmlDao.loadSubXmlByPerAll(bids);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		manageXmlDao.close();
	}
%>
<html>
	<head>
		<title></title>
		<link rel="StyleSheet" href="../dtree/dtree.css" type="text/css" />
		<script type="text/javascript" src="../dtree/dtree.js"></script>
		<style>
body {
	margin-left: 6px;
	margin-top: 0px;
	margin-right: 6px;
	margin-bottom: 6px;
	scrollbar-face-color: #E0E3EB;
	scrollbar-highlight-color: #E0E3EB;
	scrollbar-shadow-color: #E0E3EB;
	scrollbar-3dlight-color: #E0E3EB;
	scrollbar-arrow-color: #7ED053;
	scrollbar-track-color: #ffffff;
	scrollbar-darkshadow-color: #9D9DA1;
}

body,td,th {
	color: #666666;
	line-height: 20px
}

.div_RightMenu {
	z-index: 30000;
	text-align: left;
	cursor: default;
	position: absolute;
	background-color: #FAFFF8;
	width: 100px;
	height: auto;
	border: 1px solid #333333;
	display: none;
	filter: progid : DXImageTransform . Microsoft .
		Shadow(Color = #333333, Direction = 120, strength = 5);
}

.divMenuItem {
	height: 17px;
	color: Black;
	font-family: ����;
	vertical-align: middle;
	font-size: 10pt;
	margin-bottom: 3px;
	cursor: hand;
	padding-left: 10px;
	padding-top: 2px;
}
</style>
	</head>
	<body
		style="background-image: url('/afunms/resource/image/global/bg6.jpg')">
		<div class="dtree" style="">
			<p>
				<a href="javascript: d.openAll();">չ��</a> |
				<a href="javascript: d.closeAll();">�ϱ�</a>
			</p>
			<script type="text/javascript">
        var currTreeNodeId = '';		// ��ǰ���Ľڵ� Id
        var treeNodeFatherId = '';		// ��ǰ���Ľڵ�ĸ� Id	
        var key = 0 ;
		d = new dTree('d');
		d.add(0,-1,' Ӧ��ϵͳ��');
		
		<%
			String treeshowflag_str = "0";
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			SystemConfigDao systemConfigDao = new SystemConfigDao();
			try{
				treeshowflag_str = systemConfigDao.getSystemCollectByVariablename("treeshowflag");
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				systemConfigDao.close();
			}
			boolean treeshowflag = false;
			if("1".equals(treeshowflag_str)){
				treeshowflag = true;			// ������ʾģʽ �Ժ� ���Ըĳ�  0��1�� 2������������ 
			}
			//String allBusinessPid = bids_str;	// ���о����ӽڵ�ҵ���id����
			ManageXml currBusiness = null;		// ��ǰҵ��
			ManageXml currBusiness1 = null;		// ��ǰҵ��
			String currbid = "";				// ��ǰҵ��id
			String currBusinessNodeId = "";		// ��ǰҵ��ڵ��Id
			List treeNodeList = null;			// ���нڵ��б�
			String currTreeNodeId = "";			// ��ǰ�ڵ�Id
			boolean isShowTreeNodeFlag = true;	// �Ƿ���ʾ�ýڵ� 	
			boolean rightFrameFlag = true;		// �ұ߿����ʾҳ���ģʽ ��ʱֻ��Ϊ true �Ժ��������ģʽ 
			String currTreeNodeFatherId = "";			// ��ǰ�ڵ㸸 Id
			try {
				if (xmlList != null && xmlList.size() > 0) {
					// ѭ��ÿһ��ҵ��ϵͳ
					try{
						if(xmlList == null || xmlList.size()==0){
							return;
						}
						L1: for(Object object : xmlList){
						    currTreeNodeFatherId = "0";
						    TreeNodeDao treeNodeDao = new TreeNodeDao();
							try{
								treeNodeList = treeNodeDao.loadAll(); // ��ȡ���еĽڵ�
							}catch(Exception e){
								e.printStackTrace();
							} finally {
								treeNodeDao.close();
							}
							NodeUtil nodeUtil = new NodeUtil();
							nodeUtil.setSetedMonitorFlag(true);
							nodeUtil.setMonitorFlag("1");
						    int treeNodeNum = 0;				// ���ڵ��� �ڼ���
						    try{
							    currBusiness = (ManageXml)object;
								currbid = currBusiness.getId()+"";
								if(currBusiness == null){
									continue L1;
								}
								currTreeNodeId = "business_" + currbid;
								currBusinessNodeId = currTreeNodeId;
							%>
							 	currTreeNodeId = '<%=currTreeNodeId%>';
			      				currTreeNodeFatherId = '<%=currTreeNodeFatherId%>';
							    var imagestr = "";
							    d.add(currTreeNodeId,currTreeNodeFatherId,'<%=" " + currBusiness.getTopoName()%>',"<%=rootPath + "/topology/submap/submap.jsp?fileName=" + currBusiness.getXmlName()%>","","","mainFrame",imagestr,imagestr);
							<%
								if(treeNodeList == null || treeNodeList.size() == 0){
									return;
								}
								// ����ǰ�ڵ�id ��ֵ�� ���ڵ�
								currTreeNodeFatherId = currTreeNodeId;
								XmlOperator xmlOpr = new XmlOperator();
								xmlOpr.setFile(currBusiness.getXmlName());
								xmlOpr.init4updateXml();
								List nodelist = xmlOpr.getAllNodes();
								if(nodelist == null || nodelist.size() == 0){
									return;
								}
								L2: for(Object treeNodeObject : treeNodeList){
								    try{
										// ѭ��ÿһ���豸���ڵ�   
										TreeNode currTreeNode = (TreeNode)treeNodeObject;
										List nodeDTOList = null;
										Node fnode = null;
										try {
											if(nodelist!=null&&nodelist.size()>0){
											    nodeDTOList = new ArrayList();
											    for(int j=0;j<nodelist.size();j++){
											        String nodeid = ((String) nodelist.get(j)).split(":")[0];
													String category = ((String) nodelist.get(j)).split(":")[1];
											        if(nodeid.substring(0, 3).equalsIgnoreCase(currTreeNode.getNodeTag())){
											    
											            fnode = PollingEngine.getInstance().getNodeByCategory(currTreeNode.getName(),Integer.parseInt(nodeid.substring(3)));
											            if(fnode == null)continue;
											            category = currTreeNode.getCategory();
											            int i=0;
											            if(category != null && !"".equals(category.trim())) {
															category = "," + category + ",";
															String[] categorys = category.split(",");
															if(categorys!=null && categorys.length > 0 ){
															    for (String category_per : categorys){
															        if(category_per != null && category_per.trim().length() > 0 && category_per.equals(fnode.getCategory()+"")){
															           i++;
															           break;
															        }
															    }
															}
														} else {
														    i++;
														}
														if(i>0){
														    NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(fnode);
										            		nodeDTOList.add(nodeDTO);
														}
											        }
											    }
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
								   		if(nodeDTOList == null){
								   			nodeDTOList = new ArrayList();
								   		}
								   		currTreeNode.setDeceiveNum(nodeDTOList.size()+"");
									 	isShowTreeNodeFlag = true;
									 	if("0".equals(currTreeNode.getDeceiveNum())){
									 		// ����豸��Ϊ 0 �� �� ��ʾģʽ�ĸ�ֵ��
									 		isShowTreeNodeFlag = treeshowflag;
										}
										// ����ǰ�ڵ㸳ֵ Ϊ �ø��ڵ� + "_" + �ýڵ�id;
										currTreeNodeId = currBusinessNodeId + "_" + currTreeNode.getId();
										currTreeNodeFatherId = currBusinessNodeId + "_" + currTreeNode.getFatherId();
											currTreeNodeFatherId = currBusinessNodeId;
										if(isShowTreeNodeFlag){
											%>
										 	currTreeNodeId = '<%=currTreeNodeId%>';
						      				currTreeNodeFatherId = '<%=currTreeNodeFatherId%>';
										    var imagestr = "<%=rootPath%>/performance/<%=NodeHelper.getTypeImage(currTreeNode.getName())%>";
										    d.add(currTreeNodeId,currTreeNodeFatherId,'<%=" " + currTreeNode.getText() + "(" + currTreeNode.getDeceiveNum() + ")"%>',"","","","mainFrame",imagestr,imagestr);
											
											<%
											treeNodeNum++;
											currTreeNodeFatherId = currTreeNodeId;
											if("1".equals(currTreeNode.getIsHaveChild())){       
									    	// �����κ���
									    	} else {
												L3:for(Object nodeDTOObject : nodeDTOList){
												    
										   			NodeDTO nodeDTO = (NodeDTO)nodeDTOObject;
										   			int alarmLevel = 0;
										   			String chexkname = nodeDTO.getId()+":"+nodeDTO.getType()+":"+nodeDTO.getSubtype()+":";
													for(Iterator it = checkEventHashtable.keySet().iterator();it.hasNext();){ 
												        String key = (String)it.next(); 
												        if(key.startsWith(chexkname)){
												        	if(alarmLevel < (Integer) checkEventHashtable.get(key)){
												        		alarmLevel = (Integer) checkEventHashtable.get(key); 
												        	}
												        }
													}
										   			currTreeNodeId = currTreeNode.getNodeTag() + "_" + nodeDTO.getId();
										   			
										   			String imagestr = rootPath + "/resource/" + NodeHelper.getCurrentStatusImage(alarmLevel);
										   			%>
												 	currTreeNodeId = '<%=currTreeNodeId%>';
								      				currTreeNodeFatherId = '<%=currTreeNodeFatherId%>';
												    var imagestr = "<%=imagestr%>";
												    d.add(currTreeNodeId,currTreeNodeFatherId,'<%=treeFlag.equals("0") ? nodeDTO.getName().trim() : nodeDTO.getIpaddress()%>',"<%=rootPath + "/detail/dispatcher.jsp?flag=1&id=" + currTreeNode.getNodeTag() + nodeDTO.getId()%>",'<%=currTreeNode.getNodeTag() + nodeDTO.getId()%>',"<%=currTreeNode.getNodeTag() + nodeDTO.getId() + ";" + currTreeNode.getName()%>","mainFrame",imagestr,imagestr);
													<%
										   		} // ��� ÿһ���豸 ѭ�� (L3)
										   	}
										}
									}catch(Exception e){
								        e.printStackTrace();
								    }
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}	// ���ÿһ���豸���ڵ�ѭ�� (L2)
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		%>
		document.write(d);
		

//------------search one device-------------����ѡ�е����ڵ��ڵ�ͼ��������Ӧ���豸

function SearchNode(ip)
{
	//var coor = window.parent.mainFrame.mainFrame.getNodeCoor(ip);
	//if (coor == null)
	//{
	//	var msg = "û����ͼ��������IDΪ "+ ip +" �Ľڵ㡣";
	//	window.alert(msg);
	//	return;
	//}
	//else if (typeof coor == "string")
	//{
	//	window.alert(coor);
	//	return;
	//}
	//window.parent.mainFrame.mainFrame.moveMainLayer(coor);
}
//--------------------end--------------------
//--------------------beginѡ���豸��ʾ�Ҽ��˵�--------------------
var nodeid="";
var nodeip="";
var nodecategory="";
function showMenu(id,ip){
    /*
    nodeid = id.split(";")[0];
    nodecategory = id.split(";")[1];
    nodeip = ip;
    
    if(document.getElementById("div_RightMenu") == null)
    {    
        CreateMenu();
        document.oncontextmenu = ShowMenu
        document.body.onclick  = HideMenu    
    }
    else
    {
        document.oncontextmenu = ShowMenu
        document.body.onclick  = HideMenu    
    } 
    */
}
function add(){
    var nodeId = nodeid;//Ҫ��֤nodeid�ĳ��ȴ���3
    var coor = window.parent.mainFrame.mainFrame.getNodeId(nodeId);
    window.alert(coor);
    if (coor == null)
	{
         window.parent.mainFrame.mainFrame.addEquip(nodeId,nodecategory);
	}
	else if (typeof coor == "string")
	{
		window.alert(coor);
		return;
	}
    window.parent.mainFrame.mainFrame.moveMainLayer(coor);
    window.alert("���豸�Ѿ�������ͼ�д��ڣ�");
}
function detail(){
    showalert(nodeid);
	window.parent.parent.opener.focus();
}
function showalert(id) {
	window.parent.parent.opener.parent.window.document.getElementById('mainFrame').src="/afunms/detail/dispatcher.jsp?id="+id+"&fromtopo=true";
}
function evtMenuOnmouseMove()
{
    this.style.backgroundColor='#8AAD77';
    this.style.paddingLeft='10px';    
}
function evtOnMouseOut()
{
    this.style.backgroundColor='#FAFFF8';
}
function CreateMenu()
{    
        var div_Menu          = document.createElement("Div");
        div_Menu.id           = "div_RightMenu";
        div_Menu.className    = "div_RightMenu";
        
        var div_Menu1         = document.createElement("Div");
        div_Menu1.id          = "div_Menu1";
        div_Menu1.className   = "divMenuItem";
        div_Menu1.onclick     = add;
        div_Menu1.onmousemove = evtMenuOnmouseMove;
        div_Menu1.onmouseout  = evtOnMouseOut;
        div_Menu1.innerHTML   = "��ӵ�����ͼ";
        var div_Menu2         = document.createElement("Div");
        div_Menu2.id          = "div_Menu2";
        div_Menu2.className   = "divMenuItem";
        div_Menu2.onclick     = detail;
        div_Menu2.onmousemove = evtMenuOnmouseMove;
        div_Menu2.onmouseout  = evtOnMouseOut;
        div_Menu2.innerHTML   = "��ϸ��Ϣ";
        
        div_Menu.appendChild(div_Menu1);
        div_Menu.appendChild(div_Menu2);
        document.body.appendChild(div_Menu);
}
// �жϿͻ��������
function IsIE() 
{
    if (navigator.appName=="Microsoft Internet Explorer") 
    {
        return true;
    } 
    else 
    {
        return false;
    }
}

function ShowMenu()
{
    
    if (IsIE())
    {
        document.body.onclick  = HideMenu;
        var redge=document.body.clientWidth-event.clientX;
        var bedge=document.body.clientHeight-event.clientY;
        var menu = document.getElementById("div_RightMenu");
        if (redge<menu.offsetWidth)
        {
            menu.style.left=document.body.scrollLeft + event.clientX-menu.offsetWidth
        }
        else
        {
            menu.style.left=document.body.scrollLeft + event.clientX
            //�����иĶ�
            menu.style.display = "block";
        }
        if (bedge<menu.offsetHeight)
        {
            menu.style.top=document.body.scrollTop + event.clientY - menu.offsetHeight
        }
        else
        {
            menu.style.top = document.body.scrollTop + event.clientY
            menu.style.display = "block";
        }
    }
    return false;
}
function HideMenu()
{
    if (IsIE())  document.getElementById("div_RightMenu").style.display="none";    
}
</script>
		</div>
	</body>
</html>