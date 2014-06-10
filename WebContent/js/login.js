var basePath = null;
var flag = 0;
$(function() {
	if (window != top)
		top.location.href = location.href;
	basePath = $("#basePath").attr("value");
	$("#userid").focus();
	document.onkeydown = function(e) {
		var theEvent = window.event || e;
		var code = theEvent.keyCode || theEvent.which;
		if (code == 13) {
			login();
		}
	};
});

function login() {
	$.ajax({
		url : basePath + 'user.do?action=login',
		data : 'userid=' + $("#userid").val() + "&password="
				+ $("#password").val(),
		type : 'post',
		error : function() {
			alert("登录失败,请检查用户信息");
			$("#userid").val("");
			$("#password").val("");
			$("#userid").focus();
		},
		success : function(msg) {
			window.location.href = basePath + 'index.jsp';
		}
	});
};