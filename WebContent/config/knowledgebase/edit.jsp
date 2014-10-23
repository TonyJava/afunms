<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="java.util.*"%>
<%
   Knowledgebase vo = (Knowledgebase)request.getAttribute("vo");
   String rootPath = request.getContextPath();
   KnowledgebaseDao dao = new KnowledgebaseDao();
   String select=(String)session.getAttribute("select");
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>

<!--nielin add for timeShareConfig at 2010-01-04 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<!--nielin add for timeShareConfig at 2010-01-04 end-->


<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
     var chk1 = checkinput("category","string","类别",50,false);
     var chk2 = checkinput("entity","string","类型",50,false);
     var chk3 = checkinput("subentity","string","指标",50,false);
     var chk4 = checkinput("attachfiles","string","附件",200,false);
     var chk5 = checkinput("titles","string","标题",50,false);
     var chk6 = checkinput("contents","string","内容",5000,false);
     if(chk1&&chk2&&chk3&&chk5&&chk4)
     {
     	    
        	mainForm.action = "<%=rootPath%>/knowledgebase.do?action=update";
        	mainForm.submit();
        
     }
 });	
	
});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
//-- nielin modify at 2010-01-04 end ----------------


</script>

<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}
//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}
	timeShareConfiginit(); // nielin add for time-sharing at 2010-01-04
}

</script>
		<!-- 三级级联菜单 数据从表nms_alarm_indicators中查取 -->
		<script type="text/javascript">
			SPT="--请选择类型--";
			SCT="--请选择类别--";
			SAT="--请选择指标--";
			ShowT=1;		//提示文字 0:不显示 1:显示
			PCAD="<%=select%>";
			if(ShowT)PCAD=SPT+"$"+SCT+","+SAT+"#"+PCAD;
			PCAArea=[];
			PCAP=[];
			PCAC=[];
			PCAA=[];
			PCAN=PCAD.split("#");
			for(i=0;i<PCAN.length;i++)
			{
				PCAA[i]=[];TArea=PCAN[i].split("$")[1].split("|");
				for(j=0;j<TArea.length;j++)
				{
					PCAA[i][j]=TArea[j].split(",");
					if(PCAA[i][j].length==1)
					PCAA[i][j][1]=SAT;TArea[j]=TArea[j].split(",")[0]
				}
					PCAArea[i]=PCAN[i].split("$")[0]+","+TArea.join(",");
					PCAP[i]=PCAArea[i].split(",")[0];
					PCAC[i]=PCAArea[i].split(',')
			}
	
			function PCAS()
			{
				this.SelP=document.getElementsByName(arguments[0])[0];
				this.SelC=document.getElementsByName(arguments[1])[0];
				this.SelA=document.getElementsByName(arguments[2])[0];
				this.DefP=this.SelA?arguments[3]:arguments[2];
				this.DefC=this.SelA?arguments[4]:arguments[3];
				this.DefA=this.SelA?arguments[5]:arguments[4];
				this.SelP.PCA=this;
				this.SelC.PCA=this;
				this.SelP.onchange=function(){PCAS.SetC(this.PCA)};
				if(this.SelA)
				this.SelC.onchange=function(){PCAS.SetA(this.PCA)};
				PCAS.SetP(this)
			};
				
				PCAS.SetP=function(PCA){
				for(i=0;i<PCAP.length;i++){PCAPT=PCAPV=PCAP[i];
				if(PCAPT==SPT)PCAPV="";
				PCA.SelP.options.add(new Option(PCAPT,PCAPV));
				if(PCA.DefP==PCAPV)PCA.SelP[i].selected=true}PCAS.SetC(PCA)
				};
				
				PCAS.SetC=function(PCA){
				PI=PCA.SelP.selectedIndex;
				PCA.SelC.length=0;
				for(i=1;i<PCAC[PI].length;i++){PCACT=PCACV=PCAC[PI][i];
				if(PCACT==SCT)PCACV="";
				PCA.SelC.options.add(new Option(PCACT,PCACV));
				if(PCA.DefC==PCACV)
				PCA.SelC[i-1].selected=true}
				if(PCA.SelA)PCAS.SetA(PCA)
				};
				
				PCAS.SetA=function(PCA){
				PI=PCA.SelP.selectedIndex;
				CI=PCA.SelC.selectedIndex;
				PCA.SelA.length=0;
				for(i=1;i<PCAA[PI][CI].length;i++){
				PCAAT=PCAAV=PCAA[PI][CI][i];
				if(PCAAT==SAT)
				PCAAV="";
				PCA.SelA.options.add(new Option(PCAAT,PCAAV));
				if(PCA.DefA==PCAAV)PCA.SelA[i-1].selected=true}
				}
			</script>
			
</head>
<body id="body" class="body" onload="initmenu();">

    <form name="mainForm" method="post">
       <input type=hidden name="id" value="<%=vo.getId()%>">
		<table id="body-container" class="body-container">
   			<tr>
				<td class="td-container-menu-bar">
					<table id="container-menu-bar" class="container-menu-bar">
						<tr>
							<td>
								<%=menuTable%>
							</td>	
						</tr>
					</table>
				</td>
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td>
											<table id="add-content" class="add-content">
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">系统管理 >> 知识库管理 >> 编辑</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1"
																	width="100%">
																
																	<tr style="background-color: #ECECEC;">
						    <TD nowrap align="right" height="24" width="10%">类别&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;&nbsp;&nbsp;<select size="1" name="category">
							</select>
							<font color="red">&nbsp;*</font></TD>
							<TD nowrap align="right" height="24">类型&nbsp;</TD>				
							<TD nowrap>
								&nbsp;&nbsp;&nbsp;<select size="1" name="entity">
							</select>
							<font color="red">&nbsp;*</font>
							</TD>	
						</tr>
						<tr>	
							<TD nowrap align="right" height="24">指标&nbsp;</TD>				
							<TD nowrap>
								&nbsp;&nbsp;&nbsp;<select size="1" name="subentity">
							</select>
							<font color="red">&nbsp;*</font>
							</TD>	
							<script language="javascript" defer>
								new PCAS("category","entity","subentity","<%=vo.getCategory()%>","<%=vo.getEntity()%>","<%=vo.getSubentity()%>");
							</script>					
							<TD nowrap align="right" height="24">附件&nbsp;</TD>
							<TD nowrap>&nbsp;&nbsp; 
								<input type="text" id="aaa" size="20" readonly="readonly" value="<%=vo.getAttachfiles()%>" name="attachfiles">
								<input type="button" value="添加附件" onclick="showup()">
								<script type="text/javascript">
									function showup(){
											CreateWindow("<%=rootPath%>/config/knowledgebase/up2.jsp");
										}
										//重新上传
								</script>
													
							<font color="red">&nbsp;*</font>												
							</TD>	
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="24">标题&nbsp;</TD>
							<TD nowrap colspan="3">
								&nbsp;&nbsp;&nbsp;<input type="text" name="titles" value="<%=vo.getTitles()%>" width="60%" size="80"><font color="red">&nbsp;*</font>
							</TD>
							<td>
						</tr>
						<tr>
							<TD nowrap align="right" height="24">内容&nbsp;</TD>
							<TD nowrap width="33%" colspan="3">
								&nbsp;&nbsp;&nbsp;<textarea rows="10" cols="80" name="contents"><%=vo.getContents()%></textarea><font color="red">&nbsp;*</font>
							</TD>
						</tr>
						
						<!--
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="24">备注&nbsp;</TD>				
							<TD nowrap  colspan="3">&nbsp;<input type="text" name="bak" value="<%=vo.getBak()%>"></TD>
						</tr>			               
						 -->									                      								            							
															<tr style="background-color: #ECECEC;">
																<TD nowrap colspan="4" align=center>
																<br><input type="button" value="修改" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																	<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
																</TD>	
															</tr>	
							
						          </TABLE>										 							
										 							
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-footer" class="detail-content-footer">
				        									<tr>
				        										<td>
				        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
											                  			<tr>
											                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
											                    			<td></td>
											                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
											                  			</tr>
											              			</table>
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        					</table>
										</td>
									</tr>
									<tr>
										<td>
											
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>	

				</td>
			</tr>
	</table>
</form>
</body>
</HTML>