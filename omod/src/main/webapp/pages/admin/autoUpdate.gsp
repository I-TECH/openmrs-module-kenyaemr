<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/report.js")
%>
<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
			heading: "Updates",
			items: [
					[ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "admin/adminHome") ]
			]
	]) }
</div>

<div class="ke-page-content">


    <% if (isConnectionAvailable) { %>

        <% if (updatesAvailable) { %>
    <div class="ke-panel-frame">
        <div class="ke-panel-heading"> Update Available</div>
        <div class="ke-panel-content">
            <table class="ke-table-vertical">
                <thead>
                <tr>
                    <th>Version</th>
                    <th>Action</th>

                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${releaseVersion}</td>

                    <td>
                        <a href="${ url }"   target="_blank">
                            <button>
                                <img src="${ ui.resourceLink("kenyaemr", "images/downloadImage.png") }" class="ke-glyph" /> Download
                            </button>
                        </a>

                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <% } else { %>

    <div class="ke-panel-frame">
        <div class="ke-panel-heading">

            No Updates Available
        </div>
        <div class="ke-panel-content">
            You are currently running the latest version <span style="font-size: large;"><b>${version} </span>
        </div>
    </div>


    <%} %>


    <% } else { %>

    <div class="ke-panel-frame">
        <div class="ke-panel-heading">
            <img src="${ ui.resourceLink("kenyaemr", "images/no-wifi.png") }" class="ke-glyph" />
            No Internet Connection
        </div>
        <div class="ke-panel-content">
           <b> Please check your internet connection and try again.</b>
        </div>
    </div>

    <% } %>





</div>