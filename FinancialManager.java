class FinancialManager implements TransactionHandler {
    private ArrayList<Transaction> transactions;
    private Scanner scanner;

    public FinancialManager() {
        this.transactions = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        
    }

    @Override
    public void addTransaction(LocalDate date, double amount, String type, String description) {
        transactions.add(new Transaction(date, amount, type, description));
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void run() {
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    displayFinancialMap();
                    break;
                case "2":
                    handleTransactionInput();
                    break;
                case "3":
                    running = false;
                    System.out.println("Exiting program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
