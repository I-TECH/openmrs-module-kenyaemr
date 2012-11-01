<!-- This is the login page -->

<%
	ui.includeCss("uilibrary", "uilibrary.css")
%>

<style type="text/css">
	#fullwindow {
		left: 0;
		width: 100%;
		height: 100%;
		display: table;
		position: absolute;
		background-color: #e0e0e0;
	}
	
	.centered {
		vertical-align: middle;
		display: table-cell;
		margin-left: auto;
		margin-right: auto;
	}
	
	#errors {
		margin: 1em;
		text-align: center;
		background-color: #FFFFBB;
		border: 1px gray solid;
	}
</style>

<div id="kenya-header">
	${ ui.includeFragment("kenyaemr", "kenyaHeader") }
</div>

<div id="fullwindow">
	<div class="centered">
		${ ui.includeFragment("uilibrary", "flashMessage") }

		<form method="post" action="/${ contextPath }/loginServlet" autocomplete="off">
			<table class="centered">
				<tr>
					<td rowspan="5" width="300">
						<img src="${ ui.resourceLink("kenyaemr", "images/moh_logo_large.png") }"/>
					</td>
					<td colspan="2"><div style="height:100px;">&nbsp;</div></td>
				</tr>
				<tr>
					<th>Username</th>
					<td><input id="uname" type="text" name="uname"/></td>
				</tr>
				<tr>
					<th>Password</th>
					<td><input type="password" name="pw"/></td>
				</tr>
				<tr>
					<th></th>
					<td>
						<input type="submit" value="Login"/>
					</td>
				</tr>
				<tr>
					<td colspan="2"></td>
				</tr>
				<tr>
					<td><div style="height: 100px">&nbsp;</div></td>
				</tr>
			</table>
			
		</form>
	</div>
</div>

<script type="text/javascript">
	document.getElementById('uname').focus();
</script>
