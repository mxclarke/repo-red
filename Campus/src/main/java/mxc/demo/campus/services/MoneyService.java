package mxc.demo.campus.services;

import java.math.BigDecimal;

/**
 * Service for handling money.
 * 
 * Currently using BigDecimal to represent money, but may change this over to the
 * javax.money at a later date. See below.
 * 
 * JSR-354 javax.money will be the standard for Java 9, but at this point in time 
 * (mid-2016) lacks documentation and does not seem to be fully implemented.
 * For example, all attempts to parse a String to MonetaryAmount in local money
 * (Aus) failed.
 * 
 * Another alternative is Joda, but suspect this will become obsolete once Java 9
 * established.
 * 
 * Points about using BigDecimal for money:
 * <ul>
 * <li>BigDecimal is immutable</li>
 * <li>Construct BigDecimals using String argument</li>
 * <li>You can specify a scale, the number of digits after the decimal place</li>
 * <li>Always specify the rounding method when setting the scale</li>
 * <li>Use ROUND_HALF_EVEN (the default) since it minimizes cumulative error when applied repeatedly 
 * over a sequence of calculations</li>
 * <li>Use compareTo(..),not equals()</li>
 * </ul>
 */
public interface MoneyService {
	
	String getCurrencyCode();
	
	Character getCurrencySymbol();
	
	BigDecimal createMoney(Number amount);
	
	String format(Number amount);
	
	BigDecimal parse(String amount);
}
