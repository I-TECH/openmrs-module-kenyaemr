    <%
        def kDoDNumber = patient.kDoDCadre
        def kDoDCadre = patient.kDoDCadre
        def kDoDRank = patient.kDoDRank
        def kDoDUnit = patient.kDoDUnit
    %>

    <style type="text/css">


    table.moh257 {
        border-collapse: collapse;
        background-color: #D9F4D3;
        width: 75%;
    }
    table.moh257 > tbody > tr > td, table.moh257 > tbody > tr > th {
        border: 1px solid black;
        vertical-align: baseline;
        padding: 2px;
        text-align: left;
        background-color: #D9F4D3;
    }

    </style>

    <div class="ke-panel-frame">
        <div class="ke-panel-heading">Patient Summary</div>
        <div class="ke-panel-content" style="background-color: #D9F4D3">
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
                     <td>Weight (Kgs): ${weight}</td>
                      <td colspan="2">TB Screening Outcome: ${patient.tbScreeningOutcome}</td>
                   </tr>
                 <tr>
                    <td>Height (cm): ${height}</td>
                    <td colspan="2">Chronic Illness:: ${chronicDisease}</td>
                </tr>
                 <tr>
                    <td>BMI: ${patient.bmi}</td>
                    <td colspan="2">OI History: ${iosResults}</td>
                </tr>
                 <tr>
                    <td>Blood Pressure: ${bloodPressure}/${setBpDiastolic}</td>
                    <td colspan="2">STI Screening: ${patient.stiScreeningOutcome}</td>
                </tr>

                 <tr>
                   <td>Oxygen Saturation: ${oxygenSaturation}</td>
                    <td colspan="2">CACX Screening: ${patient.caxcScreeningOutcome}</td>
                </tr>

                <tr>
                   <td>Respiratory rate: ${patient.respiratoryRate}</td>
                   <td colspan="2">TPT Start Date: ${tbStartDate}</td>
                </tr>
                <tr>
                   <td>Pulse Rate: ${pulseRate}</td>
                   <td colspan="2">TPT Completion Date: ${tbEndDate}</td>
                </tr>
                <tr>
                    <td>FP Method: ${patient.familyProtection}</td>


                    <includeIf velocityTest="$patient.gender == 'F' ">
                     <td colspan="2">LMP (For Women): ${patient.lmp}</td>
                    </includeIf>

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
                </tr>

                <tr>
                </tr>


                    <td>Date: ${patient.purposeDate}</td>
                    <td colspan="2">First regimen: ${firstRegimen}</td>
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
                    <td>TPT: ${onIpt}</td>
                    <td>&nbsp;</td>
                </tr>

                <tr>
                    <td colspan="2">Clinics enrolled: ${clinicValues}</td>
                    <td>
                        Transfer out date: ${toDate} <br />
                        Transfer out facility: ${toFacility}
                    </td>
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
                <tr>
                  <td colspan="3">&nbsp;</td>
                </tr>
                <tr>
                    <td>Clinical Notes: </td>
                    <td colspan="2">

                    </td>
                </tr>

                <tr>
                    <td>Clinician Name: </td>
                    <td colspan="2">

                    </td>
                </tr>
                <tr>
                   <td>Clinician Signature: </td>
                   <td colspan="2">
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

            if("${isKDoD}"=="false"){
                jQuery('#kdod-struct').hide();
                jQuery('#kdod-service-no').hide();
            }
            else {
                jQuery('#kdod-struct').show();
                jQuery('#kdod-service-no').show();

                jq("select[name='kDoDCadre']").change(function () {
                    var cadre = jq(this).val();

                    if (cadre === "Civilian") {
                        jq('#rank').hide();
                        jq('#unit').hide();

                        jq(".kDoDUnit").val("");

                        jq(".kDoDRank")[0].selectedIndex = 0;

                        jq('.kDoDRank').removeAttr('required');
                        jq('.kDoDUnit').removeAttr('required');

                        jq('.kDoDRank').hide();
                        jq('.kDoDUnit').hide();

                    }
                    else {
                        jq('.kDoDRank').attr('required',1);
                        jq('.kDoDUnit').attr('required',1);

                        jq('#rank').show();
                        jq('#unit').show();

                        jq('.kDoDRank').show();
                        jq('.kDoDUnit').show();

                    }
                });
            }

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
