package com.example.beginner;

import java.util.Scanner;

public class EnhancedCalculator {

    // Method for factorial
    static long factorial(int n) {
        if (n < 0) return -1; // invalid
        long fact = 1;
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean continueCalc = true;

        while (continueCalc) {
            System.out.println("\n==== Enhanced Calculator ====");
            System.out.println("1. Addition");
            System.out.println("2. Subtraction");
            System.out.println("3. Multiplication");
            System.out.println("4. Division");
            System.out.println("5. Modulus");
            System.out.println("6. Power");
            System.out.println("7. Square Root");
            System.out.println("8. Factorial");
            System.out.println("9. Exit");
            System.out.print("Choose an operation: ");
            
            int choice = sc.nextInt();

            switch (choice) {
                case 1: // Addition
                    System.out.print("Enter first number: ");
                    double a1 = sc.nextDouble();
                    System.out.print("Enter second number: ");
                    double b1 = sc.nextDouble();
                    System.out.println("Result = " + (a1 + b1));
                    break;

                case 2: // Subtraction
                    System.out.print("Enter first number: ");
                    double a2 = sc.nextDouble();
                    System.out.print("Enter second number: ");
                    double b2 = sc.nextDouble();
                    System.out.println("Result = " + (a2 - b2));
                    break;

                case 3: // Multiplication
                    System.out.print("Enter first number: ");
                    double a3 = sc.nextDouble();
                    System.out.print("Enter second number: ");
                    double b3 = sc.nextDouble();
                    System.out.println("Result = " + (a3 * b3));
                    break;

                case 4: // Division
                    System.out.print("Enter numerator: ");
                    double num = sc.nextDouble();
                    System.out.print("Enter denominator: ");
                    double den = sc.nextDouble();
                    if (den == 0) {
                        System.out.println("Error! Cannot divide by zero.");
                    } else {
                        System.out.println("Result = " + (num / den));
                    }
                    break;

                case 5: // Modulus
                    System.out.print("Enter first number: ");
                    int m1 = sc.nextInt();
                    System.out.print("Enter second number: ");
                    int m2 = sc.nextInt();
                    if (m2 == 0) {
                        System.out.println("Error! Cannot take modulus with zero.");
                    } else {
                        System.out.println("Result = " + (m1 % m2));
                    }
                    break;

                case 6: // Power
                    System.out.print("Enter base: ");
                    double base = sc.nextDouble();
                    System.out.print("Enter exponent: ");
                    double exp = sc.nextDouble();
                    System.out.println("Result = " + Math.pow(base, exp));
                    break;

                case 7: // Square Root
                    System.out.print("Enter number: ");
                    double n = sc.nextDouble();
                    if (n < 0) {
                        System.out.println("Error! Cannot take square root of negative number.");
                    } else {
                        System.out.println("Result = " + Math.sqrt(n));
                    }
                    break;

                case 8: // Factorial
                    System.out.print("Enter integer: ");
                    int f = sc.nextInt();
                    long result = factorial(f);
                    if (result == -1) {
                        System.out.println("Error! Factorial not defined for negative numbers.");
                    } else {
                        System.out.println("Result = " + result);
                    }
                    break;

                case 9:
                    continueCalc = false;
                    System.out.println("Exiting calculator. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
        sc.close();
    }
}
