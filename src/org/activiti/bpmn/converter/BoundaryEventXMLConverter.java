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
package org.activiti.bpmn.converter;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.apache.commons.lang.StringUtils;

/**
 * @author Tijs Rademakers
 */
public class BoundaryEventXMLConverter extends BaseBpmnXMLConverter {
  
  public static String getXMLType() {
    return ELEMENT_EVENT_BOUNDARY;
  }
  
  public static Class<? extends BaseElement> getBpmnElementType() {
    return BoundaryEvent.class;
  }
  
  @Override
  protected String getXMLElementName() {
    return ELEMENT_EVENT_BOUNDARY;
  }
  
  @Override
  protected BaseElement convertXMLToElement(XMLStreamReader xtr) {
    BoundaryEvent boundaryEvent = new BoundaryEvent();
    
    if (StringUtils.isNotEmpty(xtr.getAttributeValue(null, ATTRIBUTE_BOUNDARY_CANCELACTIVITY))) {
      String cancelActivity = xtr.getAttributeValue(null, ATTRIBUTE_BOUNDARY_CANCELACTIVITY);
      if (ATTRIBUTE_VALUE_TRUE.equalsIgnoreCase(cancelActivity)) {
        boundaryEvent.setCancelActivity(true);
      }
    }
    boundaryEvent.setAttachedToRefId(xtr.getAttributeValue(null, ATTRIBUTE_BOUNDARY_ATTACHEDTOREF));
    parseChildElements(getXMLElementName(), boundaryEvent, xtr);
    
    return boundaryEvent;
  }

  @Override
  protected void writeAdditionalAttributes(BaseElement element, XMLStreamWriter xtw) throws Exception {
    BoundaryEvent boundaryEvent = (BoundaryEvent) element;
    if (boundaryEvent.getAttachedToRef() != null) {
      writeDefaultAttribute(ATTRIBUTE_BOUNDARY_ATTACHEDTOREF, boundaryEvent.getAttachedToRef().getId(), xtw);
    }
    writeDefaultAttribute(ATTRIBUTE_BOUNDARY_CANCELACTIVITY, String.valueOf(boundaryEvent.isCancelActivity()).toLowerCase(), xtw);
  }

  @Override
  protected void writeAdditionalChildElements(BaseElement element, XMLStreamWriter xtw) throws Exception {
    BoundaryEvent boundaryEvent = (BoundaryEvent) element;
    writeEventDefinitions(boundaryEvent.getEventDefinitions(), xtw);
  }
}
