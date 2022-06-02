/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.chore;

import org.openmrs.Concept;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.OrderType;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.Date;

/**
 * Add missing orderset and the members.
 * This affects only kenyaemr 18.x
 * Date: 31/05/2022
 */
@Component("kenyaemr.chore.updateMissingOrderSet")
public class AddMissingOrderSetAndMembers extends AbstractChore {
    private String TDF_3TC_DTG_ETV_DRV_R = "25b5b9fc-d3f3-11eb-b8bc-0242ac130003";
    private String ABC_3TC_DTG_DRV_R = "53c2ca92-d3f3-11eb-b8bc-0242ac130003";
    private String TDF_3TC_DTG_ATV_R = "bfa6fe00-d3f2-11eb-b8bc-0242ac130003";
    private String B_F_TAF = "cf29bf7e-5f36-491b-b619-8561ddd53ac1";


    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {

        OrderSetService orderSetService = Context.getOrderSetService();
        ConceptService conceptService = Context.getConceptService();
        if (orderSetService.getOrderSetByUuid(TDF_3TC_DTG_ETV_DRV_R) == null) {

            OrderSet orderSet = orderSetBuilder("TDF/3TC/DTG/ETV/DRV/r", "Adult (Third line)", TDF_3TC_DTG_ETV_DRV_R);

            OrderSetMember TDF = orderSetMemberBuilder(conceptService.getConcept(84795));
            OrderSetMember _3TC = orderSetMemberBuilder(conceptService.getConcept(78643));
            OrderSetMember DTG = orderSetMemberBuilder(conceptService.getConcept(164967));
            OrderSetMember ETV = orderSetMemberBuilder(conceptService.getConcept(159810));
            OrderSetMember DRV_R = orderSetMemberBuilder(conceptService.getConcept(162796));

            orderSet.addOrderSetMember(TDF);
            orderSet.addOrderSetMember(_3TC);
            orderSet.addOrderSetMember(DTG);
            orderSet.addOrderSetMember(ETV);
            orderSet.addOrderSetMember(DRV_R);

            Context.getOrderSetService().saveOrderSet(orderSet);
        }

        if (orderSetService.getOrderSetByUuid(ABC_3TC_DTG_DRV_R) == null) {
            OrderSet orderSet = orderSetBuilder("ABC/3TC/DTG/DRV/r", "Adult (Third line)", ABC_3TC_DTG_DRV_R);

            OrderSetMember ABC = orderSetMemberBuilder(conceptService.getConcept(70057));
            OrderSetMember _3TC = orderSetMemberBuilder(conceptService.getConcept(78643));
            OrderSetMember DTG = orderSetMemberBuilder(conceptService.getConcept(164967));
            OrderSetMember DRV_R = orderSetMemberBuilder(conceptService.getConcept(162796));

            orderSet.addOrderSetMember(ABC);
            orderSet.addOrderSetMember(_3TC);
            orderSet.addOrderSetMember(DTG);
            orderSet.addOrderSetMember(DRV_R);
            Context.getOrderSetService().saveOrderSet(orderSet);
        }

        if (orderSetService.getOrderSetByUuid(TDF_3TC_DTG_ATV_R) == null) {
            OrderSet orderSet = orderSetBuilder("TDF/3TC/DTG/ATV/r", "Adult (Third line)", TDF_3TC_DTG_ATV_R);

            OrderSetMember TDF = orderSetMemberBuilder(conceptService.getConcept(84795));
            OrderSetMember _3TC = orderSetMemberBuilder(conceptService.getConcept(78643));
            OrderSetMember DTG = orderSetMemberBuilder(conceptService.getConcept(164967));
            OrderSetMember ATV_R = orderSetMemberBuilder(conceptService.getConcept(71647));

            orderSet.addOrderSetMember(TDF);
            orderSet.addOrderSetMember(_3TC);
            orderSet.addOrderSetMember(DTG);
            orderSet.addOrderSetMember(ATV_R);
            Context.getOrderSetService().saveOrderSet(orderSet);
        }

        if (orderSetService.getOrderSetByUuid(B_F_TAF) == null) {
            OrderSet orderSet = orderSetBuilder("B/F/TAF", "Adult (first line)", B_F_TAF);

            OrderSetMember B = orderSetMemberBuilder(conceptService.getConcept(167205));
            OrderSetMember F = orderSetMemberBuilder(conceptService.getConcept(75628));
            OrderSetMember TAF = orderSetMemberBuilder(conceptService.getConcept(84795));

            orderSet.addOrderSetMember(B);
            orderSet.addOrderSetMember(F);
            orderSet.addOrderSetMember(TAF);
            Context.getOrderSetService().saveOrderSet(orderSet);
        }

        out.println("Completed adding orderset and the members");

    }

    private OrderSet orderSetBuilder(String name, String description, String uuid) {
        OrderSet orderSet = new OrderSet();
        orderSet.setName(name);
        orderSet.setUuid(uuid);
        orderSet.setDescription(description);
        orderSet.setOperator(OrderSet.Operator.ANY);
        return orderSet;
    }

    private OrderSetMember orderSetMemberBuilder(Concept concept) {

        OrderSetMember orderSetMember = new OrderSetMember();
        orderSetMember.setOrderType(Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID));
        orderSetMember.setConcept(concept);
        orderSetMember.setCreator(new User(1));
        orderSetMember.setDateCreated(new Date());
        orderSetMember.setRetired(false);

        return orderSetMember;
    }

}
