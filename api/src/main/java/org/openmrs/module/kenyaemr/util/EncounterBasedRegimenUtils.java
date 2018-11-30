package org.openmrs.module.kenyaemr.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.ui.framework.SimpleObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class EncounterBasedRegimenUtils {

    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

    public static List<SimpleObject> getRegimenHistoryFromObservations (Patient patient, String category) {

        FormService formService = Context.getFormService();
        EncounterService encounterService = Context.getEncounterService();
        String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        List<SimpleObject> history = new ArrayList<SimpleObject>();
        String categoryConceptUuid = category.equals("ARV")? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT;

        EncounterType et = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.CONSULTATION);
        Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);

        List<Encounter> regimenChangeHistory = EmrUtils.AllEncounters(patient, et, form);
        if (regimenChangeHistory != null && regimenChangeHistory.size() > 0) {
            for (Encounter e : regimenChangeHistory) {
                Set<Obs> obs = e.getObs();
                if (programEncounterMatching(obs, categoryConceptUuid)) {
                    SimpleObject object = buildRegimenChangeObject(obs, e);
                    if (object != null)
                        history.add(object);
                }
            }
            return history;
        }
        return new ArrayList<SimpleObject>();
    }

    public static Encounter getLastEncounterForCategory (Patient patient, String category) {

        FormService formService = Context.getFormService();
        EncounterService encounterService = Context.getEncounterService();
        String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        List<SimpleObject> history = new ArrayList<SimpleObject>();
        String categoryConceptUuid = category.equals("ARV")? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT;

        EncounterType et = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.CONSULTATION);
        Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);

        List<Encounter> encs = EmrUtils.AllEncounters(patient, et, form);
        NavigableMap<Date, Encounter> programEncs = new TreeMap<Date, Encounter>();
        for (Encounter e : encs) {
            if (e != null) {
                Set<Obs> obs = e.getObs();
                if (programEncounterMatching(obs, categoryConceptUuid)) {
                    programEncs.put(e.getEncounterDatetime(), e);
                }
            }
        }
        if (!programEncs.isEmpty()) {
            return programEncs.lastEntry().getValue();
        }
        return null;
    }

    public static Encounter getFirstEncounterForCategory (Patient patient, String category) {

        FormService formService = Context.getFormService();
        EncounterService encounterService = Context.getEncounterService();
        String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        List<SimpleObject> history = new ArrayList<SimpleObject>();
        String categoryConceptUuid = category.equals("ARV")? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT;

        EncounterType et = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.CONSULTATION);
        Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);

        List<Encounter> encs = EmrUtils.AllEncounters(patient, et, form);
        NavigableMap<Date, Encounter> programEncs = new TreeMap<Date, Encounter>();
        for (Encounter e : encs) {
            if (e != null) {
                Set<Obs> obs = e.getObs();
                if (programEncounterMatching(obs, categoryConceptUuid)) {
                    programEncs.put(e.getEncounterDatetime(), e);
                }
            }
        }
        if (!programEncs.isEmpty()) {
            return programEncs.firstEntry().getValue();
        }
        return null;
    }

    public static boolean programEncounterMatching(Set<Obs> obs, String conceptUuidToMatch) {
        for (Obs o : obs) {
            if (o.getConcept().getUuid().equals(conceptUuidToMatch)) {
                return true;
            }
        }
        return false;
    }

    public static SimpleObject buildRegimenChangeObject(Set<Obs> obsList, Encounter e) {

        String CURRENT_DRUGS = "1193AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String REASON_REGIMEN_STOPPED_CODED = "1252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String REASON_REGIMEN_STOPPED_NON_CODED = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String DATE_REGIMEN_STOPPED = "1191AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";



        String regimen = null;
        String regimenShort = null;
        String regimenUuid = null;
        String endDate = null;
        String startDate = e != null? DATE_FORMAT.format(e.getEncounterDatetime()) : "";
        Set<String> changeReason = new HashSet<String>();


        for(Obs obs:obsList) {

            if (obs.getConcept().getUuid().equals(CURRENT_DRUGS) ) {
                regimen = obs.getValueCoded() != null ? obs.getValueCoded().getFullySpecifiedName(CoreConstants.LOCALE).getName() : "";
                try {
                    regimenShort = getRegimenNameFromRegimensXMLString(obs.getValueCoded().getUuid(), getRegimenConceptJson());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                regimenUuid = obs.getValueCoded() != null ? obs.getValueCoded().getUuid() : "";
            } else if (obs.getConcept().getUuid().equals(REASON_REGIMEN_STOPPED_CODED)) {
                String reason = obs.getValueCoded() != null ?  obs.getValueCoded().getName().getName() : "";
                if (reason != null)
                    changeReason.add(reason);
            } else if (obs.getConcept().getUuid().equals(REASON_REGIMEN_STOPPED_NON_CODED)) {
                String reason = obs.getValueText();
                if (reason != null)
                    changeReason.add(reason);
            } else if (obs.getConcept().getUuid().equals(DATE_REGIMEN_STOPPED)) {
                endDate = DATE_FORMAT.format(obs.getValueDatetime());
            }
        }
        if(regimen != null) {
            return SimpleObject.create(
                    "startDate", startDate,
                    "endDate", endDate != null? endDate : "",
                    "regimenShortDisplay", regimenShort != null ? regimenShort : regimen,
                    "regimenLongDisplay", regimen,
                    "changeReasons", changeReason,
                    "regimenUuid", regimenUuid,
                    "current",endDate != null ? false : true

            );
        }
        return null;
    }

    public static String getRegimenNameFromRegimensXMLString(String conceptRef, String regimenJson) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode conf = (ArrayNode) mapper.readTree(regimenJson);

        for (Iterator<JsonNode> it = conf.iterator(); it.hasNext(); ) {
            ObjectNode node = (ObjectNode) it.next();
            if (node.get("conceptRef").asText().equals(conceptRef)) {
                return node.get("name").asText();
            }
        }

        return null;
    }
    public static String getRegimenConceptJson() {
        String json = "[\n" +
                "  {\n" +
                "    \"name\": \"TDF/3TC/NVP\",\n" +
                "    \"conceptRef\": \"162565AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"TDF/3TC/EFV\",\n" +
                "    \"conceptRef\": \"164505AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"AZT/3TC/NVP\",\n" +
                "    \"conceptRef\": \"1652AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"AZT/3TC/EFV\",\n" +
                "    \"conceptRef\": \"160124AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"D4T/3TC/NVP\",\n" +
                "    \"conceptRef\": \"792AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"D4T/3TC/EFV\",\n" +
                "    \"conceptRef\": \"160104AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"TDF/3TC/AZT\",\n" +
                "    \"conceptRef\": \"98e38a9c-435d-4a94-9b66-5ca524159d0e\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"AZT/3TC/DTG\",\n" +
                "    \"conceptRef\": \"6dec7d7d-0fda-4e8d-8295-cb6ef426878d\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"TDF/3TC/DTG\",\n" +
                "    \"conceptRef\": \"9fb85385-b4fb-468c-b7c1-22f75834b4b0\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ABC/3TC/DTG\",\n" +
                "    \"conceptRef\": \"4dc0119b-b2a6-4565-8d90-174b97ba31db\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"AZT/3TC/LPV/r\",\n" +
                "    \"conceptRef\": \"162561AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"AZT/3TC/ATV/r\",\n" +
                "    \"conceptRef\": \"164511AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"TDF/3TC/LPV/r\",\n" +
                "    \"conceptRef\": \"162201AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"TDF/3TC/ATV/r\",\n" +
                "    \"conceptRef\": \"164512AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"D4T/3TC/LPV/r\",\n" +
                "    \"conceptRef\": \"162560AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"AZT/TDF/3TC/LPV/r\",\n" +
                "    \"conceptRef\": \"c421d8e7-4f43-43b4-8d2f-c7d4cfb976a4\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ETR/RAL/DRV/RTV\",\n" +
                "    \"conceptRef\": \"337b6cfd-9fa7-47dc-82b4-d479c39ef355\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ETR/TDF/3TC/LPV/r\",\n" +
                "    \"conceptRef\": \"7a6c51c4-2b68-4d5a-b5a2-7ba420dde203\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ABC/3TC/LPV/r\",\n" +
                "    \"conceptRef\": \"162200AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ABC/3TC/NVP\",\n" +
                "    \"conceptRef\": \"162199AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ABC/3TC/EFV\",\n" +
                "    \"conceptRef\": \"162563AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"AZT/3TC/ABC\",\n" +
                "    \"conceptRef\": \"817AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"D4T/3TC/ABC\",\n" +
                "    \"conceptRef\": \"b9fea00f-e462-4ea5-8d40-cc10e4be697e\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"TDF/ABC/LPV/r\",\n" +
                "    \"conceptRef\": \"162562AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ABC/DDI/LPV/r\",\n" +
                "    \"conceptRef\": \"162559AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ABC/TDF/3TC/LPV/r\",\n" +
                "    \"conceptRef\": \"077966a6-4fbd-40ce-9807-2d5c2e8eb685\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"RHZE\",\n" +
                "    \"conceptRef\": \"1675AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"RHZ\",\n" +
                "    \"conceptRef\": \"768AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SRHZE\",\n" +
                "    \"conceptRef\": \"1674AAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"RfbHZE\",\n" +
                "    \"conceptRef\": \"07c72be8-c575-4e26-af09-9a98624bce67\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"RfbHZ\",\n" +
                "    \"conceptRef\": \"9ba203ec-516f-4493-9b2c-4ded6cc318bc\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"SRfbHZE\",\n" +
                "    \"conceptRef\": \"fce8ba26-8524-43d1-b0e1-53d8a3c06c00\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"S (1 gm vial)\",\n" +
                "    \"conceptRef\": \"84360AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"E\",\n" +
                "    \"conceptRef\": \"75948AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"RH\",\n" +
                "    \"conceptRef\": \"1194AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"RHE\",\n" +
                "    \"conceptRef\": \"159851AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"EH\",\n" +
                "    \"conceptRef\": \"1108AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "  }\n" +
                "]";
        return json;
    }
}
