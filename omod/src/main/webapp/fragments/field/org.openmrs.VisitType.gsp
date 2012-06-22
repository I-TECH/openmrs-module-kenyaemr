<%
// supports config.type = radio (otherwise a select list)

def widget = "selectList"
if (config?.config?.type == 'radio')
	widget = "radioButtons"
%>

<%= ui.includeFragment("widget/${ widget }", [
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