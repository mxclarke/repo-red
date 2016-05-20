package mxc.demo.masterdetailpaging.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models a JSON posted request sent from a JQuery Datables client.
 * 
 * The payload is described here: https://datatables.net/manual/server-side.
 * 
 * NOTE: the contents of this package could go into a common package/JAR for for
 * client-server DTOs.
 * 
 * Am indebted to information on this page:
 * http://stackoverflow.com/questions/28197817/spring-mvc-datatables-1-10-
 * parameters-binding
 *
 */
public final class JQueryDatatablesRequest {

	/**
	 * Draw counter. This is used by DataTables to ensure that the Ajax returns
	 * from server-side processing requests are drawn in sequence by DataTables
	 * (Ajax requests are asynchronous and thus can return out of sequence).
	 * This is used as part of the draw return parameter (see below).
	 */
	private int draw;

	/**
	 * Paging first record indicator. This is the start point in the current
	 * data set (0 index based - i.e. 0 is the first record).
	 */
	private int start;

	/**
	 * Number of records that the table can display in the current draw. It is
	 * expected that the number of records returned will be equal to this
	 * number, unless the server has fewer records to return. Note that this can
	 * be -1 to indicate that all records should be returned (although that
	 * negates any benefits of server-side processing!)
	 */
	private int length;

	/**
	 * @see SearchDTO
	 */
	private SearchDTO search;

	/**
	 * @see OrderDTO
	 */
	@JsonProperty("order")
	private List<OrderDTO> orders;

	/**
	 * @see ColumnDTO
	 */
	private List<ColumnDTO> columns;

	public JQueryDatatablesRequest() {
	}

	public JQueryDatatablesRequest(int draw, int start, int length, SearchDTO search, List<OrderDTO> orders,
			List<ColumnDTO> columns) {
		this.draw = draw;
		this.start = start;
		this.length = length;
		this.search = search;
		this.orders = orders;
		this.columns = columns;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public SearchDTO getSearch() {
		return search;
	}

	public void setSearch(SearchDTO search) {
		this.search = search;
	}

	public List<OrderDTO> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderDTO> orders) {
		this.orders = orders;
	}

	public List<ColumnDTO> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnDTO> columns) {
		this.columns = columns;
	}
}
