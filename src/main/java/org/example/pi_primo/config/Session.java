package org.example.pi_primo.config;

import org.example.pi_primo.model.Cliente;
import org.example.pi_primo.model.Produto;

public class Session {
    public static Cliente usuario= new Cliente(0,"NULL","NULL","NULL","NULL","NULL","NULL");
    public static Produto produto= new Produto(0,"NULL","NULL","NULL",0,0,"NULL","NULL",0);

    public static String pesquisa="";
    public static boolean pesquisando=false;
}
