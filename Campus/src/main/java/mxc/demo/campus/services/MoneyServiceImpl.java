package mxc.demo.campus.services;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.springframework.stereotype.Service;

/* (non-Javadoc)
 * @see mxc.demo.campus.services.MoneyService
 */
@Service
public class MoneyServiceImpl implements MoneyService {
	
	// ISO 639-2 Code is "aus", there is NO ISO 639-1 Code
	// http://www.loc.gov/standards/iso639-2/php/code_list.php
	// Country codes are ISO 3166, here, http://www.iso.org/iso/country_codes, 
	// FOr aus the country code is AU
	//private static final Locale ausLocale = new Locale("aus","AU");
	
	private final DecimalFormat formatter = new DecimalFormat("###,###.##");
	private final NumberFormat displayFormatter = new DecimalFormat("$00,000.00");
	
	public MoneyServiceImpl() {
		formatter.setParseBigDecimal(true);
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.MoneyService#getCurrencyCode()
	 */
	@Override
	public String getCurrencyCode() {
		return "AUD";
	}


	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.MoneyService#getCurrencySymbol()
	 */
	@Override
	public Character getCurrencySymbol() {
		return '$';
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.MoneyService#createMoney(java.lang.Number)
	 */
	@Override
	public BigDecimal createMoney(Number amount) {
		return new BigDecimal(amount.toString());
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.MoneyService#format(java.math.BigDecimal)
	 */
	@Override
	public String format(Number amount) {
		return displayFormatter.format(amount);
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.MoneyService#parse(java.lang.String)
	 */
	@Override
	public BigDecimal parse(String amount) {

		amount = amount.replace(getCurrencySymbol().toString(), "");
		ParsePosition parsePosition = new ParsePosition(0);
		BigDecimal num = (BigDecimal) formatter.parse(amount, parsePosition);
			
		return num;
	}
}
