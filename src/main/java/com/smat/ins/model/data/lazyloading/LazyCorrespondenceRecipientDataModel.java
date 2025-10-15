package com.smat.ins.model.data.lazyloading;

import java.util.ArrayList;
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

import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.model.data.lazysorting.LazyCorrespondenceRecipientSorter;
import com.smat.ins.model.service.CorrespondenceRecipientService;

public class LazyCorrespondenceRecipientDataModel extends LazyDataModel<CorrespondenceRecipient> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 803015932889399547L;
	private List<CorrespondenceRecipient> dataSource;
	private CorrespondenceRecipientService correspondenceRecipientService;
	private SysUser sysUserRecipient;
	private CorrespondenceState correspondenceState;
	private Long total;

	public LazyCorrespondenceRecipientDataModel(List<CorrespondenceRecipient> dataSource) {
		super();
		this.dataSource = dataSource;
	}

	public LazyCorrespondenceRecipientDataModel(SysUser sysUserRecipient, CorrespondenceState correspondenceState) {
		super();
		this.sysUserRecipient = sysUserRecipient;
		this.correspondenceState = correspondenceState;
		correspondenceRecipientService = (CorrespondenceRecipientService) BeanUtility
				.getBean("correspondenceRecipientService");

	}

	public LazyCorrespondenceRecipientDataModel(List<CorrespondenceRecipient> dataSource, SysUser sysUserRecipient,
			CorrespondenceState correspondenceState) {
		super();
		this.dataSource = dataSource;
		this.sysUserRecipient = sysUserRecipient;
		this.correspondenceState = correspondenceState;
		correspondenceRecipientService = (CorrespondenceRecipientService) BeanUtility
				.getBean("correspondenceRecipientService");
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
	public CorrespondenceRecipient getRowData() {
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
	public List<CorrespondenceRecipient> getWrappedData() {
		// TODO Auto-generated method stub
		return super.getWrappedData();
	}

	@Override
	public CorrespondenceRecipient getRowData(String rowKey) {
		for (CorrespondenceRecipient correspondenceRecipient : dataSource) {
			if (correspondenceRecipient.getId() == Integer.parseInt(rowKey)) {
				return correspondenceRecipient;
			}
		}

		return null;
	}

	@Override
	public String getRowKey(CorrespondenceRecipient correspondenceRecipient) {
		return String.valueOf(correspondenceRecipient.getId());
	}

	@Override
	public void forEach(Consumer<? super CorrespondenceRecipient> action) {
		// TODO Auto-generated method stub
		super.forEach(action);
	}

	@Override
	public Spliterator<CorrespondenceRecipient> spliterator() {
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
			total = correspondenceRecipientService.getInboxCountBySysUser(sysUserRecipient, correspondenceState);
			return total.intValue();
		} catch (Exception e) {
			// TODO: handle exception
			total = Long.valueOf(0);
			e.printStackTrace();
			return total.intValue();
		}
	}

	@Override
	public List<CorrespondenceRecipient> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
			Map<String, FilterMeta> filterBy) {
		// TODO Auto-generated method stub
		try {
			dataSource = correspondenceRecipientService.getInboxBySysUser(sysUserRecipient, correspondenceState, offset,
					pageSize);

			// sort
			if (!sortBy.isEmpty()) {
				List<Comparator<CorrespondenceRecipient>> comparators = sortBy.values().stream()
						.map(o -> new LazyCorrespondenceRecipientSorter(o.getField(), o.getOrder()))
						.collect(Collectors.toList());
				Comparator<CorrespondenceRecipient> cp = ComparatorUtils.chainedComparator(comparators); // from apache
				dataSource.sort(cp);
			}

			return dataSource;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ArrayList<CorrespondenceRecipient>();
		}
	}

	public SysUser getSysUserRecipient() {
		return sysUserRecipient;
	}

	public void setSysUserRecipient(SysUser sysUserRecipient) {
		this.sysUserRecipient = sysUserRecipient;
	}

	public CorrespondenceState getCorrespondenceState() {
		return correspondenceState;
	}

	public void setCorrespondenceState(CorrespondenceState correspondenceState) {
		this.correspondenceState = correspondenceState;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
