<%
// supports url (submit as a form to that url)
// supports page (submit as a form to a page)
// supports fragment + action (submit as json to a fragment action)

// supports mode (form|json)
// supports cancelFunction
// supports successCallbacks (a list of js snippets wrapped in function(data) { ... })
// supports submitOnEvent (will subscribe to the given topic, and submit when it fires)

// supports fields (list, whose elements can be)
//		[ label, formFieldName, class, fieldConfig ] ... delegates to the field fragment
//		[ fragment ] ... includes a fragment
//		[ value ] ... displays a value
//		[ hiddenInputName, value ] ... includes a hidden value
// supports commandObject + properties + hiddenProperties + prefix (introspects fields from a java object)

// supports noDecoration

	import org.openmrs.ui.framework.fragment.FragmentConfiguration
	import org.apache.commons.beanutils.PropertyUtils

	ui.includeJavascript("coreFragments.js")
	
    def id = config.id ?: ui.randomId("form")
    
    def mode = config.mode ?: "form"
    def url = config.url
    
    if (config.page) {
		url = ui.pageLink(config.page)
	} else if (config.fragment && config.action) {
		mode = "json"
		url = ui.actionLink(config.fragment, config.action, [successUrl: config.returnUrl])
	}

	def fields = config.fields
	if (config.commandObject) {
		if (!fields)
    		fields = []
    	// make fields for: hiddenProperties, properties
    	// prefix all formFieldNames with: prefix
        def messagePrefix = config.commandObject.class.simpleName
    	def prefix = config.prefix ?: ""
    	if (config.hiddenProperties) {
    		config.hiddenProperties.each { propName ->
    			fields << [ hiddenInputName: "${ prefix }.${ propName }", value: config.commandObject."${ propName }" ] 
    		}
    	}
    	if (config.properties) {
    		config.properties.each { propName ->
    		    fields << [ label: ui.message("${ messagePrefix }.${ propName }"),
    		                formFieldName: "${ prefix }.${ propName }",
    		                class: PropertyUtils.getPropertyType(config.commandObject, propName) ]
    		}
    	}
    }  
%>

<form id="${ id }" action="${ url }" method="post"<% if (config.noDecoration) { %> style="display: inline"<% } %>>

    <div style="display: none" id="${ id }-globalerror" class="error"></div> 

<% fields.each {
    def fragment
    if (it.fragment)
        fragment = it.fragment
    else if (it.class)
        fragment = "widget/field"
    else if (it.value && !it.hiddenInputName)
    	fragment = "widget/field"
    if (fragment) {
        fieldConfig = new FragmentConfiguration(it)
        fieldConfig.merge([ parentFormId: id, visibleFieldId: ui.randomId("field"), parentFormMode: mode ])
        if (!config.noDecoration)
            fieldConfig.merge([ decorator: "labeled", decoratorConfig: it ])
%>
		<% if (config.noDecoration && fieldConfig.label) { %>${ fieldConfig.label }<% } %>
        ${ ui.includeFragment(fragment, fieldConfig) }
        
<%  } else if (it.hiddenInputName) { %>
        <input type="hidden" name="${ it.hiddenInputName }" value="${ it.value }"/>
        
<%  } else { %>
        Don't know how to handle field ${ it }
        
<%  } %>
<% } %>

<% if (config.submitLabel) { %>
	<input type="submit" class="button" value="${ config.submitLabel }"/>
<% } %>

<% if (config.cancelLabel) { %>
    <input type="button" class="button" value="${ config.cancelLabel }" onClick="publish('${ id }.reset'); ${ config.cancelFunction }()"/>
<% } %>

</form>

<% if (mode == "json") { %>
    <script>
        jq('#${ id }').submit(function(e) {
            e.preventDefault();
            publish('${ id }.clear-errors');
            var form = jq(this);
            var data = form.serialize();
            jq.ajax({
                type: "POST",
                url: form.attr('action'),
                data: data,
                dataType: "json"
            })
            .success(function(data) {
                publish('${ id }.reset');
               	<% if (config.successEvent) { %>
                	publish('${ config.successEvent }', data);
                <% } %>
            })
            <% if (config.successCallbacks) config.successCallbacks.each { %>
                .success(function(data) {
                    ${ it }
                })
            <% } %>
            .error(function(jqXHR, textStatus, errorThrown) {
            	formWidget.handleSubmitError('${ id }', jqXHR);
            });
        });
    </script>
<% } %>
<% if (config.submitOnEvent) { %>
	<script>
		var timeoutId${ id } = null;
		subscribe('${ config.submitOnEvent }', function() {
			if (timeoutId${ id } != null) {
				clearTimeout(timeoutId${ id });
				timeoutId${ id } = null;
			}
			timeoutId${ id } = setTimeout(function() {
				jq('#${ id }').submit();
			}, 150);
		});
	</script>
<% } %>