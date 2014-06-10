var basePath = null;
var ip = null;
var allPerfData = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");

			$("div.divHead").each(function() {
				$(this).click(function() {
					var isShowDiv = $(this).parent().find("div").eq(1);
					$(isShowDiv).slideToggle(500);
					$(".l-panel-content").css("height",
							$(".l-panel-content").css("height"))
				});
			});
			// 连通曲线
			$("#pingLinePanel").ligerPanel({
						title : '连通曲线',
						showToggle : true,
						width : 350,
						height : 250
					});

			// 连通饼图
			$("#pingPiePanel").ligerPanel({
						title : '连通饼图',
						width : 500,
						height : 250
					});

			// 响应时间曲线
			$("#responseTimeLinePanel").ligerPanel({
						title : '响应时间曲线',
						showToggle : true,
						width : 350,
						height : 250
					});

			// 响应时间饼图
			$("#responseTimePiePanel").ligerPanel({
						title : '响应时间饼图',
						showToggle : true,
						width : 500,
						height : 250
					});
			// CPU曲线图
			$("#cpuLinePanel").ligerPanel({
						title : 'CPU曲线图',
						showToggle : true,
						width : 350,
						height : 250
					});
			// CPU饼图
			$("#cpuPiePanel").ligerPanel({
						title : 'CPU饼图',
						showToggle : true,
						width : 500,
						height : 250
					});
			// 内存曲线
			$("#memoryLinePanel").ligerPanel({
						title : '内存曲线',
						showToggle : true,
						width : 350,
						height : 250
					});
			// 内存柱状图
			$("#memoryHistogramPanel").ligerPanel({
						title : '内存柱状图',
						showToggle : true,
						width : 500,
						height : 250
					});

			// 内存曲线
			$("#diskLinePanel").ligerPanel({
						title : '磁盘曲线',
						showToggle : true,
						width : 350,
						height : 250
					});
			// 内存柱状图
			$("#diskHistogramPanel").ligerPanel({
						title : '磁盘柱状图',
						showToggle : true,
						width : 500,
						height : 250
					});
			
			// CPU信息曲线
			$("#cpuxinxiLinePanel").ligerPanel({
						title : 'CPU信息曲线',
						showToggle : true,
						width : 350,
						height : 250
					});
			// CPU信息柱状图
			$("#cpuxinxiHistogramPanel").ligerPanel({
						title : '磁盘柱状图',
						showToggle : true,
						width : 500,
						height : 250
					});
			
			// 换页曲线图
			$("#swapLinePanel").ligerPanel({
						title : '换页率曲线图',
						showToggle : true,
						width : 350,
						height : 250
					});
			// 换页饼图
			$("#swapPiePanel").ligerPanel({
						title : '换页率饼图',
						showToggle : true,
						width : 500,
						height : 250
					});
			f_getPingLine();
			f_getResponseTimeLine();
			f_getCpuLine();
			f_getMemoryLine();
			f_getAllPerfData();

			f_getPingPie();
			f_responseTimePieImg();
			f_cpuPieImg();
			f_getMemoryHistogram();

			f_getDiskLine();
			f_getDiskHistogram();
			
			f_getCpuxinxiLine();
			f_getCpuxinxiHistogram();

			f_getSwapLine();
			f_swapPieImg();
			
			//f_cpuxinxiDetail();
		});

function f_getAllPerfData() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getAixAllPerfData",
				// 鍙傛暟
				data : {
					ip : ip
				},
				dataType : "json",
				success : function(array) {
					allPerfData = array;
					if(array.Rows.length > 0){
						$("#totalSwap").text(array.Rows[1].totalSwap);
						$("#currSwap").text(array.Rows[1].currSwap);
						$("#avgSwap").text(array.Rows[1].avgSwap);
						$("#maxSwap").text(array.Rows[1].maxSwap);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function f_getPingLine() {
	var href = basePath + "flex/Ping.swf?ipadress=" + ip;
	var so = new SWFObject(href, "Ping", "340", "220", "8", "#ffffff");
	so.write("pingLine");
}

function f_responseTimePieImg() {
	$("#responseTimePie").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "response.png>");
}

function f_getResponseTimeLine() {
	var href = basePath + "flex/Response_time.swf?ipadress=" + ip;
	var so = new SWFObject(href, "Response_time", "340", "220", "8", "#ffffff");
	so.write("responseTimeLine");
}

function f_getCpuLine() {
	var href = basePath + "flex/Line_CPU.swf?ipadress=" + ip;
	var so = new SWFObject(href, "Line_CPU", "340", "220", "8", "#ffffff");
	so.write("cpuLine");
}

function f_cpuPieImg() {
	$("#realCpuPie").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "cpu.png>");
	$("#maxCpuPie").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "cpumax.png>");
	$("#avgCpuPie").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "cpuavg.png>");
}

function f_getMemoryLine() {
	var href = basePath + "flex/Area_Memory.swf?ipadress=" + ip;
	var so = new SWFObject(href, "Area_flux", "340", "220", "8", "#ffffff");
	so.write("memoryLine");
}

function f_getPingPie() {
	if (allPerfData.Rows.length > 0) {
		var so = new SWFObject(basePath + "amchart/ampie.swf", "ampie", "160",
				"155", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/pingStatepie.xml"));
		so.addVariable("chart_data", allPerfData.Rows[0].pingPercentString);
		so.write("realPingPie");

		so.addVariable("chart_data", allPerfData.Rows[0].pingMaxString);
		so.write("maxPingPie");

		so.addVariable("chart_data", allPerfData.Rows[0].pingAvgString);
		so.write("avgPingPie");
	}
}

function f_getMemoryHistogram() {
	if (allPerfData.Rows.length > 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"475", "220", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/memorypercent_settings.xml"));
		so.addVariable("chart_data", allPerfData.Rows[0].memoryString);
		so.addVariable("preloader_color", "#999999");
		so.write("memoryHistogram");
	}
}

function f_getDiskLine() {
	var href = basePath + "flex/Area_Disk.swf?ipadress=" + ip;
	var so = new SWFObject(href, "Area_Disk", "340", "220", "8", "#ffffff");
	so.write("diskLine");
}

function f_getDiskHistogram() {
	if (allPerfData.Rows.length > 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"475", "220", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/cpuUtilPercent_settings.xml"));
		so.addVariable("chart_data", allPerfData.Rows[0].diskString);
		so.addVariable("preloader_color", "#999999");
		so.write("diskHistogram");
	}
}

function f_getCpuxinxiLine() {
	var href = basePath + "flex/Cpu_detail.swf?ipadress=" + ip;
	var so = new SWFObject(href, "CPU详细", "346", "210", "8", "#ffffff");
	so.write("cpuxinxiLine");
}

function f_getCpuxinxiHistogram() {
	if (allPerfData.Rows.length > 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"485", "210", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/cpuUtilPercent_settings.xml"));
		so.addVariable("chart_data", allPerfData.Rows[0].cpuxinxiString);
		so.addVariable("preloader_color", "#999999");
		so.write("cpuxinxiHistogram");
	}
}

function f_getSwapLine() {
	var href = basePath + "flex/Line.swf?ipadress=" + ip;
	var so = new SWFObject(href, "PageSpace", "346", "250", "8", "#ffffff");
	so.write("swapLine");
}

function f_swapPieImg() {
	$("#realSwapPie").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "pageout.png>");
	$("#maxSwapPie").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "pageoutmax.png>");
	$("#avgSwapPie").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "pageoutavg.png>");
}