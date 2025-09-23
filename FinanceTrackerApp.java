import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main application class.
 * This class extends JFrame and acts as the main window.
 * It uses a CardLayout to manage and switch between different "screens" (JPanels).
 */
public class FinanceTrackerApp extends JFrame {

    // Constants for CardLayout panel names
    public static final String MAIN_MENU = "MAIN_MENU";
    public static final String INPUT_MENU = "INPUT_MENU";
    public static final String MANUAL_ENTRY = "MANUAL_ENTRY";
    public static final String PDF_UPLOAD = "PDF_UPLOAD";
    public static final String FINANCIAL_MAP = "FINANCIAL_MAP";

    private CardLayout cardLayout;
    private JPanel cards; // The panel that holds all other panels (the "screens")

    /**
     * Constructor for the main application.
     */
    public FinanceTrackerApp() {
        // --- Basic JFrame setup ---
        setTitle("Personal Finance Heatmap Tracker");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // --- CardLayout setup ---
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // --- Create and add all panels (screens) ---
        // Pass 'this' (the main app) to each panel so they can trigger navigation
        cards.add(new MainMenuPanel(this), MAIN_MENU);
        cards.add(new InputMenuPanel(this), INPUT_MENU);
        cards.add(new ManualEntryPanel(this), MANUAL_ENTRY);
        cards.add(new PdfUploadPanel(this), PDF_UPLOAD);
        cards.add(new FinancialMapPanel(this), FINANCIAL_MAP);

        // Add the main 'cards' panel to the JFrame
        add(cards);
    }

    /**
     * Public method to navigate between panels.
     *
     * @param panelName The string constant (e.g., MAIN_MENU) of the panel to show.
     */
    public void showPanel(String panelName) {
        cardLayout.show(cards, panelName);
    }

    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            FinanceTrackerApp app = new FinanceTrackerApp();
            app.setVisible(true);
        });
    }

    // =====================================================================
    // STEP 4: STORAGE (Model and Storage Classes)
    // =====================================================================

    /**
     * Data model for a single transaction (Step 4).
     */
    static class Transaction {
        private final LocalDate date;
        private final double amount; // Positive for Credit, Negative for Debit
        private final String type; // "Credit" or "Debit"
        private final String description;

        public Transaction(LocalDate date, double amount, String type, String description) {
            this.date = date;
            this.amount = amount;
            this.type = type;
            this.description = description;
        }

        public LocalDate getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return String.format("%s | %-7s | $%.2f | %s",
                    date, type, amount, description);
        }
    }

    /**
     * Singleton class to manage the list of transactions (Step 4).
     * This acts as our in-memory storage.
     */
    static class TransactionStorage {
        private static final TransactionStorage instance = new TransactionStorage();
        private final List<Transaction> transactions;

        private TransactionStorage() {
            transactions = new ArrayList<>();
        }

        public static TransactionStorage getInstance() {
            return instance;
        }

        public void addTransaction(Transaction t) {
            transactions.add(t);
            // In a real app, you would also save to a file here (CSV, JSON, etc.)
        }

        /**
         * Retrieves transactions within a specific date range (for Step 2).
         */
        public List<Transaction> getTransactionsForPeriod(LocalDate start, LocalDate end) {
            return transactions.stream()
                    .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                    .sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
                    .collect(Collectors.toList());
        }
    }

    // =====================================================================
    // STEP 1: MAIN MENU INTERFACE
    // =====================================================================

    static class MainMenuPanel extends JPanel {
        public MainMenuPanel(FinanceTrackerApp app) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component on a new line
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel title = new JLabel("Personal Finance Tracker");
            title.setFont(new Font("Arial", Font.BOLD, 24));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            add(title, gbc);

            JButton mapButton = new JButton("1. Financial Map");
            mapButton.setFont(new Font("Arial", Font.PLAIN, 16));
            mapButton.addActionListener(e -> app.showPanel(FINANCIAL_MAP));
            add(mapButton, gbc);

            JButton inputButton = new JButton("2. Input / Insert Transactions");
            inputButton.setFont(new Font("Arial", Font.PLAIN, 16));
            inputButton.addActionListener(e -> app.showPanel(INPUT_MENU));
            add(inputButton, gbc);

            JButton exitButton = new JButton("Exit");
            exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
            exitButton.addActionListener(e -> System.exit(0));
            add(exitButton, gbc);
        }
    }

    // =====================================================================
    // STEP 3: INPUT / INSERT TRANSACTIONS (Sub-Menu)
    // =====================================================================

    static class InputMenuPanel extends JPanel {
        public InputMenuPanel(FinanceTrackerApp app) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel title = new JLabel("Input Transactions");
            title.setFont(new Font("Arial", Font.BOLD, 20));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            add(title, gbc);

            JButton pdfButton = new JButton("1. Upload PDF");
            pdfButton.addActionListener(e -> app.showPanel(PDF_UPLOAD));
            add(pdfButton, gbc);

            JButton manualButton = new JButton("2. Manual Entry");
            manualButton.addActionListener(e -> app.showPanel(MANUAL_ENTRY));
            add(manualButton, gbc);

            JButton backButton = new JButton("Back to Main Menu");
            backButton.setForeground(Color.BLUE);
            backButton.addActionListener(e -> app.showPanel(MAIN_MENU));
            add(backButton, gbc);
        }
    }

    // =====================================================================
    // STEP 3 (Case B): MANUAL ENTRY INTERFACE
    // =====================================================================

    static class ManualEntryPanel extends JPanel {
        private final JTextField dateField;
        private final JTextField descriptionField;
        private final JTextField amountField;
        private final JRadioButton creditRadio;
        private final JRadioButton debitRadio;

        public ManualEntryPanel(FinanceTrackerApp app) {
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createTitledBorder("Manual Transaction Entry"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // --- Form Fields ---
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Date (YYYY-MM-DD):"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            dateField = new JTextField(10);
            dateField.setText(LocalDate.now().toString()); // Default to today
            add(dateField, gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            add(new JLabel("Description:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            descriptionField = new JTextField(20);
            add(descriptionField, gbc);

            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
            add(new JLabel("Amount:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            amountField = new JTextField(10);
            add(amountField, gbc);

            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
            add(new JLabel("Type:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            creditRadio = new JRadioButton("Credit (+)");
            debitRadio = new JRadioButton("Debit (-)");
            creditRadio.setSelected(true);
            ButtonGroup typeGroup = new ButtonGroup();
            typeGroup.add(creditRadio);
            typeGroup.add(debitRadio);
            JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            radioPanel.add(creditRadio);
            radioPanel.add(debitRadio);
            add(radioPanel, gbc);

            // --- Buttons ---
            gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> app.showPanel(INPUT_MENU));
            add(backButton, gbc);

            gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
            JButton submitButton = new JButton("Submit Transaction");
            submitButton.addActionListener(e -> submitTransaction());
            add(submitButton, gbc);
        }

        private void submitTransaction() {
            try {
                // 1. Get and parse values
                LocalDate date = LocalDate.parse(dateField.getText());
                String description = descriptionField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String type = creditRadio.isSelected() ? "Credit" : "Debit";

                // 2. Enforce +/- convention (Step 3B)
                if (type.equals("Debit")) {
                    amount = -Math.abs(amount);
                } else {
                    amount = Math.abs(amount);
                }

                if (description.isEmpty()) {
                    description = "(No description)";
                }

                // 3. Create and store transaction
                Transaction t = new Transaction(date, amount, type, description);
                TransactionStorage.getInstance().addTransaction(t);

                // 4. Show success and clear fields
                JOptionPane.showMessageDialog(this,
                        "Transaction added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear for next entry
                descriptionField.setText("");
                amountField.setText("");
                creditRadio.setSelected(true);

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please use YYYY-MM-DD.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid amount. Please enter a valid number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =====================================================================
    // STEP 3 (Case A): UPLOAD PDF INTERFACE (Stubbed)
    // =====================================================================

    static class PdfUploadPanel extends JPanel {
        private final JLabel selectedFileLabel;
        private File selectedFile;

        public PdfUploadPanel(FinanceTrackerApp app) {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createTitledBorder("Upload PDF Bank Statement"));

            // --- Top Panel: File Selection ---
            JPanel topPanel = new JPanel();
            JButton browseButton = new JButton("Browse...");
            selectedFileLabel = new JLabel("No file selected.");
            topPanel.add(browseButton);
            topPanel.add(selectedFileLabel);
            add(topPanel, BorderLayout.NORTH);

            // --- Center Panel: Info ---
            JTextArea infoArea = new JTextArea();
            infoArea.setEditable(false);
            infoArea.setLineWrap(true);
            infoArea.setWrapStyleWord(true);
            infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            infoArea.setText("--- PDF Parsing ---" +
                    "\n\nThis feature requires an external library like Apache PDFBox." +
                    "\n\nTo implement this, you would:" +
                    "\n1. Add the PDFBox .jar file to your project's classpath." +
                    "\n2. Use 'PDFTextStripper' to extract text from the selected file." +
                    "\n3. Use regular expressions (regex) to find patterns for:" +
                    "\n - Date (e.g., 'dd/mm/yyyy')" +
                    "\n - Description (text strings)" +
                    "\n - Amount (e.g., '$xx.xx' in a credit or debit column)" +
                    "\n4. Create 'Transaction' objects from the extracted data." +
                    "\n5. Add them to the 'TransactionStorage'.");
            add(new JScrollPane(infoArea), BorderLayout.CENTER);


            // --- Bottom Panel: Actions ---
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> app.showPanel(INPUT_MENU));
            bottomPanel.add(backButton);

            JButton processButton = new JButton("Process PDF");
            processButton.addActionListener(e -> processPdf());
            bottomPanel.add(processButton);
            add(bottomPanel, BorderLayout.SOUTH);

            // --- Action Listener for Browse Button ---
            browseButton.addActionListener(this::browseForFile);
        }

        private void browseForFile(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            // Optional: Add a file filter for PDFs
            // FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Documents", "pdf");
            // fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                selectedFileLabel.setText(selectedFile.getName());
            }
        }

        private void processPdf() {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a PDF file first.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // This is the "stubbed" part.
            // In a real app, this method would contain the complex PDFBox logic.
            JOptionPane.showMessageDialog(this,
                    "PDF Parsing logic is not implemented.\n" +
                            "This is where you would call your PDF parsing library (e.g., PDFBox)\n" +
                            "to process the file: " + selectedFile.getName(),
                    "Feature Stub",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =====================================================================
    // STEP 2: FINANCIAL MAP INTERFACE (Visualization Stubbed)
    // =====================================================================

    static class FinancialMapPanel extends JPanel {
        private final JComboBox<String> periodSelector;
        private final JTextArea summaryArea;
        private final JPanel heatmapPanel;
        private final JPanel graphPanel;

        public FinancialMapPanel(FinanceTrackerApp app) {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createTitledBorder("Financial Map"));

            // --- Top Panel: Controls (Step 2.1) ---
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            controlPanel.add(new JLabel("Select Time Period:"));
            periodSelector = new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly", "Yearly"});
            controlPanel.add(periodSelector);

            JButton generateButton = new JButton("Generate Report");
            generateButton.addActionListener(e -> generateReport());
            controlPanel.add(generateButton);

            JButton backButton = new JButton("Back to Main Menu");
            backButton.addActionListener(e -> app.showPanel(MAIN_MENU));
            controlPanel.add(backButton);
            add(controlPanel, BorderLayout.NORTH);

            // --- Center Panel: Data and Visualizations ---
            JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainSplit.setResizeWeight(0.4); // Give 40% to the left (summary)

            // --- Left Side: Data Summary (Step 2.3) ---
            summaryArea = new JTextArea("Select a period and click 'Generate'.");
            summaryArea.setEditable(false);
            summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            mainSplit.setLeftComponent(new JScrollPane(summaryArea));

            // --- Right Side: Visualizations (Step 2.4 - Stubbed) ---
            JSplitPane vizSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            vizSplit.setResizeWeight(0.5); // 50/50 split

            heatmapPanel = createPlaceholderPanel("Heatmap Visualization");
            heatmapPanel.setToolTipText("A real heatmap would show spending intensity by day/category.");
            vizSplit.setTopComponent(heatmapPanel);

            graphPanel = createPlaceholderPanel("Graph Visualization");
            graphPanel.setToolTipText("A real graph would show credits vs. debits over time (Bar/Line).");
            vizSplit.setBottomComponent(graphPanel);

            mainSplit.setRightComponent(vizSplit);
            add(mainSplit, BorderLayout.CENTER);
        }

        /**
         * Helper method to create a placeholder panel for a visualization.
         */
        private JPanel createPlaceholderPanel(String title) {
            JPanel panel = new JPanel(new GridBagLayout());
            Border etched = BorderFactory.createEtchedBorder();
            panel.setBorder(BorderFactory.createTitledBorder(etched, title));
            
            JLabel label = new JLabel(title + " (Stub)");
            label.setFont(new Font("Arial", Font.ITALIC, 14));
            label.setForeground(Color.GRAY);
            panel.add(label);
            
            JLabel info = new JLabel("Requires a library like JFreeChart");
            info.setFont(new Font("Arial", Font.ITALIC, 10));
            info.setForeground(Color.LIGHT_GRAY);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 1;
            panel.add(info, gbc);
            
            return panel;
        }

        /**
         * Corresponds to Step 2.2 and 2.3.
         * Fetches, processes, and displays the data summary.
         */
        private void generateReport() {
            // 1. Get selected period
            String period = (String) periodSelector.getSelectedItem();
            LocalDate now = LocalDate.now();
            LocalDate start = now;
            LocalDate end = now;

            // 2. Determine date range
            switch (period) {
                case "Daily":
                    // Start and end are 'now' (already set)
                    break;
                case "Weekly":
                    start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    end = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    break;
                case "Monthly":
                    start = now.withDayOfMonth(1);
                    end = now.with(TemporalAdjusters.lastDayOfMonth());
                    break;
                case "Yearly":
                    start = now.withDayOfYear(1);
                    end = now.with(TemporalAdjusters.lastDayOfYear());
                    break;
            }

            // 3. Retrieve transactions (Step 2.2)
            List<Transaction> transactions = TransactionStorage.getInstance()
                                                 .getTransactionsForPeriod(start, end);

            // 4. Process data (Step 2.3)
            double totalCredits = 0;
            double totalDebits = 0;
            for (Transaction t : transactions) {
                if (t.getAmount() > 0) {
                    totalCredits += t.getAmount();
                } else {
                    totalDebits += t.getAmount(); // Amount is already negative
                }
            }
            double netBalance = totalCredits + totalDebits;

            // 5. Generate and display summary
            StringBuilder sb = new StringBuilder();
            sb.append("Financial Report\n");
            sb.append("============================\n");
            sb.append(String.format("Period: %s (%s to %s)\n\n", period, start, end));
            sb.append(String.format("Total Credits: $%.2f\n", totalCredits));
            sb.append(String.format("Total Debits: $%.2f\n", totalDebits));
            sb.append(String.format("Net Balance: $%.2f\n", netBalance));
            sb.append("\n--- All Transactions (" + transactions.size() + ") ---\n");

            if (transactions.isEmpty()) {
                sb.append("No transactions for this period.");
            } else {
                for (Transaction t : transactions) {
                    sb.append(t.toString()).append("\n");
                }
            }
            summaryArea.setText(sb.toString());
            summaryArea.setCaretPosition(0); // Scroll to top

            // In a real app, you would now pass the 'transactions' list
            // to your heatmap and graph components to render them.
        }
    }
}