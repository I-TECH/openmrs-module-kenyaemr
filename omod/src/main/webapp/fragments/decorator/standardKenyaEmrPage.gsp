<%
	ui.includeCss("uilibrary", "jquery-ui.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");
	
	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("uilibrary", "jquery-ui.js")
	ui.includeJavascript("kenyaemr", "jquery-ui-timepicker-addon-mod.js")
	
	ui.includeJavascript("uilibrary", "uiframework.js")
	ui.includeJavascript("kenyaemr", "kenyaemr.js")

	if (config.patient) {
		config.context = "patientId=${ patient.id }"
	}

	config.beforeContent = ui.includeFragment("kenyaemr", "pageHeader", config)
	config.beforeContent += ui.includeFragment("kenyaemr", "pageAppHeader", config)

	if (config.patient) {
		config.beforeContent += ui.includeFragment("kenyaemr", "selectedPatientHeader", [ closeChartUrl: config.closeChartUrl ])
	}
	if (config.visit) {
		config.beforeContent += ui.includeFragment("kenyaemr", "selectedVisitHeader", [ visit: config.visit ])
	}
	
	ui.decorateWith("uilibrary", "standardPage", config)
%>

<!-- Override content layout from uilibrary based on the layout config value -->

<style type="text/css">
<% if (config.layout == "sidebar") { %>
	html {
		background: #FFF url('${ ui.resourceLink("kenyaemr", "images/background.png") }') repeat-y;
	}
	#content {
		margin: 0;
		padding: 0;
		position: relative;
	}
	#content-side {
		width: 320px;
		position: absolute;
		padding: 5px;
		overflow: auto;
	}
	#content-main {
		margin-left: 330px;
		padding: 5px;
		overflow: auto;
	}
<% } else { %>
	#content {
		margin: 0;
		padding: 5px;
	}
<% } %>
	.loading-placeholder {
		background-image: url('${ ui.resourceLink("kenyaemr", "images/loading.gif") }');
	}
	.field-label {
		font-size: 12px;
	}
	.toast-item {
		background-color: #464640;
		border-radius: 3px;
		border: 0;
	}
</style>

<script type="text/javascript">
jq(function() {
	/**
	 * Add icons to edit links in panel frames
	 */
	jq('.panel-frame .panel-editlink').each(function() {
		jq(this).prepend('<img src="${ ui.resourceLink("kenyaemr", "images/edit.png") }" /> ');
	});
});
</script>

<%= config.content %>