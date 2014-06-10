<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.polling.base.LinkRoad"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.topology.dao.LinkDao"%>
<%@page import="com.afunms.topology.model.Link"%>
<%
    String rootPath = request.getContextPath();
   String line = request.getParameter("line");
   int id = 0;
   if(line != null && !"".equals(line)) {
     	System.out.println("---------------"+line);
	 	id = Integer.parseInt(line);
   }
   //System.out.println("id==="+id);
   LinkRoad link = PollingEngine.getInstance().getLinkByID(id);
   if(link == null) {
	   System.out.println("is null");
	   link = new LinkRoad();
	   LinkDao linkDao = new LinkDao();
	   List<Link> list = linkDao.loadAll();
	   if(list != null && list.size() > 0) {
		   Link temp = list.get(0);
		   link.setEndId(temp.getEndId());
		   link.setStartId(temp.getStartId());
		   link.setEndIndex(temp.getEndIndex());
		   link.setStartIndex(temp.getStartIndex());
		   link.setEndIp(temp.getEndIp());
		   link.setStartIp(temp.getStartIp());
		   link.setLinkName(temp.getLinkName());
		   link.setEndDescr(temp.getEndDescr());
		   link.setStartDescr(temp.getStartDescr());
	   }
   }
   Host host1 = (Host)PollingEngine.getInstance().getNodeByID(link.getStartId());
   Host host2 = (Host)PollingEngine.getInstance().getNodeByID(link.getEndId());
   //if(host1 == null)System.out.println("host1 is null");
   //if(host2 == null)System.out.println("host2 is null");
   IfEntity if1 = host1.getIfEntityByIndex(link.getStartIndex());
   IfEntity if2 = host2.getIfEntityByIndex(link.getEndIndex());
   String InBandwidthUtilHdx1 = "";
   String OutBandwidthUtilHdx1 = "";
   String InBandwidthUtilHdx2 = "";
   String OutBandwidthUtilHdx2 = "";   
   if(if1 == null){
   	//System.out.println("if1 is null");
   	if1 = new IfEntity();
   	if1.setOperStatus(1);
   	if1.setDescr("");
   }
   if(if2 == null){
   	//System.out.println("if2 is null");
   	if2 = new IfEntity();
   	if2.setOperStatus(1);
   	if2.setDescr("");
   }
   
   
        I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
        
        Vector vector1 = new Vector();
        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        try{
        	//vector1 = hostlastmanager.getInterface_share(host1.getIpAddress(),netInterfaceItem,"index","",""); 
        	vector1 = hostlastmanager.getInterface(host1.getIpAddress(),netInterfaceItem,"index","",""); 
        }catch(Exception e){
        	e.printStackTrace();
        }
        Vector vector2 = new Vector();
        //String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        try{
        	//vector2 = hostlastmanager.getInterface_share(host2.getIpAddress(),netInterfaceItem,"index","",""); 
        	vector2 = hostlastmanager.getInterface(host2.getIpAddress(),netInterfaceItem,"index","","");
        }catch(Exception e){
        	e.printStackTrace();
        }  
        if(vector1 != null && vector1.size()>0){
             for(int i=0 ;i<vector1.size() ; i++){
                String[] strs = (String[])vector1.get(i);
                String ifname = strs[1];
                String index = strs[0];
                if(index.equalsIgnoreCase(if1.getIndex())){
                	InBandwidthUtilHdx1 = strs[5];
                	OutBandwidthUtilHdx1 = strs[4];
                	System.out.println(strs[0]+"=="+strs[1]+"==="+strs[4]+"==="+strs[5]);
                	break;
                }
             }        
        }  
        if(vector2 != null && vector2.size()>0){
             for(int i=0 ;i<vector2.size() ; i++){
                String[] strs = (String[])vector2.get(i);
                String ifname = strs[1];
                String index = strs[0];
                if(index.equalsIgnoreCase(if2.getIndex())) {
                	InBandwidthUtilHdx2 = strs[5];
                	OutBandwidthUtilHdx2 = strs[4];
                	//System.out.println(strs[0]+"=="+strs[1]+"==="+strs[4]+"==="+strs[5]);
                	break;
                }
             }        
        }     		  	   
%>
<html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script> 
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<style>
.line_frame_body {
	font-family:"宋体";
	font-size: 9pt;
	background-color:#F5F5F5;
}
.line_main_table {
	border-top:solid 1px #B2B2B2;
	border-left:solid 1px #B2B2B2;
	border-bottom:solid 1px #777777;
	border-right:solid 1px #777777;
	background-color:#CCCCCC;
}
</style>
<title>设备链路信息</title>
<script type="text/javascript" src="lib/js/disable.js"></script>
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<script type="text/JavaScript">
<!--
function swapImgRestore()
{
	var i, x, a = document.sr;
	for (i = 0; a && i<a.length && (x=a[i]) && x.oSrc; i++)
		x.src = x.oSrc;
}

function preloadImages()
{
	var d = document;
	if (d.images)
	{
		if (!d.p)
			d.p=new Array();
		var i, j = d.p.length, a = preloadImages.arguments;
		for (i = 0; i < a.length; i++)
		{
			if (a[i].indexOf("#") != 0)
			{
				d.p[j] = new Image;
				d.p[j++].src = a[i];
			}
		}
	}
}

function findObj(n, d)
{
	var p, i, x;
	if (!d)
		d = document;
	if ((p = n.indexOf("?"))>0 && parent.frames.length)
	{
		d = parent.frames[n.substring(p+1)].document;
		n = n.substring(0,p);
	}
	if (!(x=d[n]) && d.all)
		x=d.all[n];
	for (i = 0; !x && i<d.forms.length; i++)
		x = d.forms[i][n];
	for (i = 0;!x && d.layers && i<d.layers.length; i++)
		x = findObj(n,d.layers[i].document);
	if (!x && d.getElementById)
		x = d.getElementById(n);
	return x;
}

function swapImage()
{
	var i, j = 0, x, a = swapImage.arguments;
	document.sr = new Array;
	for (i = 0; i < (a.length-2); i+=3)
	if ((x=findObj(a[i]))!=null) {
		document.sr[j++] = x;
		if (!x.oSrc)
			x.oSrc = x.src;
		x.src = a[i+2];
	}
}

function refreshFrame()
{
	if (window.parent.document.all.linkedline.style.visibility == "hidden")
	{
		return;
	}

	window.location.reload();
}

setInterval("refreshFrame()", 30*1000);

function closeFrame()
{
	window.parent.closeLineInfoFrame();
}
//-->
</script>
</head>
<body id="body" class="body">
		<table id="body-container" class="body-container" >
			<tr>
			    <td class="td-container-menu-bar">
					<table>
						<tr>
							<td>
							</td>	
						</tr>
					</table>
				</td>
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-detail">
								<table id="container-main-detail" class="container-main-detail">
									<tr>
										<td>
											<table id="detail-content" class="detail-content" >
												<tr>
													<td>
														<table id="detail-content-header" class="detail-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="detail-content-title">链路详细信息</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        											<table>
										               					<tr>
										     								<td width="100%" align="left" valign="top">
										     									<table cellpadding="0" cellspacing="0" width=100% align=center>
										       										<tr>
										         										<td width="30%" height="26" align="left" nowrap>&nbsp;</td>
										         										<td width="30%" height="26" align="left" nowrap>&nbsp;起点设备:</td>
										         										<td width="30%" height="26" align="left" nowrap>&nbsp;终点设备:</td>
										       										</tr>
										       										<tr bgcolor="#F1F1F1">
										         										<td width="30%" height="26" align="left" nowrap>&nbsp;设备名称:</td>
										         										<td width="30%"><%=host1.getAlias()%></td>
										         										<td width="30%"><%=host2.getAlias()%></td>
										       										</tr>                    										
										       										<tr>
										         										<td height="29" align="left">&nbsp;管理IP地址:</td>
										         										<td><%=host1.getIpAddress()%>&nbsp;</td>
										         										<td><%=host2.getIpAddress()%>&nbsp;</td>
										       										</tr>
										       										<tr bgcolor="#F1F1F1">
										       											<td width="30%" height="26" align=left nowrap>&nbsp;接口IP地址:</td>
										       											<td width="30%"><%=link.getStartIp()%>&nbsp;</td>
										       											<td width="30%"><%=link.getEndIp()%>&nbsp;</td>
										       										</tr>
										       										<tr>
										       											<td height="29" align="left">&nbsp;接口索引/状态:</td>
										       											<td><%=link.getStartIndex()%>(<%=if1.getOperStatus()==1?"up":"<font color='red'>dwon</font>"%>)</td>
										       											<td><%=link.getEndIndex()%>(<%=if2.getOperStatus()==1?"up":"<font color='red'>down</font>"%>)</td>
										       										</tr>
										       										<tr bgcolor="#F1F1F1">
										       											<td width="30%" height="26" align=left nowrap>&nbsp;接口描述:</td>
										       											<td width="30%"><%=if1.getDescr()%></td>
										       											<td width="30%"><%=if2.getDescr()%></td>
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
									<tr>
										<td align=center>
											<table id="detail-data" class="detail-data" align=center>
											  	<tr>
											    	<td align=center>
														<table class="detail-data-body">
                 										    <tr>
										                		<td align=center >
											                		<table cellpadding="0" cellspacing="0" width=98%>
									         							<tr> 
										           							<td width="100%" > 
										           								<div id="flashcontent0">
																					<strong>You need to upgrade your Flash Player</strong>
																				</div>
																				<script type="text/javascript">
																					var so = new SWFObject("<%=rootPath%>/flex/Line_Link_Ping.swf?linkid=<%=line%>", "Line_Link_Ping", "800", "250", "8", "#ffffff");
																					so.write("flashcontent0");
																				</script>
																				<br>				
															                </td>
																	    </tr>             
															        </table>
										           			    </td>
                 											</tr>
                    										<tr>
                  												<td align=center >
                  												    <table cellpadding="0" cellspacing="0" width=98%>
								              							<tr> 
								                							<td width="100%" > 
								                								<div id="flashcontent1">
																					<strong>You need to upgrade your Flash Player</strong>
																				</div>
																				<script type="text/javascript">
																					var so = new SWFObject("<%=rootPath%>/flex/Area_Link_flux.swf?linkid=<%=line%>", "Area_Link_flux", "800", "250", "8", "#ffffff");
																					so.write("flashcontent1");
																				</script><br>				
															                </td>
																		</tr>             
																	</table>
												            		<br>
												          		</td>
												          	</tr>
												          	<tr>
												          		<td align=center >
												          		    <table cellpadding="0" cellspacing="0" width=98%>
								              							<tr> 
								                							<td width="100%" > 
								                								<div id="flashcontent2">
																					<strong>You need to upgrade your Flash Player</strong>
																				</div>
																				<script type="text/javascript">
																					var so = new SWFObject("<%=rootPath%>/flex/Area_Link_util.swf?linkid=<%=line%>", "Area_Link_util", "800", "250", "8", "#ffffff");
																					so.write("flashcontent2");
																				</script><br>				
															                </td>
																		</tr>             
																	</table>
																	<br>
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
				<td class="td-container-menu-bar">
					<table>
						<tr>
							<td>
							</td>	
						</tr>
					</table>
				</td>
			</tr>
		</table>
</BODY>
</HTML>