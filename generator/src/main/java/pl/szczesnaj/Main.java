package pl.szczesnaj;

import pl.szczesnaj.generator.CustomerGenerator;

public class Main {

    public static final int DEFAULT_NUMBER = 10;

    public static void main(String[] args) {
        int customerNumber = getCustomerNumber(args);
        CustomerGenerator generator = new CustomerGenerator();
        generator.generate(customerNumber);
    }

    private static int getCustomerNumber(String[] args) {
        try {
            if (args.length > 0) {
                return Integer.parseInt(args[0]);
            }
            return DEFAULT_NUMBER;
        } catch (NumberFormatException exc) {
            System.out.println("no customers number");
            return DEFAULT_NUMBER;
        }
    }
}


