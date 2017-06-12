package server;

import javafx.application.Application;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Filip on 20.05.2017.
 */
public class Server
{
    public List<Boolean> getLogged() {
        return logged;
    }

    public List<String> getUsers() {
        return users;
    }

    public List<String> getPasswds() {
        return passwds;
    }
    public void addUser(String login, String passwd)
    {
        users.add(login);
        passwds.add(passwd);
        logged.add(false);
    }
    public void login(int index)
    {
        logged.set(index, true);
    }

    public void logout(int index)
    {
        logged.set(index, false);
    }

    List<UserHandler> handlers = new LinkedList<>();
    ServerSocket serverSocket;
    List<String> users = new ArrayList<>();
    List<String> passwds = new ArrayList<>();
    List<Boolean> logged = new ArrayList<>();

     boolean cond=true;
        public Server() throws IOException {
            serverSocket = new ServerSocket(999);
            addUser("root","asdf");
            addUser("root1","asdf");

        }

        public void start() throws IOException {

            System.out.println("Serwer: Start na hoście-"
                    + InetAddress.getLocalHost().getCanonicalHostName()
                    + " port: " + serverSocket.getLocalPort());

            while (cond) {
                Socket client = serverSocket.accept();
                System.out.println("Accepted from " + client.getInetAddress());
                UserHandler u = new UserHandler(client,this);
                handlers.add(u);
                u.run();
            }
        }

        void close() throws IOException {
            cond=false;
            serverSocket.close();
        }
        void logout(UserHandler h){
            handlers.remove(h);
        }
        void message(String msg) throws IOException {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            Thread writer = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (UserHandler h :handlers)
                    {
                    DataOutputStream Output = new DataOutputStream(h.getOutput());
                    try {
                        Output.writeUTF(msg);
                        Output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                });
                executorService.submit(writer);
            }
        }
