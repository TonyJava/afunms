/**
 * <p>Description:dao interface,define common methods of all dao classes</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.base;

import java.util.List;

@SuppressWarnings("unchecked")
public interface DaoInterface {
	/**
	 * ɾ��һ����¼
	 */
	public boolean delete(String[] id);

	/**
	 * ��ID��һ����¼
	 */
	public BaseVo findByID(String id);

	/**
	 * �õ���ҳ����
	 */
	public JspPage getPage();

	/**
	 * ��ҳ�б�
	 */
	public List listByPage(int curpage, int perpage);

	/**
	 * �������ķ�ҳ�б�
	 */
	public List listByPage(int curpage, String where, int perpage);

	/**
	 * �������м�¼
	 */

	public List loadAll();

	public List loadAll(String where);

	/**
	 * ����һ����¼
	 */
	public boolean save(BaseVo vo);

	/**
	 * ����һ����¼
	 */
	public boolean update(BaseVo vo);
}
