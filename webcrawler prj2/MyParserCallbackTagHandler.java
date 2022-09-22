import java.net.*;
import javax.swing.text.html.*;
import javax.swing.text.*;
import javax.swing.text.html.parser.*;
import java.util.*;
import java.util.regex.*;

class MyParserCallbackTagHandler extends HTMLEditorKit.ParserCallback
	{
	static String baseHREF;

	Vector<WebsiteContentCollection> siteVector;
	WebsiteContentCollection currentSite;
	int maxRadius;
	WebsiteContentCollection tempSite;

//------------DATA MEMBERS--------------------------------

	public MyParserCallbackTagHandler(WebsiteContentCollection currentSite,Vector<WebsiteContentCollection> siteVector,int maxRadius)
		{
		this.siteVector = siteVector;
		this.currentSite = currentSite;
		System.out.println("=-=-=-=-=--=-=-=-==-=-=-=-=-_-=_-=_-=_-=_-=_-=_-=_-=_-=_-=_-=_-=_-=_-=_-=_-=_-=");
		System.out.println("CURRENTSITE: " + currentSite.website);
		this.maxRadius = maxRadius;
		}

/*================================= HANDLE START TAG ===============================================
==================================================================================================*/

//REMEMBER TO HANDLE MAIL TO

@Override
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attSet,int pos)
		{
		if(WebCrawlerFrame.maxExpansionTime > System.currentTimeMillis() && currentSite.radius != maxRadius)
			{
			Object currentTagAttribute;
			Enumeration<?> attributeEnumeration;

			if(tag == HTML.Tag.A)
				{
				currentTagAttribute = attSet.getAttribute(HTML.Attribute.HREF);

				if(currentTagAttribute != null)
					{

					if(currentTagAttribute.toString().startsWith("http") || currentTagAttribute.toString().startsWith("file"))
						{
						tempSite = new WebsiteContentCollection(currentTagAttribute.toString());

						System.out.println(""+ currentTagAttribute);

						boolean wasVisited = false;
						for(int x = 0; x<siteVector.size()-1;x++)
							{
							if(siteVector.elementAt(x).website.equals(currentTagAttribute.toString()))
								{
								wasVisited = true;
								}
							}
						if(!wasVisited)
							{
							tempSite.radius = currentSite.radius +1;
							siteVector.addElement(tempSite);
							}
						}

					else if(currentTagAttribute.toString().startsWith("mailto"))
						{
						System.out.println("EMAIL-=-=-=-=-=-=-=-================-=-=-==-=-=-=-=-=-=-=");
						String tempString;
						tempString = currentTagAttribute.toString();
						tempString = tempString.substring(7);
						//gets rid of the mailto:

						String[] tempArr = tempString.split("@");

						System.out.println(tempArr[1]);
						System.out.println(tempArr[0]);


						currentSite.addDomain(tempArr[1],tempArr[0]);
						}

					else if(baseHREF != null)
						{
						tempSite = new WebsiteContentCollection(baseHREF + currentTagAttribute.toString());

						System.out.println("" + baseHREF +currentTagAttribute);

						boolean wasVisited = false;
						for(int x = 0; x<siteVector.size()-1;x++)
							{
							if(siteVector.elementAt(x).website.equals(currentTagAttribute.toString()))
								{
								wasVisited = true;
								}
							}
						if(!wasVisited)
							{
							tempSite.radius = currentSite.radius+1;
							siteVector.addElement(tempSite);
							}
						}//BASE HREF
					}//NULL CHECK
				}//HTML TAG A
			}//TIMER & RADIUS CHECK
		else
			{
			System.out.println("MAX EXPANSION TIME OR RADIUS LIMIT HIT      " + WebCrawlerFrame.maxExpansionTime + " :::" + currentSite.radius);

			}
		}

/*============================= HANDLE SIMPLE TAG ==================================================
==================================================================================================*/
@Override
	public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attSet,int pos)
		{
	//	Object currentTagAttribute;
		Enumeration<?> attributeEnumeraton;

		if(tag == HTML.Tag.BASE)
			{
			System.out.println("BASE TAG!");
			baseHREF = attSet.getAttribute(HTML.Attribute.HREF).toString();
			}
		}

/*================================ HANDLE TEXT =====================================================
==================================================================================================*/

@Override
	public void handleText(char[] data,int pos)
		{
		if(WebCrawlerFrame.maxRunTime > System.currentTimeMillis())
			{
			Pattern pattern;
			String textToHandle;
			textToHandle = new String(data);

			String[] arr;

			String regExpStr = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
			Matcher matcher;

			pattern = Pattern.compile(regExpStr);
			matcher = pattern.matcher(textToHandle);//this is where the text goes

			boolean done = false;

			while(!done)
				{
				if(matcher.find())
					{
					System.out.println("Found \"" + textToHandle.substring(matcher.start() ,matcher.end()) + "\"");
					System.out.printf("     new region:  [%d, %d)\n",matcher.end(), textToHandle.length());

					//store the emails from this???

					arr = textToHandle.substring(matcher.start(),matcher.end()).split("@");


					//add this to the current sites vector

					System.out.println("EMAIL----------------------------------------------------------");
					System.out.println(arr[0]+ arr[1] + "");
					currentSite.addDomain(arr[1],arr[0]);

					matcher.region(matcher.end(), textToHandle.length());
					}
				else
					{
					done = true;
					}
				//split and check domain then handle accordingly
				}
			}
		else
			{
			System.out.println("MAX RUN TIME SOFT LIMIT HIT");
			}
		}
	}//end of class