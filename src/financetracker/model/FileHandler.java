package financetracker.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;


import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class FileHandler {
    private final String categoriesFilePath;
    private final String transactionsFilePath;
    private  int lastCategoryId = 0;
    private int lastTransactionId = 0;


    public FileHandler(String categoriesFilePath, String transactionsFilePath) {
        this.categoriesFilePath = categoriesFilePath;
        this.transactionsFilePath = transactionsFilePath;
        ensureFilesExist();


    }



    private void ensureFilesExist() {

        try {
             Path categoriesPath = Paths.get(categoriesFilePath);
             Path transactionsPath = Paths.get(transactionsFilePath);

             Path categoriesParent = categoriesPath.getParent();
             Path transactionsParent = transactionsPath.getParent();


             if (categoriesParent != null) {
                 Files.createDirectories(categoriesParent);
             }

             if (transactionsParent != null) {
                 Files.createDirectories(transactionsParent);
             }

             if (!Files.exists(categoriesPath)) {
                 Files.createFile(categoriesPath);
                 initializeDefaultCategories();
             }

             if (!Files.exists(transactionsPath)) {
                 Files.createFile(transactionsPath);
             }


        } catch (IOException e) {
            System.err.println("Ошибка при создании файлов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDefaultCategories() {
        List<Category> defaultCategories = new ArrayList<>();

        defaultCategories.add(new Category(1, Category.FOOD, TransactionType.EXPENSE));
        defaultCategories.add(new Category(2, Category.TRANSPORT, TransactionType.EXPENSE));
        defaultCategories.add(new Category(3, Category.HEALTH, TransactionType.EXPENSE));
        defaultCategories.add(new Category(4, Category.ENTERTAINMENT, TransactionType.EXPENSE));
        defaultCategories.add(new Category(5, Category.UTILITIES, TransactionType.EXPENSE));
        defaultCategories.add(new Category(6, Category.COMMUNICATION, TransactionType.EXPENSE));
        defaultCategories.add(new Category(7, Category.HOUSEHOLD, TransactionType.EXPENSE));
        defaultCategories.add(new Category(8, Category.OTHER, TransactionType.EXPENSE));

        defaultCategories.add(new Category(101, Category.SALARY, TransactionType.INCOME));
        defaultCategories.add(new Category(102, Category.BONUS, TransactionType.INCOME));
        defaultCategories.add(new Category(103, Category.INVESTMENTS, TransactionType.INCOME));
        defaultCategories.add(new Category(104, Category.EXTRA_INCOME, TransactionType.INCOME));

        saveCategories(defaultCategories);
        lastCategoryId = 104;
    }

    public void saveCategories(List<Category> categories) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(categoriesFilePath),
                        StandardCharsets.UTF_8
                ))) {

            for (Category category : categories) {
                writer.write(category.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении категорий: " + e.getMessage());
        }
    }


    public  List<Category> loadCategories() {
        List<Category> categories = new ArrayList<>();
        Path path = Paths.get(categoriesFilePath);


        if (!Files.exists(path) || !Files.isReadable(path)) {
            return categories;
        }
        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Category category = Category.fromFileString(line);
                        categories.add(category);

                        lastCategoryId = Math.max(lastCategoryId, category.getId());

                    } catch (Exception e) {
                        System.err.println("Ошибка при загрузке категории: " + line + " - " + e.getMessage());
                    }

                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла категорий: " + e.getMessage());
        }

        return categories;
    }


    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(transactionsFilePath),
                        StandardCharsets.UTF_8
                ))) {

            for (Transaction transaction : transactions) {
                writer.write(transaction.toFileString());
                writer.newLine();
                lastTransactionId = Math.max(lastTransactionId, transaction.getId());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении транзакций: " + e.getMessage());
        }
    }

    public  List<Transaction> loadTransactions(Map<Integer, Category> categoryMap) {
        List<Transaction> transactions = new ArrayList<>();
        Path path = Paths.get(transactionsFilePath);


        if (!Files.exists(path) || !Files.isReadable(path)) {
            return transactions;
        }
        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Transaction transaction = Transaction.fromFileString(line, categoryMap);
                        transactions.add(transaction);

                        lastTransactionId = Math.max(lastTransactionId, transaction.getId());

                    } catch (Exception e) {
                        System.err.println("Ошибка при загрузке транзакции: " + line + " - " + e.getMessage());
                    }

                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла транзакций: " + e.getMessage());
        }

        return transactions;
    }

    public int generateCategoryId() {
        lastCategoryId++;
        return lastCategoryId;
    }

    public int generateTransactionId() {
        lastTransactionId++;
        return lastTransactionId;
    }

    public boolean exportToCSV(List<Transaction> transactions, String exportFilePath) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(exportFilePath),
                        StandardCharsets.UTF_8
                ))) {

            writer.write("ID;Дата;Тип;Категория;Сумма;Описание");
            writer.newLine();

            for (Transaction transaction : transactions) {
                String type = transaction.isIncome() ? "Доход" : "Расход";
                String line = String.format("%d;%s;%s;%s;%.2f;%s",
                        transaction.getId(),
                        transaction.getFormatteddate(),
                        type,
                        transaction.getCategory().getName(),
                        transaction.getAmount(),
                        transaction.getDescription());
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка при экспорте в CSV: " + e.getMessage());
            return false;
        }
    }


    public Map<String, Object> getFileStats() {
        Map<String, Object> stats = new HashMap<>();


        try{
            Path categoriesPath = Paths.get(categoriesFilePath);
            Path transactionsPath = Paths.get(transactionsFilePath);

            if(Files.exists(categoriesPath)) {
                stats.put("categoriesFileSize", Files.size(categoriesPath));
                stats.put("categoriesLastModified", Files.getLastModifiedTime(categoriesPath).toString());
            }

            if(Files.exists(transactionsPath)) {
                stats.put("transactionsFileSize", Files.size(transactionsPath));
                stats.put("transactionsLastModified", Files.getLastModifiedTime(transactionsPath).toString());
            }

            stats.put("lastCategoryId", lastCategoryId);
            stats.put("lastTransactionId", lastTransactionId);
        } catch (IOException e){
            System.err.println("Ошибка при получении статистики файлов" + e.getMessage());
        }

        return stats;
    }

    public void clearTransactions() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(transactionsFilePath),
                        StandardCharsets.UTF_8
                ))) {

        } catch (IOException e) {
            System.err.println("Ошибка при очистке файла транзакций: " + e.getMessage());
        }
        lastTransactionId = 0;
    }
}


