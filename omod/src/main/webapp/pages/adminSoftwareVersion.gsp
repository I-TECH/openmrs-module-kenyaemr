<%
	ui.decorateWith("kenyaemr", "standardPage")
%>

<h2>Software Version</h2>

<% if (priorVersion) { %>
	<h3>Upgraded from ${ priorVersion } to ${ currentKenyaEmrVersion }</h3>
<% } else { %>
	<h3>Current Version: ${ currentKenyaEmrVersion }</h3>
<% } %>

<fieldset>
	<legend>Update from Internet</legend>
	
	TODO: a future version of the <i>moduledistro</i> module will support subscribing to a URL, and checking that URL for updates.
	Once that happens, the preferred way of doing updates is to have them be downloaded automatically, and have them notify the
	system administrator that a new version is ready for installation. At that point they just have to click a button here when
	they don't mind a bit of server downtime.  
</fieldset>

<br/>

<fieldset>
	<legend>Update Kenya EMR Distribution zip file</legend>
	<form id="file-upload-form" method="post" enctype="multipart/form-data">
		<input type="hidden" name="priorVersion" value="${ priorVersion }"/>
		<input type="file" name="distributionZip"/>
		<input type="submit" value="Upload and Install"/>
	</form>
</fieldset>

<% if (log) { %>
	<br/>
	<u><b>Log</b></u>
	<pre>${ log.join("\n") }</pre>
<% } %>

<script type="text/javascript">
jq(function() {
	jq('#file-upload-form').submit(function() {
		ui.openLoadingDialog('Uploading. This may take a while...');
	});
});
</script>