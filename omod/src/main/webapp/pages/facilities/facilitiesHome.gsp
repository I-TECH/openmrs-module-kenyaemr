<%
	ui.includeJavascript("kenyaemr", "controllers/facility.js")

	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>
<div class="ke-page-sidebar">
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Search for a Facility</div>
		<div class="ke-panel-content">
			<form ng-controller="FacilitySearchForm" ng-init="init()">
				<label class="ke-field-label">MFL code or name (3 chars min)</label>
				<span class="ke-field-content">
					<input type="text" name="query" ng-model="query" ng-change="updateSearch()" style="width: 260px" />
				</span>
			</form>
		</div>
	</div>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Help</div>
		<div class="ke-panel-content">
			Facility data is taken from the <a href="http://www.ehealth.or.ke/facilities">Master Facility List</a>. If
			you have internet you can check there for the most up to date information.
		</div>
	</div>
</div>

<div class="ke-page-content">
	<div class="ke-panel-frame" ng-controller="FacilitySearchResults">
		<div class="ke-panel-heading">Matching Facilities</div>
		<div class="ke-panel-content">
			<div class="ke-stack-item" ng-repeat="facility in results">
				${ ui.includeFragment("kenyaemr", "facility/result.full") }
			</div>
			<div ng-if="results.length == 0" style="text-align: center; font-style: italic">None</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('input[name="query"]').focus();
	});
</script>
