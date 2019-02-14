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
 * Account service
 */
kenyaemrApp.service('AccountService', function ($rootScope) {

	/**
	 * Broadcasts new account search parameters
	 */
	this.updateSearch = function(query, which) {
		$rootScope.$broadcast('account-search', { query: query, which: which });
	};
});

/**
 * Controller for account search form
 */
kenyaemrApp.controller('AccountSearchForm', ['$scope', 'AccountService', function($scope, accountService) {

	$scope.query = '';

	$scope.init = function(which) {
		$scope.which = which;
		$scope.$evalAsync($scope.updateSearch); // initiate an initial search
	};

	$scope.updateSearch = function() {
		accountService.updateSearch($scope.query, $scope.which);
	};
}]);

/**
 * Controller for account search results
 */
kenyaemrApp.controller('AccountSearchResults', ['$scope', '$http', function($scope, $http) {

	$scope.query = '';
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
	 * Listens for the 'account-search' event
	 */
	$scope.$on('account-search', function(event, data) {
		$scope.query = data.query;
		$scope.which = data.which;
		$scope.refresh();
	});

	/**
	 * Refreshes the person search
	 */
	$scope.refresh = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'search', 'accounts', { appId: $scope.appId, q: $scope.query, which: $scope.which })).
			success(function(data) {
				$scope.results = data;
			});
	};

	/**
	 * Result click event handler
	 * @param account the clicked account
	 */
	$scope.onResultClick = function(account) {
		ui.navigate($scope.pageProvider, $scope.page, { personId: account.id });
	};

}]);