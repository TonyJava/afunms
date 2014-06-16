<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.dao.ManageXmlDao"%>
<%@page import="com.afunms.topology.model.ManageXml"%>   
<%@page import="com.afunms.system.model.User"%>     
<html xmlns="http://www.w3.org/1999/xhtml">   
<%   
   String rootPath = request.getContextPath();    
   User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
   //System.out.println(current_user.getBusinessids());
   String bids[] = current_user.getBusinessids().split(",");
   String viewFile = (String)session.getAttribute(SessionConstant.CURRENT_TOPO_VIEW);   
   ManageXmlDao dao = new ManageXmlDao();
   ManageXml xmlvo = (ManageXml)dao.findByXml(viewFile);
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

function showToolBar(){
    //alert(document.all.checkbox.checked);
    var list = window.parent.mainFrame.rp_list;
  	 if(document.getElementsByName("checkbox")[0].checked){
        list.style.marginLeft="-100px";
    } else {
        list.style.marginLeft="-190px";
    }
    //alert(list.style);
}
</script>
</head>
<body topmargin="0" leftmargin="0" marginheight="0" marginwidth="0" bgcolor="#CEDFF6">
<form name="topviewForm">
<table width="100%" height="35" border="0" cellspacing="0" cellpadding="0" align="center" style="padding:0px;border-top:#CEDFF6 1px solid;border-left:#CEDFF6 1px solid;border-right:#CEDFF6 1px solid;border-bottom:#D6D5D9 1px solid;background-color:#F5F5F5;">
  <tr>    
    <td>
	<table width="100%" height="35" border="0" cellpadding="0" cellspacing="0" background="<%=rootPath%>/common/images/menubg.jpg" align="center">
	  <tr>
	    <!--<td width="32" onMouseUp="this.className='up'" onMouseDown="this.className='down'" onMouseOver="this.className='up'" onMouseOut="this.className='m'" onClick=cwin()>
	    <img id=pic height=32 src="<%=rootPath%>/resource/image/hide_menu.gif" title="显示树形"></td>
		<td width="32"><input type="button" name="search" class="button_search_out" onmouseover="javascript:buttonSearchOver();" onmouseout="javascript:buttonSearchOut();" onclick="javascript:searchNode();" title="搜索"/></td>
		<td width="32"><input type="button" name="save" class="button_save_out" onmouseover="javascript:buttonSaveOver();" onmouseout="javascript:buttonSaveOut();" onclick="javascript:saveFile();" title="保存当前拓扑图数据"/></td>
		<td width="32"><input type="button" name="refresh" class="button_refresh_out" onmouseover="javascript:buttonRefreshOver();" onmouseout="javascript:buttonRefreshOut();" onclick="javascript:refreshFile();" title="刷新当前拓扑图数据"/></td>
		<td width="32"><input type="button" name="view" class="button_view_out" onmouseover="javascript:buttonViewOver();" onmouseout="javascript:buttonViewOut();" onclick="javascript:changeName();" title="改变设备名显示信息"/></td>
		<td width="32"><input type="button" name="editmap" class="button_editmap_out" onmouseover="javascript:buttonEditMapOver();" onmouseout="javascript:buttonEditMapOut();" onclick="javascript:editMap();" title="拓扑图属性"/></td>
		<td width="32"><input type="button" name="create1" class="button_create1_out" onmouseover="javascript:buttonCreate1Over();" onmouseout="javascript:buttonCreate1Out();" onclick="javascript:createEntityLink();" title="创建实体链路"/></td>
	    <td width="32"><input type="button" name="create2" class="button_create2_out" onmouseover="javascript:buttonCreate2Over();" onmouseout="javascript:buttonCreate2Out();" onclick="javascript:createDemoLink();" title="创建示意链路"/></td>
		<td width="32"><input type="button" name="create3" class="button_create3_out" onmouseover="javascript:buttonCreate3Over();" onmouseout="javascript:buttonCreate3Out();" onclick="javascript:createDemoObj();" title="创建示意图元"/></td>
		<td width="32"><input type="button" name="create4" class="button_create4_out" onmouseover="javascript:buttonCreate4Over();" onmouseout="javascript:buttonCreate4Out();" onclick="javascript:createSubMap();" title="创建子图"/></td>
		<td width="32"><input type="button" name="create5" class="button_create5_out" onmouseover="javascript:buttonCreate5Over();" onmouseout="javascript:buttonCreate5Out();" onclick="javascript:rebuild();" title="重建拓扑图"/></td>
		<td width="32"><input type="button" name="create6" class="button_create6_out" onmouseover="javascript:buttonCreate6Over();" onmouseout="javascript:buttonCreate6Out();" onclick="javascript:backup();" title="备份拓扑图"/></td>
		<td width="32"><input type="button" name="create7" class="button_create7_out" onmouseover="javascript:buttonCreate7Over();" onmouseout="javascript:buttonCreate7Out();" onclick="javascript:resume();" title="恢复拓扑图"/></td>
		<td width="32"><input type="button" name="create8" class="button_create1_out" onmouseover="javascript:buttonCreate1Over();" onmouseout="javascript:buttonCreate1Out();" onclick="javascript:checkEntityLink();" title="链路同步"/></td>
		<td width="32">
	<%//if (fullscreen.equals("0")) {%>
		<input type="button" name="fullscreen" class="button_fullscreen_out" onmouseover="javascript:buttonFullscreenOver();" onmouseout="javascript:buttonFullscreenOut();" onclick="javascript:gotoFullScreen();" title="全屏观看视图"/>
	<%//} else {%>
		<input type="button" name="fullscreen" class="button_fullscreen_out" onmouseover="javascript:buttonFullscreenOver();" onmouseout="javascript:buttonFullscreenOut();" onclick="javascript:window.parent.close();" value="关闭" title="关闭当前窗口"/>
	<%//}%>
		</td>
		<td width="32"><input type="button" name="controller" class="button_controller_out" onmouseover="javascript:buttonControllerOver();" onmouseout="javascript:buttonControllerOut();" onclick="javascript:showController();" title="关闭显示框内的视图控制器"/></td>-->
		<td width="100" height="32"><input type="checkbox" name="checkbox" onclick="javascript:showToolBar();" title="显示工具栏"/>显示工具栏</td>
		<td width="200" height="32"><input type="text" name="searchIPTxt" width="50px" value="请输入IP地址" onfocus="if(this.value == '请输入IP地址'){this.value='';this.className='txt txt_focus';}" onblur="if(this.value == ''){this.value='请输入IP地址';this.className='txt';}"/><input type="button" name="searchIP" onclick="javascript:searchIPNODE();" value="搜索"/></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td width="200"><strong><%=xmlvo.getTopoName()%></strong></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td width="100">
			<select width="320" name="submapview" onchange="changeView()">
			
			<option value="">--选择视图--</option>
<%
	dao = new ManageXmlDao();
	List list = dao.loadAll();
	for(int i=0; i<list.size(); i++)
	{
		ManageXml vo = (ManageXml)list.get(i);
		int tag = 0;
		//System.out.println("vo.getBid()======"+vo.getBid());
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
		//System.out.println("tag======"+tag);
		if(tag>0&&!"network.jsp".equals(vo.getXmlName())){
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
