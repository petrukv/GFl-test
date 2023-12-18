import java.sql.*;

public class EquationDatabase {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/GFL";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "password";

    //вивід всіх записів із бд
    static void displayDatabaseRecords() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM gfl")) {

            System.out.println("Записи з бази даних:");
            while (resultSet.next()) {
                String equation = resultSet.getString("equation");
                String root = resultSet.getString("root");

                System.out.println("Equation: " + equation + ", Root: " + root);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //шукає рівняння за заданим коренем у базі даних
    static void findEquationByRoot(String rootToSearch) throws SQLException {
        String selectQuery = "SELECT * FROM gfl WHERE root = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            double rootValue = Double.valueOf(rootToSearch);

            preparedStatement.setString(1, String.valueOf(rootValue));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean found = false;

                while (resultSet.next()) {
                    String equation = resultSet.getString("equation");
                    String root = resultSet.getString("root");

                    System.out.println("Рівняння з коренем " + rootToSearch + ": " + equation);
                    found = true;
                }

                if (!found) {
                    System.out.println("Рівняння з коренем " + rootToSearch + " не знайдено.");
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Помилка при пошуку рівняння за коренем", e);
        }
    }

    //зберігає рівняння та його корінь в базу даних
    static void saveToDatabase(String equation, String root) {
        String insertQuery = "INSERT INTO gfl (equation, root) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, equation);
            preparedStatement.setString(2, root);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Збереження в базу даних не вдалося, жоден рядок не був змінений.");
            } else {
                // Отримайте згенерований ключ (id)
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("Запис додано до бази даних");
                    } else {
                        throw new SQLException();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //знаходить рівняння, у яких є рівно один корінь
    public static void findEquationsWithSingleRoot() {
        String selectQuery = "SELECT root, COUNT(*) as count FROM gfl GROUP BY root";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

            System.out.println("Рівняння з рівно одним коренем:");

            while (resultSet.next()) {
                String root = resultSet.getString("root");

                String equationQuery = "SELECT equation FROM gfl WHERE root = ?";
                try (PreparedStatement equationStatement = connection.prepareStatement(equationQuery)) {
                    equationStatement.setString(1, root);
                    try (ResultSet equationResultSet = equationStatement.executeQuery()) {
                        while (equationResultSet.next()) {
                            String equation = equationResultSet.getString("equation");
                            System.out.println("Корінь: " + root + ", Рівняння: " + equation);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
