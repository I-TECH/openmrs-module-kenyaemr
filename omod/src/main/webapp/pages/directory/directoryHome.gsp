<%
	ui.includeJavascript("kenyaemr", "controllers/account.js")

	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>
<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaemr", "account/accountSearchForm", [ defaultWhich: "all" ]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "account/accountSearchResults") }
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('input[name="query"]').focus();
	});
</script>
