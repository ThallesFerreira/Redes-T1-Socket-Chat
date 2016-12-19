//Disciplina: Redes de Computadores
//Trabalho 1
//Professor: Fredy Valente
//Autores: Gabriel Martins, Pedro Garcia e Thalles Ferreira

package redes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteSocket{ 
    public static void main(String[] args){
        try {
            final Socket cliente = new Socket("127.0.0.1", 5555); //host, port = 127001 (própria máquina), 5555(porta conectada no servidor)
            
            //Lendo mensagens do servidor
            
            //Thread para permitir escrita e recebimento de mensagem simultaneamente
            new Thread(){
                @Override
                public void run(){
                    try {
                        //Lê o que o servidor ou outro cliente está escrevendo
                        BufferedReader leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                        
                        while(true){
                            String msg = leitor.readLine();
                            if(msg == null || msg.length() == 0)
                                continue;
                            System.out.println((char)27 + "[32m$" + msg + (char)27 + "[0m");
                        }
                    } catch (IOException ex) {
                        System.out.println("Não foi possível ler a mensagem do servidor");
                        ex.printStackTrace();
                      //Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start(); //inicia a thread
            
            
            //Escrevendo para o servidor
            
            PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader leitorTerminal = new BufferedReader(new InputStreamReader(System.in));
            String msgTerminal = "";
            while(true){
                msgTerminal = leitorTerminal.readLine();
                if(msgTerminal == null || msgTerminal.length() == 0)
                    continue; //Passa para a próxima execução do while
                escritor.println(msgTerminal);
                //Cliente solicitou fechamento da conexão
                if(msgTerminal.equalsIgnoreCase("#bye"))
                    System.exit(0);    
            }
            
        } catch (UnknownHostException ex1){
            System.out.println("Endereço passado inválido!");
            ex1.printStackTrace();
        } catch (IOException ex2) {
            System.out.println("Servidor está fora do ar");
            ex2.printStackTrace();
            //Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
