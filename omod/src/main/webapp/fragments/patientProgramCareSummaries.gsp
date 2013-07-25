<% programs.each { programDescriptor ->
	def fragment = programDescriptor.careSummaryFragment.split(":")
%>

	${ ui.includeFragment(fragment[0], fragment[1], [ patient: patient, complete: complete, allowRegimenEdit: (visit != null)]) }
<% } %>