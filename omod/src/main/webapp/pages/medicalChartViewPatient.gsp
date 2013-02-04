<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient, layout: "sidebar" ])

	def menuItems = [
			[
					label: "Overview",
					href: ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, section: "overview" ]),
					active: (selection == "section-overview"),
					iconProvider: "kenyaemr",
					icon: "buttons/patient_overview.png"
			],
	        [
					label: "MOH 257",
					href: ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, section: "moh257" ]),
					active: (selection == "section-moh257"),
					iconProvider: "kenyaemr",
					icon: "buttons/form_moh257.png"
			]
	];

	oneTimeForms.each { form ->
		menuItems << [
				label: form.label,
				href: ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, formUuid: form.formUuid ]),
				active: (selection == "form-" + form.formUuid),
				iconProvider: form.iconProvider,
				icon: form.icon,
		]
	}

	programs.each { prog ->
		def extra = "from " + kenyaEmrUi.formatDateNoTime(prog.dateEnrolled)
		if (prog.dateCompleted)
			extra += " to " + kenyaEmrUi.formatDateNoTime(prog.dateCompleted)
		if (prog.outcome)
			exta += "<br />Outcome: <b>" + ui.format(prog.outcome) + "</b>"

		menuItems << [
				label: ui.format(prog.program),
				extra: extra,
				href: ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, patientProgramId: prog.id ]),
				active: (selection == "program-" + prog.id)
		]
	}
%>
<div id="content-side">

	<div class="panel-frame">
		<% menuItems.each { item -> print ui.includeFragment("kenyaemr", "widget/panelMenuItem", item) } %>
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
				def visitType = ui.format(visit.visitType);
				if (kenyaEmrUi.isRetrospectiveVisit(visit)) {
					visitType += " [RE]"
				}
				if (visit.stopDatetime) {
					extra += " to " + ui.format(visit.stopDatetime)
				}

				print ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
						label: visitType,
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

	<% } else if (section == "overview") { %>

		${ ui.includeFragment("kenyaemr", "medicalChartPatientOverview", [ patient: patient ]) }

	<% } else if (section == "moh257") { %>

		${ ui.includeFragment("kenyaemr", "medicalChartMoh257", [ patient: patient ]) }

	<% } %>

</div>

<% if (visit) { %>

	${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }

	${ ui.includeFragment("kenyaemr", "dialogSupport") }

<% } %>