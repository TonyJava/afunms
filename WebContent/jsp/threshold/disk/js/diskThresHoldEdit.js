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
		fields : [ {
			name : "diskThresHoldId",
			type : "hidden"
		}, {
			display : "磁盘",
			name : "diskName",
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
			display : "描述",
			name : "remark",
			newline : false,
			type : "text"
		}, {
			display : "阈值",
			name : "firstLevelValue",
			newline : true,
			type : "text",
			validate : {
				required : true
			},
			group : "一级阈值",
			groupicon : groupicon
		}, {
			display : "短信 ",
			name : "firstIsSM",
			newline : false,
			type : "select",
			validate : {
				required : true
			},
			comboboxName : "firstIsSM",
			options : {
				valueField : "value",
				textField : 'name',
				data : isColumns,
				selectBoxHeight : 50
			}
		}, {
			display : "阈值",
			name : "secondLevelValue",
			newline : true,
			type : "text",
			validate : {
				required : true
			},
			group : "二级阈值",
			groupicon : groupicon
		}, {
			display : "短信 ",
			name : "secondIsSM",
			newline : false,
			type : "select",
			validate : {
				required : true
			},
			comboboxName : "secondIsSM",
			options : {
				valueField : "value",
				textField : 'name',
				data : isColumns,
				selectBoxHeight : 50
			}
		}, {
			display : "阈值",
			name : "thirdLevelValue",
			newline : true,
			type : "text",
			validate : {
				required : true
			},
			group : "三级阈值",
			groupicon : groupicon
		}, {
			display : "短信 ",
			name : "thirdIsSM",
			newline : false,
			type : "select",
			validate : {
				required : true
			},
			comboboxName : "thirdIsSM",
			options : {
				valueField : "value",
				textField : 'name',
				data : isColumns,
				selectBoxHeight : 50
			}
		} ],
		buttons : [ {
			text : '修改',
			width : 60,
			click : f_save
		}, {
			text : '关闭',
			width : 60,
			click : closeWindow
		} ]
	});

	initData();
});

function initData() {
	$
			.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "diskThresHoldAjaxManager.ajax?action=beforeEditDiskThresHold",
				// 参数
				data : {
					diskThresHoldId : getUrlParam("diskThresHoldId")
				},
				dataType : "json",
				success : function(array) {
					liger.get("diskName").setDisabled();
					$(array.Rows).each(
							function() {
								var form = liger.get("form");
								form.setData({
									diskThresHoldId : this.diskThresHoldId,
									diskName : this.diskName,
									remark : this.remark,
									isA : this.isA,
									isRPT : this.isRPT,
									//
									firstLevelValue : this.firstLevelValue,
									firstIsSM : this.firstIsSM,
									//
									secondLevelValue : this.secondLevelValue,
									secondIsSM : this.secondIsSM,
									//
									thirdLevelValue : this.thirdLevelValue,
									thirdIsSM : this.thirdIsSM
								});

								$("#nodeInfo").text(
										this.ip + "("
												+ $("#alias").attr("value")
												+ ")");
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
				+ "diskThresHoldAjaxManager.ajax?action=editDiskThresHold",
		// 参数
		contentType : "application/x-www-form-urlencoded; charset=UTF-8",
		data : {
			diskThresHoldId : $("#diskThresHoldId").attr("value"),
			remark : liger.get('remark').getValue(),
			isA : liger.get('isA').getValue(),
			isRPT : liger.get('isRPT').getValue(),
			//
			firstLevelValue : liger.get('firstLevelValue').getValue(),
			firstIsSM : liger.get('firstIsSM').getValue(),
			//
			secondLevelValue : liger.get('secondLevelValue').getValue(),
			secondIsSM : liger.get('secondIsSM').getValue(),
			//
			thirdLevelValue : liger.get('thirdLevelValue').getValue(),
			thirdIsSM : liger.get('thirdIsSM').getValue()
		},
		dataType : "text",
		success : function(array) {
			$.ligerDialog.success(array, '提示', function(yes) {
				// 刷新列表
				window.parent.refresh();
				closeWindow();

			});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}
function closeWindow() {
	var dialog = frameElement.dialog;
	dialog.close();
}

var isColumns = [ {
	value : '0',
	name : '否'
}, {
	value : '1',
	name : '是'
} ];
