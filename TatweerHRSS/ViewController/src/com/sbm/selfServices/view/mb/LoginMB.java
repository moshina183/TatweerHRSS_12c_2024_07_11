package com.sbm.selfServices.view.mb;

import com.sbm.selfServices.view.utils.JSFUtils;

public class LoginMB {
    public LoginMB() {
        super();
    }
    
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String login() {
        if(password!=null && password.equalsIgnoreCase("welcome@4i")){
            JSFUtils.setExpressionValue("#{sessionScope.comingFrom}","LOGIN");
            JSFUtils.setExpressionValue("#{sessionScope.jwt}",null);
            return "filmStrip";
        }
        else{
            JSFUtils.addFacesErrorMessage("You don't have access to this screen");
            return null;
        }
    }
}
