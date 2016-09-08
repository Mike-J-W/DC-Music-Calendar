import java.io.*;
import java.net.*;
import java.util.*;
import com.google.api.services.calendar.model.*;
import com.google.api.client.util.DateTime;


public class UStMusicHallCrawler extends GeneralCrawler {

	String delims = "[<>=]+";

	public UStMusicHallCrawler() throws IOException {
		super("http://ustreetmusichall.com/calendar");
	}
	
	public List<String> findHeadliners() throws IOException {
		List<String> headliners = new ArrayList<String>();
		while (super.reader.ready()) {
			String line = super.reader.readLine();
			if (line.contains("list-view-details vevent")) {
				String topLine = super.reader.readLine();
				List<String> topLinePieces = Arrays.asList(topLine.split(this.delims));
				int ahrefIndex = topLinePieces.indexOf("a href");
				headliners.add(topLinePieces.get(ahrefIndex + 2));
				
			}
		}
		return headliners;
	}
	
	public List<Event> constructEvents(List<Event> currentEvents) throws IOException {
		List<Event> usmhEvents = new ArrayList<Event>();
		while (super.reader.ready()) {
			String line = super.reader.readLine();
			if (line.contains("list-view-details vevent")) {
				try {
					Event nextEvent = new Event().setLocation("U St. Music Hall, NW DC");
					String headliner = "";
					String support = "";
					String sTime = "";
					String eTime = "";
					String eventPage = "";
					String ticketPage = "";
					String additionalInfo = "";
					String nextLine = super.reader.readLine();
					while (! nextLine.contains("list-view-item")) {
						if (nextLine.contains("headliners summary")) {
							List<String> nextLinePieces = Arrays.asList(nextLine.split(this.delims));
							int ahrefIndex = nextLinePieces.indexOf("a href");
							eventPage = "http://930.com" + nextLinePieces.get(ahrefIndex + 1).replaceAll("\"", "");
							headliner = nextLinePieces.get(ahrefIndex + 2);
						}
						if (nextLine.contains("supports description")) {
							List<String> nextLinePieces = Arrays.asList(nextLine.split(this.delims));
							int ahrefIndex = nextLinePieces.indexOf("a href");
							support = nextLinePieces.get(ahrefIndex + 2);						
						}
						if (nextLine.contains("start dtstart")) {
							List<String> nextLinePieces = Arrays.asList(nextLine.split(this.delims));
	                        sTime = nextLinePieces.get(5).replace("\"", "").replace(" ", "");
	                        List<String> sDateTimeList = Arrays.asList(sTime.split("T"));
	                        List<String> sTimesList = Arrays.asList(sDateTimeList.get(1).split(":"));
	                        int hour = Integer.parseInt(sTimesList.get(0));
	                        eTime = sDateTimeList.get(0) + "T" + Integer.toString(hour+1) + ":" + String.join(":", sTimesList.subList(1, 4));
						}
						if (nextLine.contains("additional-event-info")) {
							List<String> nextLinePieces = Arrays.asList(nextLine.split(this.delims));
	                        additionalInfo = nextLinePieces.get(4);
						}
						if (nextLine.contains("ticket-link primary-link")) {
							List<String> nextLinePieces = Arrays.asList(nextLine.split(this.delims));
							int hrefIndex = nextLinePieces.indexOf("\"tickets\" href");
							ticketPage = Arrays.asList(nextLinePieces.get(hrefIndex+1).split("\"")).get(1);
						}
						nextLine = super.reader.readLine();
					}
					if (! support.equals("")) {
						nextEvent.setSummary(headliner + " with " + support);
					} else {
	     				nextEvent.setSummary(headliner);
					}
					nextEvent.setStart(new EventDateTime().setDateTime(new DateTime(sTime)));
					nextEvent.setEnd(new EventDateTime().setDateTime(new DateTime(eTime)));
					nextEvent.setDescription(additionalInfo + "\nEvent Page:\n" + eventPage + "\nTicket Page:\n" + ticketPage);
					Boolean addEvent = true;
					for (Event cE : currentEvents) {
						if (cE.getSummary().equals(nextEvent.getSummary()) && cE.getLocation().equals(nextEvent.getLocation()) && cE.getDescription().equals(nextEvent.getDescription())) {
							addEvent = false;
						}
					}
					if (addEvent) {
						usmhEvents.add(nextEvent);						
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("ArrayIndexOutOfBoundsException: " + e.getMessage());
				} catch (NullPointerException e) {
					System.out.println("NullPointerException: " + e.getMessage());
				}
			}
		}
		return usmhEvents;
	}
	
	
}