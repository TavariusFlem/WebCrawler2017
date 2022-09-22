import java.util.*;
import java.text.*;

 class WebsiteContentCollection
								implements Comparable<WebsiteContentCollection>
//maybe have this implement printable?????


	{
	//EmailDomainCollection emailDomains;
	Vector<EmailDomainCollection> domainVector;
	String website;
	int radius = 0;

//-------------DATA MEMBERS-------------------------

	WebsiteContentCollection(String website)
		{
		this.website = website;
		//Vector <EmailDomainCollection> domainVector;
		domainVector = new Vector<EmailDomainCollection>();
		}

/*======================== COMPARE =====================================================
======================================================================================*/

	public int compareTo(WebsiteContentCollection obj1)
		{
		return this.website.compareTo(obj1.website);

		}

/*======================== ADD EMAIL ===================================================
======================================================================================*/

	void addDomain(String emailDomainToBeAdded,String user)
		{
		EmailDomainCollection tempDomainCollection;

		tempDomainCollection = new EmailDomainCollection(emailDomainToBeAdded);
		System.out.println("ADDING DOMAIN");

		tempDomainCollection.emailDomainUsers.addElement(user);

		domainVector.addElement(tempDomainCollection);
		//this seems good
		}

/*===================== SORT DOMAINS ===================================================
======================================================================================*/

	void sortDomains()
		{
		//maybe clear the unsorted version of the vector and then
		//add the elements back into the vector with the sorted array

		//the issue is getting the array back to a vector once sorted
		Object[] domainCollection;
		domainCollection = domainVector.toArray();
		Arrays.sort(domainCollection,null);

		domainVector.clear();
		for(int x = 0; x<domainCollection.length-1;x++)
			{
			domainVector.addElement((EmailDomainCollection)domainCollection[x]);
			}

		//put that array back into a vector
		}
	}