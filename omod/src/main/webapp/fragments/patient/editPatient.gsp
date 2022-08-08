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
    def nameFields = [
            [
                    [object: command, property: "personName.familyName", label: "Surname *"],
                    [object: command, property: "personName.givenName", label: "First name *"],
                    [object: command, property: "personName.middleName", label: "Other name(s)"]
            ],
    ]

    def otherDemogFieldRows = [
            [
                    [object: command, property: "maritalStatus", label: "Marital status", config: [style: "list", options: maritalStatusOptions]],
                    [object: command, property: "occupation", label: "Occupation", config: [style: "list", options: occupationOptions]],
                    [object: command, property: "education", label: "Education", config: [style: "list", options: educationOptions]]
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
                    [object: command, property: "telephoneContact", label: "Telephone contact"]
            ],
            [
                    [object: command, property: "alternatePhoneContact", label: "Alternate phone number"],
                    [object: command, property: "personAddress.address1", label: "Postal Address", config: [size: 60]],
                    [object: command, property: "emailAddress", label: "Email address"]
            ]
    ]

    def locationSubLocationVillageFields = [

            [
                    [object: command, property: "personAddress.address6", label: "Location"],
                    [object: command, property: "personAddress.address5", label: "Sub-location"],
                    [object: command, property: "personAddress.cityVillage", label: "Village"]
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
%>
<script type="text/javascript" src="/${ contextPath }/moduleResources/kenyaemr/scripts/KenyaAddressHierarchy.js"></script>
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
                            <option>Select a valid identifier from the list</option>
                            <% idTypes.each {%>
                                <option value="${it.patientIdentifierTypeId}">${it.name}</option>
                            <%}%>
                        </select>
                    </td>
                    <td>
                        <input type="text" id="idValue" name="idValue" />
                    </td>
                    <td class="ke-field-instructions">
                        <button type="button" class="ke-verify-button" id="validate-identifier">Validate Identifier</button>
                        <button type="button" class="ke-verify-button" id="show-cr-info-dialog">Show CR info</button>
                        &nbsp;&nbsp;
                        <label id="msgBox"></label>
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
                    </tr>

                    <tr  id="birth-cert-no">
                        <td class="ke-field-label">Birth Certificate Number</td>
                        <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "birthCertificateNumber"])}</td>
                        <td class="ke-field-instructions"><% if (!command.birthCertificateNumber) { %>(if available or Birth Notification number)<% } %></td>
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
            <tr id="national-id">
                <td class="ke-field-label">National ID Number</td>
                <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "nationalIdNumber"])}</td>
                <td class="ke-field-instructions"><% if (!command.nationalIdNumber) { %>(Enter National Identification Number or patient's National ID waiting card number if available)<% } %></td>
            </tr>
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
            <tr  id="birth-cert-no">
                <td class="ke-field-label">Birth Certificate Number</td>
                <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "birthCertificateNumber"])}</td>
                <td class="ke-field-instructions"><% if (!command.birthCertificateNumber) { %>(if available or Birth Notification number)<% } %></td>
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
            <tr id="upi-no">
                <td class="ke-field-label">NUPI</td>
                <td>${ui.includeFragment("kenyaui", "widget/field", [object: command, property: "nationalUniquePatientNumber"])}</td>
                <td class="ke-field-instructions"><% if (!command.nationalUniquePatientNumber) { %>(If available)<%} %></td>
            </tr>
            <tr>
                <td> <input type="checkbox" name="other-identifiers" value="Y"
                            id="other-identifiers" /> More identifiers </td>
            </tr>
        </table>

    </fieldset>


</fieldset>
        <fieldset>
            <legend>Address</legend>

            <% contactsFields.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>

            <table>
                <tr>
                    <td class="ke-field-label" style="width: 265px">County</td>
                    <td class="ke-field-label" style="width: 260px">Sub-County</td>
                    <td class="ke-field-label" style="width: 260px">Ward</td>
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
            </table>
            <% locationSubLocationVillageFields.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>

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

        </fieldset>
       <fieldset>
      <table>
            <tr>
                <td class="ke-field-instructions">
                    <button type="button" id="post-registrations">Post Registration Info to CR</button>
                    <label id="post-msgBox"></label>
                </td>
            </tr>
        </table>


       </fieldset>
    </div>

    <div class="ke-panel-footer">
        <button type="submit">
            <img src="${ui.resourceLink("kenyaui", "images/glyphs/ok.png")}"/> ${command.original ? "Save Changes" : "Create Patient"}
        </button>
        <% if (config.returnUrl) { %>
        <button type="button" class="cancel-button"><img
                src="${ui.resourceLink("kenyaui", "images/glyphs/cancel.png")}"/> Cancel</button>
        <% } %>
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
                <td><button id="use-full-name" type="button">use in form</button></td>
	        </tr>
            <tr>
                <td>Sex</td>
                <td id="cr-sex"></td>
                <td><button type="button">use in form</button></td>
            </tr>
            <tr>
                <td>Primary phone Number</td>
                <td id="cr-primary-contact"></td>
                <td><button type="button" onclick="useContact('telephoneContact','primaryPhone')">use in form</button></td>
            </tr>
            <tr>
                <td>Secondary phone</td>
                <td id="cr-secondary-contact"></td>
                <td><button type="button" onclick="useContact('alternatePhoneContact','secondaryPhone')">use in form</button></td>
            </tr>
            <tr>
                <td>Email address</td>
                <td id="cr-email"></td>
                <td><button type="button" onclick="useContact('emailAddress','emailAddress')">use in form</button></td>
            </tr>
	    </table>
	</fieldset>
    <fieldset>
    <legend>Client identifiers</legend>
        <table>
            <tr>
                <td width="250px">UPI</td>
                <td id="cr-upi" width="200px"></td>
                <td></td>
            </tr>
            <tr>
                <td>National ID</td>
                <td id="cr-national-id"></td>
                <td><button type="button" onclick="useIdentifier('nationalIdNumber','Identification Number')">use in form</button></td>
            </tr>
            <tr>
                <td>Passport Number</td>
                <td id="cr-passport"></td>
                <td><button type="button">use in form</button></td>
            </tr>
        </table>
    </fieldset>
    <fieldset>
    <legend>Address</legend>
        <table>
            <tr>
                <td width="250px">County</td>
                <td id="cr-county" width="200px"></td>
                <td><button type="button">use in form</button></td>
            </tr>
            <tr>
                <td>Sub county</td>
                <td id="cr-sub-county"></td>
                <td><button type="button">use in form</button></td>
            </tr>
            <tr>
                <td>Ward</td>
                <td id="cr-ward"></td>
                <td><button type="button">use in form</button></td>
            </tr>
        </table>
    </fieldset>
    <fieldset>
    <legend>Next of kin</legend>
        <table>
            <tr>
                <td width="250px">Name</td>
                <td id="cr-kin-name" width="200px"></td>
                <td><button type="button" onclick="useNextofKin()">use all values in form</button></td>
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
    jQuery(function () {

        jQuery('#alien-no').hide();
        jQuery('#huduma-no').hide();
        jQuery('#passport-no').hide();
        jQuery('#driving-license').hide();

        jQuery('#show-cr-info-dialog').hide();
        jQuery('#other-identifiers').click(otherIdentifiersChange);
        jQuery('#show-cr-info-dialog').click(showDataFromCR);
        jQuery('#use-full-name').click(useFullName);
        jQuery('#validate-identifier').click(function(event){

            // connect to dhp server
            var authToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkU0MUU1QUM5RUIxNTlBMjc1NTY4NjM0MzIxMUJDQzAzMDMyMEUzMTZSUzI1NiIsIng1dCI6IjVCNWF5ZXNWbWlkVmFHTkRJUnZNQXdNZzR4WSIsInR5cCI6ImF0K2p3dCJ9.eyJpc3MiOiJodHRwczovL2RocGlkZW50aXR5c3RhZ2luZ2FwaS5oZWFsdGguZ28ua2UiLCJuYmYiOjE2NTIyNzMzNzksImlhdCI6MTY1MjI3MzM3OSwiZXhwIjoxNjUyMzU5Nzc5LCJhdWQiOlsiREhQLkdhdGV3YXkiLCJESFAuVmlzaXRhdGlvbiJdLCJzY29wZSI6WyJESFAuR2F0ZXdheSIsIkRIUC5WaXNpdGF0aW9uIl0sImNsaWVudF9pZCI6InBhcnRuZXIudGVzdC5jbGllbnQiLCJqdGkiOiJDMDE2RUYyQjhFMkUzQUI5NzIyNDY4Q0EwOUYxOTVGRiJ9.FD_VpzTqoUj3JnWSEZXFuvPyOs-rkDlxOLE_QnVWYY5w-scn1RtkaLuTwLU274t_TWAX3r2NYvft5l-bmEWRkrjevQ87FN9vplbpLx1kMAqgrdNysiLW4PfPFYZ_DjmoeAeSAVLEkQhs86ZjhapffI03QRmT8dFUB_Ta_Aq2l25Sru8Tb7kIE9J186e7RVIK3DgCSCS3srm66Se5HY7w56UEaYNAhjKncn_GaCpUsF6TXEvr-dOE4PdJQrjZopdLfs9DQKk8WKrlbOAZW_bznIEMhOxyTQ4hK3IvR6lcyf-S_9KZgwZF9KXK0IYNPCgzynLl_TYQmQCEPRTTlhPkHw";
            var idType = 'national-id';
            var idValue = jQuery('input[name=idValue]').val();
            var getUrl = 'https://dhpstagingapi.health.go.ke/visit/registry/search/' + idType + '/' +  idValue;
            jq.ajax({
                 url: getUrl,
                 type: "GET",
                 headers: { Authorization: 'Bearer ' + authToken},
                 error: function(err) {
                   switch (err.status) {
                     case "400":
                       // bad request
                       break;
                     case "401":
                       // expired or invalid token
                       break;
                     case "403":
                       // forbidden
                       break;
                     default:
                       //Something bad happened
                       break;
                   }
                 },
                 success: function(data) {
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

            //
        });

        //Prepare UPI payload
        var identifierType;
        var identifierValue;
        if(jQuery('input[name=nationalIdNumber]').val() !=""){
            identifierType = "national-id";
            identifierValue = jQuery('input[name=nationalIdNumber]').val();
        }
        if(jQuery('#birth-cert-no').val() !=""){
            identifierType = "birth-certificate";
            identifierValue = jQuery('#birthCertificateNo').val();
        }

        jQuery('#post-registrations').click(function(){
            postRegistrationDetailsToCR(
                jQuery('input[name="personName.familyName"]').val(),
                jQuery('input[name="personName.givenName"]').val(),
                jQuery('input[name="personName.middleName"]').val(),
                jQuery('#patient-birthdate_date').val(),
                jQuery('input[name=gender]').val(),
                jQuery('input[name=maritalStatus]').val(),
                jQuery('input[name=occupation]').val(),
                "",   //  Religeon we do not collect
                jQuery('input[name=education]').val(),
                "",   //Country variable not collected
                "",   //CountryOfBirth variable not collected
                jQuery('input[name="personAddress.countyDistrict"]').val(),
                jQuery('input[name="personAddress.stateProvince"]').val(),
                jQuery('input[name="personAddress.address4"]').val(),
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
            var age = Math.floor((new Date() - new Date(selectedDob)) / 1000 / 60 / 60 / 24 / 365.25);
            if(age > 17){
                jQuery('#driving-license').show();
                jQuery('#other-child-identifiers').hide();
            }
        }else{
            jQuery('#alien-no').hide();
            jQuery('#huduma-no').hide();
            jQuery('#passport-no').hide();
            jQuery('#driving-license').hide();
        }
    }

    function showDataFromCR() {
            kenyaui.openPanelDialog({ templateId: 'cr-dialog', width: 55, height: 80, scrolling: true });
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
    function useIdentifier(formInputName, identificationType){

        if (data.client.identifications.length > 0) {
            for (i = 0; i < data.client.identifications.length; i++) {
                var identifierObj = data.client.identifications[i];
                if (identifierObj.identificationType == identificationType) {
                    jQuery("input[name='" + formInputName +"']").val(identifierObj.identificationNumber);
                }
            }
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

    function postRegistrationDetailsToCR(firstName,middleName,lastName,dateOfBirth,gender,maritalStatus,occupation,religion,educationLevel,country,countyOfBirth,county,subCounty,ward,village,landMark,address,identificationType,identificationValue,primaryPhone,secondaryPhone,emailAddress,name,relationship,residence,nokPrimaryPhone,nokSecondaryPhone,nokEmailAddress,isAlive) {
        // connect to CR server
        var params = {"firstName":firstName,
            "middleName":middleName,
            "lastName":lastName,
            "dateOfBirth":dateOfBirth,
            "gender":gender,
            "maritalStatus":maritalStatus,
            "occupation":occupation,
            "religion":religion,
            "educationLevel":educationLevel,
            "residence": {
                "country": country,
                "countyOfBirth": countyOfBirth,
                "county": county,
                "subCounty": subCounty,
                "ward": ward,
                "village": village,
                "landMark": landMark,
                "address": address
            },
            "identification": {
                "identificationType": identificationType,
                "identificationNumber": identificationValue
            },
            "contact": {
                "primaryPhone": primaryPhone,
                "secondaryPhone": secondaryPhone,
                "emailAddress": emailAddress,
            },
            "nextOfKins": [{
                "name": name,
                "relationship": relationship,
                "residence": residence,
                "contact": {
                    "primaryPhone": nokPrimaryPhone,
                    "secondaryPhone": nokSecondaryPhone,
                    "emailAddress": nokEmailAddress,
                }
            }],
            "isAlive":isAlive,

        };
        //Using fragment action to post
        jQuery.getJSON('${ ui.actionLink("kenyaemr", "upi/upiDataExchange", "postUpiClientRegistrationInfoToCR")}',
            {
                'postParams': params.toString()
            })
            .success(function (data) {
                console.log("Response from CR  ==> ");
                console.log("Response from CR  ==> ");
            })
            .fail(function (err) {
                    console.log(err)
                }
            )

        var authToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkU0MUU1QUM5RUIxNTlBMjc1NTY4NjM0MzIxMUJDQzAzMDMyMEUzMTZSUzI1NiIsIng1dCI6IjVCNWF5ZXNWbWlkVmFHTkRJUnZNQXdNZzR4WSIsInR5cCI6ImF0K2p3dCJ9.eyJpc3MiOiJodHRwczovL2RocGlkZW50aXR5c3RhZ2luZ2FwaS5oZWFsdGguZ28ua2UiLCJuYmYiOjE2NTIxODUyMzQsImlhdCI6MTY1MjE4NTIzNCwiZXhwIjoxNjUyMjcxNjM0LCJhdWQiOlsiREhQLkdhdGV3YXkiLCJESFAuVmlzaXRhdGlvbiJdLCJzY29wZSI6WyJESFAuR2F0ZXdheSIsIkRIUC5WaXNpdGF0aW9uIl0sImNsaWVudF9pZCI6InBhcnRuZXIudGVzdC5jbGllbnQiLCJqdGkiOiJENjUyOTUwNDQ1RDYyMjg2NDc1OTE3NjkxQzMwMzM4MyJ9.tey01umz34GOZv1ewpafpyiuj3Y0-lUO0ufww5nPEQ89Gl3QG73j6AjuU-mvnupCEt5hrPePuwTXt2gQ6CSgP9C82gVsdboF8pwbcr3eBZQ8Q9jNxPzKSOFoI6FuThnig_YDg6uHEcykgMnGBcM1OJIJnEnJcvc01mcfHi6J2IRlfI_wlG5__oeKKbvt2DjGygjuwBVUb4nGyEmqhjg8VRB0LZsD83h1bB2Z0FCU7IKyqUMC5dzZxGpWLYCtABdxG_YvPAP2tkzFD7SXdJKu7GT4UMJwh5CvNmQ4BVSWfcLOEk4d_8YblHjVXDy110Zk-qmPl5vv7NNRX1lv69N-gQ";
        var idType = 'identification-number';
        var idValue = jQuery('input[name=nationalIdNumber]').val();
        var postUrl = 'https://dhpstaging.health.go.ke/visit/registry';
        console.log("Payload ==> "+JSON.stringify(params));
//        jQuery.ajax({
//            url: postUrl,
//            crossDomain:true,
//            type: "POST",
//            headers: { Authorization: 'Bearer ' + authToken },
//            error: function(err) {
//                switch (err.status) {
//                    case "400":
//                        // bad request
//                        break;
//                    case "401":
//                        // expired or invalid token
//                        break;
//                    case "403":
//                        // forbidden
//                        break;
//                    default:
//                        //Something bad happened
//                        break;
//                }
//            },
//            data:params,
//            success: function(data) {
//                if(data.clientExists) {
//                    console.log("Client Number ==> "+data.client.clientNumber);
//
//                } else {
//                    jQuery('#msgBox').text('Unable to post successfully to CR ');
//                }
//            }
//        });

    }


</script>

