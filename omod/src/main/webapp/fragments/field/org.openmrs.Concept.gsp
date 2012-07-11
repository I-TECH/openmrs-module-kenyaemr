<%
if (!config?.config?.answerTo) throw new RuntimeException("Concept field currently only supports config.answerTo mode")
// supports config.type = radio (otherwise a select list)

def options = [ [ id: "", name: "" ] ]
options.addAll(config.config.answerTo.answers.collect {
		[ id: it.answerConcept.id, name: ui.format(it.answerConcept) ]
	})

def widget = "selectList"
if (config?.config?.type == 'radio')
	widget = "radioButtons"
%>

<%= ui.includeFragment("widget/${ widget }", [
		id: config.id,
        selected: [ config?.initialValue?.id ],
        formFieldName: config.formFieldName,
        options: options,
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