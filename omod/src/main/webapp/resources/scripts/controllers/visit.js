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

kenyaemrApp.controller('ActiveVisits', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

	$scope.activeTypes = [];

	/**
	 * Initializes the controller
	 */
	$scope.init = function() {
		$scope.refresh(true);
	};

	/**
	 * Refreshes the visit types with active visits
	 */
	$scope.refresh = function(repeat) {
		$http.get(ui.fragmentActionLink('kenyaemr', 'registrationUtil', 'getActiveVisitTypes'))
			.success(function(data) {
				$scope.activeTypes = data;
				if (repeat) {
					$timeout($scope.refresh, 5000);
				}
			});
	};

	/**
	 * Closes selected visit types
	 */
	$scope.closeSelected = function() {
		kenyaui.openConfirmDialog({ heading: 'End of Day', message: 'Close all visits of the selected types?', okCallback: function() {

			var selected = _.filter($scope.activeTypes, function(type) { return type.selected; });
			var selectedIds = _.map(selected, function(type) { return type.id; });

			$http.get(ui.fragmentActionLink('kenyaemr', 'registrationUtil', 'closeActiveVisits', { typeIds: selectedIds }))
				.success(function(data) {
					kenyaui.notifySuccess(data.message);
					$scope.refresh(false);
				});
		} });
	};

}]);