import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
//import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiscountPolicyApp extends JFrame {
    private DefaultListModel<String> listModel;
    private JList<String> list;

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

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        exitItem.addActionListener(e -> System.exit(0));
    }

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

        newButton.addActionListener(e -> 
        System.out.println("New button clicked")
        //Add button functionality
        );
        editButton.addActionListener(e -> System.out.println("Edit button clicked")
        //Add Button Functionality
        );
        deleteButton.addActionListener(e -> System.out.println("Delete button clicked")
        //Add Button Functionality
        );
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DiscountPolicyApp());
    }
}

class ListItemTransferHandler extends TransferHandler {
    private int[] indices = null;
    private int addIndex = -1; // Location where items were added
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
            
            // Insert the items at the new position
            for (String item : toMove) {
                listModel.add(index++, item);
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