<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "MOH 257 Retrospective Entry" ])

	def onAddVisitSuccess = "location.href = ui.pageLink('kenyaemr', 'enterHtmlForm'," + "{" + "patientId: ${ patient.id }, htmlFormId: ${ MetadataConstants.RETROSPECTIVE_257_FORM_UUID }, visitId: data.visitId, returnUrl: location.href })"
%>

<fieldset>
	<legend>Page 1: Care Summary</legend>

	TODO: list suitable POC forms
</fieldset>
<br />
<fieldset>
	<legend>Page 2: Initial and Follow Up Visits</legend>

	<%= ui.includeFragment("uilibrary", "widget/popupForm", [
			id: "check-in-form",
			buttonConfig: [
					iconProvider: "uilibrary",
					icon: "user_add_32.png",
					label: "Add Retrospective Visit",
					classes: [ "padded" ],
					extra: "Old visits"
			],
			popupTitle: "Retrospective Visit",
			prefix: "visit",
			commandObject: newREVisit,
			hiddenProperties: [ "patient" ],
			properties: [ "visitType", "startDatetime", "stopDatetime" ],
			propConfig: [
					"visitType": [ type: "radio" ],
			],
			fieldConfig: [
					"startDatetime": [ fieldFragment: "field/java.util.Date.datetime" ],
					"stopDatetime": [ fieldFragment: "field/java.util.Date.datetime" ],
			],
			fragment: "registrationUtil",
			fragmentProvider: "kenyaemr",
			action: "createVisit",
			successCallbacks: [ onAddVisitSuccess ],
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel"),
			submitLoadingMessage: "Creating retrospective visit"
	]) %>
</fieldset>
<br />
<fieldset>
	<legend>Page 2: ARV Drugs</legend>

	TODO: display current regimen and link to POC UI for editing regimens
</fieldset>
