<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient ])
%>

<div class="ke-page-content">

	${ ui.includeFragment("kenyaui", "widget/tabMenu", [ items: [
			[ label: "Overview", tabid: "overview" ],
			[ label: "Lab Tests", tabid: "labtests" ],
			[ label: "Prescriptions", tabid: "prescriptions" ]
	] ]) }

	<div class="ke-tab" data-tabid="overview">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td width="40%" valign="top">
					${ ui.includeFragment("kenyaemr", "patientSummary", [ patient: patient ]) }
					${ ui.includeFragment("kenyaemr", "program/programHistories", [ patient: patient, showClinicalData: true ]) }
				</td>
				<td width="60%" valign="top" style="padding-left: 5px">
					${ ui.includeFragment("kenyaemr", "visitMenu", [ patient: patient, visit: visit, allowCheckIn: false, allowCheckOut: true ]) }

					${ ui.includeFragment("kenyaemr", "program/programCarePanels", [ patient: patient, complete: false, activeOnly: true ]) }

					<% if (visit) { %>
					${ ui.includeFragment("kenyaemr", "visitAvailableForms", [ visit: visit ]) }
					${ ui.includeFragment("kenyaemr", "visitCompletedForms", [ visit: visit ]) }
					<% } %>
				</td>
			</tr>
		</table>
	</div>

	<div class="ke-tab" data-tabid="labtests">
		${ ui.includeFragment("kenyalab", "patientLabTests", [ patient: patient ]) }
	</div>
	<div class="ke-tab" data-tabid="prescriptions">
		${ ui.includeFragment("kenyaemr", "prescription/patientPrescriptions", [ patient: patient ]) }
	</div>

</div>

<% if (visit) { %>
	${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
	${ ui.includeFragment("kenyaemr", "dialogSupport") }
<% } %>