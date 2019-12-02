package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class Controller {
    @FXML
    public TableView<Users> table1;
    private ObservableList<Users> data;

    @FXML
    Button buttonLoadData;
    @FXML
    Button buttonAddData;
    @FXML
    TableColumn<Users, Integer> c1;
    @FXML
    TableColumn<Users, String> c2;
    @FXML
    TableColumn<Users, String> c3;
    @FXML
    TableColumn<Users, String> c4;
    @FXML
    TableColumn<Users, Date> c5;
    @FXML
    TableColumn<Users, String> c6;
    @FXML
    TitledPane header;
    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
    TextField name;
    @FXML
    DatePicker birthday;

    private Connection con = null;

    private boolean DBconnect(String conn, String login, String password) {
        boolean result = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            // JDBC URL для базы данных на Localhost
            String url = "jdbc:mysql://"+conn;

            // здесь осуществляется соединение c login и password
            Properties properties = new Properties();
            properties.setProperty("user", login);
            properties.setProperty("password", password);
            properties.setProperty("serverTimezone", "UTC");
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");

            con = getConnection(url, properties);
            System.out.println("Connection ID" + con.toString());
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    public void LoadData(ActionEvent actionEvent) {
        try
        {
            //  Формирование запросов к БД
            Statement st = con.createStatement();

            ResultSet rs;// = st.executeQuery("use itschool;");

//            rs = st.executeQuery("SELECT * FROM users;");
            rs = st.executeQuery("select u.id, u.name, u.login, u.password, u.regdate, c.city FROM users AS u LEFT JOIN cities AS c ON c.id = u.id_city;");
            System.out.println("\nEXECUTING QUERY TO DB: "+rs.toString()+"\n");

            data = FXCollections.observableArrayList();
/*
            c1.setCellValueFactory(new PropertyValueFactory<Users, Integer>("ID"));
            c2.setCellValueFactory(new PropertyValueFactory<Users, String>("login"));
            c3.setCellValueFactory(new PropertyValueFactory<Users, String>("password"));
            c4.setCellValueFactory(new PropertyValueFactory<Users, String>("name"));
            c5.setCellValueFactory(new PropertyValueFactory<Users, Date>("datereg"));
*/
            // Можно не уточнять тип
            c1.setCellValueFactory(new PropertyValueFactory<>("ID"));
            c2.setCellValueFactory(new PropertyValueFactory<>("login"));
            c3.setCellValueFactory(new PropertyValueFactory<>("password"));
            c4.setCellValueFactory(new PropertyValueFactory<>("name"));
            c5.setCellValueFactory(new PropertyValueFactory<>("datereg"));
            c6.setCellValueFactory(new PropertyValueFactory<>("city"));

            while (rs.next())
            {
                // System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4) + "\t" + rs.getString(5));
                data.add(new Users(rs.getInt("id"), rs.getString("login"), Encrypt(rs.getString("password"), -3), rs.getString("name"), rs.getDate("regdate"), rs.getString("city")));
            }
            table1.setItems(data);

            rs.close();
            st.close();
        //  Закончили запрос
        } catch (SQLException e)
        {
            header.setText("Error while loading data!!!");
            e.printStackTrace();
        }
    }

    public void Exit(ActionEvent actionEvent) {
        if (con != null)
        {
            try {
                con.close();
                header.setText("Disconnected successfully");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void ConnectDB(ActionEvent actionEvent) {
        if (DBconnect(Const.SERVER + "/test", "root", "password"))
            header.setText("Connected sucesfully");
        else
            header.setText("Didn't connected!!!");
    }

    public void AddData(ActionEvent actionEvent)
    {
        String logins = "";
        String passw = "";
        String names = "";
        String birth = "";

        try {
            logins = login.getText();
            passw = password.getText();
            names = name.getText();
            birth = birthday.getValue().format(DateTimeFormatter.BASIC_ISO_DATE);
            System.out.println(birthday.getValue());
            System.out.println(birth);
        } catch (Exception exc) {
            MessageBox(Alert.AlertType.ERROR, "Ошибка ввода данных", "Не все поля заполнены");
            return;
        }

        try
        {
            // здесь осуществляется соединение c login и password
            Properties properties = new Properties();
            properties.setProperty("user", "root");
            properties.setProperty("password", "password");
            properties.setProperty("serverTimezone", "UTC");
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");
            con = getConnection(Const.URL, properties);

            //  Формирование запросов к БД
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("use test;");

            PreparedStatement st1 = con.prepareStatement("insert into users(login, password, name, regdate) values (?, ?, ?, ?);");
            st1.setString(1, logins);
            st1.setString(2, Encrypt(passw, 3));
            st1.setString(3, names);
            st1.setString(4, birth);
            System.out.println("\nParametrized query" + st1.toString());

            int result = st1.executeUpdate();
            System.out.println("\nInserted " + result + " records");

            rs.close();
            st.close();

            LoadData(actionEvent);
            // MessageBox(Alert.AlertType.NONE, "Encrypted text", Encrypt("ABC", 3));
//  Закончили запрос
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( con != null )
            {
                try
                {
                    // con.close();
                }
                catch( Exception e ) { }
            }
        }
    }

    private void MessageBox(Alert.AlertType type, String info, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(alert.getAlertType().toString());
        alert.setHeaderText(info);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public String Encrypt(String text, int key) {
        String result = "";
        char[] array = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            //result += ""+(char)(((int)array[i])+3);
            result += (char) (array[i] + key);
        }
        return result;
    }
}