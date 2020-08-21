<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ label: "View medic Queue", iconProvider: "kenyaemr", icon: "queue-icon.jpg", label: "View Medic Queue", href: ui.pageLink("kenyaemr", "hivTesting/medicQueueHome") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaemr", "patient/patientSearchForm", [ defaultWhich: "all" ]) }
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Medic Queue", items: menuItems ]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/patientSearchResults", [ pageProvider: "kenyaemr", page: "hivTesting/htsViewPatient" ]) }
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('input[name="query"]').focus();
	});
</script>
