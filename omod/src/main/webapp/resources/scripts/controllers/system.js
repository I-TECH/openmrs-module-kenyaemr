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

var kenyaemrApp = angular.module('kenyaemr', []);

kenyaemrApp.controller('SystemController', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

	$scope.systemInformation = [];
	$scope.databaseSummary = [];

	/**
	 * Initializes the controller
	 * @param appId the current app id
	 */
	$scope.init = function(appId) {
		$scope.appId = appId;

		$http.get(ui.fragmentActionLink('kenyaemr', 'system/systemUtils', 'getDatabaseSummary', { appId: $scope.appId })).
			success(function(data) {
				$scope.databaseSummary = data;
			});

		$scope.refresh();
	};

	/**
	 * Refreshes the system information
	 */
	$scope.refresh = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'system/systemUtils', 'getSystemInformation', { appId: $scope.appId })).
			success(function(data) {
				$scope.systemInformation = data;
				$timeout($scope.refresh, 5000);
			});
	};

}]);