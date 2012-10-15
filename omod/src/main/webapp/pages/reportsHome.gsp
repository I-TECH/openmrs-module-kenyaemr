<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<style>
	#moh-reports, #facility-reports {
		float: left;
	}
</style>

<fieldset id="moh-reports">
	<legend> Ministry of Health Reports </legend>
	
	<% mohReports.each { %>
		${ ui.includeFragment("widget/button", [
				iconProvider: "uilibrary",
				icon: "chart_32.png",
				label: it.name,
				href: ui.pageLink("reportsRunMonthlyIndicatorReport", [ manager: it.manager ])
			]) }
	<% } %>
</fieldset>

<fieldset id="facility-reports">
	<legend> Facility Reports </legend>

	${ ui.includeFragment("widget/button", [
			iconProvider: "uilibrary",
			icon: "clock_32.png",
			label: "Today's Scheduled Visits",
			href: ui.pageLink("dailySchedule")
		]) }
	<br/>
	
	<% patientAlertReports.each { %>
		${ ui.includeFragment("widget/button", [
				iconProvider: "uilibrary",
				icon: "user_warning_32.png",
				label: it.name,
				href: ui.pageLink("reportsRunPatientAlertListReport", [ manager: it.manager ])
			]) }
		<br/>
	<% } %>
	
	<%
		/* Below here are placeholders that should be removed when these
		reports are implemented */
	%>
	
</fieldset>

<fieldset id="patient-specific-reports">
<legend> Patient Specific Reports </legend>
	<% patientSummaryReports.each { %>
		${ ui.includeFragment("widget/button", [
					iconProvider: "uilibrary",
					icon: "user_warning_32.png",
					label: it.name
		]) }
		<br/>
	<% } %>

</fieldset>