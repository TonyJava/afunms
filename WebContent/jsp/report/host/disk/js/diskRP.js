var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({});

			// grid
			grid = $("#diskGrid").ligerGrid({
				columns : [{
							name : 'nodeid',
							hide : true
						},{
							display : 'IP地址',
							name : 'ipaddress',
							minWidth : 50,
							width : 150
						}, {
							display : '设备名称',
							name : 'hostname',
							minWidth : 100,
							width : 250
						}, {
							display : '操作系统',
							name : 'hostos',
							minWidth : 100,
							width : 250
						}],
				pageSize : 30,
				checkbox : true,
				data : f_getNodeListForPing(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ipaddress',
				width : '99.8%',
				height : '99.5%',
				onReload : function() {
					grid.set({
								data : f_getNodeListForPing()
							});
				},
				// 工具栏
				// 工具栏
				toolbar : {
					items : [{
								text : '生成报表',
								click : tbItemclick,
								icon : 'true'
							}]
				}
			});
			
			$("#bt").click(function(){
				grid.set({
					data : f_getNodeListForPing()
				});
			});

		});

// 获取数据方法
function f_getNodeListForPing() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostReportAjaxManager.ajax?action=getNodeListForPing",
				// 参数
				data : {
					beginDate : $("#startDate").val(),
					endDate : $("#endDate").val(),
					content : $("#content").val()
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

// 打开新的窗口方法
function openWindows(href, h, w, t) {
	var win = $.ligerDialog.open({
				title : t,
				height : h,
				url : href,
				width : w,
				showMax : true,
				showToggle : true,
				showMin : true,
				isResize : true,
				slide : false
			});
}

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "生成报表") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择将要导出的设备");
		} else {
			$(rows).each(function() {
						idString += this.nodeid + ";";
					});
			openDlgWindow(basePath + 'jsp/report/host/disk/diskRPDetail.jsp?ids=' + idString + '&startDate=' + $("#startDate").val() + '&endDate=' + $("#endDate").val(), 450, 1000);
		}
	}
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getAlarmListByDate()
			});
}

function itemclick(item, i) {
	refresh();
}

function createDiv(pw, value) {
	var text="提示";
	var color="blue";
	if(value==1){
		text="普通";
		color="yellow";
	}else if(value==2){
		text="严重";
		color="orange";
	}else if(value==3){
		text="紧急";
		color="red";
	}
	var divString = "<div style='margin-top:3px;background:"+color+"'><div>"+text+"</div></div>";
	return divString;
}