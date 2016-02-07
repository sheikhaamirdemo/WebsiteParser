package getlink;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.awt.image.BufferedImage;
import java.io.*; 
import java.util.List;

public class getlinked 
{

	static List<productdata> BRAND_LIST = new ArrayList<productdata>();
    static Set<productdata> ITEM_LIST = new HashSet<productdata>();

	public static void main(String[] args)  throws IOException, InterruptedException 
	{ 
		Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);                
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);  
        ((DesiredCapabilities) caps).setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                /**Path"\\phantomjs.exe"**/
            );
        
        WebDriver   driver = new  PhantomJSDriver(caps);
		
		ArrayList<productdata> datalist = new ArrayList<productdata>();
		
	
	    for(char alphabet = 'A'; alphabet <= 'Z';alphabet++)        
	    {
			
			driver.get(/**Webpage that has brand listings**/);
		    
		    List<WebElement> list =  driver.findElements(By.cssSelector("a[href*='products.html?brand']"));          //Finds elements you are looking for
		    for(WebElement e : list){
				String link = e.getAttribute("title");
	            String href = e.getAttribute("href");
	            datalist.add(new productdata(link, null , href, null, null ));
	        }
		}
			
	    Set<productdata> gs = new HashSet<>();                           //convert to set to remove duplicates
		gs.addAll(datalist);
		datalist.clear();
		datalist.addAll(gs);
		datalist.removeAll(Arrays.asList(null,""));                //convert back to list, remove blanks and order alphabetically
		Set<String> titles = new HashSet<String>();
	    for( productdata e : datalist ) {
		    if( titles.add( e.Name )) BRAND_LIST.add( e );
		}
		Collections.sort(BRAND_LIST, new Comparator<productdata>() {public int compare(productdata o1, productdata o2) {return o1.Name.compareTo(o2.Name); }});
		File brandlog = new File(/**Path**/"");
		 
		for(int k = 0;  k < BRAND_LIST.size(); k++)
		{
		
		try
		    {
			    if(brandlog.exists()==false)
			    {
		            System.out.println("We had to make a new file.");
		            brandlog.createNewFile();
			    }
			    PrintWriter out = new PrintWriter(new FileWriter(brandlog, true));
			    out.append(BRAND_LIST.get(k).Name + "\n");
			    out.close();
		    }
		    catch(IOException e)
		    {
		        System.out.println("COULD NOT LOG!!");
		    }
		
		}
		
		
//********************************************************************************************************************************////////////////////////////////////////
//********************************************************************************************************************************////////////////////////////////////////	
		for(int i = 0; i<BRAND_LIST.size();i++)
		{
			driver.get((BRAND_LIST.get(i).Href)+"&limit=120");
			Boolean isnextpage = true;
			File log = new File(/**Path**/"");
			new File(/**Path**/"").mkdir();
			
			while(isnextpage)
			{
				
				if(driver.findElements(By.cssSelector(".next.i-next")).size() > 0)
				{
					WebElement element = driver.findElement(By.cssSelector(".next.i-next"));
					String title = element.getAttribute("title");
					String Temphref = element.getAttribute("href");
					
					if(!title.equals("Next"))  isnextpage = false;
					
					 else if(title.equals("Next"))
					{
						int result = parser(log, driver, i, BRAND_LIST.get(i).Name);
						if(result == -1){System.out.println("The data didnt match at or before page:" + (BRAND_LIST.get(i).Href)+"&limit=120");}
						driver.get(Temphref);
				    }
				}
				
				else
				{
					int result = parser(log, driver, i, BRAND_LIST.get(i).Name);
					if(result == -1) System.out.println("The data didnt match at or before page:" + (BRAND_LIST.get(i).Href)+"&limit=120");
					break;
				}
			}
		}
//********************************************************************************************************************************////////////////////////////////////////
//********************************************************************************************************************************////////////////////////////////////////	
		driver.close();
	}
	
	static int parser(File log, WebDriver driver, int i, String Brand) throws IOException
	{
		
		try{
		
		
		WebElement mainframe =	driver.findElement(By.xpath("//div[contains(@class, 'category-products')]"));
		List<WebElement> newlist1 = mainframe.findElements(By.className("product-name"));
		List<WebElement> regprice =	mainframe.findElements(By.cssSelector(".regular-price,.old-price"));
		List<WebElement> images =	mainframe.findElements(By.xpath(/**XPath Element**/""));
		List<String> ITEM_PRICE = new ArrayList<String>();
		List<String> ITEM_NAME = new ArrayList<String>();
		List<String> ITEM_HREF = new ArrayList<String>();
		List<String> ITEM_IMAGESRC = new ArrayList<String>();
		
		for(WebElement e : regprice)
		{
			WebElement price = e.findElement(By.className("price"));
			String myprice = price.getText();
			ITEM_PRICE.add(myprice);
		}
		
		for(WebElement e : images)
		{
			
			String imgsrc = e.getAttribute("src");
			ITEM_IMAGESRC.add(imgsrc);
			
		}
		
		for(WebElement e : newlist1)                                //creates new object that holds data
		{
			WebElement atag = e.findElement(By.tagName("a"));
			String itemtitle = atag.getAttribute("title");
			String itemhref = atag.getAttribute("href");
			ITEM_NAME.add(itemtitle);
			ITEM_HREF.add(itemhref);
		}
		
		System.out.println(ITEM_NAME.size() + " " +  ITEM_PRICE.size() + " " + ITEM_HREF.size() +" "+ITEM_IMAGESRC.size());
		
		if(ITEM_NAME.size() != ITEM_PRICE.size() || ITEM_PRICE.size()  != ITEM_HREF.size() || ITEM_NAME.size()   != ITEM_HREF.size() || ITEM_IMAGESRC.size()   != ITEM_HREF.size() )
		{
			System.out.println("The data didnt match at page with href:" + BRAND_LIST.get(i).Href);
			return -1;
		}
		
		
		
		
		
		for(int j = 0; j < ITEM_NAME.size(); j++)
		{
			
			
			System.out.println(ITEM_NAME.get(j) + " " +  ITEM_PRICE.get(j) + " " + ITEM_HREF.get(j) + " " + ITEM_IMAGESRC.get(j));
			
			String s = ITEM_IMAGESRC.get(j).replaceAll("/small_image", "/image");
			URL smallurl = new URL(ITEM_IMAGESRC.get(j));
			URL bigurl = new URL(s.replaceAll("/120x", "/275x"));
			String temp = ITEM_NAME.get(j).replaceAll(",", " with ").replaceAll("/", " & ").replaceAll(":", " ").replaceAll("\\*"," ").replaceAll("\""," ");
	        
			String imgloc = /**Path**/"";
	        
			try{
				BufferedImage bufImgOne = ImageIO.read(bigurl);
				ImageIO.write(bufImgOne, "png", new File(imgloc));
			
			}
	        
			catch(Exception e){
				
				BufferedImage bufImgOne = ImageIO.read(smallurl);
				ImageIO.write(bufImgOne, "png", new File(imgloc));
				System.out.println("HAD TO USE THUMBNAIL PHOTO");
				
			
			}
	        
	        
	        
	        
	        
	        ITEM_LIST.add(new productdata(ITEM_NAME.get(j), ITEM_PRICE.get(j), ITEM_HREF.get(j), ITEM_IMAGESRC.get(j), imgloc));
	        
		    try
		    {
			    if(log.exists()==false)
			    {
		           
		            log.createNewFile();
			    }
			    PrintWriter out = new PrintWriter(new FileWriter(log, true));
			    out.append(temp + "," +  ITEM_PRICE.get(j) + "," + ITEM_HREF.get(j) + ","  + ITEM_IMAGESRC.get(j) + ","  + imgloc +  "\n");
			    out.close();
		    }
		    catch(IOException e)
		    {
		        System.out.println("COULD NOT LOG!!");
			    }
			}
		}
		catch(Exception e)
		{
			
			System.out.println("THIS BRAND HAS NO ITEMS (PLEASE REMOVE EMPTY PHOTO FOLDER LATER)");
			
		}
			
			return 0;
		}
	}




