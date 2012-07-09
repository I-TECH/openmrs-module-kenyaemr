package org.openmrs.module.kenyaemr.regimen;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;

public class RegimenHistoryTest {
	
	/**
	 * @see RegimenHistory#RegimenHistory(Set,List)
	 * @verifies create regimen history based on drug orders
	 */
	@Test
	public void RegimenHistory_shouldCreateRegimenHistoryBasedOnDrugOrders() throws Exception {
		/* Test case like this:
		 * 1: <--->
		 * 2: | <----->
		 * 3: | |  <-----...
		 * 4: | |  <-----... (this drug is not relevant)
		 *    | |  |  |
		 *   t0 t1 t2 t3
		 */
		Concept genericOne = new Concept();
		genericOne.setPreferredName(new ConceptName("One", Locale.ENGLISH));
		
		Concept genericTwo = new Concept();
		genericTwo.setPreferredName(new ConceptName("Two", Locale.ENGLISH));
		
		Concept genericThree = new Concept();
		genericThree.setPreferredName(new ConceptName("Three", Locale.ENGLISH));
		
		Concept genericFour = new Concept();
		genericFour.setPreferredName(new ConceptName("Four", Locale.ENGLISH));
		
		Concept reason = new Concept();
		reason.setConceptId(5);
		reason.setUuid("Reason");
		reason.setPreferredName(new ConceptName("Reason", Locale.ENGLISH));
		
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		Date t0 = ymd.parse("2006-01-01");
		Date t1 = ymd.parse("2006-02-01");
		Date t2 = ymd.parse("2006-03-01");
		Date t3 = ymd.parse("2006-04-01");
		
		DrugOrder one = new DrugOrder();
		one.setConcept(genericOne);
		one.setStartDate(t0);
		one.setDiscontinued(true);
		one.setDiscontinuedDate(t2);
		one.setDiscontinuedReasonNonCoded("Because I felt like it");
		
		DrugOrder two = new DrugOrder();
		two.setConcept(genericTwo);
		two.setStartDate(t1);
		two.setDiscontinued(true);
		two.setDiscontinuedDate(t3);
		two.setDiscontinuedReason(reason);
		
		DrugOrder three = new DrugOrder();
		three.setConcept(genericThree);
		three.setStartDate(t2);
		
		DrugOrder four = new DrugOrder();
		four.setConcept(genericFour);
		four.setStartDate(t2);
		
		Set<Concept> relevantDrugs = new HashSet<Concept>(Arrays.asList(genericOne, genericTwo, genericThree));
		List<DrugOrder> allDrugOrders = Arrays.asList(one, two, three, four);
		
		RegimenHistory regimenHistory = new RegimenHistory(relevantDrugs, allDrugOrders);
		for (RegimenChange c : regimenHistory.getChanges()) {
			System.out.println("On " + ymd.format(c.getDate()) + " changed from " + c.getStopped() + " to " + c.getStarted() + " for reasons: " + c.getChangeReasons() + " and " + c.getChangeReasonsNonCoded()); 
		}
	}
}