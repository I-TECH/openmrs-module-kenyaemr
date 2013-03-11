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
	/**
	 * Override styles for widget/button
	 */
	input[type="button"], input[type="submit"], input[type="reset"], button {
		text-align: center;
		cursor: pointer;
		padding: 4px;
		color: #444;
		border-top: 0px;
		border-bottom: 1px #BBB solid;
		border-left: 0px;
		border-right: 1px #BBB solid;
		border-radius: 3px;
		background-color: #e0e0e0;
		font-weight: bold;
		font-size: 14px;
	}
	input[type="button"]:hover, input[type="submit"]:hover, input[type="reset"]:hover, button:hover {
		background-color: #E9E9E9;
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

	jq('button .label').css('font-size', '14px');
	jq('button .extra').css('font-size', '11px').css('font-weight', 'normal');
});
</script>

<%= config.content %>