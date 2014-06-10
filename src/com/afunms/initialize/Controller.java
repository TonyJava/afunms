package com.afunms.initialize;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.validation.Assertion;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.afunms.system.util.CreateMenuTableUtil;
import com.database.config.SystemConfig;

public class Controller extends HttpServlet {

	private static final long serialVersionUID = 541128324260833824L;

	private SAXBuilder builder;
	private ResourceCenter res;

	// 操作权限检查:返回三个值 -1:没有登录,0:没有权限,1:有权限
	public int authenticate(HttpServletRequest request) {
		String action = request.getParameter("action");
		if (action == null) {
			action = "";
		}
		if (action.equals("login") || action.equals("logout")) {
			return 1;
		}
		if (action.equals("kuaizhao")) {
			return 1;
		}
		if (action.equals("xingneng")) {
			return 1;
		}
		if (action.equals("gaojing")) {
			return 1;
		}
		if (action.equals("tuopu")) {
			return 1;
		}
		if (action.equals("perform")) {
			return 1;
		}
		if (action.equals("ssologin")) {
			return 1;
		}
		int result = -1;
		try {
			HttpSession session = request.getSession();
			if (SystemConfig.getConfigInfomation("sso", "issso").equals("true")) {
				boolean res = false;
				try {
					String cid = String.valueOf(request.getSession().getAttribute("casUserID"));
					if ("".equals(cid) || cid == null || "null".equals(cid)) {
						Object object = request.getSession().getAttribute("_const_cas_assertion_");
						if (object != null) {
							Assertion assertion = (Assertion) object;
							String loginName = assertion.getPrincipal().getName();
							if (!loginName.equals("") && loginName != null) {
								res = true;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				String menu = request.getParameter("menu");
				if (menu == null) {
					menu = (String) session.getAttribute(SessionConstant.CURRENT_MENU);
				} else {
					session.setAttribute(SessionConstant.CURRENT_MENU, menu);
					session.setMaxInactiveInterval(1800);
				}
				if (res) {
					return 1;
				}
			} else {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 当前用户
				String menu = request.getParameter("menu");
				if (menu == null) {
					menu = (String) session.getAttribute(SessionConstant.CURRENT_MENU);
				} else {
					session.setAttribute(SessionConstant.CURRENT_MENU, menu);
					session.setMaxInactiveInterval(1800);
				}
				if (user == null) {
					return -1; // 没有登录
				}
			}
			result = 1;
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processHttpRequest(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			processHttpRequest(request, response);
		} catch (Exception e) {

		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		builder = new SAXBuilder();
		res = ResourceCenter.getInstance();
	}

	// 处理业务流程
	private void processHttpRequest(HttpServletRequest request, HttpServletResponse response) {
		String jsp = null;
		try {
			request.setCharacterEncoding("gb2312");
			response.setContentType("text/html;charset=gb2312");
			String uri = request.getRequestURI();
			int lastSeparator = uri.lastIndexOf("/") + 1;
			int dotSeparator = uri.lastIndexOf(".");
			String manageClass = uri.substring(lastSeparator, dotSeparator); // 截取类名映射
			int auth = 0;
			if ("alarm".equals(manageClass)) {
				auth = 1;
			} else {
				auth = authenticate(request);
			}

			if (auth == -1) {
				jsp = "/common/error.jsp?errorcode=" + ErrorMessage.NO_LOGIN;
			} else // 有权限才能继续
			{
				String action = request.getParameter("action");

				ManagerInterface manager = getManagerXml(manageClass);// ManagerFactory.getManager(manageClass);
				manager.setRequest(request);
				jsp = manager.execute(action);
				if (jsp == null) {
					jsp = "/common/error.jsp?errorcode=" + manager.getErrorCode();
				}

			}
			/**
			 * 根据具体页面创建菜单menu
			 */
			CreateMenuTableUtil cmtu = new CreateMenuTableUtil();
			try {
				cmtu.createMenuTableUtil(jsp, request);
			} catch (Exception e) {
				e.printStackTrace();
			}
			RequestDispatcher disp = getServletContext().getRequestDispatcher(jsp);
			if (disp != null && request != null && response != null) {
				disp.forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized ManagerInterface getManagerXml(String action) {
		try {
			Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/manager.xml"));
			List list = doc.getRootElement().getChildren("manager");
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element element = (Element) it.next();
				if (element.getChild("name").getText().equalsIgnoreCase(action)) {
					return (ManagerInterface) Class.forName(element.getChild("class").getText()).newInstance();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
