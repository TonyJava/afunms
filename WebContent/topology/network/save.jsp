<%@page language="java" contentType="text/html;charset=GB2312"%>
<%
    String fresh = (String)request.getAttribute("fresh");  
    if("fresh".equals(fresh)){
        response.sendRedirect(request.getContextPath() + "/topology/network/showMap.jsp");    
    }
%>