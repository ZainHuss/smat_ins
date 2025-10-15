package com.smat.ins.view.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.GroupMember;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.entity.WorkingGroup;
import com.smat.ins.model.service.GroupMemberService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.model.service.WorkingGroupService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@ViewScoped
public class WorkingGroupBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date dateNow;

	// entity object
	private WorkingGroup workingGroup;
	private SysUser sysUserLogin;
	
	
	private Short workingGroupID;
	private String workingGroupStr;

	// list
	private List<WorkingGroup> workingGroupList;

	private List<UserAlias> userAliasSource;
	private List<UserAlias> userAliasTarget;
	private List<GroupMember> groupMemberList;

	// Service
	private WorkingGroupService workingGroupService;
	private UserAliasService userAliasService;
	private GroupMemberService groupMemberService;
	private LocalizationService localizationService;

	

	public WorkingGroupBean() throws Exception {
		super();
		DateFormat patternDate = new SimpleDateFormat("yyyy/MM/dd");
		dateNow = new Date();
		patternDate.format(dateNow);
		
		workingGroup = new WorkingGroup();

		workingGroupList = new ArrayList<WorkingGroup>();
		userAliasSource = new ArrayList<UserAlias>();
		userAliasTarget = new ArrayList<UserAlias>();
		groupMemberList = new ArrayList<GroupMember>();

		workingGroupService = (WorkingGroupService) BeanUtility.getBean("workingGroupService");
		userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
		groupMemberService = (GroupMemberService) BeanUtility.getBean("groupMemberService");

		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");

		sysUserLogin = (SysUser) UtilityHelper.getSessionAttr("user");
		workingGroupList = workingGroupService.findAll();
	}
	@PostConstruct
	public void init() throws Exception {
		workingGroupStr = UtilityHelper.getRequestParameter("si");
		if (workingGroupStr != null) {
			try {
				workingGroupID = Short.valueOf(UtilityHelper.decipher(workingGroupStr));
				workingGroup=workingGroupService.findById(workingGroupID);
				if (workingGroup != null) {
					groupMemberList = groupMemberService.getByWorkingGroup(workingGroup);
					for(GroupMember groupMember :groupMemberList) {
						userAliasTarget.add(groupMember.getUserAlias());
						
					}
					userAliasSource = userAliasService.getAllWithDetails();
					userAliasSource.removeAll(userAliasTarget);
					
				}
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
				e.printStackTrace();
			}
			
			userAliasSource = userAliasService.getAllWithDetails();
			userAliasTarget = new ArrayList<UserAlias>();
		}
		
	}
	public boolean doValidate() throws Exception {
		boolean result = true;
		WorkingGroup workingGroup2 = workingGroupService.findByUniqueField("arabicName", workingGroup.getArabicName());

		if (workingGroup2 != null) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youMustEnterNameUnique"));

			result = false;
		}
		if (workingGroup.getArabicName() == null || workingGroup.getArabicName().equals("")) {
			UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (workingGroup.getEnglishName() == null || workingGroup.getEnglishName().equals("")) {
			UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("englishName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (userAliasTarget.size() <= 0) {
			UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("usersSelectedGroup") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));

			result = false;
		}

		return result;
	}

	public boolean doValidateUpdate() throws Exception {
		boolean result = true;

		if (workingGroup.getArabicName() == null || workingGroup.getArabicName().equals("")) {
			UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (workingGroup.getEnglishName() == null || workingGroup.getEnglishName().equals("")) {
			UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("englishName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}

	public String save() throws Exception {
		String str = "";
		if (doValidate()) {
			workingGroup.setSysUser(sysUserLogin);
			workingGroup.setCreatedDate(dateNow);

			List<GroupMember> groupMembers = new ArrayList<GroupMember>();
			for (UserAlias userAlias : userAliasTarget) {
				GroupMember groupMember = new GroupMember();
				groupMember.setWorkingGroup(workingGroup);
				groupMember.setUserAlias(userAlias);
				groupMember.setCreatedDate(dateNow);
				groupMember.setSysUser(sysUserLogin);
				groupMember.setIsDisabled(false);
				groupMembers.add(groupMember);

			}

			workingGroup.setGroupMembers(new HashSet<GroupMember>(groupMembers));

			if (workingGroupService.save(workingGroup) != null) {
				workingGroupList = workingGroupService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

				str = "pretty:workingGroupView";
			}
		}
		return str;

	}

	public String update() throws Exception {
		String str = "";
		if (doValidateUpdate()) {
			if (workingGroupService.update(workingGroup)) {
				workingGroupList = workingGroupService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

				str = "pretty:workingGroupView";
			}
		}

		return str;
	}

	public void assignWorkingGroup(WorkingGroup workingGroup) throws Exception {
		
		workingGroupID = workingGroup.getId();
		workingGroupStr = UtilityHelper.cipher(workingGroupID.toString());

		
		//this.workingGroup = workingGroup;
		//groupMemberList = groupMemberService.getByWorkingGroup(workingGroup);

	}

	public void AddUserAlias(UserAlias userAlias) {
		userAliasTarget.add(userAlias);
		userAliasSource.remove(userAlias);
	}

	public void removeUserAlias(UserAlias userAlias) {
		userAliasSource.add(userAlias);
		userAliasTarget.remove(userAlias);
	}

	public void insertUserAlias(UserAlias userAliasObj) throws Exception {
		Boolean val = false;
		for (GroupMember groupMemberObj : groupMemberList) {
			if (groupMemberObj.getUserAlias().getId() == userAliasObj.getId()) {
				val = true;

			}

		}

		if (val == false) {
			GroupMember groupMember = new GroupMember();
			groupMember.setWorkingGroup(workingGroup);
			groupMember.setUserAlias(userAliasObj);
			groupMember.setCreatedDate(dateNow);
			groupMember.setSysUser(sysUserLogin);
			groupMember.setIsDisabled(false);

			if (groupMemberService.save(groupMember) != null) {
				groupMemberList = groupMemberService.getByWorkingGroup(workingGroup);
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:groupMember_datatable");
				PrimeFaces.current().executeScript("PF('widgetVarUsersAliasDialog').hide()");
			}

		} else if (val == true) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youMustEnterNameUnique"));
			PrimeFaces.current().ajax().update("form:messages", "form:groupMember_datatable");
			PrimeFaces.current().executeScript("PF('widgetVarUsersAliasDialog').hide()");
		}

	}

	public void getUserAlias() throws Exception {
		userAliasSource = userAliasService.getAllWithDetails();
	}

	public void deleteGroupMember(GroupMember groupMember) throws Exception {
		if (groupMemberService.delete(groupMember)) {
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			groupMemberList = groupMemberService.getByWorkingGroup(workingGroup);

		}
	}

	public void reset() throws Exception {
		workingGroup = new WorkingGroup();
		//userAliasSource = userAliasService.getAllWithDetails();
		//userAliasTarget = new ArrayList<UserAlias>();
	}

	public void deleteWorkingGroup() {
		try {

			workingGroupService.delete(workingGroup);
			this.workingGroup = new WorkingGroup();
			workingGroupList = workingGroupService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-workingGroup");
			PrimeFaces.current().executeScript("PF('dtworkingGroup').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-dt-workingGroup");
			e.printStackTrace();
		}

	}

	public WorkingGroup getWorkingGroup() {
		return workingGroup;
	}

	public void setWorkingGroup(WorkingGroup workingGroup) {
		this.workingGroup = workingGroup;
	}

	public List<WorkingGroup> getWorkingGroupList() {
		return workingGroupList;
	}

	public void setWorkingGroupList(List<WorkingGroup> workingGroupList) {
		this.workingGroupList = workingGroupList;
	}

	public List<UserAlias> getUserAliasSource() {
		return userAliasSource;
	}

	public void setUserAliasSource(List<UserAlias> userAliasSource) {
		this.userAliasSource = userAliasSource;
	}

	public List<UserAlias> getUserAliasTarget() {
		return userAliasTarget;
	}

	public void setUserAliasTarget(List<UserAlias> userAliasTarget) {
		this.userAliasTarget = userAliasTarget;
	}

	public Date getDateNow() {
		return dateNow;
	}

	public void setDateNow(Date dateNow) {
		this.dateNow = dateNow;
	}

	public SysUser getSysUserLogin() {
		return sysUserLogin;
	}

	public void setSysUserLogin(SysUser sysUserLogin) {
		this.sysUserLogin = sysUserLogin;
	}

	public List<GroupMember> getGroupMemberList() {
		return groupMemberList;
	}

	public void setGroupMemberList(List<GroupMember> groupMemberList) {
		this.groupMemberList = groupMemberList;
	}
	public Short getWorkingGroupID() {
		return workingGroupID;
	}
	public void setWorkingGroupID(Short workingGroupID) {
		this.workingGroupID = workingGroupID;
	}
	public String getWorkingGroupStr() {
		return workingGroupStr;
	}
	public void setWorkingGroupStr(String workingGroupStr) {
		this.workingGroupStr = workingGroupStr;
	}

}
