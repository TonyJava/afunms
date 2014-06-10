<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Ping检测</title>
<style type="text/css">
	#bob{
		overflow:hidden;overflow-x:hidden;
		overflow:scroll;overflow-y:hidden;
		speak: none;
		background-color: #FFFFFF;
		margin-left: 0px;  
		margin-top: 0px;  
		margin-right: 0px;  
		margin-bottom: 0px;
		border-width: 0;
	}
</style>
<script type="text/javascript">
		public function openNow(a:String):void{
		 alert("I am a func!"+a);
		}
		 function openWindows(){
			// window.open ('haha.html','height=580, width=600, top=0,left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
			 window.opener=null;
		}

</script>
</head>
<body id="bob" onload="openWindows" style="width:600;height:580;">
	<h1 onclick="openNow('Ping检测')">Ping检测具体信息展示</h1>
</body>
</html>