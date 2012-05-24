<!-- This is the login page -->

<style>
	#fullwindow {
		top: 0;
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
</style>

<div id="fullwindow">
	<div class="centered">
		${ ui.includeFragment("flashMessage") }

		<form method="post" action="/${ contextPath }/loginServlet" autocomplete="off">
			<table class="centered">
				<tr>
					<td colspan="2">
						<img src="${ ui.resourceLink("kenyaemr", "images/logo.png") }"/>
						<h3 align="center">Kenya EMR</h3>
					</td>
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
						<br/>
						<input type="submit" value="Login"/>
					</td>
				</tr>
			</table>
			
		</form>
	</div>
</div>

<script>
	document.getElementById('uname').focus();
</script>
