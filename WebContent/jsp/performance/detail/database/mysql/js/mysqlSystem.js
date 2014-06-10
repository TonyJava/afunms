var ip = null;
var id = null;
var basePath = null;
$(function() {
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			basePath = $("#basePath").attr("value");
			$("#configDiv").ligerPanel({
						title : '基础信息',
						width : 600,
						height : 335
					});
			$("#flexDiv").ligerPanel({
						title : '连通率曲线图',
						width : 600,
						height : 300
					});
			$("#pingTodayPanel").ligerPanel({
						title : '今天平均连通率',
						width : 200,
						height : 200
					});

			f_getMysqlNodeConfig();
			f_setTodayPerfImg();
			f_getPingLineHistogram();

			$('#configTable tbody tr:odd').css("backgroundColor", "#F0F0F0");

		});

function f_getMysqlNodeConfig() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getMysqlNodeSystem",
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
						$("#version").text(array.Rows[0].version);
						$("#hostOS").text(array.Rows[0].hostOS);
						$("#basePaths").text(array.Rows[0].basePath);
						$("#dataPath").text(array.Rows[0].dataPath);
						$("#logerrorPath").text(array.Rows[0].logerrorPath);
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
	var so = new SWFObject(basePath + "flex/Oracle_Ping.swf?ipadress="+id+"&category=MYPing", "Oracle_Ping", "346", "250", "8", "#ffffff");
	so.write("pingLine");
}