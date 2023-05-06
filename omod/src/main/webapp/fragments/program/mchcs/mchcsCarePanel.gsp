<%
	ui.decorateWith("kenyaui", "panel", [heading: "Child Services Care"])

	def dataPointsLeft = []
	dataPointsLeft << [label: "Current prophylaxis used: ", value: calculations.prophylaxis]
	dataPointsLeft << [label: "Current Feeding Option", value: calculations.feeding]
	dataPointsLeft << [label: "Remarks", value: calculations.remarks]
	dataPointsLeft << [label: "Milestones Attained", value: calculations.milestones]
	dataPointsLeft << [label: "HIV Status", value: calculations.hivStatus]
	dataPointsLeft << [label: "HEI Outcome", value: calculations.heioutcomes]
    def dataPointsRight = []

    obbList = calculations.obbListView
    for (testResult in obbList) {
        orderReason = testResult.orderReason
        pcrDate = testResult.pcrDate.format("yyyy-MM-dd")
        testResults = testResult.testResults
        dataPointsRight << [label: "PRCs Done", value: "Order Reason: $orderReason, Date: $pcrDate, Test:  $testResults"]
    }

     %>
<div style="display: flex;">
<div style="width: 50%;">
   <% dataPointsLeft.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>
<div style="width: 50%;">
    <% dataPointsRight.each { it -> print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>

</div>
</div>