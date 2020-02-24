package model;

public class Account {
    String userName;
    int id;

    public Account(String userName, int id) {
        this.userName = userName;
        this.id = id;
    }

    //getters
    public String getUserName() {
        return userName;
    }

    public int getId() {
        return id;
    }
}
