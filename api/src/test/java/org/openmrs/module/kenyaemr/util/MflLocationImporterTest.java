package org.openmrs.module.kenyaemr.util;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class MflLocationImporterTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}
	
	/**
	 * @see MflLocationImporter#importCsv(String)
	 * @verifies import csv content
	 */
	@Test
	public void importCsv_shouldImportCsvContent() throws Exception {
		String csv = "Facility Code,Facility Name,County,Type\n";
		csv += "12866,Afya Medical Health Centre,Nairobi,Health Centre\n";
		csv += "17514,Mary Mission,Nairobi,Health Centre\n";
		csv += "13080,Mbagathi District Hospital,Nairobi,District Hospital\n";
		csv += "13103,Muteithania Medical Clinic ,Nairobi,Health Centre\n";
		csv += "13089,Mercy Mission Health Centre,Nairobi,Health Centre\n";
		csv += "13091,Mid Hill Medical Clinic ,Nairobi,Health Centre\n";
		csv += "12995,Kabiro Medical Clinic,Nairobi,Health Centre\n";
		csv += "13159,Ray Of Hope Health Centre,Nairobi,Health Centre\n";
		csv += "13200,St Catherine's Health Centre,Nairobi,Health Centre\n";
		csv += "13212,St Jude's Health Centre,Nairobi,Health Centre\n";
		csv += "13213,St Lukes (Kona) Health Centre,Nairobi,Health Centre\n";
		csv += "13227,St Teresa's Health Centre ,Nairobi,Health Centre\n";
		csv += "13238,Trinity Medical Care Health Centre,Nairobi,Health Centre\n";
		csv += "13249,Waithaka Health Centre,Nairobi,Health Centre\n";
		csv += "13256,Wema Nursing Home   ,Nairobi,Health Centre";
		
		Set<Integer> expectedCodes = new HashSet<Integer>(Arrays.asList(12866, 17514, 13080, 13103, 13089, 13091, 12995, 13159, 13200, 13212, 13213, 13227, 13238, 13249, 13256));

		LocationAttributeType facilityCodeAttrType = Context.getLocationService().getLocationAttributeTypeByUuid(MetadataConstants.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID);
		
		int numSaved = new MflLocationImporter().importCsv(csv);
		
		Assert.assertEquals(15, numSaved);
		
		for (Location l : Context.getLocationService().getAllLocations()) {
			for (LocationAttribute attr : l.getActiveAttributes(facilityCodeAttrType)) {
				expectedCodes.remove(Integer.valueOf((String) attr.getValue()));
			}
		}
		Assert.assertEquals(0, expectedCodes.size());
		
		// verify that trim() was performed on inputs
		Assert.assertEquals("Wema Nursing Home", Context.getLocationService().getLocations("Wema Nursing Home").get(0).getName());
	}
}