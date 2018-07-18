package org.openmrs.module.kenyaemr.reporting.query.patientContact;

import org.openmrs.Visit;
import org.openmrs.module.reporting.query.BaseIdSet;

import java.util.List;
import java.util.Set;

/**
 * A Set of Visit Ids
 */
public class PatientContactIdSet extends BaseIdSet<Visit> {

    public PatientContactIdSet() {
        super();
    }

    public PatientContactIdSet(Set<Integer> memberIds) {
        setMemberIds(memberIds);
    }

    public PatientContactIdSet(List<Integer> memberIds) {
        add(memberIds.toArray(new Integer[0]));
    }

    public PatientContactIdSet(Integer... memberIds) {
        add(memberIds);
    }

}
