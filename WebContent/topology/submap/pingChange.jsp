<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
    String submapXml = request.getParameter("customview");
    String fullscreen = request.getParameter("fullscreen");
    if(submapXml!=null){
        session.setAttribute(SessionConstant.CURRENT_CUSTOM_VIEW,submapXml);
        response.sendRedirect(request.getContextPath() + "/topology/submap/pingShowMap.jsp?fullscreen=" + fullscreen);    
    }
%>