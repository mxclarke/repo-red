package mxc.demo.campus.view;

import java.math.BigDecimal;

public interface ViewCourse {
	
	int getId();
	
	String getName();
	
	String getDescription();
	
	//@JsonSerialize(using=JsonMoneySerialiser.class)
	BigDecimal getCost();
	
	String getLecturerName();
	
	void setLecturerName(String lecturerName);

}
