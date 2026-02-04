package financetracker;

import financetracker.model.*;
import financetracker.view.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        try {
            // ШАГ 1: Инициализация файлового обработчика
            // Пути к файлам данных (относительно корня проекта)
            FileHandler fileHandler = new FileHandler(
                    "data/categories.txt",
                    "data/transactions.txt"
            );

            // ШАГ 2: Создание менеджера финансов
            // При создании автоматически загружаются данные из файлов
            FinanceManager financeManager = new FinanceManager(fileHandler);

            // ШАГ 3: Запуск пользовательского интерфейса
            ConsoleUI ui = new ConsoleUI(financeManager);
            ui.start();

            // ШАГ 4: Автоматическое сохранение при выходе
            // (уже реализовано в ConsoleUI при выборе пункта "Выход")
            System.out.println("Приложение завершено. Все данные сохранены.");

        } catch (Exception e) {
            // Обработка критических ошибок на верхнем уровне
            System.err.println("❌ КРИТИЧЕСКАЯ ОШИБКА: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Код выхода 1 = ошибка
        }
    }
}