package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
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

            rs = st.executeQuery("select * from users;");
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

            while (rs.next())
            {
                System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4) + "\t" + rs.getString(5));
                data.add(new Users(rs.getInt(1), rs.getString(2) ,rs.getString(3), rs.getString(4), rs.getDate(5)));
            }
            table1.setItems(data);

            rs.close();
            st.close();
        //  Закончили запрос
        }
        catch( SQLException e )
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
        if (DBconnect(Const.server+"/itschool", "itschool", "PsM2l20FKz62cK3F"))
            header.setText("Connected sucesfully");
        else
            header.setText("Didn't connected!!!");
    }

    public void AddData(ActionEvent actionEvent)
    {
        try
        {
            // здесь осуществляется соединение c login и password
            Properties properties = new Properties();
            properties.setProperty("user", "itschool");
            properties.setProperty("password", "PsM2l20FKz62cK3F");
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");
            con = getConnection(Const.url, properties);

            //  Формирование запросов к БД
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("use itschool;");

            PreparedStatement st1 = con.prepareStatement("insert into users(login, password, name, regdate) values (?, ?, ?, ?);");
            st1.setString(1, login.getText());
            st1.setString(2, password.getText());
            st1.setString(3, name.getText());
            st1.setString(4, "20190128194314");
            System.out.println("\nParametrized query" + st1.toString());

            int result = st1.executeUpdate();
            System.out.println("\nInserted " + result + " records");

            rs.close();
            st.close();
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
                    con.close();
                }
                catch( Exception e ) { }
            }
        }
    }
}