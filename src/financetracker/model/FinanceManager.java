package financetracker.model;

import java.time.LocalDate;
import java.util.*;




public class FinanceManager {
    private List<Category> categories;
    private List<Transaction> transactions;
    private final FileHandler fileHandler;
    private Map<Integer, Category> categoryMap;

    public FinanceManager(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.categoryMap = new HashMap<>();
        loadData();
    }

    private void loadData() {
        categories = fileHandler.loadCategories();

        for (Category category :categories) {
            categoryMap.put(category.getId(), category);
        }

        transactions = fileHandler.loadTransactions(categoryMap);
    }

    public void saveAllData() {
        fileHandler.saveCategories(categories);
        fileHandler.saveTransactions(transactions);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    public List<Category> getCategoriesByType(TransactionType type) {
        List<Category> result = new ArrayList<>();
        for(Category category : categories) {
            if (category.getType() == type){
                result.add(category);
            }
        }
        return result;
    }

    public Category getCategoryById(int id) {
        return categoryMap.get(id);
    }


    public Category findCategoryByName(String name) {
        if (name == null) return null;
        String trimmedName = name.trim().toLowerCase();
        for (Category category : categories) {
            if (category.getName().toLowerCase().equals(trimmedName)) {
                return category;
            }
        }
        return null;
    }


    public boolean addCategory(String name, TransactionType type) {
        if (findCategoryByName(name) != null) {
            return false;
        }

        int newId = fileHandler.generateCategoryId();

        Category newCategory = new Category(newId, name, type);

        categories.add(newCategory);
        categoryMap.put(newId, newCategory);

        fileHandler.saveCategories(categories);

        return true;
    }


    public Category addCategoryIfNotExists(String name, TransactionType type) {
        Category existing = findCategoryByName(name);
        if (existing != null) {
            return  existing;
        }

        if (addCategory(name, type)) {
            return findCategoryByName(name);
        }

        return null;
    }


    public boolean removeCategory(int categoryId) {
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().getId() == categoryId) {
                return false;
            }
        }

        Category categoryToRemove = categoryMap.get(categoryId);
        if (categoryToRemove != null) {
            categories.remove(categoryToRemove);
            categoryMap.remove(categoryId);
            fileHandler.saveCategories(categories);
            return true;
        }

        return  false;
    }

    public  List<Category> getDefaultExpenseCategories() {
        return getCategoriesByType(TransactionType.EXPENSE);
    }

    public List<Category> getDefaultIncomeCategories() {
        return getCategoriesByType(TransactionType.INCOME);
    }


    public List<Transaction> getAllTransactions() {
        return  new ArrayList<>(transactions);
    }


    public List<Transaction> getTransactionsSortedByDate(boolean descending) {
        List<Transaction> sorted = new ArrayList<>(transactions);
        sorted.sort((t1, t2) -> {
            int comparsion = t2.getDate().compareTo(t1.getDate());
            if (comparsion == 0) {
                comparsion = Integer.compare(t2.getId(), t1.getId());
            }
            return descending ? comparsion : -comparsion;
        });
        return sorted;
    }


    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getDate();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                result.add(transaction);
            }
        }
        return result;
    }

    public List<Transaction> getTransactionsByCategory(int categoryId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().getId() == categoryId) {
                result.add(transaction);
            }
        }
        return result;
    }


    public List<Transaction> getTransactionsByType(TransactionType type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().getType() == type) {
                result.add(transaction);
            }
        }
        return result;
    }


    public boolean addTransaction(double amount, Category category, String discription, LocalDate date) {

        if (amount <=0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        if (category == null) {
            throw new IllegalArgumentException("Категория не может быть null");
        }

        if (date == null) {
             date = LocalDate.now();
        }

        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата не может быть в будущем");
        }

        int newId = fileHandler.generateTransactionId();

        Transaction newTransaction = new Transaction(newId, amount, category, discription, date);

        transactions.add(newTransaction);

        fileHandler.saveTransactions(transactions);

        return true;
    }

    public  boolean removeTransaction(int transactionId) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId() == transactionId) {
                transactions.remove(i);
                fileHandler.saveTransactions(transactions);
                return true;
            }
        }
        return false;
    }

    public  boolean updateTransaction(int transactionId, Double amount, Category category, String description, LocalDate date) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == transactionId) {
                try {
                    if (amount != null) transaction.setAmount(amount);
                    if (category != null) transaction.setCategory(category);
                    if (description != null) transaction.setDescription(description);
                    if (date != null) transaction.setDate(date);

                    fileHandler.saveTransactions(transactions);
                    return true;
                } catch (IllegalArgumentException e){
                    return false;
                }
            }
        }
        return false;
    }

    public  Transaction getTransactionById(int id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == id) {
                return transaction;
            }
        }
        return null;
    }


    public double getTotalBalance() {
        double balance = 0;
        for (Transaction transaction : transactions) {
            balance += transaction.getSignedAmount();
        }
        return  balance;
    }

    public double getIncomeTotal(LocalDate startDate, LocalDate endDate) {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                LocalDate date = transaction.getDate();
                if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                    total += transaction.getAmount();
                }
            }
        }
        return total;
    }

    public double getExpenseTotal(LocalDate startDate, LocalDate endDate) {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.isExpense()) {
                LocalDate date = transaction.getDate();
                if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                    total += transaction.getAmount();
                }
            }
        }
        return total;
    }

    public double getBalanceForPeriod(LocalDate startDate, LocalDate endDate) {
        return getIncomeTotal(startDate, endDate) - getExpenseTotal(startDate, endDate);
    }

    public Map<String , Double> getCategoryStatistics(LocalDate startDate, LocalDate  endDate, TransactionType type) {
        Map<String, Double> statistics = new HashMap<>();

        for(Transaction transaction : transactions) {
            if (transaction.getCategory().getType() == type) {
                LocalDate date = transaction.getDate();
                if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                    String categoryName = transaction.getCategory().getName();
                    double amount = transaction.getAmount();
                    statistics.put(categoryName,statistics.getOrDefault(categoryName, 0.0) + amount);

                }
            }
        }
        return statistics;
    }

    public  Map<String, Map<TransactionType, Double>> getMonthlyStatistics(int year) {
        Map<String, Map<TransactionType, Double>> monthlyStats = new TreeMap<>();

        for (int month = 1; month <= 12; month++) {
            String monthKey = String.format("%02d.%d", month, year);
            monthlyStats.put(monthKey, new HashMap<>());
            monthlyStats.get(monthKey).put(TransactionType.INCOME, 0.0);
            monthlyStats.get(monthKey).put(TransactionType.EXPENSE, 0.0);
        }

        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == year) {
                String monthKey = String.format("%02d.%d", transaction.getMonth(), year);
                TransactionType type = transaction.getCategory().getType();
                double amount = transaction.getAmount();

                double current = monthlyStats.get(monthKey).get(type);
                monthlyStats.get(monthKey).put(type, current + amount);
            }
        }

        return monthlyStats;
    }

    public List<Transaction> getTopExpenses(int limit) {
        List<Transaction> expenses = getTransactionsByType(TransactionType.EXPENSE);
        expenses.sort((t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount()));
        return  expenses.subList(0, Math.min(limit, expenses.size()));
    }

    public List<Transaction> getRecentTransactions(int count) {
        List<Transaction> sorted = getTransactionsSortedByDate(true);
        return sorted.subList(0, Math.min(count, sorted.size()));
    }

    public  boolean hasTransactionsInMonth(int year, int month) {
        for (Transaction transaction : transactions) {
            if (transaction.getYear() == year && transaction.getMonth() == month) {
                return true;
            }
        }
        return false;
    }

    public  Set<Integer> getYearsWithTransactions() {
        Set<Integer> years = new TreeSet<>();
        for (Transaction transaction : transactions) {
            years.add(transaction.getYear());
        }
        return years;
    }


    public boolean exportTransactionsToCSV(String filePath) {
        return fileHandler.exportToCSV(transactions, filePath);
    }


    public boolean exportTransactionsToCSV(String filePath, LocalDate startDate, LocalDate endDate) {
        List<Transaction> filtred = getTransactionsByDateRange(startDate, endDate);
        return fileHandler.exportToCSV(filtred, filePath);
    }

    public void clearAllTransactions() {
        transactions.clear();
        fileHandler.clearTransactions();
    }

    public Map<String, Object> getFileStatistics() {
        return fileHandler.getFileStats();
    }

    public boolean validateData() {
        for(Transaction transaction : transactions) {
            Category category = transaction.getCategory();
            if (!categoryMap.containsKey(category.getId())) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Object> getFinancialSummary() {
        Map<String, Object> summary = new HashMap<>();

        summary.put("totalTransactions", transactions.size());
        summary.put("totalCategories", categories.size());
        summary.put("totalBalance", getTotalBalance());

        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate lastDayOfMonth = LocalDate.of(now.getYear(), now.getMonth(), now.lengthOfMonth());

        summary.put("currentMonthIncome", getIncomeTotal(firstDayOfMonth, lastDayOfMonth));
        summary.put("currentMonthExpenses", getExpenseTotal(firstDayOfMonth, lastDayOfMonth));
        summary.put("currentMonthBalance", getBalanceForPeriod(firstDayOfMonth, lastDayOfMonth));

        long incomeCount = transactions.stream().filter(Transaction::isIncome).count();
        long expenseCount = transactions.stream().filter(Transaction::isExpense).count();
        summary.put("incomeCount", incomeCount);
        summary.put("expenseCount", expenseCount);

        return summary;
    }

}
