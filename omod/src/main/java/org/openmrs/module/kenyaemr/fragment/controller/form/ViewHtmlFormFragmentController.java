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

package org.openmrs.module.kenyaemr.fragment.controller.form;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.kenyacore.form.FormUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.resource.ResourceFactory;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
public class ViewHtmlFormFragmentController {

	public void controller(@FragmentParam("encounter") Encounter encounter,
						   FragmentModel model,
						   HttpSession httpSession,
						   UiUtils ui,
						   @SpringBean ResourceFactory resourceFactory) throws Exception {

		Form form = encounter.getForm();

		// Get html form from database or UI resource
		HtmlForm hf = FormUtils.getHtmlForm(form, resourceFactory);

		if (hf == null) {
			throw new RuntimeException("Could not find HTML Form");
		}

		FormEntrySession fes = new FormEntrySession(encounter.getPatient(), encounter, FormEntryContext.Mode.VIEW, hf, httpSession);

		model.addAttribute("formHtml", fes.getHtmlToDisplay());
		model.addAttribute("changeHistory", new EncounterChangeHistory(encounter).simplify());
	}

	/**
	 * Represents the submission history of a form
	 */
	public class EncounterChangeHistory {

		private SortedMap<UserAndTimestamp, List<Object>> edits = new TreeMap<UserAndTimestamp, List<Object>>();

		public EncounterChangeHistory(Encounter enc) {
			for (Obs o : enc.getAllObs(true)) {
				madeEdit(o.getCreator(), o.getDateCreated(), o);
				if (o.getVoided()) {
					madeEdit(o.getVoidedBy(), o.getDateVoided(), o);
				}
			}
		}

		public List<SimpleObject> simplify() {
			List<SimpleObject> ret = new ArrayList<SimpleObject>();

			for (Map.Entry<UserAndTimestamp, List<Object>> e : edits.entrySet()) {
				UserAndTimestamp event = e.getKey();

				SimpleObject so = new SimpleObject();
				so.put("user", event.getUser());
				so.put("timestamp", event.getTimestamp());
				so.put("description", e.getValue().size() + " changes");

				ret.add(so);
			}
			return ret;
		}

		private void madeEdit(User user, Date timestamp, Object o) {
			UserAndTimestamp key = new UserAndTimestamp(user, timestamp);
			List<Object> list = edits.get(key);
			if (list == null) {
				list = new ArrayList<Object>();
				edits.put(key, list);
			}
			list.add(o);
		}
	}

	public class UserAndTimestamp implements Comparable<UserAndTimestamp> {

		private Date timestamp;

		private User user;

		public UserAndTimestamp(User user, Date timestamp) {
			this.user = user;
			this.timestamp = timestamp;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(UserAndTimestamp other) {
			int temp = timestamp.compareTo(other.timestamp);
			if (temp == 0) {
				temp = user.getId().compareTo(other.user.getId());
			}
			return temp;
		}

		/**
		 * @return the timestamp
		 */
		public Date getTimestamp() {
			return timestamp;
		}

		/**
		 * @param timestamp the timestamp to set
		 */
		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}

		/**
		 * @return the user
		 */
		public User getUser() {
			return user;
		}

		/**
		 * @param user the user to set
		 */
		public void setUser(User user) {
			this.user = user;
		}
	}
}