<%
    ui.includeJavascript("uilibrary", "coreFragments.js")
%>

<script type="text/javascript">
	jq(function() {
		jq('#${ config.id }').datetimepicker({
            dateFormat: 'dd-M-yy',
            timeFormat: '(hh:mm:ss)',
            altFieldTimeOnly: false,
            altField: '#${ config.id }_hidden',
		    altFormat: 'yy-mm-dd',
		    altTimeFormat: 'hh:mm:ss',
		    changeMonth: true,
		    changeYear: true,
		    showButtonPanel: true,
            yearRange: '-110:+5'
			<% if (config.required) { %>
				, onClose: function(dateText, inst) { clearErrors('${ config.id }-error'); validateRequired(dateText, '${ config.id }-error'); }
			<% } %>
		});
	});
</script>

<input id="${ config.id }_hidden" type="hidden" name="${ config.formFieldName }" />
<input id="${ config.id }" type="text" size="25"/>
<span id="${ config.id }-error" class="error" style="display: none"></span>


<% if (config.parentFormId) { %>
<script type="text/javascript">
	subscribe('${ config.parentFormId }.reset', function() {
		jq('#${ config.id }').datetimepicker('setDate', null);
	    jq('#${ config.id }-error').html("").hide();
	});
	subscribe('${ config.parentFormId }.clear-errors', function() {
	    jq('#${ config.id }-error').html("").hide();
	});
	subscribe('${ config.parentFormId }/${ config.formFieldName }.show-errors', function(message, payload) {
	    FieldUtils.showErrorList('${ config.id }-error', payload);
	});
	
	jq(function() {
		<% if (config.initialValue) { %>
			jq('#${ config.id }').datetimepicker('setDate', '${ ui.format(config.initialValue) }');
			jq('#${ config.id }').datetimepicker('setTime', '${ ui.format(config.initialValue) }');
		<% } %>
    	jq('#${ config.id }').change(function() {
    		publish('${ config.parentFormId }/changed');
    	});
    });
</script>
<% } %>