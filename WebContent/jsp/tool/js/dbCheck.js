var basePath = null;
var dbTypeManager = null;
var btnManager = null;
var loadingImg = null;
$(function() {
			basePath = $("#basePath").attr("value");
			loadingImg = basePath + "css/img/loading.gif";
			dbTypeManager = $('#dbType').ligerComboBox({
						valueField : 'id',
						textField : 'text'
					});
			dbTypeManager.setData(typeData);
			dbTypeManager.selectValue($("#dbTypeInt").attr("value"));

			btnManager = $("#login").ligerButton({
						click : function() {
							vertify();
						}
					});
			btnManager.setValue('连接');
		});
function vertify() {
	$("#linkInfo").html("<img src='" + loadingImg + "'/>");
	AvailabilityCheckUtil.dbCheck($("#dbType").val(), $("#ip").val(), $("#iOrn").val(), $("#port")
					.val(), $("#user").val(), $("#password").val(), function(data) {
				callbackMsg(data);
			});
}
function callbackMsg(data) {
	var msg = $("#linkInfo");
	msg.html(data);
}
var typeData = [{
			id : '1',
			text : 'oracle'
		}, {
			id : '4',
			text : 'mysql'
		}, {
			id : '2',
			text : 'sqlserver'
		}];
