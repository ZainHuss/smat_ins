package com.smat.ins.model.data.lazyloading;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;


import javax.faces.convert.Converter;
import javax.faces.model.DataModelListener;

import org.apache.commons.collections4.ComparatorUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import com.smat.ins.model.entity.SysUser;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.model.data.lazysorting.LazyUserSorter;
import com.smat.ins.model.service.SysUserService;


public class LazyUserDataModel extends LazyDataModel<SysUser> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8491498469530057384L;
	private List<SysUser> dataSource;
	private SysUserService sysUserService;
	
	private Long total;
	
	public LazyUserDataModel() {
		super();
		sysUserService=(SysUserService) BeanUtility.getBean("sysUserService");
	}

	public LazyUserDataModel(List<SysUser> dataSource) {
		super();
		this.dataSource = dataSource;
		sysUserService=(SysUserService) BeanUtility.getBean("sysUserService");
	}
	
	
	
	@Override
	public int getPageSize() {
		// TODO Auto-generated method stub
		return super.getPageSize();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return super.getRowCount();
	}

	@Override
	public SysUser getRowData() {
		// TODO Auto-generated method stub
		return super.getRowData();
	}

	

	@Override
	public int getRowIndex() {
		// TODO Auto-generated method stub
		return super.getRowIndex();
	}
	
	@Override
	public boolean isRowAvailable() {
		// TODO Auto-generated method stub
		return super.isRowAvailable();
	}


	@Override
	public List<SysUser> getWrappedData() {
		// TODO Auto-generated method stub
		return super.getWrappedData();
	}

	@Override
	public SysUser getRowData(String rowKey) {
		for (SysUser sysUser : dataSource) {
			if (sysUser.getId() == Integer.parseInt(rowKey)) {
				return sysUser;
			}
		}

		return null;
	}

	@Override
	public String getRowKey(SysUser sysUser) {
		return String.valueOf(sysUser.getId());
	}
	
	

	@Override
	public void forEach(Consumer<? super SysUser> action) {
		// TODO Auto-generated method stub
		super.forEach(action);
	}

	@Override
	public Spliterator<SysUser> spliterator() {
		// TODO Auto-generated method stub
		return super.spliterator();
	}

	@Override
	public void setWrappedData(Object list) {
		// TODO Auto-generated method stub
		super.setWrappedData(list);
	}

	@Override
	public void setRowIndex(int rowIndex) {
		// TODO Auto-generated method stub
		super.setRowIndex(rowIndex);
	}

	@Override
	public void setPageSize(int pageSize) {
		// TODO Auto-generated method stub
		super.setPageSize(pageSize);
	}

	@Override
	public void setRowCount(int rowCount) {
		// TODO Auto-generated method stub
		super.setRowCount(rowCount);
	}

	@Override
	public void setConverter(Converter converter) {
		// TODO Auto-generated method stub
		super.setConverter(converter);
	}

	@Override
	public void addDataModelListener(DataModelListener listener) {
		// TODO Auto-generated method stub
		super.addDataModelListener(listener);
	}

	@Override
	public DataModelListener[] getDataModelListeners() {
		// TODO Auto-generated method stub
		return super.getDataModelListeners();
	}

	@Override
	public void removeDataModelListener(DataModelListener listener) {
		// TODO Auto-generated method stub
		super.removeDataModelListener(listener);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	@Override
	public int count(Map<String, FilterMeta> filterBy) {
		// TODO Auto-generated method stub
		try {
			String filterValue="";
			for (Map.Entry<String, FilterMeta> entry : filterBy.entrySet()) {
				filterValue=entry.getValue().getFilterValue().toString();
			}
			total=sysUserService.getCountByGloblFilter(filterValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total.intValue();
	}

	@Override
	public List<SysUser> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
		// TODO Auto-generated method stub
		try {
			String filterValue="";
			for (Map.Entry<String, FilterMeta> entry : filterBy.entrySet()) {
				filterValue=entry.getValue().getFilterValue().toString();
			}
			
			String sortField="";
			String sortOrder="";
			for (Map.Entry<String, SortMeta> entry : sortBy.entrySet()) {
				sortField=entry.getValue().getField().toString();
				sortOrder=entry.getValue().getOrder().toString();
			}
			dataSource = sysUserService.getPagesByGloblFilter(offset, pageSize, filterValue,sortField,sortOrder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return dataSource;
	}


	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
