package com.smat.ins.model.data.lazysorting;

import java.util.Comparator;

import org.primefaces.model.SortOrder;

import com.smat.ins.model.entity.SysUser;
import com.smat.ins.util.PrimeFacesUtility;

public class LazyUserSorter implements Comparator<SysUser> {
	private String sortField;
    private SortOrder sortOrder;

    public LazyUserSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public int compare(SysUser o1, SysUser o2) {
		// TODO Auto-generated method stub
		try {
            Object value1 = PrimeFacesUtility.getPropertyValueViaReflection(o1, sortField);
            Object value2 = PrimeFacesUtility.getPropertyValueViaReflection(o2, sortField);
			@SuppressWarnings("rawtypes")
			int value = ((Comparable) value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

}
