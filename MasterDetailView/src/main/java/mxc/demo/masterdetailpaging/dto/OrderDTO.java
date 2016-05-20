package mxc.demo.masterdetailpaging.dto;

/**
 * Ordering information (ascending or descending) for a single table column in a
 * JQuery Datatables server-side request.
 */
public class OrderDTO {

	public enum Direction {
		asc, desc
	};

	/**
	 * Column to which ordering should be applied. This is an index reference to
	 * the columns array of information that is also submitted to the server.
	 */
	private int column;

	/**
	 * Ordering direction for this column. It will be <code>asc</code> or
	 * <code>desc</code> to indicate ascending ordering or descending ordering,
	 * respectively.
	 */
	private String dir;

	public OrderDTO() {
	}

	public OrderDTO(int column, String dir) {
		this.column = column;
		this.dir = dir;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
}
