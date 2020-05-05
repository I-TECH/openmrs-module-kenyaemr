<%

	def groupOptions = {
		it.regimens.collect( { reg -> """<option value="${ reg.conceptRef }">${ reg.name }</option>""" } ).join()
	}
%>
<script type="text/javascript">

	jq(function () {
		//On Ready
		jq('#drugs-select').hide();
		var nonStandardRegimenCkbox = jq('input[name="nonstandardregimen"]');

		nonStandardRegimenCkbox.on('click', function(){
			if ( jq(this).is(':checked') ) {
				jq('#drugs-select').show();
				jq('#standard').val('')
				jq('#standard').prop('disabled', 'disabled');
			}else {
				jq('#drugs-select').hide();
				jq('#non-standard').val('')
				jq('#standard').prop('disabled', false);
			}
		});


	});

</script>

<div id="${ config.id }-container">
	<input type="hidden" id="${ config.id }" name="${ config.formFieldName }" />
	<i>Use standard:</i> <select class="standard-regimen-select" name="regimenConceptRef" id="standard">
		<option label="Select..." value="" />
	<option value="">Select...</option>
		<% regimenGroups.each { group -> %>
			<optgroup label="${ group.name }">${ groupOptions(group) }</optgroup>
	<% } %>
	</select>
	<%if (config.category =="ARV") { %>
	<span id="others-checked">
		<input type="checkbox" name="nonstandardregimen" value="nonstandardregimen" id="other">Use none standard<br>
	</span>
	<% } %>
	<br />
	<span id="${ config.id }-error" class="error" style="display: none"></span>


	<div id="drugs-select">

		<i>Drug:</i>
		<select class="standard-regimen-select" id="non-standard" name="regimenConceptNonStandardRef">
		<option label="Select..." value="" />
			<% arvDrugs.each { drug -> %>
			<option value="${ drug.drugUuid }">${ drug.name }</option>
		<% } %>
	</select><br />
		<i>Drug:</i>
		<select class="standard-regimen-select" name="regimenConceptNonStandardRefOne">
		<option label="Select..." value="" />
			<% arvDrugs.each { drug -> %>
			<option value="${ drug.drugUuid }">${ drug.name }</option>
		<% } %>
	</select><br />
		<i>Drug:</i>
		<select class="standard-regimen-select" name="regimenConceptNonStandardRefTwo">
		<option label="Select..." value="" />
			<% arvDrugs.each { drug -> %>
			<option value="${ drug.drugUuid }">${ drug.name }</option>
		<% } %>
	</select><br />
		<i>Drug:</i>
		<select class="standard-regimen-select" name="regimenConceptNonStandardRefThree">
		<option label="Select..." value="" />
		<% arvDrugs.each { drug -> %>
		<option value="${ drug.drugUuid }">${ drug.name }</option>
		<% } %>
	</select><br />
		<i>Drug:</i>
		<select class="standard-regimen-select" name="regimenConceptNonStandardRefFour">
		<option label="Select..." value="" />
			<% arvDrugs.each { drug -> %>
			<option value="${ drug.drugUuid }">${ drug.name }</option>
		<% } %>
	</select><br />
	</div>

</div>

<% if (config.parentFormId) { %>
<script type="text/javascript">
	jq(function() {
		subscribe('${ config.parentFormId }.reset', function() {
			jq('#${ config.id } input, #${ config.id } select').val('');
		});

		jq('#${ config.id }').change(function() {
			publish('${ config.parentFormId }/changed');
		});
	});
</script>
<% } %>
