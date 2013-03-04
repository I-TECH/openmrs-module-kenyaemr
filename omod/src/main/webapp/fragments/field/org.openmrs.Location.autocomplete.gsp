<%= ui.includeFragment("uilibrary", "widget/autocomplete", [
		selected: config.initialValue ? [ value: config.initialValue.locationId, label: ui.format(config.initialValue) ] : null,
		selectedValue: [ config.initialValue?.name ],
        formFieldName: config.formFieldName,
        source: ui.actionLink("kenyaemr", "kenyaEmrUtil", "locationSearch"),
        showGetAll: true,
        size: 40
]) %>

<% if (config.parentFormId) { %>
	<script type="text/javascript">
	    FieldUtils.defaultSubscriptions('${ config.parentFormId }', '${ config.formFieldName }', '${ config.id }');
	    jq(function() {
	    	jq('#${ config.id }').change(function() {
	    		publish('${ config.parentFormId }/changed');
	    	});
	    });
	</script>
<% } %>