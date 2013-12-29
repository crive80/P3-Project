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
import java.util.Vector;

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
    private JPanel centerWestPanel;
    private JPanel centerEastPanel;
    private JPanel bottomPanel;
    private JTextArea log;
    private JList localClientList;
    private JList globalClientList;
    private JList serverList;
    private Server serverReference;
    
    private void setMainPanel() {
        contentPane = new JPanel();
        contentPane.setOpaque(true);
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(wgap, hgap, wgap, hgap));
        contentPane.setLayout(new BorderLayout(wgap, hgap));
    }
    
    private void setTopPanel() {
        topPanel = new JPanel();
        topPanel.setOpaque(true);
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(
            BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK),
                "Server connessi",TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.PLAIN,10)));
        serverList = new JList();
        topPanel.add(serverList, BorderLayout.PAGE_START);
        
    }
    
    private void setCenterPanel() {
        centerWestPanel = new JPanel();
        centerWestPanel.setOpaque(true);
        centerWestPanel.setBackground(Color.WHITE);
        centerWestPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK),
                "Client locali connessi",TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.PLAIN,10)));
        localClientList = new JList();
        localClientList.setFont(new Font("Arial",Font.BOLD,8));
        centerWestPanel.add(localClientList, BorderLayout.WEST);
        centerWestPanel.setPreferredSize(new Dimension((int) width/2-5, (int) height/2));

        centerEastPanel = new JPanel();
        centerEastPanel.setOpaque(true);
        centerEastPanel.setBackground(Color.WHITE);
        centerEastPanel.setBorder(
            BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK),
                "Client globali connessi",TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.PLAIN,10)));
        globalClientList = new JList();
        globalClientList.setFont(new Font("Arial",Font.BOLD,8));
        centerEastPanel.add(globalClientList);
        centerEastPanel.setPreferredSize(new Dimension((int) width/2-5, (int) height/2));
    }
    
    private void setBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setOpaque(true);
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK),
                "Log",TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.PLAIN,10)));
        bottomPanel.setLayout(new BorderLayout());
        log = new JTextArea();
        log.setRows(10);
        log.setEditable(false);
        log.setFont(new Font("Arial",Font.BOLD,8));
        log.setBackground(Color.BLACK);
        log.setForeground(Color.GREEN);
        
        bottomPanel.add(log,BorderLayout.CENTER);
    }
    
    class WindowEventHandler extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
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
        width = (int) screenSize.getWidth()/3;
        height = (int) screenSize.getHeight() - 100;

        addWindowListener(new WindowEventHandler());
        
        setMainPanel();
        setTopPanel();
        setCenterPanel();
        setBottomPanel();
        contentPane.add(topPanel,BorderLayout.PAGE_START);
        contentPane.add(centerWestPanel,BorderLayout.WEST);
        contentPane.add(centerEastPanel,BorderLayout.EAST);
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
        localClientList.setModel(model);
    }

    public void setGlobalClientList(Vector<String> l) {
        String[] s = new String[l.size()];
        for (int i=0; i<l.size(); i++) s[i] = l.elementAt(i);
        DefaultListModel<String> model = new DefaultListModel<String>();
        for (int i=0; i<s.length; i++) { model.addElement(s[i]); }
        globalClientList.setModel(model);
    }
    
    public static void main(String[] args, Server s) {
        ServerGUI g = new ServerGUI("Server1",s);
    }
}
