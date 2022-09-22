import java.util.*;

public class EmailDomainCollection
								   implements Comparable<EmailDomainCollection>
	{
	Vector<String> emailDomainUsers;
	String domain;

//-------------DATA MEMBERS ------------------------

	EmailDomainCollection(String domain)
		{
		this.domain = domain;
		emailDomainUsers = new Vector<String>(10,4);
		//NEEDS TO HAVE SOMETHING FOR THE USERS TOO
		}

/*======================== SORT USERS ==================================================
======================================================================================*/

	void sortUsers()
		{
		Collections.sort(emailDomainUsers);
		}//end of Sort()

/*======================== ADD USER ====================================================
======================================================================================*/

	void addUser(String user)
		{
		emailDomainUsers.addElement(user);
		}

/*===================== COMPARE TO ====================================================
=====================================================================================*/

	public int compareTo(EmailDomainCollection domainToCompare)
		{
		return this.domain.compareTo(domainToCompare.domain);
		}
	}//end of class