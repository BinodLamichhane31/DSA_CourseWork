import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DeliveryRouteOptimizer {

    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Delivery Route Optimizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Wrapper panel to add margin between the frame and the panels
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adds a 20px gap around the content

        // Top panel for title and buttons
        JPanel topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.LIGHT_GRAY);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };

        // Add padding inside the top panel
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title on the left
        JLabel titleLabel = new JLabel("Delivery Route Optimizer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Button panel on the right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton importButton = new JButton("Import Delivery List");
        JButton optimizeButton = new JButton("Optimize Route");
        buttonPanel.setOpaque(false); // Make panel transparent
        buttonPanel.add(importButton);
        buttonPanel.add(optimizeButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Main content area
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left box with delivery list and dropdowns
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Delivery List"));

        // Delivery list table
        String[] columnNames = {"Address", "Priority"};
        tableModel = new DefaultTableModel(new Object[][]{}, columnNames);
        JTable deliveryTable = new JTable(tableModel);
        JScrollPane deliveryListScrollPane = new JScrollPane(deliveryTable);
        leftPanel.add(deliveryListScrollPane, BorderLayout.CENTER);

        // Dropdowns for algorithm and vehicle
        JPanel dropdownPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        String[] algorithms = {"Algorithm 1", "Algorithm 2", "Algorithm 3"};
        String[] vehicles = {"Vehicle 1", "Vehicle 2", "Vehicle 3"};
        JComboBox<String> algorithmComboBox = new JComboBox<>(algorithms);
        JComboBox<String> vehicleComboBox = new JComboBox<>(vehicles);
        dropdownPanel.add(new JLabel("Select Algorithm:"));
        dropdownPanel.add(algorithmComboBox);
        dropdownPanel.add(new JLabel("Select Vehicle:"));
        dropdownPanel.add(vehicleComboBox);
        leftPanel.add(dropdownPanel, BorderLayout.SOUTH);

        // Right box for route visualization
        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.CYAN);
                g.fillArc(50, 50, 300, 200, 0, 180);
            }
        };
        rightPanel.setBorder(BorderFactory.createTitledBorder("Route Visualization"));

        // Add panels to content panel
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        // Add top panel and content panel to the wrapper panel
        wrapperPanel.add(topPanel, BorderLayout.NORTH);
        wrapperPanel.add(contentPanel, BorderLayout.CENTER);

        // Add wrapper panel to the frame
        frame.add(wrapperPanel);

        // Action listener for the import button
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importDeliveryList();
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }
    private static void importDeliveryList() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter); // Set filter to only accept CSV files
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                List<Object[]> rows = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    rows.add(new Object[]{data[0], data[1]});
                }
                // Clear existing rows and add the new rows
                tableModel.setRowCount(0);
                for (Object[] row : rows) {
                    tableModel.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            }
        }
    }

}
