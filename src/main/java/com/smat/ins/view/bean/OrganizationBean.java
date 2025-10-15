package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.smat.ins.model.entity.HierarchySystem;
import com.smat.ins.model.entity.HierarchySystemDef;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.service.HierarchySystemDefService;
import com.smat.ins.model.service.HierarchySystemService;
import com.smat.ins.model.service.OrganizationService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@ViewScoped
public class OrganizationBean implements Serializable {

	// #region "properties"
	private static final long serialVersionUID = -3183351067652089037L;

	private Organization organization;
	private Organization selectedOrganization;
	private HierarchySystem hierarchySystem;

	private List<Organization> organizations;
	private List<Organization> organizationChildren;
	private List<HierarchySystem> hierarchySystems;
	private List<HierarchySystemDef> hierarchySystemDefs;

	private TreeNode root;
	private TreeNode node;
	private TreeNode selectedNode;

	private OrganizationService organizationService;
	private HierarchySystemService hierarchySystemService;
	private HierarchySystemDefService hierarchySystemDefService;
	private LocalizationService localizationService;

	private String template;
	private String eventType;
	private String searchKey;
	private String organizationName;
	private String organizationCode;

	private ResourceBundle applicationSetting;
	private Integer orgCodeLength;

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}

	public List<Organization> getOrganizationChildren() {
		return organizationChildren;
	}

	public void setOrganizationChildren(List<Organization> organizationChildren) {
		this.organizationChildren = organizationChildren;
	}

	public Organization getSelectedOrganization() {
		return selectedOrganization;
	}

	public void setSelectedOrganization(Organization selectedOrganization) {
		this.selectedOrganization = selectedOrganization;
	}

	public HierarchySystem getHierarchySystem() {
		return hierarchySystem;
	}

	public void setHierarchySystem(HierarchySystem hierarchySystem) {
		this.hierarchySystem = hierarchySystem;
	}

	public List<HierarchySystem> getHierarchySystems() {
		return hierarchySystems;
	}

	public void setHierarchySystems(List<HierarchySystem> hierarchySystems) {
		this.hierarchySystems = hierarchySystems;
	}

	public List<HierarchySystemDef> getHierarchySystemDefs() {
		return hierarchySystemDefs;
	}

	public void setHierarchySystemDefs(List<HierarchySystemDef> hierarchySystemDefs) {
		this.hierarchySystemDefs = hierarchySystemDefs;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getNode() {
		return node;
	}

	public void setNode(TreeNode node) {
		this.node = node;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	// #endregion

	@PostConstruct
	public void init() {

	}

	public OrganizationBean() throws Exception {
		organization = new Organization();
		organization.setHasPrivateHierarchy(true);
		hierarchySystem = new HierarchySystem();

		hierarchySystems = new ArrayList<HierarchySystem>();
		hierarchySystemDefs = new ArrayList<HierarchySystemDef>();
		organizations = new ArrayList<Organization>();
		organizationChildren = new ArrayList<Organization>();

		organizationService = (OrganizationService) BeanUtility.getBean("organizationService");
		hierarchySystemService = (HierarchySystemService) BeanUtility.getBean("hierarchySystemService");
		hierarchySystemDefService = (HierarchySystemDefService) BeanUtility.getBean("hierarchySystemDefService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
		applicationSetting = ResourceBundle.getBundle("com.smat.ins.view.resources.applicationSetting");
		if (applicationSetting.containsKey("maxOrgCodeLength")
				&& applicationSetting.getString("maxOrgCodeLength") != null) {
			orgCodeLength = Integer.parseInt(applicationSetting.getString("maxOrgCodeLength"));

		} else {
			orgCodeLength = 3;
		}

		hierarchySystems = hierarchySystemService.findAll();
		if (hierarchySystems != null && hierarchySystems.size() > 0) {
			hierarchySystem = hierarchySystems.get(0);
			if (hierarchySystem != null) {
				hierarchySystemDefs = hierarchySystemDefService.getByHierarchySystem(hierarchySystem);
			}
		}
		organizations = organizationService.getByParent(null, null, null);
		if (organizations != null && organizations.size() > 0) {
			selectedOrganization = organizations.get(0);
			template = "/views/organizationTree/organizationDetails.xhtml";

		} else {
			selectedOrganization = null;
			Integer maxOrgCodeLevelLength = organizationService.getMaxLevelOrgCode(selectedOrganization);
			if (maxOrgCodeLevelLength != null) {
				organization.setCode(String.format("%0" + orgCodeLength + "d", maxOrgCodeLevelLength + 1));
			}
			template = "/views/organizationTree/addOrganization.xhtml";
		}
		intializeRootAndFirstLevel();
		eventType = "select";
		searchKey = "";

	}

	public void selectOneMenuHierarchyDef() throws Exception {
		if (hierarchySystem != null) {
			hierarchySystemDefs = hierarchySystemDefService.getByHierarchySystem(hierarchySystem);
		}
	}

	private void intializeRootAndFirstLevel() throws Exception {
		this.root = new DefaultTreeNode("Root", null, null);
		for (Organization rootOrganization : organizations) {
			this.node = new DefaultTreeNode(rootOrganization, this.root);
			List<Organization> firstLevel = organizationService.getByParent(rootOrganization, null, null);
			for (Organization organization : firstLevel) {

				@SuppressWarnings("unused")
				TreeNode node = new DefaultTreeNode(organization, this.node);
			}
		}
	}

	public void onNodeExpand(NodeExpandEvent event) throws Exception {
		event.getTreeNode().setSelected(false);
		List<TreeNode> listChild = event.getTreeNode().getChildren();
		for (TreeNode treeNode : listChild) {
			if (treeNode.getChildCount() > 0)
				return;
			List<Organization> listContactChild = organizationService.getByParent(((Organization) treeNode.getData()),
					null, null);
			for (Organization organization : listContactChild) {

				@SuppressWarnings("unused")
				TreeNode node = new DefaultTreeNode(organization, treeNode);
			}
		}
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		event.getTreeNode().setSelected(false);
		collapseTree(event.getTreeNode());
	}

	public void collapseTree(TreeNode node) {

		if (node.getChildCount() > 0) {
			node.setExpanded(false);
			node.setSelected(false);
			/*
			 * for (TreeNode child : node.getChildren()) { collapseTree(child); }
			 */
		}

	}

	public void onNodeSelect(NodeSelectEvent event) throws Exception {
		if(selectedNode != null) {
		selectedOrganization = organizationService.getOrganizationWithDetails((Organization) selectedNode.getData());
		organizationChildren = organizationService.getByParent(selectedOrganization, null, null);
		if (eventType.equals("add")) {
			organization = new Organization();
			organization.setHasPrivateHierarchy(false);
			hierarchySystem = selectedOrganization.getHierarchySystem();
			if (hierarchySystem != null) {
				hierarchySystemDefs = hierarchySystemDefService.getByHierarchySystem(hierarchySystem);
			}
			Integer maxOrgCodeLevelLength = organizationService.getMaxLevelOrgCode(selectedOrganization);
			organization.setPrefixCode(selectedOrganization.getPrefixCode());
			organization.setSuffixCode(selectedOrganization.getSuffixCode());
			if (maxOrgCodeLevelLength != null) {
				organization.setCode(selectedOrganization.getCode()
						+ String.format("%0" + orgCodeLength + "d", maxOrgCodeLevelLength + 1));
			}
			template = "/views/organizationTree/addOrganizationDetails.xhtml";
		} else if (eventType.equals("edit")) {
			template = "/views/organizationTree/editOrganizationDetails.xhtml";
		} else
			template = "/views/organizationTree/organizationDetails.xhtml";
		eventType = "select";
		}
	}

	public void startAction(String eventType) {
		this.eventType = eventType;
	}

	@SuppressWarnings("unused")
	public void save() throws Exception {
		if (validate()) {

			if (selectedOrganization != null) {
				organization.setOrganization(selectedOrganization);
			}
			if (organization.getHierarchySystem() == null)
				organization.setHierarchySystem(selectedOrganization.getHierarchySystem());
			Organization objOrganization = organizationService.save(organization);
			if (objOrganization != null) {
				if (selectedOrganization == null) {
					if (this.root != null) {
						TreeNode node = new DefaultTreeNode(objOrganization, this.root);
						this.root.setExpanded(true);
						node.setExpanded(true);
					} else {
						this.root = new DefaultTreeNode("Root", null, null);
						TreeNode node = new DefaultTreeNode(objOrganization, this.root);
						this.root.setExpanded(true);
						node.setExpanded(true);
					}
				} else {
					TreeNode node = new DefaultTreeNode(objOrganization, selectedNode);
					selectedNode.setExpanded(true);
					node.setExpanded(true);
				}
				if (objOrganization.getOrganization() == null)
					selectedOrganization = null;
				else
					selectedOrganization = objOrganization.getOrganization();
				reset();

				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			}
		}

	}

	public void reset() throws Exception {
		organization = new Organization();

		Integer maxOrgCodeLevelLength = 0;
		if (selectedOrganization != null) {
			organization.setPrefixCode(selectedOrganization.getPrefixCode());
			organization.setSuffixCode(selectedOrganization.getSuffixCode());

		} else {
			organization.setPrefixCode(null);
			organization.setSuffixCode(null);
			organization.setHasPrivateHierarchy(true);

		}
		
		maxOrgCodeLevelLength = organizationService.getMaxLevelOrgCode(selectedOrganization);
		if (maxOrgCodeLevelLength != null) {
			if (selectedOrganization != null) {
				hierarchySystemDefs = hierarchySystemDefService
						.getByHierarchySystem(selectedOrganization.getHierarchySystem());
				
				organization.setCode(String.format(selectedOrganization.getCode() + "%0" + orgCodeLength + "d",
						maxOrgCodeLevelLength + 1));
			} else {
				organization.setCode(String.format("%0" + orgCodeLength + "d", maxOrgCodeLevelLength + 1));
			}
		}
	}

	public void add() throws Exception {
		selectedOrganization = null;
		organization = new Organization();
		organization.setHasPrivateHierarchy(true);
		Integer maxOrgCodeLevelLength = organizationService.getMaxLevelOrgCode(selectedOrganization);
		if (maxOrgCodeLevelLength != null) {
			organization.setCode(String.format("%0" + orgCodeLength + "d", maxOrgCodeLevelLength + 1));
		}
		template = "/views/organizationTree/addOrganization.xhtml";
	}

	public void update() throws Exception {
		if (validateUpdate()) {
			if (organizationService.update(selectedOrganization)) {
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				((Organization) (selectedNode.getData())).setArabicName(selectedOrganization.getArabicName());
			}
		}
	}

	public void delete() throws Exception {
		if (validateDelete()) {
			if (organizationService.delete(selectedOrganization)) {
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				intializeRootAndFirstLevel();
				selectedOrganization = null;
			}

			else {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));

			}
		}
	}

	public void searchTree() throws Exception {

		if ((organizationName != null && !organizationName.isEmpty())
				|| (organizationCode != null && !organizationCode.isEmpty())) {
			this.root = new DefaultTreeNode("Root", null, null);
			List<Organization> rootsLevel = organizationService.getByNameOrCode(organizationName, organizationCode);
			for (Organization rootOrganization : rootsLevel) {
				this.node = new DefaultTreeNode(rootOrganization, this.root);
				List<Organization> firstLevel = organizationService.getByParent(rootOrganization, null, null);
				for (Organization organization : firstLevel) {

					@SuppressWarnings("unused")
					TreeNode node = new DefaultTreeNode(organization, this.node);
				}
			}
			return;
		} else {
			intializeRootAndFirstLevel();
		}

	}

	public boolean validate() {
		try {
			boolean result = true;
			
			if (organization.getArabicName() == null || organization.getArabicName().trim().equals("")) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
						+ localizationService.getErrorMessage().getString("validateInput"));
				result = false;
			}

			if (organization.getEnglishName() == null || organization.getEnglishName().trim().equals("")) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("englishName") + "  "
						+ localizationService.getErrorMessage().getString("validateInput"));
				result = false;
			}

			if (organization.getPrefixCode() == null || organization.getPrefixCode().trim().equals("")) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("prefixCode") + "  "
						+ localizationService.getErrorMessage().getString("validateInput"));
				result = false;
			}

			if (organization.getSuffixCode() == null || organization.getSuffixCode().trim().equals("")) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("suffixCode") + "  "
						+ localizationService.getErrorMessage().getString("validateInput"));
				result = false;
			}
			
			
			boolean uniqeResult = organizationService.checkIfFound(organization);
			if (uniqeResult) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("name") + "  "
						+ localizationService.getErrorMessage().getString("duplicate"));
				result = false;
			}
			
			return result;
		} catch (Exception e) {
			// TODO: handle exception
			return true;
		}
	}

	public boolean validateUpdate() {
		boolean result = true;
		if (selectedOrganization.getArabicName() == null || selectedOrganization.getArabicName().trim().equals("")) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		return result;
	}

	public boolean validateDelete() throws Exception {
		boolean result = true;
		Long totalChild = Long.valueOf(0);
		totalChild = organizationService.getByParentCount(selectedOrganization);
		if (totalChild > 0) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("thereAreChildren") + "  "
					+ localizationService.getErrorMessage().getString("operationFaild"));

			result = false;
		}
		return result;
	}

}
