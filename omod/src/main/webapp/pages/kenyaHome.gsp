<%
	ui.decorateWith("standardKenyaEmrPage", [ doNotShowApp: true, patient: patient, closeChartUrl: ui.pageLink("kenyaHome") ])
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
</style>

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