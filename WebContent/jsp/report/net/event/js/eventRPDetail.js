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
			grid = $("#eventDetailGrid").ligerGrid({
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
							display : '事件总数',
							name : 'sum',
							minWidth : 50,
							width : 100
						}, {
							display : '普通',
							name : 'levelone',
							minWidth : 50,
							width : 100
						}, {
							display : '紧急',
							name : 'leveltwo',
							minWidth : 50,
							width : 100
						},{
							display : '严重',
							name : 'levelthree',
							minWidth : 50,
							width : 100
						}, {
							display : '连通率事件',
							name : 'pingvalue',
							minWidth : 50,
							width : 100
						}, {
							display : 'CPU事件',
							name : 'cpuvalue',
							minWidth : 50,
							width : 100
						}, {
							display : '端口事件',
							name : 'portvalue',
							minWidth : 50,
							width : 100
						}, {
							display : '流速事件',
							name : 'utilvalue',
							minWidth : 50,
							width : 100
						}, {
							display : '内存事件',
							name : 'memvalue',
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
						text : '导出pdf',
						click : tbItemclick,
						icon : 'pdf'
					}]
				}
			});
			
			setTimeout(getEventDetailList(),5000);
		});

// 获取数据方法
function getEventDetailList() {
	var waitDlg = $.ligerDialog.waitting("正在获取数据,请稍等...");
	$.ajax({
				type : "POST",
				async : true,
				url : basePath
						+ "netReportAjaxManager.ajax?action=getEventDetailList",
				// 参数
				data : {
					beginDate : $("#startDate").val(),
					endDate : $("#endDate").val(),
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
function f_exportEvent(method) {
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "netReportAjaxManager.ajax?action=" + method,
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
		var filename = f_exportEvent("exportEventWord");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出word");
	} else if (item.text == "导出excel") {
		var filename = f_exportEvent("exportEventExcel");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出excel");
	} else if (item.text == "导出pdf") {
		var filename = f_exportEvent("exportEventPdf");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出excel");
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