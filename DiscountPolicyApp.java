import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DiscountPolicyApp extends JFrame {
    private Controller controller;
    private JList<DiscountPolicy> policyList;
    private DefaultListModel<DiscountPolicy> listModel;
    private JTextField nameField, percentageField, minOrderField;
    private JTextArea conditionArea;
    private JLabel resultLabel;

    public DiscountPolicyApp() {
        controller = new Controller();
        
        setTitle("Discount Policy Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Policy List Panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<>();
        policyList = new JList<>(listModel);
        listPanel.add(new JScrollPane(policyList), BorderLayout.CENTER);
        listPanel.add(new JLabel("Discount Policies"), BorderLayout.NORTH);
        add(listPanel, BorderLayout.WEST);

        // Policy Details Panel
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        detailsPanel.add(new JLabel("Policy Name:"));
        nameField = new JTextField();
        detailsPanel.add(nameField);
        
        detailsPanel.add(new JLabel("Discount Percentage:"));
        percentageField = new JTextField();
        detailsPanel.add(percentageField);
        
        detailsPanel.add(new JLabel("Minimum Order Amount:"));
        minOrderField = new JTextField();
        detailsPanel.add(minOrderField);
        
        detailsPanel.add(new JLabel("Condition:"));
        conditionArea = new JTextArea(3, 20);
        detailsPanel.add(new JScrollPane(conditionArea));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Create");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton applyButton = new JButton("Apply to Order");
        buttonsPanel.add(createButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(applyButton);

        // Result Label
        resultLabel = new JLabel("Select a policy or create a new one");
        
        // Add panels to frame
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(detailsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);

        // Add action listeners
        createButton.addActionListener(e -> createPolicy());
        updateButton.addActionListener(e -> updatePolicy());
        deleteButton.addActionListener(e -> deletePolicy());
        applyButton.addActionListener(e -> applyPolicy());
        policyList.addListSelectionListener(e -> displayPolicyDetails());

        // Load initial policies
        refreshPolicyList();

        setVisible(true);
    }

    private void refreshPolicyList() {
        listModel.clear();
        List<DiscountPolicy> policies = controller.getAllPolicies();
        for (DiscountPolicy policy : policies) {
            listModel.addElement(policy);
        }
    }

    private void displayPolicyDetails() {
        DiscountPolicy selectedPolicy = policyList.getSelectedValue();
        if (selectedPolicy != null) {
            nameField.setText(selectedPolicy.getName());
            percentageField.setText(String.valueOf(selectedPolicy.getPercentage()));
            minOrderField.setText(String.valueOf(selectedPolicy.getMinOrderAmount()));
            conditionArea.setText(selectedPolicy.getConditionString());
        }
    }

    private void createPolicy() {
        try {
            String name = nameField.getText();
            double percentage = Double.parseDouble(percentageField.getText());
            double minOrder = Double.parseDouble(minOrderField.getText());
            String condition = conditionArea.getText();

            DiscountPolicy newPolicy = new DiscountPolicy(name, percentage, minOrder, condition);
            controller.createPolicy(newPolicy);
            refreshPolicyList();
            resultLabel.setText("Policy created successfully");
        } catch (NumberFormatException ex) {
            resultLabel.setText("Invalid input. Please check your entries.");
        }
    }

    private void updatePolicy() {
        DiscountPolicy selectedPolicy = policyList.getSelectedValue();
        if (selectedPolicy != null) {
            try {
                selectedPolicy.setName(nameField.getText());
                selectedPolicy.setPercentage(Double.parseDouble(percentageField.getText()));
                selectedPolicy.setMinOrderAmount(Double.parseDouble(minOrderField.getText()));
                selectedPolicy.setConditionString(conditionArea.getText());

                controller.update(List.of(selectedPolicy));
                refreshPolicyList();
                resultLabel.setText("Policy updated successfully");
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid input. Please check your entries.");
            }
        } else {
            resultLabel.setText("Please select a policy to update");
        }
    }

    private void deletePolicy() {
        DiscountPolicy selectedPolicy = policyList.getSelectedValue();
        if (selectedPolicy != null) {
            controller.deletePolicy(selectedPolicy.getId());
            refreshPolicyList();
            resultLabel.setText("Policy deleted successfully");
        } else {
            resultLabel.setText("Please select a policy to delete");
        }
    }

    private void applyPolicy() {
        // This is a simplified version. In a real application, you'd have a more complex order input system.
        String orderAmountStr = JOptionPane.showInputDialog(this, "Enter order amount:");
        try {
            double orderAmount = Double.parseDouble(orderAmountStr);
            Order order = new Order(orderAmount);
            double discount = controller.applyDiscounts(order);
            resultLabel.setText(String.format("Applied discount: $%.2f. Final price: $%.2f", discount, orderAmount - discount));
        } catch (NumberFormatException ex) {
            resultLabel.setText("Invalid order amount");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DiscountPolicyApp();
            }
        });
    }
}