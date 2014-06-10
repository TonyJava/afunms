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
			grid = $("#pingdetailGrid").ligerGrid({
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
							minWidth : 100,
							width : 150
						}, {
							display : '数据库名称',
							name : 'dbname',
							minWidth : 100
						},{
							display : '数据库应用',
							name : 'dbuse',
							minWidth : 50,
							width : 80
						}, {
							display : '平均连通率',
							name : 'avg',
							minWidth : 50,
							width : 80
						}, {
							display : '宕机次数',
							name : 'downnum',
							minWidth : 50,
							width : 80
						}],
				pageSize : 30,
				checkbox : false,
				//data : f_getPingDetailForHost(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ipaddress',
				width : '99.8%',
				height : '40.5%',
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
								line : true
							}, {
								text : '导出pdf',
								click : tbItemclick,
								icon : 'pdf'
							}]
				}
			});
			
			f_getPingDetailForHost();

		});

// 获取数据方法
function f_getPingDetailForHost() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "databaseReportAjaxManager.ajax?action=getPingDetailForDatabase",
				// 参数
				data : {
					ids : ids,
					startdate : $("#startDate").val(),
					todate : $("#endDate").val()
				},
				dataType : "json",
				success : function(array) {
					if (array) {
						$.each(array, function(n, value) {
									createChart(this.pic[0].pingChartDivStr);
									if (this.node) {
										grid.set({
													data : this.node[0]
												});
										
									}
								});
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}

// 导出word
function f_exportPing(id,flag) {
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "databaseReportAjaxManager.ajax?action=exportDatabasePing" + flag,
				// 参数
				data : {
					id : id,
					startdate : $("#startDate").val(),
					todate : $("#endDate").val()
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
		var filename = f_exportPing("create","Word");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 420, 500, "导出word");
	} else if (item.text == "导出excel") {
		var filename = f_exportPing("create","Excel");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 420, 500, "导出word");
	} else if (item.text == "导出pdf") {
		var filename = f_exportPing("create","Pdf");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 420, 500, "导出word");
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

function createChart(pingChartDivStr){
	//画连通率曲线
	var pingSo = new SWFObject(basePath + "amchart/amline.swf", "ampie","750", "238", "8", "#FFFFFF");
    pingSo.addVariable("path", basePath + "amchart/");
    pingSo.addVariable("settings_file", escape(basePath + "amcharts_settings/netPingReport_settings.xml"));
  	pingSo.addVariable("chart_data",pingChartDivStr);  
 	pingSo.write("pingChartDiv");
}