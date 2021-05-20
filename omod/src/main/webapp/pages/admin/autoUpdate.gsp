<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/report.js")
%>

<style type="text/css">
.download-button {
    background-color:green;
    color: white;
    border-color: #999999;
    font-size: 16px;
}
.center-image {
    margin-left: auto;
    margin-right: auto;
    width: 50%;
}
</style>
<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
			heading: "Updates",
			items: [
					[ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "admin/adminHome") ]
			]
	]) }
</div>

<div class="ke-page-content">


    <% if (isOnline) { %>

        <% if (updatesAvailable) { %>
    <div class="ke-panel-frame">
        <div class="ke-panel-heading"> Update Available</div>
        <div class="ke-panel-content">
            <table class="ke-table-vertical">
                <thead>
                <tr>
                    <th width="10%">Version</th>
                    <th width="10%">Release Date</th>
                    <th width="65%">Release Notes</th>
                    <th width="10%">Action</th>

                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${releaseVersion}</td>
                    <td>${releaseDate}</td>
                    <td>${releaseNotes}</td>

                    <td>
                        <a href="${ url }"   target="_blank">
                            <button class="download-button">
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
        <div class="ke-panel-heading"></div>
        <div class="ke-panel-content" style="text-align: center;">
            <span style="font-size: larger"><b>No Updates Available </b></span> <br/>
            <span style="font-size: medium"> You are currently running the latest version</span>
            <span style="font-size: large;"><b>${version} </span>
        </div>
    </div>


    <%} %>


    <% } else { %>

    <div class="ke-panel-frame">
        <div class="ke-panel-heading">
        </div>
        <div class="ke-panel-content" style="text-align: center;">
            <img src="${ ui.resourceLink("kenyaemr", "images/no-wifi.png") }" class="ke-glyph" />
            <span style="font-size: larger"><b>No Internet Connection </b></span> <br/>
            <span style="font-size: medium">Please check your internet connection and try again.</span>
        </div>
    </div>

    <% } %>





</div>