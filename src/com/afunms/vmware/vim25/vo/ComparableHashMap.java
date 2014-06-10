package com.afunms.vmware.vim25.vo;

import com.afunms.vmware.vim25.util.Util;

import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * 可以按照HashMap中的指定键值排序
 * 
 * @author LXL
 * 
 */
public class ComparableHashMap<K, V> extends HashMap<K, V> implements
		java.lang.Comparable<ComparableHashMap<K, V>> {

	// serialVersionUID
	private static final long serialVersionUID = 6911806133650144340L;

	private final static Logger LOGGER = Logger
			.getLogger(ComparableHashMap.class);

	// 是否顺序
	private boolean isAsc;

	// 比较的键值
	private String compareKey;

	/**
	 * 构建器
	 * 
	 * @param isAsc
	 * @param compareKey
	 */
	public ComparableHashMap(boolean isAsc, String compareKey) {
		this.isAsc = isAsc;
		this.compareKey = compareKey;
	}

	public int compareTo(ComparableHashMap<K, V> o) {
		int result = 0;
		try {
			String thisObj = Util.normalizeObject(this.get(compareKey))
					.toString();
			String otherObj = Util.normalizeObject(o.get(compareKey))
					.toString();
			if ((thisObj != null) && (otherObj != null)) {
				if (isAsc) {
					result = thisObj.compareTo(otherObj);
				} else {
					result = otherObj.compareTo(thisObj);
				}
			}
			return result;
		} catch (Exception e) {
			LOGGER.error("compareTo error, ", e);
			return 0;
		}
	}
}
