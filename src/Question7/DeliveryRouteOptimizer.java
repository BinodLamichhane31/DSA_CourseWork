package Question7;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class DeliveryRouteOptimizer {

    private static DefaultTableModel tableModel;
    private static Map<String, Delivery> deliveries = new LinkedHashMap<>();
    private static List<String> route = new ArrayList<>();
    private static Map<String, Integer> vehicleMaxWeight = Map.of(
            "Bike", 50,  // Max weight in kg
            "Car", 150,
            "Truck", 500
    );

    public static void main(String[] args) {
        JFrame frame = new JFrame("Delivery Route Optimizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);
        frame.setLayout(new BorderLayout());

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.LIGHT_GRAY);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Delivery List"));

        String[] columnNames = {"Address", "Priority", "Weight (kg)"};
        tableModel = new DefaultTableModel(new Object[][]{}, columnNames);
        JTable deliveryTable = new JTable(tableModel);
        JScrollPane deliveryListScrollPane = new JScrollPane(deliveryTable);
        leftPanel.add(deliveryListScrollPane, BorderLayout.CENTER);

        JPanel dropdownPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        String[] vehicles = {"Bike", "Car", "Truck"};
        JComboBox<String> vehicleComboBox = new JComboBox<>(vehicles);
        dropdownPanel.add(new JLabel("Select Vehicle:"));
        dropdownPanel.add(vehicleComboBox);
        leftPanel.add(dropdownPanel, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(leftPanel, gbc);

        JPanel rightPanel = new RouteVisualizationPanel();
        rightPanel.setBorder(BorderFactory.createTitledBorder("Route Visualization"));

        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(rightPanel, gbc);

        wrapperPanel.add(topPanel, BorderLayout.NORTH);
        wrapperPanel.add(contentPanel, BorderLayout.CENTER);

        frame.add(wrapperPanel);

        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importDeliveryList();
            }
        });

        optimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedVehicle = (String) vehicleComboBox.getSelectedItem();
                if (selectedVehicle != null) {
                    optimizeRoute(selectedVehicle);
                    rightPanel.repaint();
                }
            }
        });

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private static void importDeliveryList() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                deliveries.clear();
                tableModel.setRowCount(0);
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    String address = data[0];
                    int priority = Integer.parseInt(data[1]);
                    int weight = Integer.parseInt(data[2]);
                    Delivery delivery = new Delivery(address, priority, weight);
                    deliveries.put(address, delivery);
                    tableModel.addRow(new Object[]{address, priority, weight});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            }
        }
    }

    private static void optimizeRoute(String vehicle) {
        int vehicleMaxWeightLimit = vehicleMaxWeight.get(vehicle);

        List<Delivery> deliveryList = new ArrayList<>(deliveries.values());

        Vehicle selectedVehicle = new Vehicle(vehicleMaxWeightLimit);
        List<Integer> optimizedRouteIndices = NearestNeighbor.solve(deliveryList, selectedVehicle);

        route.clear();
        int currentWeight = 0;

        for (int index : optimizedRouteIndices) {
            Delivery delivery = deliveryList.get(index);
            if (currentWeight + delivery.weight <= vehicleMaxWeightLimit) {
                route.add(delivery.address);
                currentWeight += delivery.weight;
            }
        }

        if (route.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No valid route found for the selected vehicle.");
            return;
        }

        double totalDistance = calculateTotalDistance(route);

        RouteVisualizationPanel.updateRouteDetails(route, totalDistance, currentWeight);

        System.out.println("Optimized Route for Vehicle " + vehicle + ":");
        System.out.println("Route: " + String.join(" → ", route));
        System.out.println("Total Distance: " + totalDistance + " km");
        System.out.println("Total Weight: " + currentWeight + " kg");
    }

    private static double calculateTotalDistance(List<String> route) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += 10;
        }
        return totalDistance;
    }

    private static class RouteVisualizationPanel extends JPanel {

        private static JTextArea routeDetailsTextArea = new JTextArea();

        public RouteVisualizationPanel() {
            setLayout(new BorderLayout());
            routeDetailsTextArea.setEditable(false);
            routeDetailsTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
            add(new JScrollPane(routeDetailsTextArea), BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawRoute(g);
        }

        private void drawRoute(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            int dotSize = 10;

            Map<String, Point> coordinates = generatePlaceholderCoordinates();

            if (!route.isEmpty()) {
                String startAddress = route.get(0);
                Point startPoint = coordinates.get(startAddress);
                g2d.setColor(Color.BLUE);
                g2d.fillOval(startPoint.x, startPoint.y, dotSize, dotSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString(startAddress, startPoint.x + dotSize / 2, startPoint.y - 5);
            }

            for (int i = 0; i < route.size(); i++) {
                String address = route.get(i);
                Point point = coordinates.get(address);

                int priority = deliveries.get(address).priority;
                if (priority == 1) {
                    g2d.setColor(Color.RED);
                } else if (priority == 2) {
                    g2d.setColor(Color.ORANGE);
                } else {
                    g2d.setColor(Color.GREEN);
                }

                g2d.fillOval(point.x, point.y, dotSize, dotSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString(address, point.x + dotSize / 2, point.y - 5);

                if (i > 0) {
                    String previousAddress = route.get(i - 1);
                    Point previousPoint = coordinates.get(previousAddress);
                    g2d.setColor(Color.GRAY);
                    g2d.drawLine(previousPoint.x + dotSize / 2, previousPoint.y + dotSize / 2,
                            point.x + dotSize / 2, point.y + dotSize / 2);
                }
            }
        }

        private Map<String, Point> generatePlaceholderCoordinates() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int margin = 50;
            int availableWidth = panelWidth - 2 * margin;
            int availableHeight = panelHeight - 2 * margin;

            Map<String, Point> coordinates = new LinkedHashMap<>();
            Random rand = new Random();
            for (String address : deliveries.keySet()) {
                int x = margin + rand.nextInt(availableWidth);
                int y = margin + rand.nextInt(availableHeight);
                coordinates.put(address, new Point(x, y));
            }
            return coordinates;
        }

        public static void updateRouteDetails(List<String> route, double totalDistance, int currentWeight) {
            StringBuilder sb = new StringBuilder();
            sb.append("Optimized Route:\n");
            sb.append(String.join(" → ", route)).append("\n\n");
            sb.append("Total Distance: ").append(totalDistance).append(" km\n");
            sb.append("Total Weight: ").append(currentWeight).append(" kg\n");
            routeDetailsTextArea.setText(sb.toString());
        }
    }

    private static class Delivery {
        String address;
        int priority;
        int weight;

        public Delivery(String address, int priority, int weight) {
            this.address = address;
            this.priority = priority;
            this.weight = weight;
        }
    }

    private static class Vehicle {
        int maxWeight;

        public Vehicle(int maxWeight) {
            this.maxWeight = maxWeight;
        }
    }

    private static class NearestNeighbor {

        public static List<Integer> solve(List<Delivery> deliveries, Vehicle vehicle) {
            int n = deliveries.size();
            boolean[] visited = new boolean[n];
            List<Integer> route = new ArrayList<>();
            int currentWeight = 0;

            int current = 0;
            route.add(current);
            visited[current] = true;

            for (int i = 1; i < n; i++) {
                int nearest = -1;
                for (int j = 1; j < n; j++) {
                    if (!visited[j] && currentWeight + deliveries.get(j).weight <= vehicle.maxWeight) {
                        if (nearest == -1 || deliveries.get(j).priority < deliveries.get(nearest).priority) {
                            nearest = j;
                        }
                    }
                }
                if (nearest == -1) break;
                route.add(nearest);
                visited[nearest] = true;
                currentWeight += deliveries.get(nearest).weight;
            }

            return route;
        }
    }
}
