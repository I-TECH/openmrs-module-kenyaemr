<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient, layout: "sidebar" ])
%>
<div id="content-side">

	<div class="panel-frame">
		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
			iconProvider: "kenyaemr",
			icon: "buttons/patient_overview.png",
			label: "Overview",
			href: ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id ]),
			active: (selection == "overview")
		]) }

		<% oneTimeForms.each { form ->
			print ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
				iconProvider: form.iconProvider,
				icon: form.icon,
				label: form.label,
				href: ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, formUuid: form.formUuid ]),
				active: (selection == "form-" + form.formUuid)
			])
		} %>

		<% programs.each { prog ->
			def extra = "from " + kenyaEmrUi.formatDateNoTime(prog.dateEnrolled)
			if (prog.dateCompleted)
				extra += " to " + kenyaEmrUi.formatDateNoTime(prog.dateCompleted)
			if (prog.outcome)
				exta += "<br />Outcome: <b>" + ui.format(prog.outcome) + "</b>"

			print ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
				label: ui.format(prog.program),
				href: ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, patientProgramId: prog.id ]),
				extra: extra,
				active: (selection == "program-" + prog.id)
			])
		} %>
	</div>

	<div class="panel-frame">
		<div class="panel-heading">Visits</div>

		<% if (!visits) {
			print ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
				label: ui.message("general.none"),
			])
		}
		else {
			visits.each { visit ->
				def extra = "from " + ui.format(visit.startDatetime)
				if (visit.stopDatetime)
					extra += " to " + ui.format(visit.stopDatetime)

				print ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
						label: ui.format(visit.visitType),
						href: ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, visitId: visit.id ]),
						extra: extra,
						active: (selection == "visit-" + visit.id)
				])
			}
		} %>
	</div>

</div>

<div id="content-main">

	<% if (visit) { %>
		${ ui.includeFragment("kenyaemr", "visitSummary", [ visit: visit ]) }
		${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: visit ]) }
		${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: visit ]) }
	<% } else if (form) { %>

		<div class="panel-frame">
			<div class="panel-heading">${ ui.format(form) }</div>
			<div class="panel-content">

				<% if (encounter) { %>
					${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm" ]) }

					<script type="text/javascript">
						jq(function() {
							publish('showHtmlForm/showEncounter', { encounterId: ${ encounter.id } });
						});
					</script>
				<% } else { %>
					<i>Not Filled Out</i>
				<% } %>

			</div>
		</div>

	<% } else if (program) { %>

		${ ui.includeFragment("kenyaemr", "medicalChartPatientProgram", [ patientProgram: program ]) }

	<% } else { %>

		${ ui.includeFragment("kenyaemr", "medicalChartPatientOverview") }

	<% } %>

</div>

<% if (visit) { %>

	${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }

	${ ui.includeFragment("kenyaemr", "dialogSupport") }

<% } %>