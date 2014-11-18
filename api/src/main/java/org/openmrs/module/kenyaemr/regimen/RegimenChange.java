/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.regimen;

import java.util.Date;
import java.util.Set;

import org.openmrs.Concept;

/**
 *
 */
public class RegimenChange {
	
	private Date date;
	
	private RegimenOrder stopped;
	
	private RegimenOrder started;
	
	private Set<Concept> changeReasons;
	
	private Set<String> changeReasonsNonCoded;
	
	/**
	 * @param date
	 * @param stopped
	 * @param started
	 * @param changeReasons
	 * @param changeReasonsNonCoded
	 */
	public RegimenChange(Date date, RegimenOrder stopped, RegimenOrder started, Set<Concept> changeReasons,
	    Set<String> changeReasonsNonCoded) {
		this.date = date;
		this.stopped = stopped;
		this.started = started;
		this.changeReasons = changeReasons;
		this.changeReasonsNonCoded = changeReasonsNonCoded;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the stopped
	 */
	public RegimenOrder getStopped() {
		return stopped;
	}
	
	/**
	 * @param stopped the stopped to set
	 */
	public void setStopped(RegimenOrder stopped) {
		this.stopped = stopped;
	}
	
	/**
	 * @return the started
	 */
	public RegimenOrder getStarted() {
		return started;
	}
	
	/**
	 * @param started the started to set
	 */
	public void setStarted(RegimenOrder started) {
		this.started = started;
	}
	
	/**
	 * @return the changeReasons
	 */
	public Set<Concept> getChangeReasons() {
		return changeReasons;
	}
	
	/**
	 * @param changeReasons the changeReasons to set
	 */
	public void setChangeReasons(Set<Concept> changeReasons) {
		this.changeReasons = changeReasons;
	}
	
	/**
	 * @return the changeReasonsNonCoded
	 */
	public Set<String> getChangeReasonsNonCoded() {
		return changeReasonsNonCoded;
	}
	
	/**
	 * @param changeReasonsNonCoded the changeReasonsNonCoded to set
	 */
	public void setChangeReasonsNonCoded(Set<String> changeReasonsNonCoded) {
		this.changeReasonsNonCoded = changeReasonsNonCoded;
	}
	
}
