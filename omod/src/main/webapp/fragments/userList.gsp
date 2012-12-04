<%
	// config supports "page", which will set up a clickFunction, that will have userId=... appended
	// provides a default numResultsFormatter to widget/stack unless you override it
	
	if (!config.numResultsFormatter) {
		config.numResultsFormatter = """function(listOfItems) { return listOfItems.length + " user(s)"; }""" 
	}
	
	def clickFunction = null
	if (config.page) {
		clickFunction = """function () {
				location.href = ui.pageLink('kenyaemr', '${ config.page }', { userId: jq(this).find('input[name=userId]').val() });
			}"""
	}
%>
<script type="text/javascript">
	var userItemOpts = {
		icon: '<img width="32" height="32" src="${ ui.resourceLink('uilibrary', 'images/user_32.png') }"/>',
		title: function(user) {
			return user.personName + '<input type="hidden" name="userId" value="' + user.userId + '"/>';
		},
		leftDetails: function(user) {
			var ret = '';
			if (user.username)
				ret += user.username + ' ';
			if (user.systemId)
				ret += user.systemId;
			return ret;
		},
		right: function(user) {
			return user.roles;
		}
	};
	
	function formatUserAsStackItem(user) {
		return kenyaemr.threeColumnStackItemFormatter(user, userItemOpts);
	}
</script>

<%= ui.includeFragment("kenyaemr", "widget/stack", config.merge([ itemFormatter: "formatUserAsStackItem", clickFunction: clickFunction ])) %>