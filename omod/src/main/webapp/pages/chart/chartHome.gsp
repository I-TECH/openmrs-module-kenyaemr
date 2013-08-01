<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
		heading: "Tasks",
		items: [
			[ iconProvider: "kenyaui", icon: "buttons/patient_search.png", label: "Search for a Patient", href: ui.pageLink("kenyaemr", "chart/chartSearch") ]
		]
	]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/patientList", [ id: "recentlyViewedPatients", page: "chart/chartViewPatient", heading: "Recently Viewed Patients" ]) }
</div>


<script type="text/javascript">
	getJsonAsEvent(ui.fragmentActionLink('kenyaemr', 'emrUtils', 'recentlyViewed'), 'recentlyViewedPatients/show');
</script>