<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: patient, closeChartUrl: ui.pageLink("kenyaemr", "home") ])
%>
<div>
	<% apps.eachWithIndex { app, i ->
		def onClick = "location.href='/" + contextPath + "/" + app.homepageUrl + (patient ? ("?patientId=" + patient.id) : "") + "'"
		def iconUrlTokens = app.iconUrl.split(":")
		def iconProvider, icon
		if (iconUrlTokens.length == 2) {
			iconProvider = iconUrlTokens[0]
			icon = iconUrlTokens[1]

			// Strip the images/ part
			if (icon.startsWith("images/")) {
				icon = icon.substring(7);
			}
		}
	%>
	<div style="float: left; margin: 7px;" >
		${ ui.includeFragment("kenyaui", "widget/appButton", [ label: app.label, iconProvider: iconProvider, icon: icon, onClick: onClick ]) }
	</div>
	<% } %>
</div>