/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.tool.assessment.ui.listener.author;

import java.util.List;

import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectOne;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import lombok.extern.slf4j.Slf4j;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.rubrics.logic.RubricsConstants;
import org.sakaiproject.rubrics.logic.RubricsService;
import org.sakaiproject.tool.assessment.ui.bean.author.ItemAuthorBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;

/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * @version $Id$
 */
@Slf4j
public class StartInsertItemListener implements ValueChangeListener {

  private RubricsService rubricsService = ComponentManager.get(RubricsService.class);

  /**
   * Standard process action method.
   * @param ae ValueChangeEvent
   * @throws AbortProcessingException
   */
  public void processValueChange(ValueChangeEvent ae) throws AbortProcessingException {
    ItemAuthorBean itemauthorbean = (ItemAuthorBean) ContextUtil.lookupBean("itemauthor");

    String selectedvalue = (String) ae.getNewValue();
    log.debug("StartInsertItemListener selectedvalue " + selectedvalue);
    String newitemtype = null;
    String insertItemPosition = null;
    String insertToSection = null;

    // only set itemtype when the value has indeed changed.
    if ((selectedvalue!=null) && (!selectedvalue.equals("")) ) {
      String[] strArray = selectedvalue.split(",");

      try {
        newitemtype = strArray[0].trim();

        if (strArray.length >= 2) {//SAK-44981 skipping default selections
          return;
        }

        ///////// SAK-3114: ///////////////////////////////////////////
        // note: you must include at least one selectItem in the form
        // type#,p#,q#
        // the rest, in a selectItems will get the p#,q# added in.
        ///////////////////////////////////////////////////////////////
        UISelectOne comp = (UISelectOne) ae.getComponent();
        List children = comp.getChildren();
        // right now there are two kids selectItems & selectItem
        // we use loop to keep this flexible
        for (int i = 0; i < children.size(); i++) {
          if (children.get(i) instanceof UISelectItem) {
              UISelectItem selectItem = (UISelectItem) children.get(i);
              log.debug("selectItem.getItemValue()="+selectItem.getItemValue());
              String itemValue = (String) selectItem.getItemValue();
              log.debug("itemValue ="+ itemValue);
              String[] insertArray = itemValue.split(",");
              // add in p#,q#
              insertToSection = insertArray[1].trim();
              insertItemPosition = insertArray[2].trim();
              break;
          }
        }
      } catch (Exception ex) {
        log.warn("unable to process value change", ex);
        return;
      }
      itemauthorbean.setItemType(newitemtype);
      itemauthorbean.setInsertToSection(insertToSection);
      itemauthorbean.setInsertPosition(insertItemPosition);
      itemauthorbean.setInsertType(newitemtype);
      itemauthorbean.setItemNo(String.valueOf(Integer.parseInt(insertItemPosition) +1));
      // clean up before new question SAK-6506  
      itemauthorbean.setAttachmentList(null);
      itemauthorbean.setTagsList(null);
      itemauthorbean.setResourceHash(null);
      itemauthorbean.setItemId("");
      itemauthorbean.setRbcsToken(rubricsService.generateJsonWebToken(RubricsConstants.RBCS_TOOL_SAMIGO));
      itemauthorbean.setRubricStateDetails("");

      StartCreateItemListener listener = new StartCreateItemListener();
      if (!listener.startCreateItem(itemauthorbean)) {
        throw new RuntimeException("failed to startCreatItem.");
      }

    }
  }
}
