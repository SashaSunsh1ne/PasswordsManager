package model;

import com.sun.rowset.CachedRowSetImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DBConnect implements DaoDataBase{

    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_CONNECTION ="jdbc:mysql://localhost:3306/passwordsmanager";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Flomaster1337882";
    public static final String DBName="users";

    // Создание соединения
    private static Connection getDBConnection() {
        Connection dbConnection = null;

        // Подключение драйвера
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    // Создание БД
    public static void createDbUserTable() throws SQLException {
        Connection dbConnection = null;
        Statement statement = null;

        String createTableSQL = "CREATE TABLE "+ DBName+ " ("
                + "USER_ID INT NOT NULL AUTO_INCREMENT, "
                + "USERNAME VARCHAR(50) NOT NULL, "
                + "PASSWORD VARCHAR(50) NOT NULL, "
                + "PRIMARY KEY (USER_ID) "
                + ")";

        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            statement.execute(createTableSQL); // Выполнить SQL запрос  ПОПРОБОВАТЬ EXECUTEQUERY
            System.out.println("Table \"dbuser\" создана!"); // \" - Вывод кавычек
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    @Override
    public Connection dbConnect() {
        Connection connection = getDBConnection();
        return connection;
    }

    @Override
    public void dbDisconnect() throws SQLException {
        getDBConnection().close();
    }

    @Override
    public ResultSet dbExecuteQueryDao(String queryStmt) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = dbExecuteQuery(queryStmt);
        return resultSet;
    }

    @Override
    public void dbExecuteUpdateDao(String sqlStmt) throws SQLException, ClassNotFoundException {
        dbExecuteUpdate(sqlStmt);
    }

    @Override
    public void updateUsersDao(ObservableList<User> users) {
        update(users);
    }

    @Override
    public void createDbUserTableDao() throws SQLException {
        createDbUserTable();
    }

    @Override
    public String getDbName() {
        return DBName;
    }


    // Выполнение запроса к БД с получением данных
    public static ResultSet dbExecuteQuery(String queryStmt) throws SQLException, ClassNotFoundException {
        Connection dbConnection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        CachedRowSetImpl crs = null;
        try {
            dbConnection=getDBConnection();
            System.out.println("Select statement: " + queryStmt + "\n");
            statement = dbConnection.createStatement();
            resultSet = statement.executeQuery(queryStmt);

            crs = new CachedRowSetImpl();
            crs.populate(resultSet);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeQuery operation : " + e);
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();

            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
        return crs;
    }

    // Обновление БД
    public static void dbExecuteUpdate(String sqlStmt) throws SQLException, ClassNotFoundException {
        Connection dbConnection = null;
        Statement statement = null;
        try {
            dbConnection=getDBConnection();
            statement = dbConnection.createStatement();
            statement.executeUpdate(sqlStmt);
        } catch (SQLException e) {
            System.out.println("Problem occurred at executeUpdate operation : " + e);
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close(); //Close connection
            }
        }
    }

    public ObservableList<User> create(String query) throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        Connection dbConnection = null;
        Statement statement = null;
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            ResultSet rs = statement.executeQuery(query); // выбираем данные с БД
            // И если что то было получено то цикл while сработает
            while (rs.next()) {
                User user = new User(Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public void update (ObservableList<User> users)
    {
        Connection dbConnection = null;
        Statement statement = null;
        if(users.isEmpty()) return;
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            statement.executeUpdate("INSERT INTO " + DBName +
                    "(USERNAME, PASSWORD) " +
                    "VALUES ('" + users.get(users.size() - 1).getUsername() + "', '" + users.get(users.size() - 1).getPassword() + "')");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
}