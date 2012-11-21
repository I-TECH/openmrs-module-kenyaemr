<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	<div class="panel-frame">
		<div class="panel-heading">Tasks</div>

		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
				iconProvider: "kenyaemr",
				icon: "buttons/patient_search.png",
				label: "Search for a Patient",
				href: ui.pageLink("kenyaemr", "medicalChartSearch")
		]) }
	</div>
</div>

<div id="content-main">
	<h2>Recently-viewed patients</h2>
	${ ui.includeFragment("kenyaemr", "patientList", [ id: "recentlyViewedPatients", page: "medicalChartViewPatient" ]) }
</div>


<script type="text/javascript">
	getJsonAsEvent(ui.fragmentActionLink('kenyaemr', 'medicalChartUtil', 'recentlyViewed'), 'recentlyViewedPatients/show');
</script>