<%
    ui.decorateWith("kenyaui", "panel", [heading: (config.heading ?: "Edit Patient"), frameOnly: true])
    def countyName = command.personAddress.countyDistrict
    def country = command.personAddress.country
    def subCounty = command.personAddress.stateProvince
    def nokRelationShip = command.nextOfKinRelationship
    def kDoDCadre = command.kDoDCadre
    def kDoDRank = command.kDoDRank
    def kDoDUnit = command.kDoDUnit
    def ward = command.personAddress.address4

    def nationalIdType = "49af6cdc-7968-4abb-bf46-de10d7f4859f"
    def birthCertificateNumberType = "68449e5a-8829-44dd-bfef-c9c8cf2cb9b2"
    def passportNumberType = "be9beef6-aacc-4e1f-ac4e-5babeaa1e303"
    def nameFields = [
            [
                    [object: command, property: "personName.familyName", label: "Surname *"],
                    [object: command, property: "personName.givenName", label: "First name *"],
                    [object: command, property: "personName.middleName", label: "Other name(s)"]
            ],
    ]

    def otherDemogFieldRows = [
            [
                    [object: command, property: "maritalStatus", label: "Marital status *", config: [style: "list", options: maritalStatusOptions]],
                    [object: command, property: "occupation", label: "Occupation *", config: [style: "list", options: occupationOptions]],
                    [object: command, property: "education", label: "Education *", config: [style: "list", options: educationOptions]]
            ]
    ]

    def deathFieldRows = [
            [
                    [object: command, property: "dead", label: "Deceased"],
                    [object: command, property: "deathDate", label: "Date of death"]
            ]
    ]

    def nextOfKinFieldRows = [
            [
                    [object: command, property: "nextOfKinContact", label: "Phone Number"],
                    [object: command, property: "nextOfKinAddress", label: "Postal Address"]
            ]
    ]

    def contactsFields = [
            [
                    [object: command, property: "telephoneContact", label: "Telephone contact *"],
                    [object: command, property: "alternatePhoneContact", label: "Alternate phone number"]
            ]
    ]
   def otherContactsFields = [         [
                    [object: command, property: "personAddress.address1", label: "Postal Address", config: [size: 60]],
                    [object: command, property: "emailAddress", label: "Email address"]
            ]
    ]

    def locationSubLocationVillageFields = [

            [
                    [object: command, property: "personAddress.address6", label: "Location"],
                    [object: command, property: "personAddress.address5", label: "Sub-location"],
                    [object: command, property: "personAddress.cityVillage", label: "Village *"]
            ]
    ]

    def landmarkNearestFacilityFields = [

            [
                    [object: command, property: "personAddress.address2", label: "Landmark"],
                    [object: command, property: "nearestHealthFacility", label: "Nearest Health Center"]
            ]
    ]
    def chtDetailsFields = [
            [
                    [object: command, property: "chtReferenceNumber", label: "CHT Username"]
            ]
    ]
    def kDoDUnitField = [
            [
                    [object: command, property: "kDoDUnit", label: "Unit *"]
            ]
    ]
    def crVerifedField = [
            [
                    [object: command, property: "CRVerificationStatus", label: "Verification Status"]
            ]
    ]
%>
<script type="text/javascript" src="/${ contextPath }/moduleResources/kenyaemr/scripts/KenyaAddressHierarchy.js"></script>
<script type="text/javascript" src="/${ contextPath }/moduleResources/kenyaemr/scripts/upiVerificationUtils.js"></script>

<form id="edit-patient-form" method="post" action="${ui.actionLink("kenyaemr", "patient/editPatient", "savePatient")}">
    <% if (command.original) { %>
    <input type="hidden" name="personId" value="${command.original.id}"/>
    <% } %>

    <div class="ke-panel-content">

        <fieldset>
            <legend>Client verification with Client Registry</legend>
            <table>
                <tr>
                    <td>Identifier Type</td>
                    <td>
                        <select id="idType" name="idtype">
                            <option value="">Select a valid identifier type</option>
                            <% idTypes.each {%>
                            <% if(it.uuid == nationalIdType || it.uuid == birthCertificateNumberType || it.uuid == passportNumberType) { %>
                            <option value="${it.uuid}">${it.name}</option>
                            <% } %>
                            <%}%>
                        </select>
                    </td>
                    <td>
                        <input type="text" id="idValue" name="idValue" />
                    </td>
                    <td class="ke-field-instructions">
                        <div class="buttons-validate-identifiers">
                            <button type="button" class="ke-verify-button" id="validate-identifier">Validate Identifier</button>
                            <div class="wait-loading"></div>
                            <button type="button" class="ke-verify-button" id="show-cr-info-dialog">View Registry info</button>
                            <div class="message-validate-identifiers">
                                <label id="msgBox"></label>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr></tr>

            </table>
        </fieldset>
        <div class="ke-form-globalerrors" style="display: none"></div>

        <div class="ke-form-instructions">
            <strong>*</strong> indicates a required field
        </div>

        <fieldset id="identifiers">
            <legend>ID Numbers</legend>

            <table>
                <% if (command.inHivProgram && isKDoD==false) { %>
                <tr>
                    <td class="ke-field-label">Unique Patient Number</td>
                    <td>${
                            ui.includeFragment("kenyaui", "widget/field", [object: command, property: "uniquePatientNumber"])}</td>
                    <td class="ke-field-instructions">(HIV program<% if (!command.uniquePatientNumber) { %>, if assigned<%
                            } %>)</td>
                </tr>

                <% } %>
                <% if(enableClientNumberField || command.clientNumber) { %>
                <tr>
                    <td class="ke-field-label">${clientNumberLabel}</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "clientNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.clientNumber) { %>(This is a generic partner identification for clients. Please only provide if available)<%
                            } %></td>
                </tr>

                <% } %>


                <tr>
                    <td class="ke-field-label">Patient Clinic Number</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "patientClinicNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.patientClinicNumber) { %>(if available)<%
                        } %></td>
                </tr>
                <tr>
                    <td class="ke-field-label">National ID Number</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "nationalIdNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.nationalIdNumber) { %>(This is required for all kenyans aged 18+)<% } %></td>
                    <td><div id="nationalID-msgBox" class="ke-warning">National Id or Birth Certificate Number is Required</div></td>
                </tr>

                <tr  id="birth-cert-no">
                    <td class="ke-field-label">Birth Certificate Number</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "birthCertificateNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.birthCertificateNumber) { %>(if available or Birth Notification number)<% } %></td>
                </tr>
                <tr id="upi-no">
                    <td class="ke-field-label">NUPI</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "nationalUniquePatientNumber"])}</td>
                    <td class="ke-field-instructions"> This will be populated from MOH Client Registry</td>
                </tr>
                <tr></tr>
                <tr>
                    <td> <input type="checkbox" name="other-identifiers" value="Y"
                                id="other-identifiers" /> More identifiers </td>
                </tr>
                <tr></tr>
                <tr id="passport-no">
                    <td class="ke-field-label">Passport Number</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "passPortNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.passPortNumber) { %>(if available)<% } %></td>
                </tr>
                <tr id="huduma-no">
                    <td class="ke-field-label">Huduma Number</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "hudumaNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.hudumaNumber) { %>(if available)<% } %></td>
                </tr>
                <tr id="alien-no">
                    <td class="ke-field-label">Alien ID Number</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "alienIdNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.alienIdNumber) { %>(if available)<% } %></td>
                </tr>
                <tr id="driving-license">
                    <td class="ke-field-label">Driving License Number</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "drivingLicenseNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.drivingLicenseNumber) { %>(if available)<% } %></td>
                </tr>

                <tr id="kdod-service-no">
                    <td class="ke-field-label">Service Number *</td>
                    <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "kDoDServiceNumber"])}</td>
                    <td class="ke-field-instructions"><% if (!command.kDoDServiceNumber) { %>(5-6 digits for service officer or 5-6 digits followed by / and 2 digits for dependant(eg.12345/01))<%} %></td>
                </tr>

            </table>

        </fieldset>

        <fieldset>
            <legend>Demographics</legend>

            <% nameFields.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>
            &nbsp;&nbsp;
            <table>
               <tr>
                <td>
                    <div id="surname-msgBox" class="ke-warning">Surname is Required</div>
                </td>
                   <td>
                       <div id="firstname-msgBox" class="ke-warning">First name is Required</div>
                   </td>
                </tr>
            </table>
            <table>
                <tr>
                    <td valign="top">
                        <label class="ke-field-label">Sex *</label>
                        <span class="ke-field-content">
                            <input type="radio" name="gender" value="F"
                                   id="gender-F" ${command.gender == 'F' ? 'checked="checked"' : ''}/> Female
                            <input type="radio" name="gender" value="M"
                                   id="gender-M" ${command.gender == 'M' ? 'checked="checked"' : ''}/> Male
                            <span id="gender-F-error" class="error" style="display: none"></span>
                            <span id="gender-M-error" class="error" style="display: none"></span>
                        </span>
                    </td>
                    <td>
                        <div id="gender-msgBox" class="ke-warning">Age is Required</div>
                    </td>
                    <td valign="top"></td>
                    <td valign="top">
                        <label class="ke-field-label">Date of Birth *</label>
                        <span class="ke-field-content">
                            ${ui.includeFragment("kenyaui", "widget/field", [id: "patient-birthdate", object: command, property: "birthdate"])}
                            <span id="patient-birthdate-estimated">
                                <input type="radio" name="birthdateEstimated"
                                       value="true" ${command.birthdateEstimated ? 'checked="checked"' : ''}/> Estimated
                                <input type="radio" name="birthdateEstimated"
                                       value="false" ${!command.birthdateEstimated ? 'checked="checked"' : ''}/> Exact
                            </span>
                            &nbsp;&nbsp;&nbsp;

                            <span id="from-age-button-placeholder"></span>
                        </span>
                    </td>
                    <td>
                        <div id="age-msgBox" class="ke-warning">Age is Required</div>
                    </td>
                </tr>
            </table>

            <% otherDemogFieldRows.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>

            <table id ="kdod-struct">
                <tr>
                    <td id="cadre" class="ke-field-label" style="width: 70px">Cadre *</td>
                    <td id="rank" class="ke-field-label" style="width: 70px">Rank *</td>
                </tr>

                <tr>
                    <td style="width: 70px">
                        <select name="kDoDCadre">
                            <option></option>
                            <%cadreOptions.each { %>
                            <option ${!kDoDCadre? "" : it.trim().toLowerCase() == kDoDCadre.trim().toLowerCase() ? "selected" : ""} value="${it}">${it}</option>
                            <%}%>
                        </select>
                    </td>
                    <td style="width: 70px">
                        <select name="kDoDRank" class ="kDoDRank">
                            <option></option>
                            <%rankOptions.each { %>
                            <option ${!kDoDRank? "" : it.trim().toLowerCase() == kDoDRank.trim().toLowerCase() ? "selected" : ""} value="${it}">${it}</option>
                            <%}%>
                        </select>
                    </td>
                </tr>

                <tr>
                    <td id="unit" class="ke-field-label" style="width: 70px">Unit *</td>
                </tr>
                <tr>
                    <td style="width: 200px" id="kdod-unit">
                        <input name="kDoDUnit" class ="kDoDUnit" ${(command.kDoDUnit != null)? command.kDoDUnit : ""}/>

                    </td>
                </tr>
            </table>

            <% deathFieldRows.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>

        </fieldset>

    </fieldset>
        <fieldset>
            <legend>Address</legend>
            <table>
            <tr>
            <td class="ke-field-label">Country *</td>
            <td> </td>
            </tr>
            <tr>
                
                <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "country", config: [style: "list", options: countryOptions]])}</td>
                <td> <input type="checkbox" name="select-kenya-option" value="Y" id="select-kenya-option" /> Select Kenya </td>
                <td>
                    <div id="country-msgBox" class="ke-warning">Country is Required</div>
                </td>
            </tr>
            </table>

            <% contactsFields.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>
            <table>
            <tr>
                <td>
                    <div id="phone-msgBox" class="ke-warning">Phone number is Required</div>
                </td>
            </tr>
           </table>
            <% otherContactsFields.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>
            <table>
                <tr>
                    <td class="ke-field-label" style="width: 265px">County *</td>
                    <td class="ke-field-label" style="width: 260px">Sub-County *</td>
                    <td class="ke-field-label" style="width: 260px">Ward *</td>
                </tr>

                <tr>
                    <td style="width: 265px">
                        <select id="county" name="personAddress.countyDistrict">
                            <option></option>
                            <%countyList.each { %>
                            <option ${!countyName? "" : it.trim().toLowerCase() == countyName.trim().toLowerCase() ? "selected" : ""} value="${it}">${it}</option>
                            <%}%>
                        </select>
                    </td>
                    <td style="width: 260px">
                        <select id="subCounty" name="personAddress.stateProvince">
                            <option></option>
                        </select>
                    </td>
                    <td style="width: 260px">
                        <select id="ward" name="personAddress.address4">
                            <option></option>
                        </select>
                    </td>
                </tr>
            <tr>
                <td>
                    <div id="county-msgBox" class="ke-warning">County is Required</div>
                </td>
                <td>
                    <div id="subCounty-msgBox" class="ke-warning">Sub County is Required</div>
                </td>
                <td>
                    <div id="ward-msgBox" class="ke-warning">Ward is Required</div>
                </td>
            </tr>
            </table>

            <% locationSubLocationVillageFields.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>
            <div id="village-msgBox" class="ke-warning">Village is Required</div>

            <% landmarkNearestFacilityFields.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>
            
        </fieldset>

        <% if (peerEducator) { %>
        <fieldset>
            <legend>CHT Details</legend>
            <table>
                <tr>
                    <td valign="top">
                        <% chtDetailsFields.each { %>
                        ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
                        <% } %>
                    </td>
                </tr>
            </table>
            <%} %>

        </fieldset>
        <fieldset>
            <legend>Next of Kin Details</legend>
            <table>
                <tr>
                    <td class="ke-field-label" style="width: 260px">Name</td>
                    <td class="ke-field-label" style="width: 260px">Relationship</td>
                </tr>

                <tr>
                    <td style="width: 260px">${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "nameOfNextOfKin"])}</td>
                    <td style="width: 260px">
                        <select name="nextOfKinRelationship">
                            <option></option>
                            <%nextOfKinRelationshipOptions.each { %>
                            <option ${!nokRelationShip? "" : it.trim().toLowerCase() == nokRelationShip.trim().toLowerCase() ? "selected" : ""} value="${it}">${it}</option>
                            <%}%>
                        </select>
                    </td>
                </tr>
            </table>
            <% nextOfKinFieldRows.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>

            <% crVerifedField.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>


        </fieldset>

        <div class="text-wrap" align="center" id="post-msgBox"></div>
        <br/>

        <fieldset>
            <div class="ke-panel-footer centre-content">
                <div class="buttons-post-create-patient centre-content">
                    <button type="button" id="post-registrations" style="margin-right: 5px; margin-left: 5px;">
                        <img src="${ui.resourceLink("kenyaui", "images/glyphs/ok.png")}"/> Post to Registry
                    </button>
                    <div class="wait-loading-post-registration"></div>
                    <button type="submit" id="createPatientBtn" style="margin-right: 5px; margin-left: 5px;">
                        <img src="${ui.resourceLink("kenyaui", "images/glyphs/ok.png")}"/> ${command.original ? "Save Changes" : "Create Patient"}
                    </button>
                    <% if (config.returnUrl) { %>
                    <button type="button" class="cancel-button" style="margin-right: 5px; margin-left: 5px;"><img
                            src="${ui.resourceLink("kenyaui", "images/glyphs/cancel.png")}"/> Cancel</button>
                    <% } %>
                </div>
            </div>
        </fieldset>
        
    </div>

</form>

<div id="cr-dialog" title="Patient Overview" style="display: none; background-color: white; padding: 10px;">
    <div id="client-registry-info">

        <fieldset>
            <legend>Client name</legend>
            <table>
                <tr>
                    <td width="250px">Full name</td>
                    <td id="cr-full-name" width="200px"></td>
                    <td><button id="use-full-name" type="button" onclick="useDemographics()">Use all values in form</button></td>
                </tr>
                <tr>
                    <td>Sex</td>
                    <td id="cr-sex"></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Primary phone Number</td>
                    <td id="cr-primary-contact"></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Secondary phone</td>
                    <td id="cr-secondary-contact"></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Email address</td>
                    <td id="cr-email"></td>
                    <td></td>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>Client identifiers</legend>
            <table>
                <tr>
                    <td width="250px">UPI</td>
                    <td id="cr-upi" width="200px"></td>
                    <td><button type="button" onclick="useIdentifiers()">Use all values in form</button></td>
                </tr>
                <tr>
                    <td>National ID</td>
                    <td id="cr-national-id"></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Passport Number</td>
                    <td id="cr-passport"></td>
                    <td></td>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>Address</legend>
            <table>
                <tr>
                    <td width="250px">County</td>
                    <td id="cr-county" width="200px"></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Sub county</td>
                    <td id="cr-sub-county"></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Ward</td>
                    <td id="cr-ward"></td>
                    <td></td>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>Next of kin</legend>
            <table>
                <tr>
                    <td width="250px">Name</td>
                    <td id="cr-kin-name" width="200px"></td>
                    <td><button type="button" onclick="useNextofKin()">Use all values in form</button></td>
                </tr>
                <tr>
                    <td>Relationship</td>
                    <td id="cr-kin-relation"></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Phone number</td>
                    <td id="cr-kin-contact"></td>
                    <td></td>
                </tr>
            </table>
        </fieldset>
    </div>
    <div align="center">
        <button type="button" onclick="kenyaui.closeDialog();"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Close</button>
    </div>
</div>

<!-- You can't nest forms in HTML, so keep the dialog box form down here -->
${ui.includeFragment("kenyaui", "widget/dialogForm", [
        buttonConfig     : [id: "from-age-button", label: "from age", iconProvider: "kenyaui", icon: "glyphs/calculate.png"],
        dialogConfig     : [heading: "Calculate Birthdate", width: 40, height: 40],
        fields           : [
                [label: "Age in years", formFieldName: "age", class: java.lang.Integer],
                [
                        label: "On date", formFieldName: "now",
                        class: java.util.Date, initialValue: new java.text.SimpleDateFormat("yyyy-MM-dd").parse((new Date().getYear() + 1900) + "-06-15")
                ]
        ],
        fragmentProvider : "kenyaemr",
        fragment         : "emrUtils",
        action           : "birthdateFromAge",
        onSuccessCallback: "updateBirthdate(data);",
        onOpenCallback   : """jQuery('input[name="age"]').focus()""",
        submitLabel      : ui.message("general.submit"),
        cancelLabel      : ui.message("general.cancel")
])}

<style>
.ke-cr-client-exists {
    padding: 10px 20px;
    background-color: yellowgreen;
    color: #ffffff;
    font-weight: 200;
}

.ke-cr-client-not-found {
    padding: 10px 20px;
    background-color: darkred;
    color: #ffffff;
    font-weight: 200;
}

.wait-loading {
    margin-right: 5px;
    margin-left: 5px;
}

.buttons-validate-identifiers {
    float: left;
}

.buttons-validate-identifiers *{
    display: inline-block;
}

.message-validate-identifiers {
    margin-right: 5px;
    margin-left: 5px;
}

.buttons-post-create-patient {
    float: left;
}

.buttons-post-create-patient *{
    display: inline-block;
    margin: 0 auto;
}

.centre-content {
    display: flex;
    justify-content: center;
}

.text-wrap {
    white-space: pre-wrap;
}

.ke-cr-network-error {
    padding: 10px 20px;
    background-color: red;
    color: #ffffff;
    font-weight: 200;
}

.ke-verify-button {
    padding: 10px 20px;
    border-radius: 5px;
    background-color: #155cd2;
    color: #ffffff;
    /*font-family: Montserrat;*/
    font-size: 16px;
    font-weight: 200;
}

.ke-verify-button:hover {
    background-color:#002ead;
    transition: 0.7s;
}
</style>
<script type="text/javascript">
    //On ready
    crResponseData = ""; // response from client registry
    var loadingImageURL = ui.resourceLink("kenyaemr", "images/loading.gif");
    var showLoadingImage = '<span style="padding:2px; display:inline-block;"> <img src="' + loadingImageURL + '" /> </span>';
    var authToken = "";

    jQuery(function () {

        function display_loading_validate_identifier(status) {
            if(status) {
                jq('.wait-loading').empty();
                jq('.wait-loading').append(showLoadingImage);
            } else {
                jq('.wait-loading').empty();
            }
        }

        // fetch the token asynchronously
        function fetchTokenAsync() {
            let dfrd = jq.Deferred();
            ui.getFragmentActionAsJson("kenyaemr", "upi/upiDataExchange", "getAuthToken", {  }, function (result) {
                authToken = result;
                dfrd.resolve();
            });

            return jq.when(dfrd).done(function(){
                console.log('Finished the fetch token!');
            }).promise();
        }

        // If there is any text in the UPI field, disable post button and enable create patient button
        // Remeber to modify this once they enable editing data on remote
        jq('input[name="nationalUniquePatientNumber"]').on("input", function(e) {
            enableDisableButtonsOnUPIChange();
        });

        jq('input[name="nationalUniquePatientNumber"]').change(function(e) {
            enableDisableButtonsOnUPIChange();
        });

        jq('input[name="nationalUniquePatientNumber"]').keypress(function(e) {
            enableDisableButtonsOnUPIChange();
        });

        jq('input[name="nationalUniquePatientNumber"]').bind('paste', function(e) {
            enableDisableButtonsOnUPIChange();
        });

        jq('input[name="nationalUniquePatientNumber"]').keyup(function(e) {
            enableDisableButtonsOnUPIChange();
        });

        function enableDisableButtonsOnUPIChange() {
            // Check if there is a value in the field and either enable or disable create patient button
            let value = jq('input[name="nationalUniquePatientNumber"]').val();
            if(!value || jq.trim(value) === '' || jq.trim(value).length == 0) {
                jq('#createPatientBtn').prop('disabled', true);
                jq('#post-registrations').prop('disabled', false);
            } else {
                jq('#createPatientBtn').prop('disabled', false);
                jq('#post-registrations').prop('disabled', true);
            }
        }

        jQuery('input[name="nationalUniquePatientNumber"]').attr('readonly', true);
        jQuery('#createPatientBtn').prop('disabled', true);
        jQuery('#alien-no').hide();
        jQuery('#huduma-no').hide();
        jQuery('#passport-no').hide();
        jQuery('#driving-license').hide();
        jQuery("#post-msgBox").hide();
        jQuery("#surname-msgBox").hide();
        jQuery("#firstname-msgBox").hide();
        jQuery("#age-msgBox").hide();
        jQuery("#gender-msgBox").hide();
        jQuery("#country-msgBox").hide();
        jQuery("#phone-msgBox").hide();
        jQuery("#county-msgBox").hide();
        jQuery("#subCounty-msgBox").hide();
        jQuery("#ward-msgBox").hide();
        jQuery("#nationalID-msgBox").hide();
        jQuery("#village-msgBox").hide();

        jQuery("input[name='CRVerificationStatus']").attr('readonly', true);

        jQuery('#show-cr-info-dialog').hide();
        jQuery('#other-identifiers').click(otherIdentifiersChange);
        jQuery('#show-cr-info-dialog').click(showDataFromCR);
        jQuery('#use-full-name').click(useFullName);
        jQuery('#select-kenya-option').click(selectCountryKenyaOption);

        // clicking on the validate identifier button
        jQuery('#validate-identifier').click(function(event){

            // connect to dhp server
            //var authToken = '${clientVerificationApiToken}';
            var idType = jQuery('#idType').val();
            var idValue = jQuery('input[name=idValue]').val();
            var idTypeParam = '';

            if (idType == '' || idValue == '') {
                jQuery('#show-cr-info-dialog').hide();
                var className = jQuery('#msgBox').attr("class");
                jQuery('#msgBox').removeClass(className);
                //jQuery('#msgBox').addClass('ke-cr-client-not-found');
                jQuery('#msgBox').text('Please specify identifier type and value for verification');
                return;
            }


            if(idType == '${nationalIdType}') {
                idTypeParam = 'national-id';
            } else if (idType == '${passportNumberType}') {
                idTypeParam = 'passport';
            } else if (idType == '${birthCertificateNumberType}') {
                idTypeParam = 'birth-certificate';
            }

            var baseVerificationUrl = '${clientVerificationApi}';
            var getUrl = baseVerificationUrl + idTypeParam + '/' +  idValue;

            // show spinner
            display_loading_validate_identifier(true);

            // get the auth token
            fetchTokenAsync().done(function() {

                // Verify that we have a token
                if (authToken == '') {
                    jQuery('#show-cr-info-dialog').hide();
                    var className = jQuery('#msgBox').attr("class");
                    jQuery('#msgBox').removeClass(className);
                    //jQuery('#msgBox').addClass('ke-cr-client-not-found');
                    jQuery('#msgBox').text('Please notify the system admin to enable verification process');
                    return;
                }

                // verify the identifier
                jq.ajax({
                    url: getUrl,
                    type: "GET",
                    async: true, // asynchronous
                    timeout: 10000, // 10 sec timeout
                    headers: { Authorization: 'Bearer ' + authToken},
                    error: function(err) {
                        // hide spinner
                        display_loading_validate_identifier(false);

                        var className = jQuery('#msgBox').attr("class");
                        jQuery('#msgBox').removeClass(className);
                        jQuery('#msgBox').addClass('ke-cr-network-error');
                        switch (err.status) {
                            case "400":
                                // bad request
                                jQuery('#msgBox').text('A network error occured: 400 - Bad Request');
                                break;
                            case "401":
                                // expired or invalid token
                                jQuery('#msgBox').text('A network error occured: 401 - Invalid Token');
                                break;
                            case "403":
                                // forbidden
                                jQuery('#msgBox').text('A network error occured: 403 - Forbidden');
                                break;
                            default:
                                //Something bad happened
                                jQuery('#msgBox').text('A network error occured: ' + err.status);
                                break;
                        }
                    },
                    success: function(data) {
                        // hide spinner
                        display_loading_validate_identifier(false);

                        crResponseData = data;
                        if(data.clientExists) {
                            var className = jQuery('#msgBox').attr("class");
                            jQuery('#msgBox').removeClass(className);
                            jQuery('#msgBox').addClass('ke-cr-client-exists');
                            jQuery('#msgBox').text('Client exists in the registry. UPI number:  ' + data.client.clientNumber);

                            // unset vars
                            jQuery('#cr-full-name').text("");
                            jQuery('#cr-sex').text("");
                            jQuery('#cr-primary-contact').text("");
                            jQuery('#cr-secondary-contact').text("");
                            jQuery('#cr-email').text("");

                            jQuery('#cr-county').text("");
                            jQuery('#cr-sub-county').text("");
                            jQuery('#cr-ward').text("");

                            jQuery('#cr-kin-name').text("");
                            jQuery('#cr-kin-relation').text("");
                            jQuery('#cr-kin-contact').text("");
                            jQuery('#cr-national-id').text("");
                            jQuery('#cr-upi').text("");

                            //
                            jQuery('#cr-full-name').text(data.client.firstName + ' ' + data.client.middleName + ' ' + data.client.lastName);
                            jQuery('#cr-sex').text(data.client.gender);
                            jQuery('#cr-primary-contact').text(data.client.contact.primaryPhone);
                            jQuery('#cr-secondary-contact').text(data.client.contact.secondaryPhone);
                            jQuery('#cr-email').text(data.client.contact.emailAddress);

                            // residence
                            jQuery('#cr-county').text(data.client.residence.county);
                            jQuery('#cr-sub-county').text(data.client.residence.subCounty);
                            jQuery('#cr-ward').text(data.client.residence.ward);

                            // next of kin

                            if(data.client.nextOfKins.length > 0) {
                                var nextOfKin = data.client.nextOfKins[0];
                                jQuery('#cr-kin-name').text(nextOfKin.name);
                                jQuery('#cr-kin-relation').text(nextOfKin.relationship);
                                jQuery('#cr-kin-contact').text(nextOfKin.contact.primaryPhone);

                            }

                            // identifiers
                            jQuery('#cr-upi').text(data.client.clientNumber); // update UPI field
                            if (data.client.identifications.length > 0) {
                                for (i = 0; i < data.client.identifications.length; i++) {
                                    var identifierObj = data.client.identifications[i];
                                    if (identifierObj.identificationType == 'Identification Number') {
                                        jQuery('#cr-national-id').text(identifierObj.identificationNumber);
                                    }
                                }
                            }

                            jQuery('#show-cr-info-dialog').show();

                        } else {
                            jQuery('#show-cr-info-dialog').hide();
                            var className = jQuery('#msgBox').attr("class");
                            jQuery('#msgBox').removeClass(className);
                            jQuery('#msgBox').addClass('ke-cr-client-not-found');
                            jQuery('#msgBox').text('Client not found in the registry. Please enter registration data and post to CR ');
                        }
                    }
                });
            });
        });

        //Prepare UPI payload

        jQuery('#post-registrations').click(function(){

            //Enable Create patient button
            jQuery('#createPatientBtn').prop('disabled', false);

            //Identifiers:
            var identifierType;
            var identifierValue;
            if(jQuery('input[name=nationalIdNumber]').val() !=""){
                identifierType = "national-id";
                identifierValue = jQuery('input[name=nationalIdNumber]').val();
                jQuery("#nationalID-msgBox").hide();
            }else if(jQuery('input[name=birthCertificateNumber]').val() !=""){
                identifierType = "birth-certificate";
                identifierValue = jQuery('input[name=birthCertificateNumber]').val();
                jQuery("#nationalID-msgBox").hide();
            }else{
                // National Id or Birth Certificate Number is required
                jQuery("#post-msgBox").text("Please enter National Id or Birth Certificate Number to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery("#nationalID-msgBox").show();
                return;
            }
                //gender:
                var gender;
                if(jQuery('input[name=gender]').val() !="") {
                    jQuery("#gender-msgBox").hide();
                    if (jQuery('#gender-F').is(':checked')) {
                        gender = "female";
                    }
                    if (jQuery('#gender-M').is(':checked')) {
                        gender = "male";
                    }
                }else{
                    // Gender is required
                    jQuery("#post-msgBox").text("Please enter gender to successfully post to CR");
                    jQuery("#post-msgBox").show();
                    jQuery("#gender-msgBox").show();
                    return;
                }
            //Marital status:
            var maritalStatus;
            if(jQuery('select[name=maritalStatus]').val() !="") {
                maritalStatus = maritalStatusObject[jQuery('select[name=maritalStatus]').val()].maritalStatus;
            }

            //Occupation status:
            var occupationStatus;
            if(jQuery('select[name=occupation]').val() !="") {
                occupationStatus = occupationObject[jQuery('select[name=occupation]').val()].occupation;
            }
            //Education status:
            var educationStatus;
            if(jQuery('select[name=education]').val() !="") {
                educationStatus = educationObject[jQuery('select[name=education]').val()].education;
            }
            var countryCode;
            if(jQuery('select[name=country]').val() !=""){
                jQuery("#country-msgBox").hide();
                countryCode = countryObject[jQuery('select[name=country]').val()].countryCode;
            } else {
                // Country is required
                jQuery("#post-msgBox").text("Please enter country to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery("#country-msgBox").show();
                return;
            }
            //County Code status:
            var countyCode;
            if(jQuery('select[name="personAddress.countyDistrict"]').val() !=""){
                jQuery("#county-msgBox").hide();
                countyCode = countyObject[jQuery('select[name="personAddress.countyDistrict"]').val()].countyCode;
            } else {
                // County is required
                jQuery("#post-msgBox").text("Please enter county to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery("#county-msgBox").show();
                return;
            }
            //SubCounty Validation
            if(jQuery('select[name="personAddress.stateProvince"]').val() ==""){
                // Sub-County is required
                jQuery("#post-msgBox").text("Please enter sub county to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery("#subCounty-msgBox").show();
                return;
            }else{
                jQuery("#subCounty-msgBox").hide();
            }
            //Ward Validation
            if(jQuery('select[name="personAddress.address4"]').val() ==""){
                //Ward is required
                jQuery("#post-msgBox").text("Please enter ward to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery("#ward-msgBox").show();
                return;
            }else{
                jQuery("#ward-msgBox").hide();
            }
            //Telephone Validation
            if(jQuery('input[name="telephoneContact"]').val() ==""){
                // Telephone number is required
                jQuery("#post-msgBox").text("Please enter telephone number to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery("#phone-msgBox").show();
                return;
            }else{
                jQuery("#phone-msgBox").hide();
            }
            //Age Validation
            if(jQuery('#patient-birthdate_date').val() ==""){
                // Age is required
                jQuery("#post-msgBox").text("Please enter age to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery("#age-msgBox").show();
                return;
            }else{
                jQuery("#age-msgBox").hide();
            }
            //First name Validation
            if(jQuery('input[name="personName.givenName"]').val() ==""){
                // First Name is required
                jQuery("#post-msgBox").text("Please enter First name to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery('#firstname-msgBox').show();
                return;
            }else{
                jQuery('#firstname-msgBox').hide();
            }
            //Surname Validation
            if(jQuery('input[name="personName.familyName"]').val() ==""){
                //Family Name is required
                jQuery("#post-msgBox").text("Please enter Surname to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery('#surname-msgBox').show();
                return;
            }else{
                jQuery('#surname-msgBox').hide();
            }
            //Village Validation
            if(jQuery('input[name="personAddress.cityVillage"]').val() =="") {
                //Village is required
                jQuery("#post-msgBox").text("Please enter Village to successfully post to CR");
                jQuery("#post-msgBox").show();
                jQuery('#village-msgBox').show();
                return;
            }else{
                jQuery('#village-msgBox').hide();
            }
            //Default mfl code
            var defaultMflCode= '${defaultMflCode}';
            //CCC Number
            var nascopCCCNumber= '${nascopCCCNumber}';
            postRegistrationDetailsToCR(
                jQuery('input[name="personName.givenName"]').val(),
                jQuery('input[name="personName.middleName"]').val(),
                jQuery('input[name="personName.familyName"]').val(),
                jQuery('#patient-birthdate_date').val(),
                gender,
                maritalStatus,
                occupationStatus,
                "",   //  Religion we do not collect
                educationStatus,
                countryCode,
                defaultMflCode,
                nascopCCCNumber,
                "",   //CountyOfBirth variable not collected
                countyCode,
                jQuery('select[name="personAddress.stateProvince"]').val(),
                jQuery('select[name="personAddress.address4"]').val(),
                jQuery('input[name="personAddress.cityVillage"]').val(),
                jQuery('input[name="personAddress.address2"]').val(),    //landmark
                jQuery('input[name="personAddress.address1"]').val(),   //address
                identifierType,
                identifierValue,
                jQuery('input[name="telephoneContact"]').val(),
                jQuery('input[name="alternatePhoneContact"]').val(),
                jQuery('input[name="emailAddress"]').val(),
                jQuery('input[name="nameOfNextOfKin"]').val(),
                jQuery('input[name="nextOfKinRelationship"]').val(),
                "", //Next of kin residence not collected
                jQuery('input[name="nextOfKinContact"]').val(),
                "", //Next of kin secondary phone not collected
                "", //Next of kin email address not collected
                jQuery('input[name="dead"]').val()

            ) });


        //On Edit prepopulate patient Identifiers
        var savedAge = jQuery('#patient-birthdate').val();
        var patientAge = Math.floor((new Date() - new Date(savedAge)) / 1000 / 60 / 60 / 24 / 365.25);
        if(savedAge !="") {
            jQuery('#identifiers').show();
            // Validate identifiers according to age
            // Hide Natioanl ID for less than 18 years old
            if(patientAge > 17){
                jQuery('#national-id').show();

            }else{
                jQuery('#birth-cert-no').show();
                jQuery('#national-id').hide();
            }
        }

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

        jQuery('#county').change(updateSubcounty);
        jQuery('#subCounty').change(updateWard);
        jQuery('#patient-birthdate_date').change(updateIdentifiers);

        jQuery('#from-age-button').appendTo(jQuery('#from-age-button-placeholder'));
        jQuery('#verify-id-button').appendTo(jQuery('#verify-id-button-placeholder'));
        jQuery('#edit-patient-form .cancel-button').click(function () {
            ui.navigate('${ config.returnUrl }');
        });
        kenyaui.setupAjaxPost('edit-patient-form', {
            onSuccess: function (data) {
                if (data.id) {
                    <% if (config.returnUrl) { %>
                    ui.navigate('${ config.returnUrl }');
                    <% } else { %>
                    ui.navigate('kenyaemr', 'registration/registrationViewPatient', {patientId: data.id});
                    <% } %>
                } else {
                    kenyaui.notifyError('Saving patient was successful, but unexpected response');
                }
            }
        });
        updateSubcountyOnEdit();
        updateWardOnEdit();

    }); // end of jQuery initialization block

    function updateBirthdate(data) {
        var birthdate = new Date(data.birthdate);
        kenyaui.setDateField('patient-birthdate', birthdate);
        kenyaui.setRadioField('patient-birthdate-estimated', 'true');
        jQuery('#identifiers').show();
        // Validate identifiers according to age
        // Hide Natioanl ID for less than 18 years old
        if(birthdate !="") {
            var age = Math.floor((new Date() - new Date(birthdate)) / 1000 / 60 / 60 / 24 / 365.25);
            if (age > 17) {
                jQuery('#national-id').show();
            } else {
                jQuery('#national-id').hide();
            }
        }
    }
    function updateSubcounty() {

        jQuery('#subCounty').empty();
        jQuery('#ward').empty();
        var selectedCounty = jQuery('#county').val();
        var scKey;
        jQuery('#subCounty').append(jQuery("<option></option>").attr("value", "").text(""));
        for (scKey in kenyaAddressHierarchy[selectedCounty]) {
            jQuery('#subCounty').append(jQuery("<option></option>").attr("value", scKey).text(scKey));

        }
    }

    function updateSubcountyOnEdit() {

        jQuery('#subCounty').empty();
        jQuery('#ward').empty();
        var selectedCounty = jQuery('#county').val();
        var scKey;
        jQuery('#subCounty').append(jQuery("<option></option>").attr("value", "").text(""));
        for (scKey in kenyaAddressHierarchy[selectedCounty]) {

            jQuery('#subCounty').append(jQuery("<option></option>").attr("value", scKey).text(scKey));

        }
        jQuery('#subCounty').val('${subCounty}');
    }

    function updateWardOnEdit() {

        jQuery('#ward').empty();
        var selectedCounty = jQuery('#county').val();
        var selectedsubCounty = jQuery('#subCounty').val();
        var scKey;
        jQuery('#ward').append(jQuery("<option></option>").attr("value", "").text(""));
        for (scKey in kenyaAddressHierarchy[selectedCounty][selectedsubCounty]) {
            jQuery('#ward').append(jQuery("<option></option>").attr("value", kenyaAddressHierarchy[selectedCounty][selectedsubCounty][scKey].facility).text(kenyaAddressHierarchy[selectedCounty][selectedsubCounty][scKey].facility));

        }
        jQuery('#ward').val('${ward}');
    }

    function updateWard() {

        jQuery('#ward').empty();
        var selectedCounty = jQuery('#county').val();
        var selectedsubCounty = jQuery('#subCounty').val();
        var scKey;
        jQuery('#ward').append(jQuery("<option></option>").attr("value", "").text(""));
        for (scKey in kenyaAddressHierarchy[selectedCounty][selectedsubCounty]) {
            jQuery('#ward').append(jQuery("<option></option>").attr("value", kenyaAddressHierarchy[selectedCounty][selectedsubCounty][scKey].facility).text(kenyaAddressHierarchy[selectedCounty][selectedsubCounty][scKey].facility));

        }
    }
    function updateIdentifiers() {
        var selectedDob = jQuery('#patient-birthdate').val();
        if(selectedDob !="") {
            jQuery('#identifiers').show();
            // Validate identifiers according to age
            // Hide Natioanl ID for less than 18 years old
            var age = Math.floor((new Date() - new Date(selectedDob)) / 1000 / 60 / 60 / 24 / 365.25);
            if(age > 17){
                jQuery('#national-id').show();
            }else{
                jQuery('#national-id').hide();
            }
        }
    }
    //Ckeckbox to populate the other identifiers
    var otherIdentifiersChange = function () {

        var val = jq(this).val();
        var selectedDob = jQuery('#patient-birthdate').val();
        if (jq(this).is(':checked')){
            jQuery('#alien-no').show();
            jQuery('#huduma-no').show();
            jQuery('#passport-no').show();
            jQuery('#birth-cert-no').show();
            jQuery('#driving-license').show();
        }else{
            jQuery('#alien-no').hide();
            jQuery('#huduma-no').hide();
            jQuery('#passport-no').hide();
            jQuery('#driving-license').hide();
        }
    }

    //Ckeckbox to select country Kenya
    var selectCountryKenyaOption = function () {
        var val = jq(this).val();
        if (jq(this).is(':checked')){
            jQuery('select[name=country]').val(162883);
        }else{
            jQuery('select[name=country]').val("");
        }

        jQuery('select[name=country]').on('change', function() {
         if(this.value != 162883)  {
             jq("#select-kenya-option").prop("checked", false);
         }
         });
    }

    function showDataFromCR() {
        kenyaui.openPanelDialog({ templateId: 'cr-dialog', width: 55, height: 80, scrolling: true });
    }

    function useDemographics(){
        useFullName();
        useContact('telephoneContact','primaryPhone');
        useContact('alternatePhoneContact','secondaryPhone');
        useContact('emailAddress','emailAddress');
    }
    // re-use name from client registry
    function useFullName(){
        if (crResponseData.client.firstName != '') {
            jQuery('input[name="personName.givenName"]').val(crResponseData.client.firstName);
        }

        if (crResponseData.client.middleName != '') {
            jQuery('input[name="personName.middleName"]').val(crResponseData.client.middleName);
        }

        if (crResponseData.client.lastName != '') {
            jQuery('input[name="personName.familyName"]').val(crResponseData.client.lastName);
        }

    }

    // uses crResponseData.client as base. Doesn't work for nested objects
    function updateClientVariable(formInputName, responseVariablePath) {
        if (crResponseData.client[responseVariablePath] != '') {
            jQuery("input[name='" + formInputName +"']").val(crResponseData.client[responseVariablePath]);
        }
    }

    // use client.contact as base
    function useContact(formInputName, responseVariablePath){
        if (crResponseData.client.contact[responseVariablePath] != '') {
            jQuery("input[name='" + formInputName +"']").val(crResponseData.client.contact[responseVariablePath]);
        }

    }



    // use client.identifications as base
    function useIdentifiers(){

        if (crResponseData.client.identifications.length > 0) {
            var nationalIdType = 'Identification Number';
            var passportIdType = 'passport-no';
            var birthCertificateIdType = 'birth-certificate';

            for (i = 0; i < crResponseData.client.identifications.length; i++) {
                var identifierObj = crResponseData.client.identifications[i];
                if (identifierObj.identificationType == nationalIdType) {
                    jQuery("input[name='nationalIdNumber']").val(identifierObj.identificationNumber);
                } else if (identifierObj.identificationType == passportIdType) {
                    jQuery("input[name='passPortNumber']").val(identifierObj.identificationNumber);
                } else if (identifierObj.identificationType == birthCertificateIdType) {
                    jQuery("input[name='birthCertificateNumber']").val(identifierObj.identificationNumber);
                }
            }

            // update NUPI
            jQuery("input[name='nationalUniquePatientNumber']").val(crResponseData.client.clientNumber).trigger('change');
            jQuery("input[name='CRVerificationStatus']").val("Verified elsewhere").attr('readonly', true);
            jq('#createPatientBtn').prop('disabled', false);
            jq('#post-registrations').prop('disabled', true);

        }

    }

    // use client.nextOfKins as base
    function useNextofKin(){

        var firstNok = crResponseData.client.nextOfKins[0];

        if (firstNok.name != '') {
            jQuery('input[name="nameOfNextOfKin"]').val(firstNok.name);
        }

        if (firstNok.contact.primaryPhone != '') {
            jQuery('input[name="nextOfKinContact"]').val(firstNok.contact.primaryPhone);
        }

        if (firstNok.residence != '') {
            jQuery('input[name="nextOfKinAddress"]').val(firstNok.residence);
        }

    }

    function display_loading_post_registration(status) {
        if(status) {
            jq('.wait-loading-post-registration').empty();
            jq('.wait-loading-post-registration').append(showLoadingImage);
        } else {
            jq('.wait-loading-post-registration').empty();
        }
    }

    function postRegistrationDetailsToCR(firstName,middleName,lastName,dateOfBirth,gender,maritalStatus,occupationStatus,religion,educationStatus,countryCode,defaultMflCode,nascopCCCNumber,countyOfBirth,countyCode,subCounty,ward,village,landMark,address,identificationType,identificationValue,primaryPhone,secondaryPhone,emailAddress,name,relationship,residence,nokPrimaryPhone,nokSecondaryPhone,nokEmailAddress,isAlive) {
        // connect to CR server and post data

        // Show spinner
        display_loading_post_registration(true);

        responseData = "";

        var params = params

        var params = {
            "firstName": firstName,
            "middleName": middleName,
            "lastName": lastName,
            "dateOfBirth": dateOfBirth,
            "maritalStatus": maritalStatus,
            "gender": gender,
            "occupation": occupationStatus,
            "religion": "",
            "educationLevel": educationStatus,
            "country": countryCode,
            "countyOfBirth": countyCode,
            "isAlive": true,
            "originFacilityKmflCode": defaultMflCode,
            "nascopCCCNumber": nascopCCCNumber,
            "residence": {
                "county": countyCode,
                "subCounty": subCounty.toLowerCase().replace(" ", '-'),
                "ward": ward.toLowerCase().replace(" ", '-'),
                "village": village,
                "landMark": landMark,
                "address": address
            },
            "identifications": [{
                "identificationType": identificationType,
                "identificationNumber": identificationValue
            }],
            "contact": {
                "primaryPhone": primaryPhone,
                "secondaryPhone": secondaryPhone,
                "emailAddress": emailAddress
            },
            "nextOfKins": []
        }


        //Using fragment action to post
        jQuery.getJSON('${ ui.actionLink("kenyaemr", "upi/upiDataExchange", "postUpiClientRegistrationInfoToCR")}',
            {
                'postParams': JSON.stringify(params)
            })
            .success(function (data) {
                // Hide spinner
                display_loading_post_registration(false);

                responseData = data;
                
                if(data.status == 200) {
                    if(data.clientNumber) {
                        jQuery("input[name='nationalUniquePatientNumber']").val(data.clientNumber);
                        jQuery("#post-msgBox").text("Assigned National UPI : " + data.clientNumber);
                        jQuery("input[name='CRVerificationStatus']").val("Yes").attr('readonly', true);
                        jQuery("#post-msgBox").show();

                    } else if(jQuery("input[name='nationalUniquePatientNumber']").val() != "" ) {
                        jQuery("#post-msgBox").text(jQuery("input[name='nationalUniquePatientNumber']").val());
                        jQuery("input[name='CRVerificationStatus']").val("Verified").attr('readonly', true);
                        jQuery("#post-msgBox").show();
                    } else if(jQuery("input[name='nationalUniquePatientNumber']").val() == "" ) {
                        jQuery("input[name='CRVerificationStatus']").val("Pending").attr('readonly', true);
                    }
                } else {
                    if(jQuery("input[name='nationalUniquePatientNumber']").val() != "" ) {
                        jQuery("input[name='CRVerificationStatus']").val("Verified");
                    } else {
                        jQuery("input[name='CRVerificationStatus']").val("Pending");
                    }
                    jQuery("#post-msgBox").text("Could not verify with Client registry. Please continue with registration : \\n" + JSON.stringify(JSON.parse(data.message).errors));
                    
                    jQuery("#post-msgBox").show();
                }
                
            })
            .fail(function (err) {
                    // Hide spinner
                    display_loading_post_registration(false);

                    console.log(err)

                    jQuery("input[name='CRVerificationStatus']").val("Pending");
                    jQuery("#post-msgBox").text("Could not verify with Client registry. Please continue with registration");
                    jQuery("#post-msgBox").show();
                }
            )
    }


</script>

