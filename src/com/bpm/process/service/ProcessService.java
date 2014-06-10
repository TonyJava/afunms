package com.bpm.process.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afunms.common.base.JspPage;
import com.afunms.common.util.DBManager;
import com.afunms.config.model.Knowledgebase;
import com.bpm.process.dao.ProcessDao;
import com.bpm.process.model.HistoryProcessInstanceModel;
import com.bpm.process.model.ProcessStatisticalsModel;
import com.bpm.system.utils.ConstanceUtil;
import com.bpm.system.utils.ProcessEnum;
import com.bpm.system.utils.StringUtil;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class ProcessService {

	@Resource
	private RepositoryService repositoryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private FormService formService;
	@Resource
	private HistoryService historyService;
	@Resource
	private ProcessDao processDao;
	@Resource
	private IdentityService identityService;

	/**
	 * 流程实例列表，表示已经启动的流程列表
	 * 
	 * @return
	 */

	public List<ProcessInstance> findPiList() {
		List<ProcessInstance> piList = runtimeService.createProcessInstanceQuery().list();
		return piList;
	}

	/**
	 * 还未签收的任务列表，等待签收
	 * 
	 * @param userId
	 * @return
	 */

	public JspPage findTaskList(String userId, int curpage, int perpage) {
		JspPage jspPage = null;
		int rowcount = taskService.createTaskQuery().taskCandidateUser(userId).list().size();
		jspPage = new JspPage(perpage, curpage, rowcount);
		// 分配给当前登陆用户的任务
		List list = taskService.createTaskQuery().taskCandidateUser(userId).orderByTaskCreateTime().desc().listPage((curpage - 1) * perpage, perpage);
		jspPage.setList(list);
		return jspPage;
	}

	public void claimTask(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}

	/**
	 * 已签收的任务列表
	 * 
	 * @param userId
	 * @return
	 */
	public JspPage findTaskClaimList(String userId, int curpage, int perpage) {
		JspPage jspPage = null;
		int rowcount = taskService.createTaskQuery().taskAssignee(userId).list().size();
		jspPage = new JspPage(perpage, curpage, rowcount);
		// 分配给当前登陆用户的任务
		List<Task> list = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().listPage((curpage - 1) * perpage, perpage);
		jspPage.setList(list);
		return jspPage;
	}

	/**
	 * 获取当前task的前一task的执行者
	 * 
	 * @param tasks
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> findBackUserTaskAssignee(List<Task> tasks) throws Exception {
		ActivityImpl activityImpl = null;
		Map<String, String> taskUsers = new HashMap<String, String>();
		for (Task task : tasks) {
			activityImpl = findBackUserTaskAssignee(task.getId());
			if (null != activityImpl) {
				taskUsers.put(task.getId(), historyService.createHistoricTaskInstanceQuery().taskDefinitionKey(activityImpl.getId()).processInstanceId(task.getProcessInstanceId())
						.singleResult().getAssignee());
			}
		}
		return taskUsers;
	}

	/**
	 * 图片流
	 * 
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public void getImageStream(String processDefinitionId, String processInstanceId, HttpServletResponse response) throws Exception {
		InputStream imageStream = null;
		response.setContentType("multipart/form-data");
		ServletOutputStream os = null;
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
		if (null == pi) {
			// 直接得到流程图片，任务节点未高亮
			imageStream = ProcessDiagramGenerator.generatePngDiagram(pde);
		} else {

			// 根据processDefinitionId，taskId直接得到图片，任务节点高亮
			String id = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult().getId();
			imageStream = ProcessDiagramGenerator.generateDiagram(pde, "png", runtimeService.getActiveActivityIds(id));
		}

		try {
			os = response.getOutputStream();
			byte[] bt = new byte[1024];
			int len;
			while ((len = imageStream.read(bt)) != -1) {
				os.write(bt, 0, len);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (imageStream != null)
					imageStream.close();
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据任务ID获取流程定义
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) throws Exception {
		TaskEntity taskEntity = findTaskById(taskId);
		String processDefinitionId = taskEntity.getProcessDefinitionId();
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
		if (processDefinition == null) {
			throw new Exception("流程定义未找到!");
		}
		return processDefinition;
	}

	/**
	 * 根据任务ID获取对应的流程实例
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	public ProcessInstance findProcessInstanceByTaskId(String taskId) throws Exception {
		// 找到流程实例
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(findTaskById(taskId).getProcessInstanceId()).singleResult();
		if (processInstance == null) {
			throw new Exception("流程实例未找到!");
		}
		return processInstance;
	}

	/**
	 * 根据taskId获取历史流程实例
	 * 
	 * @param taskId
	 * @return
	 */
	public HistoricProcessInstance findHistoriceProcessInstanceByTaskId(String taskId) throws Exception {
		HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(
				historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult().getProcessInstanceId()).singleResult();
		return instance;
	}

	/**
	 * 根据taskId获得任务实例
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	public TaskEntity findTaskById(String taskId) throws Exception {
		TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new Exception("任务实例未找到!");
		}
		return task;
	}

	/**
	 * 根据taskId获得表单
	 * 
	 * @param taskId
	 * @return
	 */
	public List<FormProperty> findFpList(String taskId) {
		TaskFormData tfd = formService.getTaskFormData(taskId);
		return tfd.getFormProperties();
	}

	/**
	 * 根据taskExtId，查询提交的内容
	 * 
	 * @param taskExtId
	 * @return
	 */
	public Map findFormMap(String taskExtId) {
		return runtimeService.getVariables(taskExtId);
	}

	/**
	 * 根据当前任务ID，查询可以驳回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 */
	public List<ActivityImpl> findBackAIList(String taskId, String processDefinitionId, String executionId) throws Exception {
		ActivityImpl activityImpl = findActivityImplByExecutionId(processDefinitionId, executionId);
		List<ActivityImpl> rtnList = iteratorBackActivity(taskId, activityImpl, new ArrayList<ActivityImpl>(), new ArrayList<ActivityImpl>());
		return reverList(rtnList);
	}

	/**
	 * 根据当前任务ID，查询可以驳回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 */
	public List<ActivityImpl> findBackAIList(String taskId) throws Exception {

		ActivityImpl activityImpl = findActivitiImpl(taskId, null);
		List<ActivityImpl> rtnList = iteratorBackActivity(taskId, activityImpl, new ArrayList<ActivityImpl>(), new ArrayList<ActivityImpl>());
		return reverList(rtnList);
	}

	/**
	 * 根据当前任务ID，查找上一任务节点
	 * 
	 * @param taskId
	 * @return
	 */
	public ActivityImpl findBackUserTaskAssignee(String taskId) throws Exception {
		ActivityImpl activityImpl = findActivitiImpl(taskId, null);
		return BackActivity(taskId, activityImpl, new ArrayList<ActivityImpl>(), new ArrayList<ActivityImpl>());
	}

	/**
	 * 根据任务ID和节点ID获取活动节点 <br>
	 * 
	 * @param taskId
	 *            任务ID
	 * @param activityId
	 *            活动节点ID <br>
	 *            如果为null或""，则默认查询当前活动节点 <br>
	 *            如果为"end"，则查询结束节点 <br>
	 * 
	 * @return
	 * @throws Exception
	 */
	public ActivityImpl findActivitiImpl(String taskId, String activityId) throws Exception {
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

		// 获取当前活动节点ID
		if (StringUtil.isBlank(activityId)) {
			activityId = findTaskById(taskId).getTaskDefinitionKey();
		}

		// 根据流程定义，获取该流程实例的结束节点
		if (activityId.toUpperCase().equals("END")) {
			for (ActivityImpl activityImpl : processDefinition.getActivities()) {
				List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
				if (pvmTransitionList.isEmpty()) {
					return activityImpl;
				}
			}
		}

		// 根据节点ID，获取对应的活动节点
		ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition).findActivity(activityId);

		return activityImpl;
	}

	/**
	 * 迭代循环流程树结构，查询当前节点可驳回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param currActivity
	 *            当前活动节点
	 * @param rtnList
	 *            存储回退节点集合
	 * @param tempList
	 *            临时存储节点集合（存储一次迭代过程中的同级userTask节点）
	 * @return 回退节点集合
	 */
	public List<ActivityImpl> iteratorBackActivity(String taskId, ActivityImpl currActivity, List<ActivityImpl> rtnList, List<ActivityImpl> tempList) throws Exception {
		// 查询流程定义，生成流程树结构
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

		// 当前节点的流入来源
		List<PvmTransition> incomingTransitions = currActivity.getIncomingTransitions();

		// 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
		List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();

		// 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
		List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();

		// 遍历当前节点所有流入路径
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			ActivityImpl activityImpl = transitionImpl.getSource();
			String type = (String) activityImpl.getProperty("type");
			/**
			 * 并行节点配置要求：<br>
			 * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
			 */
			if (ConstanceUtil.PG.equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
				if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
					return rtnList;
				} else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
					parallelGateways.add(activityImpl);
				}
			} else if (ConstanceUtil.START.equals(type)) {// 开始节点，停止递归
				return rtnList;
			} else if (ConstanceUtil.UT.equals(type)) {// 用户任务
				tempList.add(activityImpl);
			} else if (ConstanceUtil.EG.equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
				currActivity = transitionImpl.getSource();
				exclusiveGateways.add(currActivity);
			}
		}

		// 只往回找一个节点：
		// 找到上一个userTask后就不需要递归了
		if (tempList.size() == 0) {
			/**
			 * 迭代条件分支集合，查询对应的userTask节点
			 */
			for (ActivityImpl activityImpl : exclusiveGateways) {
				iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
			}

			/**
			 * 迭代并行集合，查询对应的userTask节点
			 */
			for (ActivityImpl activityImpl : parallelGateways) {
				iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
			}
		}

		/**
		 * 根据同级userTask集合，过滤最近发生的节点
		 */
		currActivity = filterNewestActivity(processInstance, tempList);
		if (currActivity != null) {
			// 查询当前节点的流向是否为并行终点，并获取并行起点ID
			String id = findParallelGatewayId(currActivity);
			if (StringUtil.isBlank(id)) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
				rtnList.add(currActivity);
			} else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
				currActivity = findActivitiImpl(taskId, id);
			}

			// 清空本次迭代临时集合
			tempList.clear();
		}
		return rtnList;
	}

	/**
	 * 迭代循环流程树结构，查询当前节点可驳回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param currActivity
	 *            当前活动节点
	 * @param rtnList
	 *            存储回退节点集合
	 * @param tempList
	 *            临时存储节点集合（存储一次迭代过程中的同级userTask节点）
	 * @return 上一节点
	 */
	public ActivityImpl BackActivity(String taskId, ActivityImpl currActivity, List<ActivityImpl> rtnList, List<ActivityImpl> tempList) throws Exception {
		// 查询流程定义，生成流程树结构
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

		// 当前节点的流入来源
		List<PvmTransition> incomingTransitions = currActivity.getIncomingTransitions();

		// 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
		List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();

		// 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
		List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();

		// 遍历当前节点所有流入路径
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			ActivityImpl activityImpl = transitionImpl.getSource();
			String type = (String) activityImpl.getProperty("type");
			/**
			 * 并行节点配置要求：<br>
			 * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
			 */
			if (ConstanceUtil.PG.equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
				if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
					return null;
				} else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
					parallelGateways.add(activityImpl);
				}
			} else if (ConstanceUtil.START.equals(type)) {// 开始节点，停止递归
				return null;
			} else if (ConstanceUtil.UT.equals(type)) {// 用户任务
				tempList.add(activityImpl);
			} else if (ConstanceUtil.EG.equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
				currActivity = transitionImpl.getSource();
				exclusiveGateways.add(currActivity);
			}
		}

		/**
		 * 迭代条件分支集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : exclusiveGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}

		/**
		 * 迭代并行集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : parallelGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}

		/**
		 * 根据同级userTask集合，过滤最近发生的节点
		 */
		currActivity = filterNewestActivity(processInstance, tempList);
		if (currActivity != null) {
			// 查询当前节点的流向是否为并行终点，并获取并行起点ID
			String id = findParallelGatewayId(currActivity);
			if (StringUtil.isBlank(id)) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
				if (currActivity.getProperty("type").equals("userTask")) {
					rtnList.add(currActivity);
					return currActivity;
				}
			} else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
				currActivity = findActivitiImpl(taskId, id);
			}

			// 清空本次迭代临时集合
			tempList.clear();
			// 执行下次迭代
			iteratorBackActivity(taskId, currActivity, rtnList, tempList);
		}
		return null;
	}

	/**
	 * 根据流入任务集合，查询最近一次的流入任务节点
	 * 
	 * @param processInstance
	 *            流程实例
	 * @param tempList
	 *            流入任务集合
	 * @return
	 */
	public ActivityImpl filterNewestActivity(ProcessInstance processInstance, List<ActivityImpl> tempList) {
		while (tempList.size() > 0) {
			ActivityImpl activity_1 = tempList.get(0);
			HistoricActivityInstance activityInstance_1 = findHistoricUserTask(processInstance, activity_1.getId());
			if (activityInstance_1 == null) {
				tempList.remove(activity_1);
				continue;
			}

			if (tempList.size() > 1) {
				ActivityImpl activity_2 = tempList.get(1);
				HistoricActivityInstance activityInstance_2 = findHistoricUserTask(processInstance, activity_2.getId());
				if (activityInstance_2 == null) {
					tempList.remove(activity_2);
					continue;
				}

				if (activityInstance_1.getEndTime().before(activityInstance_2.getEndTime())) {
					tempList.remove(activity_1);
				} else {
					tempList.remove(activity_2);
				}
			} else {
				break;
			}
		}
		if (tempList.size() > 0) {
			return tempList.get(0);
		}
		return null;
	}

	/**
	 * 查询指定任务节点的最新记录
	 * 
	 * @param processInstance
	 *            流程实例
	 * @param activityId
	 * @return
	 */
	public HistoricActivityInstance findHistoricUserTask(ProcessInstance processInstance, String activityId) {
		HistoricActivityInstance rtnVal = null;
		// 查询当前流程实例审批结束的历史节点
		List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().activityType("userTask").processInstanceId(
				processInstance.getId()).activityId(activityId).finished().orderByHistoricActivityInstanceEndTime().desc().list();
		if (historicActivityInstances.size() > 0) {
			rtnVal = historicActivityInstances.get(0);
		}

		return rtnVal;
	}

	/**
	 * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
	 * 
	 * @param activityImpl
	 *            当前节点
	 * @return
	 */
	public String findParallelGatewayId(ActivityImpl activityImpl) {
		List<PvmTransition> incomingTransitions = activityImpl.getOutgoingTransitions();
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			activityImpl = transitionImpl.getDestination();
			String type = (String) activityImpl.getProperty("type");
			if ("parallelGateway".equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
				if ("END".equals(gatewayType.toUpperCase())) {
					return gatewayId.substring(0, gatewayId.lastIndexOf("_")) + "_start";
				}
			}
		}
		return null;
	}

	/**
	 * 反向排序list集合，便于驳回节点按顺序显示
	 * 
	 * @param list
	 * @return
	 */
	public List<ActivityImpl> reverList(List<ActivityImpl> list) {
		List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
		// 由于迭代出现重复数据，排除重复
		for (int i = list.size(); i > 0; i--) {
			if (!rtnList.contains(list.get(i - 1)))
				rtnList.add(list.get(i - 1));
		}
		return rtnList;
	}

	/**
	 * 完成任务
	 * 
	 * @param taskId
	 * @param map
	 */
	public void taskComplete(String taskId, Map map) {
		taskService.complete(taskId, map);
	}

	/**
	 * 驳回
	 * 
	 * @param taskId
	 * @param activityId
	 * @throws Exception
	 */
	public void backProcess(String taskId, String activityId, String reject) throws Exception {
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
		String piId = processInstance.getId();
		Map map = runtimeService.getVariables(piId);
		map.put("reject", reject);
		backProcess(taskId, activityId, map);
		processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_RUN, processInstance.getProcessInstanceId());
	}

	/**
	 * 驳回流程
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param activityId
	 *            驳回节点ID
	 * @param variables
	 *            流程存储参数
	 * @throws Exception
	 */
	public void backProcess(String taskId, String activityId, Map<String, Object> variables) throws Exception {
		if (StringUtil.isBlank(activityId)) {
			throw new Exception("驳回目标节点ID为空！");
		}

		// 查找所有并行任务节点，同时驳回
		List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
		for (Task task : taskList) {
			commitProcess(task.getId(), variables, activityId);
		}
	}

	/**
	 * 根据流程实例ID和任务key值查询所有同级任务集合
	 * 
	 * @param processInstanceId
	 * @param key
	 * @return
	 */
	public List<Task> findTaskListByKey(String processInstanceId, String key) {
		return taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(key).list();
	}

	/**
	 * @param taskId
	 *            当前任务ID
	 * @param variables
	 *            流程变量
	 * @param activityId
	 *            流程转向执行任务节点ID<br>
	 *            此参数为空，默认为提交操作
	 * @throws Exception
	 */
	public void commitProcess(String taskId, Map<String, Object> variables, String activityId) throws Exception {
		if (variables == null) {
			variables = new HashMap<String, Object>();
		}
		// 跳转节点为空，默认提交操作
		if (StringUtil.isBlank(activityId)) {
			taskService.complete(taskId, variables);
		} else {// 流程转向操作
			turnTransition(taskId, activityId, variables);
		}
	}

	/**
	 * 流程转向操作
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param activityId
	 *            目标节点任务ID
	 * @param variables
	 *            流程变量
	 * @throws Exception
	 */
	public void turnTransition(String taskId, String activityId, Map<String, Object> variables) throws Exception {
		// 当前节点
		ActivityImpl currActivity = findActivitiImpl(taskId, null);
		// 清空当前流向
		List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

		// 创建新流向
		TransitionImpl newTransition = currActivity.createOutgoingTransition();
		// 目标节点
		ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
		// 设置新流向的目标节点
		newTransition.setDestination(pointActivity);
		// 执行转向任务
		taskService.complete(taskId, variables);
		// 删除目标节点新流入
		pointActivity.getIncomingTransitions().remove(newTransition);

		// 还原以前流向
		restoreTransition(currActivity, oriPvmTransitionList);
	}

	/**
	 * 清空指定活动节点流向
	 * 
	 * @param activityImpl
	 *            活动节点
	 * @return 节点流向集合
	 */
	public List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
		// 存储当前节点所有流向临时变量
		List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
		// 获取当前节点所有流向，存储到临时变量，然后清空
		List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
		for (PvmTransition pvmTransition : pvmTransitionList) {
			oriPvmTransitionList.add(pvmTransition);
		}
		pvmTransitionList.clear();

		return oriPvmTransitionList;
	}

	/**
	 * 还原指定活动节点流向
	 * 
	 * @param activityImpl
	 *            活动节点
	 * @param oriPvmTransitionList
	 *            原有节点流向集合
	 */
	public static void restoreTransition(ActivityImpl activityImpl, List<PvmTransition> oriPvmTransitionList) {
		// 清空现有流向
		List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
		pvmTransitionList.clear();
		// 还原以前流向
		for (PvmTransition pvmTransition : oriPvmTransitionList) {
			pvmTransitionList.add(pvmTransition);
		}
	}

	public JspPage findhistoricProcessInstanceList(int curpage, int perpage) {
		JspPage jspPage = null;
		int rowcount = historyService.createHistoricProcessInstanceQuery().finished().list().size();
		jspPage = new JspPage(perpage, curpage, rowcount);
		List list = historyService.createHistoricProcessInstanceQuery().finished().orderByProcessInstanceEndTime().desc().listPage((curpage - 1) * perpage, perpage);
		jspPage.setList(list);
		return jspPage;
	}

	/**
	 * 根据taskId读取task的外部表单
	 * 
	 * @param taskId
	 * @return
	 */
	public String findFormKeyContent(String taskId) {
		// 根据任务ID读取外部表单
		String renderedTaskForm = (String) formService.getRenderedTaskForm(taskId);
		return renderedTaskForm;
	}

	/**
	 * 完成任务
	 * 
	 * @param taskId
	 * @param map
	 */
	public void taskFormKeyComplete(String taskId, Map map) throws Exception {
		formService.submitTaskFormData(taskId, map);
		HistoricProcessInstance instance = findHistoriceProcessInstanceByTaskId(taskId);
		if (null == instance.getEndTime()) {
			processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_RUN, instance.getId());
		} else {
			processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_END, instance.getId());
		}
	}

	/**
	 * 完成任务
	 * 
	 * @param taskId
	 * @param executionId
	 * @param map
	 */
	public void taskFormKeyComplete(String taskId, String executionId) throws Exception {

		Map map = runtimeService.getVariables(executionId);
		formService.submitTaskFormData(taskId, map);
		HistoricProcessInstance instance = findHistoriceProcessInstanceByTaskId(taskId);
		if (null == instance.getEndTime()) {
			processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_RUN, instance.getId());
		} else {
			processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_END, instance.getId());
		}

	}

	/**
	 * 完成任务 包含流程历史表单变量和当前表单变量
	 * 
	 * @param taskId
	 * @param executionId
	 * @param data
	 *            新增的数据内容
	 * @throws Exception
	 */
	public void taskFormKeyComplete(String taskId, String executionId, Map<String, Object> data) throws Exception {

		Map map = runtimeService.getVariables(executionId);
		map.putAll(data);
		formService.submitTaskFormData(taskId, map);
		HistoricProcessInstance instance = findHistoriceProcessInstanceByTaskId(taskId);
		if (null == instance.getEndTime()) {
			processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_RUN, instance.getId());
		} else {
			processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_END, instance.getId());
		}

	}

	/**
	 * 完成任务
	 * 
	 * @param taskId
	 * @param executionId
	 * @param map
	 */
	public void taskFormKeyComplete(String taskId, String executionId, String banjie) {
		Map map = runtimeService.getVariables(executionId);
		map.put("banjie", banjie);
		formService.submitTaskFormData(taskId, map);
	}

	/**
	 * 根据processDefinitionId，executionId，查找ActivityImpl
	 * 
	 * @param processDefinitionId
	 * @param executionId
	 * @return
	 * @throws Exception
	 */
	public ActivityImpl findActivityImplByExecutionId(String processDefinitionId, String executionId) throws Exception {
		ProcessDefinitionEntity processDefinitionEntity = findProcessDefinitionEntity(processDefinitionId);
		ExecutionEntity execution = findExecutionEntity(executionId);
		// 当前实例的执行到哪个节点
		String activitiId = execution.getActivityId();
		// 获得当前任务的所有节点
		List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
		ActivityImpl actImpl = null;
		for (ActivityImpl activityImpl : activitiList) {
			String id = activityImpl.getId();
			if (id.equals(activitiId)) {
				actImpl = activityImpl;
				break;
			}
		}
		return actImpl;
	}

	/**
	 * 根据processDefinitionId查找ProcessDefinitionEntity
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public ProcessDefinitionEntity findProcessDefinitionEntity(String processDefinitionId) {
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
		return processDefinitionEntity;
	}

	/**
	 * 根据executionId查找ExecutionEntity
	 * 
	 * @param executionId
	 * @return
	 */
	public ExecutionEntity findExecutionEntity(String executionId) {
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(executionId).singleResult();
		return execution;
	}

	/**
	 * 
	 * Description:查询流程实例的历史任务节点 Date:2012-11-27
	 * 
	 * @author hexinlin
	 * @return List<HistoricTaskInstance>
	 */
	public List<HistoricTaskInstance> findHistoricTaskInstance(String processInstanceId) {
		List<HistoricTaskInstance> histasklist = new ArrayList<HistoricTaskInstance>();
		histasklist = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
		return histasklist;
	}

	/**
	 * 
	 * Description:获取用户已办订单 Date:2012-11-27
	 * 
	 * @author hexinlin
	 * @return List<HistoricProcessInstance>
	 */
	public JspPage findHistoricProcessInstance(String userId, int curpage, int perpage) {
		JspPage jspPage = null;
		List<HistoricTaskInstance> histasklist = new ArrayList<HistoricTaskInstance>();
		histasklist = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished().orderByProcessInstanceId().desc().list();
		String temp = "";
		Set<String> sets = new HashSet<String>();
		for (HistoricTaskInstance task : histasklist) {
			temp = task.getProcessInstanceId();
			if (sets.contains(temp)) {
				continue;
			}
			sets.add(temp);
		}
		if (sets.size() > 0) {

			int rowcount = historyService.createHistoricProcessInstanceQuery().processInstanceIds(sets).list().size();
			jspPage = new JspPage(perpage, curpage, rowcount);
			List list = historyService.createHistoricProcessInstanceQuery().processInstanceIds(sets).orderByProcessInstanceId().desc().listPage((curpage - 1) * perpage, perpage);
			jspPage.setList(list);
		}
		return jspPage;
	}

	/**
	 * 根据活动实例ID，查找活动节点
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public ActivityImpl findActivityImplByprocessInstanceId(String processInstanceId) {
		String processDefinitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();

		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);

		ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

		if (execution == null)
			return null;
		String activitiId = execution.getActivityId();
		List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
		String id = null;
		for (ActivityImpl activityImpl : activitiList) {
			id = activityImpl.getId();
			if (activitiId.equals(id)) {
				System.out.println(activityImpl.getProperty("name"));
				if ("endEvent".equals(activityImpl.getProperty("type")))
					return null;
				return activityImpl;
			}
		}
		return null;
	}

	/**
	 * 查找当前任务定义节点
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public TaskDefinition findCurrentTaskDefinition(String processInstanceId) {
		ActivityImpl activityImpl = findActivityImplByprocessInstanceId(processInstanceId);
		return currentTaskDefinition(activityImpl);
	}

	private TaskDefinition currentTaskDefinition(ActivityImpl activityImpl) {
		if ("userTask".equals(activityImpl.getProperty("type"))) {
			TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
			return taskDefinition;
		} else {
			return null;
		}
	}

	/**
	 * 获取任务所在流程实例的启动事件
	 * 
	 * @param taskId
	 * @return
	 */
	public String getProcessInstanceStartTime(String taskId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(historyService.createHistoricProcessInstanceQuery().processInstanceId(taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId())
				.singleResult().getStartTime());
	}

	/**
	 * 查询流程开始人
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public String findStartUserIdByProcessInstanceId(String processInstanceId) {
		return processDao.findStartUserIdByProcessInstanceId(processInstanceId);
	}

	/**
	 * 根据当前任务ID，查询后续流程发起人节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 */
	public ActivityImpl findAfterActivityImpl(String taskId) throws Exception {

		ActivityImpl activityImpl = findActivitiImpl(taskId, null);
		ActivityImpl afterActivityImpl = findAfterActivity(taskId, activityImpl, new ArrayList<ActivityImpl>());
		return afterActivityImpl;
	}

	/**
	 * 迭代循环流程树结构，查询后续流程发起人节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param currActivity
	 *            当前活动节点
	 * @param tempList
	 *            临时存储节点集合（存储后续userTask节点）
	 * @return
	 */
	public ActivityImpl findAfterActivity(String taskId, ActivityImpl currActivity, List<ActivityImpl> tempList) throws Exception {

		// 当前节点的流出方向
		List<PvmTransition> outcomingTransitions = currActivity.getOutgoingTransitions();

		// 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
		List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();

		// 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
		List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();

		// 遍历当前节点所有流出路径
		for (PvmTransition pvmTransition : outcomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			ActivityImpl activityImpl = transitionImpl.getDestination();
			String type = (String) activityImpl.getProperty("type");
			/**
			 * 并行节点配置要求：<br>
			 * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
			 */
			if (ConstanceUtil.PG.equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
				if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
					return null;
				} else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
					parallelGateways.add(activityImpl);
				}
			} else if (ConstanceUtil.START.equals(type)) {// 开始节点，停止递归
				return null;
			} else if (ConstanceUtil.UT.equals(type)) {// 用户任务
				tempList.add(activityImpl);
				findAfterActivity(taskId, activityImpl, tempList);

			} else if (ConstanceUtil.EG.equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
				currActivity = transitionImpl.getDestination();
				exclusiveGateways.add(currActivity);
			}
		}

		/**
		 * 迭代条件分支集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : exclusiveGateways) {
			findAfterActivity(taskId, activityImpl, tempList);
		}

		/**
		 * 迭代并行集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : parallelGateways) {
			findAfterActivity(taskId, activityImpl, tempList);
		}

		for (ActivityImpl ai : tempList) {
			String name = (String) ai.getProperty("name");
			if (name.equals(ConstanceUtil.BEFORE_END))
				return ai;
		}

		return null;
	}

	/**
	 * 半截
	 * 
	 * @param taskId
	 * @param activityId
	 * @param variables
	 * @param flag
	 * @throws Exception
	 */
	public void afterProcess(String taskId, String activityId, Map<String, Object> variables, String flag) throws Exception {
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
		Map map = new HashMap();
		if (ConstanceUtil.FORM_READ_WRITE.equals(flag)) { // 当前环节 读和写
			map = runtimeService.getVariables(processInstance.getId());
			map.putAll(variables);
		} else if (ConstanceUtil.FORM_WRITE.equals(flag)) {// 当前环节 仅写
			map = variables;
		} else if (ConstanceUtil.FORM_READ.equals(flag)) {// 当前环节 仅读
			map = runtimeService.getVariables(processInstance.getId());
		}
		afterProcess(taskId, activityId, map);
		processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_HALF, processInstance.getProcessInstanceId());// 设定状态为半截
	}

	public void afterProcess(String taskId, String activityId, Map<String, Object> variables) throws Exception {
		if (StringUtil.isBlank(activityId)) {
			throw new Exception("目标节点ID为空！");
		}

		List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
		for (Task task : taskList) {
			commitAfterProcess(task.getId(), variables, activityId);
		}
	}

	public void commitAfterProcess(String taskId, Map<String, Object> variables, String activityId) throws Exception {
		if (variables == null) {
			variables = new HashMap<String, Object>();
		}
		// 跳转节点为空，默认提交操作
		if (StringUtil.isBlank(activityId)) {
			taskService.complete(taskId, variables);
		} else {// 流程转向操作
			afterTransition(taskId, activityId, variables);
		}
	}

	public void afterTransition(String taskId, String activityId, Map<String, Object> variables) throws Exception {
		// 当前节点
		ActivityImpl currActivity = findActivitiImpl(taskId, null);
		// 清空当前流向
		List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

		// 创建新流向
		TransitionImpl newTransition = currActivity.createOutgoingTransition();
		// 目标节点
		ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
		// 设置新流向的目标节点
		newTransition.setDestination(pointActivity);

		// 执行转向任务
		taskService.complete(taskId, variables);
		// 删除目标节点新流入
		pointActivity.getIncomingTransitions().remove(newTransition);

		// 还原以前流向
		restoreTransition(currActivity, oriPvmTransitionList);
	}

	/**
	 * 根据流程信息启动流程
	 * 
	 * @param processId
	 * @return
	 */
	public String startProcessAuto(String processId, Map<String, Object> map) {
		// 设置启动流程的用户
		Map data = map;
		String message = "";// 存储反馈信息。
		boolean flag = true;// 为true时表示需要签收，为false的时候
		String userId = (String) data.get("userId");
		identityService.setAuthenticatedUserId(userId);
		data.put("owner", userId);
		data.remove("userId");
		ProcessInstance instance = runtimeService.startProcessInstanceById(processId, data);// 启动流程
		if (instance.isEnded()) {
			processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_END, instance.getProcessInstanceId());
		} else {
			processDao.changeProcessInstanceStatus(ConstanceUtil.PRO_START, instance.getProcessInstanceId());
		}
		Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).taskCandidateUser(userId).singleResult();// 获取需要签收的任务。
		if (null == task) {
			task = taskService.createTaskQuery().processInstanceBusinessKey(instance.getProcessInstanceId()).taskAssignee(userId).singleResult();// 获取需要处理的任务。
			if (null == task) {
				message = "noauth";
				return message;
			} else {
				flag = false;
			}
		}
		if (flag) {
			taskService.claim(task.getId(), userId);
		}
		return task.getId();
	}

	/**
	 * 获取流程定义列表
	 * 
	 * @return
	 */
	public List<ProcessDefinition> findPdList() {
		List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
		return pdList;
	}

	/**
	 * 获取流程变量
	 * 
	 * @param taskId
	 * @return
	 */
	public Map<String, Object> getProcessVariables(String taskId) {
		ProcessInstance processInstance = null;
		try {
			processInstance = findProcessInstanceByTaskId(taskId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String piId = processInstance.getId();
		Map map = new HashMap();
		map = runtimeService.getVariables(piId);
		return map;
	}

	/**
	 * 根据条件获取流程实例
	 * 
	 * @param status
	 * @param startuser
	 * @param starttime1
	 * @param starttime2
	 * @return
	 */
	public JspPage queryHistoricProcessInstance(String status, String startuser, String starttime1, String starttime2, int curpage, int perpage) {

		String sql = "select hp.status,hp.START_TIME_ as startTime, hp.START_USER_ID_ as startUser,hp.PROC_DEF_ID_ as processDefinitionId,hp.PROC_INST_ID_ as processInstanceId from act_hi_procinst hp where 1=1 ";
		String countsql = "select count(*) from act_hi_procinst hp where 1=1 ";
		if (StringUtil.isNotBlank(status)) {
			sql = sql + "and hp.status='" + status + "' ";
			countsql = countsql + "and hp.status='" + status + "' ";
		}
		if (StringUtil.isNotBlank(startuser)) {
			sql = sql + "and hp.START_USER_ID_ like '%" + startuser + "%' ";
			countsql = countsql + "and hp.START_USER_ID_ like '%" + startuser + "%' ";
		}
		if (StringUtil.isNotBlank(starttime1)) {
			sql = sql + "and hp.START_TIME_ >= '" + starttime1 + "' ";
			countsql = countsql + "and hp.START_TIME_ >= '" + starttime1 + "' ";
		}
		if (StringUtil.isNotBlank(starttime2)) {
			sql = sql + "and hp.START_TIME_ <= '" + starttime2 + "' ";
			countsql = countsql + "and hp.START_TIME_ <= '" + starttime2 + "' ";
		}
		sql = sql + "order by startTime desc ";
		sql = sql + "limit " + (curpage - 1) + "," + perpage;
		List<HistoryProcessInstanceModel> list = new ArrayList<HistoryProcessInstanceModel>();
		DBManager dbm = new DBManager();
		JspPage jspPage = null;
		ResultSet rs = null;
		int rowcount = 0;
		try {
			rs = dbm.executeQuery(countsql);
			if (rs.next()) {
				rowcount = rs.getInt(1);
			}
			jspPage = new JspPage(perpage, curpage, rowcount);
			rs = dbm.executeQuery(sql);
			ResultSetHandler<List<HistoryProcessInstanceModel>> rsh = new BeanListHandler<HistoryProcessInstanceModel>(HistoryProcessInstanceModel.class);
			list = rsh.handle(rs);
			jspPage.setList(list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbm.close();
		}
		return jspPage;
	}

	/**
	 * 存储解决方案。
	 * 
	 * @param nodeId
	 * @param orderSolution
	 * @param alarmType
	 * @param userId
	 */
	public void stroeOrderSolution(String ordernodetype, String orderSolution, String alarmType, String orderType, String userId) {

		processDao.storeOrderSolution(ordernodetype, orderSolution, alarmType, orderType, userId);

	}

	/**
	 * 根据节点ID获取操作系统类型
	 * 
	 * @param id
	 * @return
	 */
	public String getNodeOstypeById(String id) {
		String ostype = "";
		if (!"0".equals(id) && StringUtil.isNotBlank(id)) {
			ostype = processDao.getOstypeById(id);
		}
		return ostype;
	}

	/**
	 * 拼接sql 便于查询知识库
	 * 
	 * @param category
	 * @param entity
	 * @param subentity
	 * @param wordkey
	 * @param curpage
	 * @param perpage
	 * @return
	 */
	public JspPage queryKnowledge(String category, String entity, String subentity, String wordkey, int curpage, int perpage) {
		String querySql = "select * from system_knowledgebase sk where 1=1 ";
		String countSql = "select count(*) from system_knowledgebase sk where 1=1 ";
		if (!"全部".equals(category) && StringUtil.isNotBlank((category))) {
			querySql = querySql + "and sk.category = '" + category + "' ";
			countSql = countSql + "and sk.category = '" + category + "' ";
		}
		if (!"全部".equals(entity) && StringUtil.isNotBlank((entity))) {
			querySql = querySql + "and sk.entity = '" + entity + "' ";
			countSql = countSql + "and sk.entity = '" + entity + "' ";
		}
		if (!"全部".equals(subentity) && StringUtil.isNotBlank((subentity))) {
			querySql = querySql + "and sk.subentity = '" + subentity + "' ";
			countSql = countSql + "and sk.subentity = '" + subentity + "' ";
		}
		if (StringUtil.isNotBlank(wordkey) && StringUtil.isNotBlank((wordkey))) {
			querySql = querySql + "and sk.contents like '%" + wordkey + "%' or sk.attachfiles like '%" + wordkey + "%' or sk.titles like '%" + wordkey + "%' ";
			countSql = countSql + "and sk.contents like '%" + wordkey + "%' or sk.attachfiles like '%" + wordkey + "%' or sk.titles like '%" + wordkey + "%' ";
		}
		return processDao.queryKnowledge(querySql, countSql, curpage, perpage);
	}

	/**
	 * 统计
	 * 
	 * @param curpage
	 * @param perpage
	 * @param type//任务，实例方式
	 * @param person//人员方式
	 * @return
	 */
	public JspPage processStatisticals(int curpage, int perpage, String type, String person) {
		return processDao.processStatisticals(curpage, perpage, type, person);
	}

	/**
	 * 统计任务完成数量
	 * 
	 * @param type
	 * @param person
	 * @return
	 */
	public List<ProcessStatisticalsModel> processStatisticals(String type, String person, String startdate, String todate) {
		return processDao.queryProcessStatisticals(type, person, startdate, todate);
	}

	public JspPage queryProcessStatisticalsDetail(String exectname, String type, String person, String startdate, String todate, int curpage, int perpage) {
		return processDao.queryProcessStatisticalsDetail(exectname, type, person, startdate, todate, curpage, perpage);
	}

	/**
	 * 设置告警列表生成工单标识，0未生成，1生成
	 * 
	 * @param enentid，告警ID
	 */
	public void setEventOrderFlag(String eventid) {
		processDao.setEventOrderFlag(eventid);
	}

	public String drawBar(List<ProcessStatisticalsModel> list, String person) {
		return processDao.drawBar(list, person);
	}

	public String drawPie(List<ProcessStatisticalsModel> list, String person) {
		return processDao.drawPie(list, person);
	}

	/**
	 * 启动流程
	 * 
	 * @param deploymentId
	 * @param userId
	 */
	public String isStart(String processDefinitionId, String userId) {
		return processDao.isStart(processDefinitionId, userId);
	}

	/**
	 * 解决方案
	 * 
	 * @param category
	 * @param entity
	 * @param subentity
	 * @param wordkey
	 * @return
	 */
	public List<Knowledgebase> queryKnowledge(String category, String entity, String subentity, String wordkey) {
		String querySql = "select * from system_knowledgebase sk where 1=1 ";
		if (!"全部".equals(category) && StringUtil.isNotBlank((category))) {
			querySql = querySql + "and sk.category = '" + category + "' ";
		}
		if (!"全部".equals(entity) && StringUtil.isNotBlank((entity))) {
			querySql = querySql + "and sk.entity = '" + entity + "' ";
		}
		if (!"全部".equals(subentity) && StringUtil.isNotBlank((subentity))) {
			querySql = querySql + "and sk.subentity = '" + subentity + "' ";
		}
		if (StringUtil.isNotBlank(wordkey) && StringUtil.isNotBlank((wordkey))) {
			querySql = querySql + "and sk.contents like '%" + wordkey + "%' or sk.attachfiles like '%" + wordkey + "%' or sk.titles like '%" + wordkey + "%' ";
		}
		return processDao.queryKnowledge(querySql);
	}

	public List<String> getListcontent() {
		return processDao.getListcontent();
	}

	/**
	 * 统计pie图
	 * 
	 * @param list
	 * @return
	 */
	public String getPieXml(List<ProcessStatisticalsModel> list, String person) {
		return processDao.getPieXml(list, person);
	}

	/**
	 * 柱状图
	 * 
	 * @param list
	 * @return
	 */
	public String getBarXml(List<ProcessStatisticalsModel> list, String person) {
		return processDao.getBarXml(list, person);
	}

	/**
	 * task内容
	 * 
	 * @param list
	 * @return
	 */
	public List<String> findContentList(List list) {
		return processDao.findContentList(list);
	}

	/**
	 * 统计当前待处理任务
	 * 
	 * @param type
	 * @return
	 */
	public List<ProcessStatisticalsModel> queryProInsStatDetail(String type) {
		return processDao.queryProInsStatDetail(type);
	}

	/**
	 * 统计pie图
	 * 
	 * @param list
	 * @return
	 */
	public String getProInsPieXml(List<ProcessStatisticalsModel> list) {
		return processDao.getProInsPieXml(list);
	}

	/**
	 * 柱状图
	 * 
	 * @param list
	 * @return
	 */
	public String getProInsBarXml(List<ProcessStatisticalsModel> list) {
		return processDao.getProInsBarXml(list);
	}

	/**
	 * 流程统计
	 * 
	 * @return
	 */
	public HashMap<ProcessEnum, String> queryProIns() {
		return processDao.queryProIns();
	}

	/**
	 * 统计饼图
	 * 
	 * @param map
	 * @return
	 */
	public String pieXmlProIns(HashMap<ProcessEnum, String> map) {
		return processDao.pieXmlProIns(map);
	}

	/**
	 * 统计柱状图
	 * 
	 * @param map
	 * @return
	 */
	public String barXmllProIns(HashMap<ProcessEnum, String> map) {
		return processDao.barXmllProIns(map);
	}

}
