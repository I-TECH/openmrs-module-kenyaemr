<div class="ke-page-header">
	<div style="float: left; padding-right: 3px">
		<a href="/${ contextPath }/index.htm?<% if (config.context) { %>${ config.context }<% } %>">
			<img src="${ ui.resourceLink("kenyaemr", "images/logos/kenyaemr.png") }" width="48" height="48" />
		</a>
	</div>
	<div style="float: left">
		<span style="font-size: 24px;">${ ui.message("kenyaemr.title") }</span>
		<span style="font-size: 10px; color: #7f7b72;">${ moduleVersion }, powered by OpenMRS</span>
		<br/>
		<% if (systemLocation) { %>
			<span style="font-weight: bold; margin-left: 12px; border-top: 1px gray solid;">${ ui.format(systemLocation) }</span>
			<span style="font-size: 10px;">(${ systemLocationCode })</span>
		   <!-- pw greencard additions  -->
		     <span style="font-size: 10px;">Tier 2</span>
		    <span style="font-weight: bold; 10px;">Siaya County</span>
		    <span style="font-size: 10px;"> Bondo Sub-County</span>
		    <!-- .pw greencard additions  -->
		<% } %>
	</div>

	<div style="float: right; text-align: right">
		<img src="${ ui.resourceLink("kenyaemr", "images/logos/moh.png") }" width="48" height="48" />
	</div>
	<div style="float: right; text-align: right; font-size: 13px; font-weight: bold; padding: 9px 5px 0 0; color: #7f7b72;">
		${ ui.message("kenyaemr.subtitle") }
	</div>

	<div style="clear: both"></div>
</div>