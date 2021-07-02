<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ label: "View Afyastat Queue", iconProvider: "kenyaemr", icon: "queue-icon.jpg", label: "View Afyastat Queue", href: ui.pageLink("afyastat", "afyastatHome") ]
	]
	def muzimaMenuItems = [
			[ label: "View mUzima Queue", iconProvider: "kenyaemr", icon: "queue-icon.jpg", label: "View mUzima Queue", href: ui.pageLink("kenyaemr", "hivTesting/muzimaQueueHome") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaemr", "patient/patientSearchForm", [ defaultWhich: "all" ]) }
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "mUzima Queue", items: muzimaMenuItems ]) }
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Afyastat Queue", items: menuItems ]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/patientSearchResults", [ pageProvider: "kenyaemr", page: "hivTesting/htsViewPatient" ]) }
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('input[name="query"]').focus();
	});
</script>
