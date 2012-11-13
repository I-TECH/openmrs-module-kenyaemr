<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	<div class="panel-menu">
		<div class="panel-heading">Find a patient</div>
		<div class="panel-content">
			${ ui.includeFragment("kenyaemr", "patientSearchForm", [ defaultWhich: "all" ]) }
		</div>
	</div>

	<div class="panel-menu" style="text-align: center">
		${ ui.includeFragment("uilibrary", "widget/button", [
				id: "create-patient-button",
				iconProvider: "kenyaemr",
				icon: "buttons/patient_add.png",
				label: "Create New Patient Record",
				extra: "Patient does not exist yet",
				href: ui.pageLink("kenyaemr", "registrationCreatePatient") ])
		}
	</div>
</div>

<div id="content-main">
	${ ui.includeFragment("kenyaemr", "patientList", [
			id: "results",
			showNumResults: true,
			page: "medicalChartViewPatient"
	]) }
</div>

<script type="text/javascript">
	subscribe("patientSearch/results", function(event, data) {
		publish("results/show", data);
	});
	jq(function() {
		jq('input[name=q]').focus();
		// if the user goes back to this page in their history, redo the ajax query
		publish('patientSearch/changed');
	});
</script>
