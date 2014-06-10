//½ûÖ¹CTRL¼ü
function rf() {
	return false;
}
document.oncontextmenu = rf;
function keydown() {
	if(event.ctrlKey ==true || event.keyCode ==93 || event.shiftKey ==true) {
		return false;
	}
}
document.onkeydown = keydown;
function drag() {
	return false;
}
document.ondragstart = drag;
 
//½ûÖ¹Êó±êÓÒ¼ü
var message = "";
function clickIE() {
	if (document.all) {
		(message);
		return false;
	}
}
function clickNS(e) {
	if (document.layers||(document.getElementById&&!document.all)) {
		if (e.which==2||e.which==3) {
			(message);
			return false;
		}
	}
}

if (document.layers) {
	document.captureEvents(Event.MOUSEDOWN);
	document.onmousedown = clickNS;
}
else {
	document.onmouseup = clickNS;
	document.oncontextmenu=clickIE;
}
document.oncontextmenu = new Function("return false");