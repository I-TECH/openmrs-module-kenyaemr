<%
// supports config.type = radio (otherwise a select list)

def widget = "selectList"
if (config?.config?.type == 'radio')
	widget = "radioButtons"
%>

<%= ui.includeFragment("kenyaui", "widget/${ widget }", [
        selected: [ config?.initialValue?.id ],
        formFieldName: config.formFieldName,
        options: context.getVisitService().getAllVisitTypes().findAll { !it.retired },
        optionsDisplayField: 'name',
        optionsValueField: 'id'
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