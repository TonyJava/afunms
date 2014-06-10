var basePath = null;
var tomcatIds = null;
var iisIds = null;
var weblogicIds = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			tomcatIds = $("#tomcatIds").attr("value");
			iisIds = $("#iisIds").attr("value");
			weblogicIds = $("#weblogicIds").attr("value");
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
							width : 200
						}, {
							display : '设备名称',
							name : 'name',
							minWidth : 150
						}, {
							display : '平均连通率',
							name : 'avg',
							minWidth : 50,
							width : 150
						}, {
							display : '宕机次数',
							name : 'downnum',
							minWidth : 50,
							width : 150
						}],
				pageSize : 30,
				checkbox : false,
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ipaddress',
				width : '99.8%',
				height : '50.5%',
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
			
			setTimeout(f_getPingDetailForMiddleware(),5000);

		});

// 获取数据方法
function f_getPingDetailForMiddleware() {
	var waitDlg = $.ligerDialog.waitting("正在获取数据,请稍等...");
	$.ajax({
			type : "POST",
			async : true,
			url : basePath
					+ "middlewareReportAjaxManager.ajax?action=getPingDetailForMiddleware",
			// 参数
			data : {
				tomcatIds : tomcatIds,
				iisIds : iisIds,
				weblogicIds : weblogicIds,
				startdate : $("#startDate").val(),
				todate : $("#endDate").val()
			},
			dataType : "json",
			success : function(array) {
				if(waitDlg){
					waitDlg.close();
				}
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
}

// 导出word
function f_exportPing(id,type) {
	var rs = "";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "middlewareReportAjaxManager.ajax?action=exportMiddlewarePing" + type,
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
		var filename = f_exportPing("","Word");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 420, 500, "导出word");
	} else if (item.text == "导出excel") {
		var filename = f_exportPing("","Excel");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 420, 500, "导出excel");
	} else if (item.text == "导出pdf") {
		var filename = f_exportPing("","Pdf");
		openWindows(basePath + 'jsp/report/download/download.jsp?filename=' + filename, 420, 500, "导出pdf");
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
	var pingSo = new SWFObject(basePath + "amchart/amline.swf", "ampie","850", "238", "8", "#FFFFFF");
    pingSo.addVariable("path", basePath + "amchart/");
    pingSo.addVariable("settings_file", escape(basePath + "amcharts_settings/netPingReport_settings.xml"));
  	pingSo.addVariable("chart_data",pingChartDivStr);  
 	pingSo.write("pingChartDiv");
}