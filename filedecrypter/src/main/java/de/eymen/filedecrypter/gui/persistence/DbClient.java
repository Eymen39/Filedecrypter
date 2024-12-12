package de.eymen.filedecrypter.gui.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.eymen.filedecrypter.gui.domain.domain.*;

//Singlenton pattern
public class DbClient {
    String url = "jdbc:sqlite:my.db";
    private static DbClient instance;

    private DbClient() {
    }

    public static DbClient getInstance() {
        if (instance == null) {
            instance = new DbClient();
        }
        return instance;
    }

    public void createPublicKeyTable() throws SQLException {

        try (Connection conn = DriverManager.getConnection(url)) {

            if (conn != null) {

                String doThing = "CREATE TABLE IF NOT EXISTS publicKeys (\n"
                        + "id TEXT PRIMARY KEY, \n"
                        + "key TEXT NOT NULL\n"
                        + ");";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(doThing);

                }

            }

        } catch (SQLException e) {
            throw new SQLException();
        }

    }

    public void createSignedPublicKeyTable() throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {

            if (conn != null) {

                String doThing = "CREATE TABLE IF NOT EXISTS signedPublicKeys (\n"
                        + "id TEXT PRIMARY KEY, \n"
                        + "userId TEXT NOT NULL, \n"
                        + "publicKey TEXT NOT NULL, \n"
                        + "hash TEXT NOT NULL"
                        + ");";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(doThing);

                }

            }

        } catch (SQLException e) {
            throw new SQLException();
        }

    }

    public void createUserTable() throws SQLException {

        try (Connection conn = DriverManager.getConnection(url)) {

            if (conn != null) {

                String doThing = "CREATE TABLE IF NOT EXISTS users (\n"
                        + "id TEXT PRIMARY KEY, \n"
                        + "name TEXT NOT NULL, \n"
                        + "privateKey TEXT NOT NULL, \n"
                        + "password TEXT NOT NULL,"
                        + "notarFlag BOOLEAN NOT NULL"
                        + ");";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(doThing);

                }

            }

        } catch (SQLException e) {
            throw new SQLException();
        }

    }

    public void createDataAccessTable() throws SQLException {

        try (Connection conn = DriverManager.getConnection(url)) {

            if (conn != null) {
                String tableCreator = "CREATE TABLE IF NOT EXISTS dataAccess (\n"
                        + "id TEXT PRIMARY KEY NOT NULL, \n"
                        + "hashName TEXT NOT NULL, \n"
                        + "name TEXT NOT NULL, \n "
                        + "KeyConstruct TEXT NOT NULL, \n"
                        + "masterid TEXT NOT NULL, \n"
                        + "slaveid TEXT NOT NULL"
                        + ");";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(tableCreator);

                }
            }

        } catch (SQLException ex) {
            throw new SQLException();
        }
    }

    public void deleteTable(String table) {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sqlStatement = "DROP TABLE " + table;
            Statement statement = conn.createStatement();
            statement.executeUpdate(sqlStatement);

        } catch (SQLException ex) {
        }
    }

    public void removeUser(User user) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement(
                            "DELETE FROM users WHERE id = ?");
            statement.setString(1, user.getId());
            int rowsDeleted = statement.executeUpdate();

            System.out.println("rows deleted: " + rowsDeleted);

        } catch (SQLException ex) {
            throw new SQLException();
        }

        System.out.println("User deleted from Database");
    }

    public void insertUser(User user) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement(
                            "INSERT INTO users (id, name, privateKey, password, notarFlag) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getPrivateKey());
            statement.setString(4, user.getPassword());
            statement.setBoolean(5, user.getNotarFlag());
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
    }

    public String getNotar() throws SQLException {
        ResultSet rs = null;

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM publicKeys WHERE id = ?");
            statement.setString(1, "0000");
            rs = statement.executeQuery();
            if (rs != null) {
                return rs.getString("key");
            } else {
                throw new SQLException("no entries found");
            }

        } catch (SQLException ex) {
            throw new SQLException();

        }
    }

    public void insertPublicKey(String id, String publicKey) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("INSERT INTO publicKeys (id, key) VALUES (?, ?)");
            statement.setString(1, id);
            statement.setString(2, publicKey);
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
    }

    public void insertSignedKey(String publicKey, String hash, String userId) throws SQLException {

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement(
                            "INSERT INTO signedPublicKeys (id, userId, publicKey, hash ) VALUES (?, ?, ?, ?)");
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, userId);
            statement.setString(3, publicKey);
            statement.setString(4, hash);
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
    }

    public String getPublicKey(User user) throws SQLException {
        ResultSet rs = null;
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM publicKeys WHERE id = ?");
            statement.setString(1, user.getId());
            rs = statement.executeQuery();
            if (rs != null) {
                return rs.getString("key");
            } else {
                throw new SQLException("no entries found");
            }

        } catch (SQLException ex) {
            throw new SQLException();

        }
    }

    public Map<String, String> getAllPublicKeys() throws SQLException {
        ResultSet rs = null;
        Map<String, String> publicKeyMap = new HashMap<String, String>();
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM publicKeys");
            rs = statement.executeQuery();
            while (rs.next()) {
                publicKeyMap.put(rs.getString("id"), rs.getString("key"));
            }
            return publicKeyMap;
        } catch (SQLException ex) {
            throw new SQLException();

        }
    }

    public void getSignedPublicKeys(User user) throws SQLException {
        ResultSet rs = null;
        Map<String, String> map = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM signedPublicKeys WHERE userId = ?");
            statement.setString(1, user.getId());
            rs = statement.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("publicKey"), rs.getString("hash"));
            }

            user.setPublicKeys(map);
        } catch (SQLException ex) {
            throw new SQLException();
        }
    }

    public String getPrivateKey(User user) throws SQLException {
        ResultSet rs = null;
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setString(1, user.getId());
            rs = statement.executeQuery();
            if (rs != null) {
                return rs.getString("privateKey");
            } else {
                throw new SQLException("no entries found");
            }

        } catch (SQLException ex) {
            throw new SQLException();

        }

    }

    public ArrayList<String> getUserNames() throws SQLException {
        ArrayList<String> aL = new ArrayList<>();
        ResultSet rs = null;
        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT name FROM users WHERE name != 'notar'");
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    aL.add(rs.getString("name"));
                }
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }

        return aL;

    }

    public User getLoginData(String name) throws SQLException {
        ResultSet rs = null;
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM users WHERE name = ?");
            statement.setString(1, name);
            rs = statement.executeQuery();
            if (rs != null) {
                User newUser = new User(rs.getString("name"), rs.getString("id"), rs.getString("password"));
                newUser.setPassword(rs.getString("password"));
                newUser.setPrivateKey(rs.getString("privateKey"));
                newUser.setNotarFlag(rs.getBoolean("notarFlag"));
                return newUser;
            } else {
                throw new SQLException("no entries found");
            }

        } catch (SQLException ex) {
            throw new SQLException();

        }

    }

    public void updatePublicKey(String userId, String newPublicKey) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("UPDATE publicKeys SET key = ? WHERE id = ?");
            statement.setString(1, newPublicKey);
            statement.setString(2, userId);
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }
    }

    public void insertNewDataAccess(DataFile file) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement(
                            "INSERT INTO dataAccess (id, hashName,  name, KeyConstruct, masterid, slaveid ) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, file.hashname);
            statement.setString(3, file.name);
            statement.setString(4, file.AESkey);
            statement.setString(5, file.MasterId);
            statement.setString(6, file.SlaveId);
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }

    }

    public void insertNewDataAccess(String hash, String encryptedName, String encryptedKey, String masterid)
            throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement(
                            "INSERT INTO dataAccess (id, hashName,  name, KeyConstruct, masterid, slaveid ) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, hash);
            statement.setString(3, encryptedName);
            statement.setString(4, encryptedKey);
            statement.setString(5, masterid);
            statement.setString(6, masterid);
            statement.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();
        }

    }

    public int removeDataAccess(User user, String hashName) throws SQLException {

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement(
                            "DELETE FROM dataAccess where (hashName = ?) AND (slaveId = ?) ");
            statement.setString(1, hashName);
            statement.setString(2, user.getId());
            return statement.executeUpdate();

        } catch (SQLException ex) {
            throw new SQLException();

        }

    }

    public DataFile getDataAccess(String hashName, String userId) throws SQLException {

        ResultSet rs = null;
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM dataAccess WHERE (hashName = ?) AND (slaveid = ?)  ");
            statement.setString(1, hashName);
            statement.setString(2, userId);
            rs = statement.executeQuery();
            if (rs != null) {
                String fileNameHashed = rs.getString("hashName");
                if (fileNameHashed == null) {
                    return null;
                }
                DataFile datadao = new DataFile();
                datadao.hashname = fileNameHashed;
                datadao.name = rs.getString("name");
                datadao.AESkey = rs.getString("KeyConstruct");
                datadao.MasterId = rs.getString("masterid");
                datadao.SlaveId = rs.getString("slaveid");

                return datadao;
            }

        } catch (SQLException ex) {
            throw new SQLException();

        }
        return null;

    }

    public ArrayList<String> getUserFileNames(String uid) throws SQLException {

        ArrayList<String> aL = new ArrayList<>();
        ResultSet rs = null;
        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT name FROM dataAccess where slaveid=?");
            statement.setString(1, uid);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    aL.add(rs.getString("name"));
                }
            }
        } catch (Exception e) {
            throw new SQLException();
        }

        return aL;

    }

    public void deleteDataMaster(User user, String hashName) throws SQLException {

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement statement = conn
                    .prepareStatement("DELETE FROM dataAccess where ( masterid=?)AND (hashname=?) ");
            statement.setString(1, user.getId());
            statement.setString(2, hashName);
            statement.executeUpdate();

        } catch (Exception e) {
            throw new SQLException();
        }

    }

    public void deleteDataSlave(User user, String hashName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement statement = conn
                    .prepareStatement("DELETE FROM dataAccess where ( slaveId=?)AND (hashname=?) ");
            statement.setString(1, user.getId());
            statement.setString(2, hashName);
            statement.executeUpdate();

        } catch (Exception e) {
            throw new SQLException();
        }
    }

    public User getUser(String uuid) throws SQLException {
        ResultSet rs = null;
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setString(1, uuid);
            rs = statement.executeQuery();
            if (rs != null) {
                User newUser = new User(rs.getString("name"), rs.getString("id"), rs.getString("password"));
                newUser.setPassword(rs.getString("password"));
                newUser.setPrivateKey(rs.getString("privateKey"));
                newUser.setNotarFlag(rs.getBoolean("notarFlag"));
                return newUser;
            } else {
                throw new SQLException("no entries found");
            }

        } catch (SQLException ex) {
            throw new SQLException();
        }

    }

    public ArrayList<DataFile> getDataAccessOf1File(String dateiName) throws SQLException {

        ArrayList<DataFile> aL = new ArrayList<>();
        ResultSet rs = null;
        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement statement = conn.prepareStatement("SELECT * FROM dataAccess where hashname=?");
            statement.setString(1, dateiName);
            rs = statement.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    DataFile data = new DataFile();
                    data.hashname = dateiName;
                    data.name = rs.getString("name");
                    data.AESkey = rs.getString("KeyConstruct");
                    data.MasterId = rs.getString("masterid");
                    data.SlaveId = rs.getString("slaveid");

                    aL.add(data);

                }
                return aL;
            }
            return null;
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

}
