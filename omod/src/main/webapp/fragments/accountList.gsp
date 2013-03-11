<%
	ui.decorateWith("kenyaui", "panel", [ heading: config.heading ])

	// config supports "page", which will set up a clickFunction, that will have personId=... appended
	// provides a default numResultsFormatter to widget/stack unless you override it
	
	if (!config.numResultsFormatter) {
		config.numResultsFormatter = """function(listOfItems) { return listOfItems.length + " account(s)"; }""" 
	}
	
	def clickFunction = null
	if (config.page) {
		clickFunction = """function () {
				location.href = ui.pageLink('kenyaemr', '${ config.page }', { personId: jq(this).find('input[name=personId]').val() });
			}"""
	}
%>
<script type="text/javascript">
	var accountItemOpts = {
		icon: '<img width="32" height="32" src="${ ui.resourceLink("kenyaui", "images/buttons/account.png") }"/>',
		title: function(account) {
			return account.personName + '<input type="hidden" name="personId" value="' + account.personId + '"/>';
		},
		leftDetails: function(account) {
			var tmp = [];
			if (account.user) {
				tmp.push("User");
			}
			if (account.provider) {
				tmp.push("Provider");
			}
			return tmp.join(" + ");
		},
		right: function(account) {
			var tmp = [];
			if (account.user) {
				tmp.push('Username: ' + account.user.username);
			}
			if (account.provider) {
				tmp.push('Provider ID: ' + account.provider.identifier);
			}
			return tmp.join("<br/>");
		}
	};
	
	function formatAccountAsStackItem(account) {
		return kenyaemr.twoColumnStackItemFormatter(account, accountItemOpts);
	}
</script>

<%= ui.includeFragment("kenyaemr", "widget/stack", config.merge([ itemFormatter: "formatAccountAsStackItem", clickFunction: clickFunction ])) %>