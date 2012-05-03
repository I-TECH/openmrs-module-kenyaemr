<%= ui.includeFragment("widget/selectList", [
        selected: [config.initialValue],
        formFieldName: config.formFieldName,
        options: context.getVisitService().getAllVisitTypes(),
        optionsDisplayField: 'name',
        optionsValueField: 'id'
]) %>

<% if (config.parentFormId) { %>
<script>
    FieldUtils.defaultSubscriptions('${ config.parentFormId }', '${ config.formFieldName }', '${ config.id }');
    jq(function() {
    	jq('#${ config.id }').change(function() {
    		publish('${ config.parentFormId }/changed');
    	});
    });
</script>
<% } %>