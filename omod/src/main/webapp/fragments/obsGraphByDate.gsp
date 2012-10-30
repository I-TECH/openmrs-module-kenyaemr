<%
	ui.includeJavascript("uilibrary", "jquery.js") // force this include to be first
	ui.includeJavascript("highcharts.js")
%>

<script type="text/javascript">

var obsData = {
<% concepts.each { concept -> %>
	${ concept.conceptId }: [ <% data[concept].each { obs -> print "[" + obs.obsDatetime.time + ", " + obs.valueNumeric + "], " } %> ],
<% } %>
};

jq(function() {

	var plotStyles = ['longdash', 'shortdot'];
	var emrTextStyle = {
		fontFamily: '"Lucida Grande", "Lucida Sans", Arial, sans-serif',
		color: "#000"
	};
	var useOppositeYAxis = true;

	var chart = new Highcharts.Chart({
		chart: { renderTo: 'graph-container' },
		xAxis: { type: 'datetime' },
		yAxis: [
			<% concepts.each { concept -> %>
			{
				title: {
					text: '${ ui.format(concept) }',
					style: emrTextStyle
				},
				opposite: (useOppositeYAxis = !useOppositeYAxis),
				min: 0
			},
			<% } %>
		],
		series: [
			<%
			def conceptNum = 0;
			concepts.each { concept ->
			%>
			{
				name: '${ ui.format(concept) }',
				dashStyle: plotStyles[${ conceptNum++ }],
				data: obsData[${ concept.conceptId }]
			},
			<% } %>
		],
		legend: {
			itemStyle: emrTextStyle,
			symbolWidth: 50
		},
		title: { text: null },
		credits:{ enabled:false }
    });
});
</script>

<div id="graph-container" style="width: 500px; height: 300px"></div>
