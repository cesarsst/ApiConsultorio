package com.example.apiconsultorio.util.passwordEncoder;

import antlr.BaseAST;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordEncoder() {
    }

    public static void main(String[] args){

        String password = "cesar878795";
        String passwordEncode = passwordEncoder.encode(password);
        boolean isPasswordMatch = passwordEncoder.matches(password, passwordEncode);
        System.out.println("Password : " + password + "   isPasswordMatch    : " + isPasswordMatch);
        System.out.println(passwordEncode);
    }

    public boolean comparePassword(String password, String passwordEncode){
        boolean isPasswordMatch = passwordEncoder.matches(password, passwordEncode);
        return isPasswordMatch;
    }
}
