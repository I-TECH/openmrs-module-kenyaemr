<%
	ui.includeCss("kenyaui", "jquery-ui.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");
	
	ui.includeJavascript("kenyaui", "jquery.js")
	ui.includeJavascript("kenyaui", "jquery-ui.js")
	
	ui.includeJavascript("kenyaui", "uiframework.js")
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
	
	ui.decorateWith("kenyaui", "standardPage", config)
%>

<!-- Override content layout from uilibrary based on the layout config value -->

<style type="text/css">

<% if (config.layout == "sidebar") { %>
	html {
		background: #FFF url('${ ui.resourceLink("kenyaui", "images/background.png") }') repeat-y;
	}
	#content {
		padding: 0;
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
<% } %>

	.loading-placeholder {
		background-image: url('${ ui.resourceLink("kenyaui", "images/loading.gif") }');
	}

	/**
	 * Override styles for toasts
	 */
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
		jq(this).prepend('<img src="${ ui.resourceLink("kenyaui", "images/edit.png") }" /> ');
	});

	jq('button .label').css('font-size', '14px');
	jq('button .extra').css('font-size', '11px').css('font-weight', 'normal');
});
</script>

<%= config.content %>