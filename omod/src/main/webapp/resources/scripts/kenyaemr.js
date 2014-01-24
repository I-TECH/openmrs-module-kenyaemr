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
		var icon = ui.resourceLink('kenyaui', 'images/glyphs/' + ((person.isPatient ? 'patient' : 'person') + '_' + person.gender) + '.png');
		var html = '<img src="' + icon + '" class="ke-glyph" /> ' + person.name;
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
		var icon = ui.resourceLink('kenyaui', 'images/glyphs/patient_' + patient.gender + '.png');
		var html = '<img src="' + icon + '" class="ke-glyph" /> ' + patient.name;
		if (patient.age) {
			html += ' <span style="color: #999">' + patient.age + '</span>';
		}
		return html;
	}
});

/**
 * Configure AngularJS
 */
var kenyaemrApp = angular.module('kenyaemr', []);

/**
 * Utility methods
 */
(function(kenyaemr, $) {
	/**
	 * Opens a dialog displaying the given encounter
	 * @param appId the app id
	 * @param encounterId the encounter id
	 */
	kenyaemr.openEncounterDialog = function(appId, encounterId) {
		var contentUrl = ui.pageLink('kenyaemr', 'dialog/formDialog', { appId: appId, encounterId: encounterId, currentUrl: location.href });
		kenyaui.openDynamicDialog({ heading: 'View Form', url: contentUrl, width: 90, height: 90 });
	};

	/**
	 * Updates the value of a regimen field from its displayed controls
	 * @param fieldId the regimen field id
	 */
	kenyaemr.updateRegimenFromDisplay = function(fieldId) {
		var regimenStr = '';

		$('#' + fieldId +  '-container .regimen-component').each(function() {
			var drug = jQuery(this).find('.regimen-component-drug').val();
			var dose = jQuery(this).find('.regimen-component-dose').val();
			var units = jQuery(this).find('.regimen-component-units').val();
			var frequency = jQuery(this).find('.regimen-component-frequency').val();

			if (drug || dose) {
				regimenStr += (drug + '|' + dose + '|' + units + '|' + frequency + '|');
			}
		});

		$('#' + fieldId).val(regimenStr);
	};

	/**
	 * Creates a dynamic obs field
	 * @param parentId the container element id
	 * @param fieldName the field name
	 * @param conceptId the concept id
	 * @param initialValue the initial field value (may be null)
	 * @param readOnly true if control should be read only
	 */
	kenyaemr.dynamicObsField = function(parentId, fieldName, conceptId, initialValue, readOnly) {
		var placeHolderId = kenyaui.generateId();
		$('#' + parentId).append('<div id="' + placeHolderId + '" class="ke-loading ke-form-dynamic-field">&nbsp;</div>');
		$.get('/' + OPENMRS_CONTEXT_PATH + '/kenyaemr/generateField.htm', { name: fieldName, conceptId: conceptId, initialValue: initialValue, readOnly : readOnly })
			.done(function (html) {
				$('#' + placeHolderId).removeClass('ke-loading');
				$('#' + placeHolderId).html(html);
			});
	};

}( window.kenyaemr = window.kenyaemr || {}, jQuery ));