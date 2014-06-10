var grid = null;
var id = null;
var basePath = null;

$(function() {
			basePath = $("#basePath").attr("value");
			$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
					"like", "equal", "notequal", "greater", "less", "startwith", "endwith"];

			// 右键菜单项
			contextMenu = $.ligerMenu({
						width : 120,
						items : [{
									text : '启用',
									click : contextMenuItemClick,
									icon : 'ok'
								}, {
									text : '停用',
									click : contextMenuItemClick,
									icon : 'cancle'
								}]
					});

			// grid
			grid = $("#emailGrid").ligerGrid({
				columns : [{
							name : 'id',
							hide : true,
							width : 0.1
						}, {
							display : '用户名',
							name : 'username',
							width : 150
						}, {
							display : 'SMTP(IP)',
							name : 'smtp',
							minWidth : 150
						}, {
							display : '是否启用',
							name : 'usedflag',
							width : 200
						}],
				pageSize : 30,
				checkbox : true,
				data : listEmail(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'username',
				width : '99.8%',
				height : '100%',
				heightDiff :-4,
				onReload : function() {
					grid.set({
								data : listEmail()
							});
				},
				// 工具栏
				toolbar : {
					items : [{
								text : '高级自定义查询',
								click : sItemclick,
								icon : 'search2'
							}, {
								text : '添加',
								click : itemclick,
								href : basePath + 'jsp/systemConfig/email/emailAdd.jsp',
								icon : 'add'
							}, {
								text : '删除',
								click : itemclick,
								icon : 'delete'
							}]
				},

				// 右键菜单
				onContextmenu : function(parm, e) {
					id = parm.data.id;
					ifName = parm.data.ifName;
					contextMenu.show({
								top : e.pageY,
								left : e.pageX
							});
					return false;
				},

				// 双击
				onDblClickRow : function(data, rowindex, rowobj) {
					openDlgWindow(basePath + "jsp/systemConfig/email/emailEdit.jsp?id=" + data.id,
							250, 520, "邮箱修改");
				}
			});
		});

function contextMenuItemClick(item, i) {
	if (item.text == "启用") {
		$.ajax({
					type : "POST",
					async : false,
					url : basePath + "emailAjaxManager.ajax?action=addAlertEmail",
					// 参数
					data : {
						id : id
					},
					dataType : "text",
					success : function(array) {
						$.ligerDialog.success(array);
						refresh();
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert(errorThrown);
					}
				});
	} else if (item.text == "停用") {
		$.ajax({
					type : "POST",
					async : false,
					url : basePath + "emailAjaxManager.ajax?action=cancelAlertEmail",
					// 参数
					data : {
						id : id
					},
					dataType : "text",
					success : function(array) {
						$.ligerDialog.success(array);
						refresh();
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert(errorThrown);
					}
				});
	}
}

function sItemclick() {
	grid.options.data = $.extend(true, {}, listEmail());
	grid.showFilter();
}

function listEmail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "emailAjaxManager.ajax?action=getEmailList",
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
// 刷新列表
function refresh() {
	grid.set({
				data : listEmail()
			});
}

function itemclick(item) {
	if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.id + ";";
					});
			$.ligerDialog.success(f_deleteEmail(idString));
		}
	} else if (item.text == "添加") {
		openDlgWindow(item.href, 250, 520, item.text + "邮箱");
	}
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

function f_deleteEmail(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "emailAjaxManager.ajax?action=deleteEmail",
				// 参数
				data : {
					idString : string
				},
				dataType : "text",
				success : function(array) {
					// 成功删除则更新表格行
					refresh();
					grid.deleteSelectedRow();
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}
