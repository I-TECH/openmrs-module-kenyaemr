<%
	ui.decorateWith("kenyaui", "panel", [heading: "Child Services Care"])
	def dataPointsLeft = []
	dataPointsLeft << [label: "Current prophylaxis used: ", value: calculations.prophylaxis]
	dataPointsLeft << [label: "Current Feeding Option", value: calculations.feeding]
	dataPointsLeft << [label: "Milestones Attained", value: calculations.milestones]
	dataPointsLeft << [label: "HIV Status", value: calculations.hivStatus]
	dataPointsLeft << [label: "HEI Outcome", value: calculations.heioutcomes]
    def dataPointsRight = []

    obbList = calculations.obbListView
    def ageInWeeks = calculations.ageInWeeks
    def latestResults = [:]
    obbList.each { testResult ->
        def orderReason = testResult.orderReason
        def pcrDate = testResult.pcrDate.format("yyyy-MM-dd")
        def testResults = testResult.testResults
        switch (orderReason) {
            case "HIV RAPID TEST 1, QUALITATIVE":
                if (ageInWeeks <= 6) {
                    latestResults."PCR test for 6 weeks" = [label: "PCRs Done:6 weeks -", value: "$testResults($pcrDate)"]
                }
                break
            case "HIV RAPID TEST 2, QUALITATIVE":
                if (ageInWeeks > 6 && ageInWeeks <= 48) {
                    latestResults."PCR test for 6 months" = [label: "PCRs Done:6 months -", value: "$testResults($pcrDate)"]
                }
                break
            case "HIV DNA POLYMERASE CHAIN REACTION":
                if (ageInWeeks > 48) {
                    latestResults."PCR test for 12 months" = [label: "PCRs Done:12 months -", value: "$testResults($pcrDate)"]
                }
                break
        }
    }
    dataPointsRight.addAll(latestResults.values())
     %>
<div style="display: flex;">
<div style="width: 50%;">
   <% dataPointsLeft.each { print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>
</div>
<div style="width: 50%;">
    <% dataPointsRight.each { it -> print ui.includeFragment("kenyaui", "widget/dataPoint", it) } %>

</div>
</div>