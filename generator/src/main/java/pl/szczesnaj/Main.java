package pl.szczesnaj;

import pl.szczesnaj.generator.CustomerGenerator;

public class Main {

    public static final int DEFAULT_CUSTOMERS_AMOUNT = 100;

    public static void main(String[] args) {
        int customerAmount = getCustomerAmount(args);
        CustomerGenerator generator = new CustomerGenerator();
        generator.generate(customerAmount);
    }

    private static int getCustomerAmount(String[] args) {
        try {
            if (args.length > 0) {
                return Integer.parseInt(args[0]);
            }
            return DEFAULT_CUSTOMERS_AMOUNT;
        } catch (NumberFormatException exc) {
            System.out.println("not provide customers amount");
            return DEFAULT_CUSTOMERS_AMOUNT;
        }
    }
}


