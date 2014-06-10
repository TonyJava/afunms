var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("form").ligerForm();

			// 设置采集方式下拉值
			liger.get('collecttype').set({
						data : dbCollectType,
						valueField : 'type',
						textField : 'typeName'
					});
			liger.get('collecttype').setValue('1');

			liger.get('managed').set({
						data : managed,
						valueField : 'value',
						textField : 'name'
					});
			liger.get('managed').setValue('1');

			// 设置类型下拉值
			liger.get('dbtype').set({
						data : dbType,
						valueField : 'value',
						textField : 'name',
						onSelected : function(value) {
							onTypeChangedForNodeAdd(value);
						}
					});

			// 设置业务选择框
			liger.get('bid').set({
						width : 200,
						nullText : "请选择业务",
						onButtonClick : function() {
							openWindow(basePath + 'jsp/common/bidTree.jsp', 300, 300, "业务列表");
						}

					});

			$("#submit").click(function() {
						save();
					});

			$("#closeWindow").click(function() {
						window.close();
					});

		});

// 功能函数定义

function save() {
	var waitDlg = $.ligerDialog.waitting("正在添加,请等待...");
	$.ajax({
				type : "POST",
				async : true,
				url : basePath + "dbPerformanceAjaxManager.ajax?action=addDb",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					ip_address : liger.get('ip_address').getValue(),
					alias : liger.get('alias').getValue(),
					db_name : liger.get('db_name').getValue(),
					dbtype : liger.get('dbtype').getValue(),
					user : liger.get('user').getValue(),
					password : liger.get('password').getValue(),
					port : liger.get('port').getValue(),
					dbuse : liger.get('dbuse').getValue(),
					bid : $("#bidValue").val(),
					managed : liger.get("managed").getValue(),
					collecttype : liger.get("collecttype").getValue()
				},
				dataType : "text",
				success : function(array) {
					if (waitDlg)
						waitDlg.close();
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								refreshParent();
								window.close();

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
// 初始值定义
var managed = [{
			value : '1',
			name : '是'
		}, {
			value : '0',
			name : '否'
		}];

var dbCollectType = [{
			type : '1',
			typeName : 'JDBC'
		}, {
			type : '2',
			typeName : '脚本'
		}];

var dbType = [{
			value : '1',
			name : 'oracle'
		}, {
			value : '2',
			name : 'SQLServer'
		}, {
			value : '4',
			name : 'MySql'
		}];

function onTypeChangedForNodeAdd(type) {
	var categoryvalue = "";
	var portvalue = "";
	if (type == 1) {
		categoryvalue = 53;
		portvalue = 1521;
	} else if (type == 2) {
		categoryvalue = 54;
		portvalue = 1433;
	} else if (type == 4) {
		categoryvalue = 52;
		portvalue = 3306;
	} else if (type == 5) {
		categoryvalue = 59;
		portvalue = 50000;
	} else if (type == 6) {
		categoryvalue = 55;
		portvalue = 2638;
	} else if (type == 7) {
		categoryvalue = 60;
		portvalue = 9088;
	}
	$("#category").attr("value", categoryvalue);
	liger.get("port").setValue(portvalue);
}