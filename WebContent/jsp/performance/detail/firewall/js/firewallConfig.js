var ip = null;
var basePath = null;
$(function() {
			ip = $("#ip").attr("value");
			basePath = $("#basePath").attr("value");
			$("#configDiv").ligerPanel({
						title : '基础信息',
						width : 500,
						height : 405
					});
			$("#flexDiv").ligerPanel({
						title : 'TOPN信息',
						width : 920,
						height : 300
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

			f_getFirewallNodeConfig();
			f_setTodayPerfImg();

			$('#configTable tbody tr:odd').css("backgroundColor", "#F0F0F0");

			$(":radio[name='selectIfType']").click(function() {
						f_getSpeedAndBandWidthByType(this.value);
					});

		});

function f_getFirewallNodeConfig() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "netPerformanceAjaxManager.ajax?action=getNetNodeConfig",
				// 参数
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
						$("#location").append(array.Rows[0].location);
						$("#mac").append(array.Rows[0].mac);
						$("#oid").append(array.Rows[0].oid);
						f_getTopNFluxHistogram(array.Rows[0].fluxString);
						f_getTopNBandWidthHistogram(array.Rows[0].bandWidthString);
						f_getKeyPortHistogram(array.Rows[0].keyPortString);
						f_getResponseImg(array.Rows[0].responseTimeAvgInt);
					}

				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});

}

function f_setTodayPerfImg() {
	$("#pingTodayPic").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "pingdata.png>");
	$("#cpuTodayPic").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "cpuavg.png>");
	$("#memoryTodayPic").append("<img  src=" + basePath
			+ "resource/image/jfreechart/reportimg/" + ip + "avgpmemory.png>");
}

function f_getTopNFluxHistogram(value) {
	if (value != 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"288", "250", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/utilhdx_settings.xml"));
		so.addVariable("chart_data", value);
		so.addVariable("preloader_color", "#999999");
		so.write("topNFlux");
	}
}

function f_getTopNBandWidthHistogram(value) {
	if (value != 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"288", "250", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/band_settings.xml"));
		so.addVariable("chart_data", value);
		so.addVariable("preloader_color", "#999999");
		so.write("topNBandWidth");
	}
}

function f_getKeyPortHistogram(value) {
	if (value != 0) {
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
				"288", "250", "8", "#FFFFFF");
		so.addVariable("path", basePath + "amchart/");
		so.addVariable("settings_file", escape(basePath
						+ "amcharts_settings/hostPing_settings.xml"));
		so.addVariable("chart_data", value);
		so.addVariable("preloader_color", "#999999");
		so.write("keyPort");
	} else {
		$("#keyPort").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}

function f_getSpeedAndBandWidthByType(type) {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "netPerformanceAjaxManager.ajax?action=getSpeedAndBandWidthByType",
		// 参数
		data : {
			ip : ip,
			type : type
		},
		dataType : "json",
		success : function(array) {
			f_getTopNFluxHistogram(array.Rows[0].fluxString);
			f_getTopNBandWidthHistogram(array.Rows[0].bandWidthString);

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function f_getResponseImg(value) {
	var imgString = "<img src='"+basePath+"resource/image/chartdirector/0.png'>";
	if (value != "-1") {
		for (var i = 0; i < value.length; i++) {
			imgString=imgString+"<img src='"+basePath+"resource/image/chartdirector/"+value.charAt(i)+".png'>";
		}
		imgString=imgString+"&nbsp;<img src='"+basePath+"resource/image/chartdirector/ms.png'>";
		$("#responseTodayPic").append(imgString);
	} else {
		$("#responseTodayPic").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
	
}