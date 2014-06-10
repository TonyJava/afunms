<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
    String submapXml = request.getParameter("submapview");
    String fullscreen = request.getParameter("fullscreen");
    
    session.setAttribute(SessionConstant.CURRENT_SUBMAP_VIEW,submapXml);
    response.sendRedirect(request.getContextPath() + "/topology/submap/showMap.jsp?fullscreen=" + fullscreen);    
%>