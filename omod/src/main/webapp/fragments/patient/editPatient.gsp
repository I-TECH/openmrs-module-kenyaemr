<%
    ui.decorateWith("kenyaui", "panel", [heading: (config.heading ?: "Edit Patient"), frameOnly: true])
    def countyName = command.personAddress.country == null ? false : command.personAddress.country.toLowerCase()

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
            <% deathFieldRows.each { %>
            ${ui.includeFragment("kenyaui", "widget/rowOfFields", [fields: it])}
            <% } %>
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
                    <select class="form-control" id="county_to_refer" name="personAddress.countyDistrict" readonly="readonly">
                        <option value="">...</option>
                    </select>
                </td>
                <td style="width: 260px">
                    <select class="form-control" id="sub_county" name="personAddress.stateProvince" readonly="readonly">
                    <option value="">...</option>
                </select>
                </td>
                <td style="width: 260px">
                    <select class="form-control" id="county_facility" name="personAddress.address4" readonly="readonly">
                        <option value="">...</option>
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
                            <option value="${it}">${it}</option>
                            <%}%>
                        </select>
                    </td>
                </tr>
            </table>
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
    //On ready
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
//Counties population
        var counties={
            "Homa-Bay":
                {
                    "Homa-bay town":
                        [
                            {"facility":"Homa Bay Central","id":"Homa Bay Central"},
                            {"facility":"Homa Bay Arunjo","id":"Homa Bay Arunjo"},
                            {"facility":"Homa Bay West","id":"Homa Bay West"},
                            {"facility":"Homa Bay East","id":"Homa Bay East"}
                        ],
                    "Kasipul":
                        [
                            {"facility":"West Kasipul","id":"West Kasipul"},
                            {"facility":"South Kasipul","id":"South Kasipul"},
                            {"facility":"Central Kasipul","id":"Central Kasipul"},
                            {"facility":"East Kamagak","id":"East Kamagak"},
                            {"facility":"West Kamagak","id":"West Kamagak"}
                        ],
                    "Kabondo Kasipul":
                        [
                            {"facility":"Kabondo East","id":"Kabondo East"},
                            {"facility":"Kabondo West","id":"Kabondo West"},
                            {"facility":"Kokwanyo/Kakelo","id":"Kokwanyo/Kakelo"},
                            {"facility":"Kojwach","id":"Kojwach"},
                            {"facility":"West Kamagak","id":"West Kamagak"}
                        ],
                    "Karachuonyo":
                        [
                            {"facility":"West Karachuonyo","id":"West Karachuonyo"},
                            {"facility":"North Karachuonyo","id":"North Karachuonyo"},
                            {"facility":"Central","id":"Central"},
                            {"facility":"Kanyalou","id":"Kanyalou"},
                            {"facility":"Kibiri","id":"Kibiri"},
                            {"facility":"Wangchieng","id":"Wangchieng"},
                            {"facility":"Kendu Bay Town","id":"Kendu Bay Town"}
                        ],
                    "Rangwe":
                        [
                            {"facility":"East Gem","id":"East Gem"},
                            {"facility":"Kagen","id":"Kagen"},
                            {"facility":"Kochia","id":"Kochia"},
                            {"facility":"West Gem","id":"West Gem"}
                        ],
                    "Ndhiwa":
                        [
                            {"facility":"Kwabwai","id":"Kwabwai"},
                            {"facility":"Kanyadoto","id":"Kanyadoto"},
                            {"facility":"Kanyikela","id":"Kanyikela"},
                            {"facility":"North Kabuoch","id":"North Kabuoch"},
                            {"facility":"Kabuoch South/Pala","id":"Kabuoch South/Pala"},
                            {"facility":"Kanyamwa Kosewe","id":"Kanyamwa Kosewe"},
                            {"facility":"Kanyamwa Kologi","id":"Kanyamwa Kologi"}
                        ],
                    "Mbita":
                        [
                            {"facility":"Mfangano Island","id":"Mfangano Island"},
                            {"facility":"Rusinga Island","id":"Rusinga Island"},
                            {"facility":"Kasgunga","id":"Kasgunga"},
                            {"facility":"Gembe","id":"Gembe"},
                            {"facility":"Labwe","id":"Labwe"},
                            {"facility":"Kanyamwa Kosewe","id":"Kanyamwa Kosewe"},
                            {"facility":"Kanyamwa Kologi","id":"Kanyamwa Kologi"}
                        ],
                    "Suba":
                        [
                            {"facility":"Gwassi South","id":"Gwassi South"},
                            {"facility":"Gwassi North","id":"Gwassi North"},
                            {"facility":"Kaksingri West","id":"Kaksingri West"},
                            {"facility":"Ruma Kaksingri East","id":"Ruma Kaksingri East"}
                        ]
                },
            "Kisumu":
                {
                    "Kisumu East":
                        [
                            {"facility":"Kajulu","id":"Kajulu"},
                            {"facility":"Kolwa East","id":"Kolwa East"},
                            {"facility":"Manyatta B","id":"Manyatta B"},
                            {"facility":"Nyalenda A","id":"Nyalenda A"},
                            {"facility":"Kolwa Central","id":"Kolwa Central"}
                        ],
                    "Kisumu West":
                        [
                            {"facility":"South West Kisumu","id":"South West Kisumu"},
                            {"facility":"Central Kisumu","id":"Central Kisumu"},
                            {"facility":"Kisumu North","id":"Kisumu North"},
                            {"facility":"West Kisumu","id":"West Kisumu"},
                            {"facility":"North West Kisumu","id":"North West Kisumu"}
                        ],
                    "Kisumu Central":
                        [
                            {"facility":"Railways","id":"Railways"},
                            {"facility":"Migosi","id":"Migosi"},
                            {"facility":"Shauri Moyo Kaloleni","id":"Shauri Moyo Kaloleni"},
                            {"facility":"Market Milimani","id":"Market Milimani"},
                            {"facility":"Kondele","id":"Kondele"},
                            {"facility":"Nyalenda B","id":"Nyalenda B"}
                        ],
                    "Seme":
                        [
                            {"facility":"West Seme","id":"West Seme"},
                            {"facility":"Central Seme","id":"Central Seme"},
                            {"facility":"East Seme","id":"East Seme"},
                            {"facility":"North Seme","id":"North Seme"}
                        ],
                    "Nyando":
                        [
                            {"facility":"East Kano/Wawidhi","id":"East Kano/Wawidhi"},
                            {"facility":"Awasi/Onjiko","id":"Awasi/Onjiko"},
                            {"facility":"Ahero","id":"Ahero"},
                            {"facility":"Kabonyo/Kanyagwal","id":"Kabonyo/Kanyagwal"},
                            {"facility":"Kobura","id":"Kobura"}
                        ],
                    "Muhoroni":
                        [
                            {"facility":"Miwani","id":"Miwani"},
                            {"facility":"Ombeyi","id":"Ombeyi"},
                            {"facility":"Masogo/Nyang'oma","id":"Masogo/Nyang'oma"},
                            {"facility":"Chemilil","id":"Chemilil"},
                            {"facility":"Muhoroni/Koru","id":"Muhoroni/Koru"}
                        ],
                    "Nyakach":
                        [
                            {"facility":"South West Nyakach","id":"South West Nyakach"},
                            {"facility":"North Nyakach","id":"North Nyakach"},
                            {"facility":"Central Nyakach","id":"Central Nyakach"},
                            {"facility":"West Nyakach","id":"West Nyakach"},
                            {"facility":"South East Nyakach","id":"South East Nyakach"}
                        ]
                },
            "Migori":
                {
                    "Rongo":
                        [
                            {"facility":"North Kamagambo","id":"North Kamagambo"},
                            {"facility":"Central Kamagambo","id":"Central Kamagambo"},
                            {"facility":"East Kamagambo","id":"East Kamagambo"},
                            {"facility":"South Kamagambo","id":"South Kamagambo"}
                        ],
                    "Awendo":
                        [
                            {"facility":"North Sakwa","id":"North Sakwa"},
                            {"facility":"Central Sakwa","id":"Central Sakwa"},
                            {"facility":"West Sakwa","id":"West Sakwa"},
                            {"facility":"South Sakwa","id":"South Sakwa"}
                        ],
                    "Suna East":
                        [
                            {"facility":"God Jope","id":"God Jope"},
                            {"facility":"Suna Central","id":"Suna Central"},
                            {"facility":"Kakrao","id":"Kakrao"},
                            {"facility":"Kwa","id":"Kwa"}
                        ],
                    "Suna West":
                        [
                            {"facility":"Wiga","id":"Wiga"},
                            {"facility":"Wasweta II","id":"Wasweta II"},
                            {"facility":"Ragana-Oruba","id":"Ragana-Oruba"},
                            {"facility":"Wasimbete","id":"Wasimbete"}
                        ],
                    "Uriri":
                        [
                            {"facility":"North Kanyamkago","id":"North Kanyamkago"},
                            {"facility":"Central Kanyamkago","id":"Central Kanyamkago"},
                            {"facility":"West Kanyamkago","id":"West Kanyamkago"},
                            {"facility":"South Kanyamkago","id":"South Kanyamkago"},
                            {"facility":"East Kanyamkago","id":"East Kanyamkago"}
                        ],
                    "Nyatike":
                        [
                            {"facility":"Kachieng'","id":"Kachieng'"},
                            {"facility":"Kanyasa","id":"Kanyasa"},
                            {"facility":"North Kadem","id":"North Kadem"},
                            {"facility":"Macalder/Kanyarwanda","id":"Macalder/Kanyarwanda"},
                            {"facility":"Kaler","id":"Kaler"},
                            {"facility":"Got Kachola","id":"Got Kachola"},
                            {"facility":"Muhuru","id":"Muhuru"}
                        ],
                    "Kuria West":
                        [
                            {"facility":"Bakira East","id":"Bakira East"},
                            {"facility":"Bakira Central/Ikerege","id":"Bakira Central/Ikerege"},
                            {"facility":"Isibania","id":"Isibania"},
                            {"facility":"Makerero","id":"Makerero"},
                            {"facility":"Masaba","id":"Masaba"},
                            {"facility":"Tagare","id":"Tagare"},
                            {"facility":"Nyamosense/Komosoko","id":"Nyamosense/Komosoko"}
                        ],
                    "Kuria East":
                        [
                            {"facility":"Gokeharaka/Getambwega","id":"Gokeharaka/Getambwega"},
                            {"facility":"Ntimaru West","id":"Ntimaru West"},
                            {"facility":"Ntimaru East","id":"Ntimaru East"},
                            {"facility":"Nyabasi East","id":"Nyabasi East"},
                            {"facility":"Nyabasi West","id":"Nyabasi West"}
                        ]
                },
            "Siaya":
                {
                    "Ugenya":
                        [
                            {"facility":"Ugenya West","id":"Ugenya West"},
                            {"facility":"Ukwala","id":"Ukwala"},
                            {"facility":"North Ugenya","id":"North Ugenya"},
                            {"facility":"East Ugenya","id":"East Ugenya"}
                        ],
                    "Ugunja":
                        [
                            {"facility":"Sidindi","id":"Sidindi"},
                            {"facility":"Sigomere","id":"Sigomere"},
                            {"facility":"Ugunja","id":"Ugunja"}
                        ],
                    "Alego Usonga":
                        [
                            {"facility":"Usonga","id":"Usonga"},
                            {"facility":"West Alego","id":"West Alego"},
                            {"facility":"Central Alego","id":"Central Alego"},
                            {"facility":"Siaya Township","id":"Siaya Township"},
                            {"facility":"North Alego","id":"North Alego"},
                            {"facility":"South East Alego","id":"South East Alego"}
                        ],
                    "Gem":
                        [
                            {"facility":"North Gem","id":"North Gem"},
                            {"facility":"West Gem","id":"West Gem"},
                            {"facility":"Central Gem","id":"Central Gem"},
                            {"facility":"Yala Township","id":"Yala Township"},
                            {"facility":"East Gem","id":"East Gem"},
                            {"facility":"South Gem","id":"South Gem"}
                        ],
                    "Bondo":
                        [
                            {"facility":"West Yimbo","id":"West Yimbo"},
                            {"facility":"Central Sakwa","id":"Central Sakwa"},
                            {"facility":"South Sakwa","id":"South Sakwa"},
                            {"facility":"Yimbo East","id":"Yimbo East"},
                            {"facility":"West Sakwa","id":"West Sakwa"},
                            {"facility":"North Sakwa","id":"North Sakwa"}
                        ],
                    "Rarieda":
                        [
                            {"facility":"East Asembo","id":"East Asembo"},
                            {"facility":"West Asembo","id":"West Asembo"},
                            {"facility":"North Uyoma","id":"North Uyoma"},
                            {"facility":"South Uyoma","id":"South Uyoma"},
                            {"facility":"West Uyoma","id":"West Uyoma"}
                        ]
                },
            "Nairobi":
                {
                    "Westlands":
                        [
                            {"facility":"Kitisuru","id":"Kitisuru"},
                            {"facility":"Parklands/Highridge","id":"Parklands/Highridge"},
                            {"facility":"Karura","id":"Karura"},
                            {"facility":"Kangemi","id":"Kangemi"},
                            {"facility":"Mountain View","id":"Mountain View"}
                        ],
                    "Dagoretti North":
                        [
                            {"facility":"Kilimani","id":"Kilimani"},
                            {"facility":"Kawangware","id":"Kawangware"},
                            {"facility":"Gatina","id":"Gatina"},
                            {"facility":"Kileleshwa","id":"Kileleshwa"},
                            {"facility":"Kabiro","id":"Kabiro"}
                        ],
                    "Dagoretti South":
                        [
                            {"facility":"Mutuini","id":"Mutuini"},
                            {"facility":"Ngando","id":"Ngando"},
                            {"facility":"Riruta","id":"Riruta"},
                            {"facility":"Uthiru/Ruthimitu","id":"Uthiru/Ruthimitu"},
                            {"facility":"Waithaka","id":"Waithaka"}
                        ],
                    "Langata":
                        [
                            {"facility":"Karen","id":"Karen"},
                            {"facility":"Nairobi West","id":"Nairobi West"},
                            {"facility":"Mugumo-ini","id":"Mugumo-ini"},
                            {"facility":"South C","id":"South C"},
                            {"facility":"Nyayo Highrise","id":"Nyayo Highrise"}
                        ],
                    "Kibra":
                        [
                            {"facility":"Laini Saba","id":"Laini Saba"},
                            {"facility":"Lindi","id":"Lindi"},
                            {"facility":"Makina","id":"Makina"},
                            {"facility":"Woodley/Kenyatta Golf","id":"Woodley/Kenyatta Golf"},
                            {"facility":"Sarangombe","id":"Sarangombe"}
                        ],
                    "Roysambu":
                        [
                            {"facility":"Githurai","id":"Githurai"},
                            {"facility":"Kahawa West","id":"Kahawa West"},
                            {"facility":"Zimmerman","id":"Zimmerman"},
                            {"facility":"Roysambu","id":"Roysambu"},
                            {"facility":"Kahawa","id":"Kahawa"}
                        ],
                    "Kasarani":
                        [
                            {"facility":"Claycity","id":"Claycity"},
                            {"facility":"Mwiki","id":"Mwiki"},
                            {"facility":"Kasarani","id":"Kasarani"},
                            {"facility":"Njiru","id":"Njiru"},
                            {"facility":"Ruai","id":"Ruai"}
                        ],
                    "Ruaraka":
                        [
                            {"facility":"Baba Dogo","id":"Baba Dogo"},
                            {"facility":"Utalii","id":"Utalii"},
                            {"facility":"Mathare North","id":"Mathare North"},
                            {"facility":"Lucky Summer","id":"Lucky Summer"},
                            {"facility":"Korogocho","id":"Korogocho"}
                        ],
                    "Embakasi South":
                        [
                            {"facility":"Imara Daima","id":"Imara Daima"},
                            {"facility":"Kwa Njenga","id":"Kwa Njenga"},
                            {"facility":"Kwa Reuben","id":"Kwa Reuben"},
                            {"facility":"Pipeline","id":"Pipeline"},
                            {"facility":"Kware","id":"Kware"}
                        ],
                    "Embakasi North":
                        [
                            {"facility":"Kariobangi North","id":"Kariobangi North"},
                            {"facility":"Dandora Area I","id":"Dandora Area I"},
                            {"facility":"Dandora Area II","id":"Dandora Area II"},
                            {"facility":"Dandora Area III","id":"Dandora Area III"},
                            {"facility":"Dandora Area IV","id":"Dandora Area IV"}
                        ],
                    "Embakasi Central":
                        [
                            {"facility":"Kayole North","id":"Kayole North"},
                            {"facility":"Kayole Central","id":"Kayole Central"},
                            {"facility":"Kayole South","id":"Kayole South"},
                            {"facility":"Komarock","id":"Komarock"},
                            {"facility":"Matopeni","id":"Matopeni"}
                        ],
                    "Embakasi East":
                        [
                            {"facility":"Upper Savannah","id":"Upper Savannah"},
                            {"facility":"Lower Savannah","id":"Lower Savannah"},
                            {"facility":"Embakasi","id":"Embakasi"},
                            {"facility":"Utawala","id":"Utawala"}
                        ]
                },
            "Kiambu":
                {
                    "Gatundu South":
                        [
                            {"facility":"Kiamwangi","id":"Kiamwangi"},
                            {"facility":"Kiganjo","id":"Kiganjo"},
                            {"facility":"Ndarugo","id":"Ndarugo"},
                            {"facility":"Ngenda","id":"Ngenda"}
                        ],
                    "Gatundu North":
                        [
                            {"facility":"Gituamba","id":"Gituamba"},
                            {"facility":"Githobokoni","id":"Githobokoni"},
                            {"facility":"Gatina","id":"Gatina"},
                            {"facility":"Chania","id":"Chania"},
                            {"facility":"Mang'u","id":"Mang'u"}
                        ],
                    "Juja":
                        [
                            {"facility":"Murera","id":"Murera"},
                            {"facility":"Theta","id":"Theta"},
                            {"facility":"Juja","id":"Juja"},
                            {"facility":"Witaithie","id":"Witaithie"},
                            {"facility":"Kalimoni","id":"Kalimoni"}
                        ],
                    "Thika Town":
                        [
                            {"facility":"Township","id":"Township"},
                            {"facility":"Kamenu","id":"Kamenu"},
                            {"facility":"Hospital","id":"Hospital"},
                            {"facility":"Gatuanyaga","id":"Gatuanyaga"},
                            {"facility":"Ngoliba","id":"Ngoliba"}
                        ],
                    "Ruiru":
                        [
                            {"facility":"Gitothua","id":"Gitothua"},
                            {"facility":"Biashara","id":"Biashara"},
                            {"facility":"Gatongora","id":"Gatongora"},
                            {"facility":"Kahawa Sukari","id":"Kahawa Sukari"},
                            {"facility":"Kahawa Wendani","id":"Kahawa Wendani"},
                            {"facility":"Kiuu","id":"Kiuu"},
                            {"facility":"Mwiki","id":"Mwiki"},
                            {"facility":"Mwihoko","id":"Mwihoko"}
                        ],
                    "Githunguri":
                        [
                            {"facility":"Githunguri","id":"Githunguri"},
                            {"facility":"Githiga","id":"Githiga"},
                            {"facility":"Ikinu","id":"Ikinu"},
                            {"facility":"Ngewa","id":"Ngewa"},
                            {"facility":"Komothai","id":"Komothai"}
                        ],
                    "Kiambu":
                        [
                            {"facility":"Ting'ang'a","id":"Ting'ang'a"},
                            {"facility":"Ndumberi","id":"Ndumberi"},
                            {"facility":"Riabai","id":"Riabai"},
                            {"facility":"Township","id":"Township"}
                        ],
                    "Kiambaa":
                        [
                            {"facility":"Cianda","id":"Cianda"},
                            {"facility":"Karuri","id":"Karuri"},
                            {"facility":"Ndenderu","id":"Ndenderu"},
                            {"facility":"Muchatha","id":"Muchatha"},
                            {"facility":"Kihara","id":"Kihara"}
                        ],
                    "Kabete":
                        [
                            {"facility":"Gitaru","id":"Gitaru"},
                            {"facility":"Muguga","id":"Muguga"},
                            {"facility":"Nyadhuna","id":"Nyadhuna"},
                            {"facility":"Kabete","id":"Kabete"},
                            {"facility":"Uthiru","id":"Uthiru"}
                        ],
                    "Kikuyu":
                        [
                            {"facility":"Karai","id":"Karai"},
                            {"facility":"Nachu","id":"Nachu"},
                            {"facility":"Sigona","id":"Sigona"},
                            {"facility":"Kikuyu","id":"Kikuyu"},
                            {"facility":"Kinoo","id":"Kinoo"}
                        ],
                    "Limuru":
                        [
                            {"facility":"Bibirioni","id":"Bibirioni"},
                            {"facility":"Ndeiya","id":"Ndeiya"},
                            {"facility":"Limuru East","id":"Limuru East"},
                            {"facility":"Limuru Central","id":"Limuru Central"},
                            {"facility":"Ngecha Tigoni","id":"Ngecha Tigoni"}
                        ],
                    "Lari":
                        [
                            {"facility":"Kinale","id":"Kinale"},
                            {"facility":"Kijabe","id":"Kijabe"},
                            {"facility":"Nyanduma","id":"Nyanduma"},
                            {"facility":"Kamburu","id":"Kamburu"},
                            {"facility":"Lari/Kirenga","id":"Lari/Kirenga"}
                        ]
                },
            "Kisii":
                {
                    "Bonchari":
                        [
                            {"facility":"Bomariba","id":"Bomariba"},
                            {"facility":"Bogiakumu","id":"Bogiakumu"},
                            {"facility":"Bomorenda","id":"Bomorenda"},
                            {"facility":"Riana","id":"Riana"}
                        ],
                    "South Mugirango":
                        [
                            {"facility":"Tabaka","id":"Tabaka"},
                            {"facility":"Boikang'a","id":"Boikang'a"},
                            {"facility":"Bogetenga","id":"Bogetenga"},
                            {"facility":"Borabu/Chitago","id":"Borabu/Chitago"},
                            {"facility":"Moticho","id":"Moticho"},
                            {"facility":"Getenga","id":"Getenga"}
                        ],
                    "Bomachoge Borabu":
                        [
                            {"facility":"Bombaba Borabu","id":"Bombaba Borabu"},
                            {"facility":"Boochi Borabu","id":"Boochi Borabu"},
                            {"facility":"Bokimonge","id":"Bokimonge"},
                            {"facility":"Magenche","id":"Magenche"}
                        ],
                    "Bobasi":
                        [
                            {"facility":"Masige West","id":"Masige West"},
                            {"facility":"Masige East","id":"Masige East"},
                            {"facility":"Bobasi Central","id":"Bobasi Central"},
                            {"facility":"Nyacheki","id":"Nyacheki"},
                            {"facility":"Bobasi Bogetaorio","id":"Bobasi Bogetaorio"},
                            {"facility":"Bobasi Chache","id":"Bobasi Chache"},
                            {"facility":"Sameta/Mokwerero","id":"Sameta/Mokwerero"},
                            {"facility":"Bobasi Boitangare","id":"Bobasi Boitangare"}
                        ],
                    "Bamachoge Chache":
                        [
                            {"facility":"Majoge","id":"Majoge"},
                            {"facility":"Boochi/Tendere","id":"Boochi/Tendere"},
                            {"facility":"Bosoti/Sengera","id":"Bosoti/Sengera"}
                        ],
                    "Nyaribari Masaba":
                        [
                            {"facility":"Ichuni","id":"Ichuni"},
                            {"facility":"Nyamasibi","id":"Nyamasibi"},
                            {"facility":"Masimba","id":"Masimba"},
                            {"facility":"Gesusu","id":"Gesusu"},
                            {"facility":"Kiamokama","id":"Kiamokama"}
                        ],
                    "Nyaribari Chache":
                        [
                            {"facility":"Bobaracho","id":"Bobaracho"},
                            {"facility":"Kisii Central","id":"Kisii Central"},
                            {"facility":"Keumbu","id":"Keumbu"},
                            {"facility":"Kiogoro","id":"Kiogoro"},
                            {"facility":"Birongo","id":"Birongo"},
                            {"facility":"Ibeno","id":"Ibeno"}
                        ],
                    "Kitutu Chache North":
                        [
                            {"facility":"Monyerero","id":"Monyerero"},
                            {"facility":"Sensi","id":"Sensi"},
                            {"facility":"Marani","id":"Marani"},
                            {"facility":"Kegogi","id":"Kegogi"}

                        ],
                    "Kitutu Chache South":
                        [
                            {"facility":"Bogusero","id":"Bogusero"},
                            {"facility":"Bogeka","id":"Bogeka"},
                            {"facility":"Nyakoe","id":"Nyakoe"},
                            {"facility":"Kitutu Central","id":"Kitutu Central"},
                            {"facility":"Nyatieko","id":"Nyatieko"}
                        ]
                },
            "Nyamira":
                {
                    "Kitutu Masaba":
                        [
                            {"facility":"Rigoma","id":"Rigoma"},
                            {"facility":"Gachuba","id":"Gachuba"},
                            {"facility":"Kemera","id":"Kemera"},
                            {"facility":"Magombo","id":"Magombo"},
                            {"facility":"Manga","id":"Manga"},
                            {"facility":"Gesima","id":"Gesima"}
                        ],
                    "West Mugirango":
                        [
                            {"facility":"Nyamaia","id":"Nyamaia"},
                            {"facility":"Bogichora","id":"Bogichora"},
                            {"facility":"Bosamaro","id":"Bosamaro"},
                            {"facility":"Bonyamatuta","id":"Bonyamatuta"},
                            {"facility":"Township","id":"Township"}
                        ],
                    "North Mugirango":
                        [
                            {"facility":"Itibo","id":"Itibo"},
                            {"facility":"Bomwagamo","id":"Bomwagamo"},
                            {"facility":"Bokeira","id":"Bokeira"},
                            {"facility":"Magwagwa","id":"Magwagwa"},
                            {"facility":"Ekerenyo","id":"Ekerenyo"}
                        ],
                    "Borabu":
                        [
                            {"facility":"Mekenene","id":"Mekenene"},
                            {"facility":"Kiabonyoru","id":"Kiabonyoru"},
                            {"facility":"Nyansiongo","id":"Nyansiongo"},
                            {"facility":"Nyacheki","id":"Nyacheki"},
                            {"facility":"Esise","id":"Esise"}
                        ]

                },
            "Busia":
                {
                    "Teso North":
                        [
                            {"facility":"Malaba Central","id":"Malaba Central"},
                            {"facility":"Malaba North","id":"Malaba North"},
                            {"facility":"Ang'urai South","id":"Ang'urai South"},
                            {"facility":"Ang'urai North","id":"Ang'urai North"},
                            {"facility":"Ang'urai East","id":"Ang'urai East"},
                            {"facility":"Malaba South","id":"Malaba South"}
                        ],
                    "Teso South":
                        [
                            {"facility":"Ang'orom","id":"Ang'orom"},
                            {"facility":"Chakol South","id":"Chakol South"},
                            {"facility":"Chakol North","id":"Chakol North"},
                            {"facility":"Amukura East","id":"Amukura East"},
                            {"facility":"Amukura West","id":"Amukura West"},
                            {"facility":"Amukura Central","id":"Amukura Central"}
                        ],
                    "Nambale":
                        [

                            {"facility":"Nambale Township","id":"Nambale Township"},
                            {"facility":"Bukhayo North/Waltsi","id":"Bukhayo North/Waltsi"},
                            {"facility":"Bukhayo East","id":"Bukhayo East"},
                            {"facility":"Bukhayo Central","id":"Bukhayo Central"}
                        ],
                    "Matayos":
                        [
                            {"facility":"Bukhayo West","id":"Bukhayo West"},
                            {"facility":"Mayenje","id":"Mayenje"},
                            {"facility":"Matayos South","id":"Matayos South"},
                            {"facility":"Busibwabo","id":"Busibwabo"},
                            {"facility":"Burumba","id":"Burumba"}
                        ],
                    "Butula":
                        [
                            {"facility":"Marachi West","id":"Marachi West"},
                            {"facility":"Marachi East","id":"Marachi East"},
                            {"facility":"Marachi Central","id":"Marachi Central"},
                            {"facility":"Marachi North","id":"Marachi North"},
                            {"facility":"Elugulu","id":"Elugulu"}
                        ],
                    "Funyula":
                        [
                            {"facility":"Namboboto Nambuku","id":"Namboboto Nambuku"},
                            {"facility":"Nangina","id":"Nangina"},
                            {"facility":"Ageng'a Nanguba","id":"Ageng'a Nanguba"},
                            {"facility":"Bwiri","id":"Bwiri"}
                        ],
                    "Budalangi":
                        [
                            {"facility":"Bunyala Central","id":"Bunyala Central"},
                            {"facility":"Bunyala North","id":"Bunyala North"},
                            {"facility":"Bunyala West","id":"Bunyala West"},
                            {"facility":"Bunyala South","id":"Bunyala South"}
                        ]
                },
            "Kakamega":
                {
                    "Lugari":
                        [
                            {"facility":"Mautuma","id":"Mautuma"},
                            {"facility":"Lugari","id":"Lugari"},
                            {"facility":"Lumakanda","id":"Lumakanda"},
                            {"facility":"Chekalini","id":"Chekalini"},
                            {"facility":"Chevaywa","id":"Chevaywa"},
                            {"facility":"Lwandeti","id":"Lwandeti"}
                        ],
                    "Likuyani":
                        [
                            {"facility":"Likuyani","id":"Likuyani"},
                            {"facility":"Sango","id":"Sango"},
                            {"facility":"Kongoni","id":"Kongoni"},
                            {"facility":"Nzoia","id":"Nzoia"},
                            {"facility":"Sinoko","id":"Sinoko"}
                        ],
                    "Malava":
                        [

                            {"facility":"West Kabras","id":"West Kabras"},
                            {"facility":"East Kabras","id":"East Kabras"},
                            {"facility":"Chemuche","id":"Chemuche"},
                            {"facility":"Butali/Chegulo","id":"Butali/Chegulo"},
                            {"facility":"Manga-Shivanga","id":"Manga-Shivanga"},
                            {"facility":"South Kabras","id":"South Kabras"},
                            {"facility":"Shirugu-Mugai","id":"Shirugu-Mugai"}
                        ],
                    "Lurambi":
                        [
                            {"facility":"Butsotso East","id":"Butsotso East"},
                            {"facility":"Butsotso South","id":"Butsotso South"},
                            {"facility":"Butsotso Central","id":"Butsotso Central"},
                            {"facility":"Sheywe","id":"Sheywe"},
                            {"facility":"Mahiakalo","id":"Mahiakalo"},
                            {"facility":"Shirere","id":"Shirere"}
                        ],
                    "Navakholo":
                        [
                            {"facility":"Ingostse-Mathia","id":"Ingostse-Mathia"},
                            {"facility":"Shinoyi-Shikomari","id":"Shinoyi-Shikomari"},
                            {"facility":"Bunyala West","id":"Bunyala West"},
                            {"facility":"Bunyala East","id":"Bunyala East"},
                            {"facility":"Bunyala Central","id":"Bunyala Central"}
                        ],
                    "Mumias West":
                        [
                            {"facility":"Mumias Central","id":"Mumias Central"},
                            {"facility":"Mumias North","id":"Mumias North"},
                            {"facility":"Etenje","id":"Etenje"},
                            {"facility":"Musanda","id":"Musanda"}
                        ],
                    "Mumias East":
                        [
                            {"facility":"Lubinu/Lusheya","id":"Lubinu/Lusheya"},
                            {"facility":"Isongo/Makunga/Malaha","id":"Isongo/Makunga/Malaha"},
                            {"facility":"East Wanga","id":"East Wanga"}
                        ],
                    "Matungu":
                        [
                            {"facility":"Kayonzo","id":"Kayonzo"},
                            {"facility":"Kholera","id":"Kholera"},
                            {"facility":"Khalaba","id":"Khalaba"},
                            {"facility":"Mayoni","id":"Mayoni"},
                            {"facility":"Namamali","id":"Namamali"}
                        ],
                    "Butere":
                        [
                            {"facility":"Marama West","id":"Marama West"},
                            {"facility":"Marama Central","id":"Marama Central"},
                            {"facility":"Marenyo-Shianda","id":"Marenyo-Shianda"},
                            {"facility":"Marama North","id":"Marama North"},
                            {"facility":"Marama South","id":"Marama South"}
                        ],
                    "Khwisero":
                        [
                            {"facility":"Kisa West","id":"Kisa West"},
                            {"facility":"Kisa Central","id":"Kisa Central"},
                            {"facility":"Kisa East","id":"Kisa East"},
                            {"facility":"Kisa North","id":"Kisa North"}
                        ],
                    "Shinyalu":
                        [
                            {"facility":"Murhanda","id":"Murhanda"},
                            {"facility":"Isukha West","id":"Isukha West"},
                            {"facility":"Isukha Central","id":"Isukha Central"},
                            {"facility":"Isukha East","id":"Isukha East"},
                            {"facility":"Isukha North","id":"Isukha North"},
                            {"facility":"Isukha South","id":"Isukha South"}
                        ],
                    "Ikolomani":
                        [
                            {"facility":"Idhako South","id":"Idhako South"},
                            {"facility":"Idhako East","id":"Idhako East"},
                            {"facility":"Idhako North","id":"Idhako North"},
                            {"facility":"Idhako Central","id":"Idhako Central"}
                        ]
                },
            "Kericho":
                {
                    "Kipkelion East":
                        [
                            {"facility":"Londiani","id":"Londiani"},
                            {"facility":"Kedowa/Kimugul","id":"Kedowa/Kimugul"},
                            {"facility":"Chepseon","id":"Chepseon"},
                            {"facility":"Tendeno/Sorget","id":"Tendeno/Sorget"}
                        ],
                    "Kipkelion West":
                        [
                            {"facility":"Kunyak","id":"Kunyak"},
                            {"facility":"Kamasian","id":"Kamasian"},
                            {"facility":"Kipkelion","id":"Kipkelion"},
                            {"facility":"Chilchila","id":"Chilchila"}
                        ],
                    "Ainamoi":
                        [
                            {"facility":"Kapsoit","id":"Kapsoit"},
                            {"facility":"Ainamoi","id":"Ainamoi"},
                            {"facility":"Kapkugerwet","id":"Kapkugerwet"},
                            {"facility":"Kipchebor","id":"Kipchebor"},
                            {"facility":"Kipchimchim","id":"Kipchimchim"},
                            {"facility":"Kapsaos","id":"Kapsaos"}
                        ],
                    "Bureti":
                        [
                            {"facility":"Kisiara","id":"Kisiara"},
                            {"facility":"Tebesonik","id":"Tebesonik"},
                            {"facility":"Cheboin","id":"Cheboin"},
                            {"facility":"Chemosot","id":"Chemosot"},
                            {"facility":"Litein","id":"Litein"},
                            {"facility":"Cheplanget","id":"Cheplanget"},
                            {"facility":"Kapkatet","id":"Kapkatet"}
                        ],
                    "Belgut":
                        [
                            {"facility":"Waldai","id":"Waldai"},
                            {"facility":"Kabianga","id":"Kabianga"},
                            {"facility":"Cheptororiet/Seretut","id":"Cheptororiet/Seretu"},
                            {"facility":"Chaik","id":"Chaik"},
                            {"facility":"Kapsuser","id":"Kapsuser"}
                        ],
                    "Sigowet/Soin":
                        [
                            {"facility":"Sigowet","id":"Sigowet"},
                            {"facility":"Kaplelartet","id":"Kaplelartet"},
                            {"facility":"Soliat","id":"Soliat"},
                            {"facility":"Soin","id":"Soin"}
                        ]
                },
            "Trans Nzoia":
                {
                    "Kwanza":
                        [
                            {"facility":"Kopomboi","id":"Kopomboi"},
                            {"facility":"Kwanza","id":"Kwanza"},
                            {"facility":"Keiyo","id":"Keiyo"},
                            {"facility":"Bidii","id":"Bidii"}
                        ],
                    "Endebess":
                        [
                            {"facility":"Endebess","id":"Endebess"},
                            {"facility":"Chepchoina","id":"Chepchoina"},
                            {"facility":"Matumbei","id":"Matumbei"}
                        ],
                    "Saboti":
                        [
                            {"facility":"Kinyoro","id":"Kinyoro"},
                            {"facility":"Matisi","id":"Matisi"},
                            {"facility":"Tuwani","id":"Tuwani"},
                            {"facility":"Saboti","id":"Saboti"},
                            {"facility":"Machewa","id":"Machewa"}
                        ],
                    "Kiminini":
                        [
                            {"facility":"Kiminini","id":"Kiminini"},
                            {"facility":"Waitaluk","id":"Waitaluk"},
                            {"facility":"Sirende","id":"Sirende"},
                            {"facility":"Hospital","id":"Hospital"},
                            {"facility":"Sikhendu","id":"Sikhendu"},
                            {"facility":"Nabiswa","id":"Nabiswa"}
                        ],
                    "Cherangany":
                        [
                            {"facility":"Sinyerere","id":"Sinyerere"},
                            {"facility":"Makutano","id":"Makutano"},
                            {"facility":"Kaplamai","id":"Kaplamai"},
                            {"facility":"Motosiet","id":"Motosiet"},
                            {"facility":"Cherangany/Suwerwa","id":"Cherangany/Suwerwa"},
                            {"facility":"Chepsiro/Kiptoror","id":"Chepsiro/Kiptoror"},
                            {"facility":"Sitatunga","id":"Sitatunga"}
                        ]
                },
            "Bomet":
                {
                    "Sotik":
                        [
                            {"facility":"Ndanai/Abosi","id":"Ndanai/Abosi"},
                            {"facility":"Chemagel","id":"Chemagel"},
                            {"facility":"Kipsonoi","id":"Kipsonoi"},
                            {"facility":"Kapletundo","id":"Kapletundo"},
                            {"facility":"Rongena/Manaret","id":"Rongena/Manaret"}
                        ],
                    "Chepalungu":
                        [
                            {"facility":"Kong'asis","id":"Kong'asis"},
                            {"facility":"Nyangores","id":"Nyangores"},
                            {"facility":"Sigor","id":"Sigor"},
                            {"facility":"Chebunyo","id":"Chebunyo"},
                            {"facility":"Siongiroi","id":"Siongiroi"}
                        ],
                    "Bomet East":
                        [
                            {"facility":"Merigi","id":"Merigi"},
                            {"facility":"Kembu","id":"Kembu"},
                            {"facility":"Longisa","id":"Longisa"},
                            {"facility":"Kipreres","id":"Kipreres"},
                            {"facility":"Chemaner","id":"Chemaner"}
                        ],
                    "Bomet Central":
                        [
                            {"facility":"Silibwet Township","id":"Silibwet Township"},
                            {"facility":"Ndaraweta","id":"Ndaraweta"},
                            {"facility":"Singowet","id":"Singowet"},
                            {"facility":"Chesoen","id":"Chesoen"},
                            {"facility":"Mutarakwa","id":"Mutarakwa"}
                        ],
                    "Konoin":
                        [
                            {"facility":"Kimulot","id":"Kimulot"},
                            {"facility":"Chepchabas","id":"Chepchabas"},
                            {"facility":"Mogogosiek","id":"Mogogosiek"},
                            {"facility":"Boito","id":"Boito"},
                            {"facility":"Embomos","id":"Embomos"}
                        ]
                },
            "Nakuru":
                {
                    "Molo":
                        [
                            {"facility":"Mariashoni","id":"Mariashoni"},
                            {"facility":"Elburgon","id":"Elburgon"},
                            {"facility":"Turi","id":"Turi"},
                            {"facility":"Molo","id":"Molo"}
                        ],
                    "Njoro":
                        [
                            {"facility":"Maunarok","id":"Maunarok"},
                            {"facility":"Mauche","id":"Mauche"},
                            {"facility":"Kihingo","id":"Kihingo"},
                            {"facility":"Nessuit","id":"Nessuit"},
                            {"facility":"Lare","id":"Lare"},
                            {"facility":"Njoro","id":"Njoro"}
                        ],
                    "Naivasha":
                        [
                            {"facility":"Biashara","id":"Biashara"},
                            {"facility":"Hells Gate","id":"Hells Gate"},
                            {"facility":"Lakeview","id":"Lakeview"},
                            {"facility":"Maai-Mahiu","id":"Maai-Mahiu"},
                            {"facility":"Maiella","id":"Maiella"},
                            {"facility":"Olkaria","id":"Olkaria"},
                            {"facility":"Naivasha East","id":"Naivasha East"},
                            {"facility":"Viwandani","id":"Viwandani"}
                        ],
                    "Gilgil":
                        [
                            {"facility":"Gilgil","id":"Gilgil"},
                            {"facility":"Elementaita","id":"Elementaita"},
                            {"facility":"Mbaruk/Eburu","id":"Mbaruk/Eburu"},
                            {"facility":"Malewa West","id":"Malewa West"},
                            {"facility":"Murindati","id":"Murindati"}
                        ],
                    "Kuresoi South":
                        [
                            {"facility":"Amalo","id":"Amalo"},
                            {"facility":"Keringet","id":"Keringet"},
                            {"facility":"Kiptangich","id":"Kiptangich"},
                            {"facility":"Tinet","id":"Tinet"}
                        ]
                }


        };
        var key1,key2,key3;
        var hold_locations = [];
        var hold_divisions =[];
        var selectedDistrict,selectedDivision;
        for (key1 in counties) {
            jq("#county_to_refer").append("<option value='"+key1+"'>"+key1+"</option>");
        }
        jq("#county_to_refer").change(function () {
            selectedDistrict = this.value;
            jq("#sub_county").empty();
            jq("#sub_county").append("<option value='...'>...</option>");
            jq("#county_facility").empty();
            jq("#county_facility").append("<option value='...'>...</option>");
            for (key2 in counties[selectedDistrict]) {
                if(!hold_divisions.indexOf(key2)> -1){
                    hold_divisions.push(key2);
                    jq("#sub_county").append("<option value='"+key2+"'>"+key2+"</option>");
                }
            }

        });
        jq("#sub_county").change(function () {
            selectedDivision = this.value;
            jq("#county_facility").empty();
            jq("#county_facility").append("<option value='...'>...</option>");
            for (key3 in counties[selectedDistrict][selectedDivision]) {
                if(!hold_locations.indexOf(key3)> -1){
                    hold_locations.push(key3);
                    jq("#county_facility").append("<option value='"+counties[selectedDistrict][selectedDivision][key3].id+"'>"+counties[selectedDistrict][selectedDivision][key3].facility+"</option>");

                }
            }

        });

    }); // end of jQuery initialization block

    function updateBirthdate(data) {
        var birthdate = new Date(data.birthdate);
        kenyaui.setDateField('patient-birthdate', birthdate);
        kenyaui.setRadioField('patient-birthdate-estimated', 'true');
    }
</script>