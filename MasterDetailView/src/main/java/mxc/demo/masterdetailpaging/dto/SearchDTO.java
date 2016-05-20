package mxc.demo.masterdetailpaging.dto;

/**
 * Global search value for a JQuery Datatables server-side request. Note that this
 * isn't actually used in this project, since we have implemented column-specific
 * filtering and switched off the global search.
 */
public class SearchDTO {
    /**
     * Global search value. To be applied to all columns which have searchable as true.
     */
    private String value;

    /**
     * <code>true</code> if the global filter should be treated as a regular expression for advanced searching, false otherwise. Note that normally server-side
     * processing scripts will not perform regular expression searching for performance reasons on large data sets, but it is technically possible and at the
     * discretion of your script.
     */
    private boolean regex;

    public SearchDTO() {}
	public SearchDTO(String value, boolean regex) {
		this.value = value;
		this.regex = regex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isRegex() {
		return regex;
	}

	public void setRegex(boolean regex) {
		this.regex = regex;
	}
}
