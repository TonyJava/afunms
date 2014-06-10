var basePath = null;
var versionManager = null;
var btnManager = null;
var loadingImg = null;
$(function() {
			basePath = $("#basePath").attr("value");
			loadingImg = basePath + "css/img/loading.gif";
			versionManager = $('#version').ligerComboBox({
						valueField : 'id',
						textField : 'text'
					});
			versionManager.setData(versionData);
			versionManager.selectValue($("#versionValue").attr("value"));

			btnManager = $("#login").ligerButton({
						click : function() {
							vertify();
						}
					});
			btnManager.setValue('连接');
		});
function vertify() {
	$("#linkInfo").html("<img src='" + loadingImg + "'/>");
	AvailabilityCheckUtil.weblogicCheck($("#ip").val(), $("#port").val(), liger.get('version').getValue(),
			$("#timeOut").val(), $("#community").val(), $("#retries").val(), function(data) {
				callbackMsg(data);
			});
}
function callbackMsg(data) {
	var msg = $("#linkInfo");
	msg.html(data);
}
var versionData = [{
			id : '0',
			text : 'V1'
		}, {
			id : '1',
			text : 'V2'
		}];
