<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
    String customXml = request.getParameter("customview");
    String fullscreen = request.getParameter("fullscreen");
    
    session.setAttribute(SessionConstant.CURRENT_CUSTOM_VIEW,customXml);
    response.sendRedirect(request.getContextPath() + "/topology/view/showMap.jsp?fullscreen=" + fullscreen);    
%>