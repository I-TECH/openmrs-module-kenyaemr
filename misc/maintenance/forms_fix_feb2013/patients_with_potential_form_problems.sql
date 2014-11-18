#
# Script to list all patients with submissions of the forms listed below
#

SET @HIV_ENROLLMENT = 'e4b506c1-7379-42b6-a374-284469cba8da';
SET @HIV_DISCONTINUE = 'e3237ede-fa70-451f-9e6c-0908bc39f8b9';
SET @TB_ENROLLMENT = '89994550-9939-40f3-afa6-173bce445c79';
SET @TB_COMPLETE = '4b296dd0-f6be-4007-9eb8-d0fd4e94fb3a';
SET @OTHER_MEDS = 'd4ff8ad1-19f8-484f-9395-04c755de9a4';
SET @LAB_RESULTS = '7e603909-9ed5-4d0c-a688-26ecb05d8b6e';

# Identifier type UUIDs
SET @PCN_ID_TYPE = 'b4d66522-11fc-45c7-83e3-39a1af21ae0d';
SET @UPN_ID_TYPE = '05ee9cf4-7242-4a17-b4d4-00f707265c8a';

# Convert to database ids
SELECT patient_identifier_type_id INTO @PCN_ID_ID FROM patient_identifier_type WHERE uuid = @PCN_ID_TYPE;
SELECT patient_identifier_type_id INTO @UPN_ID_ID FROM patient_identifier_type WHERE uuid = @UPN_ID_TYPE;

SELECT
	p.patient_id AS `Database ID`,
	pi1.identifier AS `Clinic No`,
	pi2.identifier AS `Unique No`,
	CONCAT_WS(' ', n.given_name, n.middle_name, n.family_name) AS `Name`,
	COUNT(e.encounter_id) AS `Num forms`,
	GROUP_CONCAT(e.encounter_id ORDER BY e.encounter_datetime ASC SEPARATOR ', ') AS `Encounter IDs`,
	GROUP_CONCAT(CONCAT(f.name, ' (', e.encounter_datetime, ')') ORDER BY e.encounter_datetime ASC SEPARATOR ', ') AS `Forms`
FROM
		patient p
		INNER JOIN
		person_name n ON n.person_id = p.patient_id
		LEFT OUTER JOIN
		patient_identifier pi1 ON pi1.patient_id = p.patient_id AND pi1.identifier_type = @PCN_ID_ID
		LEFT OUTER JOIN
		patient_identifier pi2 ON pi2.patient_id = p.patient_id AND pi2.identifier_type = @UPN_ID_ID
		INNER JOIN
		encounter e ON e.patient_id = p.patient_id AND e.voided = 0
		INNER JOIN
		form f ON f.form_id = e.form_id AND f.uuid IN (@HIV_ENROLL, @HIV_DISCONTINUE, @TB_ENROLLMENT, @TB_COMPLETE, @OTHER_MEDS, @LAB_RESULTS)
GROUP BY
	p.patient_id
ORDER BY
	p.patient_id;