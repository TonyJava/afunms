<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK" import="java.util.*"%>
<%@page import="java.io.File"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.yhgl.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<jsp:useBean id="Upload" scope="page" class="com.jspsmart.upload.SmartUpload" />
<jsp:useBean id="docMgr" scope="session" class="com.yhgl.documentMgr" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%
	String rootPath = request.getContextPath();
	String returnInfo = "";
	String virtualname = "";
	String imgPath="";
	boolean flag=false;
	//把文件上传到服务器
	//String ipaddress = request.getParameter("ipaddress");
	//System.out.println(ipaddress);
	//Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
	//String oid = host.getSysOid();
	//oid = oid.replaceAll("\\.","-");
	Host host = (Host)request.getAttribute("host");
	String fileName = (String)request.getAttribute("fileName");
	imgPath = fileName;
	List indexlist = (List)request.getAttribute("indexlist");
	Hashtable hs = (Hashtable)request.getAttribute("hs");
    String imageType = (String)request.getAttribute("imageType");
	try {
		Upload.initialize(pageContext);
		Upload.setTotalMaxFileSize(1024 * 1024 * 5000);
		Upload.upload();

		String fs = File.separator;
		com.jspsmart.upload.File myfile = Upload.getFiles().getFile(0);
		if (!myfile.isMissing()) {
			String fname = myfile.getFileName();
			System.out.println(fname);
			//virtualname = docMgr.getVirtualName(fname);
			//virtualname = oid+".jpg";
			//String fputfile = virtualname;
			//imgPath=rootPath+"/panel/view/image/"+ virtualname;
			//virtualname = ".."+rootPath+"/panel/view/image/"+virtualname;
			//System.out.println(imgPath);
			int fileSize = myfile.getSize();
			if (fileSize > 1024 * 1024 * 10) {
				returnInfo = "插入图片最大不能超过10MB";
			} else {
				System.out.println("#### "+fname);
				try{
					myfile.saveAs(fileName,Upload.SAVE_VIRTUAL);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//System.out.println(virtualname+"====");
				//session.removeAttribute("factname");
				//session.setAttribute("factname", fname);
				//session.removeAttribute("virtualname");
				//session.setAttribute("virtualname", fputfile);
				returnInfo = "文件已经成功上传到服务器.";
				flag=true;
			}
		} else {
			returnInfo = "文件丢失，上传失败";
		}
	} catch (Exception e) {
		returnInfo = "上传文件失败";
		out.println(e.toString());
	}
%>
<html>
  <head>
    <style type="text/Css"> 
.dragme{position:relative;} 
  
</style> 
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<!--   <%--> 
	//String index = request.getParameter("index");
	//vo.setIndex(index);
	//String name = request.getParameter("name");
	//vo.setName(name);
	//dao.setVo(vo);
    //List list = dao.portList();
	<!--%>-->

  
  
  </head>

<script type="text/javascript"> 
//alert(document.getElementById("moveid").style.left); 
var ie=document.all; 
var nn6=document.getElementById&&!document.all; 
var isdrag=false; 
var x,y; 
var dobj; 
var getX; 
var getY; 
function movemouse(e){ 
  if (isdrag){ 
   dobj.style.left = nn6 ? tx + e.clientX - x : tx + event.clientX - x; 
   dobj.style.top  = nn6 ? ty + e.clientY - y : ty + event.clientY - y; 
   
   //alert(getX); 
  //alert(getY); 
   return false; 
   } 
} 

function selectmouse(e){ 
    
    var fobj       = nn6 ? e.target : event.srcElement; 
    var topelement = nn6 ? "Html" : "BODY"; 
    while (fobj.tagName != topelement && fobj.className != "dragme") { 
     fobj = nn6 ? fobj.parentNode : fobj.parentElement; 
     } 

 
//fobj = document.getElementById("moveid") 

    if (fobj.className=="dragme") { 
   isdrag = true; 
   dobj = fobj; 
   tx = parseInt(dobj.style.left+0); 
   ty = parseInt(dobj.style.top+0); 
   x = nn6 ? e.clientX : event.clientX; 
   y = nn6 ? e.clientY : event.clientY; 
   document.onmousemove=movemouse; 
   return false; 
    } 
} 

document.onmousedown=selectmouse; 
document.onmouseup=new Function("isdrag=false"); 

//得到元素id号
var allidxy = new Array();
var sel = new Array();
function getID(mid){
	var size = document.getElementById("size").value;
	<%
		if(indexlist != null && indexlist.size()>0){
			for(int k=0;k<indexlist.size();k++){
				String index = (String)indexlist.get(k);
	%>
				findPosition("index"+<%=index%>);
				var select = document.getElementById("sel"+<%=index%>).value;
	    			sel.push(select+";");
	<%
			}
		}
	%>
	allidxy=allidxy.join("");
	
	sel=sel.join("");
	
    document.getElementById("panelxml").value = allidxy;
document.getElementById("select").value = sel;
alert(document.getElementById("select").value);
panelform.action="<%=rootPath%>/panel.do?action=createpanelmodel";
panelform.submit();
}
//得到元素的坐标位置
function findPosition(id) { 
oElement = document.getElementById(id);
  var x2 = 0; 
  var y2 = 0; 
  var width = oElement.offsetWidth; //得到元素的宽度 
  var height = oElement.offsetHeight; 
  //alert(width + "=" + height); 
  if( typeof( oElement.offsetParent ) != 'undefined' ) 
  { 
    for( var posX = 0, posY = 0; oElement; oElement = oElement.offsetParent ) 
    { 
      posX += oElement.offsetLeft; 
      posY += oElement.offsetTop;      
    } 
    x2 = posX + width; 
    y2 = posY + height; 
   // alert("1====="+posX);
    document.getElementById("posX").value = posX;
    document.getElementById("posY").value = posY;
    document.getElementById("id").value = id;
    
    //alert("2====="+posY);
    //return [ posX, posY ,x2, y2]; 
    allidxy.push(posX+','+posY+','+id+';');
    } else{ 
      x2 = oElement.x + width; 
      y2 = oElement.y + height; 
  alert(oElement.x); 
  alert(oElement.y); 
      //return [ oElement.x, oElement.y, x2, y2]; 
  } 
  getX = posX - 200; 
  getY = posY - 10; 
  
  
} 
</script> 

  <body>
  <form name="panelform" method="post"> 
	<input type="hidden" id="lujin" name="virtualname" value=<%=imgPath%>>
	<input type="hidden" id="ret" name="ret" value=<%=returnInfo%>>
	<input type="hidden" id="retflag" name="retflag" value=<%=flag%>>
	<input type="hidden" id="aaaa" name="aaaa" value=<%=virtualname%>>
	<img id="img" src="<%=imgPath%>" >

<P align=center>&nbsp;</P>

  <table>    
          <div align="right">端口列表:</div> 
                                                             
        <%
      
        //int ii=0;
        //if(list!=null){ 
	       /////////Iterator iter=list.iterator();
	       //while(iter.hasNext()){
	       //ii++;
	       ///////////vo.PortsVo to=(vo.PortsVo)iter.next();
	       //String ipaddress = 
	        //Hashtable ht = EquipmentPort.getPortHash(); 
	         
	        //Hashtable hs = (Hashtable)ht.get("web1"); 
	        int size = hs.size();
	        for(int x = 0; x<size;x++){
	        	String[] str = (String[])(hs.get(x).toString()).split(";");
				if(x%8==0){
		    %><tr><td>
		     <div align="left" style="width: 5; height: 5" id = "<%=x%>">
		     <%=str[0]%><%=str[1]%><select name="sel<%=str[0]%>"><option value = "1" >向上</option><option value = "2" >向下</option></select>	      
		     <img src="<%=rootPath%>/panel/view/image/port.gif"  id="index<%=str[0]%>"  class="dragme" >
		     </div><td><%}else if(x%8==1){%> 
			 <td>
		     <div align="left" style="width: 5; height: 5" id = "<%=x%>">
		     <%=str[0]%><%=str[1]%><select name="sel<%=str[0]%>"><option value = "1" >向上</option><option value = "2" >向下</option></select>	      
		     <img src="<%=rootPath%>/panel/view/image/port.gif"  id="index<%=str[0]%>"  class="dragme" >
		     </div><td><%}else if(x%8==2){%> 
			 <td>
		     <div align="left" style="width: 5; height: 5" id = "<%=x%>">
		     <%=str[0]%><%=str[1]%><select name="sel<%=str[0]%>"><option value = "1" >向上</option><option value = "2" >向下</option></select>	      
		     <img src="<%=rootPath%>/panel/view/image/port.gif"  id="index<%=str[0]%>"  class="dragme" >
		     </div><td><%}else if(x%8==3){%> 
			 <td>
		     <div align="left" style="width: 5; height: 5" id = "<%=x%>">
		     <%=str[0]%><%=str[1]%><select name="sel<%=str[0]%>"><option value = "1" >向上</option><option value = "2" >向下</option></select>	      
		     <img src="<%=rootPath%>/panel/view/image/port.gif"  id="index<%=str[0]%>"  class="dragme" >
		     </div><td><%}else if(x%8==4){%> 
			 <td>
		     <div align="left" style="width: 5; height: 5" id = "<%=x%>">
		     <%=str[0]%><%=str[1]%><select name="sel<%=str[0]%>"><option value = "1" >向上</option><option value = "2" >向下</option></select>	      
		     <img src="<%=rootPath%>/panel/view/image/port.gif"  id="index<%=str[0]%>"  class="dragme" >
		     </div><td><%}else if(x%8==5){%> 
			 <td>
		     <div align="left" style="width: 5; height: 5" id = "<%=x%>">
		     <%=str[0]%><%=str[1]%><select name="sel<%=str[0]%>"><option value = "1" >向上</option><option value = "2" >向下</option></select>	      
		     <img src="<%=rootPath%>/panel/view/image/port.gif"  id="index<%=str[0]%>"  class="dragme" >
		     </div><td><%}else if(x%8==6){%> 
			 <td>
		     <div align="left" style="width: 5; height: 5" id = "<%=x%>">
		     <%=str[0]%><%=str[1]%><select name="sel<%=str[0]%>"><option value = "1" >向上</option><option value = "2" >向下</option></select>	      
		     <img src="<%=rootPath%>/panel/view/image/port.gif"  id="index<%=str[0]%>"  class="dragme" >
		     </div><td><%}
			
			
			
			else if(x%8==7){%> 
			 <td>
		     <div align="left" style="width: 5; height: 5" id = "<%=x%>">
		     <%=str[0]%><%=str[1]%><select name="sel<%=str[0]%>"><option value = "1" >向上</option><option value = "2" >向下</option></select>	      
		     <img src="<%=rootPath%>/panel/view/image/port.gif"  id="index<%=str[0]%>"  class="dragme" >
		     </div></td></tr>
		     <%}
			 }%>
		     <div align="center" id="nav">
             <input name="" type="button" value="保存设置" onClick= 'getID("index")'> 
</div>
      
  </table>
  
  <input type = "hidden"  id="panelxml" name = "panelxml">
  <input type = "hidden"  id="posX" name = "posX">
  <input type = "hidden"  id="posY" name = "posY">
  <input type = "hidden"  id="id" name = "id">
  <input type = "hidden"  id="select" name = "select">
  <input type = "hidden"  id="soid" name = "soid" value=<%=host.getSysOid()%>>
  <input type = "hidden"  id="imageType" name = "imageType" value=<%=imageType%>>
     <br>
	图片高度：<input  id="height" name="height">
	<br>
	图片宽度：<input  id="width" name="width">
  <input type = "hidden"  id="size" name = "size" value= <%=size%>>
  </form>
  </body>
</html>
