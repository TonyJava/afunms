<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%@page import="com.afunms.common.util.ShareData"%>
<%@page import="com.afunms.polling.node.Disk"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%
String rootPath = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+rootPath+"/";
String id = request.getParameter("id");
String ip = request.getParameter("ip");
List<Disk> disks = null;
Disk disk = null;
Hashtable ipAllData = null;
ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + ip);
if(ipAllData != null){
	disks = (List<Disk>) ipAllData.get("disks");
	disk = disks.get(Integer.parseInt(id));
}

%>
<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	    <link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />	       
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script language="JavaScript" type="text/javascript">
	function toclose()
	{	   
	   window.close();
	}	
	</script>

	</head>

	<body>
		<table class="detail-data-body">
			<tr bgcolor="#ECECEC">
				<td colspan="4" align="left">
					<font style="font-weight: bold">磁盘详细信息:</font>
				</td>
			</tr>
		<%
          if(disk != null)
          {
       %>
			<tr>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;磁盘名称:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getName())%>
				</td>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;磁盘状态:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getStatus())%>
				</td>
			</tr>
			<tr bgcolor="#ECECEC">
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;磁盘声明:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getDiskState())%>
				</td>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;磁盘类型:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getVendorID())%>
				</td>
			</tr>
			<tr>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;产品编号:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getProductID())%>
				</td>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;产品版本:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getProductRevision())%>
				</td>
			</tr>
			<tr bgcolor="#ECECEC">
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;数据容量:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getDataCapacity())%>
				</td>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;块长度:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getBlockLength())%>
				</td>
			</tr>
			<tr>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;地址:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getAddress())%>
				</td>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;节点全局名称:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getNodeWWN())%>
				</td>
			</tr>
			<tr bgcolor="#ECECEC">
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;初始化声明:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getInitializeState())%>
				</td>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;冗余组:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getRedundancyGroup())%>
				</td>
			</tr>
			<tr>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;列序列号:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getVolumeSetSerialNumber())%>
				</td>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;序列号:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getSerialNumber())%>
				</td>
			</tr>
			<tr bgcolor="#ECECEC">
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;固件版本:
				</td>
				<td width="35%">
					<%=SysUtil.ifNull(disk.getFirmwareRevision())%>
				</td>
				<td width="15%" height="26" align="left" nowrap class=txtGlobal>
					&nbsp;
				</td>
				<td width="35%">
					&nbsp;
				</td>
			</tr>
			<%
           }
        %>
        <tr>          
          <td colspan="4" align="center">
			 <input type="button" value="关闭" onclick="toclose()" />			 
		  </td>          
        </tr>          
	   </table>
	</body>
</html>
