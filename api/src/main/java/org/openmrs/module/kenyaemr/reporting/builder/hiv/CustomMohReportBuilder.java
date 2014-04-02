package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.kenyacore.report.CalculationReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
@Builds("kenyaemr.hiv.report.cohortReport")
public class CustomMohReportBuilder extends CalculationReportBuilder {

	@Override
	protected void addColumns(CalculationReportDescriptor report, PatientDataSetDefinition dsd) {

		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Concept arvStartDate = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_TREATMENT_START_DATE);
		Concept discontinueReason = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		Concept transerInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);
		Concept transferIn = Dictionary.getConcept(Dictionary.TRANSFER_IN);

		addStandardColumns(report, dsd);

		dsd.addColumn("CD4 Count", new ObsForPersonDataDefinition("CD4 Count", TimeQualifier.LAST, cd4, new Date(), null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Double.class;
			}

			@Override
			public Object convert(Object input) {
				Double out = null;
				if (input != null) {
					 out = ((Obs) input).getValueNumeric();
				}
				return out;
			}

		});

		dsd.addColumn("CD4 Count Date", new ObsForPersonDataDefinition("CD4 Count Date", TimeQualifier.LAST, cd4, new Date(), null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Date.class;
			}

			@Override
			public Object convert(Object input) {
				Date d = null;
				if (input != null) {
					d = ((Obs) input).getObsDatetime();
				}
				return d;
			}
		});

		dsd.addColumn("Arv Start Date", new ObsForPersonDataDefinition("Arv Start Date", TimeQualifier.FIRST, arvStartDate, new Date(), null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Date.class;
			}

			@Override
			public Object convert(Object input) {
				Date d = null;
				if (input != null) {
					d = ((Obs) input).getValueDate();
				}
				return d;
			}
		});

		dsd.addColumn("Transfer Out", new ObsForPersonDataDefinition("Transfer Out", TimeQualifier.ANY, discontinueReason, new Date(), null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Integer.class;
			}

			@Override
			public Object convert(Object input) {
				String s = null;
				Object o;
				if(input !=null){
					List<Object> objectList = new ArrayList<Object>((Collection<?>) input);
						for(Object oo:objectList) {
							o = ((Obs) oo).getValueCoded().getConceptId();

							if(o.equals(Dictionary.getConcept(Dictionary.TRANSFERRED_OUT))) {
								s="YES";
							}
							else {
								s="NO";
							}
						}

				}
				return s;
			}
		});

		dsd.addColumn("Transfer Out Date", new ObsForPersonDataDefinition("Transfer Out Date", TimeQualifier.LAST, discontinueReason, new Date(), null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Date.class;
			}

			@Override
			public Object convert(Object input) {
				Date d = null;
				Object o;
				if (input != null) {
					//List<Object> objectList = new ArrayList<Object>((Collection<?>) input);
					//for(Object oo:objectList) {
						o = ((Obs) input).getValueCoded().getConceptId();
						if(o.equals(Dictionary.getConcept(Dictionary.TRANSFERRED_OUT))) {
							d = ((Obs) input).getObsDatetime();
						}
					//}
				}
				return d;
			}
		});

		dsd.addColumn("Transfer In", new ObsForPersonDataDefinition("Transfer In", TimeQualifier.LAST, transferIn, new Date(), null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Integer.class;
			}

			@Override
			public Object convert(Object input) {
				Object o;
				String ans = null;
				if (input !=null) {
					o= ((Obs)input).getValueCoded().getConceptId();
					if (o.equals(Dictionary.getConcept(Dictionary.YES).getConceptId())) {
						ans = "YES";
					}
					else{
						ans = "NO";
					}
				}
			return ans;
			}
		});

		dsd.addColumn("Transfer In Date", new ObsForPersonDataDefinition("Transfer In Date", TimeQualifier.LAST, transerInDate, new Date(), null), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Obs.class;
			}

			@Override
			public Class<?> getDataType() {
				return Date.class;
			}

			@Override
			public Object convert(Object input) {
				Date d = null;
				if (input != null) {
					d = ((Obs) input).getValueDate();
				}
				return d;
			}
		});

	}
}
