<%
// supports style (css style)
// supports returnUrl (where to return to after clicking edit, defaults to the current page)

def returnUrl = config.returnUrl ?: ui.thisUrl()

// listens for the event (id)/showEncounter (takes an object with an encounterId property, and optional editButtonLabel property)
%>

<style type="text/css">
.html-form-buttons {
	position: absolute;
	top: 0px;
	right: 0px;
}

.html-form-edit-history {
	font-size: 0.8em;
	margin-top: 3em;
	border-top: 1px gray solid;
	color: gray;
}

.html-form-edit-history:hover {
	color: black;
}

.html-form-edit-history .title {
	text-decoration: underline;
	margin: 0.4em 0em;
}
</style>

<script type="text/javascript" src="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.js"></script>
<link href="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.css" type="text/css" rel="stylesheet" />

<div id="${ config.id }" <% if (config.style) { %>style="${ config.style }"<% } %>>
</div>

<script type="text/javascript">
	\$j = jQuery;
	var propertyAccessorInfo = new Array();

	// TODO move to js resource
	function confirmDeleteEncounter(encounterId, returnUrl) {
		var doIt = confirm('Are you sure you want to delete this form?');
		if (doIt) {
			ui.getFragmentActionAsJson('kenyaemr', 'showHtmlForm', 'deleteEncounter', { encounterId: encounterId }, function(data) {
				location.href = returnUrl;
			}); 
		}
	}

	subscribe('${ config.id }/showEncounter', function(message, payload) {
		jq('#${ config.id }').html('');
		ui.getFragmentActionAsJson('kenyaemr', 'showHtmlForm', 'viewFormHtml', { encounterId: payload.encounterId }, function(data) {
			var toShow = ''
			if (payload.editButtonLabel || payload.deleteButtonLabel) {
				toShow += '<div class="html-form-buttons">';
				if (payload.editButtonLabel) {
					var onClick = "location.href = '" + ui.pageLink('kenyaemr', 'editHtmlForm', { encounterId: payload.encounterId, returnUrl: '${ returnUrl }' }) + "';";
					toShow += '<input type="button" value="' + ui.escapeHtmlAttribute(payload.editButtonLabel) + '" onClick="' + onClick + '"/>';
				}
				if (payload.deleteButtonLabel) {
					toShow += '<input class="delete-button" type="button" value="' + ui.escapeHtmlAttribute(payload.deleteButtonLabel) + '"/>';
				}
				toShow += '</div>';
			}
			toShow += data.html;
			
			toShow += '<div class="html-form-edit-history"> <div class="title">Edit History</div>';
			for (var i = 0; i < data.editHistory.length; ++i) {
				var item = data.editHistory[i];
				toShow += item.user + ' on ' + item.timestamp + ': ' + item.description + '<br/>';
			}
			toShow += '</div>';
			
			jq('#${ config.id }').html(toShow);
			if (payload.deleteButtonLabel) {
			 	jq('.html-form-buttons .delete-button').click(function() {
					confirmDeleteEncounter(payload.encounterId, '${ returnUrl }');
				});
			}
		});
	});
</script>