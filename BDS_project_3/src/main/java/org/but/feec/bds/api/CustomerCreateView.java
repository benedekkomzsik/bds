package org.but.feec.bds.api;

import java.util.Arrays;

public class CustomerCreateView {
    private String username;
    private char[] password;

    public String getUsername(){return username; }
    public void setUsername(String username){
        this.username = username;
    }

    public char[] getPassword(){return password;}
    public void setPassword(char[] password){
        this.password = password;
    }

    @Override
    public String toString(){
        return "CustomerCreateView(" + "username='" + username +'\'' + ", password='" + Arrays.toString(password) + '}';
    }
}
