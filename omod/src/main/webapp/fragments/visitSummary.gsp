<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Visit Summary" ])

%>
<% if (config.showEndVisitButton) { %>
<div style="float: right">
	<%= ui.includeFragment("uilibrary", "widget/popupForm", [
		id: "check-out-form",
		buttonConfig: [
			label: "End Visit",
			extra: "Patient going home",
			classes: [ "padded" ],
			iconProvider: "kenyaemr",
			icon: "buttons/visit_end.png"
		],
		popupTitle: "Check Out",
		fields: [
			[ hiddenInputName: "visit.visitId", value: visit.visitId ],
			[ label: "End Date and Time", formFieldName: "visit.stopDatetime", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ]
		],
		fragment: "registrationUtil",
		fragmentProvider: "kenyaemr",
		action: "editVisit",
		successCallbacks: [ "location.href = '${ ui.pageLink("kenyaemr", "registrationViewPatient", [ patientId: patient.id ]) }'" ],
		submitLabel: ui.message("general.submit"),
		cancelLabel: ui.message("general.cancel"),
		submitLoadingMessage: "Checking Out"
	]) %>
</div>
<% } %>

${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Type", value: visit.visitType ]) }
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Location", value: visit.location ]) }
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "From", value: visit.startDatetime, showTime: true ]) }

<% if (visit.stopDatetime) { %>
${ ui.includeFragment("kenyaemr", "dataPoint", [ label: "Until", value: visit.stopDatetime, showTime: true ]) }
<% } %>

<div style="clear: both"></div>
