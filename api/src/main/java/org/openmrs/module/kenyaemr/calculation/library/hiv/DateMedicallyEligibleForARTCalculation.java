package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.model.PatientEligibility;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.Age;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the date when a patient was medically eligible and the reasons
 */
public class DateMedicallyEligibleForARTCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

        CalculationResultMap ages = Calculations.ages(cohort, context);

        CalculationResultMap allWhoStage = Calculations.allObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), cohort, context);
        CalculationResultMap allCd4 = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap allCd4Percent = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_PERCENT), cohort, context);
        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);

        for (Integer ptId : cohort) {
            PatientEligibility eligibilityInfo = null;
            if (inHivProgram.contains(ptId)) {
                int ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
                Date birthDate = Context.getPersonService().getPerson(ptId).getBirthdate();
                List<Obs> whoStages = CalculationUtils.extractResultValues((ListResult) allWhoStage.get(ptId));
                List<Obs> cd4s = CalculationUtils.extractResultValues((ListResult) allCd4.get(ptId));
                List<Obs> cd4Percents = CalculationUtils.extractResultValues((ListResult) allCd4Percent.get(ptId));
                Date arvStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);

                eligibilityInfo = getCriteriaAndDate(ageInMonths, cd4s, cd4Percents, whoStages, birthDate, arvStartDate);
            }
            ret.put(ptId, new SimpleResult(eligibilityInfo, this));
        }
        return ret;
    }

	/**
	 * Checks eligibility based on age, CD4 and WHO stage
	 * @param ageInMonths
	 * @param cd4
	 * @param cd4Percent
	 * @param whoStage
	 * @param birthDate
	 * @param arvStartDate
	 * @return PatientEligibility information
	 */
    protected PatientEligibility getCriteriaAndDate(int ageInMonths, List<Obs> cd4, List<Obs> cd4Percent, List<Obs> whoStage, Date birthDate, Date arvStartDate) {

        if (ageInMonths < 24) {
			return new PatientEligibility("age", birthDate);
        }
        else if (ageInMonths < 60) { // 24-59 months

			if (!whoStage.isEmpty() && (!cd4.isEmpty() || !cd4Percent.isEmpty())) {
				EligibilityDateReason dateReason = dateEligible(cd4, cd4Percent, whoStage, 25, 1000);
				if ((arvStartDate != null && dateReason.getDateEligible() != null && dateReason.getDateEligible().after(arvStartDate))) {
					return new PatientEligibility(null, arvStartDate);
				}
				return new PatientEligibility(dateReason.getReason(), dateReason.getDateEligible());
			}

            if (whoStage != null && (!cd4.isEmpty())) {
                for(Obs obsWhoStage:whoStage) {
                    if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_3_PEDS)) {
						Date d = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? arvStartDate : obsWhoStage.getObsDatetime();
						String r = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? null : "who3Peds";
						return new PatientEligibility(r, d);
                    }

                    if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_4_PEDS)) {

                        Date d = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? arvStartDate : obsWhoStage.getObsDatetime();
						String r = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? null : "who4Peds";
						return new PatientEligibility(r, d);

                    }
                }

            }
            if (cd4Percent != null && (!cd4Percent.isEmpty())) {
                for(Obs obsPercent:cd4Percent) {
                    if(obsPercent.getValueNumeric() < 25) {
						Date d =  (arvStartDate != null && obsPercent.getObsDatetime().after(arvStartDate))? arvStartDate : obsPercent.getObsDatetime() ;
						String r = (arvStartDate != null && obsPercent.getObsDatetime().after(arvStartDate))? null : "cdPercent" ;
						return new PatientEligibility(r, d);

                    }
                }
            }
            if (cd4 != null && (!cd4.isEmpty())) {
                for(Obs obsCd4 : cd4) {
                    if(obsCd4.getValueNumeric() < 1000) {
						Date d = (arvStartDate != null && obsCd4.getObsDatetime().after(arvStartDate))? arvStartDate : obsCd4.getObsDatetime() ;
						String r = (arvStartDate != null && obsCd4.getObsDatetime().after(arvStartDate))? null : "cd4Count" ;
						return new PatientEligibility(r, d);

                    }
                }
            }
        }
        else if (ageInMonths < 155) {
			if (!whoStage.isEmpty() && (!cd4.isEmpty() || !cd4Percent.isEmpty())) {
				EligibilityDateReason dateReason = dateEligible(cd4, cd4Percent, whoStage, 20, 500);
				if ((arvStartDate != null && dateReason.getDateEligible() != null && dateReason.getDateEligible().after(arvStartDate))) {
					return new PatientEligibility(null, arvStartDate);
				}
				return new PatientEligibility(dateReason.getReason(), dateReason.getDateEligible());
			}
            if (whoStage != null && (!whoStage.isEmpty())) {
                for(Obs obsWhoStage:whoStage) {
                    if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_3_PEDS)) {
						Date d = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? arvStartDate : obsWhoStage.getObsDatetime();
						String r = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? null : "who3Peds";
						return new PatientEligibility(r, d);
                    }
                    if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_4_PEDS)) {
                        Date d = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? arvStartDate : obsWhoStage.getObsDatetime();
						String r = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? null : "who4Peds";
						return new PatientEligibility(r, d);
                    }
                }
            }
            if (cd4Percent != null && (!cd4Percent.isEmpty())) {
                for(Obs obsPercent : cd4Percent) {
                    if (obsPercent.getValueNumeric() < 20) {
                        Date d = (arvStartDate != null && obsPercent.getObsDatetime().after(arvStartDate))? arvStartDate : obsPercent.getObsDatetime() ;
						String r = (arvStartDate != null && obsPercent.getObsDatetime().after(arvStartDate))? null : "cdPercent" ;
						return new PatientEligibility(r, d);
                    }
                }
            }
            if (cd4 != null && (!cd4.isEmpty())) {
                for(Obs obsCd4 : cd4) {
                    if (obsCd4.getValueNumeric() < 500) {
						Date d = (arvStartDate != null && obsCd4.getObsDatetime().after(arvStartDate))? arvStartDate : obsCd4.getObsDatetime() ;
						String r = (arvStartDate != null && obsCd4.getObsDatetime().after(arvStartDate))? null : "cd4Count" ;
						return new PatientEligibility(r, d);
                    }
                }
            }
        }
        else {
			if (!whoStage.isEmpty() && (!cd4.isEmpty() || !cd4Percent.isEmpty())) {
				EligibilityDateReason dateReason = dateEligible(cd4, cd4Percent, whoStage, 0, 350);
				if ((arvStartDate != null && dateReason.getDateEligible() != null && dateReason.getDateEligible().after(arvStartDate))) {
					return new PatientEligibility(null, arvStartDate);
				}
				return new PatientEligibility(dateReason.getReason(), dateReason.getDateEligible());
			}
            if (whoStage != null && (!whoStage.isEmpty())) {
                for(Obs obsWhoStage:whoStage) {
                    if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_3_ADULT)) {
						Date d = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? arvStartDate : obsWhoStage.getObsDatetime();
						String r = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? null : "who3Adults";
						return new PatientEligibility(r, d);
                    }
                    if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_4_ADULT)) {
						Date d = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? arvStartDate : obsWhoStage.getObsDatetime();
						String r = (arvStartDate != null && obsWhoStage.getObsDatetime().after(arvStartDate))? null : "who4Adults";
						return new PatientEligibility(r, d);
                    }
                }
            }
            if (cd4 != null && (!cd4.isEmpty())) {
                for(Obs obsCd4:cd4) {
                    if(obsCd4.getValueNumeric() < 350) {
						Date d = (arvStartDate != null && obsCd4.getObsDatetime().after(arvStartDate))? arvStartDate : obsCd4.getObsDatetime() ;
						String r = (arvStartDate != null && obsCd4.getObsDatetime().after(arvStartDate))? null : "cd4Count" ;
						return new PatientEligibility(r, d);
                    }
                }
            }
        }
        return null;
    }

	protected EligibilityDateReason dateEligible(List<Obs> cd4, List<Obs> cd4Percent, List<Obs> whoStage, int cd4PercentThreshold, int cd4CountThreshold) {
		String reason = null;
		Date dateEligible = null;
		Date cd4Date = null;
		if (!(cd4.isEmpty() || cd4Percent.isEmpty())) {
			cd4Date = compareCD4CountAndPercent(cd4, cd4Percent, cd4PercentThreshold, cd4CountThreshold);
		}

		if (whoStage.isEmpty()){
			if (cd4Date != null) {
				return new EligibilityDateReason("cd4", cd4Date);
			}
		} else {
			Date whoDate = null;
			for (Obs obsWhoStage : whoStage) {
				if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_3_ADULT)) {
					whoDate = obsWhoStage.getObsDatetime();
					break;
				}
				if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_4_ADULT)) {
					whoDate = obsWhoStage.getObsDatetime();
					break;
				}
				if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_3_PEDS)) {
					whoDate = obsWhoStage.getObsDatetime();
					break;
				}
				if (obsWhoStage.getValueCoded().equals(Dictionary.WHO_STAGE_4_PEDS)) {
					whoDate = obsWhoStage.getObsDatetime();
					break;
				}
			}

			if (whoDate != null && cd4Date == null) {
				dateEligible = whoDate;
				reason = "whoStage";
			}

			if (cd4Date != null && whoDate == null) {
				dateEligible = cd4Date;
				reason = "cd4";
			}

			if (whoDate != null && cd4Date != null && whoDate.before(cd4Date)) {
				dateEligible = whoDate;
				reason = "whoStage";
			} else if (cd4Date != null && whoDate != null && cd4Date.before(whoDate)){
				dateEligible = cd4Date;
				reason = "cd4";
			}
		}
		return new EligibilityDateReason(reason,dateEligible);
	}

	private Date compareCD4CountAndPercent (List<Obs> cd4, List<Obs> cd4Percent, int cd4PercentThreshold, int cd4CountThreshold) {
		Date eligibilityDate = null;
		Date cd4PercentDate = null;
		Date cd4CountDate = null;

		if (!cd4.isEmpty() && cd4Percent.isEmpty()) {
			for(Obs obsCount : cd4) {
				if (obsCount.getValueNumeric() < cd4CountThreshold) {
					eligibilityDate = obsCount.getObsDatetime() ;
					break;
				}
			}
		}

		if (cd4.isEmpty() && !cd4Percent.isEmpty()) {
			for(Obs obsPercent : cd4Percent) {
				if (obsPercent.getValueNumeric() < cd4PercentThreshold) {
					eligibilityDate = obsPercent.getObsDatetime() ;
					break;
				}
			}
		}

		if (!cd4.isEmpty() && !cd4Percent.isEmpty()) {

			for(Obs obsPercent : cd4Percent) {
				if (obsPercent.getValueNumeric() < cd4PercentThreshold) {
					 cd4PercentDate = obsPercent.getObsDatetime() ;
					break;
				}
			}

			for(Obs obsCount : cd4) {
				if (obsCount.getValueNumeric() < cd4CountThreshold) {
					cd4CountDate = obsCount.getObsDatetime() ;
					break;
				}
			}

			if (cd4CountDate != null && cd4PercentDate == null) {
				eligibilityDate = cd4CountDate;
			} else if (cd4PercentDate != null && cd4CountDate == null) {
				eligibilityDate = cd4PercentDate;
			} else if (cd4CountDate != null && cd4PercentDate != null){
				eligibilityDate = cd4PercentDate.before(cd4CountDate) ? cd4PercentDate : cd4CountDate;
			}


		}
		return eligibilityDate;
	}

	class EligibilityDateReason {
		private String reason;
		private Date dateEligible;

		EligibilityDateReason(String reason, Date dateEligible) {
			this.reason = reason;
			this.dateEligible = dateEligible;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public Date getDateEligible() {
			return dateEligible;
		}

		public void setDateEligible(Date dateEligible) {
			this.dateEligible = dateEligible;
		}
	}


}