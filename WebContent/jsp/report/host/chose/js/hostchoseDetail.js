var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({});

			// grid
			gridrtGrid = $("hostchoseDetail").ligerGrid({
				columns : [{
					display : 'IP地址',
					name : 'ipaddress',
					width : 30
				}, {
					display : '连通率(%)',
					columns : [{
								display : '平均',
								name : 'avgping',
								minWidth : 60,
								width : 80
							}, {
								display : '最小',
								name : 'minping',
								minWidth : 20,
								width : 20
							}]
				}, {
					display : 'CPU(%)',
					columns : [{
								display : '平均',
								name : 'avgcpu',
								minWidth : 60,
								width : 80
							}, {
								display : '最大',
								name : 'mincpu',
								minWidth : 20,
								width : 20
							}]
				}, {
					display : '物理内存(%)',
					columns : [{
								display : '平均',
								name : 'avgmem',
								minWidth : 60,
								width : 80
							}, {
								display : '最大',
								name : 'minmem',
								minWidth : 20,
								width : 20
							}]
				}, {
					display : '磁盘TOP',
					name : 'disk',
					width : 30
				}, {
					display : '事件(个)',
					columns : [{
								display : '普通',
								name : 'one',
								minWidth : 60,
								width : 80
							}, {
								display : '严重',
								name : 'two',
								minWidth : 20,
								width : 20
							}, {
								display : '紧急',
								name : 'three',
								minWidth : 20,
								width : 20
							}]
				}],
				data : {
					Rows : []
				},
				enabledEdit : true,
				width : '100%',
				usePager : false,
				checkbox : true,
				rownumbers : true,
				rowHeight : 24
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
						+ "hostReportAjaxManager.ajax?action=hostchoceList",
				data : {
					content : $("#content").val()
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
			var filename = exportHostChoseReport(idString);
			//openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出word");
			openDlgWindow(basePath + 'jsp/report/host/chose/hostchoseDetail.jsp?filename=' + filename, 450, 1000,"主机决策");
		}
	}
}

function exportHostChoseReport(ids){
	var waitDlg = $.ligerDialog.waitting("正在生成报表,请稍等...");
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostReportAjaxManager.ajax?action=exportHostChoceReport",
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
