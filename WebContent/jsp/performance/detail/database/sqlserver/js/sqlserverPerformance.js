var basePath = null;
var id = null;

var tableSpaceGrid = null;
var hitRateFlexString = "-1";
var logFileFlexString = "-1";
var tableSpaceFlexString = "-1";

$(function() {
	basePath = $("#basePath").attr("value");
	id = $("#id").attr("value");

	$("div.titleDiv").each(function() {
		$(this).click(function() {
			var isShowDiv = $(this).parent().find("div.ContentDiv").eq(0);
			$(isShowDiv).slideToggle(500);
			$(isShowDiv).find(".flexDiv").each(function() {
				liger.get($(this).find("div").attr("id")).reload();
			});
		});
	});

	$(".ContentDiv").ligerPanel({
		showToggle : true,
		width : '100%',
		height : 300
	});

	// grid
	tableSpaceGrid = $("#tableSpaceGrid").ligerGrid({
		columns : [ {
			display : '数据库名称',
			name : 'dbName',
			minWidth : 250
		}, {
			display : '大小(MB)',
			name : 'capacity',
			minWidth : 80
		}, {
			display : '使用(MB)',
			name : 'used',
			minWidth : 80
		}, {
			display : '使用率(%)',
			name : 'usePercent',
			minWidth : 80
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'tableSpaceRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});
	setSqlserverPerformance();
	setHitRateFlex();
	setLogFileFlex();
	setTableSpaceFlex();
});

function setSqlserverPerformance() {
	$
			.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getSqlserverPerformance",
				// 参数
				data : {
					id : id
				},
				dataType : "json",
				success : function(array) {
					if (array) {
						if (array.tableSpaceRows.length > 0) {
							tableSpaceGrid.set({
								data : array
							});
						}
						if (array.hitRateFlexString != "-1") {
							hitRateFlexString = array.hitRateFlexString;
						}
						if (array.tableSpaceFlexString != "-1") {
							tableSpaceFlexString = array.tableSpaceFlexString;
						}
						if (array.logFileFlexString != "-1") {
							logFileFlexString = array.logFileFlexString;
						}
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function setHitRateFlex() {
	if (hitRateFlexString != "-1") {
		var so = new SWFObject(basePath + "/amchart/amcolumn.swf", "ampie",
				"450", "400", "8", "#FFFFFF");
		so.addVariable("path", basePath + "/amchart/");
		so.addVariable("settings_file", escape(basePath
				+ "/amcharts_settings/shootingUtil_settings.xml"));
		so.addVariable("chart_data", hitRateFlexString);
		so.write("hitRateFlex");
	}
}

function setLogFileFlex() {
	if (logFileFlexString != "-1") {
		var so = new SWFObject(basePath + "/amchart/amcolumn.swf", "ampie",
				"450", "400", "8", "#FFFFFF");
		so.addVariable("path", basePath + "/amchart/");
		so.addVariable("settings_file", escape(basePath
				+ "/amcharts_settings/dbpercent_settings.xml"));
		so.addVariable("chart_data", logFileFlexString);
		so.write("logFileFlex");
	}
}

function setTableSpaceFlex() {
	if (tableSpaceFlexString != "-1") {
		var so = new SWFObject(basePath + "/amchart/amcolumn.swf", "ampie",
				"450", "400", "8", "#FFFFFF");
		so.addVariable("path", basePath + "/amchart/");
		so.addVariable("settings_file", escape(basePath
				+ "/amcharts_settings/dbpercent_settings.xml"));
		so.addVariable("chart_data", tableSpaceFlexString);
		so.write("tableSpaceFlex");
	}
}