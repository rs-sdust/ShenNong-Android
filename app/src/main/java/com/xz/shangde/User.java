package com.xz.shangde;

import java.io.Serializable;

/**
 * Created by yxq on 2018/5/8.
 */

public class User implements Serializable {

    private int User_ID;
    private String User_Name;
    private String User_PhoneNumber;
    private String User_Password;
    private int User_Role;
    private int User_Farm;
    private String User_Icon;

    public User(int user_ID, String user_Name, String user_PhoneNumber, String user_Password, int
            user_Role, int user_Farm, String user_Icon) {
        User_ID = user_ID;
        User_Name = user_Name;
        User_PhoneNumber = user_PhoneNumber;
        User_Password = user_Password;
        User_Role = user_Role;
        User_Farm = user_Farm;
        User_Icon = user_Icon;
    }

    public User(String user_Name, String user_PhoneNumber, String user_Password, int user_Role,
                int user_Farm, String user_Icon) {
        User_Name = user_Name;
        User_PhoneNumber = user_PhoneNumber;
        User_Password = user_Password;
        User_Role = user_Role;
        User_Farm = user_Farm;
        User_Icon = user_Icon;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_PhoneNumber() {
        return User_PhoneNumber;
    }

    public void setUser_PhoneNumber(String user_PhoneNumber) {
        User_PhoneNumber = user_PhoneNumber;
    }

    public String getUser_Password() {
        return User_Password;
    }

    public void setUser_Password(String user_Password) {
        User_Password = user_Password;
    }

    public int getUser_Role() {
        return User_Role;
    }

    public void setUser_Role(int user_Role) {
        User_Role = user_Role;
    }

    public int getUser_Farm() {
        return User_Farm;
    }

    public void setUser_Farm(int user_Farm) {
        User_Farm = user_Farm;
    }

    public String getUser_Icon() {
        return User_Icon;
    }

    public void setUser_Icon(String user_Icon) {
        User_Icon = user_Icon;
    }
}
