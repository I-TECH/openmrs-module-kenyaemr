<%
    config.require("label")

	def val = null
	def extra = config.extra

	if (config.value instanceof java.util.Date) {
		if (config.showTime) {
			val = ui.format(config.value)
		} else {
			val = kenyaUi.formatDate(config.value)
		}

		if (config.showDateInterval) {
			extra = kenyaUi.formatInterval(config.value)
		}
	} else {
		val = ui.format(config.value)
	}

	if (config.extra instanceof java.util.Date) {
		extra = kenyaUi.formatDate(config.extra)
	}
%>
<div class="datapoint"><span class="datapoint-label">${ config.label }</span>: <span class="datapoint-value">${ ui.escapeHtml(val) }</span><% if (extra) { %> <span class="datapoint-extra">(${ ui.escapeHtml(extra) })</span><% } %></div>