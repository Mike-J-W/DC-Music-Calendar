import java.io.*;
import java.net.*;
import java.util.*;

public class GeneralCrawler {

	URL website;
	BufferedReader reader;
	
	GeneralCrawler(String siteName) throws IOException {
		this.website = new URL(siteName);
		InputStream s = this.website.openStream();
		this.reader = new BufferedReader(new InputStreamReader(s));
	}

	public String getWebsite() {
		return this.website.toString();
	}
	
	public BufferedReader getReader() throws IOException {
		return this.reader;
	}
	
}