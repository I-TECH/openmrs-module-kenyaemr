package org.openmrs.module.kenyaemr.reporting.data.definition;

/**
 * Representation of custom person address object
 */
public class AddressObject {
	String country;
	String countyDistrict;

	public AddressObject(String country, String countyDistrict) {
		this.country = country;
		this.countyDistrict = countyDistrict;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountyDistrict() {
		return countyDistrict;
	}

	public void setCountyDistrict(String countyDistrict) {
		this.countyDistrict = countyDistrict;
	}

}
