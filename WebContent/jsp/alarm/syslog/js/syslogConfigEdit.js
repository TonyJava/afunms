var basePath = null;
var id = null;
var grid = null;
var keyGridManager = null;
var bticonadd = null;
var bticondel = null;
$(function() {
			basePath = $("#basePath").attr("value");
			id = $("#id").attr("value");
			bticonadd = basePath + "css/icons/add.gif";
			bticondel = basePath + "css/icons/delete.gif";

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
			
			keyGridManager = createGrid("keyConfigGrid");
			
			// 按钮绑定事件
			$(".btAdd").bind('click', function() {
						var id = $(this).attr("id").toString();
						if(id == "keyBtAdd"){
							initGrid(keyGridManager);
						}
					});

			$(".btDel").bind('click', function() {
						var id = $(this).attr("id").toString();
						if (id == "keyBtDel") {
							delGrid(keyGridManager);
						}
					});
			
			// 复选框初始化
			$('input:checkbox').ligerCheckBox();
			// 复选框选择事件绑定
			$("#keyCheck").change(function() {
						if (!$("#keyCheck").attr("checked")) {
							$("#keyConfigDiv").css('display', 'none');
							clearGrid(smsGridManager);
						} else {
							$("#keyConfigDiv").css('display', 'block');
						}
					});
			// 设置SysLog等级列表
			$("#sysLogCheckBoxList").ligerCheckBoxList({
						rowSize : 9,
						data : sysLogLevel,
						textField : 'name'
					});
			initData();

			$("#submit").click(function() {
						update();
					});

			// 按钮居中
			$(".l-form-buttons").css("padding-left", $(window).width() / 2 - 250);

		});

// 初始化
function initData() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "syslogAjaxManager.ajax?action=beforeEditSyslogConfig",
				// 参数
				data : {
					id : id
				},
				dataType : "json",
				success : function(array) {
					if(array){
						$.each(array, function(n, value) {
							liger.get("sysLogCheckBoxList").setValue(this.syslogrule[0].nodefacility);
							if(this.syslogrule[0].ischeck == "是") {
								liger.get('keyCheck').setValue(true);
								$("#keyConfigDiv").css('display', 'block');
							}
							
							if (this.keyConfigJson) {
								keyGridManager.set({
											data : this.keyConfigJson[0]
										});
							}
						});
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

// 功能函数定义 update
function update() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "syslogAjaxManager.ajax?action=updateSyslogConfig",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					id : id,
					sysLogLevels : liger.get("sysLogCheckBoxList").getValue(),
					keyConfigJson : JSON.stringify(keyGridManager.rows)
				},
				dataType : "text",
				success : function(array) {
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

function openWindow(href, h, w, t) {
	var win = $.ligerDialog.open({
				title : t,
				height : h,
				url : href,
				width : w,
				slide : false
			});
}


//生成详细表格
function createGrid(flag) {
	var rtGrid = null;
	rtGrid = $("#" + flag).ligerGrid({
				columns : [{
							display : '包含',
							name : 'keywords',
							editor : {
								type : 'text'
							},
							width:360
						},{
							display : '告警等级',
							name : 'alarmlevel',
							textField : 'name',
							width:110,
							editor : {
								type : 'select',
								valueField : 'value',
								textField : 'name',
								selectBoxHeight : 50,
								onButtonClick  : function(){
									 this.set({
										 data : alarmTypeColumns
									 });
								}
							},
							render : function(item) {
								if (item.alarmlevel == "1"){
									return '普通告警';
								}else if(item.alarmlevel == "2"){
									return '严重告警';
								}
								return '紧急告警';
							}
						}],
				data : {
					Rows : []
				},
				enabledEdit : true,
				width : '99%',
				usePager : false,
				checkbox : true,
				rownumbers : true,
				rowHeight : 24
			});
	return rtGrid;
}

var alarmTypeColumns = [{
	value : '1',
	name : '普通告警'
}, {
	value : '2',
	name : '严重告警'
}, {
	value : '3',
	name : '紧急告警'
}];

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
		alarmlevel : "1"
			});
}

// 删除行
function delGrid(gridManager) {
	gridManager.deleteSelectedRow();
}

function refreshParent() {
	window.opener.refresh();
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