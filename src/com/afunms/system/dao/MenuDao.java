package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Menu;

@SuppressWarnings("unchecked")
public class MenuDao extends BaseDao {
	public MenuDao() {
		super("system_menu");
	}

	public boolean changeMenuSort(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("update system_menu set sort=" + (i + 1) + " where SUBSTRING(id,1,2)=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			conn.rollback();
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteSub(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from system_menu where id='" + id + "'");
			conn.addBatch("delete from system_role_menu where menu_id='" + id + "'");
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			result = false;
			conn.rollback();
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	/**
	 * 删除一个一级菜单并把其子菜单删除,它将删除两个表中的记录 1.删除menu表中的一条记录; 2.删除role_menu表中与该角色有关的所有关系
	 */
	public boolean deleteTop(String top_id) {
		boolean result = false;
		try {
			conn.addBatch("delete from system_menu where SUBSTRING(id,1,2)='" + top_id + "'");
			conn.addBatch("delete from system_role_menu where SUBSTRING(menu_id,1,2)='" + top_id + "'");
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			result = false;
			conn.rollback();
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	@Override
	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from system_menu where id='" + id + "'");
			if (rs.next()) {
				vo = loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	private Menu getNextMenu(String top_id) {
		Menu menu = new Menu();
		int next_id = 0;
		try {
			if (top_id == null) {
				rs = conn.executeQuery("select max(SUBSTRING(id,1,2)) from system_menu where SUBSTRING(id,3,4)='00'");
				if (rs.next()) {
					next_id = rs.getInt(1) + 1;
				}
				if (next_id < 10) {
					menu.setId("0" + next_id + "00");
				} else {
					menu.setId(next_id + "00");
				}
				rs = conn.executeQuery("select max(sort) from system_menu");
				if (rs.next()) {
					menu.setSort(rs.getInt(1) + 1);
				}
			} else {
				rs = conn.executeQuery("select max(SUBSTRING(id,3,4)) from system_menu where SUBSTRING(id,1,2)='" + top_id + "'");
				if (rs.next()) {
					next_id = rs.getInt(1) + 1;
				}
				if (next_id < 10) {
					menu.setId(top_id + "0" + next_id);
				} else {
					menu.setId(top_id + next_id);
				}
				rs = conn.executeQuery("select sort from system_menu where SUBSTRING(id,1,2)='" + top_id + "'");
				if (rs.next()) {
					menu.setSort(rs.getInt(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menu;
	}

	public List loadByRole(int role_id) {
		List list = new ArrayList(10);
		try {
			String sql = null;
			if (role_id == 0) {
				sql = "select * from system_menu order by sort,id";
			} else {
				sql = "select t1.* from system_menu as t1,system_role_menu as t2 where t1.id=t2.menu_id and t2.role_id=" + role_id + " and t2.operate>1 order by t1.sort,t1.id";
			}
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Menu vo = new Menu();
		try {
			vo.setId(rs.getString("id"));
			vo.setTitle(rs.getString("title"));
			vo.setUrl(rs.getString("url"));
			vo.setSort(rs.getInt("sort"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public List loadSubMenu(String top_id) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from system_menu where SUBSTRING(id,1,2)='" + top_id + "' and SUBSTRING(id,3,4)<>'00' order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public List loadTopMenu() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from system_menu where SUBSTRING(id,3,4)='00' order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public boolean save(Menu menu, String top_id) {
		boolean result = false;
		String sql = null;
		try {
			Menu newMenu = getNextMenu(top_id);
			conn
					.addBatch("insert into system_menu(id,title,url,sort)values('" + newMenu.getId() + "','" + menu.getTitle() + "','" + menu.getUrl() + "'," + newMenu.getSort()
							+ ")");

			int id = 1;
			sql = "select max(id) from system_role_menu";
			rs = conn.executeQuery(sql);
			if (rs.next()) {
				id = rs.getInt(1) + 1;
			}

			rs = conn.executeQuery("select * from system_role order by id");
			while (rs.next()) {
				StringBuffer sb = new StringBuffer(100);
				sb.append("insert into system_role_menu(id,role_id,menu_id,operate)values(");
				sb.append(id);
				sb.append(",");
				sb.append(rs.getInt("id"));
				sb.append(",'");
				sb.append(newMenu.getId());
				sb.append("',");
				sb.append(3);
				sb.append(")");
				id++;
				conn.addBatch(sb.toString());
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			result = false;
			conn.rollback();
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean update(Menu menu) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_menu set title='");
		sql.append(menu.getTitle());
		sql.append("',url='");
		sql.append(menu.getUrl());
		sql.append("' where id='");
		sql.append(menu.getId());
		sql.append("'");

		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}
}
