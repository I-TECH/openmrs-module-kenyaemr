<%
// supports style (css style)

ui.includeJavascript("kenyaui", "jquery.js")
ui.includeJavascript("kenyaui", "jquery-ui.js")
ui.includeJavascript("kenyaemr", "dwr-util.js")
%>

<script type="text/javascript" src="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.js"></script>
<link href="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.css" type="text/css" rel="stylesheet" />

<script type="text/javascript">
	\$j = jQuery; // Forms should use jq like everything else, but for backwards compatibility we allow this for now

	function showDiv(id) {
		jq('#' + id).show();
	}
	
	function hideDiv(id) {
		jq('#' + id).hide();
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
			jq.getJSON(ui.fragmentActionLink('kenyaemr', 'emrUtils', 'isAuthenticated'), function(result) {
				checkIfLoggedInAndErrorsCallback(result.authenticated);
			});
		}
	}

	/*
		It seems the logic of  showAuthenticateDialog and 
		findAndHighlightErrors should be in the same callback function.
		i.e. only authenticated user can see the error msg of
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
		var ary = jq(".autoCompleteHidden");
		jq.each(ary, function(index, value){
			if(value.value == "ERROR"){
				if(!containError){
					alert("${ ui.message("htmlformentry.error.autoCompleteAnswerNotValid") }");
					var id = value.id;
					id = id.substring(0,id.length-4);
					jq("#"+id).focus();
				}
				containError=true;
			}
		});
		return containError;
	}

	function showAuthenticateDialog() {
		kenyaui.openPanelDialog('Login Required', authenticationDialogHtml, 50, 10);
		tryingToSubmit = false;
	}

	function onSubmitAuthenticationDialog() {
		var username = jq('#authentication-dialog-username').val();
		var password = jq('#authentication-dialog-password').val();

		kenyaui.closeDialog();

		// Try authenticating and then submitting again...
		jq.getJSON(ui.fragmentActionLink('kenyaemr', 'emrUtils', 'authenticate', { username: username, password: password }), submitHtmlForm);
	}

	function doSubmitHtmlForm() {
		
		// first call any beforeSubmit functions that may have been defined by the form
		var state_beforeSubmit=true;
		if (beforeSubmit.length > 0){
			for (var i=0, l = beforeSubmit.length; i < l; i++){
				if (state_beforeSubmit){
					var fncn=beforeSubmit[i];						
					state_beforeSubmit=fncn();					
				}
				else{
					// forces the end of the loop
					i=l;
				}
			}
		}
		
		// only do the submit if all the beforeSubmit functions returned "true"
		if (state_beforeSubmit){
			kenyaui.openLoadingDialog({ message: 'Submitting form...' });
			kenyaui.clearFormErrors('htmlform');

			var form = jq('#htmlform');
			jq.post(form.attr('action'), form.serialize(), function(result) {
				if (result.success) {
					<% if (command.returnUrl) { %>
						location.href = '${ command.returnUrl }';
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

	function handleDeleteButton() {
		jq('#confirmDeleteFormPopup').show();
	}

	function cancelDeleteForm() {
		jq('#confirmDeleteFormPopup').hide();
	}

	/**
	 * Because setValue doesn't work for datetime fields
	 */
	function setDatetimeValue(elementAndProperty, value) {
		var fieldId = elementAndProperty.split(".")[0];

		jq('#' + fieldId + ' input[type=text]').datepicker('setDate', value);

		jq('#' + fieldId + ' select[name\$=hours]').val(value.getHours());
		jq('#' + fieldId + ' select[name\$=minutes]').val(value.getMinutes());
		jq('#' + fieldId + ' select[name\$=seconds]').val(value.getSeconds());
	}

	/**
	 * Update blank encounter dates to default to visit start date or current date
	 */
	jq(function() {
		authenticationDialogHtml = jq('#authentication-dialog').html();
		jq('#authentication-dialog').empty();

		if (getValue('encounter-date.value') == '') {
			setDatetimeValue('encounter-date.value', new Date(${ visit ? ("'" + visit.startDatetime + "'") : '' }));
		}
	});
</script>

<div id="${ config.id }" <% if (config.style) { %>style="${ config.style }"<% } %>>

	<form id="htmlform" method="post" action="${ ui.actionLink("kenyaemr", "enterHtmlForm", "submit") }" onSubmit="submitHtmlForm(); return false;">
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

			<span class="error" style="display: none" id="general-form-error"></span>

			<div style="background-color: white"><!-- Because not all forms use .ke-form-content like they should -->
				${ command.htmlToDisplay }
			</div>
		</div>

	</form>
</div>

<div id="authentication-dialog" style="display: none">
	<div style="padding-bottom: 12px; text-align: center">${ ui.message("kenyaemr.authenticateForFormSubmission") }</div>
	<div align="center">
		<table border="0">
			<tr>
				<td align="right"><b>Username:</b></td>
				<td><input type="text" id="authentication-dialog-username"/></td>
			</tr>
			<tr>
				<td align="right"><b>Password:</b></td>
				<td><input type="password" id="authentication-dialog-password"/></td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input type="button" value="Submit" onClick="onSubmitAuthenticationDialog()"/></td>
			</tr>
		</table>
	</div>
</div>

<% if (command.fieldAccessorJavascript) { %>
	<script type="text/javascript">
		${ command.fieldAccessorJavascript }
	</script>
<% } %>

<script type="text/javascript">
	jq(function() {
		ui.confirmBeforeNavigating('#htmlform');
	});
</script>