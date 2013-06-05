/***************************************************
 *
 * Syncs OpenMRS locations with the Kenya MFL
 *
 **************************************************/

import org.openmrs.api.context.Context

/********* Configuration *********/

def UPDATE_URL = new URL("http://ehealth.or.ke/facilities/download-latest.aspx")
def MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID = "8a845a89-6aa5-4111-81d3-0af31c45c002";

/********* Globals *********/

svc = Context.getLocationService()
mfcAttrType = svc.getLocationAttributeTypeByUuid(MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID)
mfcToIdMap = [:]
numCreated = 0
numUpdated = 0

/********* Methods *********/

/**
 * Creates an instance of the named class, and passes args to the contructor
 * of that class which matches argClassNames
 */
def _new(className, argClassNames, Object... args) {
	def argClasses = (Class[])argClassNames.collect { Context.loadClass(it) }
	Context.loadClass(className).getDeclaredConstructor(argClasses).newInstance((Object[])args)
}

/**
 * Gets the value of an Excel spreadsheet cell
 */
def cellValue(cell) { cell.getCellType() == 0 ? cell.getNumericCellValue() : cell.getStringCellValue() }

def importXls(input) {
	def poifs = _new ("org.apache.poi.poifs.filesystem.POIFSFileSystem", [ "java.io.InputStream" ], input)
	def wbook = _new ("org.apache.poi.hssf.usermodel.HSSFWorkbook", [ "org.apache.poi.poifs.filesystem.POIFSFileSystem" ], poifs)
	def sheet = wbook.getSheetAt(0)

	for (def r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); ++r) {
		def row = sheet.getRow(r)
		def code = String.valueOf((int)cellValue(row.getCell(0)))
		def name = cellValue(row.getCell(1))
		def province = cellValue(row.getCell(2))
		def type = cellValue(row.getCell(6))

		if (code && name) {
			importLocation(code, name, province, type)
		}
	}
}

/**
 * Imports a location
 */
def importLocation(code, name, province, type) {
	def location = null

	// Look for existing location with this code
	def existingLocationId = mfcToIdMap[code]
	if (existingLocationId) {
		location = svc.getLocation(existingLocationId)
	}

	// Create new location if it doesn't exist
	if (!location) {
		location = new org.openmrs.Location()

		def mfcAttr = new org.openmrs.LocationAttribute()
		mfcAttr.setAttributeType(mfcAttrType)
		mfcAttr.setValue(code)
		mfcAttr.setOwner(location)

		location.addAttribute(mfcAttr)

		numCreated++
	}
	else {
		numUpdated++
	}

	location.setName(name)
	location.setDescription(type)
	location.setCountry("Kenya")
	location.setStateProvince(province)

	location = svc.saveLocation(location)
	mfcToIdMap.put(code, location.getLocationId())

	Context.flushSession()
	Context.clearSession()
}

// Initialize MFC > ID map
println "Loading MFC to ID mappings..."
for (def loc : svc.getAllLocations()) {
	for (def attr : loc.getActiveAttributes(mfcAttrType)) {
		if (attr.getValue()) {
			mfcToIdMap.put(attr.getValue(), loc.getLocationId())
		}
	}
}

// Download zip to temporary file
println "Downloading MFL. This may take a few minutes..."
def tmpZip = File.createTempFile("mfl", ".zip");
def out = new BufferedOutputStream(new FileOutputStream(tmpZip));
out << UPDATE_URL.openStream()
out.close()

// Import any contained xls file
println "Extracting xls from zip file..."
def zipFile = new java.util.zip.ZipFile(tmpZip)
zipFile.entries().each { entry ->
	if (entry.name.endsWith(".xls")) {
		importXls(zipFile.getInputStream(entry))
	}
}

// Delete temp zip file
tmpZip.delete()

// Show results
println "Created " + numCreated + " new locations and updated " + numUpdated + " existing locations"