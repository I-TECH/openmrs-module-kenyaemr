<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<style type="text/css">
	body {
		background-color: #e0d8cd;
	}
</style>
<script type="text/javascript">
	jQuery(function() {
		var browser = kenyaui.getBrowser();
		var name = browser ? browser[0] : '?';

		if (jQuery.inArray(name, [ 'Chrome', 'Firefox' ]) < 0) {
			jQuery('#browser-warning').show();
		}
	});
</script>

<div id="browser-warning" class="ke-warning" style="display: none; text-align: center">
	Access from an unsupported browser detected. Please use <strong>Chrome</strong> or <strong>Firefox</strong> instead.
</div>

<div style="text-align: center; padding-top: 100px">

	<form method="post" action="${ loginServletUrl }" autocomplete="off">
		<table style="margin-left: auto; margin-right: auto; border: 0" cellpadding="3" cellspacing="0">
			<tr>
				<td style="padding-right: 15px">
					<img src="${ ui.resourceLink("kenyaemr", "images/logos/moh.png") }" width="250" height="250"/>
				</td>
				<td style="padding-left: 15px; padding-top: 75px" valign="top">
					<table border="0" cellpadding="3" cellspacing="0">
						<tr>
							<th style="text-align: left">Username</th>
							<td style="text-align: left"><input id="uname" type="text" name="uname" style="width: 200px"/></td>
						</tr>
						<tr>
							<th style="text-align: left">Password</th>
							<td style="text-align: left"><input type="password" name="pw" style="width: 200px" /></td>
						</tr>
						<tr>
							<td></td>
							<td style="text-align: left"><button type="submit"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/login.png") }" /> Login</button></td>
						</tr>
						<tr>
							<td></td>
							<td style="text-align: left; vertical-align: top"><a style="font-size: 12px" href="forgotPassword.form">Forgot password?</a></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</form>
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('#uname').focus();
	});
</script>
