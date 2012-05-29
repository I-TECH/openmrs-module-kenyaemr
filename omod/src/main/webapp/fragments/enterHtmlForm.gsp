<%
// supports style (css style)

ui.includeJavascript("jquery.js")
ui.includeJavascript("jquery-ui.js")
%>

<script type="text/javascript" src="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.js"></script>
<link href="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.css" type="text/css" rel="stylesheet" />

<script>
	function showDiv(id) {
		var div = document.getElementById(id);
		if ( div ) { div.style.display = ""; }
	}
	
	function hideDiv(id) {
		var div = document.getElementById(id);
		if ( div ) { div.style.display = "none"; }
	}
</script>

<script>
	\$j = jQuery;
	var propertyAccessorInfo = new Array();
	
	// individual forms can define their own functions to execute before a form validation or submission by adding them to these lists
	// if any function returns false, no further functions are called and the validation or submission is cancelled
	var beforeValidation = new Array();     // a list of functions that will be executed before the validation of a form
	var beforeSubmit = new Array(); 		// a list of functions that will be executed before the submission of a form

	var tryingToSubmit = false;
	
	function submitHtmlForm() {
	    if (!tryingToSubmit) {
	        tryingToSubmit = true;
	        jq.getJSON(actionLink('enterHtmlForm', 'checkIfLoggedIn'), function(result) {
	        	checkIfLoggedInAndErrorsCallback(result.isLoggedIn);
	        });
	    }
	}

	function findAndHighlightErrors(){
		/* see if there are error fields */
		var containError = false
		var ary = \$j(".autoCompleteHidden");
		\$j.each(ary,function(index, value){
			if(value.value == "ERROR"){
				if(!containError){
					alert("<spring:message code='htmlformentry.error.autoCompleteAnswerNotValid'/>");
					var id = value.id;
					id = id.substring(0,id.length-4);
					\$j("#"+id).focus(); 					
				}
				containError=true;
			}
		});
		return containError;
	}

	/*
		It seems the logic of  showAuthenticateDialog and 
		findAndHighlightErrors should be in the same callback function.
		i.e. only authenticated user can see the error msg of
	*/
	function checkIfLoggedInAndErrorsCallback(isLoggedIn) {
		
		var state_beforeValidation=true;
		
		if (!isLoggedIn) {
			showAuthenticateDialog();
		}else{
			
			// first call any beforeValidation functions that may have been defined by the html form
			if (beforeValidation.length > 0){
				for (var i=0, l = beforeValidation.length; i < l; i++){
					if (state_beforeValidation){
						var fncn=beforeValidation[i];						
						state_beforeValidation=eval(fncn);
					}
					else{
						// forces the end of the loop
						i=l;
					}
				}
			}
			
			// only do the validation if all the beforeValidationk functions returned "true"
			if (state_beforeValidation){
				var anyErrors = findAndHighlightErrors();
			
        		if (anyErrors) {
            		tryingToSubmit = false;
            		return;
        		}else{
        			doSubmitHtmlForm();
        		}
			}
		}
	}

	function showAuthenticateDialog() {
		\$j('#passwordPopup').show();
		tryingToSubmit = false;
	}

	function loginThenSubmitHtmlForm() {
		
		\$j('#passwordPopup').hide();
		var username = \$j('#passwordPopupUsername').val();
		var password = \$j('#passwordPopupPassword').val();
		\$j('#passwordPopupUsername').val('');
		\$j('#passwordPopupPassword').val('');
		jq.getJSON(actionLink('enterHtmlForm', 'checkIfLoggedIn', { user: username, pass: password }), submitHtmlForm); 
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
			var form = jq('#htmlform');
			jq.post(form.attr('action'), form.serialize(), function(result) {
				if (result.success) {
					if (typeof(parent) !== 'undefined') {
						parent.location.reload();
					} else {
						location.reload();
					}
				} else {
					for (key in result.errors) {
						showError(key, result.errors[key]);
					}
				}
			}, 'json');
		}
		tryingToSubmit = false;
	}

	function handleDeleteButton() {
		\$j('#confirmDeleteFormPopup').show();
	}

	function cancelDeleteForm() {
		\$j('#confirmDeleteFormPopup').hide();
	}
</script>

<div id="${ config.id }" <% if (config.style) { %>style="${ config.style }"<% } %>>

	<form id="htmlform" method="post" action="${ ui.actionLink("enterHtmlForm", "submit") }" onSubmit="submitHtmlForm(); return false;">
		<input type="hidden" name="personId" value="${ command.patient.personId }"/>
		<input type="hidden" name="htmlFormId" value="${ command.htmlFormId }"/>
		<input type="hidden" name="formModifiedTimestamp" value="${ command.formModifiedTimestamp }"/>
		<input type="hidden" name="encounterModifiedTimestamp" value="${ command.encounterModifiedTimestamp }"/>
		<% if (command.encounter) { %>
			<input type="hidden" name="encounterId" value="${ command.encounter.encounterId }"/>
		<% } %>
		<input type="hidden" name="closeAfterSubmission" value="${ config.closeAfterSubmission }"/>

		${ command.htmlToDisplay }
		
		<div id="passwordPopup" style="position: absolute; z-axis: 1; bottom: 25px; background-color: #ffff00; border: 2px black solid; display: none; padding: 10px">
			<center>
				<table>
					<tr>
						<td colspan="2"><b><spring:message code="htmlformentry.loginAgainMessage"/></b></td>
					</tr>
					<tr>
						<td align="right"><b>Username:</b></td>
						<td><input type="text" id="passwordPopupUsername"/></td>
					</tr>
					<tr>
						<td align="right"><b>Password:</b></td>
						<td><input type="password" id="passwordPopupPassword"/></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><input type="button" value="Submit" onClick="loginThenSubmitHtmlForm()"/></td>
					</tr>
				</table>
			</center>
		</div>
	</form>
</div>