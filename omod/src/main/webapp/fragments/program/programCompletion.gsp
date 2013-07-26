<% if (encounter) { %>
${ ui.includeFragment("kenyaui", "widget/editButton", [
		href: ui.pageLink("kenyaemr", "editHtmlForm", [ encounterId: encounter.id, appId: currentApp.id, returnUrl: ui.thisUrl() ])
]) }
<% } %>
${ ui.includeFragment(summaryFragment.provider, summaryFragment.path, [ patientProgram: enrollment, encounter: encounter, showClinicalData: showClinicalData ] )}