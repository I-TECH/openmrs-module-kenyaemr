<%
	def moduleVersionFull = "v" + moduleVersion
	if (moduleBuildDate)
		moduleVersionFull += " (" + kenyaUi.formatDate(moduleBuildDate) + ")"
%>

<div id="pageheader">
	<div style="float: left; padding-right: 3px">
		<a href="/${ contextPath }/index.htm?<% if (config.context) { %>${ config.context }<% } %>">
			<img src="${ ui.resourceLink("kenyaui", "images/openmrs.png") }" width="50" height="50"/>
		</a>
	</div>
	<div style="float: left">
		<span style="font-size: 24px;">${ ui.message("kenyaemr.title") }</span>
		<span style="font-size: 10px;">
			${ moduleVersionFull }, powered by <a style="color: #000; text-decoration: none; border-bottom: 1px dotted #999" href="http://openmrs.org">OpenMRS</a>
		</span>
		<br/>
		<% if (systemLocation) { %>
			<span style="font-weight: bold; margin-left: 12px; border-top: 1px gray solid;">${ ui.format(systemLocation) }</span>
		<% } %>
	</div>

	<div style="float: right; text-align: right">
		<img src="${ ui.resourceLink("kenyaui", "images/moh_logo.png") }"/>
	</div>
	<div style="float: right; text-align: right; font-size: 13px; padding-right: 5px">
		${ ui.message("kenyaemr.subtitle") }
	</div>

	<div style="clear: both"></div>
</div>