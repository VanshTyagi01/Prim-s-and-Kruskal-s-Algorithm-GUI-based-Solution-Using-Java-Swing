import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import java.util.*;
import java.util.List;

public class GraphGUI extends JFrame {
	private Map<String, JTextField> edgeWeightFields;
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private JPanel inputPanel;
	private JPanel outputPanel;
	private Map<String, Integer> weights;

	public GraphGUI() {
		edgeWeightFields = new HashMap<>();
		weights = new HashMap<>();

		setTitle("Graph Algorithm Visualizer");
		setSize(800, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Main Panel with CardLayout to switch between input and output pages
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);

		// Initialize the input and output panels
		createInputPanel();
		createOutputPanel();

		// Add input and output panels to main panel
		mainPanel.add(inputPanel, "Input");
		mainPanel.add(outputPanel, "Output");

		add(mainPanel);
		setVisible(true);
	}

	private void createInputPanel() {
		inputPanel = new JPanel(new BorderLayout(10, 10));
		inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Left Panel - Weights List with JTextFields for each edge
		JPanel weightsPanel = new JPanel();
		weightsPanel.setLayout(new BoxLayout(weightsPanel, BoxLayout.Y_AXIS));
		weightsPanel.setBorder(BorderFactory.createTitledBorder("Enter Edge Weights"));

		String[] edges = {
			"a - b", "a - h", "b - c", "b - h",
			"h - i", "h - g", "c - i", "i - g",
			"c - d", "c - f", "g - f", "d - e",
			"d - f", "f - e"
		};

		for (String edge : edges) {
			JPanel edgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel edgeLabel = new JLabel(edge + " == ");
			JTextField weightField = new JTextField(3); // Empty text field for weight input
			edgePanel.add(edgeLabel);
			edgePanel.add(weightField);
			weightsPanel.add(edgePanel);
			edgeWeightFields.put(edge, weightField); // Store reference to each field
		}

		// Center Panel - Displaying Graph Structure (sample nodes and edges)
		JPanel graphPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawGraph(g); // Reuse the graph-drawing logic
			}
		};
		graphPanel.setPreferredSize(new Dimension(500, 300));
		graphPanel.setBorder(BorderFactory.createTitledBorder("Graph"));

		// Right Panel - Algorithm Selection and Controls
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

		JPanel algorithmPanel = new JPanel();
		algorithmPanel.setBorder(BorderFactory.createTitledBorder("Algorithms"));
		JRadioButton kruskalButton = new JRadioButton("Kruskal's");
		JRadioButton primButton = new JRadioButton("Prim's");
		ButtonGroup algorithmGroup = new ButtonGroup();
		algorithmGroup.add(kruskalButton);
		algorithmGroup.add(primButton);
		algorithmPanel.add(kruskalButton);
		algorithmPanel.add(primButton);

		// Button Panel
		JPanel buttonPanel = new JPanel();
		JButton startButton = new JButton("START");
		JButton refreshButton = new JButton("REFRESH");
		JButton saveButton = new JButton("SAVE"); // New SAVE button
		buttonPanel.add(startButton);
		buttonPanel.add(refreshButton);
		buttonPanel.add(saveButton); // Add SAVE button to the panel

		controlPanel.add(algorithmPanel);
		controlPanel.add(Box.createVerticalStrut(20)); // Spacer
		controlPanel.add(buttonPanel);

		// Add action listeners for buttons
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (collectWeights()) {
					String selectedAlgorithm = getSelectedAlgorithm(kruskalButton, primButton);
					if (selectedAlgorithm != null) {
						String result = runAlgorithm(selectedAlgorithm);
						showOutput(result);
					}
				}
			}
		});

		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshWeights();
				inputPanel.repaint(); // Refresh input panel to clear weights
			}
		});

		saveButton.addActionListener(new ActionListener() { // Action for SAVE button
			@Override
			public void actionPerformed(ActionEvent e) {
				if (collectWeights()) {
					JOptionPane.showMessageDialog(inputPanel, "Weights saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
					inputPanel.repaint(); // Refresh input panel to show weights
				}
			}
		});

		inputPanel.add(weightsPanel, BorderLayout.WEST);
		inputPanel.add(graphPanel, BorderLayout.CENTER);
		inputPanel.add(controlPanel, BorderLayout.EAST);
	}

	private void createOutputPanel() {
		outputPanel = new JPanel(new BorderLayout());

		// Create a new panel for drawing the graph
		JPanel graphPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawGraph(g);  // Call drawGraph to draw the graph
			}
		};
		graphPanel.setPreferredSize(new Dimension(500, 300)); // Set preferred size for the graph panel
		graphPanel.setBorder(BorderFactory.createTitledBorder("Graph"));

		// Add result output area
		JTextArea outputArea = new JTextArea();
		outputArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(outputArea);

		// Add components to output panel
		outputPanel.add(scrollPane, BorderLayout.CENTER);
		outputPanel.add(graphPanel, BorderLayout.SOUTH); // Add the graph panel to the output panel

		// Add "Go Back" button
		JPanel bottomPanel = new JPanel();
		JButton goBackButton = new JButton("Go Back");
		goBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(mainPanel, "Input"); // Switch back to input page
			}
		});
		bottomPanel.add(goBackButton);

		outputPanel.add(bottomPanel, BorderLayout.SOUTH);
	}

	// Shared graph-drawing logic
	private void drawGraph(Graphics g) {

		// Draw nodes
		g.setColor(Color.ORANGE);
		g.fillOval(50, 100, 30, 30); // Node A
		g.fillOval(100, 50, 30, 30); // Node B
		g.fillOval(200, 50, 30, 30); // Node C
		g.fillOval(300, 50, 30, 30); // Node D
		g.fillOval(350, 100, 30, 30); // Node E
		g.fillOval(300, 150, 30, 30); // Node F
		g.fillOval(200, 150, 30, 30); // Node G
		g.fillOval(100, 150, 30, 30); // Node H
		g.fillOval(150, 100, 30, 30); // Node I

		// Node labels
		g.setColor(Color.BLACK);
		g.drawString("A", 60, 120);
		g.drawString("B", 110, 70);
		g.drawString("C", 210, 70);
		g.drawString("D", 310, 70);
		g.drawString("E", 360, 120);
		g.drawString("F", 310, 170);
		g.drawString("G", 210, 170);
		g.drawString("H", 110, 170);
		g.drawString("I", 165, 120);

		// Draw edges and weights
		drawEdgeWithWeight(g, 75, 105, 105, 75, "a - b"); // A-B
		drawEdgeWithWeight(g, 75, 125, 105, 155, "a - h"); // A-H
		drawEdgeWithWeight(g, 115, 80, 115, 150, "b - h"); // B-H
		drawEdgeWithWeight(g, 130, 65, 200, 65, "b - c"); // B-C
		drawEdgeWithWeight(g, 125, 155, 155, 125, "h - i"); // H-I
		drawEdgeWithWeight(g, 130, 165, 200, 165, "h - g"); // H-G
		drawEdgeWithWeight(g, 175, 125, 205, 155, "i - g"); // I-G
		drawEdgeWithWeight(g, 230, 65, 300, 65, "c - d"); // C-D
		drawEdgeWithWeight(g, 225, 75, 305, 155, "c - f"); // C-F
		drawEdgeWithWeight(g, 230, 165, 300, 165, "g - f"); // G-F
		drawEdgeWithWeight(g, 325, 75, 355, 105, "d - e"); // D-E
		drawEdgeWithWeight(g, 315, 150, 315, 80, "d - f"); // F-D
		drawEdgeWithWeight(g, 325, 155, 355, 125, "f - e"); // F-E
		drawEdgeWithWeight(g, 175, 105, 205, 75, "c - i"); // I-C
	}

	private void drawEdgeWithWeight(Graphics g, int x1, int y1, int x2, int y2, String edge) {
		g.drawLine(x1, y1, x2, y2); // Draw edge
		int midX = (x1 + x2) / 2;
		int midY = (y1 + y2) / 2;
		g.setColor(Color.BLACK);
		g.drawString(weights.getOrDefault(edge, 0).toString(), midX, midY); // Draw weight
	}

	private boolean collectWeights() {
		weights.clear();
		try {
			for (String edge : edgeWeightFields.keySet()) {
				String weightText = edgeWeightFields.get(edge).getText();
				int weight = weightText.isEmpty() ? 0 : Integer.parseInt(weightText);
				weights.put(edge, weight);
			}
			return true;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter valid integer weights.", "Input Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private void refreshWeights() {
		for (JTextField field : edgeWeightFields.values()) {
			field.setText("");
		}
		weights.clear();
	}

	// Get selected algorithm
	private String getSelectedAlgorithm(JRadioButton kruskalButton, JRadioButton primButton) {
		if (kruskalButton.isSelected()) {
			return "Kruskal";
		} else if (primButton.isSelected()) {
			return "Prim";
		}
		return null;
	}

    // Run selected algorithm
	private String runAlgorithm(String algorithm) {
		if ("Prim".equals(algorithm)) {
			return primAlgorithm();
		} else if ("Kruskal".equals(algorithm)) {
			return kruskalAlgorithm();
		}
		return "No algorithm selected";
	}

	// Kruskal's Algorithm Implementation
	private String kruskalAlgorithm() {
		// Step 1: Create a list of all edges
		List<Edge> allEdges = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : weights.entrySet()) {
			String[] nodes = entry.getKey().split(" - ");
			String source = nodes[0];
			String target = nodes[1];
			int weight = entry.getValue();
			allEdges.add(new Edge(source, target, weight));
		}

		// Step 2: Sort edges by weight
		Collections.sort(allEdges, Comparator.comparingInt(e -> e.weight));

		// Step 3: Initialize Disjoint Set (Union-Find) data structure
		Map<String, String> parent = new HashMap<>();
		for (Edge edge : allEdges) {
			parent.put(edge.source, edge.source);
			parent.put(edge.target, edge.target);
		}

		// Step 4: Process edges and form MST
		List<Edge> mstEdges = new ArrayList<>();
		for (Edge edge : allEdges) {
			String rootSource = find(parent, edge.source);
			String rootTarget = find(parent, edge.target);
			if (!rootSource.equals(rootTarget)) {
				mstEdges.add(edge);
				union(parent, rootSource, rootTarget);
			}
		}

		// Step 5: Format the result as a string
		StringBuilder result = new StringBuilder("Kruskal's Algorithm MST:\n");
		for (Edge edge : mstEdges) {
			result.append(edge.source).append(" - ").append(edge.target)
			.append(" (").append(edge.weight).append(")\n");
		}
		return result.toString();
	}

    // Find operation for Disjoint Set (Union-Find)
	private String find(Map<String, String> parent, String node) {
		if (!parent.get(node).equals(node)) {
			parent.put(node, find(parent, parent.get(node))); // Path compression
		}
		return parent.get(node);
	}

    // Union operation for Disjoint Set (Union-Find)
	private void union(Map<String, String> parent, String rootSource, String rootTarget) {
		parent.put(rootSource, rootTarget); // Union step
	}
	// Prim's Algorithm Implementation
	private String primAlgorithm() {
		// Step 1: Create adjacency list
		Map<String, List<Edge>> adjList = createAdjacencyList();

		// Step 2: Initialize data structures for Prim's Algorithm
		Set<String> visited = new HashSet<>();
		PriorityQueue<Edge> minHeap = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

		// Step 3: Start with node 'a' (you can choose any node)
		visited.add("a");
		addEdges("a", adjList, minHeap, visited);

		List<Edge> mstEdges = new ArrayList<>();

		// Step 4: Run Prim's algorithm
		while (!minHeap.isEmpty()) {
			Edge edge = minHeap.poll();
			if (!visited.contains(edge.target)) {
				mstEdges.add(edge);
				visited.add(edge.target);
				addEdges(edge.target, adjList, minHeap, visited);
			}
		}

		// Step 5: Format the result as a string
		StringBuilder result = new StringBuilder("Prim's Algorithm MST:\n");
		for (Edge edge : mstEdges) {
			result.append(edge.source).append(" - ").append(edge.target)
			.append(" (").append(edge.weight).append(")\n");
		}
		return result.toString();
	}

	// Helper to add edges to the min heap from a node
	private void addEdges(String node, Map<String, List<Edge>> adjList,
	                      PriorityQueue<Edge> minHeap, Set<String> visited) {
		for (Edge edge : adjList.getOrDefault(node, new ArrayList<>())) {
			if (!visited.contains(edge.target)) {
				minHeap.offer(edge);
			}
		}
	}

	// Create adjacency list representation of the graph
	private Map<String, List<Edge>> createAdjacencyList() {
		Map<String, List<Edge>> adjList = new HashMap<>();

		// Fill the adjacency list based on edge names and weights
		for (Map.Entry<String, Integer> entry : weights.entrySet()) {
			String[] nodes = entry.getKey().split(" - ");
			String source = nodes[0];
			String target = nodes[1];
			int weight = entry.getValue();

			adjList.computeIfAbsent(source, k -> new ArrayList<>()).add(new Edge(source, target, weight));
			adjList.computeIfAbsent(target, k -> new ArrayList<>()).add(new Edge(target, source, weight)); // Undirected graph
		}

		return adjList;
	}

	// Edge class to represent an edge with a weight
	private static class Edge {
		String source, target;
		int weight;

		Edge(String source, String target, int weight) {
			this.source = source;
			this.target = target;
			this.weight = weight;
		}
	}

	// Display output
	private void showOutput(String result) {
		outputPanel.removeAll();  // Clear the output panel
		// Collect the edges that are part of the algorithm's output
		Set<String> algorithmOutputEdges = new HashSet<>();
		// Assuming you can parse the result or get it from your algorithm output
		if (result.contains("Kruskal's")) {
			if(result.contains("a - b")   || result.contains("b - a")) {
				algorithmOutputEdges.add("a-b");
			}
			if(result.contains("a - h")  || result.contains("h - a")) {
				algorithmOutputEdges.add("a-h");
			}
			if(result.contains("b - h") || result.contains("h - b")) {
				algorithmOutputEdges.add("b-h");
			}
			if(result.contains("b - c") || result.contains("c - b")) {
				algorithmOutputEdges.add("b-c");
			}
			if(result.contains("h - i") || result.contains("i - h")) {
				algorithmOutputEdges.add("h-i");
			}
			if(result.contains("h - g") || result.contains("g - h")) {
				algorithmOutputEdges.add("h-g");
			}
			if(result.contains("i - g") || result.contains("g - i")) {
				algorithmOutputEdges.add("i-g");
			}
			if(result.contains("c - d") || result.contains("d - c")) {
				algorithmOutputEdges.add("c-d");
			}
			if(result.contains("f - c") || result.contains("c - f")) {
				algorithmOutputEdges.add("c-f");
			}
			if(result.contains("g - f") || result.contains("f - g")) {
				algorithmOutputEdges.add("g-f");
			}
			if(result.contains("d - e") || result.contains("e - d")) {
				algorithmOutputEdges.add("d-e");
			}
			if(result.contains("d - f") || result.contains("f - d")) {
				algorithmOutputEdges.add("d-f");
			}
			if(result.contains("f - e") || result.contains("e - f")) {
				algorithmOutputEdges.add("f-e");
			}
			if(result.contains("c - i") || result.contains("i - c")) {
				algorithmOutputEdges.add("c-i");
			}


		} else if (result.contains("Prim's")) {
			if(result.contains("a - b") || result.contains("b - a")) {
				algorithmOutputEdges.add("a-b");
			}
			if(result.contains("a - h")   || result.contains("h - a")) {
				algorithmOutputEdges.add("a-h");
			}
			if(result.contains("b - h")  || result.contains("h - b")) {
				algorithmOutputEdges.add("b-h");
			}
			if(result.contains("b - c") || result.contains("c - b")) {
				algorithmOutputEdges.add("b-c");
			}
			if(result.contains("h - i") || result.contains("i - h")) {
				algorithmOutputEdges.add("h-i");
			}
			if(result.contains("h - g") || result.contains("g - h")) {
				algorithmOutputEdges.add("h-g");
			}
			if(result.contains("i - g") || result.contains("g - i")) {
				algorithmOutputEdges.add("i-g");
			}
			if(result.contains("c - d") || result.contains("d - c")) {
				algorithmOutputEdges.add("c-d");
			}
			if(result.contains("f - c") || result.contains("c - f")) {
				algorithmOutputEdges.add("c-f");
			}
			if(result.contains("g - f") || result.contains("f - g")) {
				algorithmOutputEdges.add("g-f");
			}
			if(result.contains("d - e") || result.contains("e - d")) {
				algorithmOutputEdges.add("d-e");
			}
			if(result.contains("d - f") || result.contains("f - d")) {
				algorithmOutputEdges.add("d-f");
			}
			if(result.contains("f - e") || result.contains("e - f")) {
				algorithmOutputEdges.add("f-e");
			}
			if(result.contains("c - i") || result.contains("i - c")) {
				algorithmOutputEdges.add("c-i");
			}
		}

		JPanel outputContainer = new JPanel(new BorderLayout());
		JPanel graphPanel = createGraphPanel(algorithmOutputEdges);  // Pass the edges here
		outputContainer.add(graphPanel, BorderLayout.WEST);
		JPanel resultPanel = createResultPanel(result);
		outputContainer.add(resultPanel, BorderLayout.CENTER);
		outputPanel.add(outputContainer, BorderLayout.CENTER);

		// Add the "Go Back" button at the bottom
		JPanel bottomPanel = new JPanel();
		JButton goBackButton = new JButton("Go Back");
		goBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(mainPanel, "Input"); // Switch back to input page
			}
		});
		bottomPanel.add(goBackButton);
		outputPanel.add(bottomPanel, BorderLayout.SOUTH);

		// Refresh the output panel
		outputPanel.revalidate();
		outputPanel.repaint();

		// Switch to the output panel
		cardLayout.show(mainPanel, "Output");
	}

    // Helper method to create the graph panel
	private JPanel createGraphPanel(Set<String> algorithmOutputEdges) {
		JPanel graphPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				// Draw nodes
				g.setColor(Color.ORANGE);
				g.fillOval(50, 100, 30, 30); // Node A
				g.fillOval(100, 50, 30, 30); // Node B
				g.fillOval(200, 50, 30, 30); // Node C
				g.fillOval(300, 50, 30, 30); // Node D
				g.fillOval(350, 100, 30, 30); // Node E
				g.fillOval(300, 150, 30, 30); // Node F
				g.fillOval(200, 150, 30, 30); // Node G
				g.fillOval(100, 150, 30, 30); // Node H
				g.fillOval(150, 100, 30, 30); // Node I

				// Node labels
				g.setColor(Color.BLACK);
				g.drawString("A", 60, 120);
				g.drawString("B", 110, 70);
				g.drawString("C", 210, 70);
				g.drawString("D", 310, 70);
				g.drawString("E", 360, 120);
				g.drawString("F", 310, 170);
				g.drawString("G", 210, 170);
				g.drawString("H", 110, 170);
				g.drawString("I", 165, 120);

				// Conditionally draw edges and weights
				if (algorithmOutputEdges.contains("a-b")) drawEdgeWithWeight(g, 75, 105, 105, 75, "a - b"); // A-B
				if (algorithmOutputEdges.contains("a-h")) drawEdgeWithWeight(g, 75, 125, 105, 155, "a - h"); // A-H
				if (algorithmOutputEdges.contains("b-h")) drawEdgeWithWeight(g, 115, 80, 115, 150, "b - h"); // B-H
				if (algorithmOutputEdges.contains("b-c")) drawEdgeWithWeight(g, 130, 65, 200, 65, "b - c"); // B-C
				if (algorithmOutputEdges.contains("h-i")) drawEdgeWithWeight(g, 125, 155, 155, 125, "h - i"); // H-I
				if (algorithmOutputEdges.contains("h-g")) drawEdgeWithWeight(g, 130, 165, 200, 165, "h - g"); // H-G
				if (algorithmOutputEdges.contains("i-g")) drawEdgeWithWeight(g, 175, 125, 205, 155, "i - g"); // I-G
				if (algorithmOutputEdges.contains("c-d")) drawEdgeWithWeight(g, 230, 65, 300, 65, "c - d"); // C-D
				if (algorithmOutputEdges.contains("c-f")) drawEdgeWithWeight(g, 225, 75, 305, 155, "c - f"); // C-F
				if (algorithmOutputEdges.contains("g-f")) drawEdgeWithWeight(g, 230, 165, 300, 165, "g - f"); // G-F
				if (algorithmOutputEdges.contains("d-e")) drawEdgeWithWeight(g, 325, 75, 355, 105, "d - e"); // D-E
				if (algorithmOutputEdges.contains("f-d")) drawEdgeWithWeight(g, 315, 150, 315, 80, "f - d"); // F-D
				if (algorithmOutputEdges.contains("f-e")) drawEdgeWithWeight(g, 325, 155, 355, 125, "f - e"); // F-E
				if (algorithmOutputEdges.contains("c-i")) drawEdgeWithWeight(g, 175, 105, 205, 75, "c - i"); // C-I
			}
		};
		graphPanel.setPreferredSize(new Dimension(500, 300));
		graphPanel.setBorder(BorderFactory.createTitledBorder("Graph"));
		return graphPanel;
	}
	
    // Helper method to create the result panel displaying algorithm output
	private JPanel createResultPanel(String result) {
		JPanel resultPanel = new JPanel(new BorderLayout());
		JTextArea outputArea = new JTextArea(result);
		outputArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(outputArea);
		resultPanel.add(scrollPane, BorderLayout.CENTER);
		return resultPanel;
	}
	
	public static void main(String[] args) {
		new GraphGUI();
	}
}


