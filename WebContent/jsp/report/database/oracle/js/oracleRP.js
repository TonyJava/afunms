var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({});

			// grid
			grid = $("#oracleGrid").ligerGrid({
				columns : [{
							name : 'nodeid',
							hide : true,
							width : 0.1
						},{
							display : 'IP地址',
							name : 'ipaddress',
							minWidth : 50,
							width : 150
						}, {
							display : '数据库类型',
							name : 'dbtype',
							minWidth : 150
						}, {
							display : '数据库名称',
							name : 'dbname',
							minWidth : 100,
							width : 200
						}, {
							display : '数据库应用',
							name : 'dbuse',
							minWidth : 100,
							width : 200
						}, {
							display : 'EXCEL综合',
							name : 'multiExcel',
							minWidth : 50,
							width : 80,
							render : function(item) {
								return toDetail(basePath
												+ 'jsp/report/download/download.jsp?filename=', 300, 850, "EXCEL综合","excel","6",item.ipaddress);
							}
						}, {
							display : 'WORD综合',
							name : 'multiWord',
							minWidth : 50,
							width : 80,
							render : function(item) {
								return toDetail(basePath
												+ 'jsp/report/download/download.jsp?filename=', 300, 850, "WORD综合","word","5",item.ipaddress);
							}
						}, {
							display : 'PDF综合',
							name : 'multiPdf',
							minWidth : 50,
							width : 80,
							render : function(item) {
								return toDetail(basePath
												+ 'jsp/report/download/download.jsp?filename=', 300, 850, "PDF综合","pdf","7",item.ipaddress);
							}
						}, {
							display : 'WORD分析',
							name : 'analyseWord',
							minWidth : 50,
							width : 80,
							render : function(item) {
								return toDetail(basePath
												+ 'jsp/report/download/download.jsp?filename=', 300, 850, "WORD分析","word","3",item.ipaddress);
							}
						}, {
							display : 'PDF分析',
							name : 'analysePDF',
							minWidth : 50,
							width : 80,
							render : function(item) {
								return toDetail(basePath
												+ 'jsp/report/download/download.jsp?filename=', 300, 850, "PDF分析","pdf","4",item.ipaddress);
							}
						}],
				pageSize : 30,
				checkbox : true,
				data : f_getOracleList(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ipaddress',
				width : '99.8%',
				height : '99.5%',
				onReload : function() {
					grid.set({
								data : f_getOracleList()
							});
				},
				// 工具栏
				toolbar : {
					items : [{
								text : '生成综合报表',
								click : tbItemclick,
								icon : 'true'
							}]
				}
			});
			
			$("#bt").click(function(){
				grid.set({
					data : f_getOracleList()
				});
			});

		});

// 获取数据方法
function f_getOracleList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "databaseReportAjaxManager.ajax?action=getNodeListForPing",
				// 参数
				data : {
					type : "oracle",
					beginDate : $("#startDate").val(),
					endDate : $("#endDate").val(),
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

function f_exportDatabaseReportForOracle(id) {
	var waitDlg = $.ligerDialog.waitting("正在生成报表,请稍等...");
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "databaseReportAjaxManager.ajax?action=exportDatabaseReportForOracle",
				// 参数
				data : {
					ids : id,
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
	if (item.text == "生成综合报表") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择将要导出的设备");
		} else {
			$(rows).each(function() {
						idString += this.nodeid + ";";
					});
			var filename = f_exportDatabaseReportForOracle(idString);
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

function toDetail(href, h, w, t, type, str, ip) {
	//var filename = exportHostReportByType(type,str);
	var imgString = "<img src='"
			+ basePath
			+ "css/icons/export_" + type + ".gif' style='margin-top:5px;' class='pDetail' onclick='exportOracleReportByType(\"" + type + "\",\"" + str + "\",\"" + ip +"\")' />";
	return imgString;
}

function exportOracleReportByType(type,str,ip) {
	var waitDlg = $.ligerDialog.waitting("正在生成报表,请稍等...");
	var filename = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "databaseReportAjaxManager.ajax?action=exportOracleReportForType",
				// 参数
				data : {
					str : str,
					ipaddress : ip,
					type : "host",
					startdate : $("#startDate").val(),
					todate : $("#endDate").val()
				},
				dataType : "text",
				success : function(array) {
					if (waitDlg)
						waitDlg.close();
					filename = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 520, 900, "导出word");
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