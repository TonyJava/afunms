<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.config.dao.IpaddressPanelDao"%>
<%@page import="com.afunms.config.model.IpaddressPanel"%>
<%@page import="com.afunms.config.dao.PanelModelDao"%>
<%@page import="com.afunms.config.model.PanelModel"%>
<%@page import="com.afunms.polling.node.Host"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="java.util.ArrayList"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
  	List iplist = (List)request.getAttribute("iplist");
  	String menuTable = (String)request.getAttribute("menuTable");
  	
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/resource/css/examples.css" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/resource/css/chooser.css" />
		<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
		<script type="text/javascript" 	src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all-debug.js"></script>
		
		<script language="javascript">	
  
		  function doQuery()
		  {  
		     if(mainForm.key.value=="")
		     {
		     	alert("请输入查询条件");
		     	return false;
		     }
		     mainForm.action = "<%=rootPath%>/network.do?action=find";
		     mainForm.submit();
		  }
		  
		  function doChange()
		  {
		     if(mainForm.view_type.value==1)
		        window.location = "<%=rootPath%>/topology/network/index.jsp";
		     else
		        window.location = "<%=rootPath%>/topology/network/port.jsp";
		  }
		
		  function toAdd()
		  {
		      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
		      mainForm.submit();
		  }
		  
		// 全屏观看
		function gotoFullScreen() {
			parent.mainFrame.resetProcDlg();
			var status = "toolbar=no,height="+ window.screen.height + ",";
			status += "width=" + (window.screen.width-8) + ",scrollbars=no";
			status += "screenX=0,screenY=0";
			window.open("topology/network/index.jsp", "fullScreenWindow", status);
			parent.mainFrame.zoomProcDlg("out");
		}
		
		</script>
		<SCRIPT language=javascript>
		 var isIE = /msie/i.test(navigator.userAgent) && !window.opera;         
			  function fileChange(target) {     
			      
			    var fileSize = 0;          
			    if (isIE && !target.files) {      
			      var filePath = target.value;      
			      var fileSystem = new ActiveXObject("Scripting.FileSystemObject");         
			     var file = fileSystem.GetFile (filePath);      
			      fileSize = file.Size;     
			    } else {     
			     fileSize = target.files[0].size;      
			     }    
			     var size = fileSize / 1024;     
			     if(size>1000){   
			    
			      alert("不能大于1M");
			       document.getElementById("ok").disabled=true; 
			       
			        
			    }else{
			     document.getElementById("ok").disabled=false; 
			    }
			       
			}  
			function checkfile(){
			  var upfilename=document.uploadform.upload.value;
			  var ipaddress=document.uploadform.ipaddress.value;
			  if(upfilename==""){
			   alert("你还没有选择你要插入的图片!");
			   return false;
			  }
			  var filetype=upfilename.substring(upfilename.indexOf(".")+1,upfilename.length).toLowerCase();
			  var fileName=upfilename.substring(0,upfilename.indexOf("."));
			  if(filetype!="jpg"&&filetype!="gif"&&filetype!="bmp"&&filetype!="png" &&filetype!="jpeg" &&filetype!="dxf")
			  {
			     alert("只支持jpg,gif,bmp,png,jpeg,dxf这几种图片格式,!!!");
			     return false;
			  }
			  window.uploadform.action="<%=rootPath%>/panel.do?action=upload&ipaddress="+ipaddress;
			  window.uploadform.submit(); 
			
			}
			
			function setfilename() {
			   var flag=document.getElementById("yrn").value;
			   var path=document.getElementById("path").value;
			   if(flag=="true"){
			   		var obj=window.parent.frames['ifRTC'].RTC;
			   		mid(flag,path,obj);
			   		
			   }
			   initmenu();
			}
			
			function mid(flag,path,obj){
				var flag1=flag;
				var path1=path;
				window.parent.img2(flag1,path1,obj);
			}
			
			function tr(){
				//window.parent.document.getElementById("pathimg").value=window.parent.frames['ifRTC'].RTC;
				//window.parent.frames['frame2'].myFocus=window.parent.frames['ifRTC'].RTC;
			}
		</SCRIPT>
		<script language="JavaScript" type="text/JavaScript">
		var show = true;
		var hide = false;
		//修改菜单的上下箭头符号
		function my_on(head,body)
		{
			var tag_a;
			for(var i=0;i<head.childNodes.length;i++)
			{
				if (head.childNodes[i].nodeName=="A")
				{
					tag_a=head.childNodes[i];
					break;
				}
			}
			tag_a.className="on";
		}
		function my_off(head,body)
		{
			var tag_a;
			for(var i=0;i<head.childNodes.length;i++)
			{
				if (head.childNodes[i].nodeName=="A")
				{
					tag_a=head.childNodes[i];
					break;
				}
			}
			tag_a.className="off";
		}
		//添加菜单	
		function initmenu()
		{
			var idpattern=new RegExp("^menu");
			var menupattern=new RegExp("child$");
			var tds = document.getElementsByTagName("div");
			for(var i=0,j=tds.length;i<j;i++){
				var td = tds[i];
				if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
					menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
					menu.init();		
				}
			}
		
		}
		
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<%
			String flag=request.getParameter("retflag");
			String str2=request.getParameter("virtualname");
		%>
		<FORM name="uploadform" method=post id="uploadform" enctype="multipart/form-data">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>	
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 资源>> 设备面板配置管理 >> 设备面板编辑 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
		        									<tr height="30px">
		        										<td>
		        											&nbsp;&nbsp;路径&nbsp;&nbsp;<input type="file" name="upload" contentEditable="false" onclick="tr()" style="height: 20px;"  onchange="fileChange(this);"> 
															<input type="hidden" id="yrn" name="yrn" value=<%=flag %>>
															<input type="hidden" id="path" name="path" value=<%=str2 %>>
															<input type="button" name="ok" id="ok" value="插入图片 " onClick="checkfile()" class="formStyle">
														</td>
													</tr>
													<tr height="20px">
														<td>
															&nbsp;&nbsp;OID&nbsp;&nbsp;
															<select name="ipaddress">
															<%
																if(iplist != null && iplist.size()>0){
																	for(int m=0;m<iplist.size();m++){
																		HostNode node = (HostNode)iplist.get(m);
															%>
																<option value="<%=node.getIpAddress()%>"><%=node.getSysOid()%>(<%=node.getIpAddress()%>)</option>
															<%
																	}
																}
															%>
															</select>						
		        										</td>
		        									</tr>
		        								</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-footer" class="content-footer">
		        									<tr>
		        										<td>
		        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
									                  			<tr>
									                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
									                    			<td></td>
									                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
