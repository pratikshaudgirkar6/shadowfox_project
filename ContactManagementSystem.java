package com.example.beginner;


    import java.util.ArrayList;
import java.util.Scanner;

class Contact {
    String name;
    String phone;
    String email;

    Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Phone: " + phone + ", Email: " + email;
    }
}

public class ContactManagementSystem {

    static ArrayList<Contact> contacts = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    // Add a contact
    static void addContact() {
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Enter phone: ");
        String phone = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();

        contacts.add(new Contact(name, phone, email));
        System.out.println("Contact added successfully!");
    }

    // View all contacts
    static void viewContacts() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
        } else {
            System.out.println("---- Contact List ----");
            for (int i = 0; i < contacts.size(); i++) {
                System.out.println((i+1) + ". " + contacts.get(i));
            }
        }
    }

    // Search contact
    static void searchContact() {
        System.out.print("Enter name to search: ");
        String name = sc.nextLine();
        boolean found = false;

        for (Contact c : contacts) {
            if (c.name.toLowerCase().contains(name.toLowerCase())) {
                System.out.println(c);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No contact found with name: " + name);
        }
    }

    // Delete contact
    static void deleteContact() {
        System.out.print("Enter name to delete: ");
        String name = sc.nextLine();
        boolean removed = contacts.removeIf(c -> c.name.equalsIgnoreCase(name));

        if (removed) {
            System.out.println("Contact deleted successfully!");
        } else {
            System.out.println("No contact found with name: " + name);
        }
    }

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n==== Contact Management System ====");
            System.out.println("1. Add Contact");
            System.out.println("2. View Contacts");
            System.out.println("3. Search Contact");
            System.out.println("4. Delete Contact");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": addContact(); break;
                case "2": viewContacts(); break;
                case "3": searchContact(); break;
                case "4": deleteContact(); break;
                case "5": running = false; System.out.println("Exiting program..."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
        sc.close();
    }
}

    

