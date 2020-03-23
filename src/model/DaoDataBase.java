package model;

import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DaoDataBase {
    Connection dbConnect();
    void dbDisconnect() throws SQLException;
    ResultSet dbExecuteQueryDao(String queryStmt) throws SQLException, ClassNotFoundException;
    void dbExecuteUpdateDao(String sqlStmt) throws SQLException, ClassNotFoundException;
    void updateUsersDao (ObservableList<User> users);
    void createDbUserTableDao() throws SQLException;
    ObservableList<User> create (String query) throws SQLException;
    String getDbName();
}
