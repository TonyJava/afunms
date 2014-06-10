package com.afunms.vmware.vim25.cache;

import com.afunms.vmware.vim25.common.VIMConnection;
import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.constants.VIMConstants;
import com.afunms.vmware.vim25.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.AlarmDescription;
import com.vmware.vim25.AlarmInfo;
import com.vmware.vim25.ElementDescription;
import com.vmware.vim25.Event;
import com.vmware.vim25.EventDescription;
import com.vmware.vim25.EventDescriptionEventDetail;
import com.vmware.vim25.EventEx;
import com.vmware.vim25.ExtendedEvent;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfInterval;
import com.vmware.vim25.TaskDescription;
import com.vmware.vim25.TypeDescription;

/**
 * VMWare对象缓冲,分门别类保存不同资源的ManagedObjectReference对象,减少频繁进行查询
 * 
 * @author LXL
 * 
 */
public class VIMCache extends VIMConnection implements VIMConstants {

	// LOGGER
	private final static Logger LOGGER = Logger.getLogger(VIMCache.class);

	// 保存Cache对象
	private static HashMap<String, VIMCache> cacheMap = new HashMap<String, VIMCache>();

	// VIM连接信息
	private String url = "";
	private String username = "";;
	private String password = "";

	// 数据中心dc对象的更新时间
	private long dcUpdatetime = 0;

	// VIM连接
	private ExtendedAppUtil localECB = null;

	// 保存所有的对象
	private HashMap<String, HashMap<String, ManagedObjectReference>> vimMOMap = new HashMap<String, HashMap<String, ManagedObjectReference>>();

	// 事件的类型,可以是静态的,版本一致的vCenter的数据应该完全一致
	private HashMap<String, String> eventCategoryMap = new HashMap<String, String>();

	// 任务的名称,可以是静态的,版本一致的vCenter的数据应该完全一致
	private HashMap<String, String> taskDescMap = new HashMap<String, String>();

	// 告警的描述,可以是静态的,版本一致的vCenter的数据应该完全一致,其中有一些值是重复的
	private HashMap<String, String> almDescMap = new HashMap<String, String>();

	// 告警
	private HashMap<String, AlarmInfo> almMap = new HashMap<String, AlarmInfo>();

	// 枚举值翻译
	private HashMap<String, String> enumMap = new HashMap<String, String>();

	// 性能间隔
	private HashMap<String, PerfInterval> perfIntervalMap = new HashMap<String, PerfInterval>();

	// 性能计数器
	private HashMap<String, PerfCounterInfo> perfCounterInfoMap = new HashMap<String, PerfCounterInfo>();

	/**
	 * 获取实例
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static VIMCache getInstance(String url, String username,
			String password) {
		VIMCache cache = null;
		try {
			if (cacheMap.containsKey(getVIMKey(url, username))) {
				cache = cacheMap.get(getVIMKey(url, username));
			} else {
				LOGGER.info("VIMCache start initialization");

				long curr = System.currentTimeMillis();

				cache = new VIMCache();
				cacheMap.put(getVIMKey(url, username), cache);

				// 设置连接信息并初始化
				cache.setConnectionInfo(url, username, password);
				cache.initCache();

				LOGGER.info("VIMCache initialization is complete, time='"
						+ (System.currentTimeMillis() - curr) / 1000 + "' s");
			}
		} catch (Exception e) {
			LOGGER.error("getInstance error, ", e);
		}
		return cache;
	}

	/**
	 * 获取VIM链接
	 * 
	 * @return
	 */
	private ExtendedAppUtil getLocalECB() {
		try {
			if (localECB == null) {
				localECB = getECB(url, username, password);
			}
		} catch (Exception e) {
			LOGGER.error("getLocalECB error, ", e);
		}
		return localECB;
	}

	/**
	 * 获取实例,测试时使用
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static VIMCache getInstanceForTest(String url, String username,
			String password) {
		VIMCache cache = null;
		try {
			if (cacheMap.containsKey(getVIMKey(url, username))) {
				cache = cacheMap.get(getVIMKey(url, username));
			} else {
				cache = new VIMCache();
				cacheMap.put(getVIMKey(url, username), cache);

				// 设置连接信息并初始化
				cache.setConnectionInfo(url, username, password);
				// 测试时为了加快速度不初始化数据
				// cache.initCache();
			}
		} catch (Exception e) {
			LOGGER.error("getInstanceForTest error, ", e);
		}
		return cache;
	}

	/**
	 * 设置连接信息
	 * 
	 * @param url
	 * @param username
	 * @param password
	 */
	private void setConnectionInfo(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	/**
	 * 初始化所有的对象
	 */
	protected void initCache() {
		try {
			// 当前时间
			long curr0 = System.currentTimeMillis();
			long curr1 = 0;

			// 初始化连接
			getLocalECB();

			curr1 = System.currentTimeMillis();
			LOGGER.info("VIMCache connect time='" + (curr1 - curr0) + "' ms");
			curr0 = curr1;

			// 初始化VIM对象

			// // 这样可能有问题,获取的对象过多,处理速度过慢
			// ObjectContent[] ocList = ecb.getServiceUtil3()
			// .getAllContainerContents();
			//
			// if (ocList != null) {
			// LOGGER.info("VIMCache initialization objsize='"
			// + ocList.length + "'");
			//
			// for (ObjectContent oc : ocList) {
			// ManagedObjectReference mor = oc.getObj();
			// if (mor.getType().equals(RESOURCE_DC)) {
			// dcUpdatetime = System.currentTimeMillis();
			// }
			// getMorMap(mor.getType()).put(mor.get_value(), mor);
			// }
			// }

			// 数据中心
			initTypeCache(RESOURCE_DC);

			// 群集,ComputeResource
			initTypeCache(RESOURCE_CR);

			// 群集,ClusterComputeResource
			initTypeCache(RESOURCE_CCR);

			// 主机
			initTypeCache(RESOURCE_HO);

			// 资源池
			initTypeCache(RESOURCE_RP);

			// 资源池
			initTypeCache(RESOURCE_VM);

			curr1 = System.currentTimeMillis();
			LOGGER.info("VIMCache initialization obj time='" + (curr1 - curr0)
					+ "' ms");
			curr0 = curr1;

			// 数据中心的存储和网络和数据中心下的文件夹
			String[] types = { RESOURCE_DC_DATASTORE, RESOURCE_DC_NETWORK,
					RESOURCE_DC_DSFOLDER, RESOURCE_DC_HOFOLDER,
					RESOURCE_DC_NWFOLDER, RESOURCE_DC_VMFOLDER };
			for (String type : types) {
				initDCPropertiesTypeCache(type);
				curr1 = System.currentTimeMillis();
				LOGGER.info("VIMCache initialization type='" + type
						+ "', time='" + (curr1 - curr0) + "' ms");
				curr0 = curr1;
			}

			// 事件的类型
			initEventCategory();
			curr1 = System.currentTimeMillis();
			LOGGER.info("VIMCache initialization type='EventCategory', time='"
					+ (curr1 - curr0) + "' ms");
			curr0 = curr1;

			// 任务的名称
			initTaskDesc();
			curr1 = System.currentTimeMillis();
			LOGGER.info("VIMCache initialization type='TaskDesc', time='"
					+ (curr1 - curr0) + "' ms");
			curr0 = curr1;

			// 告警的描述
			initAlarmDesc();
			curr1 = System.currentTimeMillis();
			LOGGER.info("VIMCache initialization type='AlarmDesc', time='"
					+ (curr1 - curr0) + "' ms");
			curr0 = curr1;

			// 告警
			initAlarm();
			curr1 = System.currentTimeMillis();
			LOGGER.info("VIMCache initialization type='Alarm', time='"
					+ (curr1 - curr0) + "' ms");
			curr0 = curr1;

			// 枚举值
			initEnum();
			curr1 = System.currentTimeMillis();
			LOGGER.info("VIMCache initialization type='Enum', time='"
					+ (curr1 - curr0) + "' ms");
			curr0 = curr1;

			// 性能间隔
			initPerfInterval();
			curr1 = System.currentTimeMillis();
			LOGGER.info("VIMCache initialization type='PerfInterval', time='"
					+ (curr1 - curr0) + "' ms");
			curr0 = curr1;

			// 性能计数器
			initPerfCounterInfo();
			curr1 = System.currentTimeMillis();
			LOGGER
					.info("VIMCache initialization type='PerfCounterInfo', time='"
							+ (curr1 - curr0) + "' ms");
			curr0 = curr1;

		} catch (Exception e) {
			LOGGER.error("initCache error, ", e);
		}
	}

	/**
	 * 初始化类别的对象
	 * 
	 * @param type
	 */
	protected void initTypeCache(String type) {
		if ((type.equals(RESOURCE_DC_DATASTORE))
				|| (type.equals(RESOURCE_DC_NETWORK))
				|| (type.equals(RESOURCE_DC_DSFOLDER))
				|| (type.equals(RESOURCE_DC_HOFOLDER))
				|| (type.equals(RESOURCE_DC_NWFOLDER))
				|| (type.equals(RESOURCE_DC_VMFOLDER))) {
			initDCPropertiesTypeCache(type);
		} else {
			initCommonTypeCache(type);
		}
	}

	/**
	 * 初始化存储和网络
	 * 
	 * @param type
	 */
	protected void initDCPropertiesTypeCache(String type) {
		try {
			ExtendedAppUtil ecb = getLocalECB();

			// 存储和网络属于数据中心
			// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.Datacenter.html

			// 重新获取数据中心,会导致数据中心的数据重复获取,但是可以保证数据完整
			initCommonTypeCache(RESOURCE_DC);
			HashMap<String, ManagedObjectReference> dcVIMMOMap = vimMOMap
					.get(RESOURCE_DC);
			// LOGGER.info("initDsAndNwTypeCache dcVIMMOMap='" + dcVIMMOMap +
			// "'");
			for (Iterator<ManagedObjectReference> dcMorIt = dcVIMMOMap.values()
					.iterator(); dcMorIt.hasNext();) {
				ManagedObjectReference dcMor = dcMorIt.next();
				// LOGGER.info("initDsAndNwTypeCache dcMor='" + dcMor + "'");
				if ((type.equals(RESOURCE_DC_DATASTORE))
						|| (type.equals(RESOURCE_DC_NETWORK))) {
					// 数据中心的多值属性,数据存储和网络,使用自身的id做key
					ManagedObjectReference[] morList = (ManagedObjectReference[]) ecb
							.getServiceUtil3().getDynamicProperty(dcMor, type);
					if (morList != null) {
						// LOGGER.info("initDsAndNwTypeCache type='" + type
						// + "', dcMor='" + dcMor.get_value()
						// + "', size='" + morList.length + "'");
						for (ManagedObjectReference mor : morList) {
							getMorMap(type).put(mor.get_value(), mor);
						}
					}
				} else {
					// 数据中心的单值属性,4个文件夹,使用数据中心的dcId做key
					ManagedObjectReference mor = (ManagedObjectReference) ecb
							.getServiceUtil3().getDynamicProperty(dcMor, type);
					if (mor != null) {
						getMorMap(type).put(dcMor.get_value(), mor);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("initDsAndNwTypeCache error, ", e);
		}
	}

	/**
	 * 初始化公共类别
	 * 
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	protected void initCommonTypeCache(String type) {
		try {
			ExtendedAppUtil ecb = getLocalECB();

			// 只有数据中心dc对象比较特殊可能会引起反复的更新,单独处理
			boolean isUpdate = true;
			// isMapEmpty方法写的有问题,返回的是!map.isEmpty()
			if ((type.equals(RESOURCE_DC)
					&& (Util.isMapEmpty(getMorMap(RESOURCE_DC))) && (!Util
					.isTimeout(dcUpdatetime, VIMConstants.DC_UPDATE_INTERVAL)))) {
				isUpdate = false;
			}

			if (isUpdate) {
				if (type.equals(RESOURCE_DC)) {
					dcUpdatetime = System.currentTimeMillis();
				}
				// getDecendentMoRefs获取的对象都是ManagedObjectReference
				ArrayList morList = ecb.getServiceUtil3().getDecendentMoRefs(
						null, type);
				if (morList != null) {
					LOGGER.info("initCommonTypeCache type='" + type
							+ "', size='" + morList.size() + "'");
					for (Object obj : morList) {
						ManagedObjectReference mor = (ManagedObjectReference) obj;
						getMorMap(mor.getType()).put(mor.get_value(), mor);
					}
				}
			} else {
				// LOGGER.info("initCommonTypeCache type='" + type
				// + "', isUpdate='" + isUpdate + "'");
			}
			// LOGGER.info("initCommonTypeCache type='" + type + "', size='"
			// + getMorMap(type).size() + "', dcUpdatetime='"
			// + Util.getDBDateTime(dcUpdatetime) + "', isUpdate='"
			// + isUpdate + "'");
		} catch (Exception e) {
			LOGGER.error("initCommonTypeCache error, ", e);
		}
	}

	/**
	 * 输出Cache中的对象,每个对象要单独获取名称
	 */
	public void printVIMMOMap() {
		try {
			ExtendedAppUtil ecb = getLocalECB();

			StringBuffer sBuff = new StringBuffer();

			for (Iterator<Entry<String, HashMap<String, ManagedObjectReference>>> vimMOMapEntryIt = vimMOMap
					.entrySet().iterator(); vimMOMapEntryIt.hasNext();) {
				Entry<String, HashMap<String, ManagedObjectReference>> vimMOMapEntry = vimMOMapEntryIt
						.next();
				if (sBuff.length() > 0) {
					sBuff.delete(0, sBuff.length());
				}
				for (Iterator<Entry<String, ManagedObjectReference>> vimMOEntryIt = vimMOMapEntry
						.getValue().entrySet().iterator(); vimMOEntryIt
						.hasNext();) {
					Entry<String, ManagedObjectReference> vimMOEntry = vimMOEntryIt
							.next();
					sBuff.append(vimMOEntry.getKey());
					sBuff.append(",");
					sBuff.append(VIMMgr.getEntityName(ecb, vimMOEntry
							.getValue()));
					sBuff.append(";");

				}
				LOGGER.info("type='" + vimMOMapEntry.getKey() + "',mos='"
						+ sBuff.toString() + "'");
			}
		} catch (Exception e) {
			LOGGER.error("printVIMMOMap error, ", e);
		}
	}

	/**
	 * 获取数据中心
	 * 
	 * @param id
	 * @return
	 */
	public ManagedObjectReference getDatacenter(String id) {
		return getResource(RESOURCE_DC, id);
	}

	/**
	 * 获取主机群集
	 * 
	 * @param id
	 * @return
	 */
	public ManagedObjectReference getComputeResource(String id) {
		return getResource(RESOURCE_CR, id);
	}

	/**
	 * 获取主机群集
	 * 
	 * @param id
	 * @return
	 */
	public ManagedObjectReference getClusterComputeResource(String id) {
		return getResource(RESOURCE_CCR, id);
	}

	/**
	 * 获取存储
	 * 
	 * @param id
	 * @return
	 */
	public ManagedObjectReference getDatastore(String id) {
		return getResource(RESOURCE_DC_DATASTORE, id);
	}

	/**
	 * 获取数据中心的虚拟机文件夹
	 * 
	 * @param dcId
	 * @return
	 */
	public ManagedObjectReference getVMFolder(String dcId) {
		return getResource(RESOURCE_DC_VMFOLDER, dcId);
	}

	/**
	 * 获取主机
	 * 
	 * @param id
	 * @return
	 */
	public ManagedObjectReference getHostSystem(String id) {
		return getResource(RESOURCE_HO, id);
	}

	/**
	 * 获取资源池
	 * 
	 * @param id
	 * @return
	 */
	public ManagedObjectReference getResourcePool(String id) {
		return getResource(RESOURCE_RP, id);
	}

	/**
	 * 获取虚拟机
	 * 
	 * @param id
	 * @return
	 */
	public ManagedObjectReference getVirtualMachine(String id) {
		return getResource(RESOURCE_VM, id);
	}

	/**
	 * 获取资源对象
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	public ManagedObjectReference getResource(String type, String id) {
		ManagedObjectReference mor = null;

		try {
			HashMap<String, ManagedObjectReference> morMap = getMorMap(type);
			if (morMap.containsKey(id)) {
				mor = morMap.get(id);
			} else {
				initTypeCache(type);
				mor = morMap.get(id);
			}
		} catch (Exception e) {
			LOGGER.error("getResource error, ", e);
		}
		return mor;
	}

	/**
	 * 获取某类别资源的Map
	 * 
	 * @param type
	 * @return
	 */
	private HashMap<String, ManagedObjectReference> getMorMap(String type) {
		HashMap<String, ManagedObjectReference> morMap = null;

		try {
			morMap = vimMOMap.get(type);
			if (morMap == null) {
				morMap = new HashMap<String, ManagedObjectReference>();
				vimMOMap.put(type, morMap);
			}
		} catch (Exception e) {
			LOGGER.error("getMorMap error, ", e);
		}
		return morMap;
	}

	/**
	 * 获取所有对象
	 * 
	 * @return
	 */
	protected HashMap<String, HashMap<String, ManagedObjectReference>> getVIMMOMap() {
		return vimMOMap;
	}

	/**
	 * 初始化事件的"类型"-->告警、信息、错误等等
	 */
	protected void initEventCategory() {
		try {
			ExtendedAppUtil ecb = getLocalECB();

			ManagedObjectReference evtMgr = ecb.getServiceConnection3()
					.getServiceContent().getEventManager();

			EventDescription evtDesc = (EventDescription) VIMMgr
					.getDynamicProperty(ecb, evtMgr,
							DYNAMICPROPERTY_DESCRIPTION);

			String key = "";
			String value = "";
			for (EventDescriptionEventDetail evtDetail : evtDesc.getEventInfo()) {
				key = "";
				if ((evtDetail.getKey().equals(EVENTDETAIL_KEY_EXTENDEDEVENT))
						|| (evtDetail.getKey().equals(EVENTDETAIL_KEY_EVENTEX))) {
					if (evtDetail.getFullFormat().indexOf("|") >= 0) {
						// EventDescriptionEventDetail=getFullFormat=ad.event.ImportCertFailedEvent|导入证书失败。
						// split("|")会把字符串拆分成单个的字符
						// String[] msgs = evtDetail.getFullFormat().split("|");
						String[] msgs = evtDetail.getFullFormat().split("\\|");
						key = msgs[0];
					}
					value = evtDetail.getCategory();
				} else {
					key = evtDetail.getKey();
					value = evtDetail.getCategory();
				}

				if (!key.equals("")) {
					if (!eventCategoryMap.containsKey(key)) {
						// if (LOGGER.isDebugEnabled()) {
						// LOGGER.debug("initEventCategory key='" + key + "'");
						// }
						eventCategoryMap.put(key, value);
					} else {
						// key重复
					}
				} else {
					// key为空
				}
			}
		} catch (Exception e) {
			LOGGER.error("initEventCategory error, ", e);
		}
	}

	/**
	 * 获取事件的"类型"
	 * 
	 * @param event
	 * @return
	 */
	public String getEventCategory(Event event) {
		String result = EVENT_CATEGORY_UNKNOWN;
		try {
			String eventClazz = event.getClass().getName();
			if (event instanceof EventEx) {
				EventEx eventEx = (EventEx) event;
				eventClazz = eventEx.getEventTypeId();
			}
			if (event instanceof ExtendedEvent) {
				ExtendedEvent extendedEvent = (ExtendedEvent) event;
				eventClazz = extendedEvent.getEventTypeId();
			}

			String[] strArr = eventClazz.split("\\.");
			String lastEventClazz = strArr[strArr.length - 1];

			if (eventCategoryMap.containsKey(eventClazz)) {
				result = eventCategoryMap.get(eventClazz);
			} else if (eventCategoryMap.containsKey(lastEventClazz)) {
				result = eventCategoryMap.get(lastEventClazz);
			}

			if (result.equals(EVENT_CATEGORY_UNKNOWN)) {
				LOGGER.error("getEventCategory event='"
						+ event.getClass().getName() + "', eventClazz='"
						+ eventClazz + "', fullMsg='"
						+ event.getFullFormattedMessage() + "'");
			}
		} catch (Exception e) {
			LOGGER.error("getEventCategory error, ", e);
		}
		return result;
	}

	/**
	 * 初始化任务的描述
	 */
	protected void initTaskDesc() {
		try {
			ExtendedAppUtil ecb = getLocalECB();

			ManagedObjectReference taskMgr = ecb.getServiceConnection3()
					.getServiceContent().getTaskManager();

			TaskDescription taskDesc = (TaskDescription) VIMMgr
					.getDynamicProperty(ecb, taskMgr,
							DYNAMICPROPERTY_DESCRIPTION);

			// LOGGER.info("-------------------------------getMethodInfo");
			// for (ElementDescription elemDesc : taskDesc.getMethodInfo()) {
			// LOGGER.info("========getKey=" + elemDesc.getKey());
			// LOGGER.info("========getLabel=" + elemDesc.getLabel());
			// LOGGER.info("========getSummary=" + elemDesc.getSummary());
			// }
			//
			// LOGGER.info("-------------------------------getReason");
			// for (TypeDescription typeDesc : taskDesc.getReason()) {
			// LOGGER.info("========getKey=" + typeDesc.getKey());
			// LOGGER.info("========getLabel=" + typeDesc.getLabel());
			// LOGGER.info("========getSummary=" + typeDesc.getSummary());
			// }
			//
			// LOGGER.info("-------------------------------getState");
			// for (ElementDescription elemDesc : taskDesc.getState()) {
			// LOGGER.info("========getKey=" + elemDesc.getKey());
			// LOGGER.info("========getLabel=" + elemDesc.getLabel());
			// LOGGER.info("========getSummary=" + elemDesc.getSummary());
			// }

			for (ElementDescription elemDesc : taskDesc.getMethodInfo()) {
				taskDescMap.put(elemDesc.getKey(), elemDesc.getLabel());
			}
		} catch (Exception e) {
			LOGGER.error("initTaskDesc error, ", e);
		}
	}

	/**
	 * 获取任务的名称
	 * 
	 * @param descId
	 * @return
	 */
	public String getTaskDesc(String descId) {
		try {
			return taskDescMap.containsKey(descId) ? taskDescMap.get(descId)
					: descId;
		} catch (Exception e) {
			LOGGER.error("getTaskDesc error, ", e);
			return descId;
		}
	}

	/**
	 * 初始化告警的描述
	 */
	protected void initAlarmDesc() {
		try {
			ExtendedAppUtil ecb = getLocalECB();

			ManagedObjectReference almMgr = ecb.getServiceConnection3()
					.getServiceContent().getAlarmManager();

			AlarmDescription almDesc = (AlarmDescription) VIMMgr
					.getDynamicProperty(ecb, almMgr,
							DYNAMICPROPERTY_DESCRIPTION);

			// SendSNMPAction,发送 SNMP
			// SendEmailAction,发送电子邮件
			// CreateTaskAction,创建任务
			// RunScriptAction,运行脚本
			// MethodAction,方法操作
			for (TypeDescription desc : almDesc.getAction()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

			// True,已连接
			// False,已断开

			for (ElementDescription desc : almDesc
					.getDatastoreConnectionState()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

			// gray,灰色
			// green,绿色
			// yellow,黄色
			// red,红色
			for (ElementDescription desc : almDesc.getEntityStatus()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

			// 无
			for (TypeDescription desc : almDesc.getExpr()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}
			// connected,已连接
			// notResponding,无响应
			// disconnected,已断开
			for (ElementDescription desc : almDesc
					.getHostSystemConnectionState()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

			// poweredOn,已打开电源
			// poweredOff,已关闭电源
			// standBy,待机
			// unknown,未知
			for (ElementDescription desc : almDesc.getHostSystemPowerState()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

			// isAbove,高于
			// isBelow,低于
			for (ElementDescription desc : almDesc.getMetricOperator()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

			// isEqual,等于
			// isUnequal,不等于
			for (ElementDescription desc : almDesc.getStateOperator()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

			// gray,灰色
			// green,绿色
			// yellow,黄色
			// red,红色
			for (ElementDescription desc : almDesc
					.getVirtualMachineGuestHeartbeatStatus()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

			// poweredOff,已关闭电源
			// poweredOn,已打开电源
			// suspended,已挂起
			for (ElementDescription desc : almDesc
					.getVirtualMachinePowerState()) {
				almDescMap.put(desc.getKey(), desc.getLabel());
			}

		} catch (Exception e) {
			LOGGER.error("initAlarmDesc error, ", e);
		}
	}

	/**
	 * 获取告警的描述
	 * 
	 * @param descId
	 * @return
	 */
	public String getAlarmDesc(String descId) {
		try {
			return almDescMap.containsKey(descId) ? almDescMap.get(descId)
					: descId;
		} catch (Exception e) {
			LOGGER.error("getAlarmDesc error, ", e);
			return descId;
		}
	}

	/**
	 * 初始化告警
	 */
	protected void initAlarm() {
		try {
			ExtendedAppUtil ecb = getLocalECB();

			ManagedObjectReference almMgr = ecb.getServiceConnection3()
					.getServiceContent().getAlarmManager();

			ManagedObjectReference[] almList = ecb.getServiceConnection3()
					.getService().getAlarm(almMgr, null);
			if (almList != null) {
				for (ManagedObjectReference almMor : almList) {
					// http://pubs.vmware.com/vsphere-50/topic/com.vmware.wssdk.apiref.doc_50/vim.alarm.Alarm.html
					AlarmInfo almInfo = (AlarmInfo) VIMMgr.getDynamicProperty(
							ecb, almMor, VIMConstants.DYNAMICPROPERTY_INFO);
					if (almInfo != null) {
						almMap.put(almMor.get_value(), almInfo);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("initAlarm error, ", e);
		}
	}

	/**
	 * 获取告警
	 * 
	 * @param id
	 * @return
	 */
	public AlarmInfo getAlarm(String id) {
		AlarmInfo almInfo = null;

		try {
			if (almMap.containsKey(id)) {
				almInfo = almMap.get(id);
			} else {
				initAlarm();
				almInfo = almMap.get(id);
			}
		} catch (Exception e) {
			LOGGER.error("getAlarm error, ", e);
		}
		return almInfo;
	}

	/**
	 * 初始化枚举值<br>
	 * 不确定系统是否提供这样的方法获取到枚举值的本地化翻译结果
	 */
	protected void initEnum() {
		try {
			// VirtualMachineToolsRunningStatus
			// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.vm.GuestInfo.ToolsRunningStatus.html
			enumMap.put("guestToolsExecutingScripts", "正在启动");
			enumMap.put("guestToolsNotRunning", "未运行");
			enumMap.put("guestToolsRunning", "已运行");

			// VirtualMachineToolsVersionStatus
			// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.vm.GuestInfo.ToolsVersionStatus.html
			// enumMap.put("guestToolsBlacklisted", "应立即升级");//Since5.0
			enumMap.put("guestToolsCurrent", "已安装");
			enumMap.put("guestToolsNeedUpgrade", "需升级");
			enumMap.put("guestToolsNotInstalled", "未安装");
			// enumMap.put("guestToolsSupportedNew", ""); //Since5.0
			// enumMap.put("guestToolsSupportedOld", "");//Since5.0
			// enumMap.put("guestToolsTooNew", "");//Since5.0
			// enumMap.put("guestToolsTooOld", "");//Since5.0
			enumMap.put("guestToolsUnmanaged", "未管理");
		} catch (Exception e) {
			LOGGER.error("initEnum error, ", e);
		}
	}

	/**
	 * 获取枚举值
	 * 
	 * @param id
	 * @return
	 */
	public String getEnum(String id) {
		String value = id;

		try {
			if (enumMap.containsKey(id)) {
				value = enumMap.get(id);
			}
		} catch (Exception e) {
			LOGGER.error("getEnum error, ", e);
		}
		return value;
	}

	/**
	 * 初始化性能间隔<br>
	 */
	protected void initPerfInterval() {

		try {
			ExtendedAppUtil ecb = getLocalECB();

			ManagedObjectReference perfMgr = ecb.getServiceConnection3()
					.getServiceContent().getPerfManager();

			PerfInterval[] historicalInterval = (PerfInterval[]) VIMMgr
					.getDynamicProperty(ecb, perfMgr,
							DYNAMICPROPERTY_HISTORICALINTERVAL);
			if (historicalInterval != null) {
				for (PerfInterval perfInterval : historicalInterval) {
					String key = Integer.toString(perfInterval.getKey());

					// getKey=1
					// getLength=86400
					// getLevel=1
					// getName=过去一天
					// getSamplingPeriod=300
					//					
					// getKey=2
					// getLength=604800
					// getLevel=1
					// getName=过去一周
					// getSamplingPeriod=1800
					//					
					// getKey=3
					// getLength=2592000
					// getLevel=1
					// getName=过去一个月
					// getSamplingPeriod=7200
					//					
					// getKey=4
					// getLength=31536000
					// getLevel=1
					// getName=过去一年
					// getSamplingPeriod=86400

					perfIntervalMap.put(key, perfInterval);
				}
			}
		} catch (Exception e) {
			LOGGER.error("initPerfInterval error, ", e);
		}
	}

	/**
	 * 获取性能间隔
	 * 
	 * @param id
	 * @return
	 */
	public PerfInterval getPerfInterval(String id) {
		PerfInterval value = null;
		try {
			if (perfIntervalMap.containsKey(id)) {
				value = perfIntervalMap.get(id);
			}
		} catch (Exception e) {
			LOGGER.error("getPerfInterval error, ", e);
		}
		return value;
	}

	/**
	 * 初始化性能计数器<br>
	 */
	protected void initPerfCounterInfo() {

		try {
			ExtendedAppUtil ecb = getLocalECB();

			ManagedObjectReference perfMgr = ecb.getServiceConnection3()
					.getServiceContent().getPerfManager();

			PerfCounterInfo[] perfCounter = (PerfCounterInfo[]) VIMMgr
					.getDynamicProperty(ecb, perfMgr,
							DYNAMICPROPERTY_PERFCOUNTER);
			if (perfCounter != null) {
				for (PerfCounterInfo perfCounterInfo : perfCounter) {
					String key = perfCounterInfo.getGroupInfo().getKey()
							+ Util.SEPARATOR_PERIOD
							+ perfCounterInfo.getNameInfo().getKey()
							+ Util.SEPARATOR_PERIOD
							+ perfCounterInfo.getRollupType();
					perfCounterInfoMap.put(key, perfCounterInfo);
				}
			}
		} catch (Exception e) {
			LOGGER.error("initPerfCounterInfo error, ", e);
		}
	}

	/**
	 * 获取性能计数器
	 * 
	 * @param id
	 * @return
	 */
	public PerfCounterInfo getPerfCounterInfo(String id) {
		PerfCounterInfo value = null;
		try {
			if (perfCounterInfoMap.containsKey(id)) {
				value = perfCounterInfoMap.get(id);
			}
		} catch (Exception e) {
			LOGGER.error("getPerfCounterInfo error, ", e);
		}
		return value;
	}

}
