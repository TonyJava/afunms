var basePath = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						inputWidth : 120,
						labelWidth : 60,
						space : 30,
						fields : [{
									name : "tableSpaceThresHoldId",
									type : "hidden"
								}, {
									display : "表空间",
									name : "tableSpaceName",
									newline : false,
									type : "text"
								}, {
									display : "告警 ",
									name : "isA",
									newline : false,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "isA",
									options : {
										valueField : "value",
										textField : 'name',
										data : isColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "报表 ",
									name : "isRPT",
									newline : true,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "isRPT",
									options : {
										valueField : "value",
										textField : 'name',
										data : isColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "描述",
									name : "remark",
									newline : false,
									type : "text"
								}, {
									display : "阈值(%)",
									name : "alarmValue",
									newline : true,
									type : "text",
									validate : {
										required : true
									},
									group : "阈值",
									groupicon : groupicon
								}],
						buttons : [{
									text : '修改',
									width : 60,
									click : f_save
								}, {
									text : '关闭',
									width : 60,
									click : closeWindows
								}]
					});

			initData();
		});

function initData() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "tableSpaceThresHoldAjaxManager.ajax?action=beforeEditTableSpaceThresHold",
		// 参数
		data : {
			tableSpaceThresHoldId : getUrlParam("tableSpaceThresHoldId")
		},
		dataType : "json",
		success : function(array) {
			liger.get("tableSpaceName").setDisabled();
			$(array.Rows).each(function() {
				var form = liger.get("form");
				form.setData({
							tableSpaceThresHoldId : this.tableSpaceThresHoldId,
							tableSpaceName : this.tableSpaceName,
							remark : this.remark,
							isA : this.isA,
							isRPT : this.isRPT,
							//
							alarmValue : this.alarmValue
						});

				$("#nodeInfo").text(getUrlParam("ip") + "(" + getUrlParam("alias") + ")");
			});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function f_save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "tableSpaceThresHoldAjaxManager.ajax?action=editTableSpaceThresHold",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					tableSpaceThresHoldId : $("#tableSpaceThresHoldId").attr("value"),
					remark : liger.get('remark').getValue(),
					isA : liger.get('isA').getValue(),
					isRPT : liger.get('isRPT').getValue(),
					//
					alarmValue : liger.get('alarmValue').getValue()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								window.parent.refresh();
								closeWindows();

							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}
function closeWindows() {
	var dialog = frameElement.dialog;
	dialog.close();
}

var isColumns = [{
			value : '0',
			name : '否'
		}, {
			value : '1',
			name : '是'
		}];
