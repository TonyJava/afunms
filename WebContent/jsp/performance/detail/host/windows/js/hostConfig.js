var ip = null;
var basePath = null;
$(function() {
			ip = $("#ip").attr("value");
			basePath = $("#basePath").attr("value");
			$("#configDiv").ligerPanel({
						title : '基础信息',
						width : 550,
						height : 405
					});

			$("#flexDiv").ligerPanel({
						title : '性能信息',
						width : 1004,
						height : 250
					});
			$("#pingTodayPanel").ligerPanel({
						title : '今天平均连通率',
						width : 200,
						height : 200
					});
			$("#responseTodayPanel").ligerPanel({
						title : '今天平均响应时间',
						width : 200,
						height : 200
					});
			$("#cpuTodayPanel").ligerPanel({
						title : 'CPU今天平均利用率',
						width : 200,
						height : 200
					});
			$("#memoryTodayPanel").ligerPanel({
						title : '内存今天平均利用率',
						width : 200,
						height : 200
					});

			f_getHostNodeConfig();
			f_setTodayPerfImg();

			$('#configTable tbody tr:odd').css("backgroundColor", "#F0F0F0");

		});

function f_getHostNodeConfig() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostNodeConfig",
				// 鍙傛暟
				data : {
					ip : ip
				},
				dataType : "json",
				success : function(array) {
					if (array.Rows.length > 0) {
						$("#nodeIp").text(array.Rows[0].nodeIp);
						$("#nodeAlias").text(array.Rows[0].nodeAlias);
						$("#nodeSysName").text(array.Rows[0].nodeSysName);
						$("#type").text(array.Rows[0].type);
						$("#subType").text(array.Rows[0].subType);
						$("#sysUpTime").text(array.Rows[0].sysUpTime);
						$("#sysDescr").val(array.Rows[0].sysDescr);
						$("#ctTime").text(array.Rows[0].ctTime);
						$("#physicalMemory").append(createDiv(180,
								array.Rows[0].physicalMemoryRate,
								array.Rows[0].physicalMemoryCap));
						$("#virtualMemory").append(createDiv(180,
								array.Rows[0].virtualMemoryRate,
								array.Rows[0].virtualMemoryCap));
						f_getResponseImg(array.Rows[0].responseTimeAvgInt);
						f_getLastWeekCpuHistogram(array.Rows[0].lastWeekCpuString);
						f_getMemoryHistogram(array.Rows[0].memoryString);
						f_getDiskHistogram(array.Rows[0].diskString);
					}

				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});

}
function createDiv(pw, value, text) {
	var divString = "<div style='float:left;margin-top:5px;width:"
			+ pw
			+ "px;border:1px solid green;'><div style='height:12px;background:green;width:"
			+ value + "%;'></div></div>" + value + "% 总" + text;
	return divString;
}

function f_setTodayPerfImg() {
	$("#pingTodayPic").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "pingdata.png>");
	$("#cpuTodayPic").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "cpuavg.png>");
	$("#memoryTodayPic").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "avgpmemory.png>");
}

function f_getResponseImg(value) {
	var imgString = "<img src='" + basePath
			+ "resource/image/chartdirector/0.png'>";
	if (value != "-1") {
		for (var i = 0; i < value.length; i++) {
			imgString = imgString + "<img src='" + basePath
					+ "resource/image/chartdirector/" + value.charAt(i)
					+ ".png'>";
		}
		imgString = imgString + "&nbsp;<img src='" + basePath
				+ "resource/image/chartdirector/ms.png'>";
		$("#responseTodayPic").append(imgString);
	} else {
		$("#responseTodayPic").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}

function f_getLastWeekCpuHistogram(value) {
	if (value != 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"330", "220", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/hostcpu_settings.xml"));
		so.addVariable("chart_data", value);
		so.write("cpuLastWeek");
	} else {
		$("#cpuLastWeek").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}

function f_getMemoryHistogram(value) {
	if (value != null) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"330", "220", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/hostmemory_settings.xml"));
		so.addVariable("chart_data", value);
		so.addVariable("preloader_color", "#999999");
		so.write("memoryHistogram");
	} else {
		$("#memoryHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}

function f_getDiskHistogram(value) {
	if (value != null) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"330", "220", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/hostdisk_settings.xml"));
		so.addVariable("chart_data", value);
		so.addVariable("preloader_color", "#999999");
		so.write("diskHistogram");
	} else {
		$("#diskHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}
