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
 * Controller for patient validation
 */
kenyaemrApp.controller('PatientValidationController', ['$scope', '$http', '$timeout', function($scope, $http) {

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