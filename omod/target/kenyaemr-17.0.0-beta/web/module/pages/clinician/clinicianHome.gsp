<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItemsNew = [
			[ label: "Seen Patients", iconProvider: "kenyaui", icon: "buttons/patients.png", href: ui.pageLink("kenyaemr", "clinician/clinicianSearchSeen") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaemr", "patient/patientSearchForm", [ defaultWhich: "checked-in" ]) }
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "", items: menuItemsNew ]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/patientSearchResults", [ pageProvider: "kenyaemr", page: "clinician/clinicianViewPatient" ]) }
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('input[name="query"]').focus();
	});
</script>