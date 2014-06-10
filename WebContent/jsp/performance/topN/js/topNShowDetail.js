var basePath = null;
// 类型
var categoryManager = null;
// top数
var countManager = null;
// 日期类型(实时、日、周、月)
var dateType = null;

var startTime = null;
var toTime = null;
var bid = null;
var w = null;
var h = null;

$(function() {
			w = $(window).width()-50;
			h = $(window).height();
			basePath = $("#basePath").attr("value");
			dateType = getUrlParam("dateType");
			// 获取时间和业务
			getDateAndBid();
			$("div.divHead").each(function() {
				$(this).click(function() {
					var flagId = $(this).attr("id");
					var isShowDiv = $(this).parent().find("div").eq(1);
					$(isShowDiv).slideToggle(500, function() {
						$(".l-panel-content").css("height",
								$(".l-panel-content").css("height"));
						if ($(this).is(":show")) {
							if (flagId == "pingAndResponseTime") {
								getPingFlex();
								getResponseTimeFlex();
							} else if (flagId == "cpuAndMemory") {
								getCpuFlex();
								getMemoryFlex();
							} else if (flagId == "ifFlux") {
								getInFluxFlex();
								getOutFluxFlex();
							} else if (flagId == "ifBandwidth") {
								getInBandwidthFlex();
								getOutBandwidthFlex();
							} else if (flagId == "disk") {
								getDiskFlex();
							}
						}
					});
				});
			});
			categoryManager = $('#topNCategory').ligerComboBox({
						valueField : 'id',
						textField : 'text',
						onselected:function (value){
							if (value == 1) {
								$("#disk").parent().css("display","none");
							}
						}
					});
			countManager = $('#topNCount').ligerComboBox({
						valueField : 'id',
						textField : 'text'
					});
			categoryManager.setData(categoryData);
			categoryManager.selectValue("4");
			countManager.setData(countData);
			countManager.selectValue("5");

			// 连通曲线
			$("#pingPanel").ligerPanel({
						title : '连通率',
						showToggle : true,
						width : w/2,
						height : 250
					});
			// 响应时间
			$("#responseTimePanel").ligerPanel({
						title : '响应时间',
						showToggle : true,
						width : w/2,
						height : 250
					});

			// CPU
			$("#cpuPanel").ligerPanel({
						title : 'CPU利用率',
						showToggle : true,
						width : w/2,
						height : 250
					});

			// 内存
			$("#memoryPanel").ligerPanel({
						title : '内存利用率',
						showToggle : true,
						width : w/2,
						height : 250
					});

			// 入口流速
			$("#inFluxPanel").ligerPanel({
						title : '入口流速',
						showToggle : true,
						width : w/2,
						height : 250
					});

			// 出口流速
			$("#outFluxPanel").ligerPanel({
						title : '出口流速',
						showToggle : true,
						width : w/2,
						height : 250
					});

			// 入口带宽
			$("#inBandwidthPanel").ligerPanel({
						title : '入口带宽',
						showToggle : true,
						width : w/2,
						height : 250
					});

			// 出口带宽
			$("#outBandwidthPanel").ligerPanel({
						title : '出口带宽',
						showToggle : true,
						width : w/2,
						height : 250
					});

			// 磁盘
			$("#diskPanel").ligerPanel({
						title : '磁盘利用率',
						showToggle : true,
						width : w/2,
						height : 250
					});
		
		});

// 方法定义
// ping
function getPingFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Network_Ping_TOPN.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=ping&bids=" + bid, "Column_Network_Ping_TOPN",
			"99%", "240", "8", "#ffffff");
	so.write("pingFlex");
}
// 响应时间
function getResponseTimeFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Network_Ping_TOPN.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=ResponseTime&bids=" + bid,
			"Column_Network_Response_TOPN", "99%", "240", "8", "#ffffff");
	so.write("responseTimeFlex");
}

// CPU
function getCpuFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Network_Ping_TOPN.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=cpu&bids=" + bid, "Column_Network_Cpu_TOPN",
			"99%", "240", "8", "#ffffff");
	so.write("cpuFlex");
}

// 内存
function getMemoryFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Network_Ping_TOPN.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=memory&bids=" + bid, "Column_Network_Memory_TopN",
			"99%", "240", "8", "#ffffff");
	so.write("memoryFlex");
}
// 入口流速
function getInFluxFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Network_Ping_TOPN.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=inutil&bids=" + bid, "Column_Network_Inutil_TOPN",
			"99%", "240", "8", "#ffffff");
	so.write("inFluxFlex");
}
// 出口流速
function getOutFluxFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Network_Ping_TOPN.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=oututil&bids=" + bid,
			"Column_Network_Oututil_TOPN", "99%", "240", "8", "#ffffff");
	so.write("outFluxFlex");
}
// 入口带宽
function getInBandwidthFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Network_Ping_TOPN.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=inutilper&bids=" + bid,
			"Column_Network_Inutilper_TOPN", "99%", "240", "8", "#ffffff");
	so.write("inBandwidthFlex");
}
// 出口带宽
function getOutBandwidthFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Network_Ping_TOPN.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=oututilper&bids=" + bid,
			"Column_Network_oututilper_TOPN", "99%", "240", "8", "#ffffff");
	so.write("outBandwidthFlex");
}

// 磁盘
function getDiskFlex() {
	var category = categoryManager.getValue();
	var count = countManager.getValue();
	var so = new SWFObject(basePath + "flex/Column_Host_Disk_TOP10.swf?topn="
					+ count + "&reporttype=" + dateType + "&b_time="
					+ startTime + "&t_time=" + toTime + "&category=" + category
					+ "&type=disk&bids=" + bid, "Column_Host_Disk_TOP10",
			"99%", "240", "8", "#ffffff");
	so.write("diskFlex");
}

function getDateAndBid() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "topNPerformancedAjaxManager.ajax?action=getDateAndBid",
				// 参数
				data : {
					dateFlag : dateType
				},
				dataType : "json",
				success : function(array) {
					if (array.Rows.length > 0) {
						startTime = array.Rows[0].startTime;
						toTime = array.Rows[0].toTime;
						bid = array.Rows[0].bid;
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});

}

var categoryData = [{
			id : '1',
			text : '网络设备'
		}, {
			id : '4',
			text : '服务器'
		}];

var countData = [{
			id : '5',
			text : '5'
		}, {
			id : '10',
			text : '10'
		}, {
			id : '20',
			text : '20'
		}, {
			id : '30',
			text : '30'
		}];
