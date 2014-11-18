<% carePanels.each { carePanel -> %>
${ ui.includeFragment(carePanel.provider, carePanel.path, [ patient: patient, complete: complete ])}
<% } %>