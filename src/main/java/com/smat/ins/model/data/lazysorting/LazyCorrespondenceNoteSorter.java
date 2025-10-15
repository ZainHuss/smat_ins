package com.smat.ins.model.data.lazysorting;

import java.util.Comparator;

import org.primefaces.model.SortOrder;

import com.smat.ins.model.entity.CorrespondenceNote;
import com.smat.ins.util.PrimeFacesUtility;

public class LazyCorrespondenceNoteSorter implements Comparator<CorrespondenceNote> {
	private String sortField;
    private SortOrder sortOrder;

    public LazyCorrespondenceNoteSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public int compare(CorrespondenceNote o1, CorrespondenceNote o2) {
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
