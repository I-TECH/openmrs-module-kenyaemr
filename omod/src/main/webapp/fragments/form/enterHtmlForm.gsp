<%
	ui.includeJavascript("kenyaemr", "dwr-util.js")
%>

<script type="text/javascript" src="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.js"></script>
<link href="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.css" type="text/css" rel="stylesheet" />

<script type="text/javascript">
	\$j = jQuery; // For backwards compatibility - some forms maybe using this to reference jQuery

	function showDiv(id) {
		jQuery('#' + id).show();
	}
	
	function hideDiv(id) {
		jQuery('#' + id).hide();
	}

	var propertyAccessorInfo = new Array();
	
	// individual forms can define their own functions to execute before a form validation or submission by adding them to these lists
	// if any function returns false, no further functions are called and the validation or submission is cancelled
	var beforeValidation = new Array();     // a list of functions that will be executed before the validation of a form
	var beforeSubmit = new Array(); 		// a list of functions that will be executed before the submission of a form

	var tryingToSubmit = false;

	function submitHtmlForm() {
		if (!tryingToSubmit) {
			tryingToSubmit = true;
			ui.disableConfirmBeforeNavigating();
			jQuery.getJSON(ui.fragmentActionLink('kenyaemr', 'emrUtils', 'isAuthenticated'), function(result) {
				checkIfLoggedInAndErrorsCallback(result.authenticated);
			});
		}
	}

	/**
	 * It seems the logic of showAuthenticateDialog and findAndHighlightErrors should be in the same callback function.
	 * i.e. only authenticated user can see the error msg of
	 */
	function checkIfLoggedInAndErrorsCallback(authenticated) {
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
					doSubmitHtmlForm();
				}
			}
		}
	}

	function findAndHighlightErrors(){
		/* see if there are error fields */
		var containError = false
		var ary = jQuery(".autoCompleteHidden");
		jQuery.each(ary, function(index, value){
			if(value.value == "ERROR"){
				if(!containError){
					alert("${ ui.message("htmlformentry.error.autoCompleteAnswerNotValid") }");
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
		kenyaui.openPanelDialog({ templateId: 'authentication-dialog', width: 50, height: 50 });
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

	function doSubmitHtmlForm() {
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
					<% if (command.returnUrl) { %>
					ui.navigate('${ command.returnUrl }');
					<% } else { %>
						if (typeof(parent) !== 'undefined') {
							parent.location.reload();
						} else {
							location.reload();
						}
					<% } %>
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

		jQuery('#' + fieldId + ' select[name\$=hours]').val(value.getHours());
		jQuery('#' + fieldId + ' select[name\$=minutes]').val(value.getMinutes());
		jQuery('#' + fieldId + ' select[name\$=seconds]').val(value.getSeconds());
	}

	jQuery(function() {
		<% if (config.defaultEncounterDate) { %>
		// Update blank encounter dates to default to visit start date or current date
		if (getValue('encounter-date.value') == '') {
			setDatetimeValue('encounter-date.value', new Date(${ config.defaultEncounterDate.time }));
		}
		<% } %>

		// Inject discard button
		jQuery('#discard-button').click(function() { ui.navigate('${ returnUrl }'); })
				.insertAfter(jQuery('input.submitButton'));
	});
</script>

<div id="${ config.id }" <% if (config.style) { %>style="${ config.style }"<% } %>>

	<div style="display: none">
		<button id="discard-button" type="button">
			<img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Discard Changes
		</button>
	</div>

	<form id="htmlform" method="post" action="${ ui.actionLink("kenyaemr", "form/enterHtmlForm", "submit") }" onSubmit="submitHtmlForm(); return false;">
		<input type="hidden" name="appId" value="${ currentApp.id }"/>
		<input type="hidden" name="personId" value="${ command.patient.personId }"/>
		<input type="hidden" name="formId" value="${ command.form.formId }"/>
		<input type="hidden" name="formModifiedTimestamp" value="${ command.formModifiedTimestamp }"/>
		<input type="hidden" name="encounterModifiedTimestamp" value="${ command.encounterModifiedTimestamp }"/>
		<% if (command.encounter) { %>
			<input type="hidden" name="encounterId" value="${ command.encounter.encounterId }"/>
		<% } %>
		<% if (visit) { %>
			<input type="hidden" name="visitId" value="${ visit.visitId }"/>
		<% } %>
		<% if (command.returnUrl) { %>
			<input type="hidden" name="returnUrl" value="${ command.returnUrl }"/>
		<% } %>
		<input type="hidden" name="closeAfterSubmission" value="${ config.closeAfterSubmission }"/>

		<div class="ke-panel-frame">
			<div class="ke-panel-heading">${ command.form.name }</div>

			<div class="ke-form-generalerror" style="display: none" id="general-form-error"></div>

			<div style="background-color: white"><!-- Because not all forms use .ke-form-content like they should -->
				${ command.htmlToDisplay }
			</div>
		</div>

	</form>
</div>

<div id="authentication-dialog" title="Login Required" style="display: none">
	<div class="ke-panel-content">
		<div style="padding-bottom: 12px; text-align: center">${ ui.message("kenyaemr.authenticateForFormSubmission") }</div>
		<div id="authentication-dialog-error" class="error" style="display: none"></div>
		<table border="0" align="center">
			<tr>
				<td align="right"><b>Username:</b></td>
				<td><input type="text" id="authentication-dialog-username"/></td>
			</tr>
			<tr>
				<td align="right"><b>Password:</b></td>
				<td><input type="password" id="authentication-dialog-password"/></td>
			</tr>
		</table>
	</div>
	<div class="ke-panel-controls">
		<button type="button" onclick="onSubmitAuthenticationDialog()">
			<img src="${ ui.resourceLink("kenyaui", "images/glyphs/login.png") }" /> Login
		</button>
	</div>
</div>

<% if (command.fieldAccessorJavascript) { %>
	<script type="text/javascript">
		${ command.fieldAccessorJavascript }
	</script>
<% } %>

<script type="text/javascript">
	jQuery(function() {
		ui.confirmBeforeNavigating('#htmlform');
	});
</script>