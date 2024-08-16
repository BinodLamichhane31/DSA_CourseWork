import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

public class DeliveryRouteOptimizer {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Delivery Route Optimizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.LIGHT_GRAY);

                GeneralPath path = new GeneralPath();
                int w = getWidth();
                int h = getHeight();
                int arcSize = 30;

                path.moveTo(0, 0);
                path.lineTo(w, 0);
                path.lineTo(w, h - arcSize);
                path.append(new Arc2D.Float(w - arcSize, h - arcSize, arcSize, arcSize, 0, -90, Arc2D.OPEN), true);
                path.lineTo(arcSize, h);
                path.append(new Arc2D.Float(0, h - arcSize, arcSize, arcSize, -90, -90, Arc2D.OPEN), true);
                path.closePath();

                g2d.fill(path);
            }
        };
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adds a 20px gap at the top


        JLabel titleLabel = new JLabel("Delivery Route Optimizer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton importButton = new JButton("Import Delivery List");
        JButton optimizeButton = new JButton("Optimize Route");
        buttonPanel.setOpaque(false);
        buttonPanel.add(importButton);
        buttonPanel.add(optimizeButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Delivery List"));

        String[] columnNames = {"Address", "Priority"};
        Object[][] data = {
                {"123 Main St, Anytown USA", "High"},
                {"456 Oak Rd, Somewhere City", "Medium"},
                {"789 Elm Ave, Elsewhere Town", "Low"}
        };
        JTable deliveryTable = new JTable(data, columnNames);
        JScrollPane deliveryListScrollPane = new JScrollPane(deliveryTable);
        leftPanel.add(deliveryListScrollPane, BorderLayout.CENTER);

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

        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.CYAN);
                g.fillArc(50, 50, 300, 200, 0, 180);
            }
        };
        rightPanel.setBorder(BorderFactory.createTitledBorder("Route Visualization"));

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
