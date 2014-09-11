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

kenyaemrApp.controller('BackupRestore', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

//    $scope.activeTypes = [];

    /**
     * Initializes the controller
     */
    $scope.init = function() {
        $scope.refresh(true);
    };
    /**
     * Closes selected visit types
     */
//    $scope.closeSelected = function() {
//        kenyaui.openFileChooser({ heading: 'Select File to upload', message: 'Close all visits of the selected types?', okCallback: function() {
//
//
//
//
//        } });
//    };
//
}]);

kenyaemr.callBackupRestore = function() {
    $.getJSON(ui.fragmentActionLink('kenyaemr', 'manageBackups', 'backupEnhancement'), function (result) {
    })
}

$(function () {
    $("#button1").click(function (event) {
        event.preventDefault();
        $('#firle').trigger('click');
    });

    document.getElementById('firle').addEventListener('change', readFile, false);
});










//AJAXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
$(document).ready(function() {
    //Stops the submit request
    $("#backup-database-form").submit(function (e) {
        e.preventDefault();
    });
    //checks for the button click event
    $("#button1").click(function (e) {

        //get the form data and then serialize that
//        dataString = $("#backup-database-form").serialize();

        //make the AJAX request, dataType is set to json
        //meaning we are expecting JSON data in response from the server
        $.ajax({
            type: "POST",
            url: "ManageBackupsFragmentController",
            data: dataString,
            dataType: "json"
        })

        //getJSON request to the Java Servlet
        $.getJSON("../javatojson", dataString, function (backupEnhancement) {

        })
    })
})
