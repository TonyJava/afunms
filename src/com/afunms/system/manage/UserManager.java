package com.afunms.system.manage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jasig.cas.client.validation.Assertion;

import wfm.encode.MD5;

import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.dao.GrapesConfigDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.dao.ResinDao;
import com.afunms.application.dao.TFTPConfigDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.dao.WebLoginConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.GrapesConfig;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.PSTypeVo;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WebConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CommonAppUtil;
import com.afunms.common.util.FlexDataXml;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.home.role.dao.HomeRoleDao;
import com.afunms.home.role.model.HomeRoleModel;
import com.afunms.home.user.dao.HomeUserDao;
import com.afunms.home.user.model.HomeUserModel;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.security.dao.MgeUpsDao;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.system.dao.PositionDao;
import com.afunms.system.dao.RoleDao;
import com.afunms.system.dao.SysLogDao;
import com.afunms.system.dao.UserAuditDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.SysLog;
import com.afunms.system.model.User;
import com.afunms.system.vo.EventVo;
import com.afunms.system.vo.FlexVo;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.MonitorHostDTO;
import com.afunms.topology.model.MonitorNetDTO;
import com.afunms.topology.model.MonitorNodeDTO;
import com.afunms.topology.model.NodeMonitor;

@SuppressWarnings("unchecked")
public class UserManager extends BaseManager implements ManagerInterface {

	private Logger logger = Logger.getLogger(this.getClass());

	private void createBussTree(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("tree");
			chartxml.addViewTree(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 设备列表
	public void createequipXmlfile(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("equip");
			chartxml.addequipXML(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 被监视对象列表
	public void createEquipXmlfile(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("jianshi");
			chartxml.addEquipXML(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成xml文件公共函数
	 * 
	 * @Author DHCC-huangguolong
	 * @Date 2009-12-17
	 */
	private void createFlexXml(List list, String dir, String xmlFileName, int topN) {
		try {
			FlexDataXml xml = new FlexDataXml(dir, xmlFileName);
			xml.buildXml(list, topN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createOneXmlfile(String filename, String name, String id, String i) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("yewu" + i);
			chartxml.addViewXML(filename, name, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 业务视图权限控制xml
	public void createxmlfile(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("yewu");
			chartxml.addViewXML(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String execute(String action) {
		if (action == null) {
			action = "";
		}
		if (action.equals("setReceiver")) {
			return setReceiver();
		}
		if (action.equals("ssologin")) {
			return ssologin();
		}
		if (action.equals("ready_add")) {
			return readyAdd();
		}
		if (action.equals("add")) {
			return save();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("getUsers")) {
			return getUsers();
		}
		if (action.equals("setdbmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				DBVo vo = new DBVo();
				DBDao dbdao = new DBDao();
				vo = (DBVo) dbdao.findByID(id);
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setdbphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				DBVo vo = new DBVo();
				DBDao dbdao = new DBDao();
				vo = (DBVo) dbdao.findByID(id);
				String userphone = vo.getSendphone();
				if (userphone != null && userphone.trim().length() > 0) {
					String mobiles[] = userphone.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}
		if (action.equals("setwebmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WebConfig vo = new WebConfig();
				WebConfigDao webdao = new WebConfigDao();
				vo = (WebConfig) webdao.findByID(id);
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setwebphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WebConfig vo = new WebConfig();
				WebConfigDao webdao = new WebConfigDao();
				vo = (WebConfig) webdao.findByID(id);
				String userphone = vo.getSendphone();
				if (userphone != null && userphone.trim().length() > 0) {
					String mobiles[] = userphone.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}
		if (action.equals("setmqmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				MQConfig vo = new MQConfig();
				MQConfigDao mqdao = new MQConfigDao();
				vo = (MQConfig) mqdao.findByID(id);
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setmqemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				MQConfig vo = new MQConfig();
				MQConfigDao mqdao = new MQConfigDao();
				vo = (MQConfig) mqdao.findByID(id);
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}
		if (action.equals("setdbphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				MQConfig vo = new MQConfig();
				MQConfigDao mqdao = new MQConfigDao();
				vo = (MQConfig) mqdao.findByID(id);
				String userphone = vo.getSendphone();
				if (userphone != null && userphone.trim().length() > 0) {
					String mobiles[] = userphone.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}
		if (action.equals("setwebemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WebConfig vo = new WebConfig();
				WebConfigDao webdao = new WebConfigDao();
				vo = (WebConfig) webdao.findByID(id);
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}
		if (action.equals("setwebphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WebConfig vo = new WebConfig();
				WebConfigDao webdao = new WebConfigDao();
				vo = (WebConfig) webdao.findByID(id);
				String useremails = vo.getSendphone();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}
		if (action.equals("setdbemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				DBVo vo = new DBVo();
				DBDao dbdao = new DBDao();
				vo = (DBVo) dbdao.findByID(id);
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}
		if (action.equals("list")) {
			DaoInterface dao = new UserDao();
			setTarget("/system/user/list.jsp");
			return list(dao);
		}
		if (action.equals("delete")) {
			boolean result = false;
			String jsp = "/user.do?action=list";
			String[] id = getParaArrayValue("checkbox");
			UserDao dao = new UserDao();
			try {
				result = dao.delete(id);
				if (result) {
					UserAuditDao userAuditDao = new UserAuditDao();
					try {
						for (int i = 0; i < id.length; i++) {
							userAuditDao.deleteByUserId(id[i]);
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						userAuditDao.close();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			return jsp;
		}
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("read")) {
			return read();
		}
		if (action.equals("login")) {
			return login();
		}
		if (action.equals("home")) {
			return home();
		}
		if (action.equals("kuaizhao")) {
			return kuaizhao();
		}
		if (action.equals("xingneng")) {
			return xingneng();
		}
		if (action.equals("gaojing")) {
			return gaojing();
		}
		if (action.equals("tuopu")) {
			return tuopu();
		}
		if (action.equals("personhome")) {
			return personhome();
		}
		if (action.equals("testJQury")) {
			return testJQury();
		}
		if (action.equals("logout")) {
			return logout();
		}
		if (action.equals("setdominomobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				DominoConfig vo = new DominoConfig();
				DominoConfigDao dominodao = new DominoConfigDao();
				vo = (DominoConfig) dominodao.findByID(id);
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setdominoemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				DominoConfig vo = new DominoConfig();
				DominoConfigDao dominodao = new DominoConfigDao();
				vo = (DominoConfig) dominodao.findByID(id);
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}
		if (action.equals("setdominophone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				DominoConfig vo = new DominoConfig();
				DominoConfigDao dominodao = new DominoConfigDao();
				vo = (DominoConfig) dominodao.findByID(id);
				String useremails = vo.getSendphone();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}
		// 设置IIS短信,邮件,电话接受人
		if (action.equals("setiismobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				IISConfig vo = new IISConfig();
				IISConfigDao iisdao = new IISConfigDao();
				try {
					vo = (IISConfig) iisdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					iisdao.close();
				}
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setiisemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				IISConfig vo = new IISConfig();
				IISConfigDao iisdao = new IISConfigDao();
				try {
					vo = (IISConfig) iisdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					iisdao.close();
				}
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}
		if (action.equals("setiisphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				IISConfig vo = new IISConfig();
				IISConfigDao iisdao = new IISConfigDao();
				try {
					vo = (IISConfig) iisdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					iisdao.close();
				}
				String useremails = vo.getSendphone();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}

		if (action.equals("setweblogicmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WeblogicConfig vo = new WeblogicConfig();
				WeblogicConfigDao weblogicdao = new WeblogicConfigDao();
				vo = (WeblogicConfig) weblogicdao.findByID(id);
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setweblogicemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WeblogicConfig vo = new WeblogicConfig();
				WeblogicConfigDao weblogicdao = new WeblogicConfigDao();
				vo = (WeblogicConfig) weblogicdao.findByID(id);
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}

		if (action.equals("setweblogicphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WeblogicConfig vo = new WeblogicConfig();
				WeblogicConfigDao weblogicdao = new WeblogicConfigDao();
				vo = (WeblogicConfig) weblogicdao.findByID(id);
				String userphones = vo.getSendphone();
				if (userphones != null && userphones.trim().length() > 0 && !"null".equalsIgnoreCase(userphones)) {
					String email[] = userphones.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}

		if (action.equals("settomcatmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				Tomcat vo = new Tomcat();
				TomcatDao tomcatdao = new TomcatDao();
				vo = (Tomcat) tomcatdao.findByID(id);
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0 && !"null".equalsIgnoreCase(usermobiles)) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("settomcatemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				Tomcat vo = new Tomcat();
				TomcatDao weblogicdao = new TomcatDao();
				vo = (Tomcat) weblogicdao.findByID(id);
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0 && !"null".equalsIgnoreCase(useremails)) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}
		if (action.equals("settomcatphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				Tomcat vo = new Tomcat();
				TomcatDao tomcatdao = new TomcatDao();
				vo = (Tomcat) tomcatdao.findByID(id);
				String userphone = vo.getSendphone();
				if (userphone != null && userphone.trim().length() > 0 && !"null".equalsIgnoreCase(userphone)) {
					String phones[] = userphone.split(",");
					if (phones != null && phones.length > 0) {
						for (int i = 0; i < phones.length; i++) {
							ids.add(Integer.parseInt(phones[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}
		// 设置SOCKET
		if (action.equals("setsocketmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				PSTypeVo vo = new PSTypeVo();
				PSTypeDao socketdao = new PSTypeDao();
				try {
					vo = (PSTypeVo) socketdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					socketdao.close();
				}
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0 && !"null".equalsIgnoreCase(usermobiles)) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setsocketemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				PSTypeVo vo = new PSTypeVo();
				PSTypeDao socketdao = new PSTypeDao();
				try {
					vo = (PSTypeVo) socketdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					socketdao.close();
				}
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0 && !"null".equalsIgnoreCase(useremails)) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}
		if (action.equals("setsocketphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				PSTypeVo vo = new PSTypeVo();
				PSTypeDao socketdao = new PSTypeDao();
				try {
					vo = (PSTypeVo) socketdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					socketdao.close();
				}
				String userphone = vo.getSendphone();
				if (userphone != null && userphone.trim().length() > 0 && !"null".equalsIgnoreCase(userphone)) {
					String phones[] = userphone.split(",");
					if (phones != null && phones.length > 0) {
						for (int i = 0; i < phones.length; i++) {
							ids.add(Integer.parseInt(phones[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setphone.jsp";
		}

		if (action.equals("setwasmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WasConfig vo = new WasConfig();
				WasConfigDao wasdao = null;
				try {
					wasdao = new WasConfigDao();
					vo = (WasConfig) wasdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (wasdao != null) {
						wasdao.close();
					}
				}
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setwasemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				WasConfig vo = new WasConfig();
				WasConfigDao wasdao = null;
				try {
					wasdao = new WasConfigDao();
					vo = (WasConfig) wasdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (wasdao != null) {
						wasdao.close();
					}
				}
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}

		if (action.equals("setnetmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				HostNode vo = new HostNode();
				HostNodeDao hostdao = new HostNodeDao();
				try {
					vo = (HostNode) hostdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							if (mobiles[i].equalsIgnoreCase("null")) {
								continue;
							}
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setnetemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				HostNode vo = new HostNode();
				HostNodeDao hostdao = new HostNodeDao();
				try {
					vo = (HostNode) hostdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							if (email[i].equalsIgnoreCase("null")) {
								continue;
							}
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}
		if (action.equals("setnetphone")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				HostNode vo = new HostNode();
				HostNodeDao hostdao = new HostNodeDao();
				try {
					vo = (HostNode) hostdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				String userphone = vo.getSendphone();
				if (userphone != null && userphone.trim().length() > 0) {
					String phone[] = userphone.split(",");
					if (phone != null && phone.length > 0) {
						for (int i = 0; i < phone.length; i++) {
							if (phone[i].equalsIgnoreCase("null")) {
								continue;
							}
							ids.add(Integer.parseInt(phone[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setnetphone.jsp";
		}

		if (action.equals("setgrapesmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				GrapesConfig vo = new GrapesConfig();
				GrapesConfigDao grapesdao = new GrapesConfigDao();
				try {
					vo = (GrapesConfig) grapesdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					grapesdao.close();
				}
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}
		if (action.equals("setgrapesemail")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				GrapesConfig vo = new GrapesConfig();
				GrapesConfigDao grapesdao = new GrapesConfigDao();
				try {
					vo = (GrapesConfig) grapesdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					grapesdao.close();
				}
				String useremails = vo.getSendemail();
				if (useremails != null && useremails.trim().length() > 0) {
					String email[] = useremails.split(",");
					if (email != null && email.length > 0) {
						for (int i = 0; i < email.length; i++) {
							ids.add(Integer.parseInt(email[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setemail.jsp";
		}

		if (action.equals("setnetsmsmobiles")) {
			DaoInterface dao = new UserDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			String id = request.getParameter("id");
			Vector ids = new Vector();
			if (id != null && id.length() > 0) {
				HostNode vo = new HostNode();
				HostNodeDao hostdao = new HostNodeDao();
				try {
					vo = (HostNode) hostdao.findByID(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				String usermobiles = vo.getSendmobiles();
				if (usermobiles != null && usermobiles.trim().length() > 0) {
					String mobiles[] = usermobiles.split(",");
					if (mobiles != null && mobiles.length > 0) {
						for (int i = 0; i < mobiles.length; i++) {
							if (mobiles[i].equalsIgnoreCase("null")) {
								continue;
							}
							ids.add(Integer.parseInt(mobiles[i]));
						}
					}
				}
			}
			request.setAttribute("ids", ids);
			return "/system/user/setmobiles.jsp";
		}

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String gaojing() {
		UserDao dao = new UserDao();
		User vo = null;
		try {
			vo = dao.loadAllByUser("portal");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo == null) // 用户名或密码不正确
		{
			setErrorCode(ErrorMessage.INCORRECT_PASSWORD);
			return null;
		}
		session.setAttribute(SessionConstant.CURRENT_USER, vo); // 用户姓名
		session.setMaxInactiveInterval(1900000000);
		CommonAppUtil.setSkin(vo.getSkins());
		this.getHome();
		this.homeModuleSet();
		return "/jsp/portalOutLink/eventGrid.jsp";
	}

	/**
	 * 获得业务权限的 SQL 语句
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 * @return
	 */
	public String getBidSql() {
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);

		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}

		String sql = "";
		if (current_user.getRole() == 0) {
			sql = "";
		} else {
			sql = s.toString();
		}
		return sql;
	}

	// 获取事件列表
	private ArrayList<EventVo> getEventList(String bids) {
		ArrayList<EventVo> flexDataList = new ArrayList<EventVo>();
		List rpceventlist = new ArrayList();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		EventListDao eventdao = new EventListDao();
		String timeFormat = "MM-dd HH:mm:ss";
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
		try {
			rpceventlist = eventdao.getQuery_flex(startTime, endTime, "99", "99", bids, 99);
			if (rpceventlist != null && rpceventlist.size() > 0) {
				for (int i = 0; i < rpceventlist.size(); i++) {
					EventVo Vo = new EventVo();
					EventList event = (EventList) rpceventlist.get(i);
					Vo.setContent(event.getContent());
					Vo.setEventlocation(event.getEventlocation());
					Date d2 = event.getRecordtime().getTime();
					String time = timeFormatter.format(d2);
					Vo.setRecordtime(time);
					String level = String.valueOf(event.getLevel1());
					if ("0".equals(level)) {
						level = "提示信息";
					}
					if ("1".equals(level)) {
						level = "普通告警";
					}
					if ("2".equals(level)) {
						level = "严重告警";
					}
					if ("3".equals(level)) {
						level = "紧急告警";
					}
					Vo.setLevel1(level);
					Vo.setNodeid(event.getNodeid());
					flexDataList.add(Vo);
					if (i == 11) {
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventdao.close();
		}
		return flexDataList;
	}

	public void getHome() {
		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER);

		List rpceventlist = new ArrayList();
		// 网络
		HostNodeDao nodedao = new HostNodeDao();
		List networklist = new ArrayList();

		List routelist = new ArrayList();
		List switchlist = new ArrayList();
		// 服务器
		List hostlist = new ArrayList();
		// 数据库
		List dblist = new ArrayList();
		// 安全
		List seculist = new ArrayList();
		// 存储
		List storagelist = new ArrayList();
		// 服务
		int servicesize = 0;

		// 中间件
		int midsize = 0;

		int routesize = 0;
		int switchsize = 0;
		int storagesize = 0;
		EventListDao eventdao = new EventListDao();

		// 生成事件列表
		try {
			servicesize = getServiceNum(vo);
			midsize = getMiddleService(vo);
			String bids = vo.getBusinessids();
			if (vo.getRole() == 0 || vo.getRole() == 1) {
				bids = "-1";
			}
			// 超级管理员
			try {
				rpceventlist = getEventList(bids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventdao.close();
			}

			try {
				networklist = nodedao.loadNetworkByBid(1, bids);
				seculist = nodedao.loadNetworkByBid(8, bids);
				routelist = nodedao.loadNetworkByBidAndCategory(1, bids);
				switchlist = nodedao.loadNetworkByBidAndCategory(2, bids);
				hostlist = nodedao.loadNetworkByBid(4, bids);
				storagelist = nodedao.loadNetworkByBid(14, bids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodedao.close();
			}
			DBDao dbDao = new DBDao();
			try {
				dblist = dbDao.getDbByMonFlag(1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.removeFlexSession();

			ManageXmlDao manageXmlDao = new ManageXmlDao();
			List xmlList = new ArrayList();
			try {
				xmlList = manageXmlDao.loadByPerAll(bids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				manageXmlDao.close();
			}
			try {
				if (xmlList != null && xmlList.size() > 0) {
					this.createBussTree(xmlList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (eventdao != null) {
				eventdao.close();
			}
			nodedao.close();
		}

		List monitornodelist = getMonitorListByCategory("net_server");
		List hostcpusortlist = new ArrayList();
		List hostmemorysortlist = new ArrayList();
		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);
		if (monitornodelist != null) {
			for (int i = 0; i < monitornodelist.size(); i++) {
				HostNode hostNode = (HostNode) monitornodelist.get(i);
				MonitorNodeDTO monitorNodeDTO = getMonitorNodeDTOByHostNode(hostNode);
				hostcpusortlist.add(monitorNodeDTO);
				hostmemorysortlist.add(monitorNodeDTO);
			}
		}
		;
		monitorListSort(hostcpusortlist, "net", "cpu", "desc");
		monitorListSort(hostmemorysortlist, "net", "memory", "desc");
		request.setAttribute("hostcpusortlist", hostcpusortlist);
		request.setAttribute("hostmemorysortlist", hostmemorysortlist);

		List monitornetlist = getMonitorListByCategory("net");
		List netcpusortlist = new ArrayList();
		List netoutsortlist = new ArrayList();
		List netinsortlist = new ArrayList();
		numberFormat.setMaximumFractionDigits(0);
		if (monitornetlist != null) {
			for (int i = 0; i < monitornetlist.size(); i++) {
				HostNode hostNode = (HostNode) monitornetlist.get(i);
				MonitorNodeDTO monitorNodeDTO = getMonitorNodeDTOByHostNode(hostNode);
				netcpusortlist.add(monitorNodeDTO);
				netoutsortlist.add(monitorNodeDTO);
				netinsortlist.add(monitorNodeDTO);
			}
		}
		monitorListSort(netcpusortlist, "net", "cpu", "desc");
		monitorListSort(netoutsortlist, "net", "oututilhdx", "desc");
		monitorListSort(netinsortlist, "net", "inutilhdx", "desc");

		if (routelist != null) {
			routesize = routelist.size();
		}
		if (switchlist != null) {
			switchsize = switchlist.size();
		}
		if (storagelist != null) {
			storagesize = storagelist.size();
		}

		Hashtable deviceHash = new Hashtable();
		deviceHash.put("routelist", routelist);
		deviceHash.put("hostlist", hostlist);
		deviceHash.put("switchlist", switchlist);
		deviceHash.put("storagelist", storagelist);
		deviceHash.put("dblist", dblist);
		deviceHash.put("seculist", seculist);
		Hashtable treeBidHash = getTreeBidHash(vo, deviceHash);
		request.setAttribute("treeBidHash", treeBidHash);
		session.setAttribute("rpceventlist", rpceventlist);
		session.setAttribute("networklist", networklist);
		session.setAttribute("hostlist", hostlist);
		session.setAttribute("midsize", midsize + "");
		session.setAttribute("servicesize", servicesize + "");
		session.setAttribute("securesize", seculist.size() + "");
		session.setAttribute("dbsize", dblist.size() + "");
		session.setAttribute("routesize", routesize + "");
		session.setAttribute("switchsize", switchsize + "");
		session.setAttribute("storagesize", storagesize + "");

		session.setAttribute("hostcpusortlist", hostcpusortlist);
		session.setAttribute("hostmemorysortlist", hostmemorysortlist);
		session.setAttribute("netcpusortlist", netcpusortlist);
		session.setAttribute("netoutsortlist", netoutsortlist);
		session.setAttribute("netinsortlist", netinsortlist);
	}

	public int getMiddleService(User vo) {
		int midsize = 0;
		// 中间件
		List iislist = new ArrayList();
		List tomcatlist = new ArrayList();
		List weblogiclist = new ArrayList();
		List waslist = new ArrayList();
		List dominolist = new ArrayList();
		List mqlist = new ArrayList();
		List jbosslist = new ArrayList();
		List apachelist = new ArrayList();
		List resinlist = new ArrayList();
		String bids = vo.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0) {
					rbids.add(bid[i].trim());
				}
			}
		}
		if (vo.getRole() == 0 || vo.getRole() == 1) {
			// 超级管理员
			MQConfigDao mqdao = new MQConfigDao();
			try {
				mqlist = mqdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mqdao.close();
			}
			if (mqlist != null && mqlist.size() > 0) {
				midsize = midsize + mqlist.size();
			}
			DominoConfigDao dominodao = new DominoConfigDao();
			try {
				dominolist = dominodao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dominodao.close();
			}
			if (dominolist != null && dominolist.size() > 0) {
				midsize = midsize + dominolist.size();
			}
			WasConfigDao wasdao = new WasConfigDao();
			try {
				waslist = wasdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wasdao.close();
			}
			if (waslist != null && waslist.size() > 0) {
				midsize = midsize + waslist.size();
			}
			WeblogicConfigDao weblogicdao = new WeblogicConfigDao();
			try {
				weblogiclist = weblogicdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				weblogicdao.close();
			}
			if (weblogiclist != null && weblogiclist.size() > 0) {
				midsize = midsize + weblogiclist.size();
			}
			TomcatDao tomcatdao = new TomcatDao();
			try {
				tomcatlist = tomcatdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tomcatdao.close();
			}
			if (tomcatlist != null && tomcatlist.size() > 0) {
				midsize = midsize + tomcatlist.size();
			}
			IISConfigDao iisdao = new IISConfigDao();
			try {
				iislist = iisdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				iisdao.close();
			}
			if (iislist != null && iislist.size() > 0) {
				midsize = midsize + iislist.size();
			}
			ApacheConfigDao apachedao = new ApacheConfigDao();
			try {
				apachelist = apachedao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				apachedao.close();
			}
			if (apachelist != null && apachelist.size() > 0) {
				midsize = midsize + apachelist.size();
			}
			ResinDao resindao = new ResinDao();
			try {
				resinlist = resindao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				resindao.close();
			}
			if (resinlist != null && resinlist.size() > 0) {
				midsize = midsize + resinlist.size();
			}
			JBossConfigDao jbossdao = new JBossConfigDao();
			try {
				jbosslist = jbossdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				jbossdao.close();
			}
			if (jbosslist != null && jbosslist.size() > 0) {
				midsize = midsize + jbosslist.size();
			}
		} else {
			MQConfigDao mqdao = new MQConfigDao();
			try {
				mqlist = mqdao.getMQByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mqdao.close();
			}
			if (mqlist != null && mqlist.size() > 0) {
				midsize = midsize + mqlist.size();
			}
			DominoConfigDao dominodao = new DominoConfigDao();
			try {
				dominolist = dominodao.getDominoByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dominodao.close();
			}
			if (dominolist != null && dominolist.size() > 0) {
				midsize = midsize + dominolist.size();
			}
			WasConfigDao wasdao = new WasConfigDao();
			try {
				waslist = wasdao.getWasByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wasdao.close();
			}
			if (waslist != null && waslist.size() > 0) {
				midsize = midsize + waslist.size();
			}
			WeblogicConfigDao weblogicdao = new WeblogicConfigDao();
			try {
				weblogiclist = weblogicdao.getWeblogicByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				weblogicdao.close();
			}
			if (weblogiclist != null && weblogiclist.size() > 0) {
				midsize = midsize + weblogiclist.size();
			}
			TomcatDao tomcatdao = new TomcatDao();
			try {
				tomcatlist = tomcatdao.getTomcatByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tomcatdao.close();
			}
			if (tomcatlist != null && tomcatlist.size() > 0) {
				midsize = midsize + tomcatlist.size();
			}
			ResinDao resindao = new ResinDao();
			try {
				resinlist = resindao.getResinByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				resindao.close();
			}
			if (resinlist != null && resinlist.size() > 0) {
				midsize = midsize + resinlist.size();
			}
			IISConfigDao iisdao = new IISConfigDao();
			try {
				iislist = iisdao.getIISByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				iisdao.close();
			}
			if (iislist != null && iislist.size() > 0) {
				midsize = midsize + iislist.size();
			}
			ApacheConfigDao apachedao = new ApacheConfigDao();
			try {
				apachelist = apachedao.getApacheByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				apachedao.close();
			}
			if (apachelist != null && apachelist.size() > 0) {
				midsize = midsize + apachelist.size();
			}
			JBossConfigDao jbossdao = new JBossConfigDao();
			try {
				jbosslist = jbossdao.getJBossByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				jbossdao.close();
			}
			if (jbosslist != null && jbosslist.size() > 0) {
				midsize = midsize + jbosslist.size();
			}
		}
		if (session != null) {
			session.setAttribute("iislist", iislist);
			session.setAttribute("tomcatlist", tomcatlist);
			session.setAttribute("weblogiclist", weblogiclist);
			session.setAttribute("waslist", waslist);
			session.setAttribute("dominolist", dominolist);
			session.setAttribute("mqlist", mqlist);
			session.setAttribute("jbosslist", jbosslist);
			session.setAttribute("apachelist", apachelist);
			session.setAttribute("resinlist", resinlist);
		}
		return midsize;
	}

	public List getMonitorListByCategory(String category) {

		String where = "";

		if ("node".equals(category)) {
			where = " where managed=1";
		} else if ("net_server".equals(category)) {
			where = " where managed=1 and category=4";
		} else if ("net".equals(category)) {
			where = " where managed=1 and (category=1 or category=2 or category=3 or category=7) ";
		} else if ("net_router".equals(category)) {
			where = " where managed=1 and category=1";
		} else if ("net_switch".equals(category)) {
			where = " where managed=1 and (category=2 or category=3 or category=7) ";
		}

		where = where + getBidSql();

		HostNodeDao dao = new HostNodeDao();

		String key = getParaValue("key");

		String value = getParaValue("value");
		if (key != null && key.trim().length() > 0 && value != null && value.trim().length() > 0) {
			where = where + " and " + key + "='" + value + "'";
		}
		try {
			list(dao, where);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		List list = (List) request.getAttribute("list");
		return list;
	}

	/**
	 * 通过 hostNode 来组装 MonitorNodeDTO
	 * 
	 * @param hostNode
	 * @return
	 */
	public MonitorNodeDTO getMonitorNodeDTOByHostNode(HostNode hostNode) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);

		MonitorNodeDTO monitorNodeDTO = null;

		String ipAddress = hostNode.getIpAddress();
		int nodeId = hostNode.getId();
		String alias = hostNode.getAlias();

		int category = hostNode.getCategory();
		if (category == 1) {
			monitorNodeDTO = new MonitorNetDTO();
			monitorNodeDTO.setCategory("路由器");
		} else if (category == 4) {
			monitorNodeDTO = new MonitorHostDTO();
			monitorNodeDTO.setCategory("服务器");
		} else {
			monitorNodeDTO = new MonitorNetDTO();
			monitorNodeDTO.setCategory("交换机");
		}

		// 设置id
		monitorNodeDTO.setId(nodeId);
		// 设置ip
		monitorNodeDTO.setIpAddress(ipAddress);
		// 设置名称
		monitorNodeDTO.setAlias(alias);

		Host node = (Host) PollingEngine.getInstance().getNodeByID(nodeId);
		// 告警状态
		if (node != null) {
			monitorNodeDTO.setStatus(node.getStatus() + "");
		} else {
			monitorNodeDTO.setStatus("0");
		}

		String cpuValue = "0"; // cpu 默认为 0
		String memoryValue = "0"; // memory 默认为 0
		String inutilhdxValue = "0"; // inutilhdx 默认为 0
		String oututilhdxValue = "0"; // oututilhdx 默认为 0
		String pingValue = "0"; // ping 默认为 0
		String eventListCount = ""; // eventListCount 默认为 0
		String collectType = ""; // 采集类型

		String cpuValueColor = "green"; // cpu 颜色
		String memoryValueColor = "green"; // memory 颜色

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		double cpuValueDouble = 0;
		double memeryValueDouble = 0;

		Hashtable eventListSummary = new Hashtable();

		Hashtable sharedata = ShareData.getSharedata();

		Hashtable ipAllData = (Hashtable) sharedata.get(ipAddress);

		Hashtable allpingdata = ShareData.getPingdata();

		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {
				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				if (cpu != null && cpu.getThevalue() != null) {
					cpuValueDouble = Double.valueOf(cpu.getThevalue());
					cpuValue = numberFormat.format(cpuValueDouble);
				}
			}

			Vector memoryVector = (Vector) ipAllData.get("memory");

			int allmemoryvalue = 0;
			if (memoryVector != null && memoryVector.size() > 0) {
				for (int si = 0; si < memoryVector.size(); si++) {
					MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
					if (memorydata.getEntity().equalsIgnoreCase("Utilization")) {
						if (category == 4 && memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")) {// 服务器的情况
							memeryValueDouble = Double.valueOf(memorydata.getThevalue());
						}
						if (category == 1 || category == 2 || category == 3) {// 网络设备的情况
							allmemoryvalue = allmemoryvalue + Integer.parseInt(memorydata.getThevalue());
							if (si == memoryVector.size() - 1) {
								memeryValueDouble = allmemoryvalue / memoryVector.size();
							}
						}
					}
				}
				memoryValue = numberFormat.format(memeryValueDouble);
			}

			Vector allutil = (Vector) ipAllData.get("allutilhdx");
			if (allutil != null && allutil.size() == 3) {
				AllUtilHdx inutilhdx = (AllUtilHdx) allutil.get(0);
				inutilhdxValue = inutilhdx.getThevalue();

				AllUtilHdx oututilhdx = (AllUtilHdx) allutil.get(1);
				oututilhdxValue = oututilhdx.getThevalue();
			}
		}

		if (allpingdata != null) {
			Vector pingData = (Vector) allpingdata.get(ipAddress);
			if (pingData != null && pingData.size() > 0) {
				PingCollectEntity pingcollectdata = (PingCollectEntity) pingData.get(0);
				pingValue = pingcollectdata.getThevalue();
			}
		}
		String count = "";
		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + hostNode.getId() + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='"
						+ totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + hostNode.getId() + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='"
						+ totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + hostNode.getId() + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='"
						+ totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid=" + hostNode.getId() + "" + " and level1=1 and recordtime>=to_date('" + starttime
						+ "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid=" + hostNode.getId() + "" + " and level1=2 and recordtime>=to_date('" + starttime
						+ "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid=" + hostNode.getId() + "" + " and level1=3 and recordtime>=to_date('" + starttime
						+ "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListCount = count;
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		monitorNodeDTO.setEventListSummary(eventListSummary);

		if (SystemConstant.COLLECTTYPE_SNMP == hostNode.getCollecttype()) {
			collectType = "SNMP";
		} else if (SystemConstant.COLLECTTYPE_PING == hostNode.getCollecttype()) {
			collectType = "PING";
		} else if (SystemConstant.COLLECTTYPE_REMOTEPING == hostNode.getCollecttype()) {
			collectType = "REMOTEPING";
		} else if (SystemConstant.COLLECTTYPE_SHELL == hostNode.getCollecttype()) {
			collectType = "SHELL";
		} else if (SystemConstant.COLLECTTYPE_SSH == hostNode.getCollecttype()) {
			collectType = "SSH";
		} else if (SystemConstant.COLLECTTYPE_TELNET == hostNode.getCollecttype()) {
			collectType = "TELNET";
		} else if (SystemConstant.COLLECTTYPE_WMI == hostNode.getCollecttype()) {
			collectType = "WMI";
		}

		NodeMonitorDao nodeMonitorDao = new NodeMonitorDao();

		List nodeMonitorList = null;
		try {
			nodeMonitorList = nodeMonitorDao.loadByNodeID(nodeId);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			nodeMonitorDao.close();
		}
		if (nodeMonitorList != null) {
			for (int j = 0; j < nodeMonitorList.size(); j++) {
				NodeMonitor nodeMonitor = (NodeMonitor) nodeMonitorList.get(j);
				if ("cpu".equals(nodeMonitor.getCategory())) {
					if (cpuValueDouble > nodeMonitor.getLimenvalue2()) {
						cpuValueColor = "red";
					} else if (cpuValueDouble > nodeMonitor.getLimenvalue1()) {
						cpuValueColor = "orange";
					} else if (cpuValueDouble > nodeMonitor.getLimenvalue0()) {
						cpuValueColor = "yellow";
					} else {
						cpuValueColor = "green";
					}
				}

				if ("memory".equals(nodeMonitor.getCategory())) {
					if (memeryValueDouble > nodeMonitor.getLimenvalue2()) {
						memoryValueColor = "red";
					} else if (memeryValueDouble > nodeMonitor.getLimenvalue1()) {
						memoryValueColor = "orange";
					} else if (memeryValueDouble > nodeMonitor.getLimenvalue0()) {
						memoryValueColor = "yellow";
					} else {
						memoryValueColor = "green";
					}
				}
			}
		}

		monitorNodeDTO.setCpuValue(cpuValue);
		monitorNodeDTO.setMemoryValue(memoryValue);
		monitorNodeDTO.setInutilhdxValue(inutilhdxValue);
		monitorNodeDTO.setOututilhdxValue(oututilhdxValue);
		monitorNodeDTO.setPingValue(pingValue);
		monitorNodeDTO.setEventListCount(eventListCount);
		monitorNodeDTO.setCollectType(collectType);
		monitorNodeDTO.setCpuValueColor(cpuValueColor);
		monitorNodeDTO.setMemoryValueColor(memoryValueColor);
		return monitorNodeDTO;
	}

	public void getPersonHome() {
		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		Date d = new Date();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String startdate = sdf0.format(d);
		String todate = sdf0.format(d);

		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		List rpceventlist = new ArrayList();
		// 网络
		HostNodeDao nodedao = new HostNodeDao();
		List networklist = new ArrayList();

		List routelist = new ArrayList();
		List switchlist = new ArrayList();
		// 服务器
		List hostlist = new ArrayList();
		// 数据库
		List dblist = new ArrayList();
		DBDao dbdao = new DBDao();

		// 安全
		List seculist = new ArrayList();

		// 服务
		int servicesize = 0;

		// 中间件
		int midsize = 0;

		int routesize = 0;
		int switchsize = 0;

		EventListDao eventdao = new EventListDao();

		// 生成事件列表
		try {
			servicesize = getServiceNum(vo);
			midsize = getMiddleService(vo);
			String bids = vo.getBusinessids();
			if (vo.getRole() == 0 || vo.getRole() == 1) {
				bids = "-1";
			}
			// 超级管理员
			try {
				rpceventlist = eventdao.getQuery(starttime, totime, "99", "99", bids, -1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventdao.close();
			}
			try {

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodedao.close();
			}
			try {
				networklist = nodedao.loadNetworkByBid(1, bids);
				seculist = nodedao.loadNetworkByBid(8, bids);
				routelist = nodedao.loadNetworkByBidAndCategory(1, bids);
				switchlist = nodedao.loadNetworkByBidAndCategory(2, bids);
				hostlist = nodedao.loadNetworkByBid(4, bids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodedao.close();
			}
			OraclePartsDao partdao = null;
			DBTypeDao typedao = null;
			try {
				dblist = dbdao.getDbByBID(bids);

				typedao = new DBTypeDao();
				List list1 = new LinkedList();
				if (dblist != null) {
					DBTypeVo oracletypevo = null;

					oracletypevo = (DBTypeVo) typedao.findByDbtype("oracle");
					for (int i = 0; i < dblist.size(); i++) {
						DBVo vo1 = (DBVo) dblist.get(i);
						if (vo1.getDbtype() != oracletypevo.getId()) {
							list1.add(vo1);
						}
					}
					String[] bidarr = bids.split(",");
					Vector vec = new Vector();
					for (String bid : bidarr) {
						if (!"".equals(bid.trim())) {
							vec.add(bid);
						}
					}
					if (dbdao != null) {
						dbdao.close();
					}
					dbdao = new DBDao();
					List oraclelist = dbdao.getDbByType(oracletypevo.getId());
					if (oraclelist != null) {

						for (int i = 0; i < oraclelist.size(); i++) {
							partdao = new OraclePartsDao();
							try {
								DBVo oraclevo = (DBVo) oraclelist.get(i);
								List<OracleEntity> oracleparts = partdao.getOraclesByDbAndBid(oraclevo.getId(), vec);
								for (OracleEntity ora : oracleparts) {
									DBVo nvo = new DBVo();
									nvo.setAlias(ora.getAlias());
									nvo.setBid(ora.getBid());
									nvo.setCategory(oraclevo.getCategory());
									nvo.setCollecttype(ora.getCollectType());
									nvo.setDbName(ora.getSid());
									nvo.setDbtype(oraclevo.getDbtype());
									nvo.setId(ora.getId());
									nvo.setIpAddress(oraclevo.getIpAddress() + ":" + ora.getSid());
									nvo.setManaged(oraclevo.getManaged());
									nvo.setPassword(ora.getPassword());
									nvo.setPort(oraclevo.getPort());
									nvo.setSendemail(oraclevo.getSendemail());
									nvo.setUser(ora.getUser());
									list1.add(nvo);
								}

							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								partdao.close();
							}
						}
					}
					dblist = list1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbdao.close();
				typedao.close();
			}

			List<FlexVo> evList = new ArrayList<FlexVo>();
			FlexVo fVo = new FlexVo();
			fVo.setObjectName("网络设备");
			fVo.setObjectNumber(networklist.size() + "");
			evList.add(fVo);
			fVo = new FlexVo();
			fVo.setObjectName("服务器");
			fVo.setObjectNumber(hostlist.size() + "");
			evList.add(fVo);
			fVo = new FlexVo();
			fVo.setObjectName("数据库");
			fVo.setObjectNumber(dblist.size() + "");
			evList.add(fVo);
			fVo = new FlexVo();
			fVo.setObjectName("应用服务");
			fVo.setObjectNumber(servicesize + "");
			evList.add(fVo);
			fVo = new FlexVo();
			fVo.setObjectName("中间件");
			fVo.setObjectNumber(midsize + "");
			evList.add(fVo);
			fVo = new FlexVo();
			fVo.setObjectName("安全设备");
			fVo.setObjectNumber(seculist.size() + "");
			evList.add(fVo);

			this.removeFlexSession();
			session.setAttribute("deviceList", evList);
			this.createFlexXml(evList, null, "pie_summary_surveillance", 9999);
			ManageXmlDao manageXmlDao = new ManageXmlDao();
			List xmlList = new ArrayList();
			try {
				xmlList = manageXmlDao.loadByPerAll(bids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				manageXmlDao.close();
			}
			try {
				if (xmlList != null && xmlList.size() > 0) {
					this.createBussTree(xmlList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventdao.close();
			nodedao.close();
			dbdao.close();
		}

		if (routelist != null) {
			routesize = routelist.size();
		}
		if (switchlist != null) {
			switchsize = switchlist.size();
		}
		session.setAttribute("rpceventlist", rpceventlist);
		session.setAttribute("networklist", networklist);
		session.setAttribute("hostlist", hostlist);
		session.setAttribute("midsize", midsize + "");
		session.setAttribute("servicesize", servicesize + "");
		session.setAttribute("securesize", seculist.size() + "");
		session.setAttribute("dbsize", dblist.size() + "");
		session.setAttribute("routesize", routesize + "");
		session.setAttribute("switchsize", switchsize + "");
	}

	public int getServiceNum(User vo) {
		int servicesize = 0;
		List socketlist = new ArrayList();
		List ftplist = new ArrayList();
		List tftplist = new ArrayList();
		List dhcplist = new ArrayList();
		List emaillist = new ArrayList();
		List weblist = new ArrayList();
		List webloginlist = new ArrayList();
		PSTypeDao psdao = new PSTypeDao();
		FTPConfigDao ftpdao = new FTPConfigDao();
		EmailConfigDao emaildao = new EmailConfigDao();
		WebConfigDao webdao = new WebConfigDao();
		WebLoginConfigDao weblogindao = new WebLoginConfigDao();
		String bids = vo.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0) {
					rbids.add(bid[i].trim());
				}
			}
		}
		if (vo.getRole() == 0 || vo.getRole() == 1) {
			// 超级管理员
			try {
				socketlist = psdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				psdao.close();
			}
			if (socketlist != null && socketlist.size() > 0) {
				servicesize = servicesize + socketlist.size();
			}
			try {
				ftplist = ftpdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ftpdao.close();
			}
			if (ftplist != null && ftplist.size() > 0) {
				servicesize = servicesize + ftplist.size();
			}

			TFTPConfigDao tftpdao = new TFTPConfigDao();
			try {
				tftplist = tftpdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tftpdao.close();
			}
			if (tftplist != null && tftplist.size() > 0) {
				servicesize = servicesize + tftplist.size();
			}

			DHCPConfigDao dhcpdao = new DHCPConfigDao();
			try {
				dhcplist = dhcpdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dhcpdao.close();
			}
			if (dhcplist != null && dhcplist.size() > 0) {
				servicesize = servicesize + dhcplist.size();
			}

			try {
				emaillist = emaildao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				emaildao.close();
			}
			if (emaillist != null && emaillist.size() > 0) {
				servicesize = servicesize + emaillist.size();
			}
			try {
				weblist = webdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				webdao.close();
			}
			if (weblist != null && weblist.size() > 0) {
				servicesize = servicesize + weblist.size();
			}
			try {
				webloginlist = weblogindao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				weblogindao.close();
			}
			if (webloginlist != null && webloginlist.size() > 0) {
				servicesize = servicesize + webloginlist.size();
			}
		} else {
			try {
				socketlist = psdao.getSocketByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				psdao.close();
			}
			if (socketlist != null && socketlist.size() > 0) {
				servicesize = servicesize + socketlist.size();
			}
			try {
				ftplist = ftpdao.getFtpByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ftpdao.close();
			}
			if (ftplist != null && ftplist.size() > 0) {
				servicesize = servicesize + ftplist.size();
			}
			try {
				emaillist = emaildao.getByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				emaildao.close();
			}
			if (emaillist != null && emaillist.size() > 0) {
				servicesize = servicesize + emaillist.size();
			}
			try {
				webloginlist = webdao.getWebByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				webdao.close();
			}
			if (webloginlist != null && webloginlist.size() > 0) {
				servicesize = servicesize + webloginlist.size();
			}
		}
		if (session != null) {
			session.setAttribute("socketlist", socketlist);
			session.setAttribute("ftplist", ftplist);
			session.setAttribute("emaillist", emaillist);
			session.setAttribute("weblist", weblist);
			session.setAttribute("webloginlist", webloginlist);
		}
		return servicesize;
	}

	/**
	 * HONGLI 得到设备快照中跳转到性能列表时 链接后对应的bid集合
	 * 
	 * @param vo
	 * @param deviceHash
	 * @return
	 */
	private Hashtable getTreeBidHash(User vo, Hashtable deviceHash) {
		Hashtable treeBidHash = new Hashtable();
		String bids = vo.getBusinessids();
		String[] bidsArr = bids.split(",");
		// 得到设备快照中路由器跳转到的所属业务的id
		String treeBidRouter = "";
		String treeBidHost = "";
		String treeBidSwitch = "";
		String treeBidDb = "";
		String treeBidMid = "";
		String treeBidService = "";
		String treeBidSecu = "";
		boolean loopRoute = true;
		boolean loopHost = true;
		boolean loopSwitch = true;
		boolean loopDb = true;
		boolean loopMid = true;
		boolean loopService = true;
		boolean loopSecu = true;
		// 默认选择该用户第一个所属业务作为跳转的treeBid
		List routelist = null;
		List hostlist = null;
		List switchlist = null;
		List dblist = null;
		List seculist = null;
		// 中间件
		List iislist = (ArrayList) session.getAttribute("iislist");
		List tomcatlist = (ArrayList) session.getAttribute("tomcatlist");
		List weblogiclist = (ArrayList) session.getAttribute("weblogiclist");
		List waslist = (ArrayList) session.getAttribute("waslist");
		List dominolist = (ArrayList) session.getAttribute("dominolist");
		List mqlist = (ArrayList) session.getAttribute("mqlist");
		List jbosslist = (ArrayList) session.getAttribute("jbosslist");
		List apachelist = (ArrayList) session.getAttribute("apachelist");
		// 服务
		List socketlist = (ArrayList) session.getAttribute("socketlist");
		List ftplist = (ArrayList) session.getAttribute("ftplist");
		List emaillist = (ArrayList) session.getAttribute("emaillist");
		List weblist = (ArrayList) session.getAttribute("weblist");

		if (deviceHash.containsKey("routelist")) {
			routelist = (ArrayList) deviceHash.get("routelist");
		}
		if (deviceHash.containsKey("hostlist")) {
			hostlist = (ArrayList) deviceHash.get("hostlist");
		}
		if (deviceHash.containsKey("switchlist")) {
			switchlist = (ArrayList) deviceHash.get("switchlist");
		}
		if (deviceHash.containsKey("dblist")) {
			dblist = (List) deviceHash.get("dblist");
		}
		if (deviceHash.containsKey("seculist")) {
			seculist = (ArrayList) deviceHash.get("seculist");
		}
		for (int i = 0; i < bidsArr.length; i++) {
			if (bidsArr[i] != null && !bidsArr[i].equals("")) {
				// 查看当前所属业务下是否存在该类设备
				if (routelist != null && loopRoute) {
					for (int j = 0; j < routelist.size(); j++) {
						HostNode node = (HostNode) routelist.get(j);
						if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
							// 该路由器不属于该业务 继续循环检测
							continue;
						} else {
							// 该路由器属于该业务 跳出循环体
							treeBidRouter = bidsArr[i];
							loopRoute = false;
							break;
						}
					}
				}
				// 查看当前所属业务下是否存在该类设备
				if (hostlist != null && loopHost) {
					for (int j = 0; j < hostlist.size(); j++) {
						HostNode node = (HostNode) hostlist.get(j);
						if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
							// 该路由器不属于该业务 继续循环检测
							continue;
						} else {
							// 该路由器属于该业务 跳出循环体
							treeBidHost = bidsArr[i];
							loopHost = false;
							break;
						}
					}
				}
				// 查看当前所属业务下是否存在该类设备
				if (switchlist != null && loopSwitch) {
					for (int j = 0; j < switchlist.size(); j++) {
						HostNode node = (HostNode) switchlist.get(j);
						if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
							// 该路由器不属于该业务 继续循环检测
							continue;
						} else {
							// 该路由器属于该业务 跳出循环体
							treeBidSwitch = bidsArr[i];
							loopSwitch = false;
							break;
						}
					}
				}
				// 查看当前所属业务下是否存在该类设备
				if (dblist != null && loopDb) {
					for (int j = 0; j < dblist.size(); j++) {
						DBVo node = (DBVo) dblist.get(j);
						if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
							// 该路由器不属于该业务 继续循环检测
							continue;
						} else {
							// 该路由器属于该业务 跳出循环体
							treeBidDb = bidsArr[i];
							loopDb = false;
							break;
						}
					}
				}
				// 查看当前所属业务下是否存在该类设备
				if (loopMid) {
					if (iislist != null && loopMid) {
						for (int j = 0; j < iislist.size(); j++) {
							IISConfig node = (IISConfig) iislist.get(j);
							if (node != null && node.getNetid() != null && !node.getNetid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidMid = bidsArr[i];
								loopMid = false;
								break;
							}
						}
					}
					if (tomcatlist != null && loopMid) {
						for (int j = 0; j < tomcatlist.size(); j++) {
							Tomcat node = (Tomcat) tomcatlist.get(j);
							if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidMid = bidsArr[i];
								loopDb = false;
								break;
							}
						}
					}
					if (weblogiclist != null && loopMid) {
						for (int j = 0; j < weblogiclist.size(); j++) {
							WeblogicConfig node = (WeblogicConfig) weblogiclist.get(j);
							if (node != null && node.getNetid() != null && !node.getNetid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidMid = bidsArr[i];
								loopDb = false;
								break;
							}
						}
					}
					if (waslist != null && loopMid) {
						for (int j = 0; j < waslist.size(); j++) {
							WasConfig node = (WasConfig) waslist.get(j);
							if (node != null && node.getNetid() != null && !node.getNetid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidMid = bidsArr[i];
								loopDb = false;
								break;
							}
						}
					}
					if (dominolist != null && loopMid) {
						for (int j = 0; j < dominolist.size(); j++) {
							DominoConfig node = (DominoConfig) dominolist.get(j);
							if (node != null && node.getNetid() != null && !node.getNetid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidMid = bidsArr[i];
								loopDb = false;
								break;
							}
						}
					}
					if (mqlist != null && loopMid) {
						for (int j = 0; j < mqlist.size(); j++) {
							MQConfig node = (MQConfig) mqlist.get(j);
							if (node != null && node.getNetid() != null && !node.getNetid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidMid = bidsArr[i];
								loopDb = false;
								break;
							}
						}
					}
					if (jbosslist != null && loopMid) {
						for (int j = 0; j < jbosslist.size(); j++) {
							JBossConfig node = (JBossConfig) jbosslist.get(j);
							if (node != null && node.getNetid() != null && !node.getNetid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidMid = bidsArr[i];
								loopDb = false;
								break;
							}
						}
					}
					if (apachelist != null && loopMid) {
						for (int j = 0; j < apachelist.size(); j++) {
							ApacheConfig node = (ApacheConfig) apachelist.get(j);
							if (node != null && node.getNetid() != null && !node.getNetid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidMid = bidsArr[i];
								loopDb = false;
								break;
							}
						}
					}
				}
				// 查看当前所属业务下是否存在该类设备
				if (loopService) {
					if (socketlist != null && loopService) {
						for (int j = 0; j < socketlist.size(); j++) {
							PSTypeVo node = (PSTypeVo) socketlist.get(j);
							if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidService = bidsArr[i];
								loopService = false;
								break;
							}
						}
					}
					if (ftplist != null && loopService) {
						for (int j = 0; j < ftplist.size(); j++) {
							FTPConfig node = (FTPConfig) ftplist.get(j);
							if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
								// 该FTP不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidService = bidsArr[i];
								loopService = false;
								break;
							}
						}
					}
					if (emaillist != null && loopService) {
						for (int j = 0; j < emaillist.size(); j++) {
							EmailMonitorConfig node = (EmailMonitorConfig) emaillist.get(j);
							if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidService = bidsArr[i];
								loopService = false;
								break;
							}
						}
					}
					if (weblist != null && loopService) {
						for (int j = 0; j < weblist.size(); j++) {
							WebConfig node = (WebConfig) weblist.get(j);
							if (node != null && node.getNetid() != null && !node.getNetid().contains(bidsArr[i])) {
								// 该路由器不属于该业务 继续循环检测
								continue;
							} else {
								// 该路由器属于该业务 跳出循环体
								treeBidService = bidsArr[i];
								loopService = false;
								break;
							}
						}
					}
				}
				// 查看当前所属业务下是否存在该类设备
				if (seculist != null && loopSecu) {
					for (int j = 0; j < seculist.size(); j++) {
						HostNode node = (HostNode) seculist.get(j);
						if (node != null && node.getBid() != null && !node.getBid().contains(bidsArr[i])) {
							// 该路由器不属于该业务 继续循环检测
							continue;
						} else {
							// 该路由器属于该业务 跳出循环体
							treeBidSecu = bidsArr[i];
							loopSecu = false;
							break;
						}
					}
				}
			}
		}
		treeBidHash.put("treeBidRouter", treeBidRouter);
		treeBidHash.put("treeBidHost", treeBidHost);
		treeBidHash.put("treeBidSwitch", treeBidSwitch);
		treeBidHash.put("treeBidDb", treeBidDb);
		treeBidHash.put("treeBidMid", treeBidMid);
		treeBidHash.put("treeBidService", treeBidService);
		treeBidHash.put("treeBidSecu", treeBidSecu);
		return treeBidHash;
	}

	/**
	 * 根据当前用户，获取ups
	 * 
	 * @param vo
	 * @return
	 */
	public int getUpsNum(User vo) {
		int upssize = 0;
		String bids = vo.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0) {
					rbids.add(bid[i].trim());
				}
			}
		}
		List upslist = new ArrayList();
		MgeUpsDao mgeUpsDao = new MgeUpsDao();
		if (vo.getRole() == 0 || vo.getRole() == 1) {
			upslist = mgeUpsDao.loadAll();
		} else {
			upslist = mgeUpsDao.getUpsByBID(rbids);
		}
		if (session != null) {
			session.setAttribute("upslist", upslist);
		}
		upssize = upslist.size();
		return upssize;
	}

	private String getUsers() {
		DaoInterface dao = new UserDao();
		List allUser = dao.loadAll();
		request.setAttribute("allUser", allUser);
		String event = request.getParameter("event");
		request.setAttribute("event", event);
		return "/schedule/scheduling/users.jsp";
	}

	private String home() {
		this.getHome();
		this.homeModuleSet();
		return "/common/home.jsp";
	}

	/**
	 * 更具查询数据库判断是否需要首页元素集合
	 */
	private void homeModuleSet() {
		// 查询用户首页模块设置 如果没有那么直接跳转到修改页面
		User uservo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		HomeRoleDao homeRoleDao = new HomeRoleDao();
		HomeUserDao homeUserDao = new HomeUserDao();
		String sqlCondition = " where user_id='" + uservo.getUserid() + "'";
		List homeUserList = homeUserDao.findByCondition(sqlCondition);

		Hashtable smallHashtable = new Hashtable();
		Hashtable bigHashtable = new Hashtable();
		if (homeUserList.size() == 0 || homeUserList == null) {
			List homeRoleList = homeRoleDao.findByCondition(" where role_id='" + uservo.getRole() + "'");
			for (int i = 0; i < homeRoleList.size(); i++) {
				HomeRoleModel homeRoleModel = (HomeRoleModel) homeRoleList.get(i);
				if (homeRoleModel.getType() == 0) {
					smallHashtable.put(homeRoleModel.getChName(), homeRoleModel.getVisible());
				}
				if (homeRoleModel.getType() == 1) {
					bigHashtable.put(homeRoleModel.getChName(), homeRoleModel.getVisible());
				}
			}
		} else {
			for (int i = 0; i < homeUserList.size(); i++) {
				HomeUserModel homeUserModel = (HomeUserModel) homeUserList.get(i);
				if (homeUserModel.getType() == 0) {
					smallHashtable.put(homeUserModel.getChName(), homeUserModel.getVisible());
				}
				if (homeUserModel.getType() == 1) {
					bigHashtable.put(homeUserModel.getChName(), homeUserModel.getVisible());
				}
			}
		}
		request.setAttribute("smallHashtable", smallHashtable);
		request.setAttribute("bigHashtable", bigHashtable);
	}

	private String kuaizhao() {
		UserDao dao = new UserDao();
		User vo = null;
		try {
			vo = dao.loadAllByUser("portal");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo == null) // 用户名或密码不正确
		{
			setErrorCode(ErrorMessage.INCORRECT_PASSWORD);
			return null;
		}
		session.setAttribute(SessionConstant.CURRENT_USER, vo); // 用户姓名
		session.setMaxInactiveInterval(1900000000);
		CommonAppUtil.setSkin(vo.getSkins());

		this.getHome();
		this.homeModuleSet();
		return "/jsp/portalOutLink/snapshot.jsp";
	}

	/**
	 * 用户登录
	 */
	private String login() {
		if (getParaValue("password") == null) {
			setErrorCode(ErrorMessage.INCORRECT_PASSWORD);
			return null;
		}
		int flag = 0;
		try {
			// 判断验证码
			// / LicenseUtil.checkLicense();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("LICENSE验证失败,请与厂商联系");
			flag = 1;
		}
		// 若验证码失败,则显示受限页面
		if (flag == 1) {
			return "/limited.jsp";
		}

		MD5 md = new MD5();
		String pwd = md.getMD5ofStr(getParaValue("password"));
		UserDao dao = new UserDao();
		User vo = null;
		try {
			vo = dao.findByLogin(getParaValue("userid"), pwd);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo == null) // 用户名或密码不正确
		{
			setErrorCode(ErrorMessage.INCORRECT_PASSWORD);
			return null;
		}

		session.setAttribute(SessionConstant.CURRENT_USER, vo); // 用户姓名
		session.setMaxInactiveInterval(1800);
		CommonAppUtil.setSkin(vo.getSkins());
		if (!"127.0.0.1".equals(request.getRemoteAddr())) {
			SysLog slvo = new SysLog();
			slvo.setUser(vo.getName());
			slvo.setEvent("登录系统");
			slvo.setLogTime(SysUtil.getCurrentTime());
			slvo.setIp(request.getRemoteAddr());
			SysLogDao sldao = new SysLogDao();
			try {
				sldao.save(slvo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sldao.close();
			}
		}
		this.getHome();
		return "/index.jsp";
	}

	/**
	 * 退出系统
	 */
	private String logout() {
		session.removeAttribute(SessionConstant.CURRENT_MENU);
		session.removeAttribute(SessionConstant.CURRENT_USER);
		return "/login.jsp";
	}

	/**
	 * 对监控列表进行排序
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 * @param montinorList
	 *            <code>监控列表</code>
	 * @param category
	 *            <code>设备类型</code>
	 * @param field
	 *            <code>排序字段</code>
	 * @param type
	 *            <code>排序类型</code>
	 * @return
	 */
	public List monitorListSort(List montinorList, String category, String field, String type) {
		for (int i = 0; i < montinorList.size() - 1; i++) {
			for (int j = i + 1; j < montinorList.size(); j++) {
				MonitorNodeDTO monitorNodeDTO = (MonitorNodeDTO) montinorList.get(i);
				MonitorNodeDTO monitorNodeDTO2 = (MonitorNodeDTO) montinorList.get(j);

				String fieldValue = "";

				String fieldValue2 = "";
				if ("name".equals(field)) {
					fieldValue = monitorNodeDTO.getAlias();

					fieldValue2 = monitorNodeDTO2.getAlias();
					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) < 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) > 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}

				} else if ("ipaddress".equals(field)) {
					fieldValue = monitorNodeDTO.getIpAddress();

					fieldValue2 = monitorNodeDTO2.getIpAddress();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) < 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) > 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("cpu".equals(field)) {
					fieldValue = monitorNodeDTO.getCpuValue();

					fieldValue2 = monitorNodeDTO2.getCpuValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("ping".equals(field)) {
					fieldValue = monitorNodeDTO.getPingValue();

					fieldValue2 = monitorNodeDTO2.getPingValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("memory".equals(field)) {
					fieldValue = monitorNodeDTO.getMemoryValue();

					fieldValue2 = monitorNodeDTO2.getMemoryValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("inutilhdx".equals(field)) {
					fieldValue = monitorNodeDTO.getInutilhdxValue();

					fieldValue2 = monitorNodeDTO2.getInutilhdxValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("oututilhdx".equals(field)) {
					fieldValue = monitorNodeDTO.getOututilhdxValue();

					fieldValue2 = monitorNodeDTO2.getOututilhdxValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("category".equals(field)) {
					fieldValue = monitorNodeDTO.getCategory();

					fieldValue2 = monitorNodeDTO2.getCategory();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) < 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) > 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				}

			}
		}
		return montinorList;
	}

	private String personhome() {
		this.getPersonHome();
		return "/common/personhome.jsp";
	}

	private String read() {
		String targetJsp = "/system/manage/read.jsp";
		BaseVo vo = null;
		UserDao dao = new UserDao();
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return targetJsp;
	}

	private String ready_edit() {
		String targetJsp = "/system/user/edit.jsp";
		BaseVo vo = null;
		UserDao dao = new UserDao();
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return targetJsp;
	}

	private String readyAdd() {
		if ((new RoleDao()).loadAll().size() == 0) {
			setErrorCode(ErrorMessage.NO_ROLE);
			return null;
		}
		if ((new PositionDao()).loadAll().size() == 0) {
			setErrorCode(ErrorMessage.NO_POSITION);
			return null;
		}
		if ((new DepartmentDao()).loadAll().size() == 0) {
			setErrorCode(ErrorMessage.NO_DEPARTMENT);
			return null;
		}
		return "/system/user/add.jsp";
	}

	/**
	 * 移除session
	 */
	private void removeFlexSession() {
		// 监视设备
		if (session.getAttribute("deviceList") != null) {
			session.removeAttribute("deviceList");
		}
		// 网络设备CPU
		if (session.getAttribute("networkCPUList") != null) {
			session.removeAttribute("networkCPUList");
		}
		// 网络设备入口流速
		if (session.getAttribute("networkInList") != null) {
			session.removeAttribute("networkInList");
		}
		// 网络设备出口流速
		if (session.getAttribute("networkOutList") != null) {
			session.removeAttribute("networkOutList");
		}
		// 服务器CPU
		if (session.getAttribute("hostCPUList") != null) {
			session.removeAttribute("hostCPUList");
		}
		// 服务器物理内存
		if (session.getAttribute("hostMemoryList") != null) {
			session.removeAttribute("hostMemoryList");
		}
		// 服务器磁盘利用率
		if (session.getAttribute("hostDiskList") != null) {
			session.removeAttribute("hostDiskList");
		}
	}

	private String save() {
		User vo = new User();
		vo.setName(getParaValue("name"));
		vo.setUserid(getParaValue("userid"));
		vo.setSex(getParaIntValue("sex"));
		vo.setDept(getParaIntValue("dept"));
		vo.setPosition(getParaIntValue("position"));
		vo.setRole(getParaIntValue("role"));
		vo.setPhone(getParaValue("phone"));
		vo.setMobile(getParaValue("mobile"));
		vo.setEmail(getParaValue("email"));
		vo.setBusinessids(getParaValue("bid"));
		vo.setGroup(getParaValue("group"));

		MD5 md = new MD5();

		vo.setPassword(md.getMD5ofStr(getParaValue("password")));
		UserDao dao = new UserDao();
		int result = dao.save(vo);

		String target = null;
		if (result == 0) {
			target = null;
			setErrorCode(ErrorMessage.USER_EXIST);
		} else if (result == 1) {
			target = "/user.do?action=list";
		} else {
			target = null;
		}
		return target;
	}

	private String setReceiver() {
		DaoInterface dao = new UserDao();
		List allUser = dao.loadAll();
		request.setAttribute("allUser", allUser);
		String event = request.getParameter("event");
		request.setAttribute("event", event);
		String value = request.getParameter("value");
		List idList = new ArrayList<Integer>();
		if (value != null && value.trim().length() > 0 && !"null".equalsIgnoreCase(value)) {
			String email[] = value.split(",");
			if (email != null && email.length > 0) {
				for (int i = 0; i < email.length; i++) {
					idList.add(Integer.parseInt(email[i]));
				}
			}
		}

		request.setAttribute("idList", idList);
		return "/system/user/setreceiver.jsp";

	}

	private String ssologin() {
		try {
			String cid = String.valueOf(request.getSession().getAttribute("casUserID"));
			if ("".equals(cid) || cid == null || "null".equals(cid)) {
				Object object = request.getSession().getAttribute("_const_cas_assertion_");
				if (object != null) {
					Assertion assertion = (Assertion) object;
					String loginName = assertion.getPrincipal().getName();
					if (!loginName.equals("") && loginName != null) {
						UserDao dao = new UserDao();
						User vo = null;
						try {
							vo = dao.loadAllByUser("admin");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dao.close();
						}
						if (vo == null) // 用户名或密码不正确
						{
							setErrorCode(ErrorMessage.INCORRECT_PASSWORD);
							return null;
						}
						session.setAttribute(SessionConstant.CURRENT_USER, vo); // 用户姓名
						session.setMaxInactiveInterval(1900000000);
						CommonAppUtil.setSkin(vo.getSkins());
						this.getHome();
						this.homeModuleSet();
						return "/common/index.jsp";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String testJQury() {
		String param1 = new String(request.getParameter("param"));
		request.setAttribute("param1", param1);
		return "/common/device_list.jsp";
	}

	private String tuopu() {
		UserDao dao = new UserDao();
		User vo = null;
		try {
			vo = dao.loadAllByUser("portal");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo == null) // 用户名或密码不正确
		{
			setErrorCode(ErrorMessage.INCORRECT_PASSWORD);
			return null;
		}
		session.setAttribute(SessionConstant.CURRENT_USER, vo); // 用户姓名
		session.setMaxInactiveInterval(1900000000);
		CommonAppUtil.setSkin(vo.getSkins());

		this.getHome();
		this.homeModuleSet();
		return "/topology/network/h_showMap.jsp";
	}

	private String update() {
		User vo = new User();
		vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setId(getParaIntValue("id"));
		vo.setUserid(getParaValue("userid"));
		vo.setSex(getParaIntValue("sex"));
		vo.setDept(getParaIntValue("dept"));
		vo.setPosition(getParaIntValue("position"));
		vo.setRole(getParaIntValue("role"));
		vo.setPhone(getParaValue("phone"));
		vo.setMobile(getParaValue("mobile"));
		vo.setEmail(getParaValue("email"));
		vo.setBusinessids(getParaValue("bid"));
		vo.setGroup(getParaValue("group"));

		String pwd = getParaValue("password");
		if (!pwd.equals("")) {
			MD5 md = new MD5();
			vo.setPassword(md.getMD5ofStr(pwd));
		} else {
			vo.setPassword(null);
		}

		UserDao dao = new UserDao();
		String target = null;
		if (dao.update(vo)) {
			target = "/user.do?action=list";
		}
		return target;
	}

	private String xingneng() {
		UserDao dao = new UserDao();
		User vo = null;
		try {
			vo = dao.loadAllByUser("portal");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo == null) // 用户名或密码不正确
		{
			setErrorCode(ErrorMessage.INCORRECT_PASSWORD);
			return null;
		}
		session.setAttribute(SessionConstant.CURRENT_USER, vo); // 用户姓名
		session.setMaxInactiveInterval(1900000000);
		CommonAppUtil.setSkin(vo.getSkins());
		this.getHome();
		this.homeModuleSet();
		return "/jsp/portalOutLink/resourceGrid.jsp";
	}
}