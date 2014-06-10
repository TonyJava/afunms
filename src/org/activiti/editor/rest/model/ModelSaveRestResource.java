/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.editor.rest.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.data.model.ModelData;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.afunms.common.util.DBManager;
import com.bpm.system.dbpool.DbConn;
import com.ibm.db2.jcc.b.p;
import com.informix.util.stringUtil;

/**
 * @author Tijs Rademakers
 */
public class ModelSaveRestResource extends ServerResource implements ModelDataJsonConstants {
  
  protected static final Logger LOGGER = Logger.getLogger(ModelSaveRestResource.class.getName());
  
  protected RepositoryService repositoryService;

  @Put
  public void saveModel(Form modelForm) {
    ObjectMapper objectMapper = new ObjectMapper();
    String modelId = (String) getRequest().getAttributes().get("modelId");
    String name = "";
    String desc = "";
    try {
      ModelData model = new ModelData();
      DBManager db=new DBManager();
     
      Connection conn=db.getConn();
      conn.setAutoCommit(false);
      PreparedStatement pstmt=null;
      
      String sql = "";
      sql = String.format("select modeljson from act_ge_bytearray_temp where modelid='%s' ", modelId);
      ResultSet rs = db.executeQuery(sql);
      if(rs.next()) {
    	  model.setModelJson(rs.getString("modeljson"));
      }
     /* ModelData model = modelDao.getModelById(Long.valueOf(modelId));*/
      
      ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getModelJson());
      
      modelJson.put(MODEL_NAME, modelForm.getFirstValue("name"));
      modelJson.put(MODEL_DESCRIPTION, modelForm.getFirstValue("description"));
      name = modelForm.getFirstValue("name");
      desc = modelForm.getFirstValue("description");
      model.setModelJson(modelJson.toString());
      System.out.println(modelForm.getFirstValue("json_xml")+"json_xml----------------");
      model.setModelEditorJson(modelForm.getFirstValue("json_xml"));
      System.out.println(modelForm.getFirstValue("svg_xml")+"svg_xml----------------");
      model.setModelSvg(new String(modelForm.getFirstValue("svg_xml").getBytes("utf-8")));
      
      
      try {
	      System.out.println("converting to model");
	      ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(model.getModelEditorJson());
	      BpmnModel model1 = new BpmnJsonConverter().convertToBpmnModel(modelNode);
	      System.out.println("converted to model");
	      
	      byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model1);
	      byte[] svgBytes=modelForm.getFirstValue("svg_xml").getBytes("utf-8");
	      pstmt=conn.prepareStatement("update act_ge_bytearray_temp set " +
	      								"name=?,svgxml=?,bpmxml=?,modeljson=?," +
	      								"editorjson=?," +
	      								"revision=?,modeldesc=? where modelid=?");
	      
	      pstmt.setString(1, name);
	      pstmt.setBytes(2, svgBytes);
	      //pstmt.setBytes(3, bpmnBytes);
	      pstmt.setString(3, new String(bpmnBytes,"utf-8"));
	      pstmt.setString(4, model.getModelJson());
	      pstmt.setString(5, model.getModelEditorJson());
	      pstmt.setInt(6, model.getRevision());
	      pstmt.setString(7, desc);
	      pstmt.setString(8, modelId);
	      pstmt.execute();
	      conn.setAutoCommit(true);
	      if(pstmt!=null) pstmt.close();
	      if(conn!=null) conn.close();
	     
	      
	     /* String bpmxml=new String(bpmnBytes,"utf-8");
	      String svgxml=new String(model.getModelSvg().getBytes("utf-8"));
	      
	      sql = String.format("update act_ge_bytearray_temp set " +
	      		"name='%s',svgxml='%s',bpmxml='%s',modeljson='%s',editorjson='%s',revision='%s',modeldesc='%s' " +
	      		"where modelid='%s'", name,svgxml,bpmxml,model.getModelJson(),
	      		model.getModelEditorJson(),model.getRevision(),desc,modelId);
	      
	      System.out.println(sql);
	      db.executeUpdate(sql);*/
	     db.close();
	      
	    } catch(Exception e) {
	    	e.printStackTrace();
	      
	    }finally {
	    	if(null!=rs) {
	    		rs.close();
	    	}
	    }
      
      
    } catch(Exception e) {
      LOGGER.log(Level.SEVERE, "Error saving model", e);
      setStatus(Status.SERVER_ERROR_INTERNAL);
    }
  }
  
  
 
}
