var basePath = null;

var typeManager = null;
var btnManager = null;

var displayPanel = null;
var commandText = null;
var server = null;
var port = null;
var terminalType = null;
var tcHashCode=null;

$(function() {
			basePath = $("#basePath").attr("value");

			tcHashCode = $("#tcHashCode");
			displayPanel = $("#printArea");
			commandText = $("#commandText");
			// 先登录才能发送命令
			commandText.attr("disabled", "disabled");
			server = $("#ipaddress");
			port = $("#port");
			terminalType = $("#terminalType");

			document.onkeydown = function(e) {
				var theEvent = window.event || e;
				var code = theEvent.keyCode || theEvent.which;
				if (code == 13) {
					sendCommand();
				}
			};

			typeManager = $('#terminalType').ligerComboBox({
						valueField : 'id',
						textField : 'text'
					});

			btnManager = $("#login").ligerButton({
						click : function() {
							login();
						}
					});
			
			btnManager.setValue('连接');
			typeManager.setData(typeData);
			typeManager.selectValue("windows");

			$(window).unload(function() {
						endSeession();
					});

		});

// 方法定义
function login() {
	displayPanel.empty();
	$.ajax({
				type : "post",
				url : basePath + "/telnetLogin",
				// 参数
				data : {
					server : server.val(),
					port : port.val(),
					terminalType : terminalType.val(),
					tcHashCode : tcHashCode.val()
				},
				dataType : 'json',
				success : function(data) {
					tcHashCode.val(data.tcHashCode);
					displayPanel.append(data.serverOutputInfo);
					commandText.removeAttr("disabled");
					commandText.focus();
				},
				error : function(data) {
					alert("登录失败");
				}
			});
}

function sendCommand() {
	$.ajax({
				type : "post",
				url : basePath + "/telnetSendCommand",
				// 参数
				data : {
					commandText : commandText.val(),
					tcHashCode : tcHashCode.val()
				},
				success : function(info) {
					commandText.val("");
					displayPanel.empty();
					displayPanel.text(info);
					var scrollTop = $("#printArea")[0].scrollHeight;
					$("#printArea").scrollTop(scrollTop);
					commandText.focus();
				},
				error : function(info) {
					alert("error");
				}
			});
}

function closeSession() {
	$.ajax({
				type : "post",
				url : basePath + "/telnetAjaxManager.ajax?action=endSession",
				data : {
					tcHashCode : tcHashCode.val()
				},
				success : function(info) {
					alert(info);
				},
				error : function(info) {
					alert("error");
				}
			});

}

var typeData = [{
			id : 'windows',
			text : 'windows'
		}, {
			id : 'linux',
			text : 'linux'
		}, {
			id : 'unix',
			text : 'unix'
		}];
