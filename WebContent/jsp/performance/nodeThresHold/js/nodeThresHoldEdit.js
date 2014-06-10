var basePath = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						inputWidth : 170,
						labelWidth : 90,
						space : 40,
						fields : [{
									name : "thresHoldId",
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
									display : "名称",
									name : "thresHoldName",
									newline : true,
									type : "text",
									group : "阈值信息",
									groupicon : groupicon
								}, {
									display : "是否启用 ",
									name : "thresHoldIsE",
									newline : false,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "thresHoldIsE",
									options : {
										valueField : "value",
										textField : 'name',
										data : isEColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "描述",
									name : "remark",
									newline : true,
									type : "text"
								}, {
									display : "告警描述",
									name : "alarmInfo",
									newline : false,
									type : "text",
									validate : {
										required : true
									}
								}, {
									display : "比较方式 ",
									name : "thresHoldCompare",
									newline : true,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "thresHoldCompare",
									options : {
										valueField : "value",
										textField : "name",
										data : thresHoldCompareColumns,
										selectBoxHeight : 70
									}
								}, {
									display : "阈值类型 ",
									name : "thresHoldDataType",
									newline : false,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "thresHoldDataType",
									options : {
										valueField : "value",
										textField : 'name',
										data : thresHoldDataTypeColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "阈值单位",
									name : "thresHoldUnit",
									newline : true,
									type : "text",
									validate : {
										required : true
									}
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
									display : "次数",
									name : "firstLevelTimes",
									newline : false,
									type : "text",
									validate : {
										required : true
									}
								}, {
									display : "启用 ",
									name : "firstIsE",
									newline : true,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "firstIsE",
									options : {
										valueField : "value",
										textField : 'name',
										data : isEColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "告警方式",
									name : "firstAlarmWay",
									newline : false,
									type : "text"
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
									display : "次数",
									name : "secondLevelTimes",
									newline : false,
									type : "text",
									validate : {
										required : true
									}
								}, {
									display : "启用 ",
									name : "secondIsE",
									newline : true,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "secondIsE",
									options : {
										valueField : "value",
										textField : 'name',
										data : isEColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "告警方式",
									name : "secondAlarmWay",
									newline : false,
									type : "text"
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
									display : "次数",
									name : "thirdLevelTimes",
									newline : false,
									type : "text",
									validate : {
										required : true
									}
								}, {
									display : "启用 ",
									name : "thirdIsE",
									newline : true,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "thirdIsE",
									options : {
										valueField : "value",
										textField : 'name',
										data : isEColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "告警方式",
									name : "thirdAlarmWay",
									newline : false,
									type : "text"
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
		url : basePath + "thresHoldAjaxManager.ajax?action=beforeEditThresHold",
		// 参数
		data : {
			thresHoldId : getUrlParam("thresHoldId")
		},
		dataType : "json",
		success : function(array) {
			liger.get("thresHoldName").setDisabled();
			$(array.Rows).each(function() {
						var form = liger.get("form");
						form.setData({
									thresHoldId : this.thresHoldId,
									nodeId : this.nodeId,
									type : this.type,
									subType : this.subType,
									thresHoldName : this.thresHoldName,
									thresHoldIsE : this.thresHoldIsE,
									remark : this.remark,
									alarmInfo : this.alarmInfo,
									thresHoldCompare : this.thresHoldCompare,
									thresHoldDataType : this.thresHoldDataType,
									thresHoldUnit : this.thresHoldUnit,
									//
									firstLevelValue : this.firstLevelValue,
									firstLevelTimes : this.firstLevelTimes,
									firstIsE : this.firstIsE,
									firstAlarmWay : this.firstAlarmWay,
									//
									secondLevelValue : this.secondLevelValue,
									secondLevelTimes : this.secondLevelTimes,
									secondIsE : this.secondIsE,
									secondAlarmWay : this.secondAlarmWay,
									//
									thirdLevelValue : this.thirdLevelValue,
									thirdLevelTimes : this.thirdLevelTimes,
									thirdIsE : this.thirdIsE,
									thirdAlarmWay : this.thirdAlarmWay
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
						+ "thresHoldAjaxManager.ajax?action=editThresHold",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					thresHoldId : $("#thresHoldId").attr("value"),
					nodeId : $("#nodeId").attr("value"),
					type : $("#type").attr("value"),
					subType : $("#subType").attr("value"),
					thresHoldName : liger.get('thresHoldName').getValue(),
					thresHoldIsE : liger.get('thresHoldIsE').getValue(),
					remark : liger.get('remark').getValue(),
					alarmInfo : liger.get('alarmInfo').getValue(),
					thresHoldCompare : liger.get('thresHoldCompare').getValue(),
					thresHoldDataType : liger.get('thresHoldDataType').getValue(),
					thresHoldUnit : liger.get('thresHoldUnit').getValue(),
					//
					firstLevelValue : liger.get('firstLevelValue').getValue(),
					firstLevelTimes : liger.get('firstLevelTimes').getValue(),
					firstIsE : liger.get('firstIsE').getValue(),
					firstAlarmWay : liger.get('firstAlarmWay').getValue(),
					//
					secondLevelValue : liger.get('secondLevelValue').getValue(),
					secondLevelTimes : liger.get('secondLevelTimes').getValue(),
					secondIsE : liger.get('secondIsE').getValue(),
					secondAlarmWay : liger.get('secondAlarmWay').getValue(),
					//
					thirdLevelValue : liger.get('thirdLevelValue').getValue(),
					thirdLevelTimes : liger.get('thirdLevelTimes').getValue(),
					thirdIsE : liger.get('thirdIsE').getValue(),
					thirdAlarmWay : liger.get('thirdAlarmWay').getValue()
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

var isEColumns = [{
			value : '0',
			name : '否'
		}, {
			value : '1',
			name : '是'
		}];

var thresHoldCompareColumns = [{
			value : "0",
			name : '降序'
		}, {
			value : "1",
			name : '升序'
		}, {
			value : "2",
			name : '相等'
		}];

var thresHoldDataTypeColumns = [{
			value : "String",
			name : '字符串'
		}, {
			value : "Number",
			name : '数字'
		}];
