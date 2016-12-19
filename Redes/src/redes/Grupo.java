/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redes;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gabri
 */
public class Grupo {
    private String nomeGrupo;
    static final Map<String,GerenciadorDeClientes> clientes = new HashMap<String,GerenciadorDeClientes>(); //Lista de clientes

    public Grupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }
    
    
    
    
}
