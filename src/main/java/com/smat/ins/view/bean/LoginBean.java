package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.omnifaces.util.Faces;

import com.smat.ins.model.entity.SysPermission;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.SysUserRole;
import com.smat.ins.model.service.SysPermissionService;
import com.smat.ins.model.service.SysUserRoleService;
import com.smat.ins.model.service.SysUserService;
import com.smat.ins.util.BCrypt;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;
import org.primefaces.PrimeFaces;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = -6287411993146050792L;
    private SysUser user;
    private String userName;
    private String password;

    // Guard to prevent concurrent/re-entrant login requests from same session
    private transient AtomicBoolean loginProcessing = new AtomicBoolean(false);


    // خصائص جديدة لتغيير كلمة المرور
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    private List<SysUserRole> userRoles;
    private List<SysPermission> sysPermissionList;

    private SysUserService userService;
    private SysUserRoleService sysUserRoleService;
    private SysPermissionService sysPermissionService;
    private LocalizationService localizationService;

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // محولات جديدة لتغيير كلمة المرور
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public List<SysUserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<SysUserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<SysPermission> getSysPermissionList() {
        return sysPermissionList;
    }

    public void setSysPermissionList(List<SysPermission> sysPermissionList) {
        this.sysPermissionList = sysPermissionList;
    }

    public LoginBean() {
        userService = (SysUserService) BeanUtility.getBean("sysUserService");
        sysUserRoleService = (SysUserRoleService) BeanUtility.getBean("sysUserRoleService");
        sysPermissionService = (SysPermissionService) BeanUtility.getBean("sysPermissionService");
        localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
    }

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
    }

    public boolean doValidate() {
        boolean result = true;

        // normalize and null-safe checks
        if (userName == null || userName.trim().isEmpty()) {
            result = false;
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("requiredUserName"));
        }

        if (password == null || password.trim().isEmpty()) {
            result = false;
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("requiredPassword"));
        }

        return result;
    }

    public String login() {
        // prevent double submissions from same session/bean
        if (!loginProcessing.compareAndSet(false, true)) {
            return null;
        }

        // ensure inputs are normalized (trim) before validation/auth
        userName = (userName != null) ? userName.trim() : null;
        password = (password != null) ? password.trim() : null;

        try {
            if (!doValidate()) {
                return null;
            }

            try {
                user = userService.auth(userName, password);
            } catch (Exception e) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("userNameOrPassIncorrect"));
                return null;
            }

            if (user != null) {
                try {
                    userRoles = sysUserRoleService.getRoleBySysUser(user);
                    if (userRoles != null && !userRoles.isEmpty()) {
                        sysPermissionList = sysPermissionService.getBySysUser(user.getId());
                    }
                    UtilityHelper.putSessionAttr("user", user);
                    return "pretty:index";
                } catch (Exception e) {
                    UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("userNameOrPassIncorrect"));
                    return null;
                }
            }

            // fallback to admin credentials from properties (exact match required)
            if (localizationService.getApplicationProperties().containsKey("username")
                    && localizationService.getApplicationProperties().containsKey("password")) {
                String userNameAdmin = localizationService.getApplicationProperties().getString("username");
                String passwordAdmin = localizationService.getApplicationProperties().getString("password");
                if (userName != null && userName.equals(userNameAdmin) && BCrypt.checkpw(password, passwordAdmin)) {
                    user = new SysUser();
                    user.setUserName(userNameAdmin);
                    user.setPassword(passwordAdmin);
                    user.setFirstName(localizationService.getApplicationProperties().getString("firstName"));
                    user.setLastName(localizationService.getApplicationProperties().getString("lastName"));
                    user.setFatherName(localizationService.getApplicationProperties().getString("fatherName"));
                    user.setPassMustChangeFirstTime(Boolean.valueOf(
                            localizationService.getApplicationProperties().getString("passMustChangeFirstTime")));
                    user.setDisabled(Boolean.valueOf(localizationService.getApplicationProperties().getString("disabled")));
                    user.setEnDisplayName(localizationService.getApplicationProperties().getString("enDisplayName"));
                    user.setArDisplayName(localizationService.getApplicationProperties().getString("arDisplayName"));
                    user.setIsSuperAdmin(true);
                    UtilityHelper.putSessionAttr("user", user);
                    return "pretty:index";
                } else {
                    UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("userNameOrPassIncorrect"));
                    return null;
                }
            }

            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("userNameOrPassIncorrect"));
            return null;
        } finally {
            // always clear the processing flag so future attempts are possible
            loginProcessing.set(false);
        }
    }

    public boolean hasSysPermission(String code) {
        if (sysPermissionList != null) {
            for (SysPermission sysPermission : sysPermissionList) {
                if (sysPermission.getCode().trim().toLowerCase().equals(code.trim().toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String logout() {
        try {
            Faces.invalidateSession();
            FacesContext.getCurrentInstance().getExternalContext().redirect("auth.xhtml");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // طريقة للتحقق من صحة كلمة المرور الحالية
    public void validateCurrentPassword(FacesContext context, javax.faces.component.UIComponent component, Object value) {
        currentPassword = (String) value;

        if (user != null) {
            if (user.getIsSuperAdmin()) {
                // للمستخدمين المسؤولين
                String storedPassword = localizationService.getApplicationProperties().getString("password");
                if (!BCrypt.checkpw(currentPassword, storedPassword)) {
                    throw new javax.faces.validator.ValidatorException(
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    localizationService.getErrorMessage().getString("incorrectCurrentPassword"),
                                    null));
                }
            } else {
                // للمستخدمين العاديين
                if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
                    throw new javax.faces.validator.ValidatorException(
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    localizationService.getErrorMessage().getString("incorrectCurrentPassword"),
                                    null));
                }
            }
        }
    }




    public void changePassword() {
        try {
            if (user != null) {
                // التحقق من تطابق كلمتي المرور الجديدة
                if (!newPassword.equals(confirmPassword)) {
                    UtilityHelper.addErrorMessage("The passwords do not match.");
                    return;
                }

                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                if (user.getIsSuperAdmin() !=null && user.getIsSuperAdmin()) {
                    // للمستخدمين المسؤولين
                    UtilityHelper.addInfoMessage("Password changed successfully");
                } else {
                    // للمستخدمين العاديين
                    user.setPassword(hashedPassword);
                    userService.update(user);
                    UtilityHelper.addInfoMessage("Password changed successfully");
                }

                // إعادة تعيين الحقول
                resetPasswordForm();

                // إضافة باراميتر للإشارة إلى النجاح بدلاً من إعادة التوجيه المباشر
                PrimeFaces.current().executeScript("handlePasswordChangeSuccess();");

            }
        } catch (Exception e) {
            UtilityHelper.addErrorMessage("فشل تغيير كلمة المرور");
            e.printStackTrace();
        }
    }
    // دالة لإرجاع أسماء الأدوار كنص واحد
    public String getRolesLabel() {
        if (userRoles == null || userRoles.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userRoles.size(); i++) {
            sb.append(userRoles.get(i).getSysRole().getName());
            if (i < userRoles.size() - 1) {
                sb.append(" • "); // فاصلة بين الأدوار
            }
        }
        return sb.toString();
    }


    // طريقة لإعادة تعيين نموذج تغيير كلمة المرور
    public void resetPasswordForm() {
        currentPassword = null;
        newPassword = null;
        confirmPassword = null;
    }
}