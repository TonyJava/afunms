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
						width : 550,
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
						width : 550,
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
						width : 550,
						height : 250
					});

			// 流速曲线
			$("#fluxLinePanel").ligerPanel({
						title : '流速曲线',
						showToggle : true,
						width : 350,
						height : 250
					});
			// 流速柱状图
			$("#fluxHistogramPanel").ligerPanel({
						title : '流速柱状图',
						showToggle : true,
						width : 550,
						height : 250
					});
			// 内存曲线
			$("#memoryLinePanel").ligerPanel({
						title : '内存曲线',
						showToggle : true,
						width : 550,
						height : 250
					});
			// 内存柱状图
			$("#memoryHistogramPanel").ligerPanel({
						title : '内存柱状图',
						showToggle : true,
						width : 550,
						height : 250
					});

			// 内存曲线
			$("#flashLinePanel").ligerPanel({
						title : '闪存曲线',
						showToggle : true,
						width : 350,
						height : 250
					});
			// 内存柱状图
			$("#flashHistogramPanel").ligerPanel({
						title : '闪存柱状图',
						showToggle : true,
						width : 550,
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

			f_getFlashLine();
			f_getFlashHistogram();
			
			f_getFluxLine();
			f_getFluxHistogram();
		});

function f_getAllPerfData() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "netPerformanceAjaxManager.ajax?action=getAllPerfData",
				// 鍙傛暟
				data : {
					ip : ip
				},
				dataType : "json",
				success : function(array) {
					allPerfData = array;
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
	var href = basePath + "flex/Net_Memory.swf?ipadress=" + ip;
	var so = new SWFObject(href, "Net_Memory", "550", "220", "8", "#ffffff");
	so.write("memoryLine");
}

function f_getPingPie() {
	if (allPerfData.Rows.length > 0) {
		var so = new SWFObject(basePath + "amchart/ampie.swf", "ampie", "180",
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
	if (allPerfData.Rows[0].memoryString!= 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"475", "220", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/netmemory_settings.xml"));
		so.addVariable("chart_data", allPerfData.Rows[0].memoryString);
		so.addVariable("preloader_color", "#999999");
		so.write("memoryHistogram");
	}else {
		$("#memoryHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}

function f_getFlashLine() {
	var href = basePath + "flex/Net_flash_Memory.swf?ipadress=" + ip;
	var so = new SWFObject(href, "Net_flash_Memory", "340", "220", "8", "#ffffff");
	so.write("flashLine");
}

function f_getFlashHistogram() {
	if (allPerfData.Rows[0].flashString!= 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "ampie",
				"475", "220", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/netmemory_settings.xml"));
		so.addVariable("chart_data", allPerfData.Rows[0].flashString);
		so.addVariable("preloader_color", "#999999");
		so.write("flashHistogram");
	}else {
		$("#flashHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}

function f_getFluxLine() {
	var href = basePath + "flex/Area_flux.swf?ipadress=" + ip;
	var so = new SWFObject(href, "Area_flux", "340", "220", "8", "#ffffff");
	so.write("fluxLine");
}

function f_getFluxHistogram() {
	if (allPerfData.Rows[0].fluxString!= 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"475", "220", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/flow_settings.xml"));
		so.addVariable("chart_data", allPerfData.Rows[0].fluxString);
		so.addVariable("preloader_color", "#999999");
		so.write("fluxHistogram");
	}else {
		$("#fluxHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}