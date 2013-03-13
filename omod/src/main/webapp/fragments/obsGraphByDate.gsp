<%
	ui.includeJavascript("kenyaui", "jquery.js") // force this include to be first
	ui.includeJavascript("kenyaui", "highcharts.js")

	config.require("id")
	config.require("concepts")
%>

<script type="text/javascript">

var obsData = {
<% concepts.each { concept -> %>
	${ concept.conceptId }: [ <% data[concept].each { obs -> print "[" + obs.obsDatetime.time + ", " + obs.valueNumeric + "], " } %> ],
<% } %>
};

jq(function() {

	var plotStyles = ['longdash', 'shortdot', 'solid'];
	var emrTextStyle = {
		fontFamily: '"Lucida Grande", "Lucida Sans", Arial, sans-serif',
		color: "#000"
	};

	var chart = new Highcharts.Chart({
		chart: { renderTo: '${ config.id }' },
		xAxis: {
			type: 'datetime',
			labels: {
				style: emrTextStyle
			}
		},
		yAxis: [
			<%
			def conceptNum = 0;
			concepts.each { concept ->
				def conceptName = ui.format(concept)
				def axisTitle = config.showUnits && !conceptName.endsWith(")") ? (conceptName + " (" + concept.units + ")") : conceptName
			%>
			{
				title: {
					text: '${ ui.escapeHtml(axisTitle) }',
					style: emrTextStyle
				},
				labels: {
					style: emrTextStyle
				},
				opposite: ${ conceptNum > 0 ? "true" : "false" },
				min: 0
			},
			<%
			    conceptNum++
			}
			%>
		],
		series: [
			<%
			conceptNum = 0;
			concepts.each { concept ->
			%>
			{
				name: '${ ui.escapeHtml(ui.format(concept)) }',
				dashStyle: plotStyles[${ conceptNum }],
				data: obsData[${ concept.conceptId }],
				yAxis: ${ conceptNum }
			},
			<%
			    conceptNum++
			}
			%>
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

<div id="${ config.id }" style="${ config.style ? config.style : "" }"></div>
