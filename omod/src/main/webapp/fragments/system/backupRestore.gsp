<%
    ui.decorateWith("kenyaui", "panel", [ heading: ui.message("Backup and Restore") ])
    ui.includeJavascript("kenyaemr", "controllers/kenyaemr.js")

%>

<div class="ke-panel-controls" xmlns="http://www.w3.org/1999/html">
    <form id="backup-database-form" method="post">
<p></p>
    <center>
        <div  ng-controller="BackupRestore" ng-init="init()">
    Click to BackUp/Restore the Database
        <br/><br/><button type="submit" ng-click="FileChooser()" value="Upload" id="backupbutton" name="backupbutton" class="backup-button"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/ok.png") }" /> Run Backup
            <div ng-if="loading" style="text-align: center; padding-top: 5px">
                <img src="${ ui.resourceLink("kenyaui", "images/loading.gif") }" />
            </div></button>

             <button type="submit"  ng-click="FileChooser()" id="restore-backup-button" name = "restorechooser" class="restorebackup-button"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/ok.png") }" /> Restore From Backup </button>
            <input type="file" id="i_file" style="display: none">
            <br>
        </div>
    </center>
    </form>



<script type="text/javascript">
    jq = jQuery;
jq(function () {
    jq('#backup-database-form .backup-button').click(function () {
        kenyaemr.callDatabaseBackup();
    })
    });

    jq(function () {
        jq('#backup-database-form .restorebackup-button').click(function () {
            kenyaemr.callDatabaseRestore();
        })
    });

</script>