<div style="float: left">
	<a href="/${ contextPath }/index.htm?<% if (config.context) { %>${ config.context }<% } %>">
		<img src="${ ui.resourceLink("uilibrary", "images/openmrs_logo_white.gif") }" width="50" height="50"/>
	</a>
</div>
<div style="float: left">
	<span style="font-size: 1.5em;">Kenya EMR</span>
	<span style="font-size: 0.6em;">powered by OpenMRS</span>
	<br/>
	<% if (systemLocation) { %>
		<span style="font-weight: bold; margin-left: 0.5em; border-top: 1px gray solid;">${ ui.format(systemLocation) }</span>
	<% } %>
</div>

<div style="float: right; text-align: right">
	<img src="${ ui.resourceLink("kenyaemr", "images/logo_50.png") }"/>
</div>
<div style="float: right; text-align: right; font-size: 0.9em;">
	Government of Kenya <br/>
	Ministry of Medical Services <br/>
	Ministry of Public Health and Sanitation
</div>


<div style="clear: both"></div>