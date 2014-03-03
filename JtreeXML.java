
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JtreeXML extends DefaultHandler {

	private JTree xmlJTree;
	DefaultTreeModel treeModel;
	int lineCounter;
	DefaultMutableTreeNode base = new DefaultMutableTreeNode("XML Viewer");
	static JtreeXML treeViewer = null;
	JTree dummy = xmlJTree;
	JTextField txtFile = null;
    static JTextArea ta;    
    static JButton openButton = new JButton("OPEN");
    
    static JButton redrawButton = new JButton("REDRAW");
    
    static JButton clearButton = new JButton("CLEAR");
    
    JPanel south = new JPanel(); 
    public static boolean RIGHT_TO_LEFT = false;
	@Override
	public void startElement(String uri, String localName, String tagName, Attributes attr) throws SAXException {

		DefaultMutableTreeNode current = new DefaultMutableTreeNode(tagName);

		base.add(current);
		base = current;

		for (int i = 0; i < attr.getLength(); i++) {
			DefaultMutableTreeNode currentAtt = new DefaultMutableTreeNode(attr.getLocalName(i) + " = "
					+ attr.getValue(i));
			base.add(currentAtt);
		}
	}

	public void skippedEntity(String name) throws SAXException {
		System.out.println("Skipped Entity: '" + name + "'");
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		 base = new DefaultMutableTreeNode("XML Viewer");
		((DefaultTreeModel) xmlJTree.getModel()).setRoot(base);
	}

	public void characters(char[] ch, int start, int length) throws SAXException {

		String s = new String(ch, start, length).trim();
		if (!s.equals("")) {
			DefaultMutableTreeNode current = new DefaultMutableTreeNode("Description : " + s);
			base.add(current);

		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

		base = (DefaultMutableTreeNode) base.getParent();
	}

	public static void main(String[] args) throws Exception {
		treeViewer = new JtreeXML();
		// treeViewer.xmlSetUp();
		treeViewer.createUI();

	}

	@Override
	public void endDocument() throws SAXException {
		// Refresh JTree
		((DefaultTreeModel) xmlJTree.getModel()).reload();
		expandAll(xmlJTree);
	}

	public void expandAll(JTree tree) {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	public void xmlSetUp(File xmlFile) {
		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser parser = fact.newSAXParser();
			parser.parse(xmlFile, this);

		} catch (Exception e) {

		}
	}

	public static void addComponentsToPane(final Container pane) throws Exception {
		
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
         
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(
                    java.awt.ComponentOrientation.RIGHT_TO_LEFT);
        }
        
        
        JPanel south = new JPanel();
        pane.add(south, BorderLayout.SOUTH);
        south.add(openButton);
       // south.add(redrawButton);
        south.add(clearButton);
        
		
	}
	
	public void createUI() throws Exception {

		treeModel = new DefaultTreeModel(base);
		xmlJTree = new JTree(treeModel);
		JScrollPane scrollPane = new JScrollPane(xmlJTree);
		
	    ta = new JTextArea();    
	    		
		JFrame windows = new JFrame();

		windows.setTitle("XML Tree Viewer using SAX Parser in JAVA");

		addComponentsToPane(windows.getContentPane());
		
		//pnl.setLayout(null);
		//JLabel lbl = new JLabel("File :");
		txtFile = new JTextField("Selected File Name Here");
		//JButton btn = new JButton("Select File");

		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JFileChooser fileopen = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("xml files", "xml");
				fileopen.addChoosableFileFilter(filter);

				int ret = fileopen.showDialog(null, "Open file");

				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileopen.getSelectedFile();
					txtFile.setText(file.getPath() + File.separator + file.getName());
					xmlSetUp(file);
					
					xmlJTree.setVisible(true);
					
	            	ta.setText("");	//reset text to nothing
	                File[] sf = fileopen.getSelectedFiles();
	                String filelist = "nothing";
	                if (sf.length > 0) filelist = sf[0].getName();
	                for (int i = 1; i < sf.length; i++) {
	                  filelist += ", " + sf[i].getName();
	                }
	                //statusbar.setText("You chose " + filelist);
	                 
	                 // Read in data file to JTextArea
	                 try{
	                 String strLine;
	                 File selectedFile = fileopen.getSelectedFile();
	                 FileInputStream in = new FileInputStream(selectedFile);
	                 BufferedReader br = new BufferedReader(new InputStreamReader(in));
	                 while ((strLine = br.readLine()) != null) {
	                     ta.append(strLine + "\n");
	                 
	                     
	                     
					
				}
	                 }
	               catch(Exception e){
	                   System.out.println("Ouch, I fell over! " + e);
	                }
	                 }
				}
		});
		
        clearButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent ae) {
            	ta.setText("");
            	//xmlJTree.clearSelection();
            	xmlJTree.setVisible(false);
            }
        });

		scrollPane.setPreferredSize(new Dimension(300,20));
		     
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setPreferredSize(new Dimension(300,20));
        windows.getContentPane().add(scroll, BorderLayout.EAST);

		windows.getContentPane().add(scrollPane,BorderLayout.WEST);

		//windows.add(pnl);
		windows.setSize(700, 400);
		windows.setVisible(true);
		windows.setDefaultCloseOperation( windows.EXIT_ON_CLOSE);
	}
}

