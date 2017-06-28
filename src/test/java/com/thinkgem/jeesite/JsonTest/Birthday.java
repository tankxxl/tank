package com.thinkgem.jeesite.JsonTest;

/**
 * Created by rgz on 09/06/2017.
 */
public class Birthday {

    private String birthday;

    public Birthday(String birthday) {
        super();
        this.birthday = birthday;
    }

    //getter、setter


    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Birthday() {}

    @Override
    public String toString() {
        return this.birthday;
    }
}
