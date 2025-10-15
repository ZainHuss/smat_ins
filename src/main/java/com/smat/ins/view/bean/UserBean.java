package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;

import com.smat.ins.model.data.lazyloading.LazyUserDataModel;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.service.SysUserService;
import com.smat.ins.util.BeanUtility;

@Named
@ViewScoped
public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LazyDataModel<SysUser> lazyUserDataModel;
	private List<SysUser> selectedUsers;
	private SysUser selectedUser;

	/**
	 * @return the lazyUserDataModel
	 */
	public LazyDataModel<SysUser> getLazyUserDataModel() {
		return lazyUserDataModel;
	}

	/**
	 * @param lazyUserDataModel the lazyUserDataModel to set
	 */
	public void setLazyUserDataModel(LazyDataModel<SysUser> lazyUserDataModel) {
		this.lazyUserDataModel = lazyUserDataModel;
	}

	/**
	 * @return the selectedUser
	 */
	public SysUser getSelectedUser() {
		return selectedUser;
	}

	/**
	 * @param selectedUser the selectedUser to set
	 */
	public void setSelectedUser(SysUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	/**
	 * @return the selectedUsers
	 */
	public List<SysUser> getSelectedUsers() {
		return selectedUsers;
	}

	/**
	 * @param selectedUsers the selectedUsers to set
	 */
	public void setSelectedUsers(List<SysUser> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	private SysUserService sysUserService;

	public UserBean() {
		sysUserService = (SysUserService) BeanUtility.getBean("sysUserService");
		selectedUsers=new ArrayList<SysUser>();
		selectedUser=new SysUser();
	}

	@PostConstruct
	public void init() {
		try {
			lazyUserDataModel = new LazyUserDataModel(sysUserService.findAll());
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
