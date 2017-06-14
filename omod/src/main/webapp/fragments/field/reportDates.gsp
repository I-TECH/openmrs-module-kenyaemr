<%
	def startFieldName = config.startFieldName ?: "param[startDate]";
	def endFieldName = config.endFieldName ?: "param[endDate]";
%>
<script type="text/javascript">
	jQuery(function() {
		jQuery('input:text[id^=${ config.id }]').datepicker({
			dateFormat: "yy-mm-dd",
			onSelect: function(dateText, obj) {
				if (obj.id.indexOf('_end') > -1) {
					jQuery('#date_value').val(dateText);
				}
			}		
		});
	});
</script>

Start Date: <input id="${ config.id }_start" type="text" name="${ startFieldName }" /> 
End Date: <input id="${ config.id }_end" type="text" name="${ endFieldName }" /> 

<span id="${ config.id }-error" class="error" style="display: none"></span>

<% if (config.parentFormId) { %>
<script type="text/javascript">
	// TODO
</script>
<% } %>
<input type="hidden" id="date_value" />
