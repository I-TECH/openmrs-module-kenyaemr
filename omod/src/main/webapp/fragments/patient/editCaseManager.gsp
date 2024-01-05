<%
	ui.decorateWith("kenyaui", "panel", [ heading: (command.existing ? "Edit" : "Add") + " Case Manager", frameOnly: true ])
	def rows = [
		[
			[ object: command, property: "startDate", label: "Start date" ],
			[ object: command, property: "endDate", label: "End date" ]
		]
	]
%>

<form id="edit-relationship-form" method="post" action="${ ui.actionLink("kenyaemr", "patient/editCaseManager", "saveRelationship") }">
	<% if (command.existing) { %>
		<input type="hidden" name="existingId" value="${ command.existing.id }"/>

	<% } %>
	<input type="hidden" name="patientId" value="${ command.patient.id }"/>

	<div class="ke-panel-content">

		<div class="ke-form-globalerrors" style="display: none"></div>
		<br/>
		<div class="ke-field-label"><strong>Choose a provider</strong></div>
		<select name="providerId" class ="providerId" id ="providerId">
			<option></option>
			<%providersList.each { %>
			<option value="${it.id}">${it.name}</option>
			<%}%>
		</select>
		<div id="provider-msgBox" class="ke-warning">Provider is Required</div>
		<br/> <br/> <br/>
		<label><strong>Relationship</strong></label>
		<br/>
		<select name="isToPatient" class ="isToPatient" id ="isToPatient">
			<option value="9065e3c6-b2f5-4f99-9cbf-f67fd9f82ec5">Case manager</option>
			</select>
		<br/> <br/> <br/>
		<% rows.each { %>
		${ ui.includeFragment("kenyaui", "widget/rowOfFields", [ fields: it ]) }
		<% } %>

	</div>
	<div class="ke-panel-footer">
		<button type="submit" id="submit-caseManager"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/ok.png") }" /> Save</button>
		<button type="button" class="cancel-button"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Cancel</button>
	</div>
	
</form>

<script type="text/javascript">
jq(function() {
	jq('#edit-relationship-form .cancel-button').click(function() {
		ui.navigate('${ returnUrl }');
	});

	kenyaui.setupAjaxPost('edit-relationship-form', {
		onSuccess: function(data) {
			ui.navigate('${ returnUrl }');
		}
	});

    jQuery("#provider-msgBox").hide();
    //validate provider is selected
    jQuery('#submit-caseManager').click(function(){
    if(jQuery('select[id=providerId]').val() !=""){
        jQuery("#provider-msgBox").hide();
    } else {
        // Provider is required
        jQuery("#provider-msgBox").show();
        return;
    }
   })

});
</script>