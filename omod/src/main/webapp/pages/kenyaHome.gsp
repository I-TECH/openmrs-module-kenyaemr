<%
	ui.decorateWith("standardKenyaEmrPage", [ doNotShowApp: true ])
	def APPS_PER_ROW = 3;
%>

<style>
	.app-button {
		border: 1px black solid;
		background-color: #e0e0e0;
		padding: 0.3em;
		margin: 0.5em;
		float: left;
		text-align: center;
		width: 7.5em;
		height: 5.5em;
	}
		
	#homepage-welcome-message {
		font-size: 1.1em;
		font-weight: bold;
		padding-top: 1em;
	}
	
	#homepage-apps {
		text-align: center;
		padding-top: 0.5em;
	}
	
	#homepage-context {
		position: relative;
		margin: 1em;
		padding: 0.5em;
		border: 1px grey solid;
		width: 30em;
		background-color: lightyellow;
	}
	#homepage-context .ui-icon {
		position: absolute;
		top: 0;
		right: 0;
	}
</style>

<div id="homepage-welcome-message">
	Hello ${ ui.format(context.authenticatedUser) }, welcome to the Kenya OpenMRS EMR.
</div>

<% if (patient || currentApp) { %>
	<div id="homepage-context">
		<% if (patient) { %>
			Selected:
			<img width="32" height="32" src="${ ui.resourceLink("uilibrary", "images/patient_" + patient.gender + ".gif") }"/>
			<b>${ ui.format(patient) }</b>
			<br/>
		<% } %>
		<% if (currentApp) { %>
			Back to:
			<a href="/${ contextPath }/${ currentApp.app.homepageUrl }<% if (patient) { %>?patientId=${ patient.id }<% } %>">
				<img width="16" height="16" src="${ ui.resourceLink("uilibrary", "images/arrow_left_16.png") }"/>
				<% if (currentApp.app.tinyIconUrl) { %>
					<img src="/${ contextPath }/${ currentApp.app.tinyIconUrl }"/>
				<% } %>
				${ currentApp.app.label }
			</a>
			<br/>
		<% } %>
		
		<a href="?clearContext=true" class="ui-icon ui-icon-closethick">Clear</a>
	</div>
<% } %>

<div id="homepage-apps">	
	<% apps.eachWithIndex { app, i -> %>
		<div class="app-button" <% if (i % APPS_PER_ROW == 0) { %> style="clear: left;" <% } %>>
			<a href="/${ contextPath }/${ app.homepageUrl }<% if (patient) { %>?patientId=${ patient.id }<% } %>">
				<% if (app.iconUrl) { %>
					<img class="app-icon" src="/${ contextPath }/${ app.iconUrl }" height="64"/><br/>
				<% } %>
				<span class="app-label">
					${ app.label }
				</span>
			</a>
		</div>
	<% } %>
</div>