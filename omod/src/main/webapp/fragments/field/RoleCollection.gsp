<%
// throw new RuntimeException(config.toString())
	config.require("formFieldName")
	// config supports separator (defaults to "<br/>")
	
	def separator = config.separator ?: "<br/>"

	def assignableRoles = context.userService.allRoles.findAll {
		it.role != "Anonymous" && it.role != "Authenticated"
	}
%>

<pre>${ config.toString() }</pre>

<div id="${ config.id }">
	<span id="${ config.id }-error" class="error" style="display: none"></span>
	<% assignableRoles.each { %>
		<input type="checkbox" name="${ config.formFieldName }" value="${ it.role }" id="${ config.id }-${ it.uuid }"
			<% if (config.initialValue && config.initialValue.contains(it)) { %> checked="true" <% } %> />
			<label for="${ config.id }-${ it.uuid }">${ it.role }</label>
		${ separator }
	<% } %>
</div>

<% if (config.parentFormId) { %>
	<script>
	    FieldUtils.defaultSubscriptions('${ config.parentFormId }', '${ config.formFieldName }', '${ config.id }');
	    jq(function() {
	    	jq('#${ config.id } input[type=checkbox]').change(function() {
	    		publish('${ config.parentFormId }/changed');
	    	});
	    });
	</script>
<% } %>