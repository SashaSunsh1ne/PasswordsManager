package controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.DBConnect;
import model.DaoDataBase;
import model.User;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public TextField addUserUsername;                    //
    public PasswordField addUserPassword;               //
    public TextField deleteUserUsername;               // Поля для заполнения
    public TextField findUserUsername;                // имен пользователей и паролей
    public TextField changePasswordUsername;         //
    public PasswordField changePasswordPassword;    //
    public TableView<User> tableView;                                            // Таблица
    public TableColumn<User, Integer> user_id;                                  // Столбец
    public TableColumn<User, String> username;                                 // Столбец
    public TableColumn<User, String> password;                                // Столбец
    DaoDataBase daoDataBase = new DBConnect();                               // Переменная для управления базой данных
    Connection connection = daoDataBase.dbConnect();                        // Подключение к базе данных
    ObservableList<User> users = null;                                     // Переменная с пользователями
    ObservableList<User> tableData = null;                                // Переменная с данными таблицы
    ResultSet resultSet = null;                                          // Переменная для записи результатов запросов на выборку
    String querySelectAll = "SELECT * FROM " + daoDataBase.getDbName(); // Запрос на выборку
    String query;                                                      // Остальные запросы




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            daoDataBase.createDbUserTableDao();                          // Создание теблицы в базе данных
            users = daoDataBase.create(querySelectAll);                 // Помещение пользователей из базы данных в переменную
            resultSet = daoDataBase.dbExecuteQueryDao(querySelectAll); // Запрос на выборку
        }
        catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }     // Исключения

        user_id.setCellValueFactory(new PropertyValueFactory<User, Integer>("userId"));
        username.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        password.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        tableView.setItems(users);
        tableData = tableView.getItems();
    }

    public void addUser(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String usernameText = addUserUsername.getText();
        String passwordText = addUserPassword.getText();
        users.add(new User(usernameText,passwordText));
        daoDataBase.updateUsersDao(users);
        resultSet = daoDataBase.dbExecuteQueryDao(querySelectAll);
        updateAll(resultSet);
    }

    public void deleteUser(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String usernameText = deleteUserUsername.getText();
        daoDataBase.dbExecuteUpdateDao("Delete from " + daoDataBase.getDbName() + " Where username = '" + usernameText + "'");
        resultSet = daoDataBase.dbExecuteQueryDao(querySelectAll);
        updateAll(resultSet);
    }

    public void findUser(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String usernameText = findUserUsername.getText();
        resultSet = daoDataBase.dbExecuteQueryDao(querySelectAll + " Where username = '"+ usernameText + "'");
        updateAll(resultSet);
    }

    public void changePassword(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String usernameText = changePasswordUsername.getText();
        String passwordText = changePasswordPassword.getText();
        query = "UPDATE users set password='" + passwordText + "' where username='" + usernameText + "'";
        daoDataBase.dbExecuteUpdateDao(query);
        resultSet = daoDataBase.dbExecuteQueryDao(querySelectAll);
        updateAll(resultSet);
    }

    public void updateAll(ResultSet rs) throws SQLException, ClassNotFoundException {
        users.clear();
        users=daoDataBase.create(querySelectAll);
        tableData.clear();
        while (rs.next()) {
            User user = new User(Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3));
            tableData.add(user);
        }
    }

}