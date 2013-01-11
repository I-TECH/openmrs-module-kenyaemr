<%
	def dataPoints = []

	dataPoints << [ label: "Disease classification", value: calculations.tbDiseaseClassification ]
	dataPoints << [ label: "Patient classification", value: calculations.tbPatientClassification ]
%>

<% dataPoints.each { print ui.includeFragment("kenyaemr", "dataPoint", it) } %>