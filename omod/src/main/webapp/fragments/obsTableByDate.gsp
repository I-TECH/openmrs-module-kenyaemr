<%
    ui.includeJavascript("uilibrary", "jquery.js") // force this include to be first
    ui.includeJavascript("highcharts.js")
    ui.includeJavascript("exporting.js")
%>

<table cellpadding="10px">
	<tr>
		<td valign="top">
			<table class="decorated" id="tblHistory">
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
			<script>
				var chart;
				var tds_Weight;
				var tds_CD4;
				var tds_dates;
				var weigthArr=[];
				var cd4Arr=[];
				var weigthArrTrim=[];
				var cd4ArrTrim=[];
					jq(function() {
					  		jq('#tblHistory tbody tr').each(function(){
							       tds_Weight = parseInt(jq(this).find('td').eq(0).html());
							       tds_CD4 = parseInt(jq(this).find('td').eq(1).html());
								       	weigthArr.push(tds_Weight);
								   		cd4Arr.push(tds_CD4);
							});
								weigthArrTrim='['+weigthArr.filter(Boolean).reverse()+']';//to follow order from the first visit
								cd4ArrTrim='['+cd4Arr.filter(Boolean).reverse()+']';//to follow order from the first visit
						chart = new Highcharts.Chart({
							chart:{
								renderTo: 'container',
								defaultSeriesType: 'line'
							},
							title: {
			                	text: 'Chart Summary of Weight and CD4 Count',
			                    x: -20 
				            },
				            subtitle: {
				                text: 'Source: openmrs.org',
				                x: -20
				            },
				            xAxis: {
				                categories: ['0', '6', '12', '18', '24', '30',
				                    '36', '42', '48', '54', '60', '66']
				            },
				            yAxis: {
				                title: {
				                    text: 'Weight/CD4 Count'
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
				                        this.x +': '+ this.y +'Kg/Counts';
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
				            series: [
				            		{
						                name: 'Weight',
						                data: JSON.parse(weigthArrTrim)
						            }, 
						            {
						                name: 'CD4 Count',
						                data: JSON.parse(cd4ArrTrim)
						            }
						            ]
            		});
            });
						
		</script>
		
			<div id="container" style="width: 35%; height: 300px; margin: 10 auto">chart goes here</div>
		</td>
</tr>			
</table>
