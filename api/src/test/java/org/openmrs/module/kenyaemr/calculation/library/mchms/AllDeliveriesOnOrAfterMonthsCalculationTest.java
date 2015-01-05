package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchms.AllDeliveriesOnOrAfterMonthsCalculation}
 */

public class AllDeliveriesOnOrAfterMonthsCalculationTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private MchMetadata mchMetadata;

    /**
     * Setup each test
     */
    @Before
    public void setup() throws Exception {
        executeDataSet("dataset/test-concepts.xml");
        mchMetadata.install();
    }

	/**
	 * @verifies all deliveries on or after a given date at a facility
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.AllDeliveriesOnOrAfterMonthsCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
    @Test
    public void evaluate_shouldCalculateAllDeliveriesOnOrAfterMonths() throws Exception {
        
        Form deliveryForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);
        EncounterType ancEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);

        // For the purposes of this test, everyone is a woman
        {
            Patient patient = TestUtils.getPatient(7);
            TestUtils.saveEncounter(patient, ancEncounterType, deliveryForm, computeWithinRangeEncounterDate(3));
            TestUtils.saveEncounter(patient, ancEncounterType, deliveryForm, computeWithinRangeEncounterDate(5));
        }

        {
            Patient patient = TestUtils.getPatient(8);
            TestUtils.saveEncounter(patient, ancEncounterType, deliveryForm, computeWithinRangeEncounterDate(10));
            TestUtils.saveEncounter(patient, ancEncounterType, deliveryForm, computeWithinRangeEncounterDate(5));
            TestUtils.saveEncounter(patient, ancEncounterType, deliveryForm, computeWithinRangeEncounterDate(20));
           
        }

        List<Integer> ptIds = Arrays.asList(7, 8, 999);
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("onOrAfter", 4);

        CalculationResultMap resultMap = new AllDeliveriesOnOrAfterMonthsCalculation().evaluate(ptIds, params, Context.getService(PatientCalculationService.class).createCalculationContext());
        Assert.assertTrue((Boolean) resultMap.get(7).getValue());
        Assert.assertFalse((Boolean) resultMap.get(8).getValue());
        Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // has no recorded status
    }

	private Date computeWithinRangeEncounterDate(int months){
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, -months);
				cal.clear(Calendar.HOUR);
				cal.clear(Calendar.MINUTE);
				cal.clear(Calendar.MILLISECOND);
				return cal.getTime();
			}

}