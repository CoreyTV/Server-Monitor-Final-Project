import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;

public class FPanel extends JPanel implements Runnable {
    public FFrame frame;
    public JTextArea text;
    public FPanel(FFrame frame) throws MessagingException {
        this.frame=frame;
        setLayout(new BorderLayout());
        text = new JTextArea();
        JScrollPane scroll = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        text.setFont(new Font("Serif", Font.PLAIN, 13));
        String[] options = {"Add Server", "Load", "Quit"};
        add(scroll, BorderLayout.CENTER);
       JOptionPane.showMessageDialog(null, "This Program Checks If A Server \n Is Up, And Will Notify You If It Is Down");
        int option= JOptionPane.showOptionDialog(null, "Choose An Option","Click a button", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if(option==0){
            String ip= JOptionPane.showInputDialog("Type in the IP");
            String port= JOptionPane.showInputDialog("Type in the Port");
            frame.model.setIP(ip);
            frame.model.setPort(port);
            frame.model.checkServer();
            String newText= (frame.model.getToReturn());
            text.removeAll();
            text.setText(newText);
            validate();
            repaint();
            this.repaint();
            frame.repaint();
        }
        else if(option==1){
            JFileChooser file = new JFileChooser();
            int option1 = file.showOpenDialog(this);
            if (option1 == JFileChooser.APPROVE_OPTION) {
                String filename = file.getSelectedFile().getAbsolutePath();
                try {
                    FileInputStream in = new FileInputStream(filename);
                    ObjectInputStream objin = new ObjectInputStream(in);
                    frame.model = (FModel) objin.readObject();
                    objin.close();
                    in.close();
                    validate();
                    repaint();
                    //model.checkServer();
                    String newText= (frame.model.getToReturn());
                    text.removeAll();
                    text.setText(newText);
                    validate();
                    repaint();
                    frame.repaint();
                    this.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if(option==2){
            System.exit(0);
        }
        text.requestFocus();
        text.setText(frame.getModel().getToReturn());
        repaint();
        Thread b= new Thread();
        b.start();
    }
    public void run(){
        while(true){
            try {
                repaint();
                revalidate();
                this.repaint();
                frame.repaint();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
