/**********************************************************************************
 * 
 * 
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.tool.assessment.ui.listener.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import lombok.extern.slf4j.Slf4j;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.samigo.util.SamigoConstants;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.ui.bean.evaluation.AgentResults;
import org.sakaiproject.tool.assessment.ui.bean.evaluation.TotalScoresBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;

/**
 * <p>
 * This handles the deletion of student submission on the Score page.
 *  </p>
 * <p>Description: Action Listener for deletion of student's submission on the Score page</p>
 * <p>Organization: Sakai Project</p>
 * @author Texas State University
 */
@Slf4j
public class GrantSubmissionListener
  implements ActionListener
{
  private final EventTrackingService eventTrackingService = ComponentManager.get( EventTrackingService.class );

  /**
   * Increases submissions remaining by 1.
   * Gives students an additional attempt.
   * 
   * @param ae ActionEvent
   * @throws AbortProcessingException
   */
  public void processAction(ActionEvent ae) throws
    AbortProcessingException
  {
    log.debug("GrantSubmission LISTENER.");

    TotalScoresBean totalScores = (TotalScoresBean) ContextUtil.lookupBean("totalScores");
	String deletedStudentId = null;
	// delivery.setSubmissionsRemaining(delivery.getSubmissionsRemaining() + 1);

	String gradingIdParam = ContextUtil.lookupParam("gradingData");
	Long gradingId = new Long(gradingIdParam);
	String publishedAssessmentId = ContextUtil.lookupParam("publishedId");

	GradingService gradingService = new GradingService();
	AssessmentGradingData ag = (AssessmentGradingData) gradingService.load(gradingIdParam);  
    gradingService.removeAssessmentGradingData(ag); // This will just flip the STATUS column, no hard deletes

    Collection agentList = totalScores.getAgents();
    for(Iterator i = agentList.iterator(); i.hasNext();) {
    	AgentResults a = (AgentResults)i.next();
    	if (a.getAssessmentGradingId().equals(gradingId)) {
    		deletedStudentId = a.getAgentId();
    		i.remove();
    	}
    }

    List gradingList = totalScores.getAssessmentGradingList();
    //Get the list of submission for this student
    List deletedStudentGradingList = new ArrayList();
    for(int i = 0; i < gradingList.size(); i++){
    	if (((AssessmentGradingData)gradingList.get(i)).getAgentId().equals(deletedStudentId)) {
    		deletedStudentGradingList.add(gradingList.get(i));
    	}
    }
    //Need to do a check to see if this is the only submission for that student
    if(deletedStudentGradingList.size() == 1){
    	//if deleted the last one submission of this student, reset the grading data to have no grade and notify gradebook
    	gradingList.clear();
    	gradingList.add(deletedStudentGradingList.get(0));
    	((AssessmentGradingData)gradingList.get(0)).setFinalScore(null);
    }
    else {
	    for(int i = 0;i < gradingList.size();i++) {
	    	if (((AssessmentGradingData)gradingList.get(i)).getAssessmentGradingId().equals(gradingId)) {
	    		gradingList.remove(i);
	    	}
	    }
    }

    totalScores.setAssessmentGradingList(gradingList);
    totalScores.setAgents(agentList);

    gradingService.notifyDeleteToGradebook(gradingList, totalScores.getPublishedAssessment(), deletedStudentId);

    // Now post an event so support teams know who deleted the submission
    eventTrackingService.post(
    		eventTrackingService.newEvent(
    				SamigoConstants.EVENT_SUBMISSION_DELETE, 
    				"siteId=" + AgentFacade.getCurrentSiteId() + ", publishedAssessmentId=" + publishedAssessmentId + ", agentId=" + deletedStudentId + ", assessmentGradingID=" + gradingId,
    				AgentFacade.getCurrentSiteId(), 
    				true, 
    				NotificationService.NOTI_NONE
    		)
    );
  }
}
