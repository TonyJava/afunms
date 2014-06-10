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
							minWidth : 100,
							width : 150
						}, {
							display : '数据库类型',
							name : 'dbtype',
							minWidth : 100,
							width : 100
						}, {
							display : '数据库名称',
							name : 'dbname',
							minWidth : 140
						}, {
							display : '数据库应用',
							name : 'dbuse',
							minWidth : 100,
							width : 100
						},{
							display : '事件总数',
							name : 'sum',
							minWidth : 50,
							width : 80
						}, {
							display : '普通',
							name : 'levelone',
							minWidth : 50,
							width : 80
						}, {
							display : '紧急',
							name : 'leveltwo',
							minWidth : 50,
							width : 80
						},{
							display : '严重',
							name : 'levelthree',
							minWidth : 50,
							width : 80
						}, {
							display : '服务器不可用次数',
							name : 'downnum',
							minWidth : 50,
							width : 100
						}],
				pageSize : 30,
				checkbox : true,
				data : getEventDetailList(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ipaddress',
				width : '99.8%',
				height : '99.5%',
				onReload : function() {
					grid.set({
								data : getEventDetailList()
							});
				},
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
		});

// 获取数据方法
function getEventDetailList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "databaseReportAjaxManager.ajax?action=getEventDetailList",
				// 参数
				data : {
					beginDate : $("#startDate").val(),
					endDate : $("#endDate").val(),
					ids : ids
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
function f_exportEvent(method) {
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "databaseReportAjaxManager.ajax?action=" + method,
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