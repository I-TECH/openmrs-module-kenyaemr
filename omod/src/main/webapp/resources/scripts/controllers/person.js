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

kenyaemrApp.controller('PersonSearch', ['$scope', '$http', function($scope, $http) {

	$scope.query = '';
	$scope.results = [];

	/**
	 * Initializes the controller
	 * @param appId the current app id
	 * @param which
	 */
	$scope.init = function(appId, which, pageProvider, page) {
		$scope.appId = appId;
		$scope.which = which;
		$scope.pageProvider = pageProvider;
		$scope.page = page;
		$scope.refresh();
	};

	/**
	 * Refreshes the person search
	 */
	$scope.refresh = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'search', 'persons', { appId: $scope.appId, q: $scope.query, which: $scope.which })).
			success(function(data) {
				$scope.results = data;
			});
	};

	/**
	 * Result click event handler
	 * @param person the clicked person
	 */
	$scope.onResultClick = function(person) {
		ui.navigate($scope.pageProvider, $scope.page, { personId: person.id });
	}

}]);