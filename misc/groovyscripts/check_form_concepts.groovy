/***************************************************
 *
 * Checks HTML forms for duplicate concepts
 *
 **************************************************/

import org.openmrs.api.context.Context

// 6042 is free-text diagnosis
def ignoreWarningsFor = [ "6042" ]

def loadInstance = { className ->
    return Context.loadClass(className).newInstance()
}

def HtmlFormEntryUtil = loadInstance("org.openmrs.module.htmlformentry.HtmlFormEntryUtil")
def FormEntryContext = loadInstance("org.openmrs.module.htmlformentry.FormEntryContext")

def FormEntrySessionClass = Context.loadClass("org.openmrs.module.htmlformentry.FormEntrySession")
def HtmlFormClass = Context.loadClass("org.openmrs.module.htmlformentry.HtmlForm")

def service = Context.getService(Context.loadClass("org.openmrs.module.htmlformentry.HtmlFormEntryService"))

def getSchema = { hf ->
    def constructor = FormEntrySessionClass.constructors.find {
        def helper = it.parameterTypes.collect { it.name }.join(",")
        return helper == "org.openmrs.Patient,org.openmrs.module.htmlformentry.HtmlForm,javax.servlet.http.HttpSession"
    }
    def fes = constructor.newInstance(HtmlFormEntryUtil.getFakePerson(), hf, null);
    return fes.getContext().getSchema();
}

def obsFieldSignature = { obsField ->
    def temp = "${ obsField.question.id }"
    if (obsField.answers) {
        temp += "(" + obsField.answers.collect { it.concept.id } .join(", ") + ")"
    }
    return temp;
}

def checkForUnsafeObsFields = { prefix, fields ->
    println("(debug) looking at ${ prefix }")
    def questionsUsed = new TreeSet();
    fields.each {
        if (it.class.simpleName == "ObsField") {
            def obsFieldSig = obsFieldSignature(it)
            if (questionsUsed.contains(obsFieldSig) && !ignoreWarningsFor.contains(obsFieldSig)) {
                println("${ prefix } -> Warning about ${ obsFieldSig }")
            }
            questionsUsed.add(obsFieldSig)
        }
    }
}

def checkField
checkField = { prefix, field ->
    def test = field.class.simpleName
    if ("HtmlFormSection" == test) {
        def name = "${prefix} -> (Section) ${field.name} "
        checkForUnsafeObsFields(name, field.fields)
        field.fields.each {
            checkField(name, it)
        }
    } else if ("ObsGroup" == test) {
        def name = "${prefix} -> (ObsGroup) ${field.concept.name.name} "
        checkForUnsafeObsFields(name, field.children)
        field.children.each {
            checkField(name, it)
        }
    }
}

def checkFields = { prefix, fields ->
    fields.each {
        checkField(prefix, it)
    }
}

service.allHtmlForms.each { hf ->
    println("=== ${ hf.name } ===")
    def schema = getSchema(hf)
    println("Schema has ${ schema.allFields.size() } fields")
    checkFields("  ", schema.allFields)
    //throw new RuntimeException("For quick development, only look at one form")
}

"Done."