var grid = null;
var basePath = null;
var contextMenu = null;
var nodeId = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
					"like", "equal", "notequal", "greater", "less", "startwith", "endwith"];
			// 右键菜单项
			contextMenu = $.ligerMenu({
						width : 120,
						items : [{
									text : '连接测试',
									click : contextMenuItemClick,
									icon : 'tomcat'
								}]
					});
			// grid
			grid = $("#tomcatGrid").ligerGrid({
				columns : [{
					display : '状态',
					name : 'status',
					width : 30,
					render : function(item) {
						return "<img class='statePic' src='" + basePath + "css/img/pList/level"
								+ item.status + ".gif'>";
					}
				}, {
					display : '网元名称',
					name : 'alias',
					minWidth : 200
				}, {
					display : '网元IP',
					name : 'ip',
					minWidth : 100
				}, {
					display : '端口',
					name : 'port',
					minWidth : 60,
					width : 60
				}, {
					display : '可用性(%)',
					columns : [{
								display : '连通率',
								name : 'pingValue',
								minWidth : 60,
								width : 80
							}, {
								display : 'D',
								minWidth : 20,
								width : 30,
								render : function(item) {
									return toDetail(basePath
													+ 'detail/tomcat_ping_month.jsp?id=2&ip='
													+ item.ip, 320, 850, "Tomcat连通率详细");
								}
							}]
				}, {
					display : 'JVM',
					columns : [{
								display : '利用率(%)',
								name : 'jvm',
								minWidth : 60,
								width : 80
							}, {
								display : 'D',
								minWidth : 20,
								width : 30,
								render : function(item) {
									return toDetail(basePath
													+ 'detail/Tomcat_JVM_month.jsp?id=2&ip='
													+ item.ip, 320, 850, "Tomcat连通率详细");
								}
							}]

				}, {
					display : '版本',
					name : 'version',
					minWidth : 40,
					width : 150
				}, {
					display : 'JDK版本',
					name : 'jdkVersion',
					minWidth : 40,
					width : 120
				}, {
					display : '操作系统',
					name : 'os',
					minWidth : 40,
					width : 120
				}, {
					display : '监控',
					name : 'isM',
					minWidth : 40,
					width : 60,
					render : function(item) {
						if (item.isM == 1) {
							return "是";
						} else {
							return "否";
						}
					}
				}, {
					name : 'nodeId',
					hide : true,
					width : 0.1
				}],
				pageSize : 30,
				checkbox : true,
				data : getTomcatNodeData(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'nodeId',
				width : '99.8%',
				height : '100%',
				heightDiff :-4,
				onReload : function() {
					grid.set({
								data : getTomcatNodeData()
							});
				},
				// 工具栏
				toolbar : {
					items : [{
								text : '高级自定义查询',
								click : sItemclick,
								icon : 'search2'
							}, {
								text : '增加',
								click : itemclick,
								href : basePath + 'jsp/resourceManage/middleware/tomcat/add.jsp',
								icon : 'add'
							}, {
								text : '修改',
								click : itemclick,
								href : basePath
										+ 'jsp/resourceManage/middleware/tomcat/edit.jsp?id=',
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
							}]
				},
				// 双击
				onDblClickRow : function(data, rowindex, rowobj) {
					openFullWindow(basePath
									+ "jsp/performance/detail/tomcatPerformanceDetail.jsp?ip="
									+ data.ip + "&alias=" + data.alias + "&nodeId=" + data.nodeId+"&type=middleware&subType=tomcat",
							"Tomcat详细");
				},
				// 右键菜单
				onContextmenu : function(parm, e) {
					nodeId = parm.data.nodeId;
					contextMenu.show({
								top : e.pageY,
								left : e.pageX
							});
					return false;
				}
			});
		});

function sItemclick() {
	grid.options.data = $.extend(true, {}, getTomcatNodeData());
	grid.showFilter();
}
function itemclick(item) {
	if (item.text == "增加") {
		openWindow(item.href, 400, 500, item.text + "网元");
	} else if (item.text == "修改") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0 || rows.length > 1) {
			$.ligerDialog.error("请选择一条记录进行修改");
		} else {
			$(rows).each(function() {
						idString = this.nodeId;
					});
			openWindow(item.href + idString, 400, 550, item.text + "tomcat");
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
			f_deleteTomcatNodes(idString);
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
				url : basePath + "tomcatPerformanceAjaxManager.ajax?action=batchAddMonitor",
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
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "tomcatPerformanceAjaxManager.ajax?action=batchCancleMonitor",
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
function f_deleteTomcatNodes(string) {
	var url=basePath + "tomcatPerformanceAjaxManager.ajax?action=deleteTomcatNodes";
	showDeleteDlg(url,string);
}

function getTomcatNodeData() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "tomcatPerformanceAjaxManager.ajax?action=getTomcatNodeData",
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
function contextMenuItemClick(item, i) {
	if (item.text == "修改") {
		openWindow(basePath + 'jsp/resourceManage/middleware/tomcat/edit.jsp?nodeId=' + nodeId,
				350, 500, "tomcat修改");
	} else if (item.text == "连接测试") {
		openDlgWindow(basePath + 'jsp/tool/tomcatCheck.jsp?tomcatId=' + nodeId, 200, 400,
				"tomcat连接测试");
	}
}
// 刷新列表
function refresh() {
	grid.set({
				data : getTomcatNodeData()
			});
}
function createDiv(pw, value, color) {
	var divString = "<div style='float:left;margin-top:5px;width:" + pw
			+ "px;border:1px solid green;'><div style='height:12px;background:" + color + ";width:"
			+ value + "%;'></div></div>" + value;
	return divString;
}

function toDetail(href, h, w, t) {
	var imgString = "<img src='"
			+ basePath
			+ "css/img/pList/pDetail.gif' style='margin-top:5px;' class='pDetail' onclick='openWindow(\""
			+ href + "\"," + h + "," + w + ",\"" + t + "\")' />";
	return imgString;
}
