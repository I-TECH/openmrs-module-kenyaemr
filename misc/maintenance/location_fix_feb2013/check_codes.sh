#!/bin/bash

DATAFILE='mfl_map.dat'

SQL="
	DROP TABLE IF EXISTS mfl_map;
	CREATE TEMPORARY TABLE mfl_map (mflcode VARCHAR(5) NOT NULL, name VARCHAR(255) NOT NULL, uuid CHAR(38) NOT NULL);

	LOAD DATA LOCAL INFILE '$DATAFILE' INTO TABLE mfl_map IGNORE 1 LINES;

	SELECT l.location_id, l.name, la.value_reference as 'MFL Code'
	FROM location l
	LEFT OUTER JOIN location_attribute la ON la.location_id = l.location_id
    LEFT OUTER JOIN location_attribute_type lat ON lat.location_attribute_type_id = la.attribute_type_id AND lat.uuid = '8a845a89-6aa5-4111-81d3-0af31c45c002'
    LEFT OUTER JOIN mfl_map mf ON mf.mflcode = la.value_reference
	WHERE mf.mflcode IS NULL;

	DROP TABLE mfl_map;
"

echo "$SQL" | mysql -uroot --local_infile=1 -p -D openmrs