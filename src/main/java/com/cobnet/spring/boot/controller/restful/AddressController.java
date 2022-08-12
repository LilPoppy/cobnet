package com.cobnet.spring.boot.controller.restful;

import com.cobnet.interfaces.spring.controller.annotations.*;
import com.cobnet.spring.boot.controller.support.enums.ComparisonOperator;
import com.cobnet.spring.boot.controller.support.enums.ConjunctiveOperator;
import com.cobnet.spring.boot.entity.Address;
import com.cobnet.spring.boot.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Address")
@RestController
@RequestMapping("/address")
public class AddressController {

    @Operation(summary = "Find matches information address.")
    @PostMapping("/autocomplete")
    public void autocomplete(@EntityVariable(groups = {
        @EntityAttributeCondition(attributes = {
                @EntityAttribute(attribute = "addresses", keys = @EntityKey(keys = {"#address.get(0).id"}, operator = ComparisonOperator.IS_IN))
        })
    }) User user,
    @EntityVariable(groups = {
        @EntityAttributeCondition(attributes = {
            @EntityAttribute(attribute = "id.street", keys = { @EntityKey(keys = {"#street"}, operator = ComparisonOperator.IS_NULL, conjunctive = ConjunctiveOperator.OR), @EntityKey(keys = "#street", operator = ComparisonOperator.IS_CONTAINING_IGNORE_CASE)}),
            @EntityAttribute(attribute = "id.postalCode", keys = { @EntityKey(keys = {"#postal-code"}, operator = ComparisonOperator.IS_NULL, conjunctive = ConjunctiveOperator.OR), @EntityKey(keys = "#postal-code", operator = ComparisonOperator.IS)}),
            @EntityAttribute(attribute = "city", keys = { @EntityKey(keys = {"#city"}, operator = ComparisonOperator.IS_NULL, conjunctive = ConjunctiveOperator.OR), @EntityKey(keys = "#city", operator = ComparisonOperator.IS_CONTAINING_IGNORE_CASE)}),
            @EntityAttribute(attribute = "state", keys = { @EntityKey(keys = {"#state"}, operator = ComparisonOperator.IS_NULL, conjunctive = ConjunctiveOperator.OR), @EntityKey(keys = "#state", operator = ComparisonOperator.IS_CONTAINING_IGNORE_CASE)}),
            @EntityAttribute(attribute = "country", keys = { @EntityKey(keys = {"#country"}, operator = ComparisonOperator.IS_NULL, conjunctive = ConjunctiveOperator.OR), @EntityKey(keys = "#country", operator = ComparisonOperator.IS_CONTAINING_IGNORE_CASE)})
            })
    }, collection = @EntityCollectionHint(distinct = true, orderBy = { @EntityOrderHint(attribute = "id.street")}, pagination = @EntityPageHint())) List<Address> address) {

        System.out.println("#:" + user);
        System.out.println("#:" + address);
    }
}
