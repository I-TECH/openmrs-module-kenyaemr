/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.api.impl;

import au.com.bytecode.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;

/**
 * Created by gitahi on 28/07/15.
 */
@Component
public class CsvMaker {

    public byte[] createCsv(List<Object> data, List<Object> headerRow) {
        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter);
        try {
            if (headerRow != null) {
                data.add(0, headerRow.toArray());
            }
            for (Object object : data) {
                Object[] values = (Object[]) object;
                String[] row = new String[values.length];
                int i = 0;
                for (Object value : values) {
                    row[i] = value != null ? value.toString() : null;
                    i++;
                }
                writer.writeNext(row);
            }
            return stringWriter.toString().getBytes();
        } catch (Exception ex) {
            throw new RuntimeException("Could not download data dictionary.");
        }
    }
}
