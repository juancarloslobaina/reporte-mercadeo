package com.labreferencia.searchspec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

    private String key;
    private String operator;
    private Object value;

    public SearchOperation getOperation() {
        switch (operator) {
            case ">=":
                return SearchOperation.GREATER_THAN_EQUAL;
            case "<=":
                return SearchOperation.LESS_THAN_EQUAL;
            case "<":
                return SearchOperation.LESS_THAN;
            case ">":
                return SearchOperation.GREATER_THAN;
            case "=":
                return SearchOperation.EQUAL;
            case "<>":
                return SearchOperation.NOT_EQUAL;
            default:
                return SearchOperation.MATCH;
        }
    }
}
