package org.openmrs.module.kenyaemr.calculation.library.tb;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link TBSuspectCalculation}
 */

public class TbSuspectCalculationTest extends BaseModuleContextSensitiveTest{
	
	@Autowired
	private TbMetadata tbMetadata;
	
	@Before
	public void setup() throws Exception{
		
		executeDataSet("dataset/test-concepts.xml");

		tbMetadata.install();
		
	}
	
	@Test
	public void evaluate_shouldDetermineWhetherPatientIsTbSuspectAndNotEnrolledInTBProgram() {
		
		fail("Not yet implemented");
	}

}
