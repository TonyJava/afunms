var basePath = null;

var byteManager = null;
var countManager = null;
var btnManager = null;
var ip = null;

$(function() {
	basePath = $("#basePath").attr("value");
	ip = $("#ipaddress").attr("value");
	if (ip) {
		ifExecute('ping', 'execute');
	}

	document.onkeydown = function(e) {
		var theEvent = window.event || e;
		var code = theEvent.keyCode || theEvent.which;
		if (code == 13) {
			ifExecute('ping', 'execute');
		}
	};

	byteManager = $('#packagelength').ligerComboBox({
		valueField : 'id',
		textField : 'text'
	});
	countManager = $('#executenumber').ligerComboBox({
		valueField : 'id',
		textField : 'text'
	});

	btnManager = $("#execute").ligerButton({
		click : function() {
			ifExecute('ping', 'execute');
		}
	});
	btnManager.setValue('执行');
	byteManager.setData(byteData);
	byteManager.selectValue("32");
	countManager.setData(countData);
	countManager.selectValue("4");
});

var byteData = [ {
	id : '32',
	text : '32'
}, {
	id : '64',
	text : '64'
}, {
	id : '128',
	text : '128'
} ];

var countData = [ {
	id : '4',
	text : '4'
}, {
	id : '8',
	text : '8'
}, {
	id : '16',
	text : '16'
}, {
	id : '32',
	text : '32'
} ];
