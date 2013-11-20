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
 * Patient service
 */
kenyaemrApp.service('PatientService', function ($rootScope) {

	this.updateSearch = function(query, which) {
		$rootScope.$broadcast('patient-search', { query: query, which: which });
	};
});

/**
 * Controller for patient search form
 */
kenyaemrApp.controller('PatientSearchForm', ['$scope', 'PatientService', function($scope, patientService) {

	$scope.query = '';
	$scope.which = '';

	$scope.init = function(which) {
		$scope.which = which;
	};

	$scope.updateSearch = function() {
		patientService.updateSearch($scope.query, $scope.which);
	};
}]);

/**
 * Controller for patient search results
 */
kenyaemrApp.controller('PatientSearchResults', ['$scope', '$http', function($scope, $http) {

	$scope.query = '';
	$scope.which = '';
	$scope.results = [];

	/**
	 * Initializes the controller
	 * @param appId the current app id
	 * @param which
	 */
	$scope.init = function(appId, pageProvider, page) {
		$scope.appId = appId;
		$scope.pageProvider = pageProvider;
		$scope.page = page;
	};

	/**
	 * Listens for the 'patient-search' event
	 */
	$scope.$on('patient-search', function(event, data) {
		$scope.query = data.query;
		$scope.which = data.which;
		$scope.refresh();
	});

	/**
	 * Refreshes the person search
	 */
	$scope.refresh = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'search', 'patients', { appId: $scope.appId, q: $scope.query, which: $scope.which })).
			success(function(data) {
				$scope.results = data;
			});
	};

	/**
	 * Result click event handler
	 * @param patient the clicked patient
	 */
	$scope.onResultClick = function(patient) {
		ui.navigate($scope.pageProvider, $scope.page, { patientId: patient.id });
	};

}]);

/**
 * Controller for similar patients (on registration form)
 */
kenyaemrApp.controller('SimilarPatients', ['$scope', '$http', function($scope, $http) {

	$scope.givenName = '';
	$scope.familyName = '';
	$scope.results = [];

	/**
	 * Initializes the controller
	 * @param appId the current app id
	 * @param which
	 */
	$scope.init = function(appId, pageProvider, page) {
		$scope.appId = appId;
		$scope.pageProvider = pageProvider;
		$scope.page = page;
		$scope.refresh();
	};

	/**
	 * Refreshes the patient search
	 */
	$scope.refresh = function() {
		var query = $scope.givenName + ' ' + $scope.familyName;
		$http.get(ui.fragmentActionLink('kenyaemr', 'search', 'patients', { appId: $scope.appId, q: query, which: 'all' })).
			success(function(data) {
				$scope.results = data;
			});
	};

	/**
	 * Result click event handler
	 * @param patient the clicked patient
	 */
	$scope.onResultClick = function(patient) {
		ui.navigate($scope.pageProvider, $scope.page, { patientId: patient.id });
	};

}]);

/**
 * Controller for daily schedule
 */
kenyaemrApp.controller('DailySchedule', ['$scope', '$http', function($scope, $http) {

	$scope.date = null;
	$scope.scheduled = [];

	/**
	 * Initializes the controller
	 * @param appId
	 * @param date
	 * @param pageProvider
	 * @param page
	 */
	$scope.init = function(appId, date, pageProvider, page) {
		$scope.appId = appId;
		$scope.date = date;
		$scope.pageProvider = pageProvider;
		$scope.page = page;
		$scope.fetch();
	};

	/**
	 * Refreshes the schedule
	 */
	$scope.fetch = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'patient/patientUtils', 'getScheduled', { appId: $scope.appId, date: $scope.date })).
			success(function(data) {
				$scope.scheduled = data;
			});
	};

	/**
	 * Result click event handler
	 * @param patient the clicked patient
	 */
	$scope.onResultClick = function(patient) {
		ui.navigate($scope.pageProvider, $scope.page, { patientId: patient.id });
	};
}]);

/**
 * Controller for recently viewed
 */
kenyaemrApp.controller('RecentlyViewed', ['$scope', '$http', function($scope, $http) {

	$scope.recent = [];

	/**
	 * Initializes the controller
	 * @param pageProvider
	 * @param page
	 */
	$scope.init = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'patient/patientUtils', 'recentlyViewed')).
			success(function(data) {
				$scope.recent = data;
			});
	};

	/**
	 * Result click event handler
	 * @param patient the clicked patient
	 */
	$scope.onResultClick = function(patient) {
		ui.navigate('kenyaemr', 'chart/chartViewPatient', { patientId: patient.id });
	};
}]);