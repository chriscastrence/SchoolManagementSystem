import java.awt.*;
import java.util.*;
import javax.swing.*;

public class PrerequisiteVisualizer extends JFrame {
    private Map<String, Node> nodes = new HashMap<>();
    private java.util.List<Edge> edges = new ArrayList<>();
    private java.util.List<Edge> highlightedEdges = new ArrayList<>();
    private Node targetNode = null;
    private static final int NODE_RADIUS = 40;
    private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 1200;
    private JTextArea prerequisitesText;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrerequisiteVisualizer());
    }

    // Creates the prerequisite visualizer frame
    public PrerequisiteVisualizer() {
        setTitle("Course Prerequisite Visualizer");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create logo panel for top left
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ImageIcon logoIcon = new ImageIcon("logo.jpg");
        Image scaledImage = logoIcon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoPanel.add(logoLabel);
        add(logoPanel, BorderLayout.WEST);

        // Create search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Create search components
        JLabel searchLabel = new JLabel("Enter Course (e.g., CS 450):");
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Go");
        JButton startDFSButton = new JButton("Start DFS");
        
        // Add components directly to search panel
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(startDFSButton);
        
        // Create prerequisites text area with larger font
        prerequisitesText = new JTextArea(10, 30);
        prerequisitesText.setEditable(false);
        prerequisitesText.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(prerequisitesText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create right panel for search and text
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Remove the logo from rightPanel (if it exists)
        rightPanel.remove(logoLabel);
        
        rightPanel.setPreferredSize(new Dimension(400, WINDOW_HEIGHT));
        
        // Add visualization panel to center
        add(new VisualizationPanel(), BorderLayout.CENTER);
        
        // Add right panel
        add(rightPanel, BorderLayout.EAST);

        // Create nodes and edges
        createNodes();
        createEdges();

        // Add search button actions
        searchButton.addActionListener(e -> {
            String course = searchField.getText().trim().toUpperCase();
            findPrerequisites(course);
        });

        startDFSButton.addActionListener(e -> {
            String course = searchField.getText().trim().toUpperCase();
            if (nodes.containsKey(course)) {
                highlightedEdges.clear();
                targetNode = nodes.get(course);
                Set<String> visited = new HashSet<>();
                prerequisitesText.setText("Starting DFS from " + course + "\n");
                startDFSVisualization(course, visited);
            } else {
                prerequisitesText.setText("Course not found: " + course);
            }
            repaint();
        });

        setVisible(true);
    }

    // Finds the prerequisites for a given course
    private void findPrerequisites(String courseName) {
        // Reset previous highlights
        highlightedEdges.clear();
        targetNode = nodes.get(courseName);
        
        if (targetNode == null) {
            prerequisitesText.setText("Course not found: " + courseName);
            repaint();
            return;
        }

        // Find all prerequisites using DFS
        Set<String> prerequisites = new LinkedHashSet<>();
        findAllPrerequisites(courseName, prerequisites, new HashSet<>());

        // Update text area
        StringBuilder sb = new StringBuilder();
        sb.append("Prerequisites for ").append(courseName).append(":\n\n");
        
        if (prerequisites.isEmpty()) {
            sb.append("No prerequisites required.");
        } else {
            sb.append("You need to take these courses in this order:\n");
            prerequisites.forEach(prereq -> sb.append("- ").append(prereq).append("\n"));
            sb.append("→ ").append(courseName);
        }
        
        prerequisitesText.setText(sb.toString());
        
        // Highlight the path
        highlightPrerequisitePaths(courseName, prerequisites);
        repaint();
    }

    // Creates the nodes for the prerequisite visualizer
    private void createNodes() {
        addNode("CS 150", 150, 150);
        addNode("CS 150L", 150, 250);
        addNode("CS 160", 300, 150);
        addNode("CS 160L", 300, 250);
        addNode("CS 210", 450, 150);
        addNode("CS 240", 450, 250);
        addNode("CS 250", 600, 250);
        addNode("MATH 141", 150, 400);
        addNode("MATH 150", 300, 400);
        addNode("MATH 151", 450, 400);
        addNode("MATH 245", 600, 400);
        addNode("MATH 254", 750, 400);
        addNode("PHYS 195", 300, 550);
        addNode("PHYS 195L", 300, 650);
        addNode("PHYS 196", 450, 550);
        addNode("PHYS 196L", 450, 650);
        addNode("STAT 250", 600, 550);
        addNode("STAT 550", 750, 550);
        addNode("CS 370", 600, 150);
        addNode("CS 450", 750, 150);
        addNode("CS 460", 750, 250);
        addNode("CS 480", 900, 150);
        addNode("CS 420", 900, 250);
    }

    private void createEdges() {
        // Add edges based on prerequisites
        addEdge("CS 150", "CS 160");
        addEdge("CS 150L", "CS 160");
        addEdge("CS 150", "CS 160L");
        addEdge("CS 150L", "CS 160L");
        addEdge("CS 160", "CS 210");
        addEdge("CS 160L", "CS 210");
        addEdge("CS 160", "CS 240");
        addEdge("CS 160L", "CS 240");
        addEdge("CS 240", "CS 250");
        addEdge("MATH 141", "MATH 150");
        addEdge("MATH 150", "MATH 151");
        addEdge("MATH 150", "MATH 245");
        addEdge("MATH 151", "MATH 254");
        addEdge("MATH 150", "PHYS 195");
        addEdge("MATH 150", "PHYS 195L");
        addEdge("PHYS 195", "PHYS 196");
        addEdge("PHYS 195L", "PHYS 196L");
        addEdge("PHYS 195L", "PHYS 196");
        addEdge("MATH 151", "PHYS 196");
        addEdge("CS 240", "CS 370");
        addEdge("CS 210", "CS 450");
        addEdge("MATH 254", "CS 450");
        addEdge("STAT 250", "CS 450");
        addEdge("CS 210", "CS 460");
        addEdge("MATH 245", "CS 460");
        addEdge("CS 210", "CS 480");
        addEdge("CS 370", "CS 480");
        addEdge("CS 210", "CS 420");
    }

    private void addNode(String name, int x, int y) {
        nodes.put(name, new Node(name, x, y));
    }

    private void addEdge(String from, String to) {
        edges.add(new Edge(nodes.get(from), nodes.get(to)));
    }

    private class Node {
        String name;
        double x, y;

        Node(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    private class Edge {
        Node from, to;

        Edge(Node from, Node to) {
            this.from = from;
            this.to = to;
        }
    }

    // Creates the visualization panel
    private class VisualizationPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw regular edges
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.LIGHT_GRAY);
            for (Edge edge : edges) {
                if (!highlightedEdges.contains(edge)) {
                    drawArrow(g2d, edge.from.x, edge.from.y, edge.to.x, edge.to.y);
                }
            }

            // Draw highlighted edges
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(Color.BLUE);
            for (Edge edge : highlightedEdges) {
                drawArrow(g2d, edge.from.x, edge.from.y, edge.to.x, edge.to.y);
            }

            // Draw nodes
            for (Node node : nodes.values()) {
                if (node == targetNode) {
                    g2d.setColor(Color.YELLOW);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillOval((int)node.x - NODE_RADIUS, (int)node.y - NODE_RADIUS, 
                            2 * NODE_RADIUS, 2 * NODE_RADIUS);
                g2d.setColor(Color.BLACK);
                g2d.drawOval((int)node.x - NODE_RADIUS, (int)node.y - NODE_RADIUS, 
                            2 * NODE_RADIUS, 2 * NODE_RADIUS);
                
                // Draw node labels
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(node.name);
                g2d.drawString(node.name, 
                             (int)(node.x - textWidth/2),
                             (int)(node.y + fm.getAscent()/2));
            }
        }

        // Draws the arrow for the prerequisite visualizer
        private void drawArrow(Graphics2D g2d, double x1, double y1, double x2, double y2) {
            double dx = x2 - x1;
            double dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            
            // Adjust start and end points to account for node radius
            x1 = x1 + NODE_RADIUS * Math.cos(angle);
            y1 = y1 + NODE_RADIUS * Math.sin(angle);
            x2 = x2 - NODE_RADIUS * Math.cos(angle);
            y2 = y2 - NODE_RADIUS * Math.sin(angle);
            
            // Draw line
            g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
            
            // Draw arrowhead
            int arrowSize = 10;
            double arrowAngle = Math.PI/6;
            
            int[] xPoints = new int[3];
            int[] yPoints = new int[3];
            
            xPoints[0] = (int)x2;
            yPoints[0] = (int)y2;
            
            xPoints[1] = (int)(x2 - arrowSize * Math.cos(angle - arrowAngle));
            yPoints[1] = (int)(y2 - arrowSize * Math.sin(angle - arrowAngle));
            
            xPoints[2] = (int)(x2 - arrowSize * Math.cos(angle + arrowAngle));
            yPoints[2] = (int)(y2 - arrowSize * Math.sin(angle + arrowAngle));
            
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }

    // Starts the DFS visualization
    private void startDFSVisualization(String course, Set<String> visited) {
        visited.add(course);
        prerequisitesText.append("Visiting: " + course + "\n");
        
        for (Edge edge : edges) {
            if (edge.to.name.equals(course) && !visited.contains(edge.from.name)) {
                highlightedEdges.add(edge);
                repaint();
                startDFSVisualization(edge.from.name, visited);
            }
        }
    }

    // Finds all prerequisites for a given course
    private void findAllPrerequisites(String course, Set<String> prerequisites, Set<String> visited) {
        visited.add(course);
        
        for (Edge edge : edges) {
            if (edge.to.name.equals(course) && !visited.contains(edge.from.name)) {
                prerequisites.add(edge.from.name);
                findAllPrerequisites(edge.from.name, prerequisites, visited);
            }
        }
    }

    // Highlights the prerequisite paths
    private void highlightPrerequisitePaths(String course, Set<String> prerequisites) {
        for (Edge edge : edges) {
            if (prerequisites.contains(edge.from.name) && 
                (prerequisites.contains(edge.to.name) || edge.to.name.equals(course))) {
                highlightedEdges.add(edge);
            }
        }
    }
}
