package sample;

import java.util.Date;

public class Users {
    private int ID;
    private String login;
    private String password;
    private String name;
    private Date datereg;

    public Users(int id, String login, String password, String name, Date datereg) {
        ID = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.datereg = datereg;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDatereg() {
        return datereg;
    }

    public void setDatereg(Date datereg) {
        this.datereg = datereg;
    }
}
