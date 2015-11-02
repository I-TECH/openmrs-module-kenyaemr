package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 06/08/15.
 * Calculates if a patient was screened for tb during their last visit
 */
public class ScreenedForTbInLastVisitCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
        Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
        Concept diseaseDiagnosed = Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED);
        Concept noSignsOrSymptoms = Dictionary.getConcept(Dictionary.NO_SIGNS_OR_SYMPTOMS_OF_DISEASE);

        Form tbScreening = MetadataUtils.existing(Form.class, TbMetadata._Form.TB_SCREENING);
        Form moh257 = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);

        CalculationResultMap allEncountersWithNoVisitsTiedOn = Calculations.allEncounters(MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION), cohort, context);

        for(Integer ptId: cohort) {
            boolean isScreened = false;
            //get the last visit of this patient
            List<Visit> visits = Context.getVisitService().getVisits(null, Arrays.asList(Context.getPatientService().getPatient(ptId)), null, null, null, context.getNow(), null, null, null, false, false);
            //get all the encounters from a auto visit forms
            ListResult listResult = (ListResult) allEncountersWithNoVisitsTiedOn.get(ptId);
            List<Encounter> allEncountersInListResult = CalculationUtils.extractResultValues(listResult);
            List<Encounter> only3Moh257 = new ArrayList<Encounter>();
            //only pull 3 encounters from the list of all
            if(allEncountersInListResult.size() > 2) {
                Collections.reverse(allEncountersInListResult);

                only3Moh257.addAll(allEncountersInListResult.subList(0, 1));
            }
            else if(allEncountersInListResult.size() <= 2) {
                Collections.reverse(allEncountersInListResult);
                only3Moh257.addAll(allEncountersInListResult);
            }
            //put all the encounters together
            Set<Encounter> combinedEncounters = new HashSet<Encounter>();
            if(visits.size() > 0 && visits.get(0).getEncounters() != null && only3Moh257.size() > 0){
                combinedEncounters.addAll(visits.get(0).getEncounters());
                combinedEncounters.addAll(only3Moh257);

            }
            else if(only3Moh257.size() > 0 && visits.size() == 0){
                combinedEncounters.addAll(only3Moh257);
            }

                if(combinedEncounters.size() > 0) {
                    List<Encounter> requiredEncounters = new ArrayList<Encounter>();
                    for(Encounter encounter : combinedEncounters) {
                        if ((encounter.getForm().equals(tbScreening) || encounter.getForm().equals(moh257))
                                && encounter.getEncounterDatetime().after(DateUtil.adjustDate(context.getNow(), -6, DurationUnit.MONTHS))
                                && encounter.getEncounterDatetime().before(context.getNow())) {
                            requiredEncounters.add(encounter);
                        }
                    }

                    if(requiredEncounters.size() > 0){

                        Collections.sort(requiredEncounters, new Comparator<Encounter>() {
                            @Override
                            public int compare(Encounter t0, Encounter t1) {
                                return t1.getEncounterDatetime().compareTo(t0.getEncounterDatetime());
                            }
                        });
                        for(Encounter enc: requiredEncounters) {
                            for(Obs obs : enc.getAllObs()) {
                                if(obs.getConcept().equals(tbDiseaseStatus) && (obs.getValueCoded().equals(diseaseSuspected) || obs.getValueCoded().equals(diseaseDiagnosed) || obs.getValueCoded().equals(noSignsOrSymptoms))) {
                                    isScreened = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            ret.put(ptId, new BooleanResult(isScreened, this));
        }
        return ret;
    }
}
