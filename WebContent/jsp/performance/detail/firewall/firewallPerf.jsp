<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String ip = (String) request.getParameter("ip");
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
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/firewallPerf.js" type="text/javascript"></script>

<style type="text/css">
#outContainer {
	margin: 2px 0 0 2px;
}

.divHead {
	width: 100%;
	background: url('<%=basePath%>css/img/pTool/divHead.gif');
	height: 25px;
	border-bottom: 1px solid green;
	font-weight: bold;
	padding: 5px 0 0 10px;
}

.partDivParent {
	width: 100%;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<input id="ip" type="hidden" value="<%=ip%>" />

	<div style="width: 100%;" id="outContainer">
		<div class="partDivParent">
			<div class="divHead">连通率</div>
			<div style="width: 100%; height: 260px; margin-top: 3px">
				<div id="pingLinePanel" style="float: left;">
					<div id="pingLine">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<div id="pingPiePanel" style="float: left; margin-left: 10px;">
					<table>
						<tbody>
							<tr>
								<td><div id="realPingPie"></div></td>
								<td><div id="maxPingPie"></div></td>
								<td><div id="avgPingPie"></div></td>
							</tr>
							<tr>
								<td style="text-align: center">当前</td>
								<td style="text-align: center">最大</td>
								<td style="text-align: center">平均</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead">响应时间</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<div id="responseTimeLinePanel" style="float: left">
					<div id="responseTimeLine">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<div id="responseTimePiePanel" style="float: left; margin-left: 10px;">
					<div id="responseTimePie"></div>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead">CPU利用率</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<!-- cpu线图 -->
				<div id="cpuLinePanel" style="float: left;">
					<div id="cpuLine">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<!-- cpu饼图 -->
				<div id="cpuPiePanel" style="float: left; margin-left: 10px;">
					<div>
						<table>
							<tbody>
								<tr>
									<td><div id="realCpuPie"></div></td>
									<td><div id="maxCpuPie"></div></td>
									<td><div id="avgCpuPie"></div></td>
								</tr>
								<tr>
									<td style="text-align: center">当前</td>
									<td style="text-align: center">最大</td>
									<td style="text-align: center">平均</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead">流速曲线</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<!-- 流速线图 -->
				<div id="fluxLinePanel" style="float: left;">
					<div id="fluxLine">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<!-- 流速柱状图 -->
				<div id="fluxHistogramPanel" style="float: left; margin-left: 10px;">
					<div id="fluxHistogram" style="margin-top: 2px; margin-left: 10px;">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead">内存利用率</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<!-- 内存线图 -->
				<div id="memoryLinePanel" style="float: left;">
					<div id="memoryLine">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<!-- 内存柱状图 -->
				<div id="memoryHistogramPanel" style="float: left; margin-left: 10px;">
					<div id="memoryHistogram" style="margin-top: 2px; margin-left: 10px;"></div>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead">闪存利用率</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<!-- 闪存线图 -->
				<div id="flashLinePanel" style="float: left;">
					<div id="flashLine">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<!-- 闪存柱状图 -->
				<div id="flashHistogramPanel" style="float: left; margin-left: 10px;">
					<div id="flashHistogram" style="margin-top: 2px; margin-left: 10px;"></div>
				</div>
			</div>
		</div>

	</div>
</body>
</html>