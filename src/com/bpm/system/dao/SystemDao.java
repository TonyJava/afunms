package com.bpm.system.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.afunms.common.base.JspPage;
import com.afunms.common.util.DBManager;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.model.Codedetail;
import com.bpm.system.model.FormModel;
import com.bpm.system.model.Group_Menu;
import com.bpm.system.model.Menu;
import com.bpm.system.model.UserModel;
import com.bpm.system.utils.UUIDKey;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class SystemDao {
	@Resource
	private SessionFactory sessionFactory;
	@Resource
	private RepositoryService repositoryService;

	/**
	 * 
	 * Description: 获取菜单信息 Date:2012-10-18
	 * 
	 * @author hexinlin
	 * @return List<Menu>
	 */

	public List<Menu> queryAllMenu() {
		List<Menu> list = new ArrayList<Menu>();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from bpm_menu order by sort,parent_id,menu_seq asc");
		list = query.list();
		return list;
	}

	/**
	 * 
	 * Description:获取一级菜单 Date:2012-10-21
	 * 
	 * @author hexinlin
	 * @return List<Menu>
	 */
	public List<Menu> queryOneLevelMenu() {
		List<Menu> list = new ArrayList<Menu>();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from bpm_menu where menu_id<>'-1' and parent_id='-1' order by menu_seq asc");
		list = query.list();
		return list;
	}

	/**
	 * 
	 * Description:删除一级菜单，含有二级菜单的不能删除。 Date:2012-10-21
	 * 
	 * @author hexinlin
	 * @return String
	 */
	public void deleteOneLevelMenuByIds(String checkbox[]) {
		Session session = sessionFactory.getCurrentSession();

		String temp = "";
		for (String str : checkbox) {
			temp = temp + str + ",";
		}
		temp = temp.length() > 0 ? temp.substring(0, temp.length() - 1) : "''";
		Query query = session.createQuery("delete from  bpm_menu where menu_id in (" + temp + ") and childrennum = ? ");
		query.setParameter(0, 0);
		query.executeUpdate();
	}

	/**
	 * 
	 * Description:修改一级菜单信息。 Date:2012-10-21
	 * 
	 * @author hexinlin
	 * @return String
	 */
	public void modifyMenu(String menu_name, String menu_url, int menu_id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("update bpm_menu set menu_name=?,menu_url=? where menu_id=?");
		query.setParameter(0, menu_name);
		query.setParameter(1, menu_url);
		query.setParameter(2, menu_id);
		query.executeUpdate();
	}

	/**
	 * 
	 * Description:增加一级菜单 Date:2012-10-21
	 * 
	 * @author hexinlin
	 * @return void
	 */
	public void addOneLevelMenu(Menu menu) {
		sessionFactory.getCurrentSession().save(menu);
	}

	/**
	 * 
	 * Description:存储用户组于菜单的对应关系 Date:2012-10-21
	 * 
	 * @author hexinlin
	 * @return void
	 */
	public void saveGroupMenu(String groupId, int checkbox[]) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("delete from group_menu where groupId=?");
		query.setParameter(0, groupId);
		query.executeUpdate();
		Group_Menu gm = new Group_Menu();
		for (int menu_id : checkbox) {
			gm.setPriKey(UUIDKey.getKey());
			gm.setGroupId(groupId);
			gm.setMenu_id(menu_id);
			session.save(gm);
		}
	}

	/**
	 * 
	 * Description:获取一级菜单的最大组别 Date:2012-10-22
	 * 
	 * @author hexinlin
	 * @return int
	 */
	public int getMaxSort() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select max(sort) from bpm_menu where parent_id=-1");
		int sort = (Integer) query.list().get(0);
		return sort;
	}

	/**
	 * 
	 * Description:获取二级菜单 Date:2012-10-23
	 * 
	 * @author hexinlin
	 * @return List<Menu>
	 */
	public List<Menu> querysecmenu(int parent_id) {
		List<Menu> list = new ArrayList<Menu>();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from bpm_menu where parent_id=?");
		query.setParameter(0, parent_id);
		list = query.list();
		return list;
	}

	/**
	 * 
	 * Description:根据父ID获取当前菜单下的最大ID号 Date:2012-10-23
	 * 
	 * @author hexinlin
	 * @return int
	 */
	public int maxMenu_id(int parent_id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("select max(menu_id) from bpm_menu where sort=?");
		query.setParameter(0, parent_id);
		int id = (Integer) query.list().get(0);
		return id;
	}

	/**
	 * 
	 * Description:添加新的二级菜单 Date:2012-10-23
	 * 
	 * @author hexinlin
	 * @return void
	 */
	public void addNewSecMenu(Menu menu) {
		Session session = sessionFactory.getCurrentSession();
		session.save(menu);
		Query query = session.createQuery("update bpm_menu set childrennum=childrennum+1 where menu_id=?");
		query.setParameter(0, menu.getParent().getMenu_id());
		query.executeUpdate();
	}

	/**
	 * 
	 * Description:更新实体 Date:2012-10-23
	 * 
	 * @author hexinlin
	 * @return void
	 */
	public void updateObj(Object o) {
		Session session = sessionFactory.getCurrentSession();
		session.update(o);
	}

	/**
	 * 
	 * Description:删除子菜单 Date:2012-10-23
	 * 
	 * @author hexinlin
	 * @return void
	 */
	public void delSecMenu(int menu_id, int parent_id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("delete from bpm_menu where menu_id=?");
		query.setParameter(0, menu_id);
		query.executeUpdate();
		query = session.createQuery("update bpm_menu set childrennum=childrennum-1 where menu_id=?");
		query.setParameter(0, parent_id);
		query.executeUpdate();
	}

	/**
	 * 
	 * Description:删除用户组，与用户相关联的组将不能被删除 Date:2012-10-23
	 * 
	 * @author hexinlin
	 * @return void
	 */
	public void delGroup(String checkbox[]) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("delete from act_id_group  where ID_ in(:name) and ID_ not in (select GROUP_ID_ from act_id_membership)");
		query.setParameterList("name", checkbox);
		query.executeUpdate();
	}

	/**
	 * 部署自定义流程
	 * 
	 * @param checkbox
	 */
	public void designProcessDeploy(String[] checkbox, String url, String url2) {
		DBManager db = new DBManager();
		String sql = getCheckBoxSql(checkbox);
		ResultSet rSet = db.executeQuery(sql);
		delpoyProcess(rSet);
		try {
			if (rSet != null)
				rSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * 组织chebox的sql语句
	 * 
	 * @param checkbox
	 * @return
	 */
	public String getCheckBoxSql(String[] checkbox) {
		String sql = "select name,bpmxml from act_ge_bytearray_temp where modelid in(";
		for (String value : checkbox) {
			sql += "'" + value + "',";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ")";
		return sql;
	}

	/**
	 * 匹配formkey，部署流程
	 * 
	 * @param rSet
	 */
	public void delpoyProcess(ResultSet rSet) {
		ArrayList<String> formList = new ArrayList<String>();// 记录所有formkey
		DBManager db = new DBManager();
		InputStream is = null;
		ResultSet rSet2 = null;
		InputStream is2 = null;
		try {
			while (rSet.next()) {
				String fileName = rSet.getString(1);
				is = rSet.getBinaryStream(2);
				formList = getFormList(is);
				is.reset();

				// 添加bpmxml文件
				DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
				deploymentBuilder.addInputStream(fileName + ".bpmn20.xml", is);

				// 添加form文件
				String formSql = getFormSql(formList);
				if (null == formSql) {
					deploymentBuilder.deploy();
				} else {
					rSet2 = db.executeQuery(formSql);
					while (rSet2.next()) {
						String name = rSet2.getString(1);
						is2 = rSet2.getBinaryStream(2);
						deploymentBuilder.addInputStream(name, is2);
					}
					deploymentBuilder.deploy();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null)
					rSet.close();
				if (rSet2 != null)
					rSet2.close();
				if (is != null)
					is.close();
				if (is2 != null)
					is2.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db.close();
		}
	}

	/**
	 * 查找bpmxml文件的所有formKey
	 * 
	 * @param is
	 * @return
	 */
	public ArrayList<String> getFormList(InputStream is) {
		ArrayList<String> formList = new ArrayList<String>();

		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(is, "UTF-8");
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.contains("activiti:formKey")) {
					String[] data = line.split("\"");
					String str = null;
					if (data.length == 0)
						continue;
					for (int i = 0; i < data.length; i++) {
						str = data[i];
						if (str.contains("activiti:formKey") && i < data.length - 1) {
							formList.add(data[i + 1]);
						}
					}
				}
				if (line.contains("bpmndi:BPMNDiagram"))
					break;
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return formList;
	}

	/**
	 * 用jdom解析xml文件，部署流程
	 * 
	 * @param rSet
	 * @return
	 */
	public void delpoyProcess(ResultSet rSet, String url, String url2) {
		DBManager db = new DBManager();
		ResultSet rSet2 = null;
		try {
			while (rSet.next()) {
				ArrayList<String> formList = new ArrayList<String>();
				String fileName = rSet.getString(1);
				InputStream is = rSet.getBinaryStream(2);
				// jdom解析xml文件，获得xml所有的formkey
				SAXBuilder sbld = new SAXBuilder();
				Document cfgxml;
				try {
					cfgxml = sbld.build(is);
					Element rootElement = cfgxml.getRootElement();
					List<Element> list1 = rootElement.getChildren();
					for (Element element : list1) {
						if (element.getName().equals("process")) {
							List<Element> list2 = element.getChildren();
							{
								for (Element element2 : list2) {
									if (element2.getName().equals("userTask")) {
										List<Attribute> list3 = element2.getAttributes();
										for (Attribute attribute : list3) {
											if (attribute.getName().equals("formKey")) {
												formList.add(attribute.getValue());
											}
										}
									}
								}
							}
						}
					}

					// 直接上传
					is.reset();
					DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
					deploymentBuilder.addInputStream(fileName + ".bpmn20.xml", is);
					is.close();

					// 添加form
					String formSql = getFormSql(formList);
					if (formSql == null)
						continue;
					rSet2 = db.executeQuery(formSql);
					while (rSet2.next()) {
						String name = rSet2.getString(1);
						InputStream is2 = rSet2.getBinaryStream(2);
						deploymentBuilder.addInputStream(name, is2);
						is2.close();
					}
					deploymentBuilder.deploy();

				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null)
					rSet.close();
				if (rSet2 != null)
					rSet2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			db.close();
		}

	}

	/**
	 * 查询xml中所有form文件
	 * 
	 * @param formList
	 * @return
	 */
	public String getFormSql(ArrayList<String> formList) {
		if (formList == null || formList.size() == 0)
			return null;
		String sql = "select name,content from bpm_form where name in(";
		for (String value : formList) {
			sql += "'" + value + "',";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ")";
		return sql;
	}

	/**
	 * 上传表单
	 * 
	 * @param file
	 * @param fileName
	 * @return
	 */
	public String deployForm(File file, String fileName) {
		String data[] = fileName.split("\\.");
		if (data.length < 1)
			return "error";
		if (!(data[data.length - 1].equals("form") || data[data.length - 1].equals("zip")))
			return "error";
		if (data[data.length - 1].equals("form")) {
			return handleForm(file, fileName, data);
		} else {
			return handleZip(file, fileName);
		}

	}

	/**
	 * 上传表单，单独的form
	 * 
	 * @param file
	 * @param fileName
	 * @param data
	 * @return
	 */
	public String handleForm(File file, String fileName, String data[]) {
		DBManager db = new DBManager();
		Connection conn = null;
		PreparedStatement pstmt = null;
		InputStream is = null;
		try {
			conn = db.getConn();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement("insert into bpm_form(id,name,content,createtime) values(?,?,?,?)");
			is = new FileInputStream(file);
			pstmt.setString(1, fileName);
			pstmt.setString(2, fileName);
			pstmt.setBinaryStream(3, is, is.available());
			pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
			pstmt.executeUpdate();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			try {
				if (is != null)
					is.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null && !conn.isClosed())
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db.close();
		}

		return "success";
	}

	/**
	 * 表单上传，zip压缩文件
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public String handleZip(File file, String fileName) {
		DBManager db = new DBManager();
		Connection conn = null;
		PreparedStatement pstmt = null;
		InputStream is = null;
		ZipFile zipFile = null;
		try {
			conn = db.getConn();
			pstmt = conn.prepareStatement("insert into bpm_form(id,name,content,createtime) values(?,?,?,?)");
			conn.setAutoCommit(false);
			pstmt.clearBatch();

			zipFile = new ZipFile(file.getPath());
			File file2 = null;
			for (Enumeration entries = zipFile.getEntries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				file2 = new File(entry.getName());
				if (entry.isDirectory()) {
					file2.mkdirs();
				} else {
					is = zipFile.getInputStream(entry);
					pstmt.setString(1, entry.getName());
					pstmt.setString(2, entry.getName());
					pstmt.setBinaryStream(3, is, is.available());
					pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
					pstmt.addBatch();
				}
			}

			pstmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			try {
				if (is != null)
					is.close();
				if (zipFile != null)
					zipFile.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null && !conn.isClosed())
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db.close();
		}

		return "success";
	}

	/**
	 * form列表
	 * 
	 * @return
	 */
	public JspPage findFormList(int curpage, int perpage) {
		List list = new ArrayList();
		JspPage jspPage = null;
		String sql = String.format("select id,name from bpm_form");
		String countSql = "select count(*) from bpm_form";
		DBManager db = new DBManager();
		ResultSet rs = null;
		try {
			int rowcount = db.executeQueryCount(countSql);
			jspPage = new JspPage(perpage, curpage, rowcount);
			rs = db.executeQueryFromAll(sql, jspPage.getMinNum(), perpage);
			ResultSetHandler<List<FormModel>> rsh = new BeanListHandler<FormModel>(FormModel.class);
			list = rsh.handle(rs);
			jspPage.setList(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			db.close();
		}
		return jspPage;
	}

	/**
	 * 删除表单
	 * 
	 * @param checkbox
	 */
	public void deleteForm(String checkbox[]) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("delete from bpm_form  " + "where id in(:tempid)");
		query.setParameterList("tempid", checkbox);
		query.executeUpdate();
	}

	/**
	 * 查询所有用户
	 * 
	 * @return
	 */
	public List<UserModel> queryAllUser() {
		DBManager dbm = new DBManager();
		List<UserModel> list = new ArrayList<UserModel>();
		ResultSet rs = null;
		String sql = "select id_ as id from act_id_user";
		try {
			rs = dbm.executeQuery(sql);
			ResultSetHandler<List<UserModel>> rsh = new BeanListHandler<UserModel>(UserModel.class);
			list = rsh.handle(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbm.close();
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return list;
	}

	public String isStart(String processDefinitionId, String userId) {
		String result = "success";
		DBManager dbm = new DBManager();
		ResultSet rs = null;
		List<String> instList = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		String sql = String.format("select count(*),PROC_INST_ID_ from " + "(select PROC_INST_ID_,ASSIGNEE_ from act_hi_actinst where PROC_DEF_ID_='%s') "
				+ "as t1 group by PROC_INST_ID_", processDefinitionId);
		rs = dbm.executeQuery(sql);
		int count = 0;
		String procId = null;
		try {
			while (rs.next()) {
				count = rs.getInt(1);
				procId = rs.getString(2);
				if (count == 2)
					instList.add(procId);
			}
			if (instList.size() == 0)
				return result;
			int size = instList.size();
			for (int i = 0; i < size; i++) {
				sb.append("'");
				sb.append(instList.get(i));
				sb.append("'");
				if (i != size - 1)
					sb.append(",");
			}
			sql = String.format("select START_USER_ID_ from act_hi_procinst where START_USER_ID_='%s' and  PROC_INST_ID_ in (%s) ", userId, sb.toString());
			rs = dbm.executeQuery(sql);
			if (rs.next()) {
				result = "error";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			dbm.close();
		}
		return result;
	}

	public String deployForm(File file, String fileName, File filepic, String filepicName) {
		DBManager db = new DBManager();
		Connection conn = null;
		PreparedStatement pstmt = null;
		InputStream is = null;
		try {
			conn = db.getConn();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement("insert into bpm_form(id,name,content,createtime) values(?,?,?,?)");
			is = new FileInputStream(file);

			pstmt.setString(1, fileName);
			pstmt.setString(2, fileName);
			pstmt.setBinaryStream(3, is, is.available());
			pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));

			pstmt.executeUpdate();
			conn.commit();
			conn.setAutoCommit(true);

			// 图片无法存入mysql中，暂时保存在本地
			String path = ResourceCenter.getInstance().getSysPath() + "images\\bpm\\" + filepicName;
			InputStream is2 = new BufferedInputStream(new FileInputStream(filepic));
			OutputStream os = new BufferedOutputStream(new FileOutputStream(path));
			byte b[] = new byte[1024];
			while (is2.read(b) > 0) {
				os.write(b);
			}
			if (is2 != null)
				is2.close();
			if (os != null)
				os.close();

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			try {
				if (is != null)
					is.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null && !conn.isClosed())
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db.close();
		}
		return "success";
	}

	/**
	 * 获取二级数据字典
	 * 
	 * @param typeId
	 * @return
	 */
	public List<Codedetail> loadCodedetail(String typeId) {
		StringBuilder sb = new StringBuilder();
		List<Codedetail> list = new ArrayList<Codedetail>();
		sb.append("select ID,NAME,CODE from nms_codedetail where 1=1").append(" and typeid='" + typeId + "' order by seq asc ");
		DBManager dbm = new DBManager();
		ResultSet rs = null;
		rs = dbm.executeQuery(sb.toString());
		Codedetail vo = null;
		try {
			while (rs.next()) {
				vo = new Codedetail();
				vo.setName(rs.getString("NAME"));
				vo.setCode(rs.getString("CODE") + "|" + rs.getString("ID"));
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbm.close();
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * 修改模型类型
	 * 
	 * @param keytext
	 * @param modelid
	 * @return
	 */
	public String modifyModelType(String keytext, String modelid) {
		DBManager dbm = new DBManager();
		String flag = "success";
		String sql = "select modelid from bpm_modeltype where modelid='" + modelid + "'";
		String sql1 = "";
		ResultSet rs = null;
		try {
			rs = dbm.executeQuery(sql);
			if (rs.next()) {
				sql1 = String.format("update bpm_modeltype set TYPEID='%s' where MODELID='%s'", keytext, modelid);
			} else {
				sql1 = String.format("insert into bpm_modeltype(ID,MODELID,TYPEID) values ('%s','%s','%s')", UUIDKey.getKey(), modelid, keytext);
			}
			dbm.executeUpdate(sql1);
		} catch (SQLException e) {
			e.printStackTrace();
			flag = "error";
		} finally {
			if (null != rs) {
				try {
					rs.close();
					dbm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
}
