package com.zcf.nettystudy.netty.v3;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-13-0:24
 * @Description: com.zcf.nettystudy.netty.v3
 * @version: 1.0
 */
public class ClientFrame extends Frame {

     private   static TextArea ta=null;
     private   static TextField tf=null;
     private Client client=null;
    public ClientFrame(){
         ta=new TextArea();
         tf=new TextField();
        this.setSize(600,400);
        this.setVisible(true);
        this.add(ta,BorderLayout.CENTER);
        this.add(tf,BorderLayout.SOUTH);
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = tf.getText();
                client.send(text);
                tf.setText("");
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.closeConnect();
                closeClientFrame();
            }
        });
    }
    public static void updateText(String msgAccepted){
        ta.setText(ta.getText()+msgAccepted+System.getProperty("line.separator"));
    }
    public static void closeClientFrame(){
        System.exit(0);
    }
    public Client newClient(){
        client=new Client();
        return client;
    }
    public static void main(String[] args) {
        ClientFrame clientFrame = new ClientFrame();
        clientFrame.newClient().connect();
    }
    public static TextArea getTa() {
        return ta;
    }

    public static TextField getTf() {
        return tf;
    }

    public Client getClient() {
        return client;
    }
}

