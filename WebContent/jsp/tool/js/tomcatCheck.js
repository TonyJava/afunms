var basePath = null;
var btnManager = null;
var loadingImg = null;
$(function() {
			basePath = $("#basePath").attr("value");
			loadingImg = basePath + "css/img/loading.gif";
			
			dwr.engine.setErrorHandler(error);

			btnManager = $("#login").ligerButton({
						click : function() {
							vertify();
						}
					});
			btnManager.setValue('连接');
		});
function vertify() {
	$("#linkInfo").html("<img src='" + loadingImg + "'/>");
	AvailabilityCheckUtil.tomcatCheck($("#ip").val(), $("#port").val(), $("#user").val(),
			$("#password").val(), function(data) {
				callbackMsg(data);
			});
}
function callbackMsg(data) {
	var msg = $("#linkInfo");
	msg.html(data);
}

function error(){
$("#linkInfo").html("无法远程访问Tomcat");
}

