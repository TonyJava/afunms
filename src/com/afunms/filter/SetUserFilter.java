package com.afunms.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import net.sf.json.JSONObject;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.jasig.cas.client.validation.Assertion;

import wfm.encode.MD5;

import com.afunms.common.util.SessionConstant;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

public class SetUserFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		try {
			String cid = String.valueOf(httpRequest.getSession().getAttribute("casUserID"));
			if ("".equals(cid) || cid == null || "null".equals(cid)) {
				Object object = httpRequest.getSession().getAttribute("_const_cas_assertion_");
				if (object != null) {
					Assertion assertion = (Assertion) object;
					String loginName = assertion.getPrincipal().getName();
					UserDao dao = new UserDao();
					User current_user = null;
					try {
						current_user = dao.loadAllByUser(loginName);
						if (null == current_user) {
							Service service = new Service();
							QName qName = new QName("http://10.161.241.43:7001/wslp/services/WebServiceFun");
							try {
								Call call = (Call) service.createCall();
								call.setTargetEndpointAddress("http://10.161.241.43:7001/wslp/services/WebServiceFun");
								call.setOperation(qName, "getUserRightList");
								JSONObject returnJson = JSONObject.fromObject(call.invoke(new Object[] { loginName, "520200000012" }));
								if (returnJson.get("root") != null && !returnJson.get("root").equals("")) {
									current_user = new User();
									current_user.setUserid(loginName);
									current_user.setName(loginName);
									MD5 md = new MD5();
									current_user.setPassword(md.getMD5ofStr(loginName));
									current_user.setRole(4);
									dao = new UserDao();
									dao.save(current_user);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						httpRequest.getSession().setAttribute(SessionConstant.CURRENT_USER, current_user);
						httpRequest.getSession().setAttribute("casUserID", loginName);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * try { //获取CAS保存的用户信息 AttributePrincipal principal =
		 * (AttributePrincipal) httpRequest .getUserPrincipal(); Map map =
		 * principal.getAttributes(); String username =
		 * String.valueOf(map.get("username")); String cardid =
		 * String.valueOf(map.get("cardid")); System.out.println("username=" +
		 * username); System.out.println("cardid=" + cardid);
		 * System.out.println("-----------结束-----------"); } catch (Exception e)
		 * { e.printStackTrace(); }
		 */
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
