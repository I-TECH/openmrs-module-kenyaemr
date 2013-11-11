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

kenyaemrApp.controller('ReportController', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

	$scope.queue = [];
	$scope.completed = [];

	/**
	 * Initializes the controller
	 * @param appId the current app id
	 * @param reportUuid the report definition UUID
	 */
	$scope.init = function(appId, reportUuid) {
		$scope.appId = appId;
		$scope.reportUuid = reportUuid;
		$scope.refresh();
	};

	/**
	 * Refreshes the lists of queued and completed requests
	 */
	$scope.refresh = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'report/reportUtils', 'getIncompleteRequests', { reportUuid: $scope.reportUuid })).
			success(function(data) {
				$scope.queue = data;
			});

		$http.get(ui.fragmentActionLink('kenyaemr', 'report/reportUtils', 'getCompletedRequests', { reportUuid: $scope.reportUuid })).
			success(function(data) {
				$scope.completed = data;
				$timeout($scope.refresh, 5000);
			});
	};

	/**
	 * Requests an evaluation of the report
	 * @param date the evaluation date
	 */
	$scope.requestReport = function(date) {
		$http.post(ui.fragmentActionLink('kenyaemr', 'report/reportUtils', 'requestReport', { reportUuid: $scope.reportUuid, date: date }))
			.success(defaultSuccessHandler)
			.error(defaultErrorHandler);

		$scope.refresh();
	};

	/**
	 * Cancels a report request
	 * @param reportUuid the report definition UUID
	 */
	$scope.cancelRequest = function(requestId) {
		kenyaui.openConfirmDialog({
			heading: "Report",
			message: "Cancel this report request?",
			okCallback: function() {
				$http.post(ui.fragmentActionLink('kenyaemr', 'report/reportUtils', 'cancelRequest', { requestId: requestId }))
					.success(defaultSuccessHandler)
					.error(defaultErrorHandler);
			}
		});
	};

	/**
	 * Navigates to view report data page
	 * @param requestId the report request id
	 */
	$scope.viewReportData = function(requestId) {
		ui.navigate('kenyaemr', 'reportView', { appId: $scope.appId, request: requestId, returnUrl: location.href });
	};

	/**
	 * Ininitates download of exported report data
	 * @param requestId the report request id
	 * @param type the export type
	 */
	$scope.exportReportData = function(requestId, type) {
		ui.navigate('kenyaemr', 'reportExport', { appId: $scope.appId, request: requestId, type: type });
	};

	var defaultSuccessHandler = function(data) {
		kenyaui.notifySuccess(data.message);
		$scope.refresh();
	};

	var defaultErrorHandler = function(data) {
		kenyaui.notifyError(data.message);
	};

}]);