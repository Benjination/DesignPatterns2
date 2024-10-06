import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiscountPolicyApp extends JFrame {
    public DefaultListModel<String> listModel;
    private JList<String> list;

    //GUI outline
    public DiscountPolicyApp() {
        setTitle("Discount Policy Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createMenuBar();
        createSidePanel();
        createMainContent();

        setVisible(true);
    }

    //MenuBar
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem newItem = new JMenuItem("New");
        //JMenuItem openItem = new JMenuItem("Open");
        //JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        fileMenu.add(newItem);
        //fileMenu.add(openItem);
        //fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        exitItem.addActionListener(e -> System.exit(0));
    }

    //Side Panel with buttons
    private void createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton newButton = new JButton("New");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        sidePanel.add(newButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(editButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(deleteButton);

        add(sidePanel, BorderLayout.WEST);

        newButton.addActionListener(e -> new DiscountPolicyForm(this));
        editButton.addActionListener(e -> editSelectedPolicy());
        deleteButton.addActionListener(e -> deleteSelectedPolicy());
    }

     //Main Window
    //String Sample data will be updated with actual policies converted into Strings
    private void createMainContent() {
        listModel = new DefaultListModel<>();
        String[] sampleData = {"Policy 1", "Policy 2", "Policy 3", "Policy 4", "Policy 5",
                               "Policy 6", "Policy 7", "Policy 8", "Policy 9", "Policy 10"};
        for (String item : sampleData) {
            listModel.addElement(item);
        }

        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setDragEnabled(true);
        list.setDropMode(DropMode.INSERT);

        list.setTransferHandler(new ListItemTransferHandler());

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);
    }

    // Method to add a new item to the list
    public void addItem(String item) {
        listModel.addElement(item);
    }

    private void editSelectedPolicy() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String selectedPolicy = listModel.get(selectedIndex);
        new DiscountPolicyForm(this, selectedIndex, selectedPolicy); // Pass the selected policy to the form for editing
    }

    private void deleteSelectedPolicy() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this policy?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            listModel.remove(selectedIndex);
        }
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
    private int editIndex = -1;  // To track if we are editing an existing policy

    public DiscountPolicyForm(DiscountPolicyApp parentApp) {
        this(parentApp, -1, null);
    }

    public DiscountPolicyForm(DiscountPolicyApp parentApp, int editIndex, String existingPolicy) {
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
        String[] discountTypes = {"Percentage", "Fixed Amount", "Variable"};
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
            String[] parts = existingPolicy.split(" - |: | \\(Min Purchase: |\\)");
            policyNameField.setText(parts[0]);
            discountTypeField.setSelectedItem(parts[1]);
            discountValueField.setText(parts[2]);
            minPurchaseField.setText(parts[3]);
        }

        setModal(true);
        setVisible(true);
    }

    private void submitForm() {
        String policyName = policyNameField.getText();
        String discountType = (String) discountTypeField.getSelectedItem();
        String discountValue = discountValueField.getText();
        String minPurchase = minPurchaseField.getText();

        if (policyName.isEmpty() || discountValue.isEmpty() || minPurchase.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String policyDetails = policyName + " - " + discountType + ": " + discountValue + " (Min Purchase: " + minPurchase + ")";

        if (editIndex == -1) {
            parentApp.addItem(policyDetails); // Add a new policy
        } else {
            parentApp.listModel.set(editIndex, policyDetails); // Update the existing policy
        }

        dispose();
    }
}

class ListItemTransferHandler extends TransferHandler {
    private int[] indices = null;
    @SuppressWarnings("unused")
    private int addIndex = -1; // Location where items were added
    @SuppressWarnings("unused")
    private int addCount = 0;  // Number of items added

    @Override
    protected Transferable createTransferable(JComponent c) {
        @SuppressWarnings("unchecked")
        JList<String> list = (JList<String>) c;
        indices = list.getSelectedIndices();
        List<String> values = list.getSelectedValuesList();
        return new StringSelection(String.join("\n", values));
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.getComponent() instanceof JList && support.isDrop();
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
        DefaultListModel<String> listModel = (DefaultListModel<String>) target.getModel();
        int index = dl.getIndex();

        try {
            String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            String[] values = data.split("\n");
            
            // Store the items to be moved
            List<String> toMove = new ArrayList<>();
            for (String value : values) {
                toMove.add(value);
            }
            
            // Remove the items from their original positions
            for (int i = indices.length - 1; i >= 0; i--) {
                if (indices[i] < index) {
                    index--;
                }
                listModel.remove(indices[i]);
            }

            // Add the items to their new positions
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