var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#syslogConfigGrid").ligerGrid({
						columns : [{
									name : 'id',
									hide : true,
									width : 0.1
								}, {
									display : '名称',
									name : 'hostname',
									minWidth : 50,
									width : 100
								}, {
									display : 'IP地址',
									name : 'ipaddress',
									minWidth : 100,
									width : 150
								}, {
									display : '型号',
									name : 'type',
									minWidth : 100,
									widht : 150
								}, {
									display : '过滤规则',
									name : 'rules',
									minWidth : 250
								}],
						pageSize : 30,
						checkbox : true,
						data : f_getSyslogConfigListByDate(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'level',
						width : '99.8%',
						height : '100%',
						heightDiff :-4,
						onReload : function() {
							grid.set({
										data : f_getSyslogConfigListByDate()
									});
						},
						// 工具栏
						toolbar : {
							items : [{
										text : '批量设置',
										click : tbItemclick,
										href : basePath + 'jsp/alarm/syslog/syslogConfigEditAll.jsp?ids=',
										icon : 'modify'
									}]
						},

						// 双击
						onDblClickRow : function(data, rowindex, rowobj) {
							var url = null;
							url = basePath + "jsp/alarm/syslog/syslogConfigEdit.jsp?id=" + data.id;
							url = encodeURI(url);
							url = encodeURI(url);
							openWindow(url, 450, 540, "syslog详细");
						}
					});

		});

// 获取数据方法
function f_getSyslogConfigListByDate() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "syslogAjaxManager.ajax?action=getSyslogConfigListByDate",

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

function openWindow(href, h, w, t) {
	var win = $.ligerDialog.open({
				title : t,
				height : h,
				url : href,
				width : w,
				slide : false
			});
}

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "批量设置") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请至少选择一条设置项");
		} else {
			$(rows).each(function() {
						idString += this.id + ";";
					});
			openDlgWindow(item.href+idString, 220, 520, item.text + "过滤规则");
		}
	} else if (item.text == "查询") {
		refresh();
	}
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getSyslogConfigListByDate()
			});
}

function itemclick(item, i) {
	refresh();
}

function f_modifySyslogRule(idString){
	
}
