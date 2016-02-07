package getlink;

public class productdata {

	String Name;
	String Href;
    String Imgsrc;
	String RegularPrice;
	String ImgFileLocation;
	String SKU;
	String CostPrice;
	String SalePrice;
	String supplierName;
	String suppliercode;
	String quantity;
	String taxCode;
	String mycode;
	
	
	
	public productdata(String name, String price, String href, String imgsrc, String imgloc)
	{
		Name = name;
		Href = href;
		RegularPrice = price;
		Imgsrc = imgsrc;
		ImgFileLocation = imgloc;
	}
	
	

	public String toString() {
	    return Name.replaceAll(",", " with ") + "," + SalePrice + "," + Href;
	}
}
