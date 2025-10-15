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

import com.smat.ins.model.entity.CorrespondenceNote;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.model.data.lazysorting.LazyCorrespondenceNoteSorter;
import com.smat.ins.model.service.CorrespondenceNoteService;

public class LazyCorrespondenceNoteDataModel extends LazyDataModel<CorrespondenceNote> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 803015932889399547L;
	private List<CorrespondenceNote> dataSource;
	private CorrespondenceNoteService correspondenceNoteService;
	private SysUser sysUserSender;
	private CorrespondenceState correspondenceState;
	private Long total;

	public LazyCorrespondenceNoteDataModel(List<CorrespondenceNote> dataSource) {
		super();
		this.dataSource = dataSource;
	}

	public LazyCorrespondenceNoteDataModel(SysUser sysUserSender, CorrespondenceState correspondenceState) {
		super();
		this.sysUserSender = sysUserSender;
		this.correspondenceState = correspondenceState;
		correspondenceNoteService = (CorrespondenceNoteService) BeanUtility.getBean("correspondenceNoteService");

	}

	public LazyCorrespondenceNoteDataModel(List<CorrespondenceNote> dataSource, SysUser sysUserSender,
			CorrespondenceState correspondenceState) {
		super();
		this.dataSource = dataSource;
		this.sysUserSender = sysUserSender;
		this.correspondenceState = correspondenceState;
		correspondenceNoteService = (CorrespondenceNoteService) BeanUtility.getBean("correspondenceNoteService");
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
	public CorrespondenceNote getRowData() {
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
	public List<CorrespondenceNote> getWrappedData() {
		// TODO Auto-generated method stub
		return super.getWrappedData();
	}

	@Override
	public CorrespondenceNote getRowData(String rowKey) {
		for (CorrespondenceNote correspondenceNote : dataSource) {
			if (correspondenceNote.getId() == Integer.parseInt(rowKey)) {
				return correspondenceNote;
			}
		}

		return null;
	}

	@Override
	public String getRowKey(CorrespondenceNote correspondenceNote) {
		return String.valueOf(correspondenceNote.getId());
	}

	@Override
	public void forEach(Consumer<? super CorrespondenceNote> action) {
		// TODO Auto-generated method stub
		super.forEach(action);
	}

	@Override
	public Spliterator<CorrespondenceNote> spliterator() {
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
			total = correspondenceNoteService.getOutBoxCountBySysUser(correspondenceState, sysUserSender);
			return total.intValue();
		} catch (Exception e) {
			// TODO: handle exception
			total = Long.valueOf(0);
			e.printStackTrace();
			return total.intValue();
		}
	}

	@Override
	public List<CorrespondenceNote> load(int offset, int pageSize, Map<String, SortMeta> sortBy,
			Map<String, FilterMeta> filterBy) {
		// TODO Auto-generated method stub
		try {
			dataSource = correspondenceNoteService.getOutBoxBySysUser(correspondenceState, sysUserSender, offset,
					pageSize);

			// sort
			if (!sortBy.isEmpty()) {
				List<Comparator<CorrespondenceNote>> comparators = sortBy.values().stream()
						.map(o -> new LazyCorrespondenceNoteSorter(o.getField(), o.getOrder()))
						.collect(Collectors.toList());
				Comparator<CorrespondenceNote> cp = ComparatorUtils.chainedComparator(comparators); // from apache
				dataSource.sort(cp);
			}

			return dataSource;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ArrayList<CorrespondenceNote>();
		}
	}

	public SysUser getSysUserSender() {
		return sysUserSender;
	}

	public void setSysUserSender(SysUser sysUserSender) {
		this.sysUserSender = sysUserSender;
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
