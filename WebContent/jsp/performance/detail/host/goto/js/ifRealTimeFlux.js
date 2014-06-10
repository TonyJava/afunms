var regenerate_data_interval = 5000;
var delayPrint_time = 500;
var nodeID = null;
var basePath = null;
var datafileName = null;
var ifindex = null;
var ip = null;
var blackFlag = true; // 用来表示是否要生成空文件

$(function() {
			basePath = $("#basePath").attr("value");
			nodeID = $("#nodeId").attr("value");
			ip = $("#ip").attr("value");
			datafileName = $("#fileName").attr("value");
			ifindex = $("#ifindex").attr("value");
			$('#startTime').val(GetNowDate());
			generateData();
			oTimer = window.setInterval('generateData()', regenerate_data_interval);
			window.setTimeout('printChart()', delayPrint_time);

			$("#suspendBtn").click(function() {
						suspendBtn();
					});

			$("#continueBtn").click(function() {
						continueBtn();
					});

			$("#excelBtn").click(function() {
						excelBtn();
					});

			$("#printBtn").click(function() {
						printBtn();
					});

			$("#exitBtn").click(function() {
						exitBtn();
					});

		});

function generateData() {
	DWREngine.setAsync(false);// 设置同步
	PortControler.generateData(datafileName, blackFlag, nodeID, ifindex, {
				callback : callback,// 回调函数
				timeout : 5000,// 超时时间
				errorHandler : function(message) {
					alert("ERROR: " + message);
				}
			});
}
function callback(result) {
	if (null != result && "success" == result) {// 生成文件成功
		blackFlag = false;// 不再生成空文件
	} else {
		// 生成文件失败
		window.clearInterval(oTimer);
		alert("生成数据文件失败:" + result);
	}
}
function printChart() {
	var so = new SWFObject(basePath + "amchart/amline.swf", "amline", "650", "300", "8", "#fffff");
	so.addVariable("path", basePath + "amchart/");
	so.addVariable("settings_file", escape(basePath + "amcharts_settings/port_setting.xml"));
	so.addVariable("data_file", basePath + "amcharts_data/" + datafileName);
	so.write("flashcontent");
}

// 暂停
function suspendBtn() {
	if (oTimer != null) {
		$('#suspendTime').val(GetNowDate());
		window.clearInterval(oTimer);
		oTimer = null;
	}

}
// 继续
function continueBtn() {
	if (oTimer == null) {
		oTimer = window.setInterval('generateData()', regenerate_data_interval);
	}
}
// 导出
function excelBtn() {
	var flashMovie = document.getElementById('amline');
	if (flashMovie) {
		flashMovie.exportImage(basePath + 'armChartServlet?ip=' + ip + '&ifindex=' + ifindex
				+ '&imageName=端口实时监控');
	}
}
// 打印
function printBtn() {
	window.print();
}
// 推出
function exitBtn() {
	this.window.opener = null;
	window.close();
}