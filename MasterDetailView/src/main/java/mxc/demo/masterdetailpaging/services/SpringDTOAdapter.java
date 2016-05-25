package mxc.demo.masterdetailpaging.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;

import mxc.demo.masterdetailpaging.dto.ColumnDTO;
import mxc.demo.masterdetailpaging.dto.OrderDTO;

public final class SpringDTOAdapter {

	public static final SpringDTOAdapter instance = new SpringDTOAdapter();

	private SpringDTOAdapter() {
	}

	/**
	 * Adapt the sorting information from the JQuery Datatables DTO to a Sort
	 * object as expected by the Spring repository.
	 * 
	 * @param columns
	 *            list of column metadata from the client's request
	 * @param orderings
	 *            list of ordering information from the client's request
	 * @return ordering information that can be used to access the repository
	 */
	public List<Sort.Order> getColumnOrderings(List<ColumnDTO> columns, Iterable<OrderDTO> orderings) {

		List<Sort.Order> orders = new ArrayList<>();

		for (OrderDTO orderDTO : orderings) {
			int column = orderDTO.getColumn();
			// Assuming what we get back from the client is correct!
			String property = columns.get(column).getData();
			Sort.Direction direction = Sort.Direction.fromStringOrNull(orderDTO.getDir().toUpperCase());
			// Sort.Direction direction =
			// Sort.Direction.valueOf(orderDTO.getDir());
			if (direction != null) {
				Sort.Order order = new Sort.Order(direction, property);
				orders.add(order);
			}
		}

		return orders;
	}

}
