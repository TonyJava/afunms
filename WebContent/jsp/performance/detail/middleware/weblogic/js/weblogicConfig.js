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
						height : 180
					});

			$("#pingTodayPanel").ligerPanel({
						title : '今天平均连通率',
						width : 200,
						height : 200
					});

			f_getWeblogicConfig();

			$('#configTable tbody tr:odd').css("backgroundColor", "#F0F0F0");

		});

function f_getWeblogicConfig() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "weblogicPerformanceAjaxManager.ajax?action=getWeblogicConfig",
		// 参数
		data : {
			ip : ip,
			nodeId : nodeId
		},
		dataType : "json",
		success : function(array) {
			if (array.Rows.length > 0) {
				$("#serverRuntimeName").text(array.Rows[0].serverRuntimeName);
				$("#serverRuntimeListenAddress")
						.text(array.Rows[0].serverRuntimeListenAddress);
				$("#serverRuntimeListenPort")
						.text(array.Rows[0].serverRuntimeListenPort);
				$("#RunOpenSocketsCurCount")
						.text(array.Rows[0].RunOpenSocketsCurCount);
				$("#serverRuntimeState").text(array.Rows[0].serverRuntimeState);
				$("#domainName").text(array.Rows[0].domainName);
				$("#domainAdministrationPort")
						.text(array.Rows[0].domainAdministrationPort);
				$("#domainConfigurationVersion")
						.text(array.Rows[0].domainConfigurationVersion);
				f_getPingPie(array.Rows[0].avgPingString);
			}

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});

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
