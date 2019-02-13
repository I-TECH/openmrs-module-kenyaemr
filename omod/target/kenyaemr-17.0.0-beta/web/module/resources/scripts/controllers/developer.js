/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

/**
 * Controller for report profiling
 */
kenyaemrApp.controller('ReportProfiling', ['$scope', '$http', function($scope, $http) {

	/**
	 * Initializes the controller
	 */
	$scope.init = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'developer/developerUtils', 'getReportProfilingEnabled')).
			success(function(data) {
				$scope.enabled = data.enabled;
			});
	};

	/**
	 * Runs the validation
	 */
	$scope.setEnabled = function(enabled) {
		$scope.results = [];
		$http.get(ui.fragmentActionLink('kenyaemr', 'developer/developerUtils', 'setReportProfilingEnabled', { enabled: enabled })).
			success(function() {
				$scope.enabled = enabled;
			});
	};

}]);

/**
 * Controller for patient validation
 */
kenyaemrApp.controller('PatientValidation', ['$scope', '$http', function($scope, $http) {

	$scope.results = [];
	$scope.loading = false;

	/**
	 * Runs the validation
	 */
	$scope.run = function() {
		$scope.results = [];
		$scope.loading = true;
		$http.get(ui.fragmentActionLink('kenyaemr', 'developer/developerUtils', 'validatePatients', {})).
			success(function(data) {
				$scope.results = data;
				$scope.loading = false;
			});
	};

}]);

/**
 * Controller for groovy console
 */
kenyaemrApp.controller('GroovyConsole', ['$scope', '$http', '$window', function($scope, $http, $window) {

	$scope.running = false;
	$scope.result = ' ';
	$scope.output = ' ';
	$scope.stacktrace = ' ';

	/**
	 * Initializes the controller
	 * @param tabsId id of the tabs widget for evaluation output
	 */
	$scope.init = function(tabsId) {
		$scope.tabsId = tabsId;
	}

	/**
	 * Runs the current script fetched from 'window.codeMirrorEditor'
	 */
	$scope.run = function() {
		$scope.running = true;
		$scope.result = ' ';
		$scope.output = ' ';
		$scope.stacktrace = ' ';

		var script = $window.codeMirrorEditor.getValue();
		var actionUrl = ui.fragmentActionLink('kenyaemr', 'developer/developerUtils', 'executeGroovy', { returnFormat: 'json' });
		var postData = jQuery.param({ script: script });

		$http({ method: 'POST', url: actionUrl, data: postData, headers: { 'Content-Type': 'application/x-www-form-urlencoded'}})
			.success(function(data) {
				$scope.result = data.result;
				$scope.output = data.output;
				$scope.stacktrace = data.stacktrace;

				var activeTab = 'result';

				if (data.stacktrace) {
					activeTab = 'stacktrace';
				} else if (data.output) {
					activeTab = 'output';
				}

				kenyatab.activateTab($scope.tabsId, activeTab);

				$scope.running = false;
			});
	};

}]);