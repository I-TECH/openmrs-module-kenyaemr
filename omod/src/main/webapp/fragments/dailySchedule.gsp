<%
	def heading = "Scheduled for "
	if (isToday)
		heading += "Today"
	else if (isTomorrow)
		heading += "Tomorrow"
	else if (isYesterday)
		heading += "Yesterday"
	else
		heading += kenyaUi.formatDate(date)

	config.require("page")
%>

${ ui.includeFragment("kenyaemr", "patient/patientList", [ id: "dailySchedule", pageProvider: "kenyaemr", page: config.page, heading: heading ]) }

<script type="text/javascript">
	getJsonAsEvent(ui.fragmentActionLink('kenyaemr', 'dailySchedule', 'scheduledPatients', { date: '${ dateYmd }' }), 'dailySchedule/show');
</script>