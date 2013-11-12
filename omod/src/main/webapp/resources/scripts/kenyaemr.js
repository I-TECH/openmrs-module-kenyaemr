/**
 * Configure search types
 */
kenyaui.configureSearch('concept', {
	searchProvider: 'kenyaemr',
	searchFragment: 'search',
	format: function(concept) { return concept.name; }
});
kenyaui.configureSearch('location', {
	searchProvider: 'kenyaemr',
	searchFragment: 'search',
	format: function(location) { return location.name + ' <span style="color: #999">' + location.code + '</span>'; }
});
kenyaui.configureSearch('person', {
	searchProvider: 'kenyaemr',
	searchFragment: 'search',
	format: function(person) {
		var icon = (person.isPatient ? 'patient' : 'person') + '_' + (person.gender == 'M' ? 'm' : 'f');
		var src = ui.resourceLink('kenyaui', 'images/glyphs/' + icon + '.png');
		var html = '<img src="' + src + '" class="ke-glyph" /> ' + person.name;
		if (person.age) {
			html += ' <span style="color: #999">' + person.age + '</span>';
		}
		return html;
	}
});
kenyaui.configureSearch('patient', {
	searchProvider: 'kenyaemr',
	searchFragment: 'search',
	format: function(patient) {
		var icon = 'patient_' + (patient.gender == 'M' ? 'm' : 'f');
		var src = ui.resourceLink('kenyaui', 'images/glyphs/' + icon + '.png');
		var html = '<img src="' + src + '" class="ke-glyph" /> ' + patient.name;
		if (patient.age) {
			html += ' <span style="color: #999">' + patient.age + '</span>';
		}
		return html;
	}
});

/**
 * Utility methods
 */
var kenyaemr = (function(jq) {

	var formatHelper = function(data, formatter) {
		if (data === null || typeof formatter === 'undefined') {
			return "";
		} else if (typeof formatter === 'function') {
			return formatter(data);
		} else {
			return formatter;
		}
	};
	
	return {

		/**
		 * Opens a dialog displaying the given encounter
		 * @param appId the app id
		 * @param encounterId the encounter id
		 */
		openEncounterDialog: function(appId, encounterId) {
			var contentUrl = ui.pageLink('kenyaemr', 'dialog/formDialog', { appId: appId, encounterId: encounterId, currentUrl: location.href });
			kenyaui.openDynamicDialog({ heading: 'View Form', url: contentUrl, width: 90, height: 90 });
		},

		/**
		 * values may specify (as function(data) or static text): icon, title, leftDetails, center, right
		 */
		twoColumnStackItemFormatter: function(data, values) {
			var clickUrl = formatHelper(data, values.clickUrl);

			var ret = '<div class="ke-stack-item ke-navigable">';
			if (clickUrl) {
				ret += '<input type="hidden" name="clickUrl" value="' + clickUrl + '" />'
			}
			ret += '<table width="100%"><tr valign="top"><td width="50%">';
			ret += '  <span class="ke-icon">' + formatHelper(data, values.icon) + '</span>';
			ret += '  <b>' + formatHelper(data, values.title) + '</b><br />' + formatHelper(data, values.leftDetails);
			ret += '</td><td align="right" width="50%">';
			ret += formatHelper(data, values.right);
			ret += '</td></tr></table>';
			ret += '</div>';
			return ret;
		},
		
		/**
		 * values may specify (as function(data) or static text): icon, title, leftDetails, center, right
		 */
		threeColumnStackItemFormatter: function(data, values) {
			var clickUrl = formatHelper(data, values.clickUrl);

			var ret = '<div class="ke-stack-item ke-navigable">';
			if (clickUrl) {
				ret += '<input type="hidden" name="clickUrl" value="' + clickUrl + '" />'
			}
			ret += '<table width="100%"><tr valign="top"><td width="40%">';
			ret += '  <span class="ke-icon">' + formatHelper(data, values.icon) + '</span>';
			ret += '  <b>' + formatHelper(data, values.title) + '</b><br />' + formatHelper(data, values.leftDetails);
			ret += '</td><td align="center width="30%">';
			ret += formatHelper(data, values.center);
			ret += '</td><td align="right" width="30%">';
			ret += formatHelper(data, values.right);
			ret += '</td></tr></table>';
			ret += '</div>';
			return ret;
		},
		
		/**
		 * returns "# result(s)"
		 */
		defaultNumResultsFormatter: function(listOfItems) {
			return typeof listOfItems.length === 'number' ? (listOfItems.length + ' result(s)') : '';
		},

		/**
		 * Updates the value of a regimen field from its displayed controls
		 * @param fieldId the regimen field id
		 */
		updateRegimenFromDisplay: function(fieldId) {
			var regimenStr = '';

			$('#' + fieldId +  '-container .regimen-component').each(function() {
				var drug = jq(this).find('.regimen-component-drug').val();
				var dose = jq(this).find('.regimen-component-dose').val();
				var units = jq(this).find('.regimen-component-units').val();
				var frequency = jq(this).find('.regimen-component-frequency').val();

				if (drug || dose) {
					regimenStr += (drug + '|' + dose + '|' + units + '|' + frequency + '|');
				}
			});

			$('#' + fieldId).val(regimenStr);
		},

		/**
		 * Creates a dynamic obs field
		 * @param parentId the container element id
		 * @param fieldName the field name
		 * @param conceptId the concept id
		 * @param initialValue the initial field value (may be null)
		 * @param readOnly true if control should be read only
		 */
		dynamicObsField: function(parentId, fieldName, conceptId, initialValue, readOnly) {
			var placeHolderId = kenyaui.generateId();
			jq('#' + parentId).append('<div id="' + placeHolderId + '" class="ke-loading ke-form-dynamic-field">&nbsp;</div>');
			jq.get('/' + OPENMRS_CONTEXT_PATH + '/kenyaemr/generateField.htm', { name: fieldName, conceptId: conceptId, initialValue: initialValue, readOnly : readOnly })
			.done(function (html) {
				jq('#' + placeHolderId).removeClass('ke-loading');
				jq('#' + placeHolderId).html(html);
			});
		}
	};

})(jQuery);