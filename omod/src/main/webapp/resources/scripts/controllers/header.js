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

kenyaemrApp.controller('PatientHeader', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

	$scope.patient = null;
	$scope.iconUrl = null;
	$scope.flags = [];

	/**
	 * Initializes the controller
	 * @param appId the current app id
	 * @param patientId the patient id
	 */
	$scope.init = function(appId, patientId) {
		$scope.appId = appId;
		$scope.patientId = patientId;
		$scope.refresh();
	};

	/**
	 * Refreshes the flags
	 */
	$scope.refresh = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'search', 'patient', { id: $scope.patientId })).
			success(function(patient) {
				$scope.patient = patient;
				$scope.iconUrl = ui.resourceLink('kenyaui', 'images/buttons/patient_' + patient.gender + '.png');

				// Only lookup flags for alive and non-voided patients, and if there is a current app
				if (!patient.dead && !patient.voided && $scope.appId) {
					$http.get(ui.fragmentActionLink('kenyaemr', 'patient/patientUtils', 'getFlags', { appId: $scope.appId, patientId: $scope.patientId }))
						.success(function(flags) {
							$scope.flags = flags;
						});
				}
				else {
					$scope.flags = [];
				}

				$timeout($scope.refresh, 30 * 1000); // update every 30 seconds
			});
	};
}]);