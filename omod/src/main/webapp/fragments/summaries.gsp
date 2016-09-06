

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
                <td>UPN: ${ patient.upn }</td>
                <td colspan="2">Name: ${names}</td>
            </tr>
            <tr>
                <td>DOB: ${ patient.birthDate }</td>
                <td colspan="2">
                    Age: ${ patient.age }
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Gender: ${ patient.gender}
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Marital status: ${ patient.maritalStatus }
                </td>
            </tr>

            <tr>
                <td colspan="3">&nbsp;&nbsp;</td>
            </tr>

            <tr>
                <td>Date Confirmed HIV Positive: ${ patient.hivConfrimedDate }</td>
                <td>First CD4: ${ patient.firstCd4 }</td>
                <td>Date first CD4: ${ patient.firstCd4Date }</td>
            </tr>
            <tr>
                <td>Date enrolled into care: ${ patient.dateEnrolledIntoCare}</td>
                <td>WHO stage at enrollment: ${patient.whoStagingAtEnrollment}</td>
                <td>Transfer in date: ${tiDate}</td>
            </tr>
            <tr>
                <td>Entry point: ${patient.patientEntryPoint}</td>
                <td>Date of entry point: ${patient.dateEntryPoint}</td>
                <td colspan="2">Facility transferred from: ${patient.transferInFacility}</td>
            </tr>
            <tr>
                <td colspan="3">&nbsp;</td>
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
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="3">Drug allergies: ${allergies}</td>
            </tr>
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td>Previous ART: ${patient.previousArt}</td>
                <td colspan="2">Date started ART: ${patient.dateStartedArt}</td>
            </tr>
            <tr>
                <td>Purpose: ${patient.artPurpose}</td>
                <td>Clinical stage at ART: ${patient.clinicalStageAtArtStart}</td>
                <td>CD4 at ART: ${patient.cd4AtArtStart}</td>
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
                <td colspan="3">Past or current OI: ${iosResults}</td>
            </tr>
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td>Current ART regimen: ${currentRegimen}</td>
                <td colspan="2">
                    <table width="100%">
                        <tr>
                            <td width="40%">ART interruptions:</td>
                            <td>
                                <table>
                                    <tr>
                                        <td>Reason: </td>
                                    </tr>
                                    <tr>
                                        <td>Date:</td>
                                    </tr>
                                </table>
                            </td>

                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td>Current WHO stage: ${patient.currentWhoStaging}</td>
                <td colspan="2">
                    <table width="100%">
                        <tr>
                            <td width="40%">Substitution within 1st line regimen:</td>
                            <td>
                                <table>
                                    <tr>
                                        <td>Reason: </td>
                                    </tr>
                                    <tr>
                                        <td>Date:</td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>

                </td>
            </tr>
            <tr>
                <td>CTX: ${onCtx}</td>
                <td colspan="2">
                    <table width="100%">
                        <tr>
                            <td width="40%">Switch to 2nd line regimen:</td>
                            <td>
                                <table>
                                    <tr>
                                        <td>Reason: </td>
                                    </tr>
                                    <tr>
                                        <td>Date:</td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td>Dapsone: ${onDapsone}</td>
                <td>IPT: ${onIpt}</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2">Clinics enrolled: ${clinicValues}</td>
                <td>Transfer out date: ${toDate}</td>
            </tr>
            <tr>
                <td colspan="2">
                    <table width="75%">
                        <tr>
                            <td width="50%">Most recent CD4: ${recentCd4Count}</td>
                            <td>Date: ${recentCd4CountDate}</td>
                        </tr>
                    </table>


                </td>
                <td>
                    Death date: ${deadDeath}
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <table width="75%">
                        <tr>
                            <td width="50%">Most recent VL: ${recentVl}</td>
                            <td> Date: ${recentVlDate}</td>
                        </tr>
                    </table>
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
            docprint.document.write('<html><head>');
            docprint.document.write('</head><body><center>');
            docprint.document.write(oTable.parentNode.innerHTML);
            docprint.document.write('</center></body></html>');
            docprint.document.close();
            docprint.print();
            docprint.close();
        });
    });
</script>
