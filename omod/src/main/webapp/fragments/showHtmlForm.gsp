<%
// supports style (css style)
// supports returnUrl (where to return to after clicking edit, defaults to the current page)

def returnUrl = config.returnUrl ?: ui.thisUrl()

// listens for the event (id)/showEncounter (takes an object with an encounterId property, and optional editButtonLabel property)
%>

<style>
.html-form-buttons {
	position: absolute;
	top: 0px;
	right: 0px;
}
</style>

<link href="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.css" type="text/css" rel="stylesheet" />

<div id="${ config.id }" <% if (config.style) { %>style="${ config.style }"<% } %>>
</div>

<script>
	// TODO move to js resource
	function confirmDeleteEncounter(encounterId, returnUrl) {
		var doIt = confirm('Are you sure you want to delete this form?');
		if (doIt) {
			ui.getFragmentActionAsJson('showHtmlForm', 'deleteEncounter', { encounterId: encounterId }, function(data) {
				location.href = returnUrl;
			}); 
		}
	}

	subscribe('${ config.id }/showEncounter', function(message, payload) {
		jq('#${ config.id }').html('');
		getFragmentActionAsJson('showHtmlForm', 'viewFormHtml', { encounterId: payload.encounterId }, function(data) {
			var toShow = ''
			if (payload.editButtonLabel || payload.deleteButtonLabel) {
				toShow += '<div class="html-form-buttons">';
				if (payload.editButtonLabel) {
					var onClick = "location.href = '" + ui.pageLink('editHtmlForm', { encounterId: payload.encounterId, returnUrl: '${ returnUrl }' }) + "';";
					toShow += '<input type="button" value="' + ui.escapeHtmlAttribute(payload.editButtonLabel) + '" onClick="' + onClick + '"/>';
				}
				if (payload.deleteButtonLabel) {
					toShow += '<input class="delete-button" type="button" value="' + ui.escapeHtmlAttribute(payload.deleteButtonLabel) + '"/>';
				}
				toShow += '</div>';
			}
			toShow += data.html;
			jq('#${ config.id }').html(toShow);
			if (payload.deleteButtonLabel) {
			 	jq('.html-form-buttons .delete-button').click(function() {
					confirmDeleteEncounter(payload.encounterId, '${ returnUrl }');
				});
			}
		});
	});
</script>