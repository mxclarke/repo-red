package mxc.demo.campus.services;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * There are a number of utilities around (e.g. Spring, Apache commons) for
 * handling bean operations. By having our own we can choose the most efficient,
 * and change them as needed.
 */
@Service
public class BeanManagerService {

	private final ObjectMapper jsonMapper;

	@Autowired
	public BeanManagerService(ObjectMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

	/**
	 * Copy only the properties in the given list from source to dest.
	 * @param source
	 * @param dest
	 * @param properties a list of names of properties; if empty, this method will do nothing
	 */
	public void copyOnlyProperties(final Object source, final Object dest, final String... properties) {

		final BeanWrapper srcWrapper = createBeanWrapper(source);
		final BeanWrapper destWrapper = createBeanWrapper(dest);

		for (final String propertyName : properties) {
			destWrapper.setPropertyValue(propertyName, srcWrapper.getPropertyValue(propertyName));
		}
	}

	/**
	 * Copy all the properties found in source to any matching in dest, with the exception of those in
	 * the given list. 
	 * @param source
	 * @param dest
	 * @param ignoreProperties a list of names of properties; if empty, all the properties will be copied
	 */
	public void copyPropertiesExcept(final Object source, final Object dest, final String... ignoreProperties) {
		BeanUtils.copyProperties(source, dest, ignoreProperties);
	}

	/**
	 * @param source
	 * @return a list of names of properties whose values are null
	 */
	public List<String> getNullPropertyNames(Object source) {

		final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
		return Stream.of(wrappedSource.getPropertyDescriptors()).map(FeatureDescriptor::getName)
				.filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
				.collect(Collectors.toList());
	}

	public <T> T mapToInterface(Object source, Class<T> targetClass) throws JsonProcessingException, IOException {

		ObjectWriter ow = jsonMapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(source); // JsonProcessingException
		String canonical = targetClass.getCanonicalName();

		T result = jsonMapper.readValue(json, jsonMapper.getTypeFactory().constructFromCanonical(canonical));
		return result;
	}

	public <T> List<T> mapToInterface(List<?> sourceList, Class<T> targetClass) throws IOException {

		ObjectWriter ow = jsonMapper.writer().withDefaultPrettyPrinter();

		String json = ow.writeValueAsString(sourceList); // JsonProcessingException

		List<T> list = jsonMapper.readValue(json,
				jsonMapper.getTypeFactory().constructCollectionType(List.class, targetClass));

		return list;
	}

	private BeanWrapper createBeanWrapper(final Object target) {
		return PropertyAccessorFactory.forBeanPropertyAccess(target);
	}
}
