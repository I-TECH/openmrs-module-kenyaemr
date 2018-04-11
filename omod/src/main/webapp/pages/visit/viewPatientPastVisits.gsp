<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, layout: "sidebar" ])

	def menuItems = [
			[label: "Back to home", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "clinician/clinicianViewPatient", [patient: currentPatient, patientId: currentPatient.patientId])]
			,
			[
					label: "MOH 257",
					href: ui.pageLink("kenyaemr", "visit/viewPatientPastVisits", [ patientId: currentPatient.id, section: "moh257" ]),
					active: (selection == "section-moh257"),
					iconProvider: "kenyaui",
					icon: "forms/moh257.png"
			]
	]
%>
<div class="ke-page-sidebar">

	<div class="ke-panel-frame">
		<% menuItems.each { item -> print ui.includeFragment("kenyaui", "widget/panelMenuItem", item) } %>
	</div>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Total Visits(${ visitsCount })</div>

		<% if (!visits) {
			print ui.includeFragment("kenyaui", "widget/panelMenuItem", [
					label: ui.message("general.none"),
			])
		}
		else {
			visits.each { visit ->
				print ui.includeFragment("kenyaui", "widget/panelMenuItem", [
						label: ui.format(visit.visitType),
						href: ui.pageLink("kenyaemr", "visit/viewPatientPastVisits", [ patientId: currentPatient.id, visitId: visit.id ]),
						extra: kenyaui.formatVisitDates(visit),
						active: (selection == "visit-" + visit.id)
				])
			}
		} %>
	</div>

</div>

<div class="ke-page-content">

	<% if (visit) { %>

	${ ui.includeFragment("kenyaemr", "visitSummary", [ visit: visit ]) }
	<% if (!visit.voided) { %>
	${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: visit ]) }
	${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: visit ]) }
	<% } %>

	<% } else if (form) { %>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">${ ui.format(form) }</div>
		<div class="ke-panel-content">

			<% if (encounter) { %>
			${ ui.includeFragment("kenyaemr", "form/viewHtmlForm", [ encounter: encounter ]) }
			<% } else { %>
			<em>Not filled out</em>
			<% } %>

		</div>
	</div>

	<% } else if (section == "moh257") { %>

	${ ui.includeFragment("kenyaemr", "moh257", [ patient: currentPatient ]) }

	<%} %>

</div>