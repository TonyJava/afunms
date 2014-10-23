<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%
	String rootPath = request.getContextPath();
	String slatype = (String) request.getParameter("slatype");
%>

<html xmlns:v="urn:schemas-microsoft-com:vml">
	<head>

		<title>拓扑图</title>
		<style>
v\: * {
	behavior: url(#default#VML);
}

</style>

		<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.2.6.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/jquery/topo-drag.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/serviceQuality/topology.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery.timers-1.1.2.js"></script>
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/discover.css" type="text/css">
		<script>
			var url="<%=rootPath%>/serviceQualityAjaxManager.ajax?action=showmap&slatype=<%=slatype%>&r="+Math.random();
			
			 $(this).everyTime('30das','topo',function(){
				$("#board").html("");
				showTopology(url);
				
				});
			//停止定时刷新
			function stop(){
			$(this).stopTime('topo',null);
			}
		   //启用定时刷新
			function start(time){
				$(this).everyTime(time,'topo',function(){
				$("#board").html("");
				showTopology(url);
				});
			}
			
			$(document).ready(function(){
			 
              $("#timer").toggle(
			  function () {
			 
			  $("#timer").val("定时已暂停");
			   stop();
			  },
			  function () {
			  var time=$("#selectTime").val();
			  $("#timer").val("定时已启动");
			  start(time);
			  }
			); 
		
			$("#selectTime").bind("click", function(){
			var temp=$("#timer").val();
			var val=$("#selectTime").val();
			   if(temp=="定时已启动"){
			    stop();
			    start(val);
			   }
			}); 
			
             }); 
			
			
		</script>
	</head>

	<body>
	<table>
	  <tr align=center valign=top>
	    
	     <td style="FONT-SIZE: 13px; valign:top">
	       &nbsp;&nbsp;拓扑图刷新间隔: <select id='selectTime'>
					        <option value='6das'>1分钟</option>
					        <option value='12das'>2分钟</option>
					        <option value='18das'>3分钟</option>
					        <option value='30das' selected>5分钟</option>
					        <option value='60das'>10分钟</option>
					        <option value='180das'>30分钟</option>
         </select>
	     </td>
	     <td width="100">
	       <input type="button" value="定时已启动"  id="timer" style="width: 80"
			class=btn_mouseout onmouseover="this.className='btn_mouseover'"
			onmouseout="this.className='btn_mouseout'"
			onmousedown="this.className='btn_mousedown'"
			onmouseup="this.className='btn_mouseup'" />  
	     </td>
	      <td width="120">
	        <input type="button" value="保存拓扑图" onclick="save('<%=rootPath%>','<%=slatype%>')" style="width: 70"
			class=btn_mouseout onmouseover="this.className='btn_mouseover'"
			onmouseout="this.className='btn_mouseout'"
			onmousedown="this.className='btn_mousedown'"
			onmouseup="this.className='btn_mouseup'" />
	     </td>
	  </tr>
	</table>
	
	
     
		<div id="board" style="position:relative;left:10px;top:43px;border:1px dashed blue;width:920px;height:500px;">
		</div>
		<script>
            showTopology(url);
       </script>
	</body>
</html>
