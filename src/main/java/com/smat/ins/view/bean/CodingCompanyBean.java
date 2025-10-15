package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.Company;
import com.smat.ins.model.service.CompanyService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingCompanyBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1072766500623909330L;

	// #region "properties"

	private Company company;

	private List<Company> companies;
	
	private List<Company> selectedCompanyList;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
	
	public List<Company> getSelectedCompanyList() {
		return selectedCompanyList;
	}

	public void setSelectedCompanyList(List<Company> selectedCompanyList) {
		this.selectedCompanyList = selectedCompanyList;
	}

	// #endregion


	// #region "services"
	private CompanyService companyService;
	private LocalizationService localizationService;
	// #endregion

	public CodingCompanyBean() {
		super();
		// TODO Auto-generated constructor stub
		company=new Company();
		companies=new ArrayList<>();
		companyService=(CompanyService) BeanUtility.getBean("companyService");
		localizationService=(LocalizationService) BeanUtility.getBean("localizationService");
	}

	@PostConstruct
	public void init() {
		try {
			
			companies=companyService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-company");
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void destroy() {

	}
	
	public void openNew() {
		company = new Company();
		
	}
	
	public boolean doValidate() {
		boolean result = true;
		if (company == null || company.getName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("name") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	
	public void save() {
		if (doValidate()) {
			try {
				Integer maxCompanyCode=companyService.getMaxCompanyCode();
				company.setCode(String.format("%0" + 3 + "d", maxCompanyCode + 1));
				companyService.saveOrUpdate(company);
				companies=companyService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-company");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-company");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelected() {
		return this.selectedCompanyList!= null && !this.selectedCompanyList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelected()) {
			int size = this.selectedCompanyList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("companySelectedNum")
					: localizationService.getInterfaceLabel().getString("oneCompanySelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void delete() {
		try {

			companyService.delete(company);
			this.company = null;
			companies = companyService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-company");
			PrimeFaces.current().executeScript("PF('widgetVarCompany').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-company");
			e.printStackTrace();
		}
	}

	public void deleteSelectedJobPosition() {
		try {   
			companyService.delete(selectedCompanyList);
			this.selectedCompanyList = null;
			companies = companyService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-company");
			PrimeFaces.current().executeScript("PF('widgetVarCompany').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-company");
			e.printStackTrace();
		}

	}
	
	

}
