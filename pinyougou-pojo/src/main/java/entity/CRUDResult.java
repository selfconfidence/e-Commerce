package entity;

import java.io.Serializable;

public class CRUDResult implements Serializable{
	private Boolean flag;
	private String messAge;
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public String getMessAge() {
		return messAge;
	}
	public void setMessAge(String messAge) {
		this.messAge = messAge;
	}
	public CRUDResult(Boolean flag, String messAge) {
		super();
		this.flag = flag;
		this.messAge = messAge;
	}
	public CRUDResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
