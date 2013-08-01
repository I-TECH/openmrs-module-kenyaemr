<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient, layout: "sidebar" ])

	def menuItems = [
			[
					label: "Overview",
					href: ui.pageLink("kenyaemr", "chart/chartViewPatient", [ patientId: patient.id, section: "overview" ]),
					active: (selection == "section-overview"),
					iconProvider: "kenyaui",
					icon: "buttons/patient_overview.png"
			],
	        [
					label: "MOH 257",
					href: ui.pageLink("kenyaemr", "chart/chartViewPatient", [ patientId: patient.id, section: "moh257" ]),
					active: (selection == "section-moh257"),
					iconProvider: "kenyaui",
					icon: "forms/moh257.png"
			]
	];

	oneTimeForms.each { form ->
		menuItems << [
				label: form.name,
				href: ui.pageLink("kenyaemr", "chart/chartViewPatient", [ patientId: patient.id, formUuid: form.formUuid ]),
				active: (selection == "form-" + form.formUuid),
				iconProvider: form.iconProvider,
				icon: form.icon,
		]
	}

	programs.each { program ->
		menuItems << [
				label: ui.format(program.target),
				extra: programSummaries[program.target],
				href: ui.pageLink("kenyaemr", "chart/chartViewPatient", [ patientId: patient.id, programId: program.target.id ]),
				active: (selection == "program-" + program.target.id)
		]
	}
%>
<div class="ke-page-sidebar">

	<div class="ke-panel-frame">
		<% menuItems.each { item -> print ui.includeFragment("kenyaui", "widget/panelMenuItem", item) } %>
	</div>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Visits</div>

		<% if (!visits) {
			print ui.includeFragment("kenyaui", "widget/panelMenuItem", [
				label: ui.message("general.none"),
			])
		}
		else {
			visits.each { visit ->
				print ui.includeFragment("kenyaui", "widget/panelMenuItem", [
						label: ui.format(visit.visitType),
						href: ui.pageLink("kenyaemr", "chart/chartViewPatient", [ patientId: patient.id, visitId: visit.id ]),
						extra: kenyaEmrUi.formatVisitDates(visit),
						active: (selection == "visit-" + visit.id)
				])
			}
		} %>
	</div>

</div>

<div class="ke-page-content">

	<% if (visit) { %>

		<% if (visit.voided) { %>
			<div class="ke-warning">This visit has been voided</div>
		<% } %>

		<script type="text/javascript">
			function onVoidVisit(visitId) {
				kenyaui.openConfirmDialog({
					heading: 'KenyaEMR',
					message: '${ ui.message("kenyaemr.confirmVoidVisit") }',
					okCallback: function() { doVisitVoid(visitId); }
				});
			}

			function doVisitVoid(visitId) {
				ui.getFragmentActionAsJson('kenyaemr', 'emrUtils', 'voidVisit', { visitId: visitId, reason: 'Data entry error' }, function() {
					ui.reloadPage();
				});
			}
		</script>

		${ ui.includeFragment("kenyaemr", "visitSummary", [ visit: visit ]) }
		${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: visit ]) }
		${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: visit ]) }

		<% if (context.hasPrivilege("Delete Visits") && !visit.voided) { %>
		<div class="ke-panel-frame" style="text-align: center">
			<% if (!visit.encounters) { %>
			${ ui.includeFragment("kenyaui", "widget/button", [
					label: "Void Visit",
					extra: "If entered by mistake",
					iconProvider: "kenyaui",
					icon: "buttons/visit_void.png",
					onClick: "onVoidVisit(" + visit.id + ")"
			]) }
			<% } else { %>
			<em>To void this visit, please delete all encounters first</em>
			<% } %>
		</div>
		<% } %>

	<% } else if (form) { %>

		<div class="ke-panel-frame">
			<div class="ke-panel-heading">${ ui.format(form) }</div>
			<div class="ke-panel-content">

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

		${ ui.includeFragment("kenyaemr", "program/programHistory", [ patient: patient, program: program, showClinicalData: true ]) }

	<% } else if (section == "overview") { %>

		${ ui.includeFragment("kenyaemr", "program/programCarePanels", [ patient: patient, complete: true, activeOnly: false ]) }

	<% } else if (section == "moh257") { %>

		${ ui.includeFragment("kenyaemr", "moh257", [ patient: patient ]) }

	<% } %>

</div>

${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
${ ui.includeFragment("kenyaemr", "dialogSupport") }