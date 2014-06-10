var basePath = null;
var groupicon = null;
var bticonadd = null;
var bticondel = null;

var mailGridManager = null;
var smsGridManager = null;
var soundGridManager = null;

$(function() {
			basePath = $("#basePath").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			bticonadd = basePath + "css/icons/add.gif";
			bticondel = basePath + "css/icons/delete.gif";
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						inputWidth : 120,
						labelWidth : 60,
						space : 30,
						labelAlign : 'right',
						fields : [{
									display : "名称",
									name : "name",
									newline : false,
									type : "text",
									group : "基础参数",
									groupicon : groupicon
								}, {
									display : "默认",
									name : "isDefault",
									newline : false,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "isDefault",
									options : {
										valueField : "value",
										textField : 'name',
										data : isColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "系统告警",
									name : "isSystem",
									newline : false,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "isSystem",
									options : {
										valueField : "value",
										textField : 'name',
										data : isColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "描述",
									name : "remark",
									newline : true,
									type : "text",
									width : 280
								}]
					});

			mailGridManager = createGrid("mailConfigGrid");
			smsGridManager = createGrid("smsConfigGrid");
			soundGridManager = createGrid("soundConfigGrid");

			// 复选框初始化
			$('input:checkbox').ligerCheckBox();
			// 复选框选择事件绑定
			$("#mailCheck").change(function() {
						if (!$("#mailCheck").attr("checked")) {
							$("#mailConfigDiv").css('display', 'none');
							clearGrid(mailGridManager);
						} else {
							$("#mailConfigDiv").css('display', 'block');
						}
					});

			$("#smsCheck").change(function() {
						if (!$("#smsCheck").attr("checked")) {
							$("#smsConfigDiv").css('display', 'none');
							clearGrid(smsGridManager);
						} else {
							$("#smsConfigDiv").css('display', 'block');
						}
					});

			$("#soundCheck").change(function() {
						if (!$("#soundCheck").attr("checked")) {
							$("#soundConfigDiv").css('display', 'none');
							clearGrid(soundGridManager);
						} else {
							$("#soundConfigDiv").css('display', 'block');
						}
					});

			// 初始化按钮
			$(".btAdd").ligerButton({
						width : 60,
						text : '增加行',
						icon : bticonadd
					});
			// 初始化按钮
			$(".btDel").ligerButton({
						width : 60,
						text : '删除行',
						icon : bticondel
					});

			// 初始化按钮
			$("#btSave").ligerButton({
						width : 60,
						text : '保存',
						click : function() {
							f_save();
						}
					});

			// 初始化按钮
			$("#btCancel").ligerButton({
						width : 60,
						text : '关闭',
						click : function() {
							window.close();
						}
					});

			// 按钮绑定事件
			$(".btAdd").bind('click', function() {
						var id = $(this).attr("id").toString();
						if (id == "mailBtAdd") {
							initGrid(mailGridManager);
						} else if (id == "smsBtAdd") {
							initGrid(smsGridManager);
						} else if (id == "soundBtAdd") {
							initGrid(soundGridManager);
						}
					});

			$(".btDel").bind('click', function() {
						var id = $(this).attr("id").toString();
						if (id == "mailBtDel") {
							delGrid(mailGridManager);
						} else if (id == "smsBtDel") {
							delGrid(smsGridManager);
						} else if (id == "soundBtDel") {
							delGrid(soundGridManager);
						}
					});

			// 初始化赋值
			liger.get('isSystem').setValue('1');
			liger.get('isDefault').setValue('0');

		});

var isColumns = [{
			value : '0',
			name : '否'
		}, {
			value : '1',
			name : '是'
		}];

var dateTypeColumns = [{
			value : 'month',
			name : '月'
		}, {
			value : 'week',
			name : '周'
		}];

// 生成详细表格
function createGrid(flag) {
	var rtGrid = null;
	rtGrid = $("#" + flag).ligerGrid({
				columns : [{
							display : '日期方式',
							name : 'dateType',
							width : 80,
							isSort : false,
							textField : 'name',
							editor : {
								type : 'select',
								data : dateTypeColumns,
								valueField : 'value',
								textField : 'name',
								selectBoxHeight : 50
							},
							render : function(item) {
								if (item.dateType == "week")
									return '周';
								return '月';
							}
						}, {
							display : '次数',
							name : 'times',
							editor : {
								type : 'text'
							},
							width : 50
						}, {
							display : '开始日期',
							name : 'startDate',
							editor : {
								type : 'spinner',
								valueField: 'startDate',
								minValue : 1,
								maxValue : 31
							},
							width : 80
						}, {
							display : '结束日期',
							name : 'endDate',
							editor : {
								type : 'spinner',
								valueField: 'endDate',
								minValue : 1,
								maxValue : 31
							},
							width : 80
						}, {
							display : '开始时间',
							name : 'startTime',
							editor : {
								type : 'spinner',
								valueField: 'startTime',
								minValue : 0,
								maxValue : 23
							},
							width : 80
						}, {
							display : '结束时间',
							name : 'endTime',
							editor : {
								type : 'spinner',
								valueField: 'endTime',
								minValue : 0,
								maxValue : 23
							},
							width : 80
						}, {
							display : '接收人',
							textField : 'userName',
							editor : {
								type : 'popup',
								valueField : 'userId',
								textField : 'userName',
								onButtonClick : function() {
									choseUser(flag);
								}
							},
							minWidth : 100
						}],
				data : {
					Rows : []
				},
				enabledEdit : true,
				width : '100%',
				usePager : false,
				checkbox : true,
				rownumbers : true,
				rowHeight : 24
			});
	return rtGrid;
}

// 重置表格
function clearGrid(grid) {
	grid.set({
				data : {
					Rows : []
				}
			});
}
// 初始化表格
function initGrid(gridManager) {
	gridManager.addRow({
				dateType : "month",
				times : 1,
				startDate : 1,
				endDate : 31,
				startTime : 0,
				endTime : 23
			});
}

// 删除行
function delGrid(gridManager) {
	gridManager.deleteSelectedRow();
}

// 选择用户
function choseUser(flag) {
	$.ligerDialog.open({
				url : 'choseUser.jsp',
				height : 480,
				width : 550,
				title : "选择用户",
				buttons : [{
							text : '确定',
							onclick : function(item, dialog) {
								// 获取对话框用户列表
								var userGrid = dialog.frame.grid;
								// 获取列表选中项
								var rows = userGrid.getSelectedRows();
								var ids = "";
								var userIds = "";
								$(rows).each(function() {
											ids += this.id + ",";
											userIds += this.userId + ",";
										});
								var grid = liger.get(flag);
								grid.updateRow(grid.lastEditRow, {
											userId : ids,
											userName : userIds
										});
								// 关闭对话框
								dialog.close();
							},
							cls : 'l-dialog-btn-highlight'
						}, {
							text : '取消',
							onclick : function(item, dialog) {
								dialog.close();
							}
						}],
				isResize : true
			});
}

function f_save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "alarmWayAjaxManager.ajax?action=addAlarmWay",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					name : liger.get('name').getValue(),
					isDefault : liger.get('isDefault').getValue(),
					isSystem : liger.get('isSystem').getValue(),
					remark : liger.get('remark').getValue(),
					isMail : liger.get('mailCheck').getValue(),
					isSms : liger.get('smsCheck').getValue(),
					isSound : liger.get('soundCheck').getValue(),

					mailConfigJson : JSON.stringify(mailGridManager.rows),
					smsConfigJson : JSON.stringify(smsGridManager.rows),
					soundConfigJson : JSON.stringify(soundGridManager.rows)
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								// 刷新列表
								opener.refresh();
								window.close();

							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});

}