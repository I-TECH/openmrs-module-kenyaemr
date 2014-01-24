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

var tryingToSubmit = false;

(function(kenyahfe, $) {

	kenyahfe.submitHtmlForm = function(returnUrl) {
		if (!tryingToSubmit) {
			tryingToSubmit = true;
			ui.disableConfirmBeforeNavigating();
			$.getJSON(ui.fragmentActionLink('kenyaemr', 'emrUtils', 'isAuthenticated'), function(result) {
				checkIfLoggedInAndErrorsCallback(result.authenticated, returnUrl);
			});
		}
	};

}( window.kenyahfe = window.kenyahfe || {}, jQuery ));


/**
 * It seems the logic of showAuthenticateDialog and findAndHighlightErrors should be in the same callback function.
 * i.e. only authenticated user can see the error msg of
 */
function checkIfLoggedInAndErrorsCallback(authenticated, returnUrl) {
	var state_beforeValidation = true;

	if (!authenticated) {
		showAuthenticateDialog();
	}
	else {
		// first call any beforeValidation functions that may have been defined by the html form
		if (beforeValidation.length > 0){
			for (var i = 0, l = beforeValidation.length; i < l; i++){
				if (state_beforeValidation){
					var fncn = beforeValidation[i];
					state_beforeValidation = eval(fncn);
				}
				else {
					i = l; // forces the end of the loop
				}
			}
		}

		// only do the validation if all the beforeValidation functions returned "true"
		if (state_beforeValidation) {
			var anyErrors = findAndHighlightErrors();

			if (anyErrors) {
				tryingToSubmit = false;
				return;
			}
			else {
				doSubmitHtmlForm(returnUrl);
			}
		}
	}
}

function findAndHighlightErrors() {
	/* see if there are error fields */
	var containError = false
	var ary = jQuery(".autoCompleteHidden");
	jQuery.each(ary, function(index, value) {
		if (value.value == "ERROR"){
			if (!containError) {
				alert("Invalid answer");
				var id = value.id;
				id = id.substring(0,id.length-4);
				jQuery("#" + id).focus();
			}
			containError = true;
		}
	});
	return containError;
}

/**
 * Shows the authentication dialog box
 */
function showAuthenticateDialog() {
	kenyaui.openPanelDialog({ templateId: 'authentication-dialog', width: 50, height: 15 });
	tryingToSubmit = false;
}

/**
 * Called when authentication dialog box is submitted
 */
function onSubmitAuthenticationDialog() {
	var username = jQuery('#authentication-dialog-username').val();
	var password = jQuery('#authentication-dialog-password').val();

	kenyaui.closeDialog();

	// Try authenticating and then submitting again...
	jQuery.getJSON(ui.fragmentActionLink('kenyaemr', 'emrUtils', 'authenticate', { username: username, password: password }), submitHtmlForm);
}

function doSubmitHtmlForm(returnUrl) {
	kenyaui.clearFormErrors('htmlform');

	// First call any beforeSubmit functions that may have been defined by the form
	var hasBeforeSubmitErrors = false;

	for (var i = 0; i < beforeSubmit.length; i++){
		if (beforeSubmit[i]() === false) {
			hasBeforeSubmitErrors = true;
		}
	}

	// If any beforeSubmit returned false, notify user of errors and abandon submission
	if (hasBeforeSubmitErrors) {
		kenyaui.notifyError('Please fix all errors and resubmit');
	}
	else {
		kenyaui.openLoadingDialog({ message: 'Submitting form...' });

		var form = jQuery('#htmlform');
		jQuery.post(form.attr('action'), form.serialize(), function(result) {
			if (result.success) {
				if (returnUrl) {
					ui.navigate(returnUrl);
				}
				else {
					if (typeof(parent) !== 'undefined') {
						parent.location.reload();
					} else {
						location.reload();
					}
				}
			} else {
					kenyaui.closeDialog();
					for (key in result.errors) {
						showError(key, result.errors[key]);
					}
					kenyaui.notifyError('Please fix all errors and resubmit');
					ui.enableConfirmBeforeNavigating();
			}
		}, 'json')
		.error(function(jqXHR, textStatus, errorThrown) {
				kenyaui.closeDialog();
				ui.enableConfirmBeforeNavigating();
				window.alert('Unexpected error, please contact your System Administrator: ' + textStatus);
		});
	}

	tryingToSubmit = false;
}

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