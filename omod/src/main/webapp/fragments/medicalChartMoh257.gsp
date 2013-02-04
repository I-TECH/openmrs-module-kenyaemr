<%
	def onAddVisitSuccess = "location.href = ui.pageLink('kenyaemr', 'enterHtmlForm', { patientId: ${ patient.id }, formUuid: '${ MetadataConstants.MOH_257_VISIT_SUMMARY_FORM_UUID }', visitId: data.visitId, returnUrl: location.href })"

	def lastRegimen = null

	if (lastARVChange) {
		lastRegimen = lastARVChange.started ? kenyaEmrUi.formatRegimenLong(lastARVChange.started, ui) : ui.message("general.none")
	}
	else {
		lastRegimen = ui.message("kenyaemr.neverOnARVs")
	}
%>

<div class="panel-frame">
	<div class="panel-heading">MOH 257: Page 1</div>
	<div class="panel-content" style="background-color: #F3F9FF">

		<fieldset>
			<legend>Initial Visit New Forms</legend>

			<% if (initialVisit) { %>
				<div>
					Information entered will be attached to <a href="${ ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, visitId: initialVisit.id ]) }"><b>${ ui.format(initialVisit.visitType) }</b> visit on <b>${ kenyaEmrUi.formatDate(initialVisit.startDatetime) }</b></a>
				</div>
				<br />
				<div>
				${ ui.includeFragment("kenyaemr", "formList", [ visit: initialVisit, forms: initialVisitAvailableForms ]) }
				</div>
			<% } else {

				print ui.includeFragment("uilibrary", "widget/popupForm", [
						id: "create-initial-visit",
						buttonConfig: [
								iconProvider: "kenyaemr",
								icon: "buttons/visit_retrospective.png",
								label: "Add Initial Visit",
								extra: "When did patient first visit clinic?",
								classes: [ "padded" ]
						],
						popupTitle: "Initial Visit Details",
						prefix: "visit",
						commandObject: newREVisit,
						hiddenProperties: [ "patient" ],
						properties: [ "visitType", "location", "visitDate" ],
						propConfig: [
								"visitType": [ type: "radio" ],
						],
						fieldConfig: [
								"location": [ fieldFragment: "field/org.openmrs.Location.kenyaemr" ]
						],
						fragmentProvider: "kenyaemr",
						fragment: "medicalChartMoh257",
						action: "createRetrospectiveVisit",
						successCallbacks: [ "ui.reloadPage()" ],
						submitLabel: ui.message("general.submit"),
						cancelLabel: ui.message("general.cancel"),
						submitLoadingMessage: "Creating initial visit"
				])
			}
			%>
		</fieldset>
		<br />
		<fieldset>
			<legend>Previously Completed Forms</legend>
			<%
				if (existingPage1Encounters && existingPage1Encounters.size > 0) {
					existingPage1Encounters.each {
						println ui.includeFragment("kenyaemr", "encounterPanel", [ encounter: it ])
					}
				} else {
					println "<i>None</i>"
				}
			%>
		</fieldset>
	</div>
</div>

<div class="panel-frame">
	<div class="panel-heading">MOH 257: Page 2</div>
	<div class="panel-content" style="background-color: #F3F9FF">

		<fieldset>
			<legend>Follow Up Visits</legend>

			<%= ui.includeFragment("uilibrary", "widget/popupForm", [
					id: "create-followup-visit",
					buttonConfig: [
							iconProvider: "kenyaemr",
							icon: "buttons/visit_retrospective.png",
							label: "Add Visit Summary",
							extra: "From column",
							classes: [ "padded" ]
					],
					popupTitle: "Visit Summary Details",
					prefix: "visit",
					commandObject: newREVisit,
					hiddenProperties: [ "patientId" ],
					properties: [ "visitType", "location", "visitDate" ],
					propConfig: [
							"visitType": [ type: "radio" ],
					],
					fieldConfig: [
							"location": [ fieldFragment: "field/org.openmrs.Location.kenyaemr" ]
					],
					fragmentProvider: "kenyaemr",
					fragment: "medicalChartMoh257",
					action: "createRetrospectiveVisit",
					successCallbacks: [ onAddVisitSuccess ],
					submitLabel: ui.message("general.submit"),
					cancelLabel: ui.message("general.cancel"),
					submitLoadingMessage: "Creating retrospective visit"
			]) %>
		</fieldset>
		<br />
		<fieldset>
			<legend>Previously Completed Visit Summaries</legend>
			<%
				if (existingPage2Encounters && existingPage2Encounters.size > 0) {
					existingPage2Encounters.each {
						println ui.includeFragment("kenyaemr", "encounterPanel", [ encounter: it ])
					}
				} else {
					println "<i>None</i>"
				}
			%>
		</fieldset>
		<br />
		<fieldset>
			<legend>ARV Regimens</legend>
			${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Last recorded regimen", value: lastRegimen ]) }

			<br />

			${ ui.includeFragment("uilibrary", "widget/button", [
					label: "Change ARV regimen",
					iconProvider: "kenyaemr",
					icon: "buttons/regimen.png",
					classes: [ "padded" ]
			]) }
		</fieldset>

	</div>
</div>