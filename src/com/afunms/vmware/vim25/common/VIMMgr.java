package com.afunms.vmware.vim25.common;

import com.afunms.vmware.vim25.cache.VIMCache;
import com.afunms.vmware.vim25.constants.VIMConstants;
import com.afunms.vmware.vim25.util.Util;
import com.afunms.vmware.vim25.vo.ComparableHashMap;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.AboutInfo;
import com.vmware.vim25.AlarmInfo;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.ComputeResourceSummary;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.Event;
import com.vmware.vim25.EventFilterSpec;
import com.vmware.vim25.EventFilterSpecByEntity;
import com.vmware.vim25.EventFilterSpecByTime;
import com.vmware.vim25.EventFilterSpecRecursionOption;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.HostRuntimeInfo;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfFormat;
import com.vmware.vim25.PerfInterval;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.StoragePodSummary;
import com.vmware.vim25.TaskEvent;
import com.vmware.vim25.TaskFilterSpec;
import com.vmware.vim25.TaskFilterSpecByEntity;
import com.vmware.vim25.TaskFilterSpecRecursionOption;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskReasonAlarm;
import com.vmware.vim25.TaskReasonSchedule;
import com.vmware.vim25.TaskReasonSystem;
import com.vmware.vim25.TaskReasonUser;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineStorageInfo;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.VirtualMachineUsageOnDatastore;

/**
 * 共用管理,实现VMWare中的管理对应的一些公共的方法
 * 
 * @author LXL
 * 
 */

public class VIMMgr extends VIMConnection implements VIMConstants {

	// LOGGER
	private final static Logger LOGGER = Logger.getLogger(VIMMgr.class);

	// 小数点后2位不数字格式
	private static final DecimalFormat BITS2_DF = new DecimalFormat("#.##");

	/**
	 * 记录错误状态和错误信息
	 * 
	 * @param resultMap
	 * @param state
	 * @param error
	 */
	protected static void recordResultMapError(HashMap<String, Object> resultMap, String error) {
		resultMap.put(INFO_STATE, STATE_ERROR);
		resultMap.put(INFO_ERROR, error);
		resultMap.put(INFO_PROGRESS, "");
		resultMap.put(INFO_RESULT, "");
	}

	/**
	 * 记录正确状态
	 * 
	 * @param resultMap
	 */
	protected static void recordResultMapSuccess(HashMap<String, Object> resultMap) {
		resultMap.put(INFO_STATE, STATE_OK);
		resultMap.put(INFO_ERROR, "");
		resultMap.put(INFO_PROGRESS, "");
		resultMap.put(INFO_RESULT, "");
	}

	// /**
	// * 记录状态和,错误信息,处理,结果
	// *
	// * @param resultMap
	// * @param state
	// * @param error
	// */
	// private static void recordResultMap(HashMap<String, Object> resultMap,
	// String state, String error, String progress, String result) {
	// resultMap.put(INFO_STATE, state);
	// resultMap.put(INFO_ERROR, error);
	// resultMap.put(INFO_PROGRESS, progress);
	// resultMap.put(INFO_RESULT, result);
	// }

	/**
	 * 捕获错误后输出错误信息
	 * 
	 * @param resultMap
	 * @param e
	 */
	protected static void recordResultMapException(HashMap<String, Object> resultMap, Exception e) {
		resultMap.put(INFO_STATE, STATE_ERROR);
		resultMap.put(INFO_ERROR, ERROR_UNKNOWN + e.getMessage());
		resultMap.put(INFO_PROGRESS, "");
		resultMap.put(INFO_RESULT, "");
	}

	/**
	 * 执行任务并
	 * 
	 * @param ecb
	 * @param taskMor
	 * @param resultMap
	 */
	protected static void execTaskAndRecordResultMap(ExtendedAppUtil ecb, ManagedObjectReference taskMor, HashMap<String, Object> resultMap) throws Exception {
		// 执行任务
		ecb.getServiceUtil3().waitForTask(taskMor);

		// 获取任务的动态属性
		TaskInfo taskInfo = (TaskInfo) getDynamicProperty(ecb, taskMor, DYNAMICPROPERTY_INFO);

		// 记录任务执行情况
		resultMap.put(INFO_STATE, taskInfo.getState().getValue());
		if (taskInfo.getError() != null) {
			resultMap.put(INFO_ERROR, taskInfo.getError().getLocalizedMessage());
		} else {
			resultMap.put(INFO_ERROR, "");
		}
		if (taskInfo.getProgress() != null) {
			resultMap.put(INFO_PROGRESS, taskInfo.getProgress().toString());
		} else {
			resultMap.put(INFO_PROGRESS, "0");
		}
		if (taskInfo.getResult() != null) {
			resultMap.put(INFO_RESULT, taskInfo.getResult().toString());
		} else {
			resultMap.put(INFO_RESULT, "");
		}
	}

	/**
	 * 获取对象的多个动态属性<br>
	 * 方法来自vsphere-ws\java\Axis\com\vmware\apputils\vim25\ServiceUtil.java<br>
	 * 增加ExtendedAppUtil参数
	 * 
	 * @param ecb
	 * @param mor
	 * @param propertyNames
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static List<Object> getDynamicProperties(ExtendedAppUtil ecb, ManagedObjectReference mor, String[] propertyNames) throws Exception {

		// TODO 这里还是返回一个对象
		ObjectContent[] objContents = getObjectPropertiesEx(ecb, null, mor, propertyNames);

		ArrayList<Object> propertyValues = new ArrayList<Object>();

		if (objContents != null) {
			for (ObjectContent objContent : objContents) {
				DynamicProperty[] dynamicProperty = objContent.getPropSet();
				if (dynamicProperty != null) {
					/*
					 * Check the dynamic propery for ArrayOfXXX object
					 */
					Object dynamicPropertyVal = dynamicProperty[0].getVal();
					String dynamicPropertyName = dynamicPropertyVal.getClass().getName();
					if (dynamicPropertyName.indexOf("ArrayOf") != -1) {
						String methodName = dynamicPropertyName.substring(dynamicPropertyName.indexOf("ArrayOf") + "ArrayOf".length(), dynamicPropertyName.length());
						/*
						 * If object is ArrayOfXXX object, then get the XXXX[]
						 * by invoking getXXX() on the object. For Ex:
						 * ArrayOfManagedObjectReference
						 * .getManagedObjectReference() returns
						 * ManagedObjectReference[] array.
						 */
						if (methodExists(dynamicPropertyVal, "get" + methodName, null)) {
							methodName = "get" + methodName;
						} else {
							/*
							 * Construct methodName for ArrayOf primitive types
							 * Ex: For ArrayOfInt, methodName is get_int
							 */
							methodName = "get_" + methodName.toLowerCase();
						}
						Method getMorMethod = dynamicPropertyVal.getClass().getDeclaredMethod(methodName, (Class[]) null);
						propertyValues.add(getMorMethod.invoke(dynamicPropertyVal, (Object[]) null));
					} else if (dynamicPropertyVal.getClass().isArray()) {
						/*
						 * Handle the case of an unwrapped array being
						 * deserialized.
						 */
						propertyValues.add(dynamicPropertyVal);
					} else {
						propertyValues.add(dynamicPropertyVal);
					}
				}
			}
		}
		return propertyValues;
	}

	/**
	 * getDynamicProperties方法依赖方法<br>
	 * 修改自getObjectProperties方法,
	 * 
	 * @param ecb
	 * @param collector
	 * @param mobj
	 * @param properties
	 * @return
	 * @throws Exception
	 */
	private static ObjectContent[] getObjectPropertiesEx(ExtendedAppUtil ecb, ManagedObjectReference collector, ManagedObjectReference mobj, String[] properties) throws Exception {

		if (mobj == null) {
			return null;
		}

		ManagedObjectReference usecoll = collector;
		if (usecoll == null) {
			usecoll = ecb.getServiceConnection3().getPropCol();
		}

		PropertyFilterSpec[] propFilterSpecs = new PropertyFilterSpec[properties.length];

		for (int i = 0; i < properties.length; i++) {

			PropertyFilterSpec spec = new PropertyFilterSpec();
			spec.setPropSet(new PropertySpec[] { new PropertySpec() });
			spec.getPropSet(0).setAll(new Boolean(properties == null || properties.length == 0));
			spec.getPropSet(0).setType(mobj.getType());
			spec.getPropSet(0).setPathSet(new String[] { properties[i] });

			spec.setObjectSet(new ObjectSpec[] { new ObjectSpec() });
			spec.getObjectSet(0).setObj(mobj);
			spec.getObjectSet(0).setSkip(Boolean.FALSE);

			propFilterSpecs[i] = spec;
		}

		return ecb.getServiceConnection3().getService().retrieveProperties(usecoll, propFilterSpecs);
	}

	/**
	 * 获取对象的动态属性<br>
	 * 方法来自vsphere-ws\java\Axis\com\vmware\apputils\vim25\ServiceUtil.java<br>
	 * 增加ExtendedAppUtil参数
	 * 
	 * @param ecb
	 * @param mor
	 * @param propertyName
	 * @return
	 * @throws Exception
	 */
	public static Object getDynamicProperty(ExtendedAppUtil ecb, ManagedObjectReference mor, String propertyName) throws Exception {
		ObjectContent[] objContent = getObjectProperties(ecb, null, mor, new String[] { propertyName });

		Object propertyValue = null;
		if (objContent != null) {
			DynamicProperty[] dynamicProperty = objContent[0].getPropSet();
			if (dynamicProperty != null) {
				/*
				 * Check the dynamic propery for ArrayOfXXX object
				 */
				Object dynamicPropertyVal = dynamicProperty[0].getVal();
				String dynamicPropertyName = dynamicPropertyVal.getClass().getName();
				if (dynamicPropertyName.indexOf("ArrayOf") != -1) {
					String methodName = dynamicPropertyName.substring(dynamicPropertyName.indexOf("ArrayOf") + "ArrayOf".length(), dynamicPropertyName.length());
					/*
					 * If object is ArrayOfXXX object, then get the XXXX[] by
					 * invoking getXXX() on the object. For Ex:
					 * ArrayOfManagedObjectReference.getManagedObjectReference()
					 * returns ManagedObjectReference[] array.
					 */
					if (methodExists(dynamicPropertyVal, "get" + methodName, null)) {
						methodName = "get" + methodName;
					} else {
						/*
						 * Construct methodName for ArrayOf primitive types Ex:
						 * For ArrayOfInt, methodName is get_int
						 */
						methodName = "get_" + methodName.toLowerCase();
					}
					Method getMorMethod = dynamicPropertyVal.getClass().getDeclaredMethod(methodName, (Class[]) null);
					propertyValue = getMorMethod.invoke(dynamicPropertyVal, (Object[]) null);
				} else if (dynamicPropertyVal.getClass().isArray()) {
					/*
					 * Handle the case of an unwrapped array being deserialized.
					 */
					propertyValue = dynamicPropertyVal;
				} else {
					propertyValue = dynamicPropertyVal;
				}
			}
		}
		return propertyValue;
	}

	/**
	 * getDynamicProperty方法依赖方法<br>
	 * 方法来自vsphere-ws\java\Axis\com\vmware\apputils\vim25\ServiceUtil.java<br>
	 * 增加ExtendedAppUtil参数
	 * 
	 * @param ecb
	 * @param collector
	 * @param mobj
	 * @param properties
	 * @return
	 * @throws Exception
	 */
	private static ObjectContent[] getObjectProperties(ExtendedAppUtil ecb, ManagedObjectReference collector, ManagedObjectReference mobj, String[] properties) throws Exception {
		if (mobj == null) {
			return null;
		}

		ManagedObjectReference usecoll = collector;
		if (usecoll == null) {
			try {
				usecoll = ecb.getServiceConnection3().getPropCol();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		PropertyFilterSpec spec = new PropertyFilterSpec();
		spec.setPropSet(new PropertySpec[] { new PropertySpec() });
		spec.getPropSet(0).setAll(new Boolean(properties == null || properties.length == 0));
		spec.getPropSet(0).setType(mobj.getType());
		spec.getPropSet(0).setPathSet(properties);

		spec.setObjectSet(new ObjectSpec[] { new ObjectSpec() });
		spec.getObjectSet(0).setObj(mobj);
		spec.getObjectSet(0).setSkip(Boolean.FALSE);

		return ecb.getServiceConnection3().getService().retrieveProperties(usecoll, new PropertyFilterSpec[] { spec });
	}

	/**
	 * getDynamicProperty方法依赖方法<br>
	 * 方法来自vsphere-ws\java\Axis\com\vmware\apputils\vim25\ServiceUtil.java<br>
	 * 
	 * @param obj
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static boolean methodExists(Object obj, String methodName, Class[] parameterTypes) {
		boolean exists = false;
		try {
			Method method = obj.getClass().getMethod(methodName, parameterTypes);
			if (method != null) {
				exists = true;
			}
		} catch (Exception e) {
		}
		return exists;
	}

	/**
	 * 同步接口,同步的数据包括数据中心、主机群集、主机、资源池、虚拟机、数据存储等，以及他们之间的关系。<br>
	 * 此接口暂不涉及返回树形结构<br>
	 * 树形结构展现形式目前先按照VIM内部结构展示。（此结构和vCenter的树形结构不同,如虚拟机和资源池只是关联关系,没有父子关系;
	 * 虚拟机上面是vm文件夹,vm文件夹上面是数据中心等）<br>
	 * <br>
	 * <br>
	 * 外部在调用方法时应该先判断返回的Map中的info.state的值,并且应该将delete的SQL和insert的SQL放在一个事务中,
	 * 防止出现返回的Map为空, 却把数据库表中的数据删除的问题 <br>
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> syncVIMObjs(String url, String username, String password) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		LOGGER.info("syncVIMObjs, url='" + url + "', username='" + username + "', password='" + password + "'");

		try {
			// 同步的数据有
			// 1：数据中心
			ArrayList<HashMap<String, Object>> dcList = new ArrayList<HashMap<String, Object>>();
			resultMap.put(SYNC_DC, dcList);
			// 2：云，群集
			ArrayList<HashMap<String, Object>> crList = new ArrayList<HashMap<String, Object>>();
			resultMap.put(SYNC_CR, crList);
			// 3：资源池
			ArrayList<HashMap<String, Object>> rpList = new ArrayList<HashMap<String, Object>>();
			resultMap.put(SYNC_RP, rpList);
			// 4：存储 存储群集
			ArrayList<HashMap<String, Object>> dsList = new ArrayList<HashMap<String, Object>>();
			resultMap.put(SYNC_DS, dsList);
			// 5：物理机
			ArrayList<HashMap<String, Object>> hoList = new ArrayList<HashMap<String, Object>>();
			resultMap.put(SYNC_HO, hoList);
			// 6：虚机
			ArrayList<HashMap<String, Object>> vmList = new ArrayList<HashMap<String, Object>>();
			resultMap.put(RESOURCE_VM, vmList);
			// 7：模版
			ArrayList<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
			resultMap.put(SYNC_TEMPLATE, tempList);
			// 其他,系统无法处理的对象
			ArrayList<HashMap<String, Object>> otherList = new ArrayList<HashMap<String, Object>>();
			resultMap.put(SYNC_OTHER, otherList);

			// 数据库表关联关系如下(多采用外键关联)
			// 1：数据中心
			// 2：云，群集 1
			// 3：资源池 2
			// 4：存储 存储群集 2
			// 5：物理机 2
			// 6：虚机 2,3,4,5
			// 7：模版 2,3,4,5

			ExtendedAppUtil ecb = getECB(url, username, password);

			// 注：下面所有已"--"开头的字段接口无法提供,由MIS系统处理

			// 获取所有的数据中心
			ArrayList dcMorList = ecb.getServiceUtil3().getDecendentMoRefs(null, RESOURCE_DC);
			if (dcMorList != null) {
				LOGGER.info("syncVIMObjs, dcMorList='" + dcMorList + "'");

				int i = 0;
				for (Object obj : dcMorList) {
					ManagedObjectReference dcMor = (ManagedObjectReference) obj;
					// 同步数据中心对象
					syncDatacenter(ecb, dcMor, dcList);

					// 保存rp和cr的关系,为了虚拟通过rp获取到cr的id
					// key是rpid,value是crid
					HashMap<String, String> crRpMap = new HashMap<String, String>();
					// 获取群集和物理主机
					syncHostFolder(ecb, dcMor, crList, rpList, hoList, otherList, crRpMap);

					if (i == 0) {
						// 获取vmFolder
						syncVMFolder(ecb, dcMor, vmList, tempList, rpList, otherList, crRpMap);
					}
					// 存储和存储群集
					syncDatastoreFolderAndDatastore(ecb, dcMor, dsList, otherList);

					i++;
					// 数据中心下还有networkFolder和network未处理
				}
			}

			// 处理虚拟机，取出数据重复
			// if(vmList != null && vmList.size()>0){
			// for(int i=0;i<vmList.size()/dcMorList.size();i++){
			// vmList1.add(vmList.get(i));
			// }
			// }
			// resultMap.put(RESOURCE_VM, vmList1);

			// 记录处理正确的标识
			recordResultMapSuccess(resultMap);
			LOGGER.error("#############同步接口，采集完毕！！####################");
		} catch (Exception e) {
			LOGGER.error("syncVIMObj error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 同步数据中心对象
	 * 
	 * @param ecb
	 * @param mor
	 * @param resultList
	 * @throws Exception
	 */
	private static void syncDatacenter(ExtendedAppUtil ecb, ManagedObjectReference mor, ArrayList<HashMap<String, Object>> resultList) throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		LOGGER.info("syncDatacenter, mor='" + mor + "'");

		HashMap<String, Object> map = new HashMap<String, Object>();

		// 数据中心表中的字段有
		// --1：主键ID

		// 2：数据中心名称
		map.put(SYNC_COMMON_NAME, getEntityName(ecb, mor));

		// --3：备注

		// 4：VMware对应的资源ID，就是VID
		map.put(SYNC_COMMON_VID, mor.get_value());

		resultList.add(map);
	}

	/**
	 * 获取文件夹下的实体数据
	 * 
	 * @param ecb
	 * @param mor
	 * @param folderName
	 * @return
	 */
	private static ManagedObjectReference[] getChildEntityFromFolder(ExtendedAppUtil ecb, ManagedObjectReference mor, String folderName) throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		ManagedObjectReference folderMor = (ManagedObjectReference) getDynamicProperty(ecb, mor, folderName);
		return (ManagedObjectReference[]) getDynamicProperty(ecb, folderMor, DYNAMICPROPERTY_CHILDENTITY);
	}

	/**
	 * 同步数据中心vmFolder下的对象,
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param crList
	 * @param rpList
	 * @param hoList
	 * @param otherList
	 * @param crRpMap
	 * @throws Exception
	 */
	private static void syncHostFolder(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ArrayList<HashMap<String, Object>> crList, ArrayList<HashMap<String, Object>> rpList,
			ArrayList<HashMap<String, Object>> hoList, ArrayList<HashMap<String, Object>> otherList, HashMap<String, String> crRpMap) throws Exception {

		ManagedObjectReference[] hoMorList = getChildEntityFromFolder(ecb, dcMor, DYNAMICPROPERTY_HOSTFOLDER);
		if (hoMorList != null) {
			LOGGER.info("syncHostFolder, dcMor='" + dcMor + "', hoMorList='" + hoMorList.length + "'");

			for (ManagedObjectReference hoMor : hoMorList) {
				if ((hoMor.getType().equals(RESOURCE_CCR)) || (hoMor.getType().equals(RESOURCE_CR))) {
					syncComputeResource(ecb, dcMor, hoMor, crList, rpList, hoList, otherList, crRpMap);
					// } else if (hoMor.getType().equals(RESOURCE_HO)) {
					// 公司和现场都没有这种情况。主机直接添加到数据中心下也不会出现,系统会自动创建一个与该主机同名的群集
					// 该群集的命名为domain-s,一般的群集命名为domain-c
					// syncHostSystem(ecb, dcMor, hoMor, hoList);
				} else {
					syncOtherMor(ecb, dcMor, hoMor, "syncHostFolder", DYNAMICPROPERTY_VMFOLDER, otherList);
				}
			}
		}
	}

	/**
	 * 同步资源池对象,包括ResourcePool和VirtualApp
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param crMor
	 * @param mor
	 * @param resultList
	 * @param crRpMap
	 * @throws Exception
	 */
	private static void syncResourcePool(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ManagedObjectReference crMor, ManagedObjectReference mor,
			ArrayList<HashMap<String, Object>> resultList, HashMap<String, String> crRpMap) throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		LOGGER.info("syncResourcePool, dcMor='" + dcMor + "', crMor='" + crMor + "', mor='" + mor + "'");

		HashMap<String, Object> map = new HashMap<String, Object>();

		// 属主
		ManagedObjectReference ownerMor = (ManagedObjectReference) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_OWNER);

		// 资源池
		ManagedObjectReference[] rpMorList = (ManagedObjectReference[]) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_RESOURCEPOOL);

		// 资源池表中的字段有
		// -- 1：主键ID

		// 2：资源池名称
		map.put(SYNC_COMMON_NAME, getEntityName(ecb, mor));

		// 3：返回的接口ID，就是VID
		map.put(SYNC_COMMON_VID, mor.get_value());

		// -- 4：资源池备注

		// 数据中心ID
		map.put(SYNC_COMMON_DCID, dcMor.get_value());

		// 群集ID
		map.put(SYNC_COMMON_CRID, ownerMor.get_value());

		// 处理子群集,如果数据有问题可能会引起死循环
		if (rpMorList != null) {
			for (ManagedObjectReference rpMor : rpMorList) {
				syncResourcePool(ecb, dcMor, crMor, rpMor, resultList, crRpMap);
			}
		}

		// 记录cr和rp的关系
		// key是rpid,value是crid
		if (crMor != null) {
			crRpMap.put(mor.get_value(), crMor.get_value());
		}

		resultList.add(map);
	}

	/**
	 * 同步群集对象
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param mor
	 * @param crList
	 * @param rpList
	 * @param hoList
	 * @param otherList
	 * @param crRpMap
	 * @throws Exception
	 */
	private static void syncComputeResource(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ManagedObjectReference mor, ArrayList<HashMap<String, Object>> crList,
			ArrayList<HashMap<String, Object>> rpList, ArrayList<HashMap<String, Object>> hoList, ArrayList<HashMap<String, Object>> otherList, HashMap<String, String> crRpMap)
			throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		LOGGER.info("syncComputeResource, dcMor='" + dcMor + "', mor='" + mor + "'");

		HashMap<String, Object> map = new HashMap<String, Object>();

		// 根资源池
		ManagedObjectReference rpMor = (ManagedObjectReference) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_RESOURCEPOOL);

		// 所有主机
		ManagedObjectReference[] hoMorList = (ManagedObjectReference[]) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_HOST);

		// 数据存储
		ManagedObjectReference[] dsMorList = (ManagedObjectReference[]) getDynamicProperty(ecb, dcMor, DYNAMICPROPERTY_DATASTORE);

		// 摘要
		ComputeResourceSummary summary = (ComputeResourceSummary) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_SUMMARY);

		// 云，群集表中的字段有
		// --1：主键ID

		// 2：云名称
		map.put(SYNC_COMMON_NAME, getEntityName(ecb, mor));

		// --3：备注
		// --4：使用状态

		// 5：返回的接口ID，就是VID
		map.put(SYNC_COMMON_VID, mor.get_value());

		// 6：数据中心ID
		map.put(SYNC_COMMON_DCID, dcMor.get_value());

		// --7：当前云下所有虚机cpu核数总和
		// 摘要-->常规-->总CPU资源,summary.getTotalCpu,单位MHz
		map.put(SYNC_CR_TOTALCPU, summary.getTotalCpu());

		// 摘要-->常规-->处理器总数,summary.getNumCpuCores
		map.put(SYNC_CR_NUMCPUCORES, summary.getNumCpuCores());

		// --8：当前云下所有虚机内存总数
		// 摘要-->常规-->总内存,summary.getTotalMemory,单位字节
		map.put(SYNC_CR_TOTALMEMORY, convertBytes2MBString(summary.getTotalMemory()));

		// --9：当前云下所有虚机储存总数
		// 摘要-->常规-->总存储
		long totalds = 0;
		if (dsMorList != null) {
			for (ManagedObjectReference dsMor : dsMorList) {
				// 存储摘要
				DatastoreSummary dsSummary = (DatastoreSummary) getDynamicProperty(ecb, dsMor, DYNAMICPROPERTY_SUMMARY);
				totalds += dsSummary.getCapacity();
			}
		}
		map.put(SYNC_CR_TOTALDSSIZEMB, convertBytes2MBString(totalds));

		// 摘要-->常规-->主机数summary.getNumHosts(有效主机数summary.getNumEffectiveHosts)
		map.put(SYNC_CR_NUMHOSTS, summary.getNumHosts());

		crList.add(map);

		// 处理根资源池
		syncResourcePool(ecb, dcMor, mor, rpMor, rpList, crRpMap);

		// 处理主机
		if (hoMorList != null) {
			for (ManagedObjectReference hoMor : hoMorList) {
				if (hoMor.getType().equals(RESOURCE_HO)) {
					syncHostSystem(ecb, dcMor, mor, hoMor, hoList);
				} else {
					syncOtherMor(ecb, dcMor, hoMor, "syncComputeResource", DYNAMICPROPERTY_HOST, otherList);
				}
			}
		}
	}

	/**
	 * 同步主机对象
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param crMor
	 * @param mor
	 * @param resultList
	 * @throws Exception
	 */
	private static void syncHostSystem(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ManagedObjectReference crMor, ManagedObjectReference mor,
			ArrayList<HashMap<String, Object>> resultList) throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		LOGGER.info("syncHostSystem, dcMor='" + dcMor + "', crMor='" + crMor + "', mor='" + mor + "'");

		HashMap<String, Object> map = new HashMap<String, Object>();

		// 摘要
		HostListSummary summary = (HostListSummary) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_SUMMARY);

		// 硬件
		HostHardwareInfo hardware = (HostHardwareInfo) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_HARDWARE);

		// 运行
		HostRuntimeInfo runtime = (HostRuntimeInfo) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_RUNTIME);

		// 物理机表中的字段
		// --1：主键ID

		// 2：物理主机名称
		map.put(SYNC_COMMON_NAME, summary.getConfig().getName());

		// 3：物理主机型号
		// 摘要-->常规-->型号,样本:PowerEdge R710
		// 摘要-->常规-->制造商,样本:Dell Inc.,hardware.getSystemInfo().getVendor
		map.put(SYNC_HO_MODEL, hardware.getSystemInfo().getModel());

		// --4：购机日期
		// --5：使用年限
		// --6：设备描述

		// XXX 7：IP地址（界面上没有，不确定是否能取到）
		// summary.getManagementServerIp公司的值为null,山西的值所有的主机都是同一个IP
		// map.put(SYNC_HO_IP, Util.normalizeString(summary
		// .getManagementServerIp()));

		// XXX 8：电压

		// --9：运行状态
		// --10：使用状况
		// --11：所在位置

		// 12：所属云ID
		map.put(SYNC_COMMON_CRID, crMor.get_value());

		// 13：cpu个数
		// 摘要-->常规-->处理器插槽
		map.put(SYNC_HO_NUMCPU, summary.getHardware().getNumCpuPkgs());

		// 14：每个cpu核数
		// 摘要-->常规-->每个插槽的内核数
		map.put(SYNC_HO_NUMCORE, summary.getHardware().getNumCpuCores() / summary.getHardware().getNumCpuPkgs());
		// 摘要-->常规-->逻辑处理器,summary.getHardware.getNumCpuThreads

		// 15：cpu主频
		// 摘要-->常规-->CPU内核
		map.put(SYNC_HO_CPUMHZ, summary.getHardware().getCpuMhz());

		// 16：内存大小
		map.put(SYNC_HO_MEMORYSIZEMB, convertBytes2MBString(hardware.getMemorySize()));

		// 17：网卡个数（物理）
		map.put(SYNC_HO_NUMNICS, summary.getHardware().getNumNics());

		// --18：空闲存储大小

		// 19：返回的接口ID，就是Vid
		map.put(SYNC_COMMON_VID, mor.get_value());

		// 20: 电源状态
		String powerState = "";
		if (runtime != null) {
			powerState = runtime.getPowerState().toString();
		}
		map.put(SYNC_HO_POWERSTATE, powerState);

		resultList.add(map);
	}

	/**
	 * 同步数据中心vmFolder下的对象,目前发现的对象有虚拟机(模板)和vApp
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param vmList
	 * @param tempList
	 * @param rpList
	 * @param otherList
	 * @param crRpMap
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static void syncVMFolder(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ArrayList<HashMap<String, Object>> vmList, ArrayList<HashMap<String, Object>> tempList,
			ArrayList<HashMap<String, Object>> rpList, ArrayList<HashMap<String, Object>> otherList, HashMap<String, String> crRpMap) throws Exception {
		ArrayList morList = ecb.getServiceUtil3().getDecendentMoRefs(null, RESOURCE_VM);
		if (morList != null) {
			LOGGER.info("initCommonTypeCache type='" + RESOURCE_VM + "', size='" + morList.size() + "'");
			for (Object obj : morList) {
				ManagedObjectReference vmMor = (ManagedObjectReference) obj;
				if (vmMor.getType().equals(RESOURCE_VM)) {
					syncVirtualMachine(ecb, dcMor, vmMor, vmList, tempList, crRpMap);
					// vmFolder下发现名称为"已发现虚拟机"的"Folder"
					// vmFolder下发现名称为"childvApp1(自定义名称)"的"VirtualApp"

					// 暂时不处理vApp类型
					// } else if (vmMor.getType().equals(RESOURCE_VAPP)) {
					// 公司环境中在资源池下面创建的vApp在这里,获取的类型为VirtualApp
					// vApp是一个虚拟机和其他vApp的集合。
					// 从管理的角度来看，一个多层次的vApp的行为很像一个虚拟机对象。它拥有发电业务，网络，数据存储，可以配置其资源使用。
					// 从技术的角度来看，一个vApp容器是一个专门的资源库 。
					// 所以放入vApp的虚拟机就无法再遍历到
					// 模板无法拖入到vApp中
					// syncVirtualApp(ecb, dcMor, vmMor, vAppList, crRpMap);
				} else {
					syncOtherMor(ecb, dcMor, vmMor, "syncVMFolder", DYNAMICPROPERTY_VMFOLDER, otherList);
				}
			}
		}
	}

	/**
	 * 同步虚拟机和模板对象
	 * 
	 * @param ecb
	 * @param dcMo
	 * @param mor
	 * @param vmList
	 * @param tempList
	 * @param crRpMap
	 * @throws Exception
	 */
	private static void syncVirtualMachine(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ManagedObjectReference mor, ArrayList<HashMap<String, Object>> vmList,
			ArrayList<HashMap<String, Object>> tempList, HashMap<String, String> crRpMap) throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		LOGGER.info("syncVirtualMachine, dcMor='" + dcMor + "', mor='" + mor + "'");

		HashMap<String, Object> map = new HashMap<String, Object>();

		// 配置
		VirtualMachineConfigInfo config = (VirtualMachineConfigInfo) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_CONFIG);

		// 运行
		VirtualMachineRuntimeInfo runtime = (VirtualMachineRuntimeInfo) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_RUNTIME);

		// 摘要
		VirtualMachineSummary summary = (VirtualMachineSummary) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_SUMMARY);

		// 资源池
		ManagedObjectReference rpMor = (ManagedObjectReference) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_RESOURCEPOOL);

		// 存储
		VirtualMachineStorageInfo storage = (VirtualMachineStorageInfo) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_STORAGE);

		// 网络列表
		ManagedObjectReference[] netMorList = (ManagedObjectReference[]) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_NETWORK);

		// 虚机表中的字段
		// 模版表中的字段与虚机的字段一样只是主键ID和来源ID不一样

		// --1：主键ID

		// 2：虚机名称
		map.put(SYNC_COMMON_NAME, config.getName());

		// 3：所属云ID MIS系统中的ID,"21：群集ID "是云(群集)的VID

		// 4：所属物理主机ID 可能为空（模板就没有这个值，并且这个值可能会变化）
		String hoId = "";
		if ((runtime != null) && (runtime.getHost() != null)) {
			hoId = runtime.getHost().get_value();
		}
		map.put(SYNC_VM_HOID, hoId);

		// --5：运行状态ID

		// 6：描述
		map.put(SYNC_VM_DESC, Util.normalizeString(config.getAnnotation()));

		// --7：模板ID
		// --8：使用状态

		// 9：存储池ID
		String dsId = "";
		VirtualMachineUsageOnDatastore[] usageOnDS = storage.getPerDatastoreUsage();
		if ((usageOnDS != null) && (usageOnDS.length > 0)) {
			dsId = usageOnDS[0].getDatastore().get_value();
			if (usageOnDS.length > 1) {
				LOGGER.error("syncVMObj error, vm='" + mor.get_value() + "' usageOnDS='" + Arrays.asList(usageOnDS) + "'");
			}
		}
		map.put(SYNC_COMMON_DSID, dsId);

		// 10：cup总核数
		int numCpu = config.getHardware().getNumCPU();
		int numCore = config.getHardware().getNumCoresPerSocket();
		map.put(SYNC_VM_CPU, numCpu);

		// 11：内存容量
		map.put(SYNC_VM_MEMORYSIZEMB, summary.getConfig().getMemorySizeMB());

		// 12：存储容量
		map.put(SYNC_VM_DSSIZEMB, convertBytes2MBString(summary.getStorage().getCommitted() + summary.getStorage().getUncommitted()));

		// 13：网卡个数
		// VirtualMachineConfigSummary.numEthernetCards
		map.put(SYNC_VM_NETNUM, netMorList.length);

		// --14：操作系统版本（Windows、Linux、其他等）不显示

		// 15：虚机操作系统
		map.put(SYNC_VM_GUESTFULLNAME, config.getGuestFullName());

		// --16：所属IP的ID

		// 17：返回的接口ID 就是VID
		map.put(SYNC_COMMON_VID, mor.get_value());

		// 18：数据中心ID
		map.put(SYNC_COMMON_DCID, dcMor.get_value());

		// --19：cpu主频（一般情况下配置的都是无限）

		// 20：资源池名称ID
		String rpId = "";
		if (rpMor != null) {
			rpId = rpMor.get_value();
		}
		map.put(SYNC_VM_RPID, rpId);

		// 21：群集ID
		// 需要通过资源池id获取到群集的ID
		String crId = Util.normalizeString(crRpMap.get(rpId));
		map.put(SYNC_COMMON_CRID, crId);
		// //只有模板的资源池为空
		// if (crId.equals("")) {
		// LOGGER.error("syncVirtualMachine error, id='" + mor.get_value()
		// + "', rpId='" + rpId + "', crId='" + crId + "'");
		// }

		// 22：cpu 个数
		map.put(SYNC_VM_NUMCPU, numCpu / numCore);

		// 23：每个cpu核数
		map.put(SYNC_VM_NUMCORE, numCore);

		// --24：账户
		// --25：密码
		// --26：虚机类型 0普通虚机 1 loadrunner虚机 2 是拨测虚机
		// --27：操作动作 1为克隆 2为复制
		// --28：来源ID（对应的模板id）

		// 29: 电源状态
		String powerState = "";
		if (runtime != null) {
			powerState = runtime.getPowerState().toString();
		}
		map.put(SYNC_VM_POWERSTATE, powerState);

		// 判断是否为模板
		if (config.isTemplate()) {
			tempList.add(map);
		} else {
			vmList.add(map);
		}

		// 试试去掉判断
		// vmList.add(map);
	}

	/**
	 * 同步数据中心datastoreFolder和datastore下的对象
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param dsList
	 * @param otherList
	 * @throws Exception
	 */
	private static void syncDatastoreFolderAndDatastore(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ArrayList<HashMap<String, Object>> dsList,
			ArrayList<HashMap<String, Object>> otherList) throws Exception {

		// 从山西现场的数据看datastore和datastoreFolder的数据中本地的磁盘是重复的
		// 存储的数据是不同的,在界面中显示的是datastore的数据,但是在创建虚拟机选择磁盘的时候显示的是datastoreFolder的数据
		// ------------------------------------------------datastore
		// ============datastore-271,Cloud-CX960-Storage-VMFS5-A,Datastore
		// ============datastore-285,Cloud-CX960-Storage-VMFS5-B,Datastore
		// ============datastore-286,Cloud-CX960-Converter-VMFS3,Datastore
		// ------------------------------------------------datastoreFolder
		// ============group-p273,Cloud-CX960,StoragePod
		// ============datastore-286,Cloud-CX960-Converter-VMFS3,Datastore

		// 记录ds结果的Map
		HashMap<String, HashMap<String, Object>> dsObjMap = new HashMap<String, HashMap<String, Object>>();

		ManagedObjectReference[] dsMorList1 = (ManagedObjectReference[]) getDynamicProperty(ecb, dcMor, DYNAMICPROPERTY_DATASTORE);
		syncDatastoreMorList(ecb, dcMor, dsMorList1, dsObjMap, DYNAMICPROPERTY_DATASTORE, otherList);

		ManagedObjectReference[] dsMorList2 = getChildEntityFromFolder(ecb, dcMor, DYNAMICPROPERTY_DATASTOREFOLDER);
		syncDatastoreMorList(ecb, dcMor, dsMorList2, dsObjMap, DYNAMICPROPERTY_DATASTOREFOLDER, otherList);

		// 将Map中的数据放入dsList
		dsList.addAll(dsObjMap.values());
	}

	/**
	 * 处理数据存储列表
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param dsMorList
	 * @param dsObjMap
	 * @param source
	 * @param otherList
	 */
	private static void syncDatastoreMorList(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ManagedObjectReference[] dsMorList,
			HashMap<String, HashMap<String, Object>> dsObjMap, String source, ArrayList<HashMap<String, Object>> otherList) throws Exception {
		if (dsMorList != null) {
			LOGGER.info("syncDatastoreMorList, dcMor='" + dcMor + "', dsMorList='" + dsMorList.length + "'");

			for (ManagedObjectReference dsMor : dsMorList) {
				if (!dsObjMap.containsKey(dsMor.get_value())) {
					if (dsMor.getType().equals(RESOURCE_DS)) {
						syncDatastore(ecb, dcMor, dsMor, dsObjMap);
					} else if (dsMor.getType().equals(RESOURCE_STORAGEPOD)) {
						syncStoragePod(ecb, dcMor, dsMor, dsObjMap);
					} else {
						syncOtherMor(ecb, dcMor, dsMor, "syncDatastoreMorList", source, otherList);
					}
				}
			}
		}
	}

	/**
	 * 同步数据存储
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param mor
	 * @param dsObjMap
	 * @throws Exception
	 */
	private static void syncDatastore(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ManagedObjectReference mor, HashMap<String, HashMap<String, Object>> dsObjMap)
			throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		LOGGER.info("syncDatastore, dcMor='" + dcMor + "', mor='" + mor + "'");

		HashMap<String, Object> map = new HashMap<String, Object>();

		// 摘要
		DatastoreSummary summary = (DatastoreSummary) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_SUMMARY);

		// 存储 存储群集表中的字段
		// -- 1：主键ID
		// --2：运行状态

		// 3：存储池的容量
		map.put(SYNC_DS_CAPACITY, convertBytes2MBString(summary.getCapacity()));

		// 4：所属云ID(应该是数据中心,存储是挂接在数据中心下的,不是挂接在群集[云]下的)
		map.put(SYNC_COMMON_DCID, dcMor.get_value());

		// --5：存储池描述
		// --6：使用状态

		// 7：空闲容量
		map.put(SYNC_DS_FREESPACE, convertBytes2MBString(summary.getFreeSpace()));

		// 8：存储VID
		map.put(SYNC_COMMON_VID, mor.get_value());

		// 9：存储名称
		map.put(SYNC_COMMON_NAME, summary.getName());

		dsObjMap.put(mor.get_value(), map);
	}

	/**
	 * 同步StoragePod
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param mor
	 * @param dsObjMap
	 * @throws Exception
	 */
	private static void syncStoragePod(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ManagedObjectReference mor, HashMap<String, HashMap<String, Object>> dsObjMap)
			throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		LOGGER.info("syncStoragePod, dcMor='" + dcMor + "', mor='" + mor + "'");

		HashMap<String, Object> map = new HashMap<String, Object>();

		// 摘要
		StoragePodSummary summary = (StoragePodSummary) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_SUMMARY);

		// 子实体列表
		ManagedObjectReference[] dsMorList = (ManagedObjectReference[]) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_CHILDENTITY);

		// 存储 存储群集表中的字段
		// -- 1：主键ID
		// --2：运行状态

		// 3：存储池的容量
		map.put(SYNC_DS_CAPACITY, convertBytes2MBString(summary.getCapacity()));

		// 4：所属云ID(应该是数据中心,存储是挂接在数据中心下的,不是挂接在群集[云]下的)
		map.put(SYNC_COMMON_DCID, dcMor.get_value());

		// --5：存储池描述
		// --6：使用状态

		// 7：空闲容量
		map.put(SYNC_DS_FREESPACE, convertBytes2MBString(summary.getFreeSpace()));

		// 8：存储VID
		map.put(SYNC_COMMON_VID, mor.get_value());

		// 9：存储名称
		map.put(SYNC_COMMON_NAME, summary.getName());

		// 记录一下StoragePod和Datastore的关系
		if (dsMorList != null) {
			StringBuffer sBuff = new StringBuffer();
			for (ManagedObjectReference dsMor : dsMorList) {
				sBuff.append(dsMor.get_value() + Util.SEPARATOR_COMMA);
			}
			// 删除最后一个字符
			if (sBuff.length() > 0) {
				sBuff.delete(sBuff.length() - 1, sBuff.length());
			}
			if (sBuff.length() > 0) {
				map.put(SYNC_DS_CHILDREN, sBuff.toString());
			}
		}

		dsObjMap.put(mor.get_value(), map);
	}

	/**
	 * 同步其他对象,在系统处理中未发现的对象
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param mor
	 * @param method
	 * @param source
	 * @param resultList
	 * @throws Exception
	 */
	private static void syncOtherMor(ExtendedAppUtil ecb, ManagedObjectReference dcMor, ManagedObjectReference mor, String method, String source,
			ArrayList<HashMap<String, Object>> resultList) throws Exception {
		// 不做任何判断,出错就直接抛出违例,在外面捕获

		LOGGER.info("syncOtherMor, dcMor='" + dcMor + "', mor='" + mor + "'");

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put(SYNC_COMMON_VID, mor.get_value());
		map.put(SYNC_COMMON_NAME, getEntityName(ecb, mor));
		map.put(SYNC_COMMON_DCID, dcMor.get_value());
		map.put(SYNC_OTHER_SOURCE, source);
		map.put(SYNC_OTHER_TYPE, mor.getType());
		map.put(SYNC_OTHER_METHOD, method);

		resultList.add(map);
	}

	/**
	 * 将字节数转换为合适的显示字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String getByteSizeStrFromBytes(long bytes) {
		String result = "";
		try {
			long dividend = 0;
			String unit = "";
			// 不可能小于KB
			if (bytes <= MB_SIZE) {
				dividend = KB_SIZE;
				unit = KB;
			} else if (bytes <= GB_SIZE) {
				dividend = MB_SIZE;
				unit = MB;
			} else if (bytes <= TB_SIZE) {
				dividend = GB_SIZE;
				unit = GB;
			} else if (bytes <= PB_SIZE) {
				dividend = TB_SIZE;
				unit = TB;
			} else {
				dividend = PB_SIZE;
				unit = PB;
			}
			result = BITS2_DF.format((Double.valueOf(bytes) / dividend)) + unit;
		} catch (Exception e) {
			LOGGER.error("getByteSizeStrFromBytes error,", e);
		}

		return result;
	}

	/**
	 * 将字节数转换为MB
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static long convertBytes2MBLong(long bytes) throws Exception {
		return bytes / MB_SIZE;
	}

	/**
	 * 将字节数转换为MB,输出类型为字符串
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static String convertBytes2MBString(long bytes) throws Exception {
		return Long.toString(convertBytes2MBLong(bytes));
	}

	/**
	 * 将MB数转换为合适的显示字符串
	 * 
	 * @param mb
	 * @return
	 */
	public static String getByteSizeStrFromMB(long mb) {
		return mb + MB;
	}

	/**
	 * 获取vCenter Server的相关信息
	 * 
	 * @param url
	 * @param username
	 * @param password
	 */
	public static void getAbout(String url, String username, String password) {
		try {

			ExtendedAppUtil ecb = VIMMgr.getECB(url, username, password);

			AboutInfo aboutInfo = ecb.getServiceConnection3().getServiceContent().getAbout();

			LOGGER.info("=========getApiType=" + aboutInfo.getApiType());
			LOGGER.info("=========getApiVersion=" + aboutInfo.getApiVersion());
			LOGGER.info("=========getBuild=" + aboutInfo.getBuild());
			LOGGER.info("=========getFullName=" + aboutInfo.getFullName());
			LOGGER.info("=========getInstanceUuid=" + aboutInfo.getInstanceUuid());
			LOGGER.info("=========getLicenseProductName=" + aboutInfo.getLicenseProductName());
			LOGGER.info("=========getLicenseProductVersion=" + aboutInfo.getLicenseProductVersion());
			LOGGER.info("=========getLocaleBuild=" + aboutInfo.getLocaleBuild());
			LOGGER.info("=========getLocaleVersion=" + aboutInfo.getLocaleVersion());
			LOGGER.info("=========getName=" + aboutInfo.getName());
			LOGGER.info("=========getOsType=" + aboutInfo.getOsType());
			LOGGER.info("=========getProductLineId=" + aboutInfo.getProductLineId());
			LOGGER.info("=========getVendor=" + aboutInfo.getVendor());
			LOGGER.info("=========getVersion=" + aboutInfo.getVersion());
		} catch (Exception e) {
			LOGGER.error("getAbout error, ", e);
		}
	}

	/**
	 * 获取对象的事件,<br>
	 * vtype对应的是对象的类型,定义在VIMConstants中的RESOURCE_XXX,常用的有虚拟机,物理机等<br>
	 * vid是vtype类型的对象的id<br>
	 * timeUnit为Calendar中定义的时间单位,如Calendar.WEEK_OF_YEAR或Calendar.DAY_OF_YEAR等<br>
	 * timeDiff为时间差和timeUnit组合为开始时间,如timeUnit为Calendar.WEEK_OF_YEAR加timeDiff为2,
	 * 则开始时间为当前时间到2周前的事件<br>
	 * <br>
	 * 如果timeDiff的值为0,则时间不起作用 <br>
	 * <br>
	 * 事件的输出结果是按照时间顺序
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vtype
	 * @param vid
	 * @param timeUnit
	 * @param timeDiff
	 * @return
	 */
	public static Map<String, Object> getEvents(String url, String username, String password, String vtype, String vid, int timeUnit, int timeDiff) {

		// WebService的接口
		// This file was auto-generated from WSDL
		// VimPortType vpt=ecb.getServiceConnection3().getService();
		// ServiceContent sc = ecb.getServiceConnection3().getServiceContent();

		// 事件的类型获取的过程
		// 1.Event中没有类型的信息
		// 2.发现eventFilter.setType可以设置类型,所以一定有办法获取
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.event.EventFilterSpec.html
		// 3.查看上面的连接发现type已经废弃,用eventTypeId替代。
		// 4.在所有的类中搜索一下eventTypeId
		// 发现D:\VMSDK\vsphere-ws\java\Axis\com\vmware\vim25\EventAlarmExpression.java
		// D:\VMSDK\vsphere-ws\java\Axis\com\vmware\vim25\EventEx.java
		// D:\VMSDK\vsphere-ws\java\Axis\com\vmware\vim25\EventFilterSpec.java
		// D:\VMSDK\vsphere-ws\java\Axis\com\vmware\vim25\ExtendedEvent.java
		// 都含有eventTypeId
		// 5.在查看http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.event.Event.html
		// 发现Event Extended by EventEx
		// 6.在下面打印一下Event的className,发现有
		// com.vmware.vim25.VmAcquiredTicketEvent-->VmEvent
		// com.vmware.vim25.VmRemoteConsoleConnectedEvent
		// com.vmware.vim25.EventEx
		// 7.在打印EventEx的属性的时候发现getSeverity,认为这个对应是是类型
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.event.EventEx.html
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.event.Event.EventSeverity.html
		// 发现EventEventSeverity对应4个值:error,info,user,warning
		// 8.但是打印出来的getSeverity都是空
		// 9.最终也没有获取界面上的"类型"数据
		// 10.EventManager的description属性类型是EventDescription,
		// EventDescription的属性category的枚举类型是error,info,user,warning
		// EventDescription的属性eventInfo的类型是EventDescriptionEventDetail,EventDescriptionEventDetail也有category属性
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.event.EventManager.html
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.event.EventDescription.html
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.event.EventDescription.EventCategory.html
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.event.EventDescription.EventDetail.html

		boolean isTimeFilter = false;
		Calendar beginTime = null;
		Calendar endTime = null;

		if (timeDiff > 0) {
			beginTime = Calendar.getInstance();
			beginTime.add(timeUnit, 0 - timeDiff);
			endTime = Calendar.getInstance();
			isTimeFilter = true;
		}

		return getEvents(url, username, password, vtype, vid, beginTime, endTime, isTimeFilter);
	}

	/**
	 * 获取对象的事件,说明参考前面的方法<br>
	 * 接口直接传入开始和结束时间
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vtype
	 * @param vid
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static Map<String, Object> getEvents(String url, String username, String password, String vtype, String vid, Calendar beginTime, Calendar endTime) {
		return getEvents(url, username, password, vtype, vid, beginTime, endTime, true);
	}

	/**
	 * 获取对象的事件,说明参考前面的方法<br>
	 * 接口直接传入开始和结束时间,isTimeFilter判断是否使用时间条件
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vtype
	 * @param vid
	 * @param beginTime
	 * @param endTime
	 * @param isTimeFilter
	 * @return
	 */
	private static Map<String, Object> getEvents(String url, String username, String password, String vtype, String vid, Calendar beginTime, Calendar endTime, boolean isTimeFilter) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, String>> resultEvtList = new ArrayList<HashMap<String, String>>();

		try {
			LOGGER.info("getEvents start, vtype='" + vtype + "', vid='" + vid + "', beginTime='" + beginTime + "', endTime='" + endTime + "'");

			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getResource(vtype, vid);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				ManagedObjectReference evtMgr = ecb.getServiceConnection3().getServiceContent().getEventManager();

				EventFilterSpecByEntity entitySpec = new EventFilterSpecByEntity();
				entitySpec.setEntity(mor);
				entitySpec.setRecursion(EventFilterSpecRecursionOption.all);

				EventFilterSpec eventFilter = new EventFilterSpec();
				eventFilter.setEntity(entitySpec);

				if (isTimeFilter) {
					EventFilterSpecByTime timeSpec = new EventFilterSpecByTime();
					timeSpec.setBeginTime(beginTime);
					timeSpec.setEndTime(endTime);
					eventFilter.setTime(timeSpec);
				}

				Event[] evtList = ecb.getServiceConnection3().getService().queryEvents(evtMgr, eventFilter);

				if (evtList != null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.info("getEvents , vtype='" + vtype + "', vid='" + vid + "', beginTime='" + beginTime + "', endTime='" + endTime + "', size='" + evtList.length
										+ "'");
					}
					for (Event evt : evtList) {
						convertEvent2Map(cache, resultEvtList, evt);
					}
				} else {
					LOGGER.error("getEvents evtList is null , vtype='" + vtype + "', vid='" + vid + "', beginTime='" + beginTime + "', endTime='" + endTime + "'");
				}

				resultMap.put(INFO_STATE, STATE_OK);
				resultMap.put(INFO_RESULT, resultEvtList);
			} else {
				recordResultMapError(resultMap, "类型为'" + vtype + "'的对象'" + vid + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getEvents error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 将Event对象转换为前台显示的对象
	 * 
	 * @param cache
	 * @param resultEvtList
	 * @param evt
	 * @throws Exception
	 */
	private static void convertEvent2Map(VIMCache cache, ArrayList<HashMap<String, String>> resultEvtList, Event evt) throws Exception {
		HashMap<String, String> evtMap = new HashMap<String, String>();
		resultEvtList.add(evtMap);

		// 描述
		evtMap.put(EVENT_DESC, evt.getFullFormattedMessage());
		// 类型,暂不获取,
		// 类型(严重性):信息和警告等
		// 好像需要根据时间的类型来确定严重性,如某种类型的Event就是警告等
		// if (evt instanceof AlarmEvent) {
		// } else if (evt instanceof TaskEvent) {
		// } else if (evt instanceof VmEvent) {
		// }
		evtMap.put(EVENT_CATEGORY, cache.getEventCategory(evt));
		// 时间
		evtMap.put(EVENT_TIME, Util.getDBDateTime(evt.getCreatedTime().getTimeInMillis()));
		// 任务,任务名称可能还需要翻译
		if (evt instanceof TaskEvent) {
			TaskEvent taskEvt = (TaskEvent) evt;
			evtMap.put(EVENT_TASK, cache.getTaskDesc(taskEvt.getInfo().getDescriptionId()));
		} else {
			evtMap.put(EVENT_TASK, "");
		}
		// 目标,当前设备事件,目标通过虚拟机-->主机-->资源池-->数据中心,从下开始取第一个
		if (evt.getVm() != null) {
			evtMap.put(EVENT_TARGET, evt.getVm().getName());
		} else if (evt.getHost() != null) {
			evtMap.put(EVENT_TARGET, evt.getHost().getName());
		} else if (evt.getComputeResource() != null) {
			evtMap.put(EVENT_TARGET, evt.getComputeResource().getName());
		} else if (evt.getDatacenter() != null) {
			evtMap.put(EVENT_TARGET, evt.getDatacenter().getName());
		} else {
			evtMap.put(EVENT_TARGET, "");
		}
		// 用户
		evtMap.put(EVENT_USER, Util.normalizeString(evt.getUserName()));
	}

	/**
	 * 获取对象的任务,<br>
	 * vtype对应的是对象的类型,定义在VIMConstants中的RESOURCE_XXX,常用的有虚拟机,物理机等<br>
	 * vid是vtype类型的对象的id<br>
	 * timeUnit为Calendar中定义的时间单位,如Calendar.WEEK_OF_YEAR或Calendar.DAY_OF_YEAR等<br>
	 * timeDiff为时间差和timeUnit组合为开始时间,如timeUnit为Calendar.WEEK_OF_YEAR加timeDiff为2,
	 * 则开始时间为当前时间到2周前的任务<br>
	 * <br>
	 * --参数去掉,如果timeDiff的值为0,则时间不起作用 <br>
	 * <br>
	 * --参数去掉,如果maxCount的值为0,则maxCount为默认值 <br>
	 * <br>
	 * 获取数据的数量目前无法控制只能获取TaskHistoryCollector的latestPage的任务数,也无法获取maxCount的具体值,
	 * 实际获取的数据大约10条左右<br>
	 * <br>
	 * 任务的输出结果是按照时间逆序,与事件的顺序正好相反
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vtype
	 * @param vid
	 * @return
	 */
	public static Map<String, Object> getTasks(String url, String username, String password, String vtype, String vid) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, String>> resultTaskList = new ArrayList<HashMap<String, String>>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getResource(vtype, vid);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				ManagedObjectReference taskMgr = ecb.getServiceConnection3().getServiceContent().getTaskManager();

				TaskFilterSpecByEntity entitySpec = new TaskFilterSpecByEntity();
				entitySpec.setEntity(mor);
				entitySpec.setRecursion(TaskFilterSpecRecursionOption.all);

				TaskFilterSpec taskFilter = new TaskFilterSpec();
				taskFilter.setEntity(entitySpec);

				// //出现错误
				// AxisFault
				// faultCode:
				// {http://schemas.xmlsoap.org/soap/envelope/}Server.userException
				// faultSubcode:
				// faultString: java.io.IOException: java.io.IOException:
				// java.io.IOException: Non nillable element 'timeType' is null.
				// faultActor:
				// faultNode:
				// faultDetail:
				// {http://xml.apache.org/axis/}stackTrace:java.io.IOException:
				// java.io.IOException: java.io.IOException: Non nillable
				// element 'timeType' is null.
				// at
				// org.apache.axis.encoding.ser.BeanSerializer.serialize(BeanSerializer.java:275)
				// at
				// org.apache.axis.encoding.SerializationContext.serializeActual(SerializationContext.java:1504)
				// at
				// org.apache.axis.encoding.SerializationContext.serialize(SerializationContext.java:980)
				// at
				// org.apache.axis.encoding.SerializationContext.serialize(SerializationContext.java:801)
				// at
				// org.apache.axis.message.RPCParam.serialize(RPCParam.java:208)
				// at
				// org.apache.axis.message.RPCElement.outputImpl(RPCElement.java:433)
				// at
				// org.apache.axis.message.MessageElement.output(MessageElement.java:1208)
				// at
				// org.apache.axis.message.SOAPBody.outputImpl(SOAPBody.java:139)
				// at
				// org.apache.axis.message.SOAPEnvelope.outputImpl(SOAPEnvelope.java:478)
				// at
				// org.apache.axis.message.MessageElement.output(MessageElement.java:1208)
				// at org.apache.axis.SOAPPart.writeTo(SOAPPart.java:315)
				// at org.apache.axis.SOAPPart.writeTo(SOAPPart.java:269)
				// at org.apache.axis.SOAPPart.saveChanges(SOAPPart.java:530)
				// at
				// org.apache.axis.SOAPPart.getContentLength(SOAPPart.java:229)
				// at org.apache.axis.Message.getContentLength(Message.java:510)
				// at
				// org.apache.axis.transport.http.HTTPSender.writeToSocket(HTTPSender.java:371)
				// at
				// org.apache.axis.transport.http.HTTPSender.invoke(HTTPSender.java:138)
				// at
				// org.apache.axis.strategies.InvocationStrategy.visit(InvocationStrategy.java:32)
				// at
				// org.apache.axis.SimpleChain.doVisiting(SimpleChain.java:118)
				// at org.apache.axis.SimpleChain.invoke(SimpleChain.java:83)
				// at
				// org.apache.axis.client.AxisClient.invoke(AxisClient.java:165)
				// at org.apache.axis.client.Call.invokeEngine(Call.java:2784)
				// at org.apache.axis.client.Call.invoke(Call.java:2767)
				// at org.apache.axis.client.Call.invoke(Call.java:2443)
				// at org.apache.axis.client.Call.invoke(Call.java:2366)
				// at org.apache.axis.client.Call.invoke(Call.java:1812)
				// at
				// com.vmware.vim25.VimBindingStub.createCollectorForTasks(VimBindingStub.java:54266)

				// 去掉设置TaskFilterSpecByTime就正常
				// if (timeDiff > 0) {
				// TaskFilterSpecByTime timeSpec = new TaskFilterSpecByTime();
				// Calendar beginTime = Calendar.getInstance();
				// beginTime.add(timeUnit, 0 - timeDiff);
				// Calendar endTime = Calendar.getInstance();
				// timeSpec.setBeginTime(beginTime);
				// timeSpec.setEndTime(endTime);
				// taskFilter.setTime(timeSpec);
				// }

				// CreateCollectorForTasks返回值为TaskHistoryCollector
				ManagedObjectReference taskHisColl = ecb.getServiceConnection3().getService().createCollectorForTasks(taskMgr, taskFilter);

				// // 调用SetCollectorPageSize设置maxCount好像不起作用
				// if (maxCount > 0) {
				// ecb.getServiceConnection3().getService()
				// .setCollectorPageSize(taskMor, maxCount);
				// }

				TaskInfo[] taskInfoList = (TaskInfo[]) VIMMgr.getDynamicProperty(ecb, taskHisColl, DYNAMICPROPERTY_LATESTPAGE);

				if (taskInfoList != null) {
					for (TaskInfo taskInfo : taskInfoList) {
						convertTaskInfo2Map(cache, resultTaskList, taskInfo);
					}
				}

				resultMap.put(INFO_STATE, STATE_OK);
				resultMap.put(INFO_RESULT, resultTaskList);
			} else {
				recordResultMapError(resultMap, "类型为'" + vtype + "'的对象'" + vid + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getTasks error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 将TaskInfo对象转换为前台显示的对象
	 * 
	 * @param cache
	 * @param resultTaskList
	 * @param taskInfo
	 * @throws Exception
	 */
	private static void convertTaskInfo2Map(VIMCache cache, ArrayList<HashMap<String, String>> resultTaskList, TaskInfo taskInfo) throws Exception {

		HashMap<String, String> taskMap = new HashMap<String, String>();
		resultTaskList.add(taskMap);

		// 名称,任务名称可能还需要翻译
		taskMap.put(TASK_NAME, cache.getTaskDesc(taskInfo.getDescriptionId()));
		// 目标
		taskMap.put(TASK_TARGET, taskInfo.getEntityName());
		// 状态
		taskMap.put(TASK_STATE, taskInfo.getState().getValue());
		// 错误信息
		if (taskInfo.getError() != null) {
			taskMap.put(TASK_ERROR, taskInfo.getError().getLocalizedMessage());
		} else {
			taskMap.put(TASK_ERROR, "");
		}
		// 启动者
		if (taskInfo.getReason() != null) {
			if (taskInfo.getReason() instanceof TaskReasonAlarm) {
				taskMap.put(TASK_USER, TASK_USER_ALARM + ((TaskReasonAlarm) taskInfo.getReason()).getAlarmName());
			}
			if (taskInfo.getReason() instanceof TaskReasonSchedule) {
				taskMap.put(TASK_USER, TASK_USER_SCHEDULE + ((TaskReasonSchedule) taskInfo.getReason()).getName());
			}
			if (taskInfo.getReason() instanceof TaskReasonSystem) {
				taskMap.put(TASK_USER, TASK_USER_SYSTEM);
			}
			if (taskInfo.getReason() instanceof TaskReasonUser) {
				taskMap.put(TASK_USER, ((TaskReasonUser) taskInfo.getReason()).getUserName());
			}
		} else {
			taskMap.put(TASK_USER, "");
		}
		// 请求开始时间
		taskMap.put(TASK_QUEUETIME, Util.getDBDateTime(taskInfo.getQueueTime().getTimeInMillis()));
		// 开始时间
		taskMap.put(TASK_STARTTIME, Util.getDBDateTime(taskInfo.getStartTime().getTimeInMillis()));
		// 完成时间
		taskMap.put(TASK_COMPLETETIME, Util.getDBDateTime(taskInfo.getCompleteTime().getTimeInMillis()));
	}

	/**
	 * 获取对象的告警,<br>
	 * vtype对应的是对象的类型,定义在VIMConstants中的RESOURCE_XXX,常用的有虚拟机,物理机等<br>
	 * vid是vtype类型的对象的id<br>
	 * <br>
	 * <br>
	 * VMWare中告警的管理模式是AlarmManager.getAlarm指定entity获取的告警只能是后面创建的告警,<br>
	 * vCenterServer安装完成后默认会创建一些告警,这些告警的entity都是数据中心(group-d1)对象。<br>
	 * <br>
	 * 获取指定entity告警应该通过AlarmManager.
	 * getAlarmState中获取所有的告警状态getOverallStatus为red和yellow的告警,<br>
	 * 再通过almState.getAlarm()获取到告警的名称。<br>
	 * <br>
	 * 告警的输出结果由于样本数据过少,应该和时间无关,应该是按照alm的key排序。后修改为按照时间逆序。<br>
	 * <br>
	 * 目前还无法和vCenterServer界面一样显示自身及子对象的告警,如itiimsDC数据中心可以显示下属的物理主机和虚拟机的告警<br>
	 * 只能显示自身的告警<br>
	 * <br>
	 * 接口无法获取虚拟机以外的对象的告警<br>
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vtype
	 * @param vid
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Map<String, Object> getAlarms0(String url, String username, String password, String vtype, String vid) {
		// 接口无法获取虚拟机以外的对象的告警,这个问题比较严重
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		ArrayList<ComparableHashMap<String, String>> resultAlmList = new ArrayList<ComparableHashMap<String, String>>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getResource(vtype, vid);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				ManagedObjectReference almMgr = ecb.getServiceConnection3().getServiceContent().getAlarmManager();

				AlarmState[] almStateList = ecb.getServiceConnection3().getService().getAlarmState(almMgr, mor);
				if (almStateList != null) {
					for (AlarmState almState : almStateList) {
						// red:实体一定有问题
						// yellow:实体可能有问题
						if ((almState.getOverallStatus().equals(ManagedEntityStatus.red)) || (almState.getOverallStatus().equals(ManagedEntityStatus.yellow))) {
							convertAlarmState2Map(cache, ecb, resultAlmList, almState);
							// 告警按时间排序
							Collections.sort(resultAlmList);
						}
					}
				}
				resultMap.put(INFO_STATE, STATE_OK);
				resultMap.put(INFO_RESULT, resultAlmList);
			} else {
				recordResultMapError(resultMap, "类型为'" + vtype + "'的对象'" + vid + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getAlarms error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 获取对象的告警,<br>
	 * <br>
	 * 告警的输出结果是按照时间啊逆序<br>
	 * <br>
	 * 通过界面"清除"的告警通过接口无法再获取到<br>
	 * <br>
	 * 其他的说明见上面的方法
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vtype
	 * @param vid
	 * @return
	 */
	public static Map<String, Object> getAlarms(String url, String username, String password, String vtype, String vid) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		ArrayList<ComparableHashMap<String, String>> resultAlmList = new ArrayList<ComparableHashMap<String, String>>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getResource(vtype, vid);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// 定义的告警
				// AlarmState[] declaredAlarmState = (AlarmState[])
				// getDynamicProperty(
				// ecb, mor, "declaredAlarmState");

				// 触发的告警
				AlarmState[] triggeredAlarmState = (AlarmState[]) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_TRIGGEREDALARMSTATE);

				if (triggeredAlarmState != null) {
					for (AlarmState almState : triggeredAlarmState) {
						// red:实体一定有问题
						// yellow:实体可能有问题
						if ((almState.getOverallStatus().equals(ManagedEntityStatus.red)) || (almState.getOverallStatus().equals(ManagedEntityStatus.yellow))) {
							convertAlarmState2Map(cache, ecb, resultAlmList, almState);
							// 告警按时间排序
							Collections.sort(resultAlmList);
						}
					}
				}

				resultMap.put(INFO_STATE, STATE_OK);
				resultMap.put(INFO_RESULT, resultAlmList);
			} else {
				recordResultMapError(resultMap, "类型为'" + vtype + "'的对象'" + vid + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getAlarms error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 将AlarmState对象转换为前台显示的对象
	 * 
	 * @param cache
	 * @param ecb
	 * @param resultAlmList
	 * @param alarmState
	 * @throws Exception
	 */
	private static void convertAlarmState2Map(VIMCache cache, ExtendedAppUtil ecb, ArrayList<ComparableHashMap<String, String>> resultAlmList, AlarmState almState)
			throws Exception {
		ComparableHashMap<String, String> almMap = new ComparableHashMap<String, String>(false, ALARM_TIME);
		resultAlmList.add(almMap);

		// 对象
		almMap.put(ALARM_ENTITY, getEntityName(ecb, almState.getEntity()));
		// 状态
		almMap.put(ALARM_STATUS, almState.getOverallStatus().getValue());
		// 名称
		AlarmInfo almInfo = cache.getAlarm(almState.getAlarm().get_value());
		almMap.put(ALARM_NAME, (almInfo != null ? almInfo.getName() : ""));
		// 触发时间
		almMap.put(ALARM_TIME, Util.getDBDateTime(almState.getTime().getTimeInMillis()));
		if (almState.getAcknowledged()) {
			almMap.put(ALARM_ACKNOWLEDGEDTIME, Util.getDBDateTime(almState.getAcknowledgedTime().getTimeInMillis()));
			almMap.put(ALARM_ACKNOWLEDGEDUSER, almState.getAcknowledgedByUser());
		} else {
			almMap.put(ALARM_ACKNOWLEDGEDTIME, "");
			almMap.put(ALARM_ACKNOWLEDGEDUSER, "");
		}
	}

	/**
	 * 确认告警,告警的确认者是username,目前的应用方式通过接口确认的告警的确认者将都是Administrator
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vtype
	 * @param vid
	 * @param almId
	 * @return
	 */
	public static Map<String, Object> acknowledgeAlarm(String url, String username, String password, String vtype, String vid, String almId) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getResource(vtype, vid);
			AlarmInfo alarmInfo = cache.getAlarm(almId);

			if ((mor != null) && (alarmInfo != null) && (alarmInfo.getAlarm() != null)) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				ManagedObjectReference almMgr = ecb.getServiceConnection3().getServiceContent().getAlarmManager();
				ecb.getServiceConnection3().getService().acknowledgeAlarm(almMgr, alarmInfo.getAlarm(), mor);

				resultMap.put(INFO_STATE, STATE_OK);
			} else {
				if (mor == null) {
					recordResultMapError(resultMap, "类型为'" + vtype + "'的对象'" + vid + "'不存在");
				}
				if ((alarmInfo == null) || (alarmInfo.getAlarm() == null)) {
					recordResultMapError(resultMap, "告警'" + almId + "'不存在");
				}
			}
		} catch (Exception e) {
			LOGGER.error("acknowledgeAlarm error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	// /**
	// * XXX 删除告警,不是"清除"告警<br>
	// * 按照现在的数据结构应该是删除AlarmState对象或者在ManagedEntity里面清除,但是目前没有找到相关的接口<br>
	// * 前台界面上的标签为"已触发的警报",这个和AlarmAction是否有关系
	// *
	// * @param url
	// * @param username
	// * @param password
	// * @param almId
	// * @return
	// */
	// public static Map<String, Object> removeAlarm(String url, String
	// username,
	// String password, String almId) {
	// HashMap<String, Object> resultMap = new HashMap<String, Object>();
	//
	// try {
	// VIMCache cache = VIMCache.getInstance(url, username, password);
	// AlarmInfo alarmInfo = cache.getAlarm(almId);
	//
	// if ((alarmInfo != null) && (alarmInfo.getAlarm() != null)) {
	// ExtendedAppUtil ecb = getECB(url, username, password);
	// // 该告警被删除,所有设备上与该告警关联的告警都不见了,和界面上的"清除"不是一个逻辑
	// ecb.getServiceConnection3().getService().removeAlarm(
	// alarmInfo.getAlarm());
	// resultMap.put(INFO_STATE, STATE_OK);
	// } else {
	// recordResultMapError(resultMap, "告警'" + almId + "'不存在");
	// }
	// } catch (Exception e) {
	// LOGGER.error("acknowledgeAlarm error, ", e);
	// recordResultMapException(resultMap, e);
	// }
	//
	// return resultMap;
	// }

	/**
	 * 获取实体的名称
	 * 
	 * @param ecb
	 * @param mor
	 * @return
	 */
	public static String getEntityName(ExtendedAppUtil ecb, ManagedObjectReference mor) {
		String entityName = "";

		try {
			if (mor != null) {
				entityName = mor.get_value();
				entityName = (String) getDynamicProperty(ecb, mor, DYNAMICPROPERTY_NAME);
			}
		} catch (Exception e) {
			LOGGER.error("getEntityName error, ", e);
		}
		return entityName;
	}

	/**
	 * 获取性能采集间隔列表
	 * 
	 * @param url
	 * @param username
	 * @param password
	 */
	public static Map<String, Object> getHistoricalInterval(String url, String username, String password) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			ExtendedAppUtil ecb = getECB(url, username, password);

			ManagedObjectReference perfMgr = ecb.getServiceConnection3().getServiceContent().getPerfManager();

			PerfInterval[] historicalInterval = (PerfInterval[]) getDynamicProperty(ecb, perfMgr, DYNAMICPROPERTY_HISTORICALINTERVAL);
			if (historicalInterval != null) {
				resultMap.put(INFO_STATE, STATE_OK);
				resultMap.put(INFO_RESULT, historicalInterval);
			} else {
				recordResultMapError(resultMap, "获取的性能间隔为空");
			}

		} catch (Exception e) {
			LOGGER.error("getHistoricalInterval error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 获取对象性能数据
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vtype
	 * @param vid
	 * @param perfIntervalKey
	 * @param perfCounterKeys
	 * @param endTime
	 * @return
	 */
	public static Map<String, Object> getPerformances(String url, String username, String password, String vtype, String vid, String perfIntervalKey, String[] perfCounterKeys,
			Calendar endTime) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		// System.out.println("--getPerformances()--------"+vtype+"---------------username:"+username+"----password:"+password+"-----url:"+url);
		try {
			LOGGER.info("getPerformances start, vtype='" + vtype + "', vid='" + vid + "', perfIntervalKey='" + perfIntervalKey + "', perfCounterKeys='"
					+ Arrays.asList(perfCounterKeys) + "', endTime='" + endTime + "'");

			VIMCache cache = VIMCache.getInstance(url, username, password);

			// System.out.println("---------cache-----------"+cache);
			// 实体对象
			// System.out.println(vtype+"---------// 实体对象------------"+vid);
			ManagedObjectReference mor = cache.getResource(vtype, vid);

			// 性能间隔
			PerfInterval perfInterval = cache.getPerfInterval(perfIntervalKey);

			// 性能计数器
			String nullPerfCounterKey = "";
			ArrayList<PerfCounterInfo> perfCounterInfoList = new ArrayList<PerfCounterInfo>();
			for (String perfCounterKey : perfCounterKeys) {
				PerfCounterInfo perfCounterInfo = cache.getPerfCounterInfo(perfCounterKey);
				if (perfCounterInfo == null) {
					nullPerfCounterKey = perfCounterKey;
					break;
				} else {
					perfCounterInfoList.add(perfCounterInfo);
				}
			}

			if ((mor != null) && (perfInterval != null) && (nullPerfCounterKey.equals(""))) {

				ExtendedAppUtil ecb = getECB(url, username, password);
				// System.out.println("-------连接VM--------"+ecb);

				ManagedObjectReference perfMgr = ecb.getServiceConnection3().getServiceContent().getPerfManager();

				// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.PerformanceManager.QuerySpec.html
				PerfQuerySpec querySpec = new PerfQuerySpec();
				// 实体对象
				querySpec.setEntity(mor);
				// 格式
				// cvs和normal
				querySpec.setFormat(PerfFormat._csv);
				// 间隔
				querySpec.setIntervalId(perfInterval.getSamplingPeriod());
				// 结束时间
				querySpec.setEndTime(endTime);
				// 要检索的指标

				PerfMetricId[] metricIds = new PerfMetricId[perfCounterInfoList.size()];
				for (int i = 0; i < perfCounterInfoList.size(); i++) {
					metricIds[i] = new PerfMetricId(null, null, perfCounterInfoList.get(i).getKey(), "");
				}
				querySpec.setMetricId(metricIds);

				// 查询指标
				PerfEntityMetricBase[] perfEntityMetricBases = ecb.getServiceConnection3().getService().queryPerf(perfMgr, new PerfQuerySpec[] { querySpec });

				resultMap.put(INFO_STATE, STATE_OK);
				resultMap.put(INFO_RESULT, perfEntityMetricBases);
			} else {
				if (mor == null) {
					recordResultMapError(resultMap, "类型为'" + vtype + "'的对象'" + vid + "'不存在");
				}
				if (perfInterval == null) {
					recordResultMapError(resultMap, "性能间隔'" + perfIntervalKey + "'不存在");
				}
				if (!nullPerfCounterKey.equals("")) {
					recordResultMapError(resultMap, "性能计数器'" + nullPerfCounterKey + "'不存在");
				}
			}
		} catch (Exception e) {
			LOGGER.error("getPerformances error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

}
