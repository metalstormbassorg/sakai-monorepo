/**
 * Copyright (c) 2003-2016 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.grading.api;

/**
 * Represents the different ways an (internal) Assignment can be sorted
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public enum SortType {

    SORT_BY_NONE, //no explicit sorting
    SORT_BY_DATE,
    SORT_BY_NAME,
    SORT_BY_MEAN,
    SORT_BY_POINTS,
    SORT_BY_RELEASED,
    SORT_BY_COUNTED,
    SORT_BY_EDITOR,
    SORT_BY_CATEGORY,
    SORT_BY_SORTING; //default
}
