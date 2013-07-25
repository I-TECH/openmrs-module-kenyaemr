<% programs.each { programDescriptor -> %>
	${ ui.includeFragment(
			programDescriptor.careSummaryFragment.provider,
			programDescriptor.careSummaryFragment.path,
			[ patient: patient, complete: complete, allowRegimenEdit: (visit != null)]
	)}
<% } %>