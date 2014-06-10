var basePath = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						inputWidth : 120,
						labelWidth : 90,
						labelAlign : 'right',
						space : 30,
						fields : [{
									name : "interfaceThresHoldId",
									type : "hidden"
								}, {
									display : "接口名称",
									name : "interfaceName",
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
									display : "报表显示 ",
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
									display : "短信 ",
									name : "isSM",
									newline : false,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "isSM",
									options : {
										valueField : "value",
										textField : 'name',
										data : isColumns,
										selectBoxHeight : 50
									}
								}, {
									display : '描述',
									name : 'remark',
									newline : true,
									type : "text"
								}, {
									display : "出口流速阈值",
									name : "outAlarmVlaue",
									newline : true,
									type : "text",
									validate : {
										required : true
									},
									group : "阈值",
									groupicon : groupicon
								}, {
									display : "入口流速阈值",
									name : "inAlarmVlaue",
									newline : false,
									type : "text",
									validate : {
										required : true
									}
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
				+ "interfaceThresHoldAjaxManager.ajax?action=beforeEditInterfaceThresHold",
		// 参数
		data : {
			interfaceThresHoldId : getUrlParam("interfaceThresHoldId")
		},
		dataType : "json",
		success : function(array) {
			liger.get("interfaceName").setDisabled();
			$(array.Rows).each(function() {
				var form = liger.get("form");
				form.setData({
							interfaceThresHoldId : this.interfaceThresHoldId,
							interfaceName : this.interfaceName,
							remark : this.remark,
							isA : this.isA,
							isRPT : this.isRPT,
							isSM : this.isSM,
							//
							outAlarmVlaue : this.outAlarmVlaue,
							inAlarmVlaue : this.inAlarmVlaue
						});

				$("#nodeInfo").text(this.ip + "(" + $("#alias").attr("value") + ")");
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
				+ "interfaceThresHoldAjaxManager.ajax?action=editInterfaceThresHold",
		// 参数
		contentType : "application/x-www-form-urlencoded; charset=UTF-8",
		data : {
			interfaceThresHoldId : $("#interfaceThresHoldId").attr("value"),
			remark : liger.get('remark').getValue(),
			isA : liger.get('isA').getValue(),
			isRPT : liger.get('isRPT').getValue(),
			//
			outAlarmVlaue : liger.get('outAlarmVlaue').getValue(),
			inAlarmVlaue : liger.get('inAlarmVlaue').getValue(),
			//
			isSM : liger.get('isSM').getValue()
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
