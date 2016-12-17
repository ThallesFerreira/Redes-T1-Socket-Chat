//Disciplina: Redes de Computadores
//Trabalho 1
//Professor: Fredy Valente
//Autores: Gabriel Martins, Pedro Garcia e Thalles Ferreira

package redes;

import com.sun.imageio.plugins.common.InputStreamAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thalles
 */

//Gerencia a comunicação cliente-servidor e cliente-cliente
//Thread permite usar múltipos gerenciadores de cliente
public class GerenciadorDeClientes extends Thread{
    private Socket cliente;
    private String nomeCliente;
    private BufferedReader leitor;
    private PrintWriter escritor;
    private static final Map<String,GerenciadorDeClientes> clientes = new HashMap<String,GerenciadorDeClientes>(); //Lista de clientes
    
    //1 gerenciador para cada cliente
    public GerenciadorDeClientes(Socket cliente){
        this.cliente = cliente;
        start();
        
    }

    @Override
    public void run(){
        //Exceção pode ser causada pelo cliente ter fechado a conexão, falta de internet etc.
        try {
            //Pegando a mensagem from cliente
            //Usando BufferedReader para transformar os dados dos pacotes da mensagem de bytes para string
            leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            escritor = new PrintWriter(cliente.getOutputStream(), true); //Escrever os dados obtidos pela mensagem do cliente
            escritor.println("Digite seu nome"); //Segundo paramentro (AutoFlush) do PrintWriter = true, manda esta mensagem direto para o cliente
            String msg = leitor.readLine();
            this.nomeCliente = msg.toLowerCase().replaceAll(",", "");
            escritor.println("Bem vindo " + this.nomeCliente);
            clientes.put(this.nomeCliente, this);
            
            while(true){
                msg = leitor.readLine();
                //Fecha conexão, caso for enviada a mensagem de solicitação de fechamento
                if(msg.equalsIgnoreCase("#bye")){
                    this.cliente.close();
                } else if(msg.toLowerCase().startsWith("::msg")){
                    String nomeDestinatario = msg.substring(5, msg.length());
                    System.out.println("Enviando para " + nomeDestinatario);
                    GerenciadorDeClientes destinatario = clientes.get(nomeDestinatario);
                    if(destinatario == null){
                        escritor.println("Cliente inexistente");
                    } else{
                        escritor.println("Digite uma mensagem para " + destinatario.getNomeCliente());
                        destinatario.getEscritor().println(this.nomeCliente + " disse: " + leitor.readLine());
                    }
                //Lista o nome de todos os clientes conectados
                } else if(msg.equals("::listar-clientes")){
                    StringBuilder str = new StringBuilder(); //Mesma coisa do StringBuffer
                    for(String c: clientes.keySet()){
                        str.append(c);
                        str.append(",");
                    }
                    str.delete(str.length()-1, str.length());
                    escritor.println(str.toString());
                } else{
                    escritor.println(this.nomeCliente + " disse: " + msg);
                }
            }
        } catch (IOException ex) {
            System.err.println("Cliente fechou a conexão");
            ex.printStackTrace();
            //Logger.getLogger(GerenciadorDeClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public PrintWriter getEscritor(){
        return escritor;
    }
    
    public String getNomeCliente(){
        return nomeCliente;
    }
    
}
