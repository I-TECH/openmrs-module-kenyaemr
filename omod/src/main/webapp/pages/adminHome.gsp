<%
	ui.decorateWith("standardKenyaEmrPage")
	ui.includeJavascript("jquery-ui.js")
	
	def formatMap = { map ->
		def ret = "<table>"
		map.each {
			ret += '<tr valign="top">'
			ret += "<th>${ it.key }</th>"
			ret += "<td>${ ui.format(it.value) }</td>"
			ret += "</tr>"
		}
		ret += "</table>"
		return ret
	}
%>

<script>
	jq(function() {
		jq('.accordion').accordion();
	});
</script>

<div style="float: right; width: 30%;">
	<fieldset>
		<legend>Actions</legend>
		${ ui.includeFragment("widget/button", [
				iconProvider: "uilibrary",
				icon: "users_32.png",
				label: "Manage Users",
				href: ui.pageLink("adminManageUsers")
			]) }
		<br/>

		${ ui.includeFragment("widget/button", [
				iconProvider: "uilibrary",
				icon: "users_business_32.png",
				label: "Manage Providers"
			]) }
		<br/>

		${ ui.includeFragment("widget/button", [
				iconProvider: "uilibrary",
				icon: "refresh.png",
				label: "Install New<br/>Software Version"
			]) }
		<br/>
		
	</fieldset>
</div>

<div style="float: left; width: 65%">
	<div class="accordion">
		<% info.each { %>
			<h3><a href="#">${ it.key }</a></h3>
			<div>
				<% if (it.value instanceof java.util.Map) { %>
					${ formatMap(it.value) }
				<% } else { %>
					${ ui.format(it.value) }
				<% } %>
			</div>
		<% } %>
	</div>
</div>