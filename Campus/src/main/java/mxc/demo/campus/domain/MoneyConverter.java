package mxc.demo.campus.domain;

import java.math.BigDecimal;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * NOT CURRENTLY IN USE, although I'm leaving it in for now as I hope to resurrect it.
 * 
 * Converts MonetaryAmount to a database column.
 *
 * There are a couple of problems with this converter. THe first is that we're storing 
 * money amounts as a string, e.g. "UD:12.4" for $12.40, "AUD:11"for $11.00, and so on. 
 * HOwever, to get this to work, we'd have to introduce a custom sort on the database column or the
 * repository. It would be better to store currency in two columns, with a 
 * foreign key into a currency type table.
 * 
 * THis is the sort of thing that could be added later, when multiple
 * currencies become an issue.
 * 
 * We need to inject the MoneyService, but Spring does not inject dependencies into objects 
 * created using the new operator (as this MoneyConverter is, if you have a look at some of 
 * the domain objects). Spring doesn't inject dependencies into objects unless you've retrieved 
 * them from the ApplicationContext, since it doesn't know about their existence. Therefore
 * I'd need to set this up as a Spring bean.
 * 
 * http://stackoverflow.com/questions/16471636/bean-injection-inside-a-jpa-entity
 */

@Converter
//@Configurable(preConstruction = true)
//@Configurable(autowire=Autowire.BY_TYPE, preConstruction = true)
public class MoneyConverter implements AttributeConverter<BigDecimal, String> {
	
//	@Autowired
//	private MoneyService moneyService;
	
	// String.split uses as regular expression so be careful what you use here
	private static final String SEPARATOR = ":";
	
	@Override
	public String convertToDatabaseColumn(BigDecimal moneyAmount) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("AUD");
		//sb.append(moneyService.getCurrencyCode());
		//sb.append(moneyAmount.getCurrency().getCurrencyCode());
		sb.append(SEPARATOR);
		sb.append(moneyAmount);
		//sb.append(moneyAmount.getNumber());
		return sb.toString();
	}

	@Override
	public BigDecimal convertToEntityAttribute(String dbStr) {
		
		String[] moneyString = dbStr.split(SEPARATOR);
		
		String currency = moneyString[0];
		BigDecimal amount = new BigDecimal(moneyString[1]);
		
//		MonetaryAmountFactory<?> amountFactory = Monetary.getDefaultAmountFactory().setCurrency(currency);
//		MonetaryAmount monetaryAmount = amountFactory.setNumber(amount).create();
//		
//		return monetaryAmount;
		
		return amount;
	}

}
