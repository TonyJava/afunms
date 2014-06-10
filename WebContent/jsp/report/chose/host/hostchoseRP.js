var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({});

			// grid
			grid = $("#hostchoseGrid").ligerGrid({
				columns : [{
							name : 'nodeid',
							hide : true
						},{
							display : 'IP地址',
							name : 'ipaddress',
							minWidth : 50,
							width : 150
						}, {
							display : '设备名称',
							name : 'hostname',
							minWidth : 100,
							width : 250
						}, {
							display : '操作系统',
							name : 'hostos',
							minWidth : 100,
							width : 250
						}],
				pageSize : 30,
				checkbox : true,
				data : f_getNodeList(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ipaddress',
				width : '99.8%',
				height : '99.5%',
				onReload : function() {
					grid.set({
								data : f_getNodeList()
							});
				},
				// 工具栏
				toolbar : {
					items : [{
								text : '导出',
								click : tbItemclick,
								icon : 'true'
							}]
				},
			});
			
			$("#bt").click(function(){
				grid.set({
					data : f_getNodeList()
				});
			});

		});

// 获取数据方法
function f_getNodeList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostReportAjaxManager.ajax?action=hostchoseList",
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

// 打开新的窗口方法
function openWindows(href, h, w, t) {
	var win = $.ligerDialog.open({
				title : t,
				height : h,
				url : href,
				width : w,
				showMax : true,
				showToggle : true,
				showMin : true,
				isResize : true,
				slide : false
			});
}

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "导出") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择将要导出的设备");
		} else {
			$(rows).each(function() {
						idString += this.nodeid + ";";
					});
			alert(idString);
			var filename = exportHostChoseReport(idString);
			alert(idString);
			openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出word");
		}
	}
}

function exportHostChoseReport(ids){
	alert(ids);
	var waitDlg = $.ligerDialog.waitting("正在生成报表,请稍等...");
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostReportAjaxManager.ajax?action=exportHostChoseReport",
				// 参数
				data : {
					ids : ids,
					startdate : $("#startDate").val(),
					todate : $("#endDate").val()
				},
				dataType : "text",
				success : function(array) {
					if (waitDlg)
						waitDlg.close();
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
				data : f_getNodeList()
			});
}

function itemclick(item, i) {
	refresh();
}
