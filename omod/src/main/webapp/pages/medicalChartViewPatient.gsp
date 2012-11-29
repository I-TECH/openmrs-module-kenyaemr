<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient, layout: "sidebar" ])

	//def kenyaEmrWebUtils = context.loadClass("org.openmrs.module.kenyaemr.KenyaEmrUiUtils")
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

		<div class="panel-frame">
			<div class="panel-heading">Visit Summary</div>
			<div class="panel-content">
				Type: <b>${ ui.format(visit.visitType) }</b><br />
				Location: <b>${ ui.format(visit.location) }</b><br />
				From <b>${ ui.format(visit.startDatetime) }</b> <% if (visit.stopDatetime) { %> to <b>${ ui.format(visit.stopDatetime) }</b> <% } %>
			</div>
		</div>

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

	<% } else {
		def cs = context.conceptService
		def conceptList = [ cs.getConcept(5089), cs.getConcept(5497) ] // Weight and CD4
	%>

		<div class="panel-frame">
			<div class="panel-heading">CD4/Weight</div>
			<div class="panel-content">
				<div style="float: left; width: 49%">
					${ ui.includeFragment("kenyaemr", "obsTableByDate", [ id: "tblhistory", concepts: conceptList ]) }
				</div>
				<div style="float: right; width: 49%">
					${ ui.includeFragment("kenyaemr", "obsGraphByDate", [ id: "cd4graph", concepts: conceptList, showUnits: true, style: "height: 300px" ]) }
				</div>
			</div>
		</div>
	<% } %>

</div>

<% if (visit) { %>

${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }

${ ui.includeFragment("kenyaemr", "dialogSupport") }

<% } %>