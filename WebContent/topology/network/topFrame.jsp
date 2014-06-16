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
	
	// �ж�ȫ����ʾ״̬
	String fullscreen = request.getParameter("fullscreen");	
	
	if (fullscreen.equals("0")) 
	   fullscreen = "0";
	else 
	{
		fullscreen = "1";
		out.println("viewWidth = 0;");
	}
	// ȡ���û�Ȩ��---�������Ʊ��桢ˢ�¡��༭�Ȳ���
	boolean admin = false;
	String user = "admin";

	if (user.equalsIgnoreCase("admin") || user.equalsIgnoreCase("superuser")) {
		out.println("var admin = true;"); //Ϊ�ˣ����༭����������ʹ��
		admin = true;
	}
	else {
		out.println("var admin = false;");	
		admin = false;
	}
	out.println("</script>");
	
	String disable = "";//���ư�ť�Ƿ񼤻�
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
	    <img id=pic height=32 src="<%=rootPath%>/resource/image/hide_menu.gif" title="��ʾ����"></td>
		<td width="32"><input type="button" name="search" class="button_search_out" onmouseover="javascript:buttonSearchOver();" onmouseout="javascript:buttonSearchOut();" onclick="javascript:searchNode();" title="����"/></td>
		<td width="32"><input type="button" name="save" class="button_save_out" onmouseover="javascript:buttonSaveOver();" onmouseout="javascript:buttonSaveOut();" onclick="javascript:saveFile();" title="���浱ǰ����ͼ����"/></td>
		<td width="32"><input type="button" name="refresh" class="button_refresh_out" onmouseover="javascript:buttonRefreshOver();" onmouseout="javascript:buttonRefreshOut();" onclick="javascript:refreshFile();" title="ˢ�µ�ǰ����ͼ����"/></td>
		<td width="32"><input type="button" name="view" class="button_view_out" onmouseover="javascript:buttonViewOver();" onmouseout="javascript:buttonViewOut();" onclick="javascript:changeName();" title="�ı��豸����ʾ��Ϣ"/></td>
		<td width="32"><input type="button" name="editmap" class="button_editmap_out" onmouseover="javascript:buttonEditMapOver();" onmouseout="javascript:buttonEditMapOut();" onclick="javascript:editMap();" title="����ͼ����"/></td>
		<td width="32"><input type="button" name="create1" class="button_create1_out" onmouseover="javascript:buttonCreate1Over();" onmouseout="javascript:buttonCreate1Out();" onclick="javascript:createEntityLink();" title="����ʵ����·"/></td>
	    <td width="32"><input type="button" name="create2" class="button_create2_out" onmouseover="javascript:buttonCreate2Over();" onmouseout="javascript:buttonCreate2Out();" onclick="javascript:createDemoLink();" title="����ʾ����·"/></td>
		<td width="32"><input type="button" name="create3" class="button_create3_out" onmouseover="javascript:buttonCreate3Over();" onmouseout="javascript:buttonCreate3Out();" onclick="javascript:createDemoObj();" title="����ʾ��ͼԪ"/></td>
		<td width="32"><input type="button" name="create4" class="button_create4_out" onmouseover="javascript:buttonCreate4Over();" onmouseout="javascript:buttonCreate4Out();" onclick="javascript:createSubMap();" title="������ͼ"/></td>
		<td width="32"><input type="button" name="create5" class="button_create5_out" onmouseover="javascript:buttonCreate5Over();" onmouseout="javascript:buttonCreate5Out();" onclick="javascript:rebuild();" title="�ؽ�����ͼ"/></td>
		<td width="32"><input type="button" name="create6" class="button_create6_out" onmouseover="javascript:buttonCreate6Over();" onmouseout="javascript:buttonCreate6Out();" onclick="javascript:backup();" title="��������ͼ"/></td>
		<td width="32"><input type="button" name="create7" class="button_create7_out" onmouseover="javascript:buttonCreate7Over();" onmouseout="javascript:buttonCreate7Out();" onclick="javascript:resume();" title="�ָ�����ͼ"/></td>
		<td width="32"><input type="button" name="create8" class="button_create1_out" onmouseover="javascript:buttonCreate1Over();" onmouseout="javascript:buttonCreate1Out();" onclick="javascript:checkEntityLink();" title="��·ͬ��"/></td>
		<td width="32">
	<%//if (fullscreen.equals("0")) {%>
		<input type="button" name="fullscreen" class="button_fullscreen_out" onmouseover="javascript:buttonFullscreenOver();" onmouseout="javascript:buttonFullscreenOut();" onclick="javascript:gotoFullScreen();" title="ȫ���ۿ���ͼ"/>
	<%//} else {%>
		<input type="button" name="fullscreen" class="button_fullscreen_out" onmouseover="javascript:buttonFullscreenOver();" onmouseout="javascript:buttonFullscreenOut();" onclick="javascript:window.parent.close();" value="�ر�" title="�رյ�ǰ����"/>
	<%//}%>
		</td>
		<td width="32"><input type="button" name="controller" class="button_controller_out" onmouseover="javascript:buttonControllerOver();" onmouseout="javascript:buttonControllerOut();" onclick="javascript:showController();" title="�ر���ʾ���ڵ���ͼ������"/></td>-->
		<td width="100" height="32"><input type="checkbox" name="checkbox" onclick="javascript:showToolBar();" title="��ʾ������"/>��ʾ������</td>
		<td width="200" height="32"><input type="text" name="searchIPTxt" width="50px" value="������IP��ַ" onfocus="if(this.value == '������IP��ַ'){this.value='';this.className='txt txt_focus';}" onblur="if(this.value == ''){this.value='������IP��ַ';this.className='txt';}"/><input type="button" name="searchIP" onclick="javascript:searchIPNODE();" value="����"/></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td width="200"><strong><%=xmlvo.getTopoName()%></strong></td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td width="100">
			<select width="320" name="submapview" onchange="changeView()">
			
			<option value="">--ѡ����ͼ--</option>
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
