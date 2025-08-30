package com.example.Intermediate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Model class
class Product {
    String id, name;
    int quantity;
    double price;

    Product(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}

public class InventoryManagementSystem extends JFrame {
    private ArrayList<Product> inventory = new ArrayList<>();

    // UI components
    private JTextField idField, nameField, quantityField, priceField;
    private DefaultTableModel tableModel;
    private JTable table;

    public InventoryManagementSystem() {
        setTitle("Inventory Management System");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // --- Top panel for input ---
        JPanel inputPanel = new JPanel(new GridLayout(2,4,5,5));
        idField = new JTextField();
        nameField = new JTextField();
        quantityField = new JTextField();
        priceField = new JTextField();

        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);

        // --- Middle: Table to show products ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Quantity", "Price"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        // --- Bottom: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Product");
        JButton updateBtn = new JButton("Update Quantity");
        JButton deleteBtn = new JButton("Delete Product");
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        // --- Add everything to frame ---
        add(inputPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Event handling ---
        addBtn.addActionListener(e -> addProduct());
        updateBtn.addActionListener(e -> updateQuantity());
        deleteBtn.addActionListener(e -> deleteProduct());
    }

    private void addProduct() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            int qty = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID and Name cannot be empty.");
                return;
            }
            Product p = new Product(id, name, qty, price);
            inventory.add(p);
            tableModel.addRow(new Object[]{id, name, qty, price});

            // Clear fields
            idField.setText(""); nameField.setText("");
            quantityField.setText(""); priceField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be numbers.");
        }
    }

    private void updateQuantity() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to update.");
            return;
        }
        String newQtyStr = JOptionPane.showInputDialog(this, "Enter new quantity:");
        try {
            int newQty = Integer.parseInt(newQtyStr);
            inventory.get(row).quantity = newQty;
            tableModel.setValueAt(newQty, row, 2);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
            return;
        }
        inventory.remove(row);
        tableModel.removeRow(row);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryManagementSystem().setVisible(true));
    }
}



    

