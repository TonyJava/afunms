var basePath = null;
var ids = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ids = $("#ids").attr("value");

			// 设置SysLog等级列表
			$("#sysLogCheckBoxList").ligerCheckBoxList({
						rowSize : 9,
						data : sysLogLevel,
						textField : 'name'
					});

			$("#submit").click(function() {
						updateAll();
					});

		});

// 功能函数定义 update
function updateAll() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "syslogAjaxManager.ajax?action=updateSyslogConfigAll",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					ids : ids,
					sysLogLevels : liger.get("sysLogCheckBoxList").getValue()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								window.parent.refresh();
								closeDlgWindow();

							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function refreshParent() {
	window.opener.refresh();
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getSyslogAlarmListByDate()
			});
}

function itemclick(item, i) {
	refresh();
}

var sysLogLevel = [{
			id : 0,
			name : '紧急'
		}, {
			id : 1,
			name : '报警'
		}, {
			id : 2,
			name : '关键'
		}, {
			id : 3,
			name : '错误'
		}, {
			id : 4,
			name : '警告'
		}, {
			id : 5,
			name : '通知'
		}, {
			id : 6,
			name : '提示'
		}, {
			id : 7,
			name : '调试'
		}];