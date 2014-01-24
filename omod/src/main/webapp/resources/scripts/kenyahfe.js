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
 *
 * Html Form Entry support for KenyaEMR
 */

(function(kenyahfe, $) {
	var submitting = false;

	/**
	 * Tries to submit the current HTML form
	 * @param returnUrl the URL to redirect to if successful
	 */
	kenyahfe.submitForm = function(returnUrl) {
		// Ensure users can't submit again whilst submission is ongoing
		if (submitting) {
			return;
		}

		submitting = true;

		// Show login dialog if user not authenticated
		kenyaemr.ensureUserAuthenticated(function() {

			if (validateForm()) {
				doFormSubmission(returnUrl);
			}
			else {
				// Keep user on page to fix client-side errors
				kenyaui.notifyError('Please fix all errors and resubmit');
				submitting = false;
				return;
			}
		});
	};

	/**
	 * Performs submission of the current HTML form
	 */
	function doFormSubmission(returnUrl) {
		kenyaui.openLoadingDialog({ message: 'Submitting form...' });

		var form = $('#htmlform');

		jQuery.post(form.attr('action'), form.serialize(), function(result) {
			if (result.success) {
				ui.disableConfirmBeforeNavigating();

				if (returnUrl) {
					ui.navigate(returnUrl);
				}
				else {
					ui.reloadPage();
				}
			}
			else {
				// Show errors on form
				for (key in result.errors) {
					showError(key, result.errors[key]);
				}

				// Keep user on form page to fix errors
				kenyaui.notifyError('Please fix all errors and resubmit');
				kenyaui.closeDialog();
				submitting = false;
			}
		}, 'json')
		.error(function(jqXHR, textStatus, errorThrown) {
			window.alert('Unexpected error, please contact your System Administrator: ' + textStatus);
			console.log(errorThrown);
		});
	}

	/**
	 * Validates the current HTML form to determine if it's ready to be submitted
	 * @returns boolean whether or not form is valid
	 */
	function validateForm() {
		var fieldsWithIllegalValues = $('.illegalValue');
		if (fieldsWithIllegalValues.length > 0) {
			return false;
		}

		kenyaui.clearFormErrors('htmlform');

		var ary = $(".autoCompleteHidden");
		$.each(ary, function(index, value) {
			if (value.value == "ERROR"){
				var id = value.id;
				id = id.substring(0, id.length - 4);
				$("#" + id).focus();
				return false;
			}
		});

		var hasBeforeSubmitErrors = false;

		for (var i = 0; i < beforeSubmit.length; i++){
			if (beforeSubmit[i]() === false) {
				hasBeforeSubmitErrors = true;
			}
		}

		return !hasBeforeSubmitErrors;
	}

}( window.kenyahfe = window.kenyahfe || {}, jQuery ));

/**
 * Because setValue doesn't work for datetime fields
 */
function setDatetimeValue(elementAndProperty, value) {
	var fieldId = elementAndProperty.split(".")[0];

	jQuery('#' + fieldId + ' input[type=text]').datepicker('setDate', value);

	jQuery('#' + fieldId + ' select[name$=hours]').val(value.getHours());
	jQuery('#' + fieldId + ' select[name$=minutes]').val(value.getMinutes());
	jQuery('#' + fieldId + ' select[name$=seconds]').val(value.getSeconds());
}


function showDiv(id) {
	jQuery('#' + id).show();
}

function hideDiv(id) {
	jQuery('#' + id).hide();
}