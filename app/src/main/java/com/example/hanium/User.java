package com.example.hanium;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private  String userID;
    private  String userPassword;
    private ArrayList<String> carNum;

    User(String userID,String userPassword){
        this.userID = userID;
        this.userPassword = userPassword;
    }

    void addCar(String car){
        carNum.add(car);
    }
    String getUserID(){
        return this.userID;
    }
    String getUserPassword(){
        return this.userPassword;
    }
    ArrayList<String> getCarNum(){
        return  carNum;
    }

}
