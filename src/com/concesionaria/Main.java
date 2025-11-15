package com.concesionaria;

import com.concesionaria.negocio.GestorConcesionaria;
import com.concesionaria.presentacion.MenuPrincipal;

public class Main {
    public static void main(String[] args) {
        GestorConcesionaria gestor = new GestorConcesionaria();
        MenuPrincipal menu = new MenuPrincipal(gestor);
        menu.iniciar();
    }
}