package com.soerdev.sims.models;

public class Admin {
    public String emailUser, namaUser;

    public Admin() {

    }

    public Admin(String emailUser, String namaUser) {
        this.emailUser = emailUser;
        this.namaUser = namaUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }
}
