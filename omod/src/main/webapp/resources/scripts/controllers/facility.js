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
 * Facility service
 */
kenyaemrApp.service('FacilityService', function ($rootScope) {

	/**
	 * Broadcasts new facility search parameters
	 */
	this.updateSearch = function(query) {
		$rootScope.$broadcast('facility-search', { query: query });
	};
});

/**
 * Controller for facility search form
 */
kenyaemrApp.controller('FacilitySearchForm', ['$scope', 'FacilityService', function($scope, facilityService) {

	$scope.query = '';

	$scope.init = function() {
		$scope.$evalAsync($scope.updateSearch); // initiate an initial search
	};

	$scope.updateSearch = function() {
		facilityService.updateSearch($scope.query);
	};
}]);

/**
 * Controller for facility search results
 */
kenyaemrApp.controller('FacilitySearchResults', ['$scope', '$http', function($scope, $http) {

	$scope.query = '';
	$scope.results = [];

	/**
	 * Listens for the 'facility-search' event
	 */
	$scope.$on('facility-search', function(event, data) {
		$scope.query = data.query;
		$scope.refresh();
	});

	/**
	 * Refreshes the facility search
	 */
	$scope.refresh = function() {
		if ($scope.query.length >= 3) {
			$http.get(ui.fragmentActionLink('kenyaemr', 'search', 'locations', { q: $scope.query })).
				success(function(data) {
					$scope.results = data;
				});
		}
		else {
			$scope.results = []
		}
	};

}]);