<%
	ui.includeCss("kenyaemr", "referenceapplication.css", 100)
%>
<%
	def url = "kenyaemr/userHome.page"
	ui.decorateWith("kenyaemr", "standardPage")
	def onClick = "ui.navigate('/" + contextPath + "/" + url + "')"
%>
<script type="text/javascript" src="./moduleResources/kenyaemr/scripts/highcharts.js"></script>
${ ui.includeFragment("kenyaemr", "facilityDashboard/miniFacilityDashboard") }
