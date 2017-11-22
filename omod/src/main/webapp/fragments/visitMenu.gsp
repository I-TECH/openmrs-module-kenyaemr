<script type="text/javascript">
    function openPatientChart() {
        kenyaui.openPanelDialog({ templateId: 'patient-chart', width: 85, height: 70, scrolling: true });
    }

    function openPatientSummary() {
        kenyaui.openPanelDialog({ templateId: 'patient-summary', width: 85, height: 70, scrolling: true });
    }

    function openVisitSummary() {
        kenyaui.openPanelDialog({ templateId: 'visit-summary', width: 85, height: 70, scrolling: true });
    }

</script>

<div class="ke-panelbar" style="text-align: right">
	<% if (visit) { %>
	<button type="button" onclick="openPatientChart();"><img src="${ ui.resourceLink("kenyaui", "images/buttons/summary.png") }" /> Patient Overview</button>
	<button type="button" onclick="openPatientSummary();"><img src="${ ui.resourceLink("kenyaui", "images/buttons/summary.png") }" /> Patient Summary</button>
	<button type="button" onclick="openVisitSummary();"><img src="${ ui.resourceLink("kenyaui", "images/buttons/summary.png") }" /> Visit Summary</button>
	<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
			buttonConfig: [ label: "Check out of visit", iconProvider: "kenyaui", icon: "buttons/visit_end.png" ],
			dialogConfig: [ heading: "Check Out", width: 50, height: 30 ],
			fields: [
						[ hiddenInputName: "visitId", value: visit.visitId ],
						[ hiddenInputName: "appId", value: currentApp.id ],
						[ label: "End Date and Time", formFieldName: "stopDatetime", class: java.util.Date, initialValue: new Date(), showTime: true ]
			],
			fragmentProvider: "kenyaemr",
			fragment: "registrationUtil",
			action: "stopVisit",
			onSuccessCallback: "ui.reloadPage()",
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
	]) %>
	<% } else if (!patient.dead && !patient.voided) { %>
	<button type="button" onclick="openPatientChart();"><img src="${ ui.resourceLink("kenyaui", "images/buttons/summary.png") }" /> Patient Overview</button>
	<button type="button" onclick="openPatientSummary();"><img src="${ ui.resourceLink("kenyaui", "images/buttons/summary.png") }" /> Patient Summary</button>
	<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
			buttonConfig: [ label: "Check in for visit", iconProvider: "kenyaui", icon: "buttons/registration.png" ],
			dialogConfig: [ heading: "Check In", width: 50, height: 30 ],
			prefix: "visit",
			commandObject: newCurrentVisit,
			hiddenProperties: [ "patient" ],
			properties: [ "visitType", "startDatetime" ],
			extraFields: [
					[ hiddenInputName: "appId", value: currentApp.id ]
			],
			propConfig: [
					"visitType": [ type: "radio" ],
			],
			fieldConfig: [
					"visitType": [ label: "Visit Type" ],
					"startDatetime": [ showTime: true ]
			],
			fragmentProvider: "kenyaemr",
			fragment: "registrationUtil",
			action: "startVisit",
			onSuccessCallback: "ui.reloadPage()",
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
	]) %>
	<% } %>
</div>

<div id="patient-chart" title="Patient Overview" style="display: none">
	${ ui.includeFragment("kenyaemr", "program/programCarePanels", [ patient: currentPatient, complete: true, activeOnly: false ]) }
	<div align="center">
		<button type="button" onclick="kenyaui.closeDialog();"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Close</button>
	</div>
</div>

<div id="patient-summary" title="" style="display: none">
	${ ui.includeFragment("kenyaemr", "summaries", [ patient: currentPatient ]) }
	<br/>
	<br/>
	<div align="center">
		<button type="button" onclick="kenyaui.closeDialog();"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Close</button>
	</div>
</div>

<div id="visit-summary" title="Visit Summary" style="display: none">
	${ ui.includeFragment("kenyaemr", "patient/currentVisitSummary", [ patient: currentPatient, visit: visit]) }
	<div align="center">
		<button type="button" onclick="kenyaui.closeDialog();"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Close</button>
	</div>
	</div>