package entity;

import java.io.Serializable;
import java.util.List;

//分页的实体类分装
//由于使用到了mybatis的的分页技术我们只需要将这个分页分装一个总记录数,以及数据的集合即可
public class PageResult<T> implements Serializable{
    public PageResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PageResult(Long pagetotal, List<T> pageRows) {
		super();
		this.pagetotal = pagetotal;
		this.pageRows = pageRows;
	}
	private  Long pagetotal;
    private  List<T> pageRows;

	public Long getPagetotal() {
		return pagetotal;
	}
	public void setPagetotal(Long pagetotal) {
		this.pagetotal = pagetotal;
	}
	public List<T> getPageRows() {
		return pageRows;
	}
	public void setPageRows(List<T> pageRows) {
		this.pageRows = pageRows;
	}
    
}
