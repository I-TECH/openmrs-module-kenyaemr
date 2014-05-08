<%
	ui.decorateWith("kenyaui", "panel", [ heading: definition.name ])

	def formatData = { result -> (result != null) ? result : "-" }

	def instanceOf = { obj, clazz -> obj.class.name == clazz }
%>
<script type="text/javascript">
	function showCohortDialog(dataset, column) {
		var contentUrl = ui.pageLink('kenyaemr', 'dialog/cohortDialog', { appId: '${ currentApp.id }', request: '${ reportRequest.id }', dataset: dataset, column: column });

		kenyaui.openDynamicDialog({ heading: 'View Cohort', url: contentUrl, width: 90, height: 90, scrolling: true });
	}
</script>

<div class="ke-panel-content">
	<% if (definition.description) { %>
	<fieldset>
		<legend>Description</legend>
		${ definition.description }
	</fieldset>
	<% } %>

	<fieldset>
		<legend>Parameters</legend>
		<% definition.parameters.each { %>
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: it.label, value: reportData.context.parameterValues[it.name] ]) }
		<% } %>
	</fieldset>

	<%
	reportData.dataSets.each {
		def ds = it.value
		def dsName = it.key
		def dsd = definition.dataSetDefinitions[dsName].parameterizable
	%>

	<fieldset>
		<legend>${ dsd.name }${ dsd.description ? (': ' + dsd.description) : '' }</legend>
		<table class="ke-table-vertical">
			<tbody>
			<% ds.metaData.columns.each { col ->
				def colVal = ds.data.columnValues[col]
				def hasCohort = instanceOf(colVal, 'org.openmrs.Cohort') || instanceOf(colVal, 'org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult')
			%>
			<tr>
					<td><strong>${ col.name }</strong>&nbsp;&nbsp;${ col.label }</td>
					<td style="text-align: center">
						<% if (hasCohort) { %>
						<a href="javascript:showCohortDialog('${ dsName }', '${ col.name }')">${ formatData(colVal) }</a>
						<% } else { %>
						${ formatData(colVal) }
						<% } %>
					</td>
				</tr>
			<% } %>
			</tbody>
		</table>
	</fieldset>

	<% } %>
</div>