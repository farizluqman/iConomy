package com.iConomy.net;

import com.iConomy.util.Constants;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

public class Database {
    private JdbcConnectionPool h2pool;
    private String driver;
    private String dsn;
    private String username;
    private String password;

    public Database() throws Exception {
        if (Constants.isDatabaseTypeH2()) {
            this.driver = "org.h2.Driver";
            this.dsn = "jdbc:h2:" + Constants.Plugin_Directory + File.separator + Constants.SQLDatabase + ";AUTO_RECONNECT=TRUE";
            this.username = "sa";
            this.password = "sa";
        } else if (Constants.DatabaseType.equalsIgnoreCase("mysql")) {
            this.driver = "com.mysql.jdbc.Driver";
            this.dsn = "jdbc:mysql://" + Constants.SQLHostname + ":" + Constants.SQLPort + "/" + Constants.SQLDatabase + "?useSSL=" + Constants.SQLUseSSL;
            this.username = Constants.SQLUsername;
            this.password = Constants.SQLPassword;
        } else {
            throw new Exception("Invalid database type!");
        }

        try {
            Class.forName(this.driver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[iConomy] Driver error: " + e);
        }

        if (Constants.isDatabaseTypeH2() && this.h2pool == null)
            this.h2pool = JdbcConnectionPool.create(this.dsn, this.username, this.password);
    }

    public Connection getConnection() {
        try {
            if (this.username.equalsIgnoreCase("") && this.password.equalsIgnoreCase("")) {
                return DriverManager.getConnection(this.dsn);
            }
            if (Constants.isDatabaseTypeH2()) {
                return this.h2pool.getConnection();
            }
            return DriverManager.getConnection(this.dsn, this.username, this.password);
        } catch (SQLException e) {
            System.out.println("[iConomy] Could not create connection: " + e);
        }
        return null;
    }

    public void close(Connection connection) {
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException ex) {}
    }

    public void setupBankTable() throws Exception {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (Constants.isDatabaseTypeH2()) {
            try {
                ps = conn.prepareStatement("CREATE TABLE " + Constants.SQLTable + "_Banks(" + "id INT auto_increment PRIMARY KEY," + "name VARCHAR(32)," + "major VARCHAR(255)," + "minor VARCHAR(255)," + "initial DECIMAL(64,2)," + "fee DECIMAL(64,2)" + ");");

                ps.executeUpdate();
            } catch (SQLException E) {}
        } else {
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, Constants.SQLTable + "_Banks", null);

            if (!rs.next()) {
                System.out.println("[iConomy] Creating table: " + Constants.SQLTable + "_Banks");

                ps = conn.prepareStatement("CREATE TABLE " + Constants.SQLTable + "_Banks(" + "`id` INT(10) NOT NULL AUTO_INCREMENT," + "`name` VARCHAR(32) NOT NULL," + "`major` VARCHAR(255)," + "`minor` VARCHAR(255)," + "`initial` DECIMAL(64,2)," + "`fee` DECIMAL(64,2)," + "PRIMARY KEY (`id`)" + ")");

                ps.executeUpdate();

                System.out.println("[iConomy] Table Created.");
            }
        }

        if (ps != null)
            try {
                ps.close();
            } catch (SQLException ex) {}
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException ex) {}
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException ex) {}
    }

    public void setupBankRelationTable() throws Exception {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (Constants.isDatabaseTypeH2()) {
            try {
                ps = conn.prepareStatement("CREATE TABLE " + Constants.SQLTable + "_BankRelations(" + "id INT auto_increment PRIMARY KEY," + "account_name VARCHAR(32)," + "bank_id INT(10)," + "holdings DECIMAL(64,2)," + "main BOOLEAN DEFAULT '0'," + "hidden BOOLEAN DEFAULT '0'" + ");");

                ps.executeUpdate();
            } catch (SQLException E) {}
        } else {
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, Constants.SQLTable + "_BankRelations", null);

            if (!rs.next()) {
                System.out.println("[iConomy] Creating table: " + Constants.SQLTable + "_BankRelations");

                ps = conn.prepareStatement("CREATE TABLE " + Constants.SQLTable + "_BankRelations(" + "`id` INT(10) NOT NULL AUTO_INCREMENT," + "`account_name` VARCHAR(32)," + "`bank_id` INT(10)," + "`holdings` DECIMAL(64,2)," + "`main` BOOLEAN DEFAULT '0'," + "`hidden` BOOLEAN DEFAULT '0'," + "PRIMARY KEY (`id`)" + ")");

                ps.executeUpdate();

                System.out.println("[iConomy] Table Created.");
            }
        }

        if (ps != null)
            try {
                ps.close();
            } catch (SQLException ex) {}
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException ex) {}
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException ex) {}
    }

    public void setupAccountTable() throws Exception {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (Constants.isDatabaseTypeH2()) {
            try {
                ps = conn.prepareStatement("CREATE TABLE " + Constants.SQLTable + "(" + "id INT auto_increment PRIMARY KEY," + "username VARCHAR(32) UNIQUE," + "balance DECIMAL (64, 2)," + "hidden BOOLEAN DEFAULT '0'" + ");");

                ps.executeUpdate();
            } catch (SQLException E) {}
        } else {
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, Constants.SQLTable, null);

            if (!rs.next()) {
                System.out.println("[iConomy] Creating table: " + Constants.SQLTable);

                ps = conn.prepareStatement("CREATE TABLE " + Constants.SQLTable + " (" + "`id` INT(10) NOT NULL AUTO_INCREMENT," + "`username` VARCHAR(32) NOT NULL," + "`balance` DECIMAL(64, 2) NOT NULL," + "`hidden` BOOLEAN NOT NULL DEFAULT '0'," + "PRIMARY KEY (`id`)," + "UNIQUE(`username`)" + ")");

                if (ps != null) {
                    ps.executeUpdate();
                }

                System.out.println("[iConomy] Table Created.");
            }
        }

        if (ps != null)
            try {
                ps.close();
            } catch (SQLException ex) {}
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException ex) {}
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException ex) {}
    }

    public void setupTransactionTable() throws Exception {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (Constants.Logging) {
            if (Constants.isDatabaseTypeH2()) {
                try {
                    ps = conn.prepareStatement("CREATE TABLE " + Constants.SQLTable + "_Transactions(" + "id INT AUTO_INCREMENT PRIMARY KEY, " + "account_from TEXT, " + "account_to TEXT, " + "account_from_balance DECIMAL(64, 2), " + "account_to_balance DECIMAL(64, 2), " + "timestamp TEXT, " + "set DECIMAL(64, 2), " + "gain DECIMAL(64, 2), " + "loss DECIMAL(64, 2)" + ");");

                    ps.executeUpdate();
                } catch (SQLException E) {}
            } else {
                DatabaseMetaData dbm = conn.getMetaData();
                rs = dbm.getTables(null, null, Constants.SQLTable + "_Transactions", null);

                if (!rs.next()) {
                    System.out.println("[iConomy] Creating logging database.. [" + Constants.SQLTable + "_Transactions]");
                    ps = conn.prepareStatement("CREATE TABLE " + Constants.SQLTable + "_Transactions (" + "`id` INT(255) NOT NULL AUTO_INCREMENT, " + "`account_from` TEXT NOT NULL, " + "`account_to` TEXT NOT NULL, " + "`account_from_balance` DECIMAL(65, 2) NOT NULL, " + "`account_to_balance` DECIMAL(65, 2) NOT NULL, " + "`timestamp` TEXT NOT NULL, " + "`set` DECIMAL(65, 2) NOT NULL, " + "`gain` DECIMAL(65, 2) NOT NULL, " + "`loss` DECIMAL(65, 2) NOT NULL, " + "PRIMARY KEY (`id`)" + ");");

                    if (ps != null) {
                        ps.executeUpdate();
                        System.out.println("[iConomy] Database Created.");
                    }
                }
                System.out.println("[iConomy] Logging enabled.");
            }
        } else
            System.out.println("[iConomy] Logging is currently disabled.");

        if (ps != null)
            try {
                ps.close();
            } catch (SQLException ex) {}
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException ex) {}
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException ex) {}
    }

    public JdbcConnectionPool connectionPool() {
        return this.h2pool;
    }
}
