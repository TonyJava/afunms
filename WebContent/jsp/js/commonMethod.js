function getTypeData() {
	var data = [];
	$(t_st.Rows).each(function() {
		if (!this.type)
			return;
		if (!exist(this.type, data)) {
			data.push({
				type : this.type,
				tName : this.tName
			});
		}
	});
	return data;

	function exist(type, data) {
		for (var i = 0, l = data.length; i < l; i++) {
			if ($.trim(data[i].type.toString()) == $.trim(type.toString())) {
				return true;
			}
		}
		return false;
	}
}

function getSubType(type) {
	var data = [];
	$(t_st.Rows).each(function() {
		if (!this.subType)
			return;
		if (this.type == type && !exist(this.subType, data)) {
			data.push({
				// 子类型
				subType : this.subType,
				// 子类型名称
				stName : this.stName
			});
		}
	});
	return data;
	// 过滤重复子类型
	function exist(subType, data) {
		for (var i = 0, l = data.length; i < l; i++) {
			if ($.trim(data[i].subType.toString()) == $
					.trim(subType.toString()))
				return true;
		}
		return false;
	}
}

function getNode(array, subType) {

	var data = [];
	$(array.Rows).each(function() {
		if (!this.nodeId)
			return;
		if (this.subType == subType && !exist(this.nodeId, data)) {
			data.push({
				// 网元ID
				nodeId : this.nodeId,
				// 网元名称
				alias : this.alias
			});
		}
	});
	return data;
	// 过滤重复网元
	function exist(nodeId, data) {
		for (var i = 0, l = data.length; i < l; i++) {
			if ($.trim(data[i].nodeId.toString()) == $.trim(nodeId.toString()))
				return true;
		}
		return false;
	}
}

// 类型 改变事件：清空子类型,重新绑定数据
function f_onTypeChanged(value) {
	var combo = liger.get('subType');
	if (!combo)
		return;
	var data = getSubType(value);
	combo.clear();
	combo.set('data', data);
}

// 子类型 改变事件：清空网元,重新绑定数据
function f_onSubTypeChanged(value) {
	if (!value) {
		return;
	}
	$.ajax({
		type : "get",
		url : basePath + "indicatorAjaxManager.ajax?action=getNode",
		dataType : "json",
		contentType : "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(array) {
			var combo = liger.get('nodeId');
			if (!combo)
				return;
			var nodes = getNode(array, value);
			combo.clear();
			combo.set('data', nodes);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});

}

function getUrlParam(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg); // 匹配目标参数
	if (r != null) {
		return decodeURI(decodeURI(escape(r[2])));
	}
	return null; // 返回参数值
}

// 打开新的窗口方法
function openWindow(href, h, w, t) {
	// href 转向网页的地址
	// t 网页名称，可为空
	// w 弹出窗口的宽度
	// h 弹出窗口的高度
	// window.screen.height获得屏幕的高，window.screen.width获得屏幕的宽
	var top = (window.screen.height - 30 - h) / 2; // 获得窗口的垂直位置;
	var left = (window.screen.width - 10 - w) / 2; // 获得窗口的水平位置;
	var features = "height="
			+ h
			+ ", width="
			+ w
			+ ",top="
			+ top
			+ ",left="
			+ left
			+ ",toolbar=no,menubar=no,scrollbars=no,resizable=yes,location=no,status=no";
	window.open(href, t, features);
}

// 服务器添加专用
function getTypeDataForNodeAdd() {
	var data = [];
	$(t_ost.Rows).each(function() {
		if (!this.typeValue)
			return;
		if (!exist(this.typeValue, data)) {
			data.push({
				typeValue : this.typeValue,
				typeDescr : this.typeDescr
			});
		}
	});
	return data;

	function exist(typeValue, data) {
		for (var i = 0, l = data.length; i < l; i++) {
			if ($.trim(data[i].typeValue.toString()) == $.trim(typeValue
					.toString())) {
				return true;
			}
		}
		return false;
	}
}

function getOsType(typeValue) {
	var data = [];
	$(t_ost.Rows).each(function() {
		if (!this.osValue)
			return;
		if (this.typeValue == typeValue && !exist(this.osValue, data)) {
			data.push({
				// 子类型
				osValue : this.osValue,
				// 子类型名称
				osDescr : this.osDescr
			});
		}
	});
	return data;
	// 过滤重复子类型
	function exist(osValue, data) {
		for (var i = 0, l = data.length; i < l; i++) {
			if ($.trim(data[i].osValue.toString()) == $
					.trim(osValue.toString()))
				return true;
		}
		return false;
	}
}

// 类型 改变事件：清空子类型,重新绑定数据
function f_onTypeChangedForNodeAdd(value) {
	var combo = liger.get('subType');
	if (!combo)
		return;
	var data = getOsType(value);
	combo.clear();
	combo.set('data', data);
}

// 对话框遮罩窗口
function openDlgWindow(href, h, w, t) {
	var win = $.ligerDialog.open({
		title : t,
		height : h,
		url : href,
		width : w,
		slide : false
	});
	return win;
}

// 对话框遮罩窗口
function openPnDlgWindow(href, h, w, left, top, t) {
	var win = $.ligerDialog.open({
		title : t,
		height : h,
		left : left,
		top : top,
		url : href,
		width : w,
		slide : true,
		isDrag : false
	});
	return win;
}
// 全屏窗口
function openFullWindow(href, t) {
	var h = screen.availHeight;
	var w = screen.availWidth;
	var features = " top=0,left=0,width="
			+ w
			+ ",height="
			+ h
			+ ",location=no,menubar=no,resizable=no,scrollbars=no,status=no,toolbar=no ";
	var self = window.open(href, t, features);
	self.resizeTo(w, h);
	self.moveTo(0, 0);
}

// 全屏窗口
function openCommonFullWindow(href, t) {
	var h = screen.availHeight;
	var w = screen.availWidth;
	var features = " top=0,left=0,width="
			+ w
			+ ",height="
			+ h
			+ ",location=no,menubar=yes,resizable=no,scrollbars=no,status=no,toolbar=yes";
	var self = window.open(href, t, features);
	self.resizeTo(w, h);
	self.moveTo(0, 0);
}

// 关闭对话框窗口
function closeDlgWindow() {
	var dialog = frameElement.dialog;
	dialog.close();
}

function showDeleteDlg(url, string) {
	$("html,body").css("height", "100%");
	var bodyH = $(document.body).height();
	var bodyW = $(document.body).width();
	$(document.body)
			.append(
					"<div id='dlgTipMask'></div>"
							+ "<div id='dlgTip'><div id='title'></div>"
							+ "<div id='tipContent'>"
							+ "<div id='tipImgDiv'><img id='tipImg' src='/afunms/css/img/loading.gif' /></div>"
							+ "<div id='tipText'>正在删除,请稍后...</div></div>"
							+ "<div id='tipOp'><div id='btn'></div></div>" //
							+ "</div>");

	var tipH = $("#dlgTip").height();
	var tipW = $("#dlgTip").width();
	$("#dlgTip").css({
		"left" : (bodyW - tipW) / 2,
		"top" : (bodyH - tipH) / 2
	});

	$("#btn").ligerButton({
		width : 60,
		text : '确定',
		click : function() {
			$("#dlgTipMask").remove();
			$("#dlgTip").remove();
			// 刷新列表
			refresh();
		}
	});

	$("#dlgTip").show(function() {
		$.ajax({
			type : "POST",
			async : false,
			url : url,
			// 参数
			data : {
				string : string
			},
			dataType : "text",
			success : function(array) {
				$("#tipImg").attr("src", "/afunms/css/img/ok.gif");
				$("#tipText").html(array);
				$("#tipOp").css("display", "block");
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown);
			}
		});
	});
}