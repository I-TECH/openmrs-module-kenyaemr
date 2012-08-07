<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<style>
	#search {
		float: left;
	}
</style>

<script type="text/javascript">
	jq(function() {
		jq('#calendar').datepicker({
			dateFormat: 'yy-mm-dd',
			defaultDate: '${ new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) }',
			gotoCurrent: true,
			onSelect: function(dateText) {
				location.href = ui.pageLink('dailySchedule', { date: dateText });
			}
		});
	});
</script>

<fieldset id="search">
	<legend>
		Choose Another Date
	</legend>
	
	<div id="calendar"></div>
</fieldset>

<div style="float: left; margin-left: 1em;">
	<h2>Scheduled Visits on ${ ui.format(date) }</h2>

	${ ui.includeFragment("dailySchedule", [ date: date, page: "registrationViewPatient" ]) }
</div>