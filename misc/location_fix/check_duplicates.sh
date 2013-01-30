#!/bin/bash

SQL="
	SELECT l1.location_id, l1.name, la1.value_reference as 'MFL code'
	FROM location_attribute la1
	INNER JOIN location_attribute la2 ON la1.value_reference = la2.value_reference
	INNER JOIN location l1 ON la1.location_id = l1.location_id
	WHERE la1.location_attribute_id != la2.location_attribute_id
	ORDER BY la1.value_reference ASC;
"

echo "$SQL" | mysql -uroot -p -D openmrs