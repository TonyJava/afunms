var ip = null;
var basePath = null;
$(function() {
			ip = $("#ip").attr("value");
			basePath = $("#basePath").attr("value");

			$("#pingPanel").ligerPanel({
						title : '连通率',
						width : 450,
						height : 300
					});
			$("#jvmPanel").ligerPanel({
						title : 'JVM利用率',
						width : 450,
						height : 300
					});

			f_getPingLine();
			f_getJVMLine()
		});

function f_getPingLine() {
	var so = new SWFObject(basePath + "/flex/Tomcat_Ping.swf?ipadress=" + ip,
			"Tomcat_Ping", "450", "300", "8", "#ffffff");
	so.write("pingLine");
}

function f_getJVMLine() {
	var so = new SWFObject(basePath + "/flex/Tomcat_JVM_Memory.swf?ipadress="
					+ ip, "Tomcat_JVM_Memory", "450", "300", "8", "#ffffff");
	so.write("jvmLine");
}
