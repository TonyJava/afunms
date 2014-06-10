var grid = null;
var basePath = null;
var contextMenu = null;
var nodeIp = null;
var nodeId = null;
$(function() {
	basePath = $("#basePath").attr("value");
	$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
			"like", "equal", "notequal", "greater", "less", "startwith",
			"endwith" ];
	// 右键菜单项
	contextMenu = $.ligerMenu({
		width : 120,
		items : [ {
			text : 'Ping',
			click : contextMenuItemClick,
			icon : 'ping'
		}, {
			text : 'WebTelnet',
			click : contextMenuItemClick,
			icon : 'telnet'
		}, {
			text : 'SNMP',
			click : contextMenuItemClick,
			icon : 'snmp'
		} ]
	});
	// grid
	grid = $("#firewallGrid")
			.ligerGrid(
					{
						columns : [
								{
									display : '状态',
									name : 'status',
									width : 30,
									render : function(item) {
										return "<img class='statePic' src='"
												+ basePath
												+ "css/img/pList/level"
												+ item.status + ".gif'>";
									}
								},
								{
									display : '网元名称',
									name : 'alias',
									minWidth : 200
								},
								{
									display : '网元IP',
									name : 'ip',
									minWidth : 100
								},
								{
									display : '可用性(%)',
									columns : [
											{
												display : '连通率',
												name : 'pingValue',
												minWidth : 60,
												width : 80
											},
											{
												display : 'D',
												minWidth : 20,
												width : 20,
												render : function(item) {
													return toDetail(
															basePath
																	+ 'jsp/performance/pDetail/pingDetail.jsp?id=2&nodeid='
																	+ item.nodeId
																	+ '&alias='
																	+ item.alias
																	+ '&ip='
																	+ item.ip,
															320, 850, "连通率详细");
												}
											} ]
								},
								{
									display : 'CPU(%)',
									columns : [
											{
												display : '利用率',
												name : 'cpuValue',
												minWidth : 80,
												width : 100,
												render : function(item) {
													return createDiv(60,
															item.cpuValue,
															item.cpuColor);
												}
											},
											{
												display : 'D',
												minWidth : 20,
												width : 20,
												render : function(item) {
													return toDetail(
															basePath
																	+ 'jsp/performance/pDetail/cpuDetail.jsp?id=2&nodeid='
																	+ item.nodeId
																	+ '&alias='
																	+ item.alias
																	+ '&ip='
																	+ item.ip,
															300, 850, "CPU详细");
												}
											} ]
								},
								{
									display : '内存(%)',
									columns : [
											{
												display : '物理',
												name : 'physicalMemoryValue',
												minWidth : 80,
												width : 120,
												render : function(item) {
													return createDiv(
															60,
															item.physicalMemoryValue,
															item.physicalMemoryColor);
												}
											},
											{
												display : 'D',
												minWidth : 20,
												width : 20,
												render : function(item) {
													return toDetail(
															basePath
																	+ 'jsp/performance/pDetail/netMemoryDetail.jsp?id=2&nodeid='
																	+ item.nodeId
																	+ '&alias='
																	+ item.alias
																	+ '&ip='
																	+ item.ip,
															480, 850, "内存详细");
												}
											} ]
								},
								{
									display : '流量(KB/s)',
									columns : [
											{
												display : '出口',
												name : 'outUtilHdx',
												minWidth : 60,
												width : 120
											},
											{
												display : '入口',
												name : 'inUtilHdx',
												minWidth : 60,
												width : 120
											},
											{
												display : 'D',
												minWidth : 20,
												width : 20,
												render : function(item) {
													return toDetail(
															basePath
																	+ 'jsp/performance/pDetail/fluxDetail.jsp?id=2&nodeid='
																	+ item.nodeId
																	+ '&alias='
																	+ item.alias
																	+ '&ip='
																	+ item.ip,
															480, 850, "流速详细");
												}
											} ]
								}, {
									display : '接口',
									name : 'ifNumber',
									minWidth : 40,
									width : 50
								}, {
									display : '厂家',
									name : 'vender',
									minWidth : 40,
									width : 60
								}, {
									display : '监控',
									name : 'isM',
									minWidth : 40,
									width : 50,
									render : function(item) {
										if (item.isM == "true") {
											return "是";
										} else {
											return "否";
										}
									}
								}, {
									name : 'nodeId',
									hide : true,
									width : 0.1
								} ],
						pageSize : 30,
						checkbox : true,
						data : getNetNodeDataByType(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'nodeId',
						width : '99.8%',
						height : '100%',
						heightDiff : -4,
						onReload : function() {
							grid.set({
								data : getNetNodeDataByType()
							});
						},
						// 工具栏
						toolbar : {
							items : [
									{
										text : '高级自定义查询',
										click : sItemclick,
										icon : 'search2'
									},
									{
										text : '增加',
										click : itemclick,
										href : basePath
												+ 'jsp/resourceManage/device/add.jsp',
										icon : 'add'
									},
									{
										line : true
									},
									{
										text : '修改',
										click : itemclick,
										href : basePath
												+ 'jsp/resourceManage/device/edit.jsp?id=',
										icon : 'modify'
									}, {
										line : true
									}, {
										text : '删除',
										click : itemclick,
										icon : 'delete'
									}, {
										line : true
									}, {
										text : '开启监控',
										click : itemclick,
										icon : 'ok'
									}, {
										line : true
									}, {
										text : '取消监控',
										click : itemclick,
										icon : 'back'
									} ]
						},
						// 双击
						onDblClickRow : function(data, rowindex, rowobj) {
							openFullWindow(
									basePath
											+ "jsp/performance/detail/firewallPerformanceDetail.jsp?ip="
											+ data.ip + "&alias=" + data.alias
											+ "&nodeId=" + data.nodeId
											+ "&type=firewall&subType="
											+ data.vender, "网络设备详细");
						},
						// 右键菜单
						onContextmenu : function(parm, e) {
							nodeIp = parm.data.ip;
							nodeId = parm.data.nodeId;
							contextMenu.show({
								top : e.pageY,
								left : e.pageX
							});
							return false;
						}
					});
});
function contextMenuItemClick(item, i) {
	if (item.text == "Ping") {
		openDlgWindow(basePath + "jsp/tool/ping.jsp?ip=" + nodeIp, 380, 650,
				"Ping");
	} else if (item.text == "WebTelnet") {
		openDlgWindow(basePath + "jsp/tool/webTelnet.jsp?ip=" + nodeIp, 450,
				630, "WebTelnet");
	} else if (item.text == "SNMP") {
		openDlgWindow(basePath + "jsp/tool/snmpCheck.jsp?nodeId=" + nodeId,
				280, 650, "SNMP测试");
	}
}

function sItemclick() {
	grid.options.data = $.extend(true, {}, getNetNodeDataByType());
	grid.showFilter();
}

function itemclick(item) {
	if (item.text == "增加") {
		openWindow(item.href, 600, 650, item.text + "网元");
	} else if (item.text == "修改") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0 || rows.length > 1) {
			$.ligerDialog.error("请选择一条记录进行修改");
		} else {
			$(rows).each(function() {
				idString = this.nodeId;
			});
			openWindow(item.href + idString, 600, 650, item.text + "网元");
		}
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
				idString += this.nodeId + ";";
			});
			f_deleteNodes(idString);
		}
	} else if (item.text == "开启监控") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择项");
		} else {
			$(rows).each(function() {
				idString += this.nodeId + ";";
			});
			$.ligerDialog.success(batchAddMonitor(idString));
		}
	} else if (item.text == "取消监控") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择项");
		} else {
			$(rows).each(function() {
				idString += this.nodeId + ";";
			});
			$.ligerDialog.success(batchCancleMonitor(idString));
		}
	}
	refresh();
}

function batchAddMonitor(string) {
	var rs = "启用监控错误";
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "nodeHelperAjaxManager.ajax?action=batchAddMonitor",
		// 参数
		data : {
			string : string
		},
		dataType : "text",
		success : function(array) {
			rs = array;
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
	return rs;

}

function batchCancleMonitor(string) {
	var rs = "取消监控错误";
	$
			.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "nodeHelperAjaxManager.ajax?action=batchCancleMonitor",
				// 参数
				data : {
					string : string
				},
				dataType : "text",
				success : function(array) {
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;

}

function f_deleteNodes(string) {
	var url = basePath + "nodeHelperAjaxManager.ajax?action=deleteNodes";
	showDeleteDlg(url, string);
}

function getNetNodeDataByType() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "netPerformanceAjaxManager.ajax?action=getNetNodeDataByType",
		// 参数
		data : {
			type : getUrlParam("type")
		},
		dataType : "json",
		success : function(array) {
			rs = array;
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
	return rs;
}

// 刷新列表
function refresh() {
	grid.set({
		data : getNetNodeDataByType()
	});
}
function createDiv(pw, value, color) {
	var divString = "<div style='float:left;margin-top:5px;width:" + pw
			+ "px;border:1px solid green;'><div style='height:12px;background:"
			+ color + ";width:" + value + "%;'></div></div>" + value;
	return divString;
}

function toDetail(href, h, w, t) {
	var imgString = "<img src='"
			+ basePath
			+ "css/img/pList/pDetail.gif' style='margin-top:5px;' class='pDetail' onclick='openWindow(\""
			+ href + "\"," + h + "," + w + ",\"" + t + "\")' />";
	return imgString;
}
