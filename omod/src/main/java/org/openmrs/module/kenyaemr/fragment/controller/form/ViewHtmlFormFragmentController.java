/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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