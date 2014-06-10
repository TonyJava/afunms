<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="java.io.File"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.topology.util.ReadFile"%>
<%@page import="com.afunms.config.dao.BusinessDao"%>
<%@page import="com.afunms.config.model.Business"%>

<%  
   String rootPath = request.getContextPath();  
   ManageXml vo = (ManageXml)request.getAttribute("vo");
   BusinessDao bussdao = new BusinessDao();
   List allbuss = bussdao.loadAll();
   String bid = vo.getBid();
   List bidlist = new ArrayList();
   if(bid!=null){
       String id[] = bid.split(",");
   
	   if(id != null &&id.length>0){
		for(int i=0;i<id.length;i++){
			bidlist.add(id[i]);
		}
	  }
   }
    
%>
<html>
<head>
<base target="_self">
<meta http-equiv="Pragma" content="no-cache">

<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/addTimeConfig.js" charset="gb2312"></script>
		<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/jquery-1.4.2.js" charset="gb2312"></script>
<script language="javascript">
function edit(){
    if(checkinput("topo_name","string","拓扑子图名称",30,false)&&
       checkinput("topo_title","string","拓扑子图标题",50,true)&&
       checkinput("utilhdx","number","链路流速对比值",10,false)&&
       checkinput("utilhdxperc","number","链路带宽对比值",5,false)){
        var args = window.dialogArguments;
        mainForm.action = "<%=rootPath%>/submap.do?action=editSubMap";
        mainForm.submit();
        window.close(); 
        args.location.reload();
    }   
}

</script>
</head>
<body id="body" class="body">
		<form id="mainForm" method="post" name="mainForm"  >
		<input type=hidden name="id" value="<%=vo.getId()%>">
<input type=hidden name="xml_name" value="<%=vo.getXmlName()%>">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-add">
									<table id="container-main-add" class="container-main-add">
										<tr>
											<td>
												<table id="add-content" class="add-content">
													<tr>
														<td>
															<table id="add-content-header" class="add-content-header">
																<tr>
																	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
																	<td class="add-content-title">拓扑图属性
																	</td>
																	<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td bgcolor="#FFFFFF">
															<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%" align="center">
					<TBODY>				
			            <tr>						
			                <TD  nowrap align="right" height="24" width="20%">拓扑子图名称&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="topo_name" size="30" class="formStyle" value="<%=vo.getTopoName()%>"><font color="red">&nbsp;*</font>(限20个中文字符)
			               </TD>
			            </tr>
			            <!--  
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">拓扑子图别名&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="alias_name" size="20" class="formStyle" value="<%=vo.getAliasName()%>"><font color="red">&nbsp;*</font>(限6个中文字符)
			               </TD>
			            </tr>
			            -->
			            <tr>						
			                <TD  nowrap align="right" height="24" width="20%">拓扑子图标题&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="topo_title" size="30" class="formStyle" value="<%=vo.getTopoTitle()%>">&nbsp;(限32个中文字符)
			               </TD>
			            </tr>
			           <tr>						
			                <TD  nowrap align="right" height="24" width="20%">&nbsp;链路流速对比值</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="utilhdx" size="30" class="formStyle" value="<%=vo.getUtilhdx()%>">&nbsp;
			               </TD>
			            </tr>
			            <tr>						
			                <TD  nowrap align="right" height="24" width="20%">&nbsp;链路带宽对比值</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="utilhdxperc" size="30" class="formStyle" value="<%=vo.getUtilhdxperc()%>">&nbsp;
			               </TD>
			            </tr>
			            <!--
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">地域&nbsp;</TD>				
			                <TD nowrap width="80%">
			                <%  
			                    ///String selected1 = "";
			                    //String selected2 = "";
			                    //if("1".equals(vo.getTopoArea())){
			                    //    selected1 = "selected"; 
			                    //    selected2 = ""; 
			                    //}
			                    //if("2".equals(vo.getTopoArea())){
			                    //    selected1 = ""; 
			                    //    selected2 = "selected"; 
			                    //}
			                %>
			                 &nbsp; <select name='topo_area' style='width:100px;'>
            								<option value='1' >北京</option>
            								<option value='2' >上海</option>
            					    </select>&nbsp;
			               </TD>
			            </tr>  -->
			            <tr>						
			                <TD  nowrap align="right" height="24" width="20%">背景图片&nbsp;</TD>				
			                <TD nowrap width="80%">
			                <%  String selected3 = "";
			                    if("0".equals(vo.getTopoBg())){
			                        selected3 = "selected"; 
			                    }
			                    if("Blue.jpg".equals(vo.getTopoBg())){
			                        selected3 = ""; 
			                    }
			                %>
			                 &nbsp; <select name='topo_bg' style='width:100px;'>
            								<option value='0' <%=selected3%>>无</option>
            								<%
	            							   ReadFile readFile = new ReadFile();
	            							   String  path=application.getRealPath(request.getRequestURI());
	                                           String  dir=new File(path).getParent(); 
	            							   List<String> fileList = readFile.readfile(dir.substring(0,dir.lastIndexOf("afunms"))+"\\resource\\image\\bg");
	            							   if(fileList.size()>0){
	            							       for(int i = 0;i<fileList.size();i++){
	            							       String selected = "";
	            							       if(fileList.get(i).equals(vo.getTopoBg()))selected="selected";
	            							 %>
	            								<option value='<%=fileList.get(i)%>' <%=selected%>><%=fileList.get(i)%></option>
	            							<% 
	            							       }
	            							    }
	            							%>
            					    </select>&nbsp;
			               </TD>
			            </tr>
			            <tr>						
			<TD  nowrap align="right" height="24" width="20%">所属业务&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
				<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none border=1>
      		    <tbody>  
      				<% if( allbuss.size()>0){
					for(int i=0;i<allbuss.size();i++){
					    int flag = 0;
						Business buss = (Business)allbuss.get(i);
						for(int j=0;j<allbuss.size();j++){
						    Business bvo = (Business)allbuss.get(j);
						    if(buss.getId().equals(bvo.getPid())){
						        flag++;
						    }
						}
						String checkflag = "";
						if(bidlist.contains(buss.getId()+""))checkflag="checked";
						if(flag == 0){
      									%>                  										                        								
  										<tr align="left"> 
  											<td height="28" align="left">&nbsp;<INPUT type="checkbox" class=noborder name=checkbox value="<%=buss.getId()%>" <%=checkflag%>>&nbsp;<%=buss.getName()%></td>
  										</tr>  
  										<%
  										    }
  											}
  										}
  										%>                  										                 										                      								
				</tbody>
				</table>
			</TD>																			
			</tr>			
			            <tr align="left"> 
 						    <td height="28" colspan="2">
 						    <table>
 						        <tr>
 						        <%  
			                         if(vo.getTopoType()==4){
			                         
			                    %>
 						            <td><INPUT type="radio" class=noborder name="topo_type" value="4" checked>作为子图创建(默认)</td>
 						        <% 
 						            } else if(vo.getTopoType()==0) {
 						            
 						        %>
 						            <td><INPUT type="radio" class=noborder name="topo_type" value="0" checked>物理根图</td>
 						        <%
 						            } else {
 						         //   if("2".equals(vo.getTopoType())){
 						         %> 
 						   
 						         <td><INPUT type="radio" class=noborder name="topo_type" value="1" checked>作为业务视图创建</td>
 						        <%
 						            }
 						         //   } else {
 						         %> 
 						           <!--     <td><INPUT type="radio" class=noborder name="topo_type" value="2" >作为示意拓扑图创建</td>
 						         <% 
 						         //    }
			                     //    if("3".equals(vo.getTopoType())){
 						        %>
 						             <td><INPUT type="radio" class=noborder name="topo_type" value="3" checked>作为缩略拓扑图创建</td>
 						        <%
 						         //   } else {
 						        %> 
 						             <td><INPUT type="radio" class=noborder name="topo_type" value="3" >作为缩略拓扑图创建</td>
 						         <%// } %>-->
 						        </tr>
 						    </table>
 						    </td>
 						</tr>
 						<tr align="left"> 
 						    <td height="28" colspan="2">
	 						    <table>
	 						        <% 
	 						         String check = "";
 						             if(vo.getHome_view()==1||vo.getBus_home_view()==1) {
 						                 check = "checked";
 						             } else {
 						                 check = "";
 						             }
 						             String selected1 = "";
 						             String selected2 = "";
 						             String selected = "";
 						             String selected4 = "";
 						             String selected5 = "";
 						             String selected6 = "";
 						             String selected7 = "";
 						             String selected8 = "";
 						             String selected9 = "";
 						             String selected10 = "";
 						             if(("0.1").equals(vo.getPercent()+"")) {
 						                 selected1 = "selected";
 						             } 
 						             if(("0.2").equals(vo.getPercent()+"")) {
 						                 selected2 = "selected";
 						             }
 						             if(("0.3").equals(vo.getPercent()+"")) {
 						                 selected = "selected";
 						             }
 						             if(("0.4").equals(vo.getPercent()+"")) {
 						                 selected4 = "selected";
 						             }
 						             if(("0.5").equals(vo.getPercent()+"")) {
 						                 selected5 = "selected";
 						             }
 						             if(("0.6").equals(vo.getPercent()+"")) {
 						                 selected6 = "selected";
 						             }
 						             if(("0.7").equals(vo.getPercent()+"")) {
 						                 selected7 = "selected";
 						             }
 						             if(("0.8").equals(vo.getPercent()+"")) {
 						                 selected8 = "selected";
 						             }
 						             if(("0.9").equals(vo.getPercent()+"")) {
 						                 selected9 = "selected";
 						             }
 						             if(("1").equals(vo.getPercent()+"")) {
 						                 selected10 = "selected";
 						             }
 						            %>
	 						        <tr>
	 						            <td><INPUT type="checkbox" class=noborder name="home_view" value="1" <%=check%>>首页显示</td>
	 						            <td nowrap>&nbsp;缩放比例:
								        <select name='zoom_percent' style='width:50px;'>
								            <option value='1' <%=selected10%>>1</option>
            								<option value='0.9' <%=selected9%>>0.9</option>
            								<option value='0.8' <%=selected8%>>0.8</option>
            								<option value='0.7' <%=selected7%>>0.7</option>
            								<option value='0.6' <%=selected6%>>0.6</option>
            								<option value='0.5' <%=selected5%>>0.5</option>
            								<option value='0.4' <%=selected4%>>0.4</option>
            								<option value='0.3' <%=selected%>>0.3</option>
            								<option value='0.2' <%=selected2%>>0.2</option>
            								<option value='0.1' <%=selected1%>>0.1</option>
            							</select>&nbsp;
            						</td> 
	 						        </tr>
	 						    </table>
 						    </td>
 						</tr>
						<tr>
							<TD nowrap colspan="2" align=center>
								<br>
								<input type="button" value="保存" style="width:50"  onclick="edit()">
								<input type="button" value="关闭" style="width:50"  onclick="window.close();">
							</TD>	
						</tr>
			         </TBODY>
				   </TABLE>			
														</td>
													</tr>
													<tr>
														<td>
															<table id="detail-content-footer" class="detail-content-footer">
																<tr>
																	<td>
																		<table width="100%" border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td align="left" valign="bottom">
																					<img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" />
																				</td>
																				<td></td>
																				<td align="right" valign="bottom">
																					<img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" />
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
	</body>

</html>