import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class FFrame extends JFrame implements ActionListener, Serializable, Runnable {
    public Thread[] servers;
    public FPanel panel;
    public String savedPath;
    public FModel model;
    public int checkInterval=500;

    public FFrame() throws MessagingException {

            model= new FModel();
        setTitle("Server Monitor");
        JMenuBar mbar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenu settings = new JMenu("Settings");
        JMenuItem add = new JMenuItem("Add Server");
        JMenuItem interval = new JMenuItem("Check Interval");
        JMenuItem notification = new JMenuItem("Notifications");
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screensize.width / 2, screensize.height / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new FPanel(this);
        JMenuItem save = new JMenuItem("Save");
        JMenuItem load = new JMenuItem("Load");
        JMenuItem quit = new JMenuItem("Quit");
        JMenuItem refresh= new JMenuItem("Refresh");
        menu.add(save);
        menu.add(load);
        menu.add(quit);
        settings.add(refresh);
        settings.add(add);
        settings.add(interval);
        settings.add(notification);
        mbar.add(menu);
        mbar.add(settings);
        setJMenuBar(mbar);
        save.addActionListener( this);
        load.addActionListener( this);
        quit.addActionListener( this);
        add.addActionListener(this);
        refresh.addActionListener(this);
        notification.addActionListener(this);
        interval.addActionListener(this);
        Thread b= new Thread(this);
        //this.model.setIP("192.168.0.148"); //hard coded my minecraft server
        //this.model.setPort("25565"); //hardcoded my minecraft server
        b.start();
        add(panel);
        setVisible(true);
    }
    public FModel getModel() {
        return model;
    }
    public void run(){
        while(true){
            //model.checkServer();
            Date date= new Date();
            SimpleDateFormat b= new SimpleDateFormat("dd-MM-yyyy HH:mm");
            model.time= b.format(date);
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(model.popup||model.both) {
                if (model.index >= 0) {
                    JOptionPane.showMessageDialog(null, model.ip.get(model.index)+" has been down since "+model.time +"\n(Notice pressing \"OK\" will reset the time it has been down)");
                    model.index=-1;
                }
            }
            try {
                model.checkServer();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            String newText= (this.model.getToReturn());
            panel.text.setText(newText);
            panel.validate();
            repaint();
            panel.repaint();
            this.repaint();
        }
    }
    public void actionPerformed(ActionEvent e) {
        String click = e.getActionCommand();
        if (click.equals("Save")) {
            JFileChooser file = new JFileChooser();
            int option = file.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = file.getSelectedFile().getAbsolutePath();
                savedPath = filename;
                JOptionPane.showMessageDialog(this, filename);
                try {
                    FileOutputStream out = new FileOutputStream(savedPath);
                    ObjectOutputStream objout = new ObjectOutputStream(out);
                    objout.writeObject(getModel());
                    objout.close();
                    out.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if(click.equals("Add Server")){
            String ip= JOptionPane.showInputDialog("Type in the IP");
            String port= JOptionPane.showInputDialog("Type in the Port");
            this.model.setIP(ip);
            this.model.setPort(port);
            try {
                model.checkServer();
            } catch (MessagingException messagingException) {
                messagingException.printStackTrace();
            }
            String newText= (this.model.getToReturn());
            panel.text.removeAll();
            panel.text.setText(newText);
            panel.validate();
            repaint();
            panel.repaint();
            this.repaint();
        }
        else if(click.equals("Refresh")){
            try {
                model.checkServer();
            } catch (MessagingException messagingException) {
                messagingException.printStackTrace();
            }
            String newText= (this.model.getToReturn());
            panel.text.removeAll();
            panel.text.setText(newText);
            panel.validate();
            repaint();
            panel.repaint();
            this.repaint();
        }
        else if(click.equals("Check Interval")){
           String check= (JOptionPane.showInputDialog("Check Interval (ms)"));
           checkInterval= Integer.parseInt(check);
        }
        else if(click.equals("Notifications")){
            String[] options = { "Popup Notifications", "Message On Screen", "Both"};
            int option= JOptionPane.showOptionDialog(null, "Click On An Option","Notifications", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if(option==0){
                model.popup=true;
                model.display=false;
                model.both=false;
            }
            else if(option==1){
                model.display=true;
                model.popup=false;
                model.both=false;
            }
            else if(option==2){
                model.display=false;
                model.popup=false;
                model.both=true;
            }
        }
        else if (click.equals("Load")) {
            JFileChooser file = new JFileChooser();
            int option = file.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = file.getSelectedFile().getAbsolutePath();
                try {
                    FileInputStream in = new FileInputStream(filename);
                    ObjectInputStream objin = new ObjectInputStream(in);
                    model = (FModel) objin.readObject();
                    objin.close();
                    in.close();
                    validate();
                    repaint();
                    //model.checkServer();
                    String newText= (this.model.getToReturn());
                    panel.text.removeAll();
                    panel.text.setText(newText);
                    panel.validate();
                    repaint();
                    panel.repaint();
                    this.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
                        else if(click.equals("Quit")){
                    System.exit(0);
                }
            }
        }
