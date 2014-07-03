package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.module.kenyaemr.reporting.library.shared.mchcs.MchcsCohortLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of DHIS2 related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class Dhis2IndicatorLibrary {

	@Autowired
	private MchcsCohortLibrary mchcsCohortLibrary;

	/**
	 * Number of infant patients who took pcr test aged 2 months and below
	 * @return the indicator
	 */
	public CohortIndicator pcrInitialWithin2Months() {
		return cohortIndicator("Infants given pcr within 2 months-dhis",
				map(mchcsCohortLibrary.pcrInitialWithin2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of infant patients who took pcr test aged 3 to 8 months and below
	 * @return the indicator
	 */
	public CohortIndicator pcrInitial3To8Months() {
		return cohortIndicator("Infants given pcr within 3 to 8 months-dhis",
				map(mchcsCohortLibrary.pcrInitialBetween3To8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of infant patients who took antibody test aged between 9 and 12 months
	 * @return the indicator
	 */
	public CohortIndicator serologyAntBodyTestBetween9And12Months() {
		return cohortIndicator("Infants given antibody aged between 9 and 12 months-dhis",
				map(mchcsCohortLibrary.serologyAntBodyTestBetween9And12Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of infant patients who took PCR test aged between 9 and 12 months
	 * @return the indicator
	 */
	public CohortIndicator pcrTestBetween9And12Months() {
		return cohortIndicator("Infants given pcr aged between 9 and 12 months-dhis",
				map(mchcsCohortLibrary.pcrBetween9And12MonthsAge(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number HEI tested by 12 months
	 * @return the indicator
	 */
	public CohortIndicator totalHeiTestedBy12Months() {
		return cohortIndicator("Total HEI tested by 12 months-dhis",
				map(mchcsCohortLibrary.totalHeitestedBy12Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of confirmed pcr positive infants aged 2 months and below
	 * @return the indicator
	 */
	public CohortIndicator pcrConfirmedPositive2Months() {
		return cohortIndicator("Number of pcr confirmed positive infants aged 2 months-dhis",
				map(mchcsCohortLibrary.pcrConfirmedPositive2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of confirmed pcr positive infants aged between 3 and 8 months
	 * @return the indicator
	 */
	public CohortIndicator pcrConfirmedPositiveBetween3To8Months() {
		return cohortIndicator("Number of pcr confirmed positive infants aged between 3 and 8 months-dhis",
				map(mchcsCohortLibrary.pcrConfirmedPositiveBetween3To8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of confirmed pcr positive infants aged between 9 and 12 months
	 * @return the indicator
	 */
	public CohortIndicator pcrConfirmedPositiveBetween9To12Months() {
		return cohortIndicator("Number of pcr confirmed positive infants aged between 9 and 12 months-dhis",
				map(mchcsCohortLibrary.pcrConfirmedPositiveBetween9To12Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of infants confirmed positive
	 * @return the indicator
	 */
	public CohortIndicator pcrTotalConfirmedPositive() {
		return cohortIndicator("Total Confirmed Positive Infants-dhis",
				map(mchcsCohortLibrary.pcrTotalConfirmedPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}
}
