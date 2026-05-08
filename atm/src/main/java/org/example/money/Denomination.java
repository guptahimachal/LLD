package org.example.money;

public enum Denomination {

    TWO_THOUSAND(2000),
    FIVE_HUNDRED(500),
    TWO_HUNDRED(200),
    HUNDRED(100);

    Integer value;

    Denomination(Integer value) {
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }


}
