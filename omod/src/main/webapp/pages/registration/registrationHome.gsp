<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/visit.js")

	def menuItems = [
			[ label: "Find or create patient", iconProvider: "kenyaui", icon: "buttons/patient_search.png", href: ui.pageLink("kenyaemr", "registration/registrationSearch") ]
	]
%>

<style type="text/css">
	#calendar {
		text-align: center;
	}
	#calendar .ui-widget-content {
		border: 0;
		background: inherit;
		padding: 0;
		margin: 0 auto;
	}
</style>

<script type="text/javascript">
	jQuery(function() {
		jQuery('#calendar').datepicker({
			dateFormat: 'yy-mm-dd',
			defaultDate: '${ kenyaui.formatDateParam(scheduleDate) }',
			gotoCurrent: true,
			onSelect: function(dateText) {
				ui.navigate('kenyaemr', 'registration/registrationHome', { scheduleDate: dateText });
			}
		});
	});
</script>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Tasks", items: menuItems ]) }

	${ ui.decorate("kenyaui", "panel", [ heading: "Select Day to View" ], """<div id="calendar"></div>""") }

	<div class="ke-panel-frame" ng-controller="ActiveVisits" ng-init="init()">
		<div class="ke-panel-heading">Active Visits</div>

		<div class="ke-panel-content">
			<div class="ke-stack-item" ng-repeat="type in activeTypes">
				<input type="checkbox" name="{{ type.id }}" ng-model="type.selected" /> {{ type.name }} ({{ type.count }})
			</div>
			<span ng-if="activeTypes.length == 0"><em>None</em></span>
		</div>

		<div class="ke-panel-controls" ng-show="activeTypes.length > 0">
			<button type="button" ng-click="closeSelected()"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/checkout.png") }" /> Close Visits</button>
		</div>
	</div>
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/dailySchedule", [ pageProvider: "kenyaemr", page: "registration/registrationViewPatient", date: scheduleDate ]) }
</div>