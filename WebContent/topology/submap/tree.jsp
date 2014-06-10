<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.polling.node.Host"%>
<%@page import="com.afunms.polling.base.Node"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.dao.TreeNodeDao"%>
<%@page import="com.afunms.topology.model.TreeNode"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.system.manage.UserManager"%>
<%@page import="com.afunms.application.dao.OraclePartsDao"%>
<%@page import="com.afunms.application.model.OracleEntity"%>
<%@page import="com.afunms.polling.node.DBNode"%>
<%@page import="com.afunms.application.dao.DBTypeDao"%>
<%@page import="com.afunms.application.model.DBTypeVo"%>
<%@page import="com.afunms.config.dao.BusinessDao"%>
<%@page import="com.afunms.config.model.Business"%>
<%    
    String rootPath = request.getContextPath(); 
    StringBuffer url=request.getRequestURL();
  //System.out.println("url====="+url.substring(0,url.lastIndexOf("afunms")));
    String urlPath = url.substring(0,url.lastIndexOf("afunms"));
    String treeFlag = request.getParameter("treeflag");
    if(treeFlag==null) treeFlag = "0";
    String typeStr = (String)request.getAttribute("typeStr");
    if(typeStr==null)typeStr="设备树";
    TreeNodeDao treeNodeDao = new TreeNodeDao();
    List poList = treeNodeDao.findByFatherId("0"); // 得到所有父节点
  //System.out.println("mapList==============="+mapList.size());
    User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
    int midsize = 0;
    int servicesize = 0;
    UserManager userManager = new UserManager();
    midsize = userManager.getMiddleService(current_user);
    servicesize = userManager.getServiceNum(current_user);
    DBTypeDao typedao = new DBTypeDao();
    DBTypeVo oracleType=(DBTypeVo) typedao.findByDbtype("oracle");
    typedao.close();
//
	List list = null;
	BusinessDao dao = new BusinessDao();
	try {
		list = dao.loadAll();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		dao.close();
	}
	String bids_str = "";
	for(int i=0;i<list.size();i++) {
	    Business business = (Business)list.get(i);
	    bids_str = bids_str + business.getPid()+",";
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
body,td,th {color: #666666;line-height:20px}
.div_RightMenu
{
    z-index:30000;
    text-align:left;    
    cursor: default;
    position: absolute;
    background-color:#FAFFF8;
    width:100px;
    height:auto;
    border: 1px solid #333333;
    display:none;
    filter:progid:DXImageTransform.Microsoft.Shadow(Color=#333333,Direction=120,strength=5);    
}
.divMenuItem
{
    height:17px;
    color:Black;
    font-family:宋体;
    vertical-align:middle;
    font-size:10pt;
    margin-bottom:3px;
    cursor:hand;
    padding-left:10px;
    padding-top:2px;
}
</style>
</head>
<body>
<div class="dtree">
<p><a href="javascript: d.openAll();">展开</a> | <a href="javascript: d.closeAll();">合闭</a></p>
<script type="text/javascript">
        var key = 0;
        var ffid = 0;
        var fid = 0;
        var fids = 0;
		d = new dTree('d');
		d.add(0,-1,' 设备资源树');
		<%
		    if(list!=null&&list.size()>0){
		        for(int x=0;x<list.size();x++){
		            Business business = (Business)list.get(x);
		            if(!bids_str.contains(business.getId())){
		 %>
		    key++;
		    ffid = key;   
		    var imagestr = "";
		    d.add(key,0,'<%=" "+business.getName()%>',"","","","rightFrame",imagestr,imagestr);
		<%           
		                int totalCount = poList.size();
						if (totalCount > 0) {
							for (int i = 0; i < totalCount; i++) {
								TreeNode po = (TreeNode) poList.get(i);
								List fnodes = PollingEngine.getInstance().getAllTypeMap().get(po.getName());
								if(fnodes!=null&&fnodes.size()>0){
								    if(po.getName().indexOf("net")==0){//网络设备的情况下
						                int num = 0;
										if(po.getCategory()!=null&&!"".equals(po.getCategory())){
											String cates[]=po.getCategory().split(",");
											if(cates.length>0){
											    for(int k=0;k<fnodes.size();k++){
											        Host fnode = (Host) fnodes.get(k);
													for(int l=0;l<cates.length;l++){
														if(fnode.getCategory()==Integer.parseInt(cates[l])&&fnode.getBid().contains(business.getId())){
														    num++;
														}
													}
											    }
											    if(num>0){
					%>
					    key++;
					    fid = key;   
					    var imagestr = "<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(po.getName())%>";
					    d.add(key,ffid,'<%=" "+po.getText()+"("+num+")"%>',"","","","",imagestr,imagestr);
					<%
												}
												for(int k=0;k<fnodes.size();k++){
													Host fnode = (Host) fnodes.get(k);
													for(int l=0;l<cates.length;l++){
														if(fnode.getCategory()==Integer.parseInt(cates[l])&&fnode.getBid().contains(business.getId())){
															String types = "";
															if(fnode.getSysOid()!=null){
															    types = fnode.getSysOid();
															} else {
															    types = cates[l];
															}
					%>
					    key++;
		    			d.add(key,fid,'<%=treeFlag.equals("0")?fnode.getAlias().trim():fnode.getIpAddress()%>',"",'<%=fnode.getIpAddress()%>',"<%="net"+fnode.getId()+";"+po.getName()%>","","<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(fnode.getSysOid())%>");
					<%
														}
														}	
													}
												}
											}
										} else {
										    if(fnodes.size()>0){
					%>
					    key++;
					    fid = key;
					    var imagestr = "<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(po.getName())%>";
					    d.add(key,ffid,'<%=" "+po.getText()+"("+fnodes.size()+")"%>',"","","","",imagestr,imagestr);
					<%
					                        }
					                        if("dbs".equals(po.getName())){
						                        for(int k=0;k<fnodes.size();k++){
										DBNode fnode = (DBNode) fnodes.get(k);
										
					%>
					    key++;
		                d.add(key,fid,'<%=treeFlag.equals("0")?fnode.getAlias().trim():fnode.getIpAddress()%>',"",'<%=fnode.getIpAddress()%>',"<%=po.getNodeTag()+fnode.getId()+";"+po.getName()%>","","<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(fnode.getCategory()+"")%>");
					<%
											    }
					                        } else {
					                            for(int k=0;k<fnodes.size();k++){
												    Node fnode = (Node) fnodes.get(k);
												    if(fnode.getBid().contains(business.getId())){
					%>
					    key++;
		                d.add(key,fid,'<%=treeFlag.equals("0")?fnode.getAlias().trim():fnode.getIpAddress()%>',"",'<%=fnode.getIpAddress()%>',"<%=po.getNodeTag()+fnode.getId()+";"+po.getName()%>","","<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(fnode.getStatus())%>");
					<%
											        }
											    }
					                        }
										}
					%>
					
					<%
								} else if("middleware".equals(po.getName())&&midsize>0){
					%>
					    key++;
					    fid = key;
					    var imagestr = "<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(po.getName())%>";
		                d.add(key,ffid,'<%=" "+po.getText()+"("+midsize+")"%>',"","","","",imagestr,imagestr);
					<%
								} else if("services".equals(po.getName())&&servicesize>0){
					%>
					    key++;
					    fid = key;
					    var imagestr = "<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(po.getName())%>";
		                d.add(key,ffid,'<%=" "+po.getText()+"("+servicesize+")"%>',"","","","",imagestr,imagestr);
					<%
								}else {
					%>
					    //key++;
					    //fid = key;
		                //var imagestr = "<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(po.getName())%>";
					    //d.add(key,ffid,'<%=" "+po.getText()+"(0)"%>',"<%=rootPath+po.getUrl()+"&category="+po.getName()%>","","","rightFrame",imagestr,imagestr);
					<%
					            }
								List subList = treeNodeDao.findByFatherId(po.getId() + "");// 得到该父节点的所有子节点
								if (subList.size() > 0) {
									for (int j = 0; j < subList.size(); j++) {
										TreeNode subpo = (TreeNode) subList.get(j);
										List nodes = PollingEngine.getInstance().getAllTypeMap().get(subpo.getName());
					                    if(nodes!=null&&nodes.size()>0){
											if(subpo.getName().indexOf("net")==0){//网络设备的情况下
											    int num = 0;
												if(subpo.getCategory()!=null&&!"".equals(subpo.getCategory())){
													String cate[]=subpo.getCategory().split(",");
													if(cate.length>0){
														for(int k=0;k<nodes.size();k++){
													        Host node = (Host) nodes.get(k);
															for(int l=0;l<cate.length;l++){
																if(node.getCategory()==Integer.parseInt(cate[l])&&node.getBid().contains(business.getId())){
																    num++;
																}
															}
													    }
													    if(num>0){
					%>
					    key++;
					    fids = key;
					    var imagestr = "<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(subpo.getName())%>";
					    d.add(key,fid,'<%=" "+subpo.getText()+"("+num+")"%>',"","","","",imagestr,imagestr);
					<%
														}
														for(int k=0;k<nodes.size();k++){
															Host node = (Host) nodes.get(k);
															for(int l=0;l<cate.length;l++){
																if(node.getCategory()==Integer.parseInt(cate[l])&&node.getBid().contains(business.getId())){
																	String types = "";
																	if(node.getSysOid()!=null){
																	    types = node.getSysOid();
																	} else {
																	    types = cate[l]+"";
																	}
					 %>
					     key++;
		     d.add(key,fids,'<%=treeFlag.equals("0")?node.getAlias().trim():node.getIpAddress()%>',"",'<%=node.getIpAddress()%>',"<%="net"+node.getId()+";"+subpo.getName()%>","","<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(node.getSysOid())%>");                                         
					 <%
																}
															}	
														}
													}
												}
											} else {
											    if(nodes.size()>0){
					
					%>
					    key++;
					    fids = key;
					    var imagestr = "<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(subpo.getName())%>";
					    d.add(key,fid,'<%=" "+subpo.getText()+"("+nodes.size()+")"%>',"","","","",imagestr,imagestr);   
					 <%
						                        }
						                        if("dbs".equals(subpo.getName())){
							                        for(int k=0;k<nodes.size();k++){
														DBNode node = (DBNode) nodes.get(k);
														if(node.getBid().contains(business.getId())){
														
														
						%>
						    key++;
						    d.add(key,fids,'<%=treeFlag.equals("0")?node.getAlias().trim():node.getIpAddress()%>',"",'<%=node.getIpAddress()%>',"<%=subpo.getNodeTag()+node.getId()+";"+subpo.getName()%>","rightFrame","<%=rootPath%>/topology/<%=NodeHelper.getTypeImage(node.getCategory()+"")%>");
						<%
												        }
												    }
						                        } else if("socket".equals(subpo.getName())){
						                            for(int k=0;k<nodes.size();k++){
													    Node node = (Node) nodes.get(k);
													  //  System.out.println("======================"+node);
													    if(node.getBid() != null && node.getBid().contains(business.getId())){
						%>
						    key++;
						    d.add(key,fids,'<%=treeFlag.equals("0")?node.getAlias().trim():node.getIpAddress()%>',"",'<%=node.getIpAddress()%>',"<%=subpo.getNodeTag()+node.getId()+";"+subpo.getName()%>","rightFrame","<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(node.getStatus())%>");
						<%
												        }
												    }
						                        } else if("jboss".equals(subpo.getName())){
						                            for(int k=0;k<nodes.size();k++){
													    Node node = (Node) nodes.get(k);
													    if(node.getBid() != null && node.getBid().contains(business.getId())){
						%>
						    key++;
						    d.add(key,fids,'<%=treeFlag.equals("0")?node.getAlias().trim():node.getIpAddress()%>',"",'<%=node.getIpAddress()%>',"<%=subpo.getNodeTag()+node.getId()+";"+subpo.getName()%>","rightFrame","<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(node.getStatus())%>");
						<%
												        }
												    }
						                        } else if("apache".equals(subpo.getName())){
						                            for(int k=0;k<nodes.size();k++){
													    Node node = (Node) nodes.get(k);
													    if(node.getBid() != null && node.getBid().contains(business.getId())){
						%>
						    key++;
						    d.add(key,fids,'<%=treeFlag.equals("0")?node.getAlias().trim():node.getIpAddress()%>',"",'<%=node.getIpAddress()%>',"<%=subpo.getNodeTag()+node.getId()+";"+subpo.getName()%>","rightFrame","<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(node.getStatus())%>");
						<%
												        }
												    }
						                        } else if("was".equals(subpo.getName())){
						                            for(int k=0;k<nodes.size();k++){
													    Node node = (Node) nodes.get(k);
													    if(node.getBid() != null && node.getBid().contains(business.getId())){
						%>
						    key++;
						    d.add(key,fids,'<%=treeFlag.equals("0")?node.getAlias().trim():node.getIpAddress()%>',"",'<%=node.getIpAddress()%>',"<%=subpo.getNodeTag()+node.getId()+";"+subpo.getName()%>","rightFrame","<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(node.getStatus())%>");
						<%
												        }
												    }
						                        } else {
						                            for(int k=0;k<nodes.size();k++){
													    Node node = (Node) nodes.get(k);
													    if(node.getBid() != null && node.getBid().contains(business.getId())){
						%>
						    key++;
		    d.add(key,fids,'<%=treeFlag.equals("0")?node.getAlias().trim():node.getIpAddress()%>',"",'<%=node.getIpAddress()%>',"<%=subpo.getNodeTag()+node.getId()+";"+subpo.getName()%>","","<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(node.getStatus())%>");
						<%
												        }
												    }
						                        }
											}
										}
									}
								}
							}
						}
		            }
		        }
		        treeNodeDao.close();
		    }
		%>
		document.write(d);
		

//------------search one device-------------根据选中的树节点在地图上搜索对应的设备

function SearchNode(ip)
{
	var coor = window.parent.mainFrame.mainFrame.getNodeCoor(ip);
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
	window.parent.mainFrame.mainFrame.moveMainLayer(coor);
}
//--------------------end--------------------
//--------------------begin选中设备显示右键菜单--------------------
var nodeid="";
var nodeip="";
var nodecategory="";
function showMenu(id,ip){
    nodeid = id.split(";")[0];
    nodecategory = id.split(";")[1];
    nodeip = ip;
    /**/
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

}
function add(){
    var nodeId = nodeid;//要保证nodeid的长度大于3
    var coor = window.parent.mainFrame.mainFrame.getNodeId(nodeId);
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
    window.alert("该设备已经在拓扑图中存在！");
}
function detail(){
    showalert(nodeid);
	window.parent.parent.opener.focus();
}
function showalert(id) {
	//window.parent.parent.opener.location="/afunms/detail/dispatcher.jsp?id="+id;
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
        div_Menu1.innerHTML   = "添加到拓扑图";
        var div_Menu2         = document.createElement("Div");
        div_Menu2.id          = "div_Menu2";
        div_Menu2.className   = "divMenuItem";
        div_Menu2.onclick     = detail;
        div_Menu2.onmousemove = evtMenuOnmouseMove;
        div_Menu2.onmouseout  = evtOnMouseOut;
        div_Menu2.innerHTML   = "详细信息";
        
        div_Menu.appendChild(div_Menu1);
        div_Menu.appendChild(div_Menu2);
        document.body.appendChild(div_Menu);
}
// 判断客户端浏览器
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
            //这里有改动
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