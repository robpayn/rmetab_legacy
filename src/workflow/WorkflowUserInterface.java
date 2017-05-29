package workflow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import stream_metab.utils.ConfigElement;

public class WorkflowUserInterface implements Runnable {
    
    private class FileItem extends File
    {

        public FileItem(String pathname)
        {
            super(pathname);
        }
        
        @Override
        public String toString()
        {
            return getName();
        }
        
    }
    
    public static Dimension LEFT_MIN = new Dimension(300,300);
    
    public static Dimension RIGHT_MIN = new Dimension(500,300);
    
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(new WorkflowUserInterface());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private File selectedFile;
    
    private JTree tree;
    
    private JFrame mainFrame;
    
    private JTextField hydroDownTextField;

    private Element configElement;

    private Document optimumDoc = null;
    
    @Override
    public void run()
    {
        buildMainFrame();
    }

    private void buildMainFrame()
    {
        mainFrame = new JFrame("Metab workflow");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JScrollPane fileScroller = new JScrollPane();
        fileScroller.setMinimumSize(LEFT_MIN);
        fileScroller.setPreferredSize(LEFT_MIN);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
                new FileItem(System.getProperty("user.dir"))
                );
        addFileNodes(rootNode);
        tree = new JTree(rootNode);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.getSelectionModel().addTreeSelectionListener(
                new TreeSelectionListener()
                {
                    @Override
                    public void valueChanged(TreeSelectionEvent e)
                    {
                        updateSelectedFile();
                    }
                }
                );
        tree.setSelectionRow(0);
        fileScroller.getViewport().add(tree);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setMinimumSize(RIGHT_MIN);
        rightPanel.setPreferredSize(RIGHT_MIN);
        hydroDownTextField = new JTextField("", 25);
        rightPanel.add(hydroDownTextField);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        save();
                    }
                }
                );
        rightPanel.add(saveButton);
        JButton reportButton = new JButton("Report");
        reportButton.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        report();
                    }
                }
                );
        rightPanel.add(reportButton);

        JSplitPane mainPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                fileScroller,
                rightPanel
                );
        mainFrame.getContentPane().add(mainPane);
        
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    protected void save()
    {
        if (optimumDoc != null)
        {
            Element hydroElem = (Element)configElement.getElementsByTagName("Hydro").item(0);
            Element hydroTestElem = null;
            NodeList downList = hydroElem.getElementsByTagName("hydroTestDownstreamHolon");
            if (downList.getLength() == 0)
            {
                hydroTestElem = optimumDoc.createElement("hydroTestDownstreamHolon");
                hydroElem.appendChild(hydroTestElem);
            }
            else
            {
                hydroTestElem = (Element)downList.item(0);
            }
            hydroTestElem.setTextContent(hydroDownTextField.getText().trim());
            hydroDownTextField.setForeground(Color.BLACK);
            
            
            try
            {
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                DOMSource source = new DOMSource(optimumDoc);
                StreamResult result = new StreamResult(new File(
                        selectedFile.getAbsolutePath() + File.separator + "mcmcout_f" + File.separator + "optimum.xml"
                        )); 
                xformer.transform(source, result);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(mainFrame, e.getMessage());
                e.printStackTrace();
            }
        }
        
    }

    protected void report()
    {
        Report report;
        try
        {
            report = new Report(selectedFile);
            report.run();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage());
            e.printStackTrace();
        }
    }

    protected void updateSelectedFile()
    {
        TreePath selectionPath = tree.getSelectionModel().getSelectionPath();
        if (selectionPath != null)
        {
            selectedFile = (File)((DefaultMutableTreeNode)selectionPath.getLastPathComponent()).getUserObject();
            if (selectedFile.isDirectory())
            {
                File optimum = new File(
                        selectedFile.getAbsolutePath() + File.separator + "mcmcout_f" + File.separator + "optimum.xml"
                        );
                if (optimum.exists())
                {
                    try
                    {
                        optimumDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(optimum);
                        configElement = optimumDoc.getDocumentElement();
                        Element hydroElem = (Element)configElement.getElementsByTagName("Hydro").item(0);
                        NodeList downList = hydroElem.getElementsByTagName("hydroTestDownstreamHolon");
                        if (downList.getLength() > 0)
                        {
                            hydroDownTextField.setText(((Element)downList.item(0)).getTextContent().trim());
                            hydroDownTextField.setForeground(Color.BLACK);
                        }
                        else
                        {
                            BufferedReader reportReader = new BufferedReader(new FileReader(new File(
                                    selectedFile.getAbsolutePath() + File.separator + "output" + File.separator + "report.r"
                                    )));
                            String line = reportReader.readLine().split("<-")[1].trim();
                            line = line.substring(1, line.length() - 2);
                            hydroDownTextField.setText(line);
                            hydroDownTextField.setForeground(Color.RED);
                        }
                    }
                    catch (Exception e)
                    {
                        JOptionPane.showMessageDialog(mainFrame, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            selectedFile = null;
        }
    }

    private void addFileNodes(DefaultMutableTreeNode curTop)
    {
        Vector<String> orderedList = new Vector<String>();
        File dir = (FileItem)curTop.getUserObject();
        String[] list = dir.list();
        for (String item: list)
        {
            orderedList.addElement(item);
        }
        Collections.sort(orderedList, String.CASE_INSENSITIVE_ORDER);
        
        Vector<DefaultMutableTreeNode> files = new Vector<DefaultMutableTreeNode>();
        for (String item: orderedList) 
        {
            File f = new FileItem(dir.getAbsolutePath() + File.separator + item);
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(f);
            if (f.isDirectory())
            {
                curTop.add(newNode);
                addFileNodes(newNode);
            }
            else
            {
                files.add(newNode);
            }
        }
        for (DefaultMutableTreeNode node: files)
        {
            curTop.add(node);
        }
    }

}
