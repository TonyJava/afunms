var ip = null;
var id = null;
var contrGrid = null;
var logGrid = null;
var keepObj = null;
var basePath = null;
$(function() {
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			basePath = $("#basePath").attr("value");
			
			$("div.divHead").each(function() {
				$(this).click(function() {
							var isShowDiv = $(this).parent()
									.find("div").eq(1);
							$(isShowDiv).slideToggle(500);
							liger.get($(isShowDiv).attr("id")).reload();
						});
			});
			
			$("#configDiv").ligerPanel({
						title : '基础信息',
						width : '49.8%',
						height : 335
					});
			$("#pingTodayPanel").ligerPanel({
						title : '今天平均连通率',
						width : 300,
						height : 250
					});
			$("#dbdataPanel").ligerPanel({
				title : '命中率',
				width : 320,
				height : 335
			});
			
			$("#pgaPanel").ligerPanel({
				title : 'PGA信息详情',
				width : 320,
				height : 335
			});
			
			$("#sgaPanel").ligerPanel({
				title : 'SGA信息详情',
				width : 320,
				height : 335
			});
			
			$("#spacePanel").ligerPanel({
				title : '表空间使用率',
				width : 320,
				height : 335
			});
			
			$("#pingLinePanel").ligerPanel({
				title : '连通率曲线图',
				width : 350,
				height : 335
			});
			
			$("#contrPanel").ligerPanel({
				title : '控制文件状态',
				width : '99.8%',
				height : 200
			});
			
			$("#logPanel").ligerPanel({
				title : '日志文件状态',
				width : '99.8%',
				height : 200
			});
			
			$("#keepobjPanel").ligerPanel({
				title : '固定缓存对象',
				width : '99.8%',
				height : 300
			});
			f_getOracleSystemDetail();
			f_setTodayPerfImg();
			f_getPingLineHistogram();
			
			contrGrid = $("#contrGrid").ligerGrid({
				columns : [{
						display : '名称',
						name : 'contrName',
						minWidth : 200,
						width : 500
					},
					{
						display : '状态',
						name : 'contrStatus',
						minWidth : 40,
						width : 150
					}],
				pageSize : 15,
				checkbox : false,
				data : f_getOracleFileDetail("contr"),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'contrName',
				width : '99.8%',
				height : '30%'

			});
			logGrid = $("#logGrid").ligerGrid({
				columns : [{
						display : 'group',
						name : 'group',
						minWidth : 50,
						width : 90
					},
					{
						display : '状态',
						name : 'logstatus',
						minWidth : 40,
						width : 90
					},
					{
						display : '类型',
						name : 'logtype',
						minWidth : 40,
						width : 100
					},
					{
						display : '名称',
						name : 'logname',
						minWidth : 150,
						width : 350
					}],
				pageSize : 15,
				checkbox : false,
				data : f_getOracleFileDetail("log"),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'group',
				width : '99.8%',
				height : '30%'

			});
			keepObjGrid = $("#keepobjGrid").ligerGrid({
				columns : [{
						display : '所有者',
						name : 'owner',
						minWidth : 50,
						width : 200
					},
					{
						display : '名称',
						name : 'name',
						minWidth : 80
					},
					{
						display : 'NAMESPACE',
						name : 'namespace',
						minWidth : 80
					},
					{
						display : '类型',
						name : 'type',
						minWidth : 80
					},
					{
						display : 'SHARABLE_MEM',
						name : 'sharable_mem',
						minWidth : 40,
						width : 150
					},
					{
						display : 'PINS',
						name : 'pins',
						minWidth : 40,
						width : 100
					},
					{
						display : 'KEPT',
						name : 'kept',
						minWidth : 40,
						width : 100
					}],
				pageSize : 15,
				checkbox : false,
				data : f_getOracleFileDetail("keepobj"),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'owner',
				width : '99.8%',
				height : '45%'

			});
			$('#configTable tbody tr:odd').css("backgroundColor", "#F0F0F0");

		});


function f_getOracleFileDetail(type){
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "dbPerformanceAjaxManager.ajax?action=getOracleFileDetail",
		// 参数
		data : {
			id : id,
			ip : ip,
			type : type
		},
		dataType : "json",
		success : function(array) {
			rs =  array;
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
	return rs;
}

function f_getOracleSystemDetail() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleSystem",
				// 参数
				data : {
					id : id
				},
				dataType : "json",
				success : function(array) {
					if (array.Rows.length > 0) {
						$("#alias").text(array.Rows[0].alias);
						$("#dbname").text(array.Rows[0].dbname);
						$("#dbtype").text(array.Rows[0].dbtype);
						$("#ipaddress").text(array.Rows[0].ipaddress);
						$("#port").text(array.Rows[0].port);
						$("#managed").text(array.Rows[0].managed);
						$("#status").text(array.Rows[0].status);
						$("#lstrnStatu").text(array.Rows[0].lstrnStatu);
						$("#hostname").text(array.Rows[0].hostname);
						$("#DBname").text(array.Rows[0].DBname);
						$("#version").text(array.Rows[0].version);
						$("#instancename").text(array.Rows[0].instancename);
						$("#instancestatus").text(array.Rows[0].instancestatus);
						$("#startup_time").text(array.Rows[0].startup_time);
						$("#archiver").text(array.Rows[0].archiver);
						$("#created").text(array.Rows[0].created);
						$("#memsql").text(array.Rows[0].memsql);
						$("#opencurstr").text(array.Rows[0].opencurstr);
						$("#curconnectstr").text(array.Rows[0].curconnectstr);
						f_getDbdataHistogram(array.Rows[0].dbdata);
						f_getSgaHistogram(array.Rows[0].sgadata);
						f_getPgaHistogram(array.Rows[0].pgadata);
						f_getSpaceHistogram(array.Rows[0].tabledata);
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
}


function f_getPingLineHistogram() {
	var so = new SWFObject(basePath + "flex/Oracle_Ping.swf?ipadress="+id+"&category=ORAPing", "Oracle_Ping", "335", "300", "8", "#ffffff");
	so.write("pingLine");
}

function f_getDbdataHistogram(value){
	if(value != null){
     var so = new SWFObject(basePath + "amchart/amcolumn.swf", "ampie","250", "278", "8", "#FFFFFF");
     so.addVariable("path", basePath + "amchart/");
     so.addVariable("settings_file", escape(basePath + "amcharts_settings/shootingUtil_settings.xml"));
     so.addVariable("chart_data", value);
     so.write("dbdataHistogram");
	} else {
		$("#dbdataHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
}

function f_getPgaHistogram(value){
	if(value != null){
		 var so = new SWFObject(basePath + "amchart/amcolumn.swf", "ampie","300", "278", "8", "#FFFFFF");
         so.addVariable("path", basePath + "amchart/");
         so.addVariable("settings_file", escape(basePath + "amcharts_settings/dbmemory_settings.xml"));
         so.addVariable("chart_data", value);
        so.write("pgaHistogram");
	} else {
		$("#pgaHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
	
}

function f_getSgaHistogram(value){
	if(value != null){
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "ampie","260", "278", "8", "#FFFFFF");
        so.addVariable("path", basePath + "amchart/");
        so.addVariable("settings_file", escape(basePath + "amcharts_settings/dbmemory_settings.xml"));
        so.addVariable("chart_data", value);
        so.write("sgaHistogram");
	} else {
		$("#sgaHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
	
}

function f_getSpaceHistogram(value){
	if(value != null){
		var so = new SWFObject(basePath + "amchart/amcolumn.swf", "ampie","265", "278", "8", "#FFFFFF");
        so.addVariable("path", basePath + "amchart/");
        so.addVariable("settings_file", escape(basePath + "amcharts_settings/dbpercent_settings.xml"));
        so.addVariable("chart_data", value);
        so.write("spaceHistogram");
	} else {
		$("#spaceHistogram").append("<img alt='没有数据' src=" + basePath
				+ "resource/image/nodata.gif>");
	}
	
}