package uc2;


//import com.designpatters.uc2.DiscountPolicy;

import javax.swing.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DiscountPolicyApp extends JFrame {
    
    public DefaultListModel<DiscountPolicy> listModel;
    public JList<DiscountPolicy> list;
    private List<Order> userPurchases = new ArrayList<>();
    private JLabel totalLabel;

    // GUI outline
    private Controller controller;
    public DiscountPolicyApp() {
        setTitle("Discount Policy Application");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    
        createMenuBar();
        createSidePanel();
        createMainContent();
    
        this.controller = new Controller();
        loadPolicies();
        setVisible(true);   
    }

    // MenuBar
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        newItem.addActionListener(e -> new DiscountPolicyForm(this));
        exitItem.addActionListener(e -> System.exit(0));
    }

    // Side Panel with buttons
    private void createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton newButton = new JButton("New");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton purchaseButton = new JButton("Purchase");
        JButton viewPurchasesButton = new JButton("View Purchases");
        JButton clearPurchasesButton = new JButton("Clear Purchases");
       
        // JButton loadButton = new JButton("Load");

        totalLabel = new JLabel("Total: $0.00"); // Label to show the total cost
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidePanel.add(newButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(editButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(deleteButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(saveButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(purchaseButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(viewPurchasesButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(totalLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(clearPurchasesButton);
        // sidePanel.add(loadButton);

        add(sidePanel, BorderLayout.WEST);

        newButton.addActionListener(e -> new DiscountPolicyForm(this));
        editButton.addActionListener(e -> editSelectedPolicy());
        deleteButton.addActionListener(e -> deleteSelectedPolicy());
        saveButton.addActionListener(e -> savePolicies());
        purchaseButton.addActionListener(e -> handlePurchase());
        viewPurchasesButton.addActionListener(e -> viewPurchases()); // Attach action to "View Purchases" button
        clearPurchasesButton.addActionListener(e -> clearPurchases());
        // loadButton.addActionListener(e -> loadPolicies());
    }

    // Main Window
    // String Sample data will be updated with actual policies converted into
    // Strings

    private static final String SAVE_FILE = "discount_policies.txt";


    private void createMainContent() {
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setDragEnabled(true);
        list.setDropMode(DropMode.INSERT);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DiscountPolicy) {
                    setText(((DiscountPolicy) value).toString());
                }
                return this;
            }
        });

        list.setTransferHandler(new ListItemTransferHandler());

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void addItem(DiscountPolicy policy) {
        listModel.addElement(policy);
        savePolicies();
    }

    private void editSelectedPolicy() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            DiscountPolicy selectedPolicy = listModel.getElementAt(selectedIndex);
            new DiscountPolicyForm(this, selectedIndex, selectedPolicy);
        }
    }

    private void deleteSelectedPolicy() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this policy?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                listModel.remove(selectedIndex);
                savePolicies();
            }
        }
    }

    public void savePolicies() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            for (int i = 0; i < listModel.size(); i++) {
                DiscountPolicy policy = listModel.getElementAt(i);
                writer.write(policy.getName() + "," + policy.getDiscountType() + "," + policy.getDiscountValue() + "," + policy.getMinPurchase());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving policies", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadPolicies() {
        listModel.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    DiscountPolicy policy = new DiscountPolicy(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
                    listModel.addElement(policy);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing policies found. Starting with an empty list.");
        }
    }

    private void clearPurchases() {
        int response = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all purchases?", 
            "Confirm Clear Purchases", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            userPurchases.clear();
            updateTotal();
            JOptionPane.showMessageDialog(this, 
                "All purchases have been cleared.", 
                "Purchases Cleared", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handlePurchase() {
        PurchaseForm purchaseForm = new PurchaseForm(this);
        Order order = purchaseForm.getOrder();
        if (order != null) {
            DiscountPolicy applicablePolicy = findApplicablePolicy(order.getTotalCost());
            double discount = 0;
            String policyApplied = "No policy applied";
            
            if (applicablePolicy != null) {
                discount = applicablePolicy.calculateDiscount(order.getTotalCost());
                policyApplied = applicablePolicy.getName();
            }
            
            double finalCost = order.getTotalCost() - discount;
            
            JOptionPane.showMessageDialog(this, 
                String.format("Purchase details:\n" +
                              "Total amount: $%.2f\n" +
                              "Discount applied: %s\n" +
                              "Amount saved: $%.2f\n" +
                              "Final price: $%.2f", 
                    order.getTotalCost(), 
                    policyApplied,
                    discount,
                    finalCost),
                "Purchase Result", JOptionPane.INFORMATION_MESSAGE);
            
            userPurchases.add(new Order(order.getNumberOfItems(), finalCost, order.isNewCustomer()));
            updateTotal();
        }
    }
    
    private DiscountPolicy findApplicablePolicy(double totalAmount) {
        for (int i = 0; i < listModel.size(); i++) {
            DiscountPolicy policy = listModel.getElementAt(i);
            if (totalAmount >= policy.getMinPurchase()) {
                return policy;
            }
        }
        return null;
    }
    
    private DiscountHandler buildDiscountChain() {
        List<DiscountPolicy> policies = getAllPolicies();
        if (policies.isEmpty()) {
            return null;
        }
        
        DiscountHandler firstHandler = new ConcreteDiscountHandler(policies.get(0));
        DiscountHandler current = firstHandler;
        
        for (int i = 1; i < policies.size(); i++) {
            DiscountHandler next = new ConcreteDiscountHandler(policies.get(i));
            current.setNext(next);
            current = next;
        }
        
        return firstHandler;
    }
    
    private List<DiscountPolicy> getAllPolicies() {
        List<DiscountPolicy> policies = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            policies.add(listModel.getElementAt(i));
        }
        return policies;
    }

    private void viewPurchases() {
        if (userPurchases.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No purchases made yet.", "View Purchases",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder purchasesInfo = new StringBuilder("User Purchases:\n");
        for (Order order : userPurchases) {
            purchasesInfo.append("Items: ").append(order.getNumberOfItems())
                    .append(", This Order Total Cost: $").append(order.getTotalCost())
                    .append(", New Customer: ").append(order.isNewCustomer() ? "Yes" : "No")
                    .append("\n");
        }

        JOptionPane.showMessageDialog(this, purchasesInfo.toString(), "View Purchases",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTotal() {
        double total = userPurchases.stream().mapToDouble(Order::getTotalCost).sum();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DiscountPolicyApp());
    }
}

class DiscountPolicyForm extends JDialog {
    private JTextField policyNameField;
    private JComboBox<String> discountTypeField;
    private JTextField discountValueField;
    private JTextField minPurchaseField;
    private JButton submitButton;
    private DiscountPolicyApp parentApp;
    private int editIndex = -1;

    public DiscountPolicyForm(DiscountPolicyApp parentApp) {
        this(parentApp, -1, null);
    }

    public DiscountPolicyForm(DiscountPolicyApp parentApp, int editIndex, DiscountPolicy existingPolicy) {
        this.parentApp = parentApp;
        this.editIndex = editIndex;

        setTitle(editIndex == -1 ? "Create New Discount Policy" : "Edit Discount Policy");
        setSize(400, 300);
        setLocationRelativeTo(parentApp);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Policy Name:"));
        policyNameField = new JTextField();
        add(policyNameField);

        add(new JLabel("Discount Type:"));
        String[] discountTypes = { "Fixed", "Percentage" };
        discountTypeField = new JComboBox<>(discountTypes);
        add(discountTypeField);

        add(new JLabel("Discount Value:"));
        discountValueField = new JTextField();
        add(discountValueField);

        add(new JLabel("Minimum Purchase:"));
        minPurchaseField = new JTextField();
        add(minPurchaseField);

        submitButton = new JButton("Submit");
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(e -> submitForm());

        if (existingPolicy != null) {
            policyNameField.setText(existingPolicy.getName());
            discountTypeField.setSelectedItem(existingPolicy.getDiscountType());
            discountValueField.setText(String.valueOf(existingPolicy.getDiscountValue()));
            minPurchaseField.setText(String.valueOf(existingPolicy.getMinPurchase()));
        }

        setModal(true);
        setVisible(true);
    }

    private void submitForm() {
        String policyName = policyNameField.getText();
        String discountType = (String) discountTypeField.getSelectedItem();
        String discountValueText = discountValueField.getText();
        String minPurchaseText = minPurchaseField.getText();

        if (policyName.isEmpty() || discountValueText.isEmpty() || minPurchaseText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double discountValue = Double.parseDouble(discountValueText);
            double minPurchase = Double.parseDouble(minPurchaseText);

            if (discountValue < 0 || minPurchase < 0) {
                throw new NumberFormatException();
            }

            if (discountType.equals("Percentage") && discountValue > 100) {
                JOptionPane.showMessageDialog(this, "Percentage discount cannot exceed 100%", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DiscountPolicy policy = new DiscountPolicy(policyName, discountType, discountValue, minPurchase);

            if (editIndex == -1) {
                parentApp.addItem(policy);
            } else {
                parentApp.listModel.set(editIndex, policy);
                parentApp.savePolicies();
            }

            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for discount value and minimum purchase", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class PurchaseForm extends JDialog {
    private JTextField totalAmountField;
    private JButton submitButton;
    private double totalAmount;
    private boolean submitted = false;

    public PurchaseForm(JFrame parent) {
        setTitle("Purchase Information");
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(2, 2));

        add(new JLabel("Total Amount:"));
        totalAmountField = new JTextField();
        add(totalAmountField);

        submitButton = new JButton("Submit");
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(e -> {
            try {
                totalAmount = Double.parseDouble(totalAmountField.getText());
                submitted = true;
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for total amount", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setModal(true);
        setVisible(true);
    }

    public Order getOrder() {
        if (submitted) {
            return new Order(1, totalAmount, false); // Assuming 1 item and not a new customer
        }
        return null;
    }


    public double getTotalAmount() {
        return totalAmount;
    }
}

interface DiscountHandler {
    DiscountResult handleDiscount(double amount);
    void setNext(DiscountHandler next);
}

class ConcreteDiscountHandler implements DiscountHandler {
    private DiscountPolicy policy;
    private DiscountHandler nextHandler;

    public ConcreteDiscountHandler(DiscountPolicy policy) {
        this.policy = policy;
    }

    @Override
    public DiscountResult handleDiscount(double amount) {
        if (amount >= policy.getMinPurchase()) {
            double discountAmount = policy.calculateDiscount(amount);
            return new DiscountResult(policy, discountAmount, amount - discountAmount);
        } else if (nextHandler != null) {
            return nextHandler.handleDiscount(amount);
        }
        return null;
    }

    @Override
    public void setNext(DiscountHandler next) {
        this.nextHandler = next;
    }
}

class DiscountResult {
    DiscountPolicy appliedPolicy;
    double discountAmount;
    double finalPrice;

    public DiscountResult(DiscountPolicy appliedPolicy, double discountAmount, double finalPrice) {
        this.appliedPolicy = appliedPolicy;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
    }
}



class ListItemTransferHandler extends TransferHandler {
    private int[] indices = null;
    @SuppressWarnings("unused")
    private int addIndex = -1; // Location where items were added
    @SuppressWarnings("unused")
    private int addCount = 0; // Number of items added

    @Override
protected Transferable createTransferable(JComponent c) {
    @SuppressWarnings("unchecked")
    JList<DiscountPolicy> list = (JList<DiscountPolicy>) c;
    indices = list.getSelectedIndices();
    List<DiscountPolicy> values = list.getSelectedValuesList();
    return new StringSelection(String.join("\n", values.stream().map(DiscountPolicy::toString).toArray(String[]::new)));
}



    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        JList<?> target = (JList<?>) support.getComponent();
        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
        @SuppressWarnings("unchecked")
        DefaultListModel<DiscountPolicy> listModel = (DefaultListModel<DiscountPolicy>) target.getModel();
        int index = dl.getIndex();

        try {
            String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            String[] values = data.split("\n");

            List<DiscountPolicy> toMove = new ArrayList<>();
            for (String value : values) {
                String[] parts = value.split(" - |: | \\(Min Purchase: |\\)");
                toMove.add(new DiscountPolicy(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
            }

            for (int i = indices.length - 1; i >= 0; i--) {
                if (indices[i] < index) {
                    index--;
                }
                listModel.remove(indices[i]);
            }

            for (int i = 0; i < toMove.size(); i++) {
                listModel.add(index++, toMove.get(i));
            }

            addIndex = index - toMove.size();
            addCount = toMove.size();
            return true;
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        indices = null;
        addCount = 0;
        addIndex = -1;
    }
}