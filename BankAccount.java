package com.example.Intermediate;
public class BankAccount {
    private String accountNumber;
    private String holderName;
    private double balance;

    public BankAccount(String accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        if (balance < 0) throw new IllegalArgumentException("Initial balance cannot be negative.");
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive.");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive.");
        if (amount > balance) throw new IllegalArgumentException("Insufficient balance.");
        balance -= amount;
    }

    @Override
    public String toString() {
        return "Account: " + accountNumber + ", Name: " + holderName + ", Balance: " + balance;
    }
}


    

