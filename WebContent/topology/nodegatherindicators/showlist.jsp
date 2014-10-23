<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.indicators.model.NodeGatherIndicators"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>

<%
    String rootPath = request.getContextPath();

    String menuTable = (String) request.getAttribute("menuTable");

    String nodeid = (String) request.getAttribute("nodeid");
    String type = (String) request.getAttribute("type");
    String subtype = (String) request.getAttribute("subtype");

    List allNodeDTOlist = (List) request.getAttribute("allNodeDTOlist");
    List<NodeGatherIndicators> list = (List<NodeGatherIndicators>) request.getAttribute("list");
    Hashtable<String, NodeDTO> nodeDTOHashtable = (Hashtable<String, NodeDTO>) request.getAttribute("nodeDTOHashtable");
    JspPage jp = (JspPage) request.getAttribute("page");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link
			href="<%=rootPath%>/common/contextmenu/skins/default/contextmenu.css"
			rel="stylesheet">
		<script src="<%=rootPath%>/common/contextmenu/js/jquery-1.8.2.min.js"
			type="text/javascript"></script>
		<script src="<%=rootPath%>/common/contextmenu/js/contextmenu.js"
			type="text/javascript"></script>

		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript">
		 	
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
		<script language="JavaScript">
			function add(){
				var type = document.getElementById("type");
				var subtype = document.getElementById("subtype");
				if(type.value!="-1" && subtype.value !="-1" ){
					mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showAdd&jspFlag=add";
				mainForm.submit();
				}
				if(type.value=="-1"){
					alert("请选择类型!");
				}
				if(subtype.value=="-1"){
					alert("请选择子类型!");
				}
			}
			
			function toChooseNode(){
				var nodeid = document.getElementById("nodeid");
				var type = document.getElementById("type");
				var subtype = document.getElementById("subtype");
				if(type.value!="-1" && subtype.value !="-1" && nodeid.value !="-1"){
					mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showAdd&jspFlag=multi";
					mainForm.submit();
				}
				if(type.value=="-1"){
					alert("请选择类型!");
				}
				if(subtype.value=="-1"){
					alert("请选择子类型!");
				}
				if(nodeid.value=="-1"){
					alert("请选择设备!");
				}
				
			}
			function search(){
				mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showlist";
				mainForm.submit();
			}
		</script>
		<script type="text/javascript">
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
  			var delAction = "<%=rootPath%>/nodeGatherIndicators.do?action=showDelete";
  			var listAction = "<%=rootPath%>/nodeGatherIndicators.do?action=showlist";
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
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
									<table id="container-main-content"
										class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
													<tr>
														<td align="left" width="5">
															<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																width="5" height="29" />
														</td>
														<td class="content-title">
															&nbsp;资源 >> 性能监视 >> 设备采集指标列表
														</td>
														<td align="right">
															<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																width="5" height="29" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="content-body" class="content-body">
													<tr>
														<td>
															<table>
																<tr>
																	<td class="body-data-title" style="text-align: left;">
																		&nbsp;&nbsp;
																		<b>类型：</b>
																		<select id="type" name="type" style="width: 150px"
																			onchange="changeType()">
																			<option value="-1">
																				不限
																			</option>
																			<option value="host">
																				服务器
																			</option>
																			<option value="net">
																				网络设备
																			</option>
																			<option value="firewall">
																				防火墙
																			</option>
																			<option value="gateway">
																				邮件安全网关
																			</option>
																			<option value="atm">
																				ATM
																			</option>
																			<option value="balancer">
																				负载均衡器
																			</option>
																			<option value="db">
																				数据库
																			</option>
																			<option value="middleware">
																				中间件
																			</option>
																			<option value="service">
																				服务
																			</option>
																			<option value="ups">
																				UPS
																			</option>
																			<option value="air">
																				空调
																			</option>
																			<option value="storage">
																				存储
																			</option>
																		</select>
																		&nbsp;&nbsp;&nbsp;
																		<b>子类型：</b>
																		<select id="subtype" name="subtype"
																			style="width: 150px" onchange="changeNode()">
																			<option value="-1">
																				不限
																			</option>
																		</select>
																		&nbsp;&nbsp;&nbsp;
																		<b>设备：</b>
																		<select id="nodeid" name="nodeid" style="width: 150px">
																		</select>
																		&nbsp;&nbsp;&nbsp;
																		<input type="button" value="查  询" onclick="search()">
																	</td>
																	<td class="body-data-title" style="text-align: right;">
																		<a href="#" onclick="add()">添加</a>&nbsp;&nbsp;&nbsp;
																		<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
																		<a href="#" onclick="toChooseNode()">批量应用</a>&nbsp;&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
																	<td class="body-data-title">
																		<jsp:include page="../../common/page.jsp">
																			<jsp:param name="curpage"
																				value="<%=jp.getCurrentPage()%>" />
																			<jsp:param name="pagetotal"
																				value="<%=jp.getPageTotal()%>" />
																		</jsp:include>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
																	<td class="body-data-title">
																		<INPUT type="checkbox" name="checkall"
																			onclick="javascript:chkall()">
																		序号
																	</td>
																	<td class="body-data-title">
																		设备名称
																	</td>
																	<td class="body-data-title">
																		设备IP
																	</td>
																	<td class="body-data-title">
																		指标名称
																	</td>
																	<td class="body-data-title">
																		描述
																	</td>
																	<td class="body-data-title">
																		类型
																	</td>
																	<td class="body-data-title">
																		子类型
																	</td>
																	<td class="body-data-title">
																		是否采集
																	</td>
																	<td class="body-data-title">
																		采集间隔
																	</td>
																	<td class="body-data-title">
																		详情
																	</td>
																</tr>
																<%
																    if (list != null && list.size() > 0) {
																        for (int i = 0; i < list.size(); i++) {
																            NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) list.get(i);

																            String unit = "";
																            if ("s".equals(nodeGatherIndicators.getInterval_unit())) {
																                unit = "秒";
																            } else if ("m".equals(nodeGatherIndicators.getInterval_unit())) {
																                unit = "分钟";
																            } else if ("h".equals(nodeGatherIndicators.getInterval_unit())) {
																                unit = "小时";
																            } else if ("d".equals(nodeGatherIndicators.getInterval_unit())) {
																                unit = "天";
																            } else if ("w".equals(nodeGatherIndicators.getInterval_unit())) {
																                unit = "周";
																            } else if ("mt".equals(nodeGatherIndicators.getInterval_unit())) {
																                unit = "月";
																            } else if ("y".equals(nodeGatherIndicators.getInterval_unit())) {
																                unit = "年";
																            }
																            String isCollection = "否";
																            if ("1".equals(nodeGatherIndicators.getIsCollection())) {
																                isCollection = "是";
																            }

																            NodeDTO nodeDTO = (NodeDTO) nodeDTOHashtable.get(String.valueOf(nodeGatherIndicators.getNodeid()) + ":" + nodeGatherIndicators.getType() + ":" + nodeGatherIndicators.getSubtype());
																            if (nodeDTO == null) {
																                nodeDTO = new NodeDTO();
																            }
																            String ipaddress = nodeDTO.getIpaddress();
																            if (ipaddress == null) {
																                ipaddress = "";
																            }
																%>
																<tr>
																	<td class="body-data-list">
																		<INPUT type="checkbox" name="checkbox"
																			value="<%=nodeGatherIndicators.getId()%>"><%=jp.getStartRow() + i%></td>
																	<td class="body-data-list"><%=nodeDTO.getName()%></td>
																	<td class="body-data-list"><%=ipaddress%></td>
																	<td class="body-data-list"><%=nodeGatherIndicators.getName()%></td>
																	<td class="body-data-list"><%=nodeGatherIndicators.getAlias()%></td>
																	<td class="body-data-list"><%=nodeGatherIndicators.getType()%></td>
																	<td class="body-data-list"><%=nodeGatherIndicators.getSubtype()%></td>
																	<td class="body-data-list"><%=isCollection%></td>
																	<td class="body-data-list"><%=nodeGatherIndicators.getPoll_interval() + " " + unit%></td>
																	<td class="body-data-list">
																		<input type="hidden" id="id" name="id"
																			value="<%=nodeGatherIndicators.getId()%>">
																		<img class="img"
																			src="<%=rootPath%>/resource/image/status.gif"
																			border="0" width=15>
																	</td>
																</tr>
																<%
																    }
																    }
																%>
															</table>
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
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0">
																<tr>
																	<td align="left" valign="bottom">
																		<img src="<%=rootPath%>/common/images/right_b_01.jpg"
																			width="5" height="12" />
																	</td>
																	<td></td>
																	<td align="right" valign="bottom">
																		<img src="<%=rootPath%>/common/images/right_b_03.jpg"
																			width="5" height="12" />
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

	<script>
	
		
	
		var nodeArray = new Array();
		
		<%
		    String Type="";
		    String Subtype="";
			if(allNodeDTOlist != null){
				for(int j = 0 ; j < allNodeDTOlist.size(); j++){
				 	NodeDTO  nodeDTO = (NodeDTO)allNodeDTOlist.get(j);
					   Type=nodeDTO.getType();
					   Subtype=nodeDTO.getSubtype();
					%>
						nodeArray.push({
							id:"<%=nodeDTO.getId()%>",
							nodeid:"<%=nodeDTO.getNodeid()%>",
							name:"<%=nodeDTO.getName()%>",
							ipaddress:"<%=nodeDTO.getIpaddress()%>",
							type:"<%=Type%>",
							subtype:"<%=Subtype%>",
							businessId:"<%=nodeDTO.getBusinessId()%>",
							businessName:"<%=nodeDTO.getBusinessName()%>"
					});
					<%
				}
			}
			
		%>
		
		function changeType(){
			var hostArray = [
						["windows" , "windows"],
						["linux" , "linux"],
						["aix" , "aix"],
						["hpunix" , "hpunix"],
						["solaris" , "solaris"],
						["scounix" , "scounix"],
						["scoopenserver" , "scoopenserver"],
						["as400" , "as400"]
						];
			var netArray = new Array();
			netArray = 	[
						["cisco" , "cisco"],
						["h3c" , "h3c"],
						["entrasys" , "entrasys"],
						["maipu" , "maipu"],
						["redgiant" , "redgiant"],
						["northtel" , "northtel"],
						["dlink" , "dlink"],
						["Brocade" , "brocade"],
						["hp" , "hp"],
						["DPtech" , "dptech"],
						["bdcom" , "bdcom"]
						];
			var dbArray = new Array();
			dbArray = 	[
						["oracle" , "oracle"],
						["sqlserver" , "sqlserver"],
						["mysql" , "mysql"],
						["db2" , "db2"],
						["sybase" , "sybase"],
						["Informix" , "informix"]
						];
			var middlewareArray = new Array();
			middlewareArray = 	[
						["mq" , "mq"],
						["domino" , "domino"],
						["was" , "was"],
						["tomcat" , "tomcat"],
						["iis" , "iis"],
						["jboss" , "jboss"],
						["apache" , "apache"],
						["tuxedo" , "tuxedo"],
						["cics" , "cics"],
						["dns" , "dns"],
						["weblogic" , "weblogic"]
								];
			var serviceArray = new Array();
			serviceArray = 	[
						["ftp" , "ftp"],
						["mail" , "mail"],
						["hostprocess" , "hostprocess"],
						["url" , "url"],
						["socket" , "socket"]
					];
		    var firewallArray = new Array();
			firewallArray = [
						["山石" , "hillstone"],
						["Netscreen" , "netscreen"],
						["NOKIA" , "nokia"],
						["TopSec" , "topsec"],
						["SecGate" , "secgate"],
						["DPtech" , "dptech"],
						["Venus" , "venus"],
						["TippingPoint" , "tippingpoint"],
						["SecWrold" , "secworld"]
								];
			var atmArray = new Array();
			atmArray = [
						["ATM" , "atm"]
					   ];
		    var f5Array = new Array();
			f5Array = [
						["F5" , "f5"],
						["深信服" , "sangfor"]
					   ];
			var gatewayArray = new Array();
			gatewayArray = [
						    ["CISCO" , "cisco"]
						   ];
			var upsArray = new Array();
			upsArray = 	[
						["艾默生" , "ems"],
						["梅兰日兰" , "mge"]
								];
			var airArray = new Array();
			airArray = 	[
						["艾默生" , "ems"]  
								];
		    var storageArray = new Array();
			storageArray = 	[
						["hds" , "hds"]  
								];
			var type = document.getElementById("type");
			var subtype = document.getElementById("subtype");
			subtype.length = 0;
			if(type.value == "-1"){
				subtype.options[0] = new Option("不限" , "-1");      
			}else if(type.value == "net"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <netArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(netArray[i][0],netArray[i][1]);                     
				}	
			}else if(type.value == "host"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <hostArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(hostArray[i][0],hostArray[i][1]);                     
				}	
			}else if(type.value == "db"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <dbArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(dbArray[i][0],dbArray[i][1]);                     
				}	
			}else if(type.value == "middleware"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <middlewareArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(middlewareArray[i][0],middlewareArray[i][1]);                     
				}	
			}else if(type.value == "service"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <serviceArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(serviceArray[i][0],serviceArray[i][1]);  
				}                   
			}else if(type.value == "ups"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <upsArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(upsArray[i][0],upsArray[i][1]);                     
				}	
			}else if(type.value == "air"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <airArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(airArray[i][0],airArray[i][1]);                     
				}	
			}else if(type.value == "atm"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <atmArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(atmArray[i][0],atmArray[i][1]);                     
				}	
			}else if(type.value == "balancer"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <f5Array.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(f5Array[i][0],f5Array[i][1]);                     
				}	
			}else if(type.value == "firewall"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <firewallArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(firewallArray[i][0],firewallArray[i][1]);                     
				}	
			}else if(type.value == "gateway"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <gatewayArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(gatewayArray[i][0],gatewayArray[i][1]);                     
				}	
			}else if(type.value == "storage"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <storageArray.length;i++)   
	      		{    
					subtype.options[i+1] = new Option(storageArray[i][0],storageArray[i][1]); 
				}	
			}
			changeNode();
		}
		
		
		function changeNode(){
			
			var type = document.getElementById("type");
			var subtype = document.getElementById("subtype");
			var nodeid = document.getElementById("nodeid");
			nodeid.length = 0;
			nodeid.options[0] = new Option("不限","-1");
			if(type.value == "-1"){
				var k = 1;
				
				for (var i = 0 ;i <nodeArray.length;i++)   
	      		{                   
					nodeid.options[k] = new Option(nodeArray[i].name,nodeArray[i].nodeid);
	      			k++;               
				}	
			}else if(type.value != "-1" && subtype.value == "-1"){
				var k = 1;
				for (var i = 0 ;i <nodeArray.length;i++)   
	      		{      
	      			if(nodeArray[i].type == type.value){
	      				nodeid.options[k] = new Option(nodeArray[i].name,nodeArray[i].nodeid);
	      				k++;
	      			}             
					                
				}	
			}else if(type.value != "-1" && subtype.value != "-1"){
				var k = 1;
				for (var i = 0 ;i <nodeArray.length;i++)
	      		{      
	      			if(nodeArray[i].type == type.value && nodeArray[i].subtype == subtype.value){
	      				nodeid.options[k] = new Option(nodeArray[i].name,nodeArray[i].nodeid);
	      				k++;
	      			}             
					                
				}	
			}
			
		}
		
		function initAttribute(){
			var type = document.getElementById("type");
			var subtype = document.getElementById("subtype");
			var nodeid = document.getElementById("nodeid");
			
			type.value = "<%=type%>";
			changeType();
			subtype.value = "<%=subtype%>";
			changeNode();
			nodeid.value = "<%=nodeid%>";
		}
		
		initAttribute();
	</script>
	<script type="text/javascript">  
			$('.img').contextmenu({
				height:35,
				width:100,
				items : [{
					text :'编辑',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/edit.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
						edit(id);
					}
				}]
			});	
			
		//右键菜单方法开始
			function edit(node){
				mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showEdit&id=" + node;
				mainForm.submit();
			}
		//右键菜单方法结束
		</script>
</html>
