<table cellpadding="10px">
	<tr>
		<td valign="top">
			<table class="decorated">
					<thead>
						<tr>
							<th></th>
							<% concepts.each { %>
								<th>
									${ ui.format(it) }
								</th>
							<% } %> 
						</tr>
					</thead>
					<tbody>
						<% if (!data) { %>
							<tr>
								<td></td>
								<td colspan="${ concepts.size() }">${ ui.message("general.none") }</td>
							</tr>
						<% } %>
						<% data.each { date, results -> %>
							<tr>
								<th>${ ui.format(date) }</th>
								<% concepts.each { concept -> %>
									<td>
										<%
											def obs = results[concept]
											if (obs) {
										%>
											${ ui.format(obs) }
										<% } %>
									</td>
								<% } %>
							</tr>
						<% } %>
					</tbody>
				</table>	
		</td>
		<td valign="top">
		<u><h3>Chart Summary of Weight and CD4</h3></u>
			<script>
				var chart;
				jq(function() {
				chart = new Highcharts.Chart({
					chart: {
					renderTo: 'container',
					defaultSeriesType: 'line'
					},
					 title: {
                text: 'Monthly Average Temperature',
                x: -20 //center
		            },
		            subtitle: {
		                text: 'Source: WorldClimate.com',
		                x: -20
		            },
		            xAxis: {
		                categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
		                    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
		            },
		            yAxis: {
		                title: {
		                    text: 'Temperature (°C)'
		                },
		                plotLines: [{
		                    value: 0,
		                    width: 1,
		                    color: '#808080'
		                }]
		            },
		            tooltip: {
		                formatter: function() {
		                        return '<b>'+ this.series.name +'</b><br/>'+
		                        this.x +': '+ this.y +'°C';
		                }
		            },
		            legend: {
		                layout: 'vertical',
		                align: 'right',
		                verticalAlign: 'top',
		                x: -10,
		                y: 100,
		                borderWidth: 0
		            },
							
						);
						alert('Stop disturbing me you code2');
		</script>	
			<div id="container" style="width: 100%; height: 500px; margin: 10 auto">chart goes here</div>
		</td>
</tr>			
</table>
