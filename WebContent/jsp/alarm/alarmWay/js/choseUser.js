var grid = null;
var key = null;
var userData = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			userData = f_setUserData();
			key = $("#key").ligerTextBox({});
			grid = $("#userGrid").ligerGrid({
						columns : [{
									name : 'id',
									hide : true,
									width : 0.1
								}, {
									display : '登录ID',
									name : 'userId',
									align : 'left',
									width : 80
								}, {
									display : '描述',
									name : 'name',
									align : 'left'
								}, {
									display : '手机',
									name : 'phone',
									width : 100
								}, {
									display : '邮件',
									name : 'mail',
									width : 180
								}],
						pageSize : 30,
						rownumbers : true,
						allowHideColumn : false,
						 checkbox: true,
						where : f_getWhere(),
						data : $.extend(true, {}, userData),
						width : '100%',
						height : 380,
						onReload : function() {
							userData = f_setUserData();
							grid.set({
										data : $.extend(true, {}, userData)
									});
						}
					});
			$("#key").keyup(function() {
						f_search();
					});

		});
function f_search() {
	grid.options.data = $.extend(true, {}, userData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		return rowdata.userId.indexOf(key) > -1;
	};
	return clause;
}

function f_setUserData() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "alarmWayAjaxManager.ajax?action=choseUser",
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