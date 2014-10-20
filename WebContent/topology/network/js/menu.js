//-----右键菜单的样式---------------------------------------
function pingMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="ping_menu_out";
}
function pingMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="ping_menu_over";
}
function showmenu(elmnt)
{
	var secondMenu = document.getElementById(elmnt);
	secondMenu.style.display="block";
	secondMenu.style.left =  get_previoussibling(secondMenu).offsetWidth+'px';
}
function get_previoussibling(n)
{
	var x=n.previousSibling;
	while (x.nodeType!=1)
	  {
	  x=x.previousSibling;
	  }
	return x;
}
function hidemenu(elmnt)
{
	document.getElementById(elmnt).style.display="none";
}
function toolMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="tool_menu_out";
}
function toolMenuOver() {
	

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="tool_menu_over";
}
function deleteMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="deleteline_menu_out";
}
function deleteMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="deleteline_menu_over";
}
//第一层“查看”菜单
function detailMainMenuOut() 
{
	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="detail_mainmenu_out";
}
function detailMainMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
var srcElement = event.srcElement||event.target;
srcElement.className="detail_mainmenu_over";
}
function detailMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="detail_menu_out";
}
function detailMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="detail_menu_over";
}
//第一层“设置”菜单
function setMenuOut() {
	

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="set_menu_out";
}
function setMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="set_menu_over";
}
//第一层“设备管理”菜单
function manageMainMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="manage_mainmenu_out";
}
function manageMainMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="manage_mainmenu_over";
}
//第一层“报表查看”菜单
function reportMainMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="report_mainmenu_out";
}
function reportMainMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="report_mainmenu_over";
}
//第一层“告警”菜单
function alarmMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="alarm_menu_out";
}
function alarmMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="alarm_menu_over";
}
//设备面板
function sbmbMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="sbmb_menu_out";
}
function sbmbMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="sbmb_menu_over";
}
//第二级“报表查看”
function reportMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="report_menu_out";
}
function reportMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="report_menu_over";
}
//设备关联应用  HONGLI ADD
function deleteEquipRelatedApplicationsMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="equipRelatedApplications_menu_out";
}
function deleteEquipRelatedApplicationsMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="equipRelatedApplications_menu_over";
}
function panelmanageMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="panel_manage_menu_out";
}
function panelmanagelMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="panel_manage_menu_over";
}
function detailMenuOut1() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="detail_menu_out1";
}
function detailMenuOver1() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="detail_menu_over1";
}
function manageMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="manage_menu_out";
}
function manageMenuOver() {
	srcElement.className="manage_menu_over";
}
function downloadMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="download_menu_out";
}
function downloadMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="download_menu_over";
}
//yangjun add
function deleteEquipMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="deleteEquip_menu_out";
}
function deleteEquipMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="deleteEquip_menu_over";
}
//hukelei add
function confirmAlarmMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="confirmAlarm_menu_out";
}
function confirmAlarmMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="confirmAlarm_menu_over";
}
function propertyMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="property_menu_out";
}
function propertyMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="property_menu_over";
}
//采集指标
function collectionMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="collection_menu_out";
}
function collectionMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="collection_menu_over";
}
//指标阀值
function thresholdMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="threshold_menu_out";
}
function thresholdMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="threshold_menu_over";
}
//端口配置
function portMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="port_menu_out";
}
function portMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="port_menu_over";
}
//端口阀值
function portthresholdMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="portthreshold_menu_out";
}
function portthresholdMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="portthreshold_menu_over";
}
//端口扫描
function portscanMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="portscan_menu_out";
}
function portscanMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="portscan_menu_over";
}
function relationMapMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="relationmap_menu_out";
}
function relationMapMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="relationmap_menu_over";
}
function deleteLineMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="deleteline_menu_out";
}
function deleteLineMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="deleteline_menu_over";
}
function editLineMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="editline_menu_out";
}
function editLineMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="editline_menu_over";
}

function telnetMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="telnet_menu_out";
}

function telnetMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="telnet_menu_over";
}

function listMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="list_menu_out";
}

function listMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="list_menu_over";
}

//end
function traceMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="trace_menu_out";
}
function traceMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="trace_menu_over";
}

function cloudMenuOut() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="cloud_menu_out";
}
function cloudMenuOver() {

	var event = arguments.callee.caller.arguments[0]||window.event;
	var srcElement = event.srcElement||event.target;
	srcElement.className="cloud_menu_over";
}
function showalert(id) {
	//window.parent.parent.opener.location="/afunms/detail/dispatcher.jsp?id="+id;
	window.parent.parent.opener.parent.window.document.getElementById('mainFrame').src="/afunms/detail/dispatcher.jsp?id="+id;
}
//右键菜单
//


//document.oncontextmenu = function() {
//    if($("div_RightMenu") == null)
//    {    
//        CreateMenu();
//        document.oncontextmenu = ShowMenu
//        document.body.onclick  = HideMenu    
//    }
//    else
//    {
//        document.oncontextmenu = ShowMenu
//        document.body.onclick  = HideMenu    
//    }    
//}
function evtMenu2()
{    
    HideMenu();
}
function evtMenuOnmouseMove()
{
    this.style.backgroundColor='#8AAD77';
    this.style.paddingLeft='30px';    
}
function evtOnMouseOut()
{
    this.style.backgroundColor='#FAFFF8';
}
function CreateMenu()
{    
        var div_Menu          = document.createElement("Div");
        div_Menu.id           = "div_RightMenu";
        div_Menu.className    = "div_RightMenu";
        
        var div_Menu1          = document.createElement("Div");
        div_Menu1.className   = "divMenuItem";
        div_Menu1.onclick     = createSubMap;
        div_Menu1.onmousemove = evtMenuOnmouseMove;
        div_Menu1.onmouseout  = evtOnMouseOut;
        div_Menu1.innerHTML   = "创建子图";
        
        var div_Menu2          = document.createElement("Div");
        div_Menu2.className   = "divMenuItem";
        div_Menu2.onclick     = evtMenu2;
        div_Menu2.onmousemove = evtMenuOnmouseMove
        div_Menu2.onmouseout  = evtOnMouseOut
        div_Menu2.innerHTML   = "删除记录";
        
        
        var div_Menu4          = document.createElement("Div");
        div_Menu4.className   = "divMenuItem";
        div_Menu4.onmousemove = evtMenuOnmouseMove;
        div_Menu4.onmouseout  = evtOnMouseOut;
        div_Menu4.innerHTML   = "刷新";
        
        var Hr1        = document.createElement("Hr");
        
        
        var div_Menu6          = document.createElement("Div");
        div_Menu6.className   = "divMenuItem";
        div_Menu6.onmousemove = evtMenuOnmouseMove;
        div_Menu6.onmouseout  = evtOnMouseOut;
        div_Menu6.innerHTML   = "复制";
        
        var div_Menu7          = document.createElement("Div");
        div_Menu7.className   = "divMenuItem";
        div_Menu7.onmousemove = evtMenuOnmouseMove;
        div_Menu7.onmouseout  = evtOnMouseOut;
        div_Menu7.innerHTML   = "全选";
        
        var Hr2        = document.createElement("Hr");
        
        var div_Menu10           = document.createElement("Div");
        div_Menu10.className   = "divMenuItem";
        div_Menu10.style.marginBottom =  0;
        div_Menu10.onmousemove = evtMenuOnmouseMove;
        div_Menu10.onmouseout  = evtOnMouseOut;
        div_Menu10.innerHTML   = "属性";
        
        
        div_Menu.appendChild(div_Menu1);
        div_Menu.appendChild(div_Menu2);
        div_Menu.appendChild(div_Menu4);
        div_Menu.appendChild(Hr1);
        
        div_Menu.appendChild(div_Menu6);
        div_Menu.appendChild(div_Menu7);
        div_Menu.appendChild(Hr2);
        
        div_Menu.appendChild(div_Menu10);

        document.body.appendChild(div_Menu);
}
    
// 判断客户端浏览器
function IsIE() 
{
    if (navigator.appName=="Microsoft Internet Explorer") 
    {
        return true;
    } 
    else 
    {
        return false;
    }
}

function ShowMenu()
{
    if (IsIE())
    {
        document.body.onclick  = HideMenu;
        var redge=document.body.clientWidth-event.clientX;
        var bedge=document.body.clientHeight-event.clientY;
        var menu = $("div_RightMenu");
        if (redge<menu.offsetWidth)
        {
            menu.style.left=document.body.scrollLeft + event.clientX-menu.offsetWidth
           
        }
        else
        {
            menu.style.left=document.body.scrollLeft + event.clientX
            //这里有改动
            menu.style.display = "block";
        }
        if (bedge<menu.offsetHeight)
        {
            menu.style.top=document.body.scrollTop + event.clientY - menu.offsetHeight
        }
        else
        {
            menu.style.top = document.body.scrollTop + event.clientY
            menu.style.display = "block";
        }
    }
    return false;
}
function HideMenu()
{
    if (IsIE()) $("div_RightMenu").style.display="none";    
}

function $(gID)
{
    return document.getElementById(gID);
}