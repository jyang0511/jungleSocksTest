package JY_Tests;

import org.junit.Test;
public class jungleSocksTest extends AbstractWebDriver {


	@Test
	public void testRegularState(){
		 enterZebraQty("10");
		 selectStateByValue("AZ");
		 checkOut();
         waitForElement("//td[@id='subtotal']");
         verifyTotalPrice();
	}
	
	@Test
	public void testSpecialState(){
		 enterElephQty("1");
		 selectStateByValue("CA");
		 checkOut();
         waitForElement("//td[@id='subtotal']");
         verifyTotalPrice();
	}
}
