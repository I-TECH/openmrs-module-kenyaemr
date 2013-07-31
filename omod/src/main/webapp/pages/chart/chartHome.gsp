<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
		heading: "Tasks",
		items: [
			[ iconProvider: "kenyaui", icon: "buttons/patient_search.png", label: "Search for a Patient", href: ui.pageLink("kenyaemr", "chart/chartSearch") ]
		]
	]) }
</div>

<div id="content-main">
	${ ui.includeFragment("kenyaemr", "patient/patientList", [ id: "recentlyViewedPatients", page: "chart/chartViewPatient", heading: "Recently Viewed Patients" ]) }
</div>


<script type="text/javascript">
	getJsonAsEvent(ui.fragmentActionLink('kenyaemr', 'chartUtil', 'recentlyViewed'), 'recentlyViewedPatients/show');
</script>