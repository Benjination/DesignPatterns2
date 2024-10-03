import javax.swing.*;
import java.awt.*;

public class DiscountPolicyApp extends JFrame {
    private JTextField priceField;
    private JTextField discountField;
    private JLabel resultLabel;

    public DiscountPolicyApp() {
        setTitle("Discount Policy Calculator");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        // Original Price
        add(new JLabel("Original Price:"));
        priceField = new JTextField();
        add(priceField);

        // Discount Percentage
        add(new JLabel("Discount %:"));
        discountField = new JTextField();
        add(discountField);

        // Calculate Button
        JButton calculateButton = new JButton("Calculate");
        add(calculateButton);

        // Placeholder for future functionality
        add(new JLabel());

        // Result Display
        add(new JLabel("Discounted Price:"));
        resultLabel = new JLabel();
        add(resultLabel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DiscountPolicyApp();
            }
        });
    }
}