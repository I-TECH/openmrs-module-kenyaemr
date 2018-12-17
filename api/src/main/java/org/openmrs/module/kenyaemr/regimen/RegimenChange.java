/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.regimen;

import org.openmrs.Concept;

import java.util.Date;
import java.util.Set;

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
