//Disciplina: Redes de Computadores
//Trabalho 1
//Professor: Fredy Valente
//Autores: Gabriel Martins, Pedro Garcia e Thalles Ferreira

package redes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorSocket {
    public static void main(String[] args){
        //Ajustando escopo para o catch...
        ServerSocket servidor = null;
        try {
            System.out.println("Iniciando o servidor...");
            servidor = new ServerSocket(5555); //Tentando se conectar pela porta 5555 (exemplo)
            System.out.println("Servidor iniciado!");
            
            //Esperando/aceitando novas conexões
            while(true){            
                Socket cliente = servidor.accept(); //Espera clientes se conectarem. Caso algum se conecte, cria um socket
                new GerenciadorDeClientes(cliente); //Nova conexao = novo gerenciador de clientes
            }
        } catch (IOException ex) {
            try{
                if(servidor != null)
                    //Fechar servidor em caso de exception
                    servidor.close();
            } catch (IOException ex1){
                
            }
            //Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Porta ocupada ou servidor fechado");
            ex.printStackTrace();
        }
        
    }
}
