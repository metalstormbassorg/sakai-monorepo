/**********************************************************************************
 * $URL $
 * $Id $
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
package org.sakaiproject.tool.assessment.shared.api.grading;

import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.PublishedAssessmentIfc;

/**
 * The GradebookServiceAPI describes an interface for gradebook information
 * for published assessments.
 *
 * @author Ed Smiley <esmiley@stanford.edu>
 */
public interface GradebookServiceAPI
{
  /**
   *
   * @param publishedAssessment
   * @return
   */
  public boolean addToGradebook(PublishedAssessmentIfc publishedAssessment) ;
  /**
   *
   * @param publishedAssessment
   * @return
   */
  public boolean isAssignmentDefined(String assessmentTitle);
  /**
   *
   * @param siteId
   * @param publishedAssessmentId
   */
  public void removeExternalAssessment(String siteId,
                                              String publishedAssessmentId);
  /**
   *
   * @param ag
   * @param agentIdString
   */
  public void updateExternalAssessment(AssessmentGradingData ag,
                                              String agentIdString);

  /**
   *
   * @param ag
   */
  public void updateExternalAssessmentScore(AssessmentGradingData ag) ;

}
