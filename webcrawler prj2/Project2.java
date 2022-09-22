import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.print.*;
import javax.swing.event.*;
import java.util.*;
import java.net.*;
import javax.swing.text.*;
import java.io.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

public class Project2
	{
	public static void main(String[] args)
		{
		new WebCrawlerFrame();
		}
	}
/*======================================================================================
======================================================================================*/
class WebCrawlerFrame extends JFrame
						implements ActionListener,DocumentListener,Printable
	{
	JTextField seedURLField;
	JButton goButton;
	static long maxExpansionTime;
	static long maxRunTime;
	Vector<WebsiteContentCollection> siteVector;
	int maxRadius = 4;
	JList<String> siteList;
	DefaultListModel<String> dlm;
	Vector<String> listToBeConverted;
	//int elementIndex;

/*========================= DATA MEMBERS ===============================================
======================================================================================*/

	WebCrawlerFrame()
		{
		JButton printButton;
		printButton = new JButton("Print");
		printButton.setActionCommand("PRINT");
		printButton.addActionListener(this);

		JPanel mainPanel;
		mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);

		JPanel southPanel;
		southPanel = new JPanel();

		goButton = new JButton("Go");
		goButton.setActionCommand("GO");
		goButton.addActionListener(this);

		JPanel northPanel;
		northPanel = new JPanel(new FlowLayout());
		JLabel seedURLLbl;
		seedURLLbl = new JLabel("ENTER SEED URL");
		northPanel.add(seedURLLbl);

		mainPanel.add(northPanel,BorderLayout.NORTH);

		seedURLField = new JTextField("http://www2.fairmontstate.edu/users/tlarue/smallweb/root.htm",51);

		southPanel.add(goButton);
		southPanel.add(printButton);

		northPanel.add(seedURLField);


		mainPanel.add(southPanel,BorderLayout.SOUTH);
		setupMainFrame(40, 40,"PROJECT2");
		siteVector = new Vector<WebsiteContentCollection>();

		dlm = new DefaultListModel<String>();

		siteList = new JList<String>();
		//siteList.setModel(dlm);

		JScrollPane scrollList;
		scrollList = new JScrollPane(siteList);

		mainPanel.add(scrollList,BorderLayout.CENTER);
		getRootPane().setDefaultButton(goButton);
	//	elementIndex = 0;

		}

/*========================= ACTION PERFORMED ===========================================
======================================================================================*/

	public void actionPerformed(ActionEvent cmd)
		{
		if(cmd.getActionCommand().equals("PRINT"))
			{

			try
				{
				doPrint();
				}
			catch(PrinterException pe)
				{
				System.out.println("ERROR PRINTING....");
				}
			}
		else if(cmd.getActionCommand().equals("GO"))
			{
			maxExpansionTime = 1000*9+ System.currentTimeMillis();

			maxRunTime = 1000*12+ System.currentTimeMillis();

			crawl(seedURLField.getText().trim());

			sortSiteList();
			createListModel();
			repaint();
			}
		}

/*========================= CHANGED UPDATE =============================================
======================================================================================*/

	public void changedUpdate(DocumentEvent de)
		{
		}

/*========================= INSERT UPDATE ==============================================
======================================================================================*/

	public void insertUpdate(DocumentEvent de)
		{
		seedURLField.getText().trim();
		if(seedURLField.getText().equals(""))
			{
			goButton.setEnabled(false);
			}
		else
			{
			goButton.setEnabled(true);
			}
		}

/*========================= REMOVE UPDATE ==============================================
======================================================================================*/

	public void removeUpdate(DocumentEvent de)
		{
		seedURLField.getText().trim();
		if(seedURLField.getText().equals(""))
			{
			goButton.setEnabled(false);
			}
		else
			{
			goButton.setEnabled(true);
			}
		}

/*============================ CRAWL ===================================================
======================================================================================*/

	void crawl(String seed)
		{
		URLConnection urlConnection;
		boolean done = false;
		WebsiteContentCollection temp;
		URL tempURL;
		MyParserCallbackTagHandler tagHandler;
		InputStreamReader isr;
		int x = 0;

		temp = new WebsiteContentCollection(seed);
		siteVector.addElement(temp);

			while(maxExpansionTime > System.currentTimeMillis() && !done)
				{
				try
					{
					tempURL = new URL(siteVector.elementAt(x).website);
					urlConnection = tempURL.openConnection();
					isr = new InputStreamReader(urlConnection.getInputStream());
					tagHandler = new MyParserCallbackTagHandler(siteVector.elementAt(x),siteVector,maxRadius);
					new ParserDelegator().parse(isr,tagHandler,true);

					if(MyParserCallbackTagHandler.baseHREF != null)
						MyParserCallbackTagHandler.baseHREF = null;




					}
				catch(MalformedURLException mue)
					{
					System.out.println("MALFORMED URL EXCEPTION");
					//mue.printStackTrace();
					}
				catch(IOException ioe)
					{
					System.out.println("IO EXCEPTION");
					ioe.printStackTrace();
					}
				catch(Exception e)
					{
					System.out.println("EXCEPTION");
					}
				x++;
				if(x >= siteVector.size())
					done = true;
				}

			seedURLField.setText("");
/*			for(int y = 0; y<siteVector.size()-1;y++)
				{
				dlm.addElement(siteVector.elementAt(y));
				siteList.setModel(dlm);
				}
	*/

		}//end of crawl(String)

/*=========================== DO PRINT==================================================
======================================================================================*/

	void doPrint() throws PrinterException
		{
		PrinterJob printerJob;
		PageFormat pageFormat;
		PageFormat defaultPageFormat;

		printerJob = PrinterJob.getPrinterJob();
		defaultPageFormat = printerJob.defaultPage();
		pageFormat = printerJob.pageDialog(defaultPageFormat);
		if(defaultPageFormat != pageFormat)
			{
			printerJob.setPrintable(this,pageFormat);//this being the frame
													//not sure if this is the way to go about it
			if(printerJob.printDialog())
				{
				System.out.println("READY TO PRINT!");
				printerJob.print();
				}
			}
		}//end of doPrint()

/*=========================== SORT SITE LIST ===========================================
======================================================================================*/

	void sortSiteList()
		{
		Object[] tempArr;
		tempArr = siteVector.toArray();
		Arrays.sort(tempArr,null);

		siteVector.clear();

		for(int x=0;x<tempArr.length-1;x++)
			{
			siteVector.addElement((WebsiteContentCollection)tempArr[x]);
			}

		for(int x = 0;x<siteVector.size()-1;x++)
			{
			siteVector.elementAt(x).sortDomains();
			for(int y = 0;y<siteVector.elementAt(x).domainVector.size()-1;y++)
				{
				siteVector.elementAt(x).domainVector.elementAt(y).sortUsers();
				}
			}
		}
/*========================= PRINT TO CONSOLE ============================================
=======================================================================================*/
	void printToConsole()
		{
		for(int x = 0; x<siteVector.size()-1;x++)
			{
			System.out.println(siteVector.elementAt(x).website);
			System.out.println("*****************************************************************");
			if(siteVector.elementAt(x).domainVector != null)
				{
				for(int y = 0; y< siteVector.elementAt(x).domainVector.size()-1;y++)
					{
					for(int z = 0; z < siteVector.elementAt(x).domainVector.elementAt(y).emailDomainUsers.size()-1;z++)
						{
						System.out.println(siteVector.elementAt(x).domainVector.elementAt(y).emailDomainUsers.elementAt(z).toString() + siteVector.elementAt(x).domainVector.elementAt(y).toString());
						}
					}
				}
			System.out.println(" ");
			}
		}//end of printToConsole()
/*======================== PRINT ========================================================
=======================================================================================*/
@Override
 	 public int print(Graphics g,
 	 				  PageFormat pf,
 	 				  int pageIndex)
 	 	{
		int fontSize;
		int linesPerPage;
		Graphics2D g2;

		fontSize = 12;
		linesPerPage = ((int)(pf.getImageableHeight() + 2*fontSize)/ (2 * fontSize)) - 1;

		System.out.println("NUMBER OF LINES PER PAGE :"+(int)linesPerPage);
		if( pageIndex > ( dlm.size()/(int)linesPerPage) )
			return Printable.NO_SUCH_PAGE;

		System.out.println("NUM PAGES:"+ dlm.size()/(int)linesPerPage +"");//num pages
		System.out.println("Page index: " + pageIndex);

		g2 = (Graphics2D)g;
		g2.setFont(new Font("Monospaced", Font.PLAIN,fontSize));//use the font size
		g2.setPaint(Color.black);

		double y = pf.getImageableY() + 2 * fontSize;
		double x = pf.getImageableX();//the bounds that you can print on


		for(int i = linesPerPage*pageIndex;i < dlm.size() && i < linesPerPage*(pageIndex + 1);i++)
			{
			System.out.println("index: " + i + " ELEMENT: " + dlm.elementAt(i)
			+ " I VALUE " +i);
			g2.drawString(dlm.elementAt(i),(float)x,(float)y);//out of bounds here
			//elementIndex++;//maybe do plus equals 1
			y+= fontSize*2;//gives double space

			System.out.println("line Position on screen; "+y+"");
			}
		g2.drawString((pageIndex+1) + "",((float)pf.getImageableWidth()+(float)pf.getImageableX())/2,(float)pf.getImageableHeight()+(float)pf.getImageableY());
		return PAGE_EXISTS;
		}

/*========================SET UP MAINFRAME===============================================
=======================================================================================*/
	void setupMainFrame(int xScreenPercentage,
						int yScreenPercentage,
						String title)
		{
		Toolkit tk;
		Dimension d;

		tk = Toolkit.getDefaultToolkit();
		d =tk.getScreenSize();
		setSize(d.width/2,d.height/2);
		setLocation(d.width/4,d.height/4);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle(title);
		setVisible(true);
		}
/*===================== CONVERT VECTOR =================================================
======================================================================================*/
	void createListModel()
		{

//create list model

		listToBeConverted = new Vector<String>();



		for(int x = 0; x<siteVector.size();x++)
			{
			listToBeConverted.addElement("Site:");
			listToBeConverted.addElement(siteVector.elementAt(x).website);

			if(siteVector.elementAt(x).domainVector != null)
				{
				listToBeConverted.addElement("****************************************************");
				listToBeConverted.addElement("Emails:");
				System.out.println("NOT NULL");
				for(int y = 0; y< siteVector.elementAt(x).domainVector.size();y++)
					{

					for(int z = 0; z < siteVector.elementAt(x).domainVector.elementAt(y).emailDomainUsers.size() ;z++)
						{
						listToBeConverted.addElement(siteVector.elementAt(x).domainVector.elementAt(y).emailDomainUsers.elementAt(z).toString() + "@"+ siteVector.elementAt(x).domainVector.elementAt(y).domain);

						System.out.println("EMAIL:"+siteVector.elementAt(x).domainVector.elementAt(y).emailDomainUsers.elementAt(z).toString() + siteVector.elementAt(x).domainVector.elementAt(y).domain);
						System.out.println("sdlfjsldkfls");
						}
					}
				}
			System.out.println(" @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			listToBeConverted.addElement(" ");
			}
		for(int j = 0;j<listToBeConverted.size()-1;j++)
			{
			System.out.println(listToBeConverted.elementAt(j));
			dlm.addElement(listToBeConverted.elementAt(j)+ "");
			siteList.setModel(dlm);
			}
		}
	}//end of class