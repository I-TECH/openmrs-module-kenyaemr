<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, closeChartUrl: ui.pageLink("kenyaemr", "home") ])
%>
<div class="ke-page-content">
	<% apps.eachWithIndex { app, i ->
		def onClick = "ui.navigate('/" + contextPath + "/" + app.url + (currentPatient ? ("?patientId=" + currentPatient.id) : "") + "')"
		def iconTokens = app.icon != null ? app.icon.split(":") : null
		def iconProvider, icon
		if (iconTokens !=null && iconTokens.length == 2) {
			iconProvider = iconTokens[0]
			icon = "images/" + iconTokens[1]
		}
		if(icon != null) {
	%>
	<div style="float: left; margin: 5px;" >
		<button type="button" class="ke-app" onclick="${ onClick }"><img src="${ ui.resourceLink(iconProvider, icon) }" />${ app.label }</button>
	</div>
	<% }} %>
</div>