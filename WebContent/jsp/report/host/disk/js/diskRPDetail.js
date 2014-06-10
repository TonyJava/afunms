var basePath = null;
var ids = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ids = $("#ids").attr("value");
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({});

			// grid
			grid = $("#diskDetailGrid").ligerGrid({
				columns : [{
							display : 'IP地址',
							name : 'ipaddress',
							minWidth : 50,
							width : 100
						}, {
							display : '设备名称',
							name : 'hostname',
							minWidth : 100
						}, {
							display : '操作系统',
							name : 'hostos',
							minWidth : 100,
							width : 100
						},{
							display : '磁盘名称',
							name : 'diskname',
							minWidth : 50,
							width : 100
						}, {
							display : '总大小',
							name : 'total',
							minWidth : 50,
							width : 100
						}, {
							display : '已用大小',
							name : 'use',
							minWidth : 50,
							width : 100
						},{
							display : '利用率',
							name : 'utilization',
							minWidth : 50,
							width : 100
						}],
				pageSize : 30,
				checkbox : false,
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ipaddress',
				width : '99.8%',
				height : '99.5%',
				// 工具栏
				toolbar : {
					items : [{
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
			
			setTimeout(getDiskDetailList(),1000);
		});

// 获取数据方法
function getDiskDetailList() {
	var waitDlg = $.ligerDialog.waitting("正在获取数据,请稍等...");
	$.ajax({
				type : "POST",
				async : true,
				url : basePath
						+ "hostReportAjaxManager.ajax?action=getDiskDetailList",
				// 参数
				data : {
					startdate : $("#startDate").val(),
					todate : $("#endDate").val(),
					ids : ids
				},
				dataType : "json",
				success : function(array) {
					if(waitDlg){
						waitDlg.close();
					}
					grid.set({
						data : array
					});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

// 导出word
function f_exportDisk(method) {
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostReportAjaxManager.ajax?action=" + method,
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
	if (item.text == "导出word") {
		var filename = f_exportDisk("exportDiskWord");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出word");
	} else if (item.text == "导出excel") {
		var filename = f_exportDisk("exportDiskExcel");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出excel");
	} else if (item.text == "导出pdf") {
		var filename = f_exportDisk("exportDiskPdf");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出pdf");
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
