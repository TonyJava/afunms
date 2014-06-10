var basePath = null;
var ip = null;
var allPerfData = null;
var fielGrid = null;
var diskGrid = null;
var pageGrid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");

			f_getFilefData();
			f_getFilePic();
			
			f_getDiskAddPic();
			
			fielGrid = $("#fileLineGrid").ligerGrid({
				columns : [{
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
						}],
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
				columns : [{
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
							display : '平均深度',
							name : 'avque',
							minWidth : 100,
							width : 120
						}, {
							display : '读写块数/秒',
							name : 'r+w/s',
							minWidth : 100,
							width : 120
						}, {
							display : '读写字节(k)/秒',
							name : 'Kbs/s',
							minWidth : 50,
							width : 120
						}, {
							display : '平均等待时间(ms)',
							name : 'avwait',
							minWidth : 50,
							width : 120
						}, {
							display : '平均执行时间(ms)',
							name : 'avserv',
							minWidth : 50,
							width : 123
						}],
				pageSize : 10,
				checkbox : true,
				data : getHostDiskperDetail("aix"),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				//sortName : 'diskName',
				width : '99.8%',
				height : '35%'
			});
			
			pageGrid = $("#pageGrid").ligerGrid({
				columns : [{
							display : '页面调度程序输入/输出列表',
							name : 'pageRe',
							minWidth : 150,
							width : 150
						}, {
							display : '内存页面调进数',
							name : 'pagePi',
							minWidth : 100,
							width : 150
						}, {
							display : '内存页面调出数',
							name : 'pagePo',
							minWidth : 100,
							width : 120
						}, {
							display : '释放的页数',
							name : 'pageFr',
							minWidth : 100,
							width : 120
						}, {
							display : '扫描的页',
							name : 'pageSr',
							minWidth : 50,
							width : 120
						}, {
							display : '时钟周期',
							name : 'pageCy',
							minWidth : 50,
							width : 120
						}],
				pageSize : 10,
				checkbox : true,
				data : getHostAixPageDetail(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				//sortName : 'diskName',
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
	 var so = new SWFObject(basePath + "amchart/amcolumn.swf", "amcolumn","742", "230", "8", "#FFFFFF");
     so.addVariable("path", basePath +  "amchart/");
     so.addVariable("settings_file", escape(basePath + "amcharts_settings/dbpercent_settings.xml"));
     so.addVariable("chart_data", allPerfData.Rows[0].valueStr);
     so.write("fileSystem");
}

function f_getDiskAddPic() {
	 var so = new SWFObject(basePath + "flex/Area_Disk_month.swf?ipadress="+ ip +"&id=2", "Area_Disk_month", "830", "440", "8", "#ffffff");
    so.write("diskAddGrid");
}

function getHostFileSystemDetail() {
	var rs = null;
	$.ajax({
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

function getHostAixPageDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostAixPageDetail",
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


function getHostDiskperDetail(type) {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostDiskperDetail",
				// 鍙傛暟
				data : {
					ip : ip,
					type : type
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

