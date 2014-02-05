<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Profiling", frameOnly: true ]);

	def currentLogLevel = org.apache.log4j.LogManager.getLogger("org.openmrs.module.reporting.evaluation.EvaluationProfiler").getLevel()
	def reportProfilingEnabled = org.apache.log4j.Level.TRACE.equals(currentLogLevel)
%>
<script type="text/javascript">
	jQuery(function() {
		jQuery('#report-profiling-enable').click(function() {
			ui.getFragmentActionAsJson('kenyaemr', 'developer/developerUtils', 'enableReportProfiling', {}, function() {
				ui.reloadPage();
			});
		});
		jQuery('#report-profiling-disable').click(function() {
			ui.getFragmentActionAsJson('kenyaemr', 'developer/developerUtils', 'disableReportProfiling', {}, function() {
				ui.reloadPage();
			});
		});
	});
</script>

<div class="ke-panel-content">
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Report evaluation profiling", value: reportProfilingEnabled ? "ON" : "OFF" ]) }
</div>

<div class="ke-panel-controls">
	<% if (!reportProfilingEnabled) { %>
	<button id="report-profiling-enable"><img src="${ ui.resourceLink("images/glyphs/enable.png") }" /> Enable</button>
	<% } else { %>
	<button id="report-profiling-disable"><img src="${ ui.resourceLink("images/glyphs/disable.png") }" /> Disable</button>
	<% } %>
</div>