package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.DragDropEvent;

import com.smat.ins.model.entity.SysPermission;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysRolePermission;
import com.smat.ins.model.service.SysPermissionService;
import com.smat.ins.model.service.SysRolePermissionService;
import com.smat.ins.model.service.SysRoleService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@ViewScoped
public class SysRoleBean implements Serializable {

	//#region "properties"
	private static final long serialVersionUID = -6940976497901194753L;

	// entity object
	private SysRole sysRole;

	private Short sysRoleID;
	private String sysRoleStr;
	
	// list
	private List<SysRole> sysRoleList;
	private List<SysRole> selectedSysRoleList;
	private List<SysRolePermission> rolePermissionList;
	private List<SysPermission> permissionsSource;
	private List<SysPermission> permissionsTarget;

	// Service
	private SysRoleService sysRoleService;
	private SysPermissionService sysPermissionService;
	private SysRolePermissionService sysRolePermissionService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {
		sysRoleStr = UtilityHelper.getRequestParameter("ru");
		try {
			if (sysRoleStr != null) {

				sysRoleID = Short.valueOf(UtilityHelper.decipher(sysRoleStr));
				sysRole = sysRoleService.findById(sysRoleID);
				if (sysRole != null) {
					rolePermissionList = sysRolePermissionService.getBySysRoleWithDetails(sysRole.getId());
				}

			}

		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}

	public SysRoleBean() throws Exception {
		sysRole = new SysRole();
		
		sysRoleList = new ArrayList<SysRole>();
		selectedSysRoleList = new ArrayList<SysRole>();
		rolePermissionList = new ArrayList<SysRolePermission>();
		permissionsSource = new ArrayList<SysPermission>();
		permissionsTarget = new ArrayList<SysPermission>();
		
		sysRoleService = (SysRoleService) BeanUtility.getBean("sysRoleService");
		sysPermissionService = (SysPermissionService) BeanUtility.getBean("sysPermissionService");
		sysRolePermissionService = (SysRolePermissionService) BeanUtility.getBean("sysRolePermissionService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	
		sysRoleList = sysRoleService.findAll();
		permissionsSource = sysPermissionService.findAll();
	}

	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.sysRole = new SysRole();
	}

	public boolean doValidate() throws Exception {
		boolean result = true;
		
		if (sysRole.getName() == null || sysRole.getName().equals("")) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("name") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (permissionsTarget.size() <= 0) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("permissions") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));

			result = false;
		}

		return result;
	}
	public boolean doValidateUpdate() throws Exception {
		boolean result = true;
		if (sysRole.getName() == null || sysRole.getName().equals("")) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("name") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (permissionsTarget.size() <= 0) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("permissions") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));

			result = false;
		}

		return result;
	}
	public String save() throws Exception {
		String str= "";
		if (doValidate()) {
			List<SysRolePermission> sysRolePermissions = new ArrayList<SysRolePermission>();
			for (SysPermission sysPermession : permissionsTarget) {
				
				SysRolePermission sysRolePermission = new SysRolePermission();
				sysRolePermission.setSysPermission(sysPermession);
				sysRolePermission.setSysRole(sysRole);
				sysRolePermissions.add(sysRolePermission);
			}

			sysRole.setSysRolePermissions(new HashSet<SysRolePermission>(sysRolePermissions));

			if (sysRoleService.save(sysRole) != null) {
				sysRoleList = sysRoleService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				
				str = "pretty:roles";
			}

		}
		return str;
	}

	public String update() throws Exception {
		String str= "";
		if (doValidateUpdate()) {
			if (sysRoleService.update(sysRole)) {
				sysRoleList = sysRoleService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				str = "pretty:roles";
			}
		}
		return str;
	}

	
	
	public void deleteSysRole() {
		try {

			sysRoleService.delete(sysRole);
			this.sysRole = null;
			sysRoleList = sysRoleService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysRoles");
			PrimeFaces.current().executeScript("PF('dtSysRoles').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysRoles");
			e.printStackTrace();
		}
	}
	public void deleteSelectedSysRoles() {
		try {   
			sysRoleService.delete(selectedSysRoleList);
			this.selectedSysRoleList = null;
			sysRoleList = sysRoleService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysRoles");
			PrimeFaces.current().executeScript("PF('dtSysRoles').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysRoles");
			e.printStackTrace();
		}

	}
	
	public void deletePermission(SysRolePermission sysRolePermission) throws Exception {
		if (sysRolePermissionService.delete(sysRolePermission)) {
			rolePermissionList = sysRolePermissionService.getBySysRoleWithDetails( sysRolePermission.getSysRole().getId());

			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

		} else {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
		}

	}

	public void addPermission() throws Exception {
		if (permissionsTarget.size() > 0) {

			List<SysRolePermission> rolePermissions = new ArrayList<SysRolePermission>();
			for (SysPermission sysPermission : permissionsTarget) {
				SysRolePermission sysRolePermission = new SysRolePermission();
				sysRolePermission.setSysRole(sysRole);
				sysRolePermission.setSysPermission(sysPermission);
				rolePermissions.add(sysRolePermission);
			}
			if (sysRolePermissionService.saveAll(rolePermissions) != null) {
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

				rolePermissionList = sysRolePermissionService.getBySysRoleWithDetails(sysRole.getId());
			}

		}
	}

	public void deleteFromPermissionSelected(SysPermission permissions) {
		permissionsTarget.remove(permissions);
		permissionsSource.add(permissions);
	}


	public void assignSysRole(SysRole sysRole) throws Exception {
		sysRoleID = sysRole.getId();
		sysRoleStr = UtilityHelper.cipher(sysRoleID.toString());
		
		//this.sysRole = sysRole;
		//rolePermissionList = sysRolePermissionService.getBySysRoleWithDetails(sysRole.getId());

	}
	public void getPermission(SysRole sysRole) throws Exception {
		
		
		this.sysRole = sysRole;
		rolePermissionList = sysRolePermissionService.getBySysRoleWithDetails(sysRole.getId());

	}
	public void getPermissionSourceList() throws Exception {
		permissionsTarget = new ArrayList<SysPermission>();
		permissionsSource = sysPermissionService.getExceptInRole(sysRole.getId());
		

	}

	public void rest() throws Exception {
		sysRole = new SysRole();
		permissionsTarget = new ArrayList<SysPermission>();
		
	}

	public void onPermissionDrop(DragDropEvent<SysPermission> ddEvent) {
		SysPermission permissions =  ddEvent.getData();
		permissionsTarget.add(permissions);
		permissionsSource.remove(permissions);
	}
	public void addPermission(SysPermission sysPermission) {
		
		permissionsTarget.add(sysPermission);
		permissionsSource.remove(sysPermission);
	}
	public boolean hasSelectedSysRoles() {
		return this.selectedSysRoleList != null && !this.selectedSysRoleList.isEmpty();
	}
	public String getDeleteButtonMessage() {

		if (hasSelectedSysRoles()) {
			int size = this.selectedSysRoleList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("sysRolesSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneSysRoleSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}
	public SysRole getSysRole() {
		return sysRole;
	}

	public void setSysRole(SysRole sysRole) {
		this.sysRole = sysRole;
	}

	public List<SysRole> getSysRoleList() {
		return sysRoleList;
	}

	public void setSysRoleList(List<SysRole> sysRoleList) {
		this.sysRoleList = sysRoleList;
	}

	public List<SysRolePermission> getRolePermissionList() {
		return rolePermissionList;
	}

	public void setRolePermissionList(List<SysRolePermission> rolePermissionList) {
		this.rolePermissionList = rolePermissionList;
	}

	public List<SysPermission> getPermissionsSource() {
		return permissionsSource;
	}

	public void setPermissionsSource(List<SysPermission> permissionsSource) {
		this.permissionsSource = permissionsSource;
	}

	public List<SysPermission> getPermissionsTarget() {
		return permissionsTarget;
	}

	public void setPermissionsTarget(List<SysPermission> permissionsTarget) {
		this.permissionsTarget = permissionsTarget;
	}

	public List<SysRole> getSelectedSysRoleList() {
		return selectedSysRoleList;
	}

	public void setSelectedSysRoleList(List<SysRole> selectedSysRoleList) {
		this.selectedSysRoleList = selectedSysRoleList;
	}

	

	public Short getSysRoleID() {
		return sysRoleID;
	}

	public void setSysRoleID(Short sysRoleID) {
		this.sysRoleID = sysRoleID;
	}

	public String getSysRoleStr() {
		return sysRoleStr;
	}

	public void setSysRoleStr(String sysRoleStr) {
		this.sysRoleStr = sysRoleStr;
	}
	
	
	
}
