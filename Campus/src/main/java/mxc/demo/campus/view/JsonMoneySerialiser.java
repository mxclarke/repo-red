package mxc.demo.campus.view;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import mxc.demo.campus.services.MoneyService;

/**
 * Serialises a money amount from a domain object for the view.
 * Note -- javax.money has formatters but not for AUS and it seems no easy way to build.
 * When I tried, got exception with msg No MonetaryAmountFormat for AmountFormatQuery.
 * So just using normal number formatters.
 */
@Component
public class JsonMoneySerialiser extends JsonSerializer<BigDecimal> {

	//private static final DecimalFormat ausFormatter = new DecimalFormat("$00,000.00");
	
	private final MoneyService moneyService;
	
	@Autowired
	public JsonMoneySerialiser(MoneyService moneyService) {
		this.moneyService = moneyService;
	}
	
	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
	 */
	@Override
	public void serialize(BigDecimal amount, JsonGenerator generator, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		String moneyStr = moneyService.format(amount);
		generator.writeString(moneyStr);
//		if ( amount.getCurrency().getCurrencyCode().equalsIgnoreCase("AUD") ) {
//			generator.writeString(ausFormatter.format(amount.getNumber().doubleValue()));
//		} else {
//			generator.writeString(amount.toString());
//		}
	}

}
