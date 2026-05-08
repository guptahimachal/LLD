package src.main.java.open_closed.withoutOc;

import java.math.BigDecimal;

public class Invoice {

    private String invoiceNum;
    private BigDecimal amount;

    void save() {
        System.out.printf("Saving to DB %s , %s%n", invoiceNum, amount);
    }

    // Suppose new functionality regarding save to file
    void saveToFile() {
        System.out.printf("Saving to File %s , %s%n", invoiceNum, amount);

    }


}
