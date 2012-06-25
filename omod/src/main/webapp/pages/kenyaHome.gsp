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
		width: 10em;
		height: 7em;
	}
		
	#homepage-logo {
		float: left;
		margin-right: 1em;
	}
	
	#homepage-welcome-message {
		font-size: 1.2em;
		font-weight: bold;
		padding-top: 1em;
	}
	
	#homepage-apps {
		clear: both;
		text-align: center;
		padding-top: 0.5em;
	}
	
	#homepage-context {
		position: fixed;
		bottom: 0;
		right: 0;
		padding: 0.5em;
		border: 1px grey solid;
	}
	#homepage-context .ui-icon {
		position: absolute;
		top: 0;
		right: 0;
	}
</style>

<img id="homepage-logo" src="${ ui.resourceLink("kenyaemr", "images/logo.png") }"/>

<div id="homepage-welcome-message">
	Hello ${ ui.format(context.authenticatedUser) }, welcome to the Kenya OpenMRS EMR.
</div>

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

<% if (patient || currentApp) { %>
	<div id="homepage-context">
		<% if (patient) { %>
			Patient: <b>${ ui.format(patient) }</b> <br/>
		<% } %>
		<% if (currentApp) { %>
			Most Recent App:
			<% if (currentApp.app.tinyIconUrl) { %>
				<img src="/${ contextPath }/${ currentApp.app.tinyIconUrl }"/>
			<% } %>
			<a href="/${ contextPath }/${ currentApp.app.homepageUrl }">
				${ currentApp.app.label }
			</a>
			<br/>
		<% } %>
		
		<a href="?clearContext=true" class="ui-icon ui-icon-closethick">Clear</a>
	</div>
<% } %>