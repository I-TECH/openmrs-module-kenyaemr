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

	/**
	 * Broadcasts new patient search parameters
	 */
	this.updateSearch = function(query, which) {
		$rootScope.$broadcast('patient-search', { query: query, which: which });
	};
});

/**
 * Controller for patient search form
 */
kenyaemrApp.controller('PatientSearchForm', ['$scope', 'PatientService','$timeout', function($scope, patientService, $timeout) {

	$scope.query = '';

	$scope.init = function(which) {
		$scope.which = which;
		$scope.$evalAsync($scope.updateSearch); // initiate an initial search
	};
	$scope.delayOnChange = (function() {
		var promise = null;
		return function(callback, ms) {
			$timeout.cancel(promise); //clearTimeout(timer);
			promise = $timeout(callback, ms); //timer = setTimeout(callback, ms);
		};
	})();

	$scope.updateSearch = function() {
		patientService.updateSearch($scope.query, $scope.which);
	};
}]);

/**
 * Controller for peer search form
 */
kenyaemrApp.controller('PeerSearchForm', ['$scope', 'PatientService', function($scope, patientService) {

    $scope.query = '';

    $scope.init = function() {
        $scope.which = "";
        $scope.$evalAsync($scope.updateSearch); // initiate an initial search
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
 * Controller for daily seen patients
 */
kenyaemrApp.controller('DailySeen', ['$scope', '$http', function($scope, $http) {

	$scope.date = null;
	$scope.seen = [];

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
	 * Refreshes the seen patients
	 */
	$scope.fetch = function() {
		$http.get(ui.fragmentActionLink('kenyaemr', 'patient/patientUtils', 'getSeenPatients', { appId: $scope.appId, date: $scope.date })).
			success(function(data) {
				$scope.seen = data;
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

    /**
     * Controller for peer search results
     */
    kenyaemrApp.controller('PeerSearchResults', ['$scope', '$http','$q','$timeout', function($scope, $http,$q,$timeout) {

        $scope.query = '';
        $scope.dateFilter = '';
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
         * Listens for the 'peer-search' event
         */
        $scope.$on('patient-search', function(event, data) {
            $scope.query = data.query;
            $scope.which = "all";
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
        $scope.onPeerEducatorResultClick = function(peer) {
            $scope.effectiveDate = angular.element('#startDate').val();
            $scope.datecopy = angular.copy( $scope.effectiveDate);
            var date = getMonthDays($scope.datecopy);
            $scope.effectiveDate = date +'-'+ $scope.effectiveDate;
            var dateFormat = "yy-mm-dd";
            var currentDate = $.datepicker.formatDate(dateFormat, new Date($scope.effectiveDate));

            var finalDate ='';
            if(currentDate.charAt(0)   === "-"){
                finalDate = currentDate.substring(1);
            }else {
                finalDate = currentDate
            }
            ui.navigate('kenyaemr', 'peerCalender/peerViewClients', { patientId: peer.id ,effectiveDate:finalDate});

        };




        function getMonthDays(MonthYear) {
            var months = [
                'January',
                'February',
                'March',
                'April',
                'May',
                'June',
                'July',
                'August',
                'September',
                'October',
                'November',
                'December'
            ];

            var Value=MonthYear.split("-");
            var month = (months.indexOf(Value[0]) + 1);
            return new Date(Value[1], month, 0).getDate();
        }

    }]);

}]);