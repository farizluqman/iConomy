package com.iConomy.system;

import com.iConomy.events.AccountResetEvent;
import com.iConomy.events.AccountSetEvent;
import com.iConomy.events.AccountUpdateEvent;
import com.iConomy.iConomy;
import com.iConomy.util.Constants;
import com.iConomy.util.Misc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class Holdings {
    private String name = "";
    private boolean bank = false;
    private int bankId = 0;

    public Holdings(String name) {
        this.name = name;
    }

    public Holdings(int id, String name) {
        this.bankId = id;
        this.name = name;
    }

    public Holdings(int id, String name, boolean bank) {
        this.bank = bank;
        this.bankId = id;
        this.name = name;
    }

    public boolean isBank() {
        return this.bank;
    }

    public double balance() {
        return get();
    }

    private double get() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Double balance = Double.valueOf(Constants.Holdings);
        try {
            conn = iConomy.getiCoDatabase().getConnection();

            if (this.bankId == 0) {
                ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + " WHERE username = ? LIMIT 1");
                ps.setString(1, this.name);
            } else {
                ps = conn.prepareStatement("SELECT * FROM " + Constants.SQLTable + "_BankRelations WHERE account_name = ? AND bank_id = ? LIMIT 1");
                ps.setString(1, this.name);
                ps.setInt(2, this.bankId);
            }

            rs = ps.executeQuery();

            if (rs.next())
                balance = Double.valueOf(this.bankId == 0 ? rs.getDouble("balance") : rs.getDouble("holdings"));
        } catch (Exception ex) {
            System.out.println("[iConomy] Failed to grab holdings: " + ex);
        } finally {
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
        return balance.doubleValue();
    }

    public void set(double balance) {
        AccountSetEvent Event;
        try {
            Event = new AccountSetEvent(this.name, balance);
            iConomy.getBukkitServer().getPluginManager().callEvent(Event);
        } catch (Exception e) {
            // fix to check if this function was run from a asynchronous thread
            Event = new AccountSetEvent(this.name, balance, true);
            iConomy.getBukkitServer().getPluginManager().callEvent(Event);
        }


        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = iConomy.getiCoDatabase().getConnection();

            if (this.bankId == 0) {
                ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + " SET balance = ? WHERE username = ?");
                ps.setDouble(1, balance);
                ps.setString(2, this.name);
            } else {
                ps = conn.prepareStatement("UPDATE " + Constants.SQLTable + "_BankRelations SET holdings = ? WHERE account_name = ? AND bank_id = ?");
                ps.setDouble(1, balance);
                ps.setString(2, this.name);
                ps.setInt(3, this.bankId);
            }

            ps.executeUpdate();
        } catch (Exception ex) {
            System.out.println("[iConomy] Failed to set holdings: " + ex);
        } finally {
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException ex) {}
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException ex) {}
        }
    }

    public void add(double amount) {
        double balance = get();
        double ending = balance + amount;

        math(amount, balance, ending);
    }

    public void subtract(double amount) {
        double balance = get();
        double ending = balance - amount;

        math(amount, balance, ending);
    }

    public void divide(double amount) {
        double balance = get();
        double ending = balance / amount;

        math(amount, balance, ending);
    }

    public void multiply(double amount) {
        double balance = get();
        double ending = balance * amount;

        math(amount, balance, ending);
    }

    public void reset() {
        AccountResetEvent Event = new AccountResetEvent(this.name);
        iConomy.getBukkitServer().getPluginManager().callEvent(Event);

        if (!Event.isCancelled())
            set(Constants.Holdings);
    }

    private void math(double amount, double balance, double ending) {
        try {
            AccountUpdateEvent Event = new AccountUpdateEvent(this.name, balance, ending, amount);
            iConomy.getBukkitServer().getPluginManager().callEvent(Event);

            if (!Event.isCancelled())
                set(ending);
        } catch (Exception e) {
            // fix to check if this function was run from a asynchronous thread
            AccountUpdateEvent Event = new AccountUpdateEvent(this.name, balance, ending, amount, true);
            iConomy.getBukkitServer().getPluginManager().callEvent(Event);

            if (!Event.isCancelled())
                set(ending);
        }
    }

    public boolean isNegative() {
        return get() < 0.0D;
    }

    public boolean hasEnough(double amount) {
        return amount <= get();
    }

    public boolean hasOver(double amount) {
        return amount < get();
    }

    public boolean hasUnder(double amount) {
        return amount > get();
    }

    public String toString() {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        Double balance = Double.valueOf(get());
        String formatted = formatter.format(balance);

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        if (this.bankId == 0) {
            return Misc.formatted(formatted, Constants.Major, Constants.Minor);
        }

        Bank b = new Bank(this.bankId);
        return Misc.formatted(formatted, b.getMajor(), b.getMinor());
    }
}
