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

kenyaemrApp.controller('ServerInformation', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

	$scope.infos = [];

	/**
	 * Initializes the controller
	 */
	$scope.init = function() {
		$scope.refresh();
	};

	/**
	 * Refreshes the server information
	 */
	$scope.refresh = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'system/systemUtils', 'getServerInformation')).
			success(function(data) {
				$scope.infos = data;
				$timeout($scope.refresh, 5000);
			});
	};
}]);

kenyaemrApp.controller('DatabaseSummary', ['$scope', '$http', function($scope, $http) {

	$scope.infos = [];

	/**
	 * Initializes the controller
	 * appId the current app id
	 */
	$scope.init = function(appId) {
		$scope.appId = appId;

		$http.get(ui.fragmentActionLink('kenyaemr', 'system/systemUtils', 'getDatabaseSummary', { appId: $scope.appId })).
			success(function(data) {
				$scope.infos = data;
			});
	};
}]);