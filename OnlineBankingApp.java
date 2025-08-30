package com.example;


import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class OnlineBankingApp extends Application {

    private final BankService bank = new BankService();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("JavaFX Online Banking");
        stage.setScene(buildLoginScene());
        stage.show();
    }

    private Scene buildLoginScene() {
        var title = new Label("Welcome to Online Banking");
        title.getStyleClass().add("title");

        var userField = new TextField();
        userField.setPromptText("Username (e.g., alice)");
        var passField = new PasswordField();
        passField.setPromptText("Password (try 1234)");

        var loginBtn = new Button("Sign in");
        loginBtn.setDefaultButton(true);
        loginBtn.setOnAction(e -> {
            var user = bank.login(userField.getText().trim(), passField.getText());
            if (user != null) {
                primaryStage.setScene(buildMainScene(user));
            } else {
                showError("Login failed", "Invalid username or password.");
            }
        });

        var form = new VBox(10, title, userField, passField, loginBtn);
        form.setPadding(new Insets(24));
        form.setAlignment(Pos.CENTER);
        var root = new StackPane(form);
        root.setPadding(new Insets(24));
        root.getStylesheets().add(inlineStyles());
        return new Scene(root, 980, 640);
    }

    private Scene buildMainScene(User user) {
        var topBar = buildTopBar(user);
        var tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        var accountsTab = new Tab("Accounts", buildAccountsView(user));
        var transferTab = new Tab("Transfer", buildTransferView(user));
        var historyTab = new Tab("History", buildHistoryView(user));
        var profileTab = new Tab("Profile", buildProfileView(user));

        tabs.getTabs().addAll(accountsTab, transferTab, historyTab, profileTab);

        var root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabs);
        root.getStylesheets().add(inlineStyles());
        var scene = new Scene(root, 1100, 720);
        return scene;
    }

    private Node buildTopBar(User user) {
        var hello = new Label("Hello, " + user.getFullName());
        hello.getStyleClass().add("headline");

        var totalBalance = new Label();
        totalBalance.getStyleClass().add("balance");
        totalBalance.textProperty().bind(Bindings.createStringBinding(() ->
                "Total: " + Money.format(bank.totalBalance(user.getUsername())),
                bank.accountsProperty(user.getUsername())));

        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        var logout = new Button("Logout");
        logout.setOnAction(e -> primaryStage.setScene(buildLoginScene()));

        var bar = new HBox(16, hello, totalBalance, spacer, logout);
        bar.setPadding(new Insets(16));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("topbar");
        return bar;
    }

    private Node buildAccountsView(User user) {
        var table = new TableView<Account>();
        table.setItems(bank.listAccounts(user.getUsername()));

        var colName = new TableColumn<Account, String>("Account");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(220);

        var colId = new TableColumn<Account, String>("Number");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(180);

        var colType = new TableColumn<Account, String>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(120);

        var colBal = new TableColumn<Account, String>("Balance");
        colBal.setCellValueFactory(cd -> new SimpleStringProperty(Money.format(cd.getValue().getBalance())));
        colBal.setPrefWidth(140);

        table.getColumns().addAll(colName, colId, colType, colBal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        var refresh = new Button("Refresh");
        refresh.setOnAction(e -> table.refresh());

        var box = new VBox(10, table, refresh);
        box.setPadding(new Insets(16));
        return box;
    }

    private Node buildTransferView(User user) {
        var fromCbx = new ComboBox<Account>();
        fromCbx.setPromptText("From account");
        fromCbx.setItems(bank.listAccounts(user.getUsername()));
        fromCbx.setConverter(Account.accountConverter());

        var toCbx = new ComboBox<Account>();
        toCbx.setPromptText("To account or payee");
        toCbx.setItems(bank.listAllPayableAccounts(user.getUsername()));
        toCbx.setConverter(Account.accountConverter());

        var amtField = new TextField();
        amtField.setPromptText("Amount (e.g., 250.00)");
        var descField = new TextField();
        descField.setPromptText("Description (optional)");

        var transferBtn = new Button("Send");
        transferBtn.setDefaultButton(true);

        transferBtn.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        fromCbx.getValue() == null || toCbx.getValue() == null ||
                                fromCbx.getValue().getId().equals(Optional.ofNullable(toCbx.getValue()).map(Account::getId).orElse("")) ||
                                !Money.isValid(amtField.getText()),
                fromCbx.valueProperty(), toCbx.valueProperty(), amtField.textProperty()));

        transferBtn.setOnAction(e -> {
            try {
                var amount = Money.parse(amtField.getText());
                bank.transfer(fromCbx.getValue().getId(), toCbx.getValue().getId(), amount, descField.getText());
                showInfo("Transfer successful", Money.format(amount) + " sent to " + toCbx.getValue().getName());
                amtField.clear();
                descField.clear();
            } catch (IllegalArgumentException ex) {
                showError("Transfer failed", ex.getMessage());
            }
        });

        var form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.addRow(0, new Label("From"), fromCbx);
        form.addRow(1, new Label("To"), toCbx);
        form.addRow(2, new Label("Amount"), amtField);
        form.addRow(3, new Label("Description"), descField);
        form.add(transferBtn, 1, 4);
        GridPane.setHalignment(transferBtn, HPos.RIGHT);

        var wrap = new VBox(16, new Label("Make a Transfer"), form);
        wrap.setPadding(new Insets(16));
        return wrap;
    }

    private Node buildHistoryView(User user) {
        var table = new TableView<Transaction>();
        var search = new TextField();
        search.setPromptText("Search description or account...");

        var all = bank.transactionsForUser(user.getUsername());
        var filtered = new FilteredList<>(all, t -> true);
        search.textProperty().addListener((obs, old, val) -> {
            String q = Optional.ofNullable(val).orElse("").toLowerCase();
            filtered.setPredicate(t ->
                    t.getDescription().toLowerCase().contains(q) ||
                            t.getFromAccountId().toLowerCase().contains(q) ||
                            t.getToAccountId().toLowerCase().contains(q)
            );
        });

        table.setItems(filtered);

        var colWhen = new TableColumn<Transaction, String>("Date");
        colWhen.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getWhenFormatted()));
        colWhen.setPrefWidth(180);

        var colFrom = new TableColumn<Transaction, String>("From");
        colFrom.setCellValueFactory(new PropertyValueFactory<>("fromAccountId"));
        colFrom.setPrefWidth(180);

        var colTo = new TableColumn<Transaction, String>("To");
        colTo.setCellValueFactory(new PropertyValueFactory<>("toAccountId"));
        colTo.setPrefWidth(180);

        var colAmt = new TableColumn<Transaction, String>("Amount");
        colAmt.setCellValueFactory(cd -> new SimpleStringProperty(Money.format(cd.getValue().getAmount())));
        colAmt.setPrefWidth(140);

        var colDesc = new TableColumn<Transaction, String>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(300);

        table.getColumns().addAll(colWhen, colFrom, colTo, colAmt, colDesc);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        var box = new VBox(10, search, table);
        box.setPadding(new Insets(16));
        return box;
    }

    private Node buildProfileView(User user) {
        var name = new Label(user.getFullName());
        name.getStyleClass().add("headline");

        var changePwdField = new PasswordField();
        changePwdField.setPromptText("New password");
        var confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");
        var saveBtn = new Button("Change Password");

        saveBtn.disableProperty().bind(Bindings.createBooleanBinding(() ->
                changePwdField.getText().isBlank() || !Objects.equals(changePwdField.getText(), confirmField.getText()),
                changePwdField.textProperty(), confirmField.textProperty()));

        saveBtn.setOnAction(e -> {
            bank.changePassword(user.getUsername(), changePwdField.getText());
            changePwdField.clear();
            confirmField.clear();
            showInfo("Password updated", "Your password has been changed.");
        });

        var grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.addRow(0, new Label("New password"), changePwdField);
        grid.addRow(1, new Label("Confirm"), confirmField);
        grid.add(saveBtn, 1, 2);
        GridPane.setHalignment(saveBtn, HPos.RIGHT);

        var wrap = new VBox(12, name, new Separator(), grid);
        wrap.setPadding(new Insets(16));
        return wrap;
    }

    private void showError(String title, String msg) {
        var a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(title);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        var a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(title);
        a.showAndWait();
    }

    private String inlineStyles() {
        return """
                .root { -fx-font-family: 'Segoe UI', sans-serif; }
                .title { -fx-font-size: 24px; -fx-font-weight: bold; }
                .headline { -fx-font-size: 18px; -fx-font-weight: 600; }
                .balance { -fx-font-size: 16px; -fx-text-fill: #0a6; }
                .topbar { -fx-background-color: linear-gradient(to right, #f9f9ff, #eef8ff); }
                .button { -fx-background-radius: 12; -fx-padding: 8 16; }
                .text-field, .combo-box { -fx-background-radius: 10; }
                """;
    }

    // ======== Models ========
    public static class User {
        private final String username;
        private String password; // demo only
        private final String fullName;

        public User(String username, String password, String fullName) {
            this.username = username; this.password = password; this.fullName = fullName;
        }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
    }

    public static class Account {
        private final StringProperty id = new SimpleStringProperty();
        private final StringProperty ownerUsername = new SimpleStringProperty();
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty type = new SimpleStringProperty();
        private final ObjectProperty<BigDecimal> balance = new SimpleObjectProperty<>(BigDecimal.ZERO);

        public Account(String id, String ownerUsername, String name, String type, BigDecimal balance) {
            setId(id); setOwnerUsername(ownerUsername); setName(name); setType(type); setBalance(balance);
        }
        public String getId() { return id.get(); }
        public void setId(String v) { id.set(v); }
        public StringProperty idProperty() { return id; }
        public String getOwnerUsername() { return ownerUsername.get(); }
        public void setOwnerUsername(String v) { ownerUsername.set(v); }
        public StringProperty ownerUsernameProperty() { return ownerUsername; }
        public String getName() { return name.get(); }
        public void setName(String v) { name.set(v); }
        public StringProperty nameProperty() { return name; }
        public String getType() { return type.get(); }
        public void setType(String v) { type.set(v); }
        public StringProperty typeProperty() { return type; }
        public BigDecimal getBalance() { return balance.get(); }
        public void setBalance(BigDecimal v) { balance.set(v); }
        public ObjectProperty<BigDecimal> balanceProperty() { return balance; }

        public static StringConverter<Account> accountConverter() {
            return new StringConverter<>() {
                @Override public String toString(Account a) { return a == null ? "" : a.getName() + " (" + a.getId() + ")"; }
                @Override public Account fromString(String s) { return null; }
            };
        }
    }

    public static class Transaction {
        private final String id;
        private final LocalDateTime when;
        private final String fromAccountId;
        private final String toAccountId;
        private final BigDecimal amount;
        private final String description;

        public Transaction(String id, LocalDateTime when, String fromAccountId, String toAccountId, BigDecimal amount, String description) {
            this.id = id; this.when = when; this.fromAccountId = fromAccountId; this.toAccountId = toAccountId; this.amount = amount; this.description = description;
        }
        public String getId() { return id; }
        public LocalDateTime getWhen() { return when; }
        public String getFromAccountId() { return fromAccountId; }
        public String getToAccountId() { return toAccountId; }
        public BigDecimal getAmount() { return amount; }
        public String getDescription() { return description == null ? "" : description; }
        public String getWhenFormatted() { return when.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")); }
    }

    // ======== Money helpers ========
    public static class Money {
        public static String format(BigDecimal bd) {
            if (bd == null) return "₹0.00";
            return "₹" + bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        public static boolean isValid(String s) {
            try { return parse(s).compareTo(BigDecimal.ZERO) > 0; } catch (Exception e) { return false; }
        }
        public static BigDecimal parse(String s) {
            var x = new BigDecimal(s.trim());
            if (x.scale() > 2) x = x.setScale(2, RoundingMode.HALF_UP);
            return x;
        }
    }

    // ======== Service & Data ========
    public static class BankService {
        private final ObservableMap<String, User> users = FXCollections.observableHashMap();
        private final ObservableMap<String, Account> accounts = FXCollections.observableHashMap();
        private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        public BankService() { seed(); }

        public User login(String username, String password) {
            var u = users.get(username);
            return (u != null && Objects.equals(u.getPassword(), password)) ? u : null;
        }

        public void changePassword(String username, String newPass) {
            var u = users.get(username);
            if (u != null) u.setPassword(newPass);
        }

        public ObservableList<Account> listAccounts(String username) {
            return accounts.values().stream()
                    .filter(a -> a.getOwnerUsername().equals(username))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        public ObservableList<Account> listAllPayableAccounts(String username) {
            // include user's own accounts + known payees (other users' accounts)
            return accounts.values().stream()
                    .filter(a -> true) // in demo, allow all accounts
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        public BigDecimal totalBalance(String username) {
            return listAccounts(username).stream()
                    .map(Account::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public ReadOnlyObjectProperty<ObservableMap<String, Account>> accountsProperty(String username) {
            // A crude binding handle: we return a property whose value changes when any account balance changes.
            // For demo we just wrap the map itself.
            return new SimpleObjectProperty<>(accounts);
        }

        public void transfer(String fromId, String toId, BigDecimal amount, String desc) {
            var from = accounts.get(fromId);
            var to = accounts.get(toId);
            if (from == null) throw new IllegalArgumentException("From account not found");
            if (to == null) throw new IllegalArgumentException("To account not found");
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
            if (from.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient funds");

            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));

            var t = new Transaction(UUID.randomUUID().toString(), LocalDateTime.now(), fromId, toId, amount, desc);
            transactions.add(0, t); // newest first
        }

        public ObservableList<Transaction> transactionsForUser(String username) {
            var userAccountIds = listAccounts(username).stream().map(Account::getId).collect(Collectors.toSet());
            return transactions.filtered(t -> userAccountIds.contains(t.getFromAccountId()) || userAccountIds.contains(t.getToAccountId()));
        }

        private void seed() {
            users.put("alice", new User("alice", "1234", "Alice Johnson"));
            users.put("bob", new User("bob", "1234", "Bob Singh"));

            // Alice accounts
            addAccount(new Account("AC-11001", "alice", "Alice • Savings", "SAVINGS", new BigDecimal("25000.00")));
            addAccount(new Account("AC-11002", "alice", "Alice • Checking", "CHECKING", new BigDecimal("7300.50")));

            // Bob accounts
            addAccount(new Account("AC-22001", "bob", "Bob • Salary", "CHECKING", new BigDecimal("15890.75")));
            addAccount(new Account("AC-22002", "bob", "Bob • Travel", "SAVINGS", new BigDecimal("9800.00")));

            // Sample transactions
            transfer("AC-11001", "AC-11002", new BigDecimal("500.00"), "Initial top-up");
            transfer("AC-11002", "AC-22001", new BigDecimal("1200.00"), "Rent share");
        }

        private void addAccount(Account a) {
            accounts.put(a.getId(), a);
        }
    }

    public static void main(String[] args) { launch(args); }
}


    

