package com.bpm.design.action;

import org.activiti.editor.data.model.ModelData;
import org.activiti.editor.exception.ModelException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.design.dao.DesignDao;
import com.bpm.system.action.BaseAction;

/**
 * 处理创建模型请求
 * 
 * @author HXL
 * 
 */
@Controller
@Scope("prototype")
public class CreateModelAction extends BaseAction {

	private String modelname;
	private String modeldesc;
	private String url;
	private String keytext;

	@Override
	public String execute() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode editorNode = objectMapper.createObjectNode();
		editorNode.put("id", "canvas");
		editorNode.put("resourceId", "canvas");
		ObjectNode stencilSetNode = objectMapper.createObjectNode();
		stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
		editorNode.put("stencilset", stencilSetNode);
		ModelData modelData = new ModelData();
		modelData.setModelEditorJson(editorNode.toString());

		ObjectNode modelObjectNode = objectMapper.createObjectNode();
		modelObjectNode.put("name", modelname);
		modelObjectNode.put("revision", 1);
		String description = null;
		if (StringUtils.isNotEmpty(modeldesc)) {
			description = modeldesc;
		} else {
			description = "";
		}
		modelObjectNode.put("description", description);
		modelData.setModelJson(modelObjectNode.toString());
		try {
			JsonNode modelNode = objectMapper.readTree(modelData.getModelJson());
			modelData.setName(modelNode.get("name").getTextValue());
			modelData.setRevision(modelNode.get("revision").getNumberValue().intValue());
		} catch (Exception e) {
			throw new ModelException("Model Json tree could not be read");
		}
		System.out.println(modelData.getModelJson());
		long modelId = new DesignDao().saveBaseModel(modelData, description, keytext);
		url = "/afunms/service/editor?id=" + modelId;
		return SUCCESS;
	}

	public String getKeytext() {
		return keytext;
	}

	public String getModeldesc() {
		return modeldesc;
	}

	public String getModelname() {
		return modelname;
	}

	public String getUrl() {
		return url;
	}

	public void setKeytext(String keytext) {
		this.keytext = keytext;
	}

	public void setModeldesc(String modeldesc) {
		this.modeldesc = modeldesc;
	}

	public void setModelname(String modelname) {
		this.modelname = modelname;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
