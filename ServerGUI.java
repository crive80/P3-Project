/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import Server.*;
import java.rmi.*;
import java.net.MalformedURLException;

/**
 *
 * @author luca
 */
public class ServerGUI extends JFrame {
    private String title;
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int width;
    private int height;
    private static final int hgap = 5;
    private static final int wgap = 5;
    private JPanel contentPane;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JTextArea log;
    private JList clientList;
    private JList serverList;
    private Server serverReference;
    
    private void setMainPanel() {
        contentPane = new JPanel();
        contentPane.setOpaque(true);
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(5, 5));
    }
    
    private void setTopPanel() {
        topPanel = new JPanel();
        topPanel.setOpaque(true);
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(
            BorderFactory.createTitledBorder("Client connessi"));
        clientList = new JList();
        topPanel.add(clientList, BorderLayout.PAGE_START);
        
    }
    
    private void setCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setOpaque(true);
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(
                BorderFactory.createTitledBorder("Server connessi")
                );
        serverList = new JList();
        centerPanel.add(serverList, BorderLayout.CENTER);
    }
    
    private void setBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setOpaque(true);
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(
                BorderFactory.createTitledBorder("Log"));
        bottomPanel.setLayout(new BorderLayout());
        log = new JTextArea();
        log.setRows(10);
        log.setEditable(false);
        log.setBackground(Color.BLACK);
        log.setForeground(Color.GREEN);
        
        bottomPanel.add(log,BorderLayout.CENTER);
    }
    
    class WindowEventHandler extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            System.out.println("Window closed");
            try {
                serverReference.disconnect();
            }
            catch (RemoteException e1) { System.out.println("Errore di connessione."); }
            catch (MalformedURLException e2) { System.out.println("Errore di malformazione URL"); }
            catch (NotBoundException e3) { System.out.println("NotBoundException"); }
        }
    }

    public ServerGUI(String n, Server s) {
        super(n);
        title = n;        
        serverReference = s;
        width = (int) screenSize.getWidth()/4;
        height = (int) screenSize.getHeight()/2;

        addWindowListener(new WindowEventHandler());
        
        setMainPanel();
        setTopPanel();
        setCenterPanel();
        setBottomPanel();
        contentPane.add(topPanel,BorderLayout.PAGE_START);
        contentPane.add(centerPanel,BorderLayout.CENTER);
        contentPane.add(bottomPanel,BorderLayout.PAGE_END);
        setContentPane(contentPane);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setVisible(true);
    }
    
    public void appendLog(String n) {
        log.append(n);
    }

    public void setServerList(String[] l) {
        DefaultListModel<String> model = new DefaultListModel<String>();
        for (int i=0; i<l.length; i++) { model.addElement(l[i]); }
        serverList.setModel(model);
    }

    public void setClientList(String[] l) {
        DefaultListModel<String> model = new DefaultListModel<String>();
        for (int i=0; i<l.length; i++) { model.addElement(l[i]); }
        clientList.setModel(model);
    }
    
    public static void main(String[] args, Server s) {
        ServerGUI g = new ServerGUI("Server1",s);
    }
}
