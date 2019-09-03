package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("DataBase");
        primaryStage.setScene(new Scene(root, 600, 350));
        primaryStage.show();

        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }

        try
        {
            // JDBC URL для базы данных на Localhost
            String url = Const.URL;

            // здесь осуществляется соединение c login и password
            Properties properties = new Properties();
            properties.setProperty("user", "root");
            properties.setProperty("password", "password");//"PsM2l20FKz62cK3F");
            properties.setProperty("useSSL", "false");
            properties.setProperty("serverTimezone", "UTC");
            properties.setProperty("autoReconnect", "true");
            con = getConnection(url, properties);
            System.out.println("Connection ID" + con.toString());

            //  Формирование запросов к БД
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("use test;");
            System.out.println("\nUSING DB" + rs.toString());
//"update lessons set name = 'qwerty' where id=1;"
            int result = st.executeUpdate("delete from users where id > 2;");
            System.out.println("\nDeleted " + result + " records");

            result = st.executeUpdate("insert into users(id, login, password, name, regdate) values (null, 'user123', 'password123', 'User User User', '20090708212121');");
            System.out.println("\nInserted " + result + " records");

            PreparedStatement st1 = con.prepareStatement("insert into users(id, login, password, name, regdate) values (?, ?, ?, ?, ?);");
            double num1 = Math.random()*100;
            String date1 = "20190128080000";

            String str = String.valueOf(5+Math.round(num1));
            st1.setString(1, "4");
            st1.setString(2, "user" + str);
            st1.setString(3, "password" + str);
            st1.setString(4, "user" + str);
            st1.setString(5, date1);
            System.out.println("\nParametrized query" + st1.toString());

            result = st1.executeUpdate();
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


    public static void main(String[] args) {
        launch(args);
    }
}
