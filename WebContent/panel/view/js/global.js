<!--

var g_viewFlag = 0;				// 当前选择的视图类型，0 - 设备名，1 - 设备IP

var g_subFlag = false;			// 该参数在 showSubMap.jsp 模拟全局变量
								// 以便节点解析函数分析

//
// 加载对话框(div)
//

// processing frame - begin
var procDlgWidth = 280;
var procDlgHeight = 110;

var screenWidth = 0;
var screenHeight = 0;
if (viewWidth < 1)
	screenWidth = window.screen.width;
else
	screenWidth = viewWidth;

if (viewHeight < 1)
 	screenHeight = window.screen.height;
else
	screenHeight = viewHeight;

var toolbarHeight = window.screen.height - window.screen.availHeight;
var toolbarWidth = window.screen.width - window.screen.availWidth;

document.write('<div id="processing" style="position:absolute;border:#000000 1px solid;font-size:14px;font-weight:bold;text-align:center;background-color:#F5F5F5;color:#000000;');
document.write('height:' + procDlgHeight + 'px;top:' + (screenHeight/2 - toolbarHeight - procDlgHeight - 80) + 'px;');
document.write('width:' + procDlgWidth + 'px;left:'+ (screenWidth - procDlgWidth)/2 +'px;');
document.write('visibility:hidden;z-index:999;" align="center"><br/><br/><br/>正在处理数据，请稍候 ...</div>');

function openProcDlg() {
	document.all.processing.style.visibility = "visible";
}

function closeProcDlg() {
	procDlgWidth = 280;
	procDlgHeight = 110;
	document.all.processing.style.height = procDlgHeight;
	document.all.processing.style.width = procDlgWidth;
	document.all.processing.style.top = (screenHeight/2 - toolbarHeight - procDlgHeight - 80);
	document.all.processing.style.left = (screenWidth - procDlgWidth)/2;
	document.all.processing.style.zoom = 1.0;
	document.all.processing.style.zIndex = 999;
	document.all.processing.style.visibility = "hidden";
}

function resetProcDlg() {
	procDlgWidth = 280;
	procDlgHeight = 110;
	document.all.processing.style.height = procDlgHeight;
	document.all.processing.style.width = procDlgWidth;
	document.all.processing.style.top = (screenHeight/2 - toolbarHeight - procDlgHeight - 80);
	document.all.processing.style.left = (screenWidth - procDlgWidth)/2;
	document.all.processing.style.zoom = 1.0;
	document.all.processing.style.zIndex = 999;
	document.all.processing.style.visibility = "visible";
}

function timingCloseProDlg(timeout) {
	setTimeout("zoomProcDlg('out')", parseInt(timeout));
}

var zoom = 1.0;
var left = (screenWidth - procDlgWidth)/2;
var top = (screenHeight/2 - toolbarHeight - procDlgHeight - 80);

var scale = 0.1;
var horizontal = 14;
var vertical = 6;
var speed = 100;
var timer;

function zoomProcDlg(flag) {	
	if (flag == "out") {
		document.all.processing.style.zoom = 1.0;
		clearTimer();
		updateStatus();
		zoom = zoom - scale;
		left = left - horizontal;
		timer = setInterval("zoomOutAction()", speed);
	}
}

function zoomOutAction() {
	updateStatus();
	if (zoom == 0.1) {
		clearTimer();
		closeProcDlg();
		return;
	}
	document.all.processing.style.zoom = (zoom - scale);
	document.all.processing.style.left = (left + horizontal);
	document.all.processing.style.top = (top + vertical);
}

function clearTimer(){
	clearInterval(timer);
}

function updateStatus() {
	var divLeft = parseInt(processing.style.left);
	var divTop = parseInt(processing.style.top);
	
	//divLeft = divLeft.substring(0, divLeft.length - 2);
	//divTop = divTop.substring(0, divTop.length - 2);

	left = parseInt(divLeft);
	top = parseInt(divTop);

	zoom = document.all.processing.style.zoom;
}

// processing frame - end
//-->