<%
    ui.decorateWith("kenyaui", "panel", [heading: (config.heading ?: "Edit Patient"), frameOnly: true])

    def nameFields = [
            [
                    [object: command, property: "personName.familyName", label: "Surname "],
                    [object: command, property: "personName.givenName", label: "First name *"],
                    [object: command, property: "personName.middleName", label: "Other name(s)"]
            ],
    ]

    def otherDemogFieldRows = [
            //pw greencard additions
            [
                    [object: command, property: "dead", label: "In School"],
                    [object: command, property: "dead", label: "Orphan <18 yrs"],

            ],
            //.pw greencard additions
            [
                    [object: command, property: "maritalStatus", label: "Marital status", config: [style: "list", options: maritalStatusOptions]],
                    [object: command, property: "occupation", label: "Occupation", config: [style: "list", answerTo: occupationConcept]],
                    [object: command, property: "education", label: "Education", config: [style: "list", options: educationOptions]]
            ],
            [
                    [object: command, property: "dead", label: "Deceased"],
                    [object: command, property: "deathDate", label: "Date of death"]
            ]
    ]

    def nextOfKinFieldRows = [
            [
                    [object: command, property: "nameOfNextOfKin", label: "Next of kin name"],
                    [object: command, property: "nextOfKinRelationship", label: "Next of kin relationship"]
            ],
            [
                    [object: command, property: "nextOfKinContact", label: "Next of kin contact"],
                    [object: command, property: "nextOfKinAddress", label: "Next of kin address"]
            ]
    ]
    def guardianfieldrows  = [
            [
                    [object: command, property: "nameOfNextOfKin", label: "Guardian last name"],
                    [object: command, property: "nextOfKinRelationship", label: "Guardian first name"]
            ]


    ]
    def addressFieldRows = [
            [   //pw greencard additions -- alternate phone and email
                    [object: command, property: "telephoneContact", label: "Telephone contact*"],
                    [object: command, property: "nextOfKinAddress", label: "Alternate phone number"],
                    [object: command, property: "nextOfKinAddress", label: "Email address"]
            ], //.pw greencard additions -- alternat phone and email
            [
                    [object: command, property: "personAddress.address1", label: "Postal Address*", config: [size: 60]],
                    [object: command, property: "personAddress.country", label: "County*", config: [size: 60]],
                    [object: command, property: "subChiefName", label: "Sub County*"]
            ],
            [
                    [object: command, property: "personAddress.address3", label: "Ward*", config: [size: 60]],
                    [object: command, property: "personAddress.countyDistrict", label: "Location"],
                    [object: command, property: "personAddress.stateProvince", label: "Sub Location"]
            ],
            [[object: command, property: "personAddress.address6", label: "Village"],
             [object: command, property: "personAddress.address5", label: "Landmark*"],
             [object: command, property: "personAddress.address4", label: "Nearest Health Center*"]
            ]

    ]
%>

<form id="edit-patient-form" method="post" action="${ui.actionLink("kenyaemr", "patient/editPatient", "savePatient")}">
    <% if (command.original) { %>
    <input type="hidden" name="personId" value="${command.original.id}"/>
    <% } %>

    <div class="ke-panel-content">

        <div class="ke-form-globalerrors" style="display: none"></div>

        <div class="ke-form-instructions">
            <strong>*</strong> indicates a required field
        </div>

        <fieldset>
            <legend>ID Numbers</legend>

            <table>
                <% if (command.inHivProgram) { %>
                <tr>
                    <td class="ke-field-label">Unique Patient Number</td>
                    <td>${
                            ui.includeFragment("kenyaui", "widget/field", [object: command, property: "uniquePatientNumber"])}</td>
                    <td class="ke-field-instructions">(HIV program<% if (!command.uniquePatientNumber) { %>, if assigned<%
                            } %>)</td>
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
                    <td class="ke-field-instructions"><% if (!command.nationalIdNumber) { %>(If the patient is below 18 years of age, enter the guardian`s National Identification Number if available.)<% } %></td>
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

            <% guardianfieldrows.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>
        </fieldset>

        <fieldset>
            <legend>Address</legend>

            <% addressFieldRows.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>

        </fieldset>

        <fieldset>
            <legend>Next of Kin Details</legend>

            <% nextOfKinFieldRows.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>

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

<script type="text/javascript">
    jQuery(function () {
        jQuery('#from-age-button').appendTo(jQuery('#from-age-button-placeholder'));

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
    });

    function updateBirthdate(data) {
        var birthdate = new Date(data.birthdate);

        kenyaui.setDateField('patient-birthdate', birthdate);
        kenyaui.setRadioField('patient-birthdate-estimated', 'true');
    }
</script>