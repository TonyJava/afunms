var basePath = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						inputWidth : 170,
						labelWidth : 70,
						labelAlign :'right' ,
						space : 40,
						fields : [{
									name : "indicatorId",
									type : "hidden"
								}, {
									name : "nodeId",
									type : "hidden"
								}, {
									name : "type",
									type : "hidden"
								}, {
									name : "subType",
									type : "hidden"
								}, {
									name : "isDefault",
									type : "hidden"
								}, {
									name : "classPath",
									type : "hidden"
								}, {
									display : "名称",
									name : "indicatorName",
									newline : true,
									type : "text",
									group : "采集指标信息",
									groupicon : groupicon
								}, {
									display : "别名",
									name : "indicatorAlias",
									newline : false,
									type : "text",
									validate : {
										required : true
									}
								}, {
									display : "是否采集 ",
									name : "isC",
									newline : true,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "isC",
									options : {
										valueField : "value",
										textField : 'name',
										data : isCColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "间隔 ",
									name : "interval",
									newline : false,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "interval",
									options : {
										valueField : "interval",
										textField : 'intervalName',
										data : intervalColumns
									}
								}, {
									display : "指标种类",
									name : "category",
									newline : true,
									type : "text",
									validate : {
										required : true
									}
								}, {
									display : "描述",
									name : "remark",
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
		url : basePath + "indicatorAjaxManager.ajax?action=beforeEditIndicator",
		// 参数
		data : {
			indicatorId : getUrlParam("indicatorId")
		},
		dataType : "json",
		success : function(array) {
			liger.get("indicatorName").setDisabled();
			$(array.Rows).each(function() {
						var form = liger.get("form");
						form.setData({
									indicatorId : this.indicatorId,
									nodeId : this.nodeId,
									type : this.type,
									subType : this.subType,
									isDefault : this.isDefault,
									classPath : this.classPath,
									indicatorName : this.indicatorName,
									indicatorAlias : this.indicatorAlias,
									isC : this.isC,
									interval : this.interval,
									category : this.category,
									remark : this.remark
								});
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
						+ "indicatorAjaxManager.ajax?action=editIndicator",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					indicatorId : $("#indicatorId").attr("value"),
					nodeId : $("#nodeId").attr("value"),
					type : $("#type").attr("value"),
					subType : $("#subType").attr("value"),
					isDefault : $("#isDefault").attr("value"),
					classPath : $("#classPath").attr("value"),
					indicatorName : liger.get('indicatorName').getValue(),
					indicatorAlias : liger.get('indicatorAlias').getValue(),
					isC : liger.get('isC').getValue(),
					interval : liger.get('interval').getValue(),
					category : liger.get('category').getValue(),
					remark : liger.get('remark').getValue()
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

var intervalColumns = [{
			interval : '30:s',
			intervalName : "30秒"
		}, {
			interval : '1:m',
			intervalName : "1分钟"
		}, {
			interval : '2:m',
			intervalName : "2分钟"
		}, {
			interval : '3:m',
			intervalName : "3分"
		}, {
			interval : '4:m',
			intervalName : "4分钟"
		}, {
			interval : '5:m',
			intervalName : "5分钟"
		}, {
			interval : '10:m',
			intervalName : "10分钟"
		}, {
			interval : '30:m',
			intervalName : "30分钟"
		}, {
			interval : '1:h',
			intervalName : "1小时"
		}, {
			interval : '4:h',
			intervalName : "4小时"
		}, {
			interval : '8:h',
			intervalName : "8小时"
		}, {
			interval : '12:h',
			intervalName : "12小时"
		}, {
			interval : '1:d',
			intervalName : "1天"
		}];

var isCColumns = [{
			value : '0',
			name : '否'
		}, {
			value : '1',
			name : '是'
		}];