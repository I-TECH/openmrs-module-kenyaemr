

<style type="text/css">


table.moh257 {
    border-collapse: collapse;
    background-color: #F3F9FF;
    width: 75%;
}
table.moh257 > tbody > tr > td, table.moh257 > tbody > tr > th {
    border: 1px solid black;
    vertical-align: baseline;
    padding: 2px;
    text-align: left;
    background-color: #F3F9FF;
}
</style>
<div class="ke-panel-frame">
    <div class="ke-panel-heading">Patient Summary</div>
    <div class="ke-panel-content" style="background-color: #F3F9FF">
        <table id="tblDetails" class="moh257" align="center" border="1" cellpadding="0" cellspacing="0">
            <tr>
                <td>Date of report: ${patient.dateOfReport}</td>
                <td>Clinic name: ${patient.clinicName}</td>
                <td>MFL code: ${patient.mflCode}</td>
            </tr>
            <tr>
                <td colspan="3">Unique Patient Number: ${ patient.upn }</td>
            </tr>
            <tr>
                <td colspan="3">Name: ${names}</td>
            </tr>
            <tr>
                <td colspan="3">DOB: ${ patient.birthDate }</td>
            </tr>
            <tr>
                <td colspan="3">Age: ${ patient.age }</td>
            </tr>

            <tr>
                <td colspan="3">Gender: ${ patient.gender}</td>
            </tr>

            <tr>
                <td colspan="3">Marital status: ${ patient.maritalStatus }</td>
            </tr>
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td>Date Confirmed HIV Positive: ${ patient.hivConfrimedDate }</td>
                <td>1st CD4: ${ patient.firstCd4 }</td>
                <td>Date 1st CD4: ${ patient.firstCd4Date }</td>
            </tr>
            <tr>
                <td>Date enrolled into care: ${ patient.dateEnrolledIntoCare}</td>
                <td colspan="2">WHO stage at enrollment: ${patient.whoStagingAtEnrollment}</td>
            </tr>
            <tr>
                <td>Patient entry point: ${patient.patientEntryPoint}</td>
                <td colspan="2">Date of entry point: ${patient.dateEntryPoint}</td>
            </tr>
            <tr>
                <td>Transfer in date: ${patient.transferInDate}</td>
                <td colspan="2">Facility transferred from: ${patient.transferInFacility}</td>
            </tr>
            <tr>
                <td colspan="3">Treatment supporter details:</td>
            </tr>
            <tr>
                <td>Name: ${patient.nameOfTreatmentSupporter}</td>
                <td>Relationship: ${patient.relationshipToTreatmentSupporter}</td>
                <td>Contact details: ${patient.contactOfTreatmentSupporter}</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="3">Drug allergies: ${patient.drigAllergies}</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>Previous ART: ${patient.previousArt}</td>
                <td colspan="2">Date started ART: ${patient.dateStartedArt}</td>
            </tr>
            <tr>
                <td>Purpose: ${patient.artPurpose}</td>
                <td>Clinical stage: ${patient.clinicalStageAtArtStart}</td>
                <td>CD4: ${patient.cd4AtArtStart}</td>
            </tr>
            <tr>
                <td>Drugs/Regimen: ${patient.purposeDrugs}</td>
                <td colspan="2">Weight at ART: ${patient.weightAtArtStart}</td>

            </tr>
            <tr>
                <td>Date: ${patient.purposeDate}</td>
                <td colspan="2">First regimen: ${firstRegimen}</td>
            </tr>
            <tr>
                <td colspan="3">Past or current OI: ${patient.ois}</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>Current ART regimen: ${currentRegimen}</td>
                <td colspan="2">
                    ART interruptions: ${ patient.artInterruptions}
                    Reason: ${patient.artInterruptionReason}
                    Date: ${patient.artInterruptionDate}
                </td>
            </tr>
            <tr>
                <td>Current WHO stage: ${patient.currentWhoStaging}</td>
                <td colspan="2">
                    Substitution within 1st line regimen: ${patient.substitutionWithFirstLine}
                    Reason: ${patient.substitutionWithFirstLineReason}
                    Date: ${patient.substitutionWithFirstLineDate}
                </td>
            </tr>
            <tr>
                <td>CTX: ${onCtx}</td>
                <td colspan="2">
                    Switch to 2nd line regimen: ${patient.switchToSecondLineRegimen}
                    Reason: ${patient.switchToSecondLineRegimenReason}
                    Date: ${patient.switchToSecondLineRegimenDate}
                </td>
            </tr>
            <tr>
                <td>Dapsone: ${onDapsone}</td>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td>IPT: ${onIpt}</td>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2">Clinics enrolled: ${programs}</td>
                <td>Transfer out date: ${toDate}</td>
            </tr>
            <tr>
                <td colspan="2">
                    Most recent CD4: ${recentCd4Count}
                    Date: ${recentCd4CountDate}
                </td>
                <td>
                    Death date: ${deadDeath}
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    Most recent VL: ${recentVl}
                    Date: ${recentVlDate}
                </td>
                <td>
                    Next appointment: ${returnVisitDate}
                </td>
            </tr>
        </table>
    </div>

</div>
<div align="center">
    <button id="print"><img src="${ ui.resourceLink("kenyaui", "images/buttons/summary.png") }" /> Print summaries</button>
</div>
<script type="text/javascript">
    jQuery(function(){
        jQuery('#print').click(function(){
            var disp_setting="toolbar=yes,location=yes,directories=yes,menubar=yes,";
            disp_setting+="scrollbars=yes,width=1000, height=780, left=100, top=25";
            var docprint = window.open("about:blank", "_blank", disp_setting);
            var oTable = document.getElementById("tblDetails");

            docprint.document.open();
            docprint.document.write('<html><head><title>${names} Summary</title>');
            docprint.document.write('</head><body><center>');
            docprint.document.write(oTable.parentNode.innerHTML);
            docprint.document.write('</center></body></html>');
            docprint.document.close();
            docprint.print();
            docprint.close();
        });
    });
</script>
