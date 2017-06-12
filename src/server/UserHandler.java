package server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Filip on 21.05.2017.
 */
public class UserHandler implements Runnable{

    boolean cond=true;
    Socket skt;
    Server server;

    public DataOutputStream getOutput() {
        return Output;
    }

    DataInputStream Input;
    DataOutputStream Output;
    UserHandler(Socket s, Server server)
    {
        skt=s;
        this.server=server;
    }

    @Override
    public void run() {

        try{
            //Próba połączenia z serwerem

            Output = new DataOutputStream(new BufferedOutputStream(skt.getOutputStream()));
            Input = new DataInputStream(new BufferedInputStream(skt.getInputStream()));
            //Opcje odczytu i zapisu z i do strumienia
            //Output.writeUTF(" no elo");
            //Output.flush();
            Log();
            Thread reader = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (cond) {
                        String buf=null;
                        try {
                            buf = Input.readUTF();
                            switch (buf.charAt(0)) {
                                case '1':

                                    break;
                                case '2':

                                    break;
                                case '3':
                                    close();
                                    break;
                                case '4':
                                    StringBuilder sb = new StringBuilder(buf);
                                    sb.deleteCharAt(0);
                                    buf=sb.toString();
                                    System.out.println(buf);
                                    server.message(buf);
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Thread writer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (cond) {

                    }
                }
            });

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.submit(reader);
            executorService.submit(writer);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Uuuups, coś się skopało. nie podziałam!");
        }

    }

    public void close() throws IOException {
        System.out.println("Rozlaczono");
        server.message("Klient sie rozlaczyl");
        server.logout(this);
        cond=false;
    }

    void Log() throws IOException {
        boolean correct=false;
        while(!correct) {
            //Output.writeUTF("Podaj usera");
            //Output.flush();
            String login;
            login = Input.readUTF();
            if(login.equals(3))
                close();
            //Output.writeUTF("Podaj haslo");
            //Output.flush();
            String password;
            password = Input.readUTF();
            int index;
            for (String user : server.getUsers()) {
                if (user.equals(login)) {
                    index = server.getUsers().indexOf(user);
                    if (server.getPasswds().get(index).equals(password)&&!server.getLogged().get(index)) {
                        Output.writeInt(1);
                        Output.flush();
                        correct = true;
                        server.login(index);
                    }
                    else if(server.getPasswds().get(index).equals(password)) {
                        Output.writeInt(2);
                        Output.flush();
                    }
                    else
                    {
                        Output.writeInt(-1);
                        Output.flush();
                    }
                }
            }
        }

    }

}
