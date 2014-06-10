var basePath = null;
var grid = null;
var linkData = null;
$(function() {
	basePath = $("#basePath").attr("value");
	linkData = f_getLinkPerformanceList();

	$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
			"like", "equal", "notequal", "greater", "less", "startwith",
			"endwith" ];
	// grid
	grid = $("#linkPerformanceGrid")
			.ligerGrid(
					{
						columns : [
								{
									name : 'nodeid',
									hide : true,
									width : 0.1
								},
								{
									display : '名称',
									name : 'linkname',
									minWidth : 200
								},
								{
									display : '起始设备ip',
									name : 'startip',
									minWidth : 100,
									width : 150
								},
								{
									display : '起始设备端口',
									name : 'startport',
									minWidth : 100,
									width : 100
								},
								{
									display : '终止设备ip',
									name : 'endip',
									minWidth : 80,
									width : 150
								},
								{
									display : '终止设备端口',
									name : 'endport',
									minWidth : 80,
									width : 100
								},
								{
									display : '上行流速(KB/s)',
									columns : [
											{
												display : '流速大小',
												name : 'uplinkspeed',
												minWidth : 60,
												width : 80
											},
											{
												display : 'D',
												minWidth : 20,
												width : 20,
												render : function(item) {
													return toDetail(
															basePath
																	+ 'jsp/performance/linkDetail/linkedutil.jsp?type=Area_Link_flux&line='
																	+ item.nodeid,
															500, 850, "流速详细");
												}
											} ]
								},
								{
									display : '下行流速(%)',
									columns : [
											{
												display : '流速大小',
												name : 'downlinkspeed',
												minWidth : 60,
												width : 80
											},
											{
												display : 'D',
												minWidth : 20,
												width : 20,
												render : function(item) {
													return toDetail(
															basePath
																	+ 'jsp/performance/linkDetail/linkedutil.jsp?type=Area_Link_flux&line='
																	+ item.nodeid,
															500, 850, "流速详细");
												}
											} ]
								},
								{
									display : '可行性(%)',
									columns : [
											{
												display : '大小',
												name : 'pingvalue',
												minWidth : 60,
												width : 60
											},
											{
												display : 'D',
												minWidth : 20,
												width : 20,
												render : function(item) {
													return toDetail(
															basePath
																	+ 'jsp/performance/linkDetail/linkedutil.jsp?type=Line_Link_Ping&line='
																	+ item.nodeid,
															500, 850, "可用性详细");
												}
											} ]
								},
								{
									display : '宽带利用率(%)',
									columns : [
											{
												display : '利用率大小',
												name : 'allspeedrate',
												minWidth : 60,
												width : 80
											},
											{
												display : 'D',
												minWidth : 20,
												width : 20,
												render : function(item) {
													return toDetail(
															basePath
																	+ 'jsp/performance/linkDetail/linkedutil.jsp?type=Area_Link_util&line='
																	+ item.nodeid,
															500, 850, "宽带利用率详细");
												}
											} ]
								} ],
						pageSize : 30,
						checkbox : true,
						data : linkData,
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'linkname',
						width : '99.8%',
						height : '100%',
						heightDiff : -4,
						// 工具栏
						toolbar : {
							items : [ {
								text : '高级自定义查询',
								click : sItemclick,
								icon : 'search2'
							} ]
						}
					});

});

// 获取数据方法
function f_getLinkPerformanceList() {
	var rs = null;
	$
			.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "linkPerformanceAjaxManager.ajax?action=getLinkPerformanceList",
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

// 打开新的窗口方法
function openWindow(href, h, w, t) {
	var win = $.ligerDialog.open({
		title : t,
		height : h,
		url : href,
		width : w,
		slide : false
	});
}
function openWindows(href, h, w, t) {
	// href 转向网页的地址
	// t 网页名称，可为空
	// w 弹出窗口的宽度
	// h 弹出窗口的高度
	// window.screen.height获得屏幕的高，window.screen.width获得屏幕的宽
	var top = (window.screen.height - 30 - h) / 2; // 获得窗口的垂直位置;
	var left = (window.screen.width - 10 - w) / 2; // 获得窗口的水平位置;
	var features = "height="
			+ h
			+ ", width="
			+ w
			+ ",top="
			+ top
			+ ",left="
			+ left
			+ ",toolbar=no,menubar=no,scrollbars=no,resizable=yes,location=no,status=no";
	window.open(href, t, features);
}

// 刷新列表
function refresh() {
	linkData = f_getLinkThresHoldList();
	grid.set({
		data : $.extend(true, {}, linkData)
	});
	f_search();
}

function toDetail(href, h, w, t) {
	var imgString = "<img src='"
			+ basePath
			+ "css/img/pList/pDetail.gif' style='margin-top:5px;' class='pDetail' onclick='openWindow(\""
			+ href + "\"," + h + "," + w + ",\"" + t + "\")' />";
	return imgString;
}

function sItemclick() {
	grid.options.data = $.extend(true, {}, linkData);
	grid.showFilter();
}