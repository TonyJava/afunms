var ip = null;
var nodeId = null;
var basePath = null;
$(function() {
			ip = $("#ip").attr("value");
			nodeId = $("#nodeId").attr("value");
			basePath = $("#basePath").attr("value");
			$("#configDiv").ligerPanel({
						title : '基础信息',
						width : 1000,
						height : 480
					});

			$("#pingTodayPanel").ligerPanel({
						title : '今天平均连通率',
						width : 200,
						height : 200
					});
			$("#jvmTodayPanel").ligerPanel({
						title : 'JVM平均利用率',
						width : 200,
						height : 200
					});

			f_getTomcatConfig();
			f_setTodayPerfImg();

			$('#configTable tbody tr:odd').css("backgroundColor", "#F0F0F0");

		});

function f_getTomcatConfig() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "tomcatPerformanceAjaxManager.ajax?action=getTomcatConfig",
		// 参数
		data : {
			ip : ip,
			nodeId : nodeId
		},
		dataType : "json",
		success : function(array) {
			if (array.Rows.length > 0) {
				$("#serverInfo").text(array.Rows[0].serverInfo);
				$("#implementationVersion")
						.text(array.Rows[0].implementationVersion);
				$("#vmVendor").text(array.Rows[0].vmVendor);
				$("#vmNameVer").text(array.Rows[0].vmNameVer);
				$("#startTime").text(array.Rows[0].startTime);
				$("#upTime").text(array.Rows[0].upTime);
				$("#operatingSystemName")
						.text(array.Rows[0].operatingSystemName);
				$("#totalPhysicalMemorySizeL")
						.text(array.Rows[0].totalPhysicalMemorySizeL);
				$("#freePhysicalMemorySizeL")
						.text(array.Rows[0].freePhysicalMemorySizeL);
				$("#totalSwapSpaceSizeL")
						.text(array.Rows[0].totalSwapSpaceSizeL);
				$("#freeSwapSpaceSizeL").text(array.Rows[0].freeSwapSpaceSizeL);
				$("#committedVirtualMemorySizeL")
						.text(array.Rows[0].committedVirtualMemorySizeL);
				$("#arch").text(array.Rows[0].arch);
				$("#availableProcessors")
						.text(array.Rows[0].availableProcessors);
				$("#processCpuTime").text(array.Rows[0].processCpuTime);
				$("#ThreadCount").text(array.Rows[0].ThreadCount);
				$("#PeakThreadCount").text(array.Rows[0].PeakThreadCount);
				$("#DaemonThreadCount").text(array.Rows[0].DaemonThreadCount);
				$("#TotalStartedThreadCount")
						.text(array.Rows[0].TotalStartedThreadCount);
				$("#loadedClassCount").text(array.Rows[0].loadedClassCount);
				$("#totalLoadedClassCount")
						.text(array.Rows[0].totalLoadedClassCount);
				$("#unloadedClassCount").text(array.Rows[0].unloadedClassCount);
				$("#heapUsedMemoryL").text(array.Rows[0].heapUsedMemoryL);
				$("#heapCommitMemoryL").text(array.Rows[0].heapCommitMemoryL);
				$("#heapMaxMemoryL").text(array.Rows[0].heapMaxMemoryL);
				$("#collectorString").text(array.Rows[0].collectorString);
				f_getPingPie(array.Rows[0].avgPingString);
			}

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});

}
function f_setTodayPerfImg() {
	$("#jvmTodayPic").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "tomcat_jvm.png>");
}

function f_getPingPie(value) {
	if (value != 0) {
		var so = new SWFObject(basePath + "amchart/ampie.swf", "ampie", "165",
				"155", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/pingStatepie.xml"));
		so.addVariable("chart_data", value);
		so.write("pingTodayPie");
	} else {
		$("#pingTodayPie").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}
