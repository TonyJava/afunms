var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({});

			// grid
			grid = $("#pingrpGrid").ligerGrid({
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
				data : f_getNodeListForPing(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ipaddress',
				width : '99.8%',
				height : '99.5%',
				onReload : function() {
					grid.set({
								data : f_getNodeListForPing()
							});
				},
				// 工具栏
				// 工具栏
				toolbar : {
					items : [{
								text : '预览',
								click : tbItemclick,
								icon : 'true'
							}, {
								text : '导出word',
								click : tbItemclick,
								icon : 'word'
							}, {
								text : '导出excel',
								click : tbItemclick,
								icon : 'excel'
							}, {
								text : '导出pdf',
								click : tbItemclick,
								icon : 'pdf'
							}]
				}
			});
			
			$("#bt").click(function(){
				grid.set({
					data : f_getNodeListForPing()
				});
			});

		});

// 获取数据方法
function f_getNodeListForPing() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostReportAjaxManager.ajax?action=getNodeListForPing",
				// 参数
				data : {
					beginDate : $("#startDate").val(),
					endDate : $("#endDate").val()
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

// 导出word
function f_exportPing(id,flag) {
	var waitDlg = $.ligerDialog.waitting("正在生成报表,请稍等...");
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostReportAjaxManager.ajax?action=exportHostPing" + flag,
				// 参数
				data : {
					id : id,
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
	if (item.text == "预览") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择将要预览的设备");
		} else {
			$(rows).each(function() {
				idString += this.nodeid + ";";
			});
			openDlgWindow(basePath + 'jsp/report/host/ping/pingDetail.jsp?ids=' + idString + '&startDate=' + $("#startDate").val() + '&endDate=' + $("#endDate").val(), 500, 950, "预览");
		}

	} else if (item.text == "导出word") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择将要导出的设备");
		} else {
			$(rows).each(function() {
						idString += this.nodeid + ";";
					});
			var filename = f_exportPing(idString,"Word");
			openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出word");
		}
	} else if (item.text == "导出excel") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择将要导出的设备");
		} else {
			$(rows).each(function() {
						idString += this.nodeid + ";";
					});
			var filename = f_exportPing(idString,"Excel");
			openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出word");
		}
	} else if (item.text == "导出pdf") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择将要导出的设备");
		} else {
			$(rows).each(function() {
						idString += this.nodeid + ";";
					});
			var filename = f_exportPing(idString,"Pdf");
			openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出word");
		}
	}
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getAlarmListByDate()
			});
}

function itemclick(item, i) {
	refresh();
}

function createDiv(pw, value) {
	var text="提示";
	var color="blue";
	if(value==1){
		text="普通";
		color="yellow";
	}else if(value==2){
		text="严重";
		color="orange";
	}else if(value==3){
		text="紧急";
		color="red";
	}
	var divString = "<div style='margin-top:3px;background:"+color+"'><div>"+text+"</div></div>";
	return divString;
}