package dummy;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
public class AbstractWebDriver {
	
	protected WebDriver driver;
	String ZebraQty;
	String LionQty; 
	String ElephQty; 
	String GirafQty;
	Locale subTotalUSD = Locale.US;
	Number SubtotalNumber;
	Number TotalPay;
	//int total=totalPriceEleph+totalPriceGiraf+totalPriceLion+totalPriceZebra;
	double totalTax;
	private String stateValue;
	private double total;

	@Before
	public void openPage(){
		System.setProperty("webdriver.chrome.driver", "C://Users//IPL//Desktop//Jia//BrowserDrivers//chromedriver.exe");
		driver=new ChromeDriver();
		driver.get("https://jungle-socks.herokuapp.com/");
	}
	
	@After
	public void closePage(){
		driver.close();
	}
	
	public void enterZebraQty(String ZebraQty){
		this.ZebraQty=ZebraQty;
		driver.findElement(By.id("line_item_quantity_zebra")).sendKeys(ZebraQty);
	}
	
	public void enterLionQty(String LionQty){
		this.LionQty=LionQty;
		driver.findElement(By.id("line_item_quantity_lion")).sendKeys(LionQty);
	}
	
	public void enterElephQty(String ElephQty){
		this.ElephQty=ElephQty;
		driver.findElement(By.id("line_item_quantity_elephant")).sendKeys(ElephQty);
	}
	
	public void enterGirafQty(String GirafQty){
		this.GirafQty=GirafQty;
		driver.findElement(By.id("line_item_quantity_giraffe")).sendKeys(GirafQty);
	}
	
	public void selectStateByText(String stateTet){
        Select statesList = new Select(driver.findElement(By.name("state")));   
        statesList.selectByVisibleText(stateTet);	
	}
	
	public void selectStateByIndex(int stateIndex){
        Select statesList = new Select(driver.findElement(By.name("state")));   
        statesList.selectByIndex(stateIndex);	
	}
	
	public void selectStateByValue(String stateValue){
		this.stateValue=stateValue;
        Select statesList = new Select(driver.findElement(By.name("state")));   
        statesList.selectByValue(stateValue);
	}
	
	public String getStateValue(){
		return this.stateValue;
	}
	
	public void checkOut(){
		driver.findElement(By.name("commit")).click();
	}
	
	public void waitForElement(String xPath){
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
	}
	
	public void getPrice(){
		driver.findElement(By.xpath("/html/body/form/table[1]/tbody/tr[2]/td[2]")).getText();
	}
		
	public void verifyInven(String price, String quantity, String Subtotal, String total){
		price=driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[2]/td[2]")).getText();//price
		int price_One=Integer.parseInt(price);
		System.out.println(price_One);
		
		
	}
	
	public int getInventoryZebra(){
	return Integer.parseInt(ZebraQty);	
	}
	
	public int getInventoryLion(){
	int v=Integer.parseInt(LionQty); 
	return v;	
	}
	
	public int getInventoryEleph(){
	int v=Integer.parseInt(ElephQty); 
	return v;	
	}
	
	public int getInventoryGiraf(){
	int v=Integer.parseInt(GirafQty); 
	return v;	
	}

	public void verifyTotalPrice(){
		String subTotal= driver.findElement(By.xpath("//td[@id='subtotal']")).getText();
		String expectedTotal= driver.findElement(By.xpath("//td[@id='total']")).getText();
		
		int totalPriceZebra=0;
		int totalPriceGiraf=0;
		int totalPriceEleph=0;
		int totalPriceLion=0;

		if(isElementPresent(By.xpath("//tr[@class='line_item zebra']/td[2]"))){
			totalPriceZebra=13*getInventoryZebra();
		}
		if(isElementPresent(By.xpath("//tr[@class='line_item elephant']/td[2]"))){
			totalPriceEleph=35*getInventoryEleph();
		}
		
		//element is not presented, will cause TF: 
		if(isElementPresent(By.xpath("//tr[@class='line_item lion]/td[2]"))){
			totalPriceLion=20*getInventoryLion();
		}
		
		if(isElementPresent(By.xpath("//tr[@class='line_item giraffe']/td[2]"))){
			totalPriceGiraf=17*getInventoryGiraf();
		}
		
		
		//verify total
		try {
			
			totalTax=(totalPriceEleph+totalPriceGiraf+totalPriceLion+totalPriceZebra)*getTax(getStateValue());
			SubtotalNumber = NumberFormat.getCurrencyInstance(subTotalUSD).parse(subTotal);
			TotalPay=NumberFormat.getCurrencyInstance(subTotalUSD).parse(expectedTotal);
			total=totalTax+SubtotalNumber.doubleValue();
			 waitForElement("//td[@id='subtotal']");
			 Assert.assertEquals(total, TotalPay.doubleValue());
		} catch (ParseException e) {
		}		
		
		}
	
	

	public boolean isElementPresent(By...locators){
		try{
		for(By locator:locators){
			
			try{
				return driver.findElements(locator).size()>0;
			}catch(StaleElementReferenceException x){
				continue;
			}catch(NoSuchElementException ns){
				continue;
			}catch(InvalidElementStateException es){
				throw new WebDriverException("Cannot find element: "+ locator, es);
			}catch(NullPointerException npe){
				throw new WebDriverException("Cannot find element: "+ locator, npe);
			}
		}
		return false;		
		}finally{
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);//wait for 5 second default;
 }
}
	
	public double getTax(String stateValue) {
		double tax = 0.05;
		if(stateValue.equals("CA")){
		 tax=0.08;
		}else if(stateValue.equals("NY")){
		 tax=0.06;
		}else if(stateValue.equals("MN")){
		 tax=0.00;
		}	
		return tax;
		
	}
	
}


