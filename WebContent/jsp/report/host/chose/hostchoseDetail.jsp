<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.text.SimpleDateFormat,java.util.Date,java.util.*,com.afunms.common.util.SysUtil"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	String startDate = df.format(new Date());
	String endDate = df.format(new Date());
	String runAppraise = (String) request.getAttribute("runAppraise");
	Hashtable allreporthash = (Hashtable) request
			.getAttribute("allreporthash");
	int rowSize = 0;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/ligerui-icons.css" rel="stylesheet"
	type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/jquery.validate.min.js"
	type="text/javascript"></script>
<script src="<%=basePath%>js/jquery.metadata.js" type="text/javascript"></script>
<script src="<%=basePath%>js/messages_cn.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerui.all.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerGrid.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerDialog.js" type="text/javascript"></script>
<script src="js/hostchoseRP.js"></script>
<script src="<%=basePath%>jsp/js/typeAndSubType.js"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
</head>
<style type="text/css">
body {
	font-size: 12px;
}

.microsoftLook0{ 
	FONT-SIZE: 9pt; 
	FONT-FAMILY: 宋体;
	BACKGROUND-COLOR: #ECECEC; 
	text-overflow: ellipsis; 
	overflow: hidden;
}

.trClass {
	background-color: white;
}

.l-table-edit {
	
}

.l-table-edit-td {
	padding: 4px;
}

.l-button-submit {
	width: 60px;
	float: left;
	margin-left: 10px;
	padding-bottom: 2px;
}

.l-verify-tip {
	left: 230px;
	top: 120px;
}
</style>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<form>
		<table cellspacing="1" cellpadding="0" width="100%" border="1">
			<tr align="center" height=28 class="microsoftLook0">
				<td colspan='15'>服务器决策支持报表 <input type="hidden" name="ids"
					value="<%=request.getAttribute("oids")%>" />
				</td>
			</tr>
			<tr align="center" height=28 class="microsoftLook0">
				<td>时间</td>
				<td colspan="14" class="trClass">&nbsp;&nbsp;从&nbsp;<%=startDate%>&nbsp;到&nbsp;<%=endDate%></td>
			</tr>
			<tr align="center" height=28 class="microsoftLook0">
				<td>管理员</td>
				<td colspan="3" class="trClass"><%=request.getAttribute("username")%></td>
				<td>部门</td>
				<td colspan="4" class="trClass"><%=request.getAttribute("positionname")%></td>
				<td>运行评价</td>
				<td colspan="5" class="trClass"><select name="runAppraise">
						<option label="优" <%="优".equals(runAppraise) ? "selected" : ""%>>
							优</option>
						<option label="良" <%="良".equals(runAppraise) ? "selected" : ""%>>
							良</option>
						<option label="差" <%="差".equals(runAppraise) ? "selected" : ""%>>
							差</option>
				</select></td>
			</tr>
			<tr align="center" height=28 class="microsoftLook0">
				<td rowspan="<%=rowSize + 2%>">服务器</td>
				<td rowspan="2">IP</td>
				<td colspan="2">连通率</td>
				<td colspan="2">CPU(%)</td>
				<td colspan="2">物理内存</td>
				<td rowspan="2">磁盘TOP</td>
				<td colspan="3">事件(个)</td>
			</tr>
			<tr align="center" height=28 class="microsoftLook0">
				<td>平均</td>
				<td>最小</td>
				<td>平均</td>
				<td>最大</td>
				<td>平均</td>
				<td>最大</td>
				<td>普通</td>
				<td>严重</td>
				<td>紧急</td>
			</tr>
			<%
				if (allreporthash != null && allreporthash.size() > 0) {
					String[] memoryItemch = { "内存容量", "当前利用率", "最大利用率", "平均利用率" };
					String[] memoryItem = { "Capability", "c" };
					String[] diskItem = { "AllSize", "UsedSize", "Utilization",
							"Utilizationvalue" };
					String[] diskItemch = { "总容量", "已用容量", "利用率" };
					Iterator keys = allreporthash.keySet().iterator();
					String ip = "";
					int sheetNum = 0;
					java.text.NumberFormat formate = java.text.NumberFormat
							.getNumberInstance();
					formate.setMaximumFractionDigits(0);//
					while (keys.hasNext()) {
						ip = keys.next().toString();
						String newip = SysUtil.doip(ip);
						Hashtable report_has = (Hashtable) allreporthash.get(ip);
						String hostname = (String) report_has.get("equipname");
						Hashtable CPU = (Hashtable) report_has.get("CPU");
						String Ping = (String) report_has.get("Ping");
						Calendar colTime = (Calendar) report_has.get("time");
						Date cc = colTime.getTime();
						Hashtable Memory = (Hashtable) report_has.get("Memory");
						Hashtable Disk = (Hashtable) report_has.get("Disk");

						Hashtable memMaxHash = (Hashtable) report_has
								.get("memmaxhash");
						Hashtable memAvgHash = (Hashtable) report_has
								.get("memavghash");
						Hashtable maxping = (Hashtable) report_has.get("ping");

						String levelone = (String) report_has.get("levelone");
						String levletwo = (String) report_has.get("levletwo");
						String levelthree = (String) report_has.get("levelthree");
						String string1 = ((String) maxping.get("pingmax")).replace(
								"%", "");
						String string2 = ((String) maxping.get("avgpingcon"))
								.replace("%", "");

						String cpu = "";
						if (CPU.get("cpu") != null)
							cpu = (String) CPU.get("cpu");
						String cpumax = "";
						if (CPU.get("cpumax") != null)
							cpumax = (String) CPU.get("cpumax");
						String avgcpu = "";
						if (CPU.get("avgcpu") != null)
							avgcpu = (String) CPU.get("avgcpu");
						String string3 = avgcpu.replace("%", "");
						String string4 = cpumax.replace("%", "");
						String string5 = null;
						String string6 = null;
						String avgvalue = "0.0";
						String maxvalue = "0.0";
						if (memAvgHash.get("PhysicalMemory") != null) {
							avgvalue = (String) memAvgHash.get("PhysicalMemory");
							string5 = avgvalue.replace("%", "");
							maxvalue = (String) memMaxHash.get("PhysicalMemory");
							string6 = maxvalue.replace("%", "");
						}

						String value1 = "0";
						String name = "";
						String Utilization = "";
						if (Disk != null && Disk.size() > 0) {
							for (int i = 0; i < Disk.size(); i++) {
								Hashtable diskhash = (Hashtable) (Disk
										.get(new Integer(i)));
								if (diskhash.get(diskItem[3]) != null) {
									if (value1.compareTo((String) diskhash
											.get(diskItem[3])) < 0) {
										value1 = (String) diskhash.get(diskItem[3]);
										name = (String) diskhash.get("name");
										Utilization = (String) diskhash
												.get(diskItem[2]);
									}
								}
							}
						}
						String topDiskPer = name + "  " + Utilization;
			%>
			<tr align="left" height=28 class="microsoftLook0">
				<td class="trClass"><%=ip%></td>
				<td class="trClass"><%=formate.format(Double.valueOf(string1))%></td>
				<td class="trClass"><%=formate.format(Double.valueOf(string2))%></td>
				<td class="trClass"><%=formate.format(Double.valueOf(string3))%></td>
				<td class="trClass"><%=formate.format(Double.valueOf(string4))%></td>
				<td class="trClass"><%=formate.format(Double.valueOf(string5))%></td>
				<td class="trClass"><%=formate.format(Double.valueOf(string6))%></td>
				<td class="trClass"><%=topDiskPer%></td>
				<td class="trClass"><%=levelone%></td>
				<td class="trClass"><%=levletwo%></td>
				<td class="trClass"><%=levelthree%></td>
			</tr>

			<%
				}
				}
			%>
			<tr align="center" height=28 class="microsoftLook0">
				<td colspan="15">业务分析</td>
			</tr>
			<tr align="center" height=28 class="microsoftLook0">
				<td colspan="15" class="trClass"><textarea rows="5"
						name="businessAnalytics" cols="210" style="border: 1;"></textarea>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>