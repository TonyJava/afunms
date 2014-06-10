var basePath = null;
var ip = null;
var allPerfData = null;
var fielGrid = null;
var diskGrid = null;
$(function() {
	basePath = $("#basePath").attr("value");
	ip = $("#ip").attr("value");

	$("div.divHead").each(function() {
		$(this).click(function() {
			var isShowDiv = $(this).parent().find("div").eq(1);
			$(isShowDiv).slideToggle(500);
			liger.get($(isShowDiv).attr("id")).reload();
		});
	});

	f_getFilefData();
	f_getFilePic();

	fielGrid = $("#fileLineGrid").ligerGrid({
		columns : [ {
			display : '文件系统名',
			name : 'fileName',
			minWidth : 180,
			width : 230
		}, {
			display : '总容量',
			name : 'allSize',
			minWidth : 300,
			width : 350
		}, {
			display : '已用容量',
			name : 'usedSize',
			minWidth : 300,
			width : 350
		}, {
			display : '利用率',
			name : 'utilization',
			minWidth : 300,
			width : 350
		} ],
		pageSize : 10,
		checkbox : true,
		data : getHostFileSystemDetail(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'allSize',
		width : '99.8%',
		height : '35%'

	});

	diskGrid = $("#diskperGrid").ligerGrid({
		columns : [ {
			display : '磁盘名',
			name : 'diskName',
			minWidth : 150,
			width : 150
		}, {
			display : '繁忙(%)',
			name : 'busy',
			minWidth : 100,
			width : 150
		}, {
			display : '传输字节/秒',
			name : 'tps',
			minWidth : 100,
			width : 120
		}, {
			display : '读字节/秒',
			name : 'rd_sec',
			minWidth : 100,
			width : 120
		}, {
			display : '写字节/秒',
			name : 'wr_sec',
			minWidth : 50,
			width : 120
		}, {
			display : '平均请求队列',
			name : 'avgrq',
			minWidth : 50,
			width : 120
		}, {
			display : '平均服务队列',
			name : 'avgqu',
			minWidth : 50,
			width : 120
		}, {
			display : '等待时间(s)',
			name : 'await',
			minWidth : 50,
			width : 120
		}, {
			display : '服务时间(s)',
			name : 'svctm',
			minWidth : 50,
			width : 123
		}, {
			display : 'CPU占用',
			name : 'util',
			minWidth : 50,
			width : 125
		} ],
		pageSize : 10,
		checkbox : true,
		data : getHostDiskperDetail(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		// sortName : 'diskName',
		width : '99.8%',
		height : '35%'
	});

});

function f_getFilefData() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "hostPerformanceAjaxManager.ajax?action=getHostFileDetail",
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

function f_getFilePic() {
	var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn",
			"742", "230", "8", "#FFFFFF");
	so.addVariable("path", basePath + "amchart/");
	so.addVariable("settings_file", escape(basePath
			+ "amcharts_settings/dbpercent_settings.xml"));
	so.addVariable("chart_data", allPerfData.Rows[0].valueStr);
	so.write("fileSystem");
}

function getHostFileSystemDetail() {
	var rs = null;
	$
			.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostFileSystemDetail",
				// 鍙傛暟
				data : {
					ip : ip
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

function getHostDiskperDetail() {
	var rs = null;
	$
			.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostDiskperDetail",
				// 鍙傛暟
				data : {
					ip : ip
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
