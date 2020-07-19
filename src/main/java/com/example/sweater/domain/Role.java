package com.example.sweater.domain;

import org.springframework.security.core.GrantedAuthority;

//Реализует интерфейс тк класс User реализует UserDetails
public enum Role implements GrantedAuthority {
    USER, ADMIN;

    //name() это строковое представление ролей
    @Override
    public String getAuthority() {
        return name();
    }
}
