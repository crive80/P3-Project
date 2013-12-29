/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import Client.Client;
import java.util.Vector;
import java.awt.event.*;
import java.awt.Font;
import javax.swing.plaf.basic.BasicBorders;
import java.rmi.*;
import java.net.MalformedURLException;

/**
 *
 * @author luca
 */
public class ClientGUI extends JFrame {
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
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JTextArea log;
    private JTextField searchField;
    private JButton searchButton;
    private JList serverList;
    private JList downloadQueue;
    private JList resourceList;
    private JButton connectButton;
    private Client clientReference;
    
    class WindowEventHandler extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            System.out.println("Window closed");
            clientReference.disconnect();
        }
    }

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
            BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK),
                "Cerca file",TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.PLAIN,10)));
        
        searchField = new JTextField(10);
        searchButton = new JButton("Cerca");
        searchButton.setFont(new Font("Arial",Font.BOLD,10));
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = searchField.getText();
                String [] a = s.split("\\s+"); // separo le parole
                clientReference.sendRequest(a[0],a[1]);
            }
        });
        topPanel.add(searchField,BorderLayout.WEST);
        topPanel.add(searchButton,BorderLayout.EAST);
        
        
    }
    
    private void setCenterPanel() {
        centerWestPanel = new JPanel();
        centerWestPanel.setOpaque(true);
        centerWestPanel.setBackground(Color.WHITE);
        centerWestPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK),
                "Server disponibili",TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.PLAIN,10)));
        serverList = new JList();
        serverList.setFont(new Font("Arial",Font.BOLD,6));
        connectButton = new JButton("Connetti");
        connectButton.setFont(new Font("Arial",Font.BOLD,10));
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = (String) serverList.getSelectedValue();
                try {
                    clientReference.connect(s);
                } catch (Exception exc) { exc.printStackTrace(); }
            }
        });        
        centerWestPanel.setLayout(new BoxLayout(centerWestPanel,BoxLayout.PAGE_AXIS));
        centerWestPanel.add(serverList);
        centerWestPanel.add(connectButton);
        //centerWestPanel.setPreferredSize(new Dimension((int) width/2 -25, (int) height/2));

        centerPanel = new JPanel();
        centerPanel.setOpaque(true);
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK),
                "Coda download",TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.PLAIN,10)));
        downloadQueue = new JList();
        downloadQueue.setFont(new Font("Arial",Font.BOLD,6));
        centerPanel.add(downloadQueue,BorderLayout.NORTH);
        /*****/
        DefaultListModel<String> model = new DefaultListModel<String>();
        downloadQueue.setModel(model);
        /****/
        centerEastPanel = new JPanel();
        centerEastPanel.setOpaque(true);
        centerEastPanel.setBackground(Color.WHITE);
        centerEastPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK),
                "Risorse disponibili",TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.PLAIN,10)));
        //centerEastPanel.setPreferredSize(new Dimension((int) width/2 -25, (int) height/2));
        resourceList = new JList();
        resourceList.setFont(new Font("Arial",Font.BOLD,10));
        centerEastPanel.add(resourceList);
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
        log.setFont(new Font("Arial",Font.PLAIN,9));
        log.setRows(10);
        log.setEditable(false);
        log.setBackground(Color.BLACK);
        log.setForeground(Color.GREEN);
        
        bottomPanel.add(log,BorderLayout.CENTER);
    }
    
    public ClientGUI(String n, Client r) {
        super(n);
        clientReference = r;
        title = n;        
        width = (int) screenSize.getWidth()/4;
        height = (int) screenSize.getHeight()/2;
        
        setMainPanel();
        setTopPanel();
        setCenterPanel();
        setBottomPanel();
        contentPane.add(topPanel,BorderLayout.PAGE_START);
        contentPane.add(centerWestPanel,BorderLayout.WEST);
        contentPane.add(centerEastPanel,BorderLayout.EAST);
        contentPane.add(centerPanel,BorderLayout.CENTER);
        contentPane.add(bottomPanel,BorderLayout.PAGE_END);
        setContentPane(contentPane);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setVisible(true);
    }

    public void setServerList(String[] l) {
        DefaultListModel<String> model = new DefaultListModel<String>();
        for (int i=0; i<l.length; i++) { model.addElement(l[i]); }
        serverList.setModel(model);
    }

    public void setResourceList(Vector<String> l) {
        DefaultListModel<String> model = new DefaultListModel<String>();
        for (int i=0; i<l.size(); i++) { model.addElement(l.elementAt(i)); }
        resourceList.setModel(model);
    }

    public void appendLog(String s) {
        log.append(s + "\n");
    }
}
