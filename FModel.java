import javax.mail.Authenticator;
import javax.mail.*;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

public class FModel implements Runnable, Serializable {
    public boolean isActive = true;
    public boolean isOpen;
    public boolean popup = false;
    public boolean display = false;
    public boolean both = true;
    public ArrayList<String> ip = new ArrayList<>();
    public ArrayList<String> port = new ArrayList<>();
    public String toReturn;
    public int checkTime = 1;
    public int index = -1;
    public int timer = 0;
    public String time;
    public String UnknownHost = "Cannot Find Host";

    public FModel() {
        Thread a = new Thread();
        a.start();
    }

    public FModel(String ip, String port) {
        setIP(ip);
        setPort(port);
    }

    public String checkServer() throws MessagingException {
        toReturn = "";
        for (int i = 0; i < ip.size(); i++) {
            try {
                Socket ss = new Socket(ip.get(i), Integer.parseInt(port.get(i)));
                isOpen = true;
                if (both || display) {
                    toReturn = toReturn + ("Server (" + ip.get(i) + ") and Port (" + port.get(i) + ") is Open!\n");
                }
            } catch (UnknownHostException e) {
                System.out.println(UnknownHost);
                isOpen = false;
                index = i;

            } catch (IOException e) {
                if (both || display) {
                    sendMail("4052694805@messaging.sprintpcs.com"); //sending me a text through my phones email address (every carrier has this)
                    try {
                        Runtime.getRuntime().exec("C:\\Users\\serverauto.cmd"); //starting the server because it is down
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    toReturn = toReturn + ("Server (" + ip.get(i) + ") and Port (" + port.get(i) + ") is not found! since " + time + "\n");
                }
                isOpen = false;
                index = i;
            } catch (Exception e) {
                isOpen = false;
                if (both || display) {
                    sendMail("4052694805@messaging.sprintpcs.com");
                    try {
                        Runtime.getRuntime().exec("C:\\Users\\serverauto.cmd");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    toReturn = toReturn + ("Something strange happened. Couldn't connect to Server " + ip.get(i) + " and Port " + port.get(i) + "! since " + time + "\n");
                }
                index = i;
            }
        }
        return toReturn;
    }

    public boolean continuouslyCheck() throws MessagingException {
        while (true) {
            checkServer();
            try {
                Thread.sleep(checkTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendMail(String recipient) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        String myAccountEmail = ""; //enter email address here
        String password = ""; //enter password here (needs to be a static password with google might have to look up for instructions with this api) 
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });
        Message message = prepareMessage(session, myAccountEmail, recipient);
        Transport.send(message);
       // Transport transport = session.getTransport("smtp.gmail.com");
       // transport.connect("smtp.gmail.com", myAccountEmail, password );
        //transport.sendMessage(message, message.getAllRecipients() );
        //transport.close();
        System.out.println("Message Sent");
    }

    private static Message prepareMessage(Session session, String myAccountEmail, String recipient) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Server is Down");
            message.setText("Server is down");
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getToReturn(){
        return toReturn;
    }
    public void startServer(){
        Thread a= new Thread();
        a.start();
    }
    public boolean getOpen(){
        return this.isOpen;
    }
    public boolean isDown() throws InterruptedException {
        if(!isOpen){
            while(!isOpen){
                timer++;
                Thread.sleep(1000);
            }
            System.out.println("Server offline for "+timer+" seconds.");
        }
        else{
            return true;
        }
        return isOpen;
    }
    public void run(){
        try {
            continuouslyCheck();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void setIP(String input){
        ip.add(input);
    }
    public void setPort(String input){
        port.add(input);
    }
    public static void main(String[] args) throws MessagingException {
        sendMail("4052694805@messaging.sprintpcs.com");
        //Thread b= new Thread(new FModel("206.246.18.67", "25565"));
       // Thread c= new Thread(new FModel( "youtube.com", "80"));
        //Thread d= new Thread(new FModel("yasdfas", "9"));
       // c.start();
       // d.start();
       // b.start();
    }
}
