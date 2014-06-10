<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<link href="<%=basePath%>css/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="<%=basePath%>css/Gray/css/all.css" rel="stylesheet" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/base.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerTab.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerComboBox.js" type="text/javascript"></script>
<script src="<%=basePath%>js/ligerPanel.js" type="text/javascript"></script>
<script src="<%=basePath%>js/swfobject.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<script src="js/topNShowDetail.js" type="text/javascript"></script>
<title>Insert title here</title>
<style type="text/css">
#option {
	border-bottom: 1px black dotted;
	height: 32px;
}

.opContainer {
	margin: 5px 0 0 10px;
	float: left;
	width: 190px;
}

.title {
	float: left;
	width: 45px;
	text-align: right;
	padding: 0 5px 0 0;
	height: 20px;
	line-height: 20px;
}

.inputDiv {
	float: left;
}

.partDivParent {
	width: 100%;
	float: left;
}

.divHead {
	width: 99%;
	background: url('<%=basePath%>css/img/pTool/divHead.gif');
	height: 25px;
	border-bottom: 1px solid green;
	font-weight: bold;
	padding: 5px 0 0 10px;
}
</style>
</head>
<body>
	<input id="basePath" type="hidden" value="<%=basePath%>" />
	<div style="width: 100%;" id="outContainer">
		<div id="option">
			<div class="opContainer">
				<div class="title">类型:</div>
				<div class="inputDiv">
					<input type="text" id="topNCategory" />
				</div>
			</div>
			<div class="opContainer">
				<div class="title">记录数:</div>
				<div class="inputDiv">
					<input type="text" id="topNCount" />
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead" id="pingAndResponseTime">连通率|响应时间</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<div id="pingPanel" style="float: left; margin: 0 0 0 5px">
					<div id="pingFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<div id="responseTimePanel" style="float: left; margin: 0 0 0 20px">
					<div id="responseTimeFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead" id="cpuAndMemory">CPU|内存</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<div id="cpuPanel" style="float: left; margin: 0 0 0 5px">
					<div id="cpuFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<div id="memoryPanel" style="float: left; margin: 0 0 0 20px">
					<div id="memoryFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead" id="ifBandwidth">带宽利用率</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<div id="inBandwidthPanel" style="float: left; margin: 0 0 0 5px">
					<div id="inBandwidthFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<div id="outBandwidthPanel" style="float: left; margin: 0 0 0 20px">
					<div id="outBandwidthFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead" id="disk">磁盘利用率</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<div id="diskPanel" style="float: left; margin: 0 0 0 5px">
					<div id="diskFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
			</div>
		</div>

		<div class="partDivParent">
			<div class="divHead" id="ifFlux">接口流速</div>
			<div style="width: 100%; height: 260px; margin-top: 3px; display: none">
				<div id="inFluxPanel" style="float: left; margin: 0 0 0 5px">
					<div id="inFluxFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
				<div id="outFluxPanel" style="float: left; margin: 0 0 0 20px">
					<div id="outFluxFlex">
						<strong>You need to upgrade your Flash Player</strong>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>