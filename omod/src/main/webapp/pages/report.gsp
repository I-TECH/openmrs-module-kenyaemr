<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems =  [
			[ iconProvider: "kenyaui", icon: "buttons/report_generate.png", label: "Request Report", href: "javascript:requestReport()" ],
			[ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: returnUrl ]
	]

	def requestIsComplete = { request -> request.status.ordinal() >= 4 }

	def evaluationTime = { request ->
		(request.evaluateCompleteDatetime ?: new Date()) - (request.evaluateStartDatetime ?: new Date())
	}
%>
<script type="text/javascript">
	var requestDialogContent = null;

	jq(function(){
		requestDialogContent = jq('#request-dialog-content').html();
		jq('#request-dialog-content').remove();
	});

	function requestReport() {
		kenyaui.openPanelDialog({ heading: 'Request report', content: requestDialogContent });

		jq('#request-report-ok').click(function() {
			var params = { appId: '${ currentApp.id }', reportId: '${ report.id }' };

			<% if (isIndicator) { %>
			params.startDate = jq('#request-startdate').val();
			<% } %>

			ui.getFragmentActionAsJson('kenyaemr', 'report/reportUtils', 'requestReport', params, function() {
				ui.reloadPage();
			});
		});
		jq('#request-report-cancel').click(function() {
			kenyaui.closeDialog();
		});
	}
</script>
<div class="ke-page-sidebar">

	<div class="ke-panel-frame" id="end-of-day">
		<div class="ke-panel-heading">Tasks</div>
		<% menuItems.each { item -> %>
			${ ui.includeFragment("kenyaui", "widget/panelMenuItem", item) }
		<% } %>
	</div>
</div>

<div class="ke-page-content">
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Summary</div>
		<div class="ke-panel-content">
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Name", value: definition.name ]) }
			<% if (definition.description) { %>
			${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Description", value: definition.description ]) }
			<% } %>
		</div>
	</div>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Requests</div>
		<div class="ke-panel-content">
			<% if (requests) { %>
			<table class="ke-table-vertical">
				<thead>
					<tr>
						<th>Requested</th>
						<th>By</th>
						<th>Status</th>
						<th>Time taken</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<% requests.each { request -> %>
					<tr>
						<td>${ kenyaUi.formatDateTime(request.requestDate) }</td>
						<td>${ kenyaUi.formatUser(request.requestedBy) }</td>
						<td>${ ui.format(request.status) }</td>
						<td>${ kenyaUi.formatDuration(evaluationTime(request)) }</td>
						<td><% if (requestIsComplete(request)) { %><a href="${ ui.pageLink("kenyaemr", "reportView", [ appId: currentApp.id, request: request.id, returnUrl: ui.thisUrl() ]) }">View</a><% } %></td>
					</tr>
				</tbody>
				<% } %>
			</table>
			<% } else { %>
				<i>None</i>
			<% } %>
		</div>
	</div>
</div>

<div id="request-dialog-content" style="display: none">
	<div class="ke-panel-content">
		<div>Report: <strong>${ ui.format(definition.name) }</strong></div>
		<% if (isIndicator) { %>
		<div>Period: ${ ui.includeFragment("kenyaui", "widget/selectList", [
				id: "request-startdate",
				formFieldName: "startDate",
				selected: startDateSelected,
				options: startDateOptions,
				optionsValueField: "key",
				optionsDisplayField: "value"
		]) }
		</div>
		<% } %>
	</div>
	<div class="ke-panel-footer">
		<div class="ke-button" id="request-report-ok"><div class="ke-button-text"><div class="ke-label">OK</div></div></div>
		<div class="ke-button" id="request-report-cancel"><div class="ke-button-text"><div class="ke-label">Cancel</div></div></div>
	</div>
</div>