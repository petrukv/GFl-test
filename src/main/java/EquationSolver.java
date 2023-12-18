import java.sql.*;
import java.util.Scanner;

public class EquationSolver {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/GFL";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Оберіть опцію:");
            System.out.println("1. Ввести рівняння та корінь");
            System.out.println("2. Переглянути записи з бази даних");
            System.out.println("3. Пошук рівняння за коренем");
            System.out.println("4. Пошук рівняння в якому тільки один корінь");
            System.out.println("5. Вийти");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> EquationHandler.executeEquationSolver(scanner);
                case 2 -> EquationDatabase.displayDatabaseRecords();
                case 3 -> EquationHandler.searchEquationByRoot(scanner);
                case 4 -> EquationDatabase.findEquationsWithSingleRoot();
                case 5 -> {
                    System.out.println("Дякую за використання програми. До побачення!");
                    System.exit(0);
                }
                default -> System.out.println("Некоректний вибір. Спробуйте ще раз.");
            }
        }
    }
}
