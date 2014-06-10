<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
	String nodeId = (String) request.getParameter("nodeId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="js/tomcatConfig.js" type="text/javascript"></script>

<style type="text/css">
body {
	font-size: 12px;
}

.l-table-edit {
	
}

.l-table-edit-td {
	height: 30px;
	line-height: 30px;
	text-align: right;
	width: 120px;
}

.l-table-value-td {
	height: 30px;
	line-height: 30px;
	text-align: left;
	border-right: 1px dotted green;
}

#lineOne {
	width: 100%;
	float: left;
}

#configDiv {
	float: left;
	margin: 2px 0 0 2px;
}

#configDiv table {
	width: 100%;
}

#picDiv {
	float: left;
	margin: 2px 0 0 5px;
	width: 420px;
}

.group {
	height: 30px;
	line-height: 30px;
	border-right: 1px solid green;
	color: white;
	background: url('<%=basePath%>css/img/common/blue-sides.gif');
	font-weight: bold;
	color: white
}
</style>

</head>

<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="ip" type="hidden" value="<%=ip%>" />
	<input id="nodeId" type="hidden" value="<%=nodeId%>" />
	<div id="lineOne">
		<div id="configDiv">
			<table cellpadding="0" cellspacing="0" class="l-table-edit" id="configTable">
				<tbody>
					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">基础信息</td>
					</tr>
					<tr>
						<td class="l-table-edit-td">Tomcat版本:</td>
						<td class="l-table-value-td" id="serverInfo" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">JDK版本:</td>
						<td class="l-table-value-td" id="implementationVersion" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">JDK供应商:</td>
						<td class="l-table-value-td" id="vmVendor" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td" valign="top">虚拟机:</td>
						<td class="l-table-value-td" id="vmNameVer" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">启动时间:</td>
						<td class="l-table-value-td" id="startTime" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">工作时间:</td>
						<td class="l-table-value-td" id="upTime" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">操作系统</td>
					</tr>
					<tr>
						<td class="l-table-edit-td">操作系统:</td>
						<td class="l-table-value-td" id="operatingSystemName" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">物理内存总量(Mb):</td>
						<td class="l-table-value-td" id="totalPhysicalMemorySizeL" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">可用物理内存(Mb):</td>
						<td class="l-table-value-td" id="freePhysicalMemorySizeL" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">交换空间总量(Mb):</td>
						<td class="l-table-value-td" id="totalSwapSpaceSizeL" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">可用交换空间(Mb):</td>
						<td class="l-table-value-td" id="freeSwapSpaceSizeL" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">分配虚拟内存(Mb):</td>
						<td class="l-table-value-td" id="committedVirtualMemorySizeL" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">体系架构:</td>
						<td class="l-table-value-td" id="arch" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">可用CPU数:</td>
						<td class="l-table-value-td" id="availableProcessors" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">处理Cpu时间(S):</td>
						<td class="l-table-value-td" id="processCpuTime" style="padding-left: 15px;"></td>
					</tr>

					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">类加载</td>
					</tr>
					<tr>
						<td class="l-table-edit-td">活动线程:</td>
						<td class="l-table-value-td" id="ThreadCount" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">峰:</td>
						<td class="l-table-value-td" id="PeakThreadCount" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">守护线程:</td>
						<td class="l-table-value-td" id="DaemonThreadCount" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">已启动线程:</td>
						<td class="l-table-value-td" id="TotalStartedThreadCount" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">当前装入类:</td>
						<td class="l-table-value-td" id="loadedClassCount" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">已装入类总数:</td>
						<td class="l-table-value-td" id="totalLoadedClassCount" style="padding-left: 15px;"></td>
					</tr>
					<tr>
						<td class="l-table-edit-td">已卸载类:</td>
						<td colspan="5" class="l-table-value-td" id="unloadedClassCount" style="padding-left: 15px;"></td>
					</tr>

					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">内存信息</td>
					</tr>
					<tr>
						<td class="l-table-edit-td">当前堆大小(MB):</td>
						<td class="l-table-value-td" id="heapUsedMemoryL" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td">分配的内存(MB):</td>
						<td class="l-table-value-td" id="heapCommitMemoryL" style="padding-left: 15px;"></td>
						<td class="l-table-edit-td" valign="top">堆最大值(MB)</td>
						<td class="l-table-value-td" id="heapMaxMemoryL" style="padding-left: 15px;"></td>
					</tr>

					<tr>
						<td colspan="6" class="group" style="padding-left: 15px;">垃圾收集器</td>
					</tr>
					<tr>
						<td colspan="6" class="l-table-value-td" id="collectorString" style="padding-left: 15px;"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div id="picDiv">
			<div>
				<div id="pingTodayPanel" style="float: left;">
					<div id="pingTodayPie"></div>
				</div>
			</div>
			<div>
				<div id="jvmTodayPanel" style="float: left; margin-top: 2px;">
					<div id="jvmTodayPic"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>