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
import java.util.Iterator;
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
    private static final Map<String,Grupo> grupos = new HashMap<String,Grupo>(); //Lista de grupos
    //1 gerenciador para cada cliente
    public GerenciadorDeClientes(Socket cliente){
        this.cliente = cliente;
        start();
        
    }

    @Override
    public void run(){
        //Exceção pode ser causada pelo cliente ter fechado a conexão, falta de internet etc.
        try {
            String msg;
            //Pegando a mensagem from cliente
            //Usando BufferedReader para transformar os dados dos pacotes da mensagem de bytes para string
            leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            escritor = new PrintWriter(cliente.getOutputStream(), true); //Escrever os dados obtidos pela mensagem do cliente
            
            boolean adicionado = false;
            do{
                escritor.println("Digite seu nome"); //Segundo paramentro (AutoFlush) do PrintWriter = true, manda esta mensagem direto para o cliente
                msg = leitor.readLine();
                this.nomeCliente = msg.replaceAll(",", "");
                if(!clientes.containsKey(this.nomeCliente)){
                    escritor.println("Bem vindo " + this.nomeCliente);
                    escritor.println("Digite \"::help\" para lista de comandos disponiveis");
                    clientes.put(this.nomeCliente, this);
                    adicionado = true;
                }
            }while(!adicionado);
            while(true){
                msg = leitor.readLine();
                //Fecha conexão, caso for enviada a mensagem de solicitação de fechamento
                if(msg.equalsIgnoreCase("#bye")){
                    clientes.remove(this.nomeCliente);
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
                } else if(msg.equals("::listclients")){
                    StringBuilder str = new StringBuilder(); //Mesma coisa do StringBuffer
                    for(String c: clientes.keySet()){
                        str.append(c);
                        str.append(",");
                    }
                    str.delete(str.length()-1, str.length());
                    escritor.println(str.toString());
                }else if(msg.equals("::newgroup")){
                    escritor.println("Digite nome do grupo");
                    msg = leitor.readLine();
                    if(!grupos.containsKey(msg)){
                        Grupo novoGrupo = new Grupo("msg");
                        novoGrupo.clientes.put(this.nomeCliente,this);
                        grupos.put(msg, novoGrupo);//Criar grupo
                        System.out.println("Grupo " + msg + " criado e usuário " + this.getNomeCliente() + " adicionado");
                        escritor.println("Grupo " + msg + " criado");
                    }else{
                        escritor.println("Grupo " + msg + " já existe");
                    }
                    
                }else if(msg.equals("::entergroup")){
                     escritor.println("Digite nome do grupo");
                     String nomeGrupo = leitor.readLine();
                     System.out.println("Adicionando " + this.nomeCliente + " ao grupo " + nomeGrupo);
                     if(grupos.containsKey(nomeGrupo) && !grupos.get(nomeGrupo).clientes.containsKey(this.nomeCliente)){//Verifica se o grupo existe e se o usuario ja não está no grupo
                         grupos.get(nomeGrupo).clientes.put(this.nomeCliente,this);
                         escritor.println("Adicionado ao grupo");
                     }else{
                         escritor.println("Grupo inexistente");
                     }
                }else if(msg.startsWith("::group")){
                     String nomeGrupo = msg.substring(7, msg.length());
                     if(grupos.containsKey(nomeGrupo) && grupos.get(nomeGrupo).clientes.containsKey(this.nomeCliente)){//Verifica se o grupo existe e se o usuario está no grupo
                         System.out.println("Mensagem de "+ this.nomeCliente + " para o grupo " + nomeGrupo);
                         escritor.println("Digite uma mensagem para " + nomeGrupo);
                         msg = leitor.readLine();
                         for(Map.Entry<String,GerenciadorDeClientes> entry: grupos.get(nomeGrupo).clientes.entrySet()){
                             if(!entry.getKey().equals(this.nomeCliente))
                                entry.getValue().escritor.println("Grupo "+ nomeGrupo+ " - " +this.nomeCliente + " : " + msg);
                         }
                     }else{
                         System.out.println(this.nomeCliente + " tentou enviar uma mensagem não sucedida ao grupo " + nomeGrupo);
                         escritor.println("Grupo inexistente ou você não pertence à esse grupo");
                     }
                }else if(msg.equals("::help")){
                     escritor.println("#bye: Fecha conexão");
                     escritor.println("::listclients: Lista os usuários ativos");
                     escritor.println("::msg(User): Envia mensagem para o usuário(User)");
                     escritor.println("::newgroup: Cria novo grupo e adiciona o usuário ao grupo");
                     escritor.println("::entergroup: Insere usuário no grupo");
                     escritor.println("::group(Group): Envia mensagem para os usuários do grupo(Group)");
                }else{
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
