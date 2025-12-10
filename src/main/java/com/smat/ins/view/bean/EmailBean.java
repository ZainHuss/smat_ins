package com.smat.ins.view.bean;

import java.io.Serializable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.smat.ins.model.entity.Company;
import com.smat.ins.model.service.CompanyService;
import com.smat.ins.util.BeanUtility;
import org.primefaces.PrimeFaces;

@Named
@ViewScoped
public class EmailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Company> companiesWithEmail = new ArrayList<>();
    private String selectedRecipientEmail;
    private String description;
    private Company selectedCompany;
    private CompanyService companyService;

    // Keep subject only; remove character count and signature properties
    private String emailSubject = "Message from smat-ins";

    @PostConstruct
    public void init() {
        companyService = (CompanyService) BeanUtility.getBean("companyService");
        loadCompaniesWithEmail();
        // Read optional request parameters to prefill recipient and subject
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null) {
                Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
                if (params != null) {
                    String recipient = params.get("recipient");
                    String subject = params.get("subject");
                    if (recipient != null && !recipient.trim().isEmpty()) {
                        // Set selected recipient and update the selected company
                        this.selectedRecipientEmail = recipient;
                        onRecipientChange();
                    }
                    if (subject != null && !subject.trim().isEmpty()) {
                        try {
                            this.emailSubject = java.net.URLDecoder.decode(subject, "UTF-8");
                        } catch (Exception ignore) {
                            this.emailSubject = subject;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore param reading errors
        }
    }

    private void loadCompaniesWithEmail() {
        try {
            List<Company> all = companyService.findAll();
            companiesWithEmail.clear();
            for (Company c : all) {
                if (c != null && c.getEmail() != null && !c.getEmail().trim().isEmpty()) {
                    companiesWithEmail.add(c);
                }
            }
            // Sort companies alphabetically by name
            companiesWithEmail.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Failed to load company list: " + e.getMessage());
        }
    }

    public void clearForm() {
        selectedRecipientEmail = null;
        description = null;
        selectedCompany = null;
        emailSubject = "Message from smat-ins";

        // Reset PrimeFaces components
        PrimeFaces.current().resetInputs("form:recipient, form:description");

        // Add success message
        addSuccessMessage("Form cleared successfully. You can start a new email.");
    }

    public void validateAndPrepareEmail() {
        if (selectedRecipientEmail == null || selectedRecipientEmail.trim().isEmpty()) {
            addErrorMessage("Please select a recipient email address.");
            return;
        }

        // Verify configured sender is the required company address
        String configuredSender = getConfiguredSender();
        if (configuredSender == null || !"info@smat-ins.com".equalsIgnoreCase(configuredSender.trim())) {
            addErrorMessage("Sending is allowed only when configured sender is info@smat-ins.com. Current configured sender: " + (configuredSender == null ? "(not configured)" : configuredSender));
            return;
        }

        if (description == null || description.trim().isEmpty()) {
            addErrorMessage("Please write your message before sending.");
            return;
        }

        // Validate email format
        if (!isValidEmail(selectedRecipientEmail)) {
            addErrorMessage("The selected email address appears to be invalid.");
            return;
        }

        // Prepare the final message body
        String finalBody = buildFinalMessage();

        // Open Gmail compose window
        openGmailComposeWindow(finalBody);

        // Log the action (optional)
        logEmailAction();
    }

    private String buildFinalMessage() {
        if (description != null) {
            return description.trim();
        }
        return "";
    }

    private void openGmailComposeWindow(String body) {
        try {
            String url = "https://mail.google.com/mail/?view=cm&fs=1" +
                    "&to=" + encodeURIComponent(selectedRecipientEmail) +
                    "&su=" + encodeURIComponent(emailSubject) +
                    "&body=" + encodeURIComponent(body);

            // Execute JavaScript to open new window
            PrimeFaces.current().executeScript("window.open('" + url + "', '_blank');");

            // Add success message
            addSuccessMessage("Email window opened successfully. Your message is ready to send.");

        } catch (Exception e) {
            addErrorMessage("Failed to open email composer: " + e.getMessage());
        }
    }

    // Read configured sender from environment or application.properties
    private String getConfiguredSender() {
        try {
            // first check environment override
            String env = System.getenv("MAIL_DEFAULT_FROM");
            if (env != null && !env.trim().isEmpty()) return env.trim();

            Properties p = new Properties();
            try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
                if (in != null) {
                    p.load(in);
                    String v = p.getProperty("mail.default.from");
                    if (v != null && !v.trim().isEmpty()) return v.trim();
                }
            }
        } catch (Exception e) {
            // ignore and fall through
        }
        return null;
    }

    public void onRecipientChange() {
        if (selectedRecipientEmail != null && !selectedRecipientEmail.isEmpty()) {
            // Find the selected company object
            selectedCompany = companiesWithEmail.stream()
                    .filter(c -> selectedRecipientEmail.equals(c.getEmail()))
                    .findFirst()
                    .orElse(null);

            if (selectedCompany != null) {
                addInfoMessage("Selected: " + selectedCompany.getName());
            }
        } else {
            selectedCompany = null;
        }
    }

    public void loadEmailTemplate() {
        if (selectedCompany != null) {
            description = String.format("Dear %s,\n\nI hope this message finds you well.\n\n",
                    selectedCompany.getName());
            addInfoMessage("Basic email template loaded. Please customize your message.");
        } else {
            addErrorMessage("Please select a company first to load a template.");
        }
    }

    public void resetToDefaultSubject() {
        emailSubject = "Message from smat-ins";
        addInfoMessage("Subject reset to default.");
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        // Simple email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private String encodeURIComponent(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("%21", "!")
                    .replaceAll("%27", "'")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%7E", "~");
        } catch (Exception e) {
            return s;
        }
    }

    private void logEmailAction() {
        // You can implement logging functionality here
        System.out.println("Email prepared for: " + selectedRecipientEmail +
                ", Subject: " + emailSubject);
    }

    // Helper methods for messages
    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }

    private void addWarnMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", message));
    }

    // Getters and Setters
    public List<Company> getCompaniesWithEmail() {
        return companiesWithEmail;
    }

    public String getSelectedRecipientEmail() {
        return selectedRecipientEmail;
    }

    public void setSelectedRecipientEmail(String selectedRecipientEmail) {
        this.selectedRecipientEmail = selectedRecipientEmail;
        onRecipientChange(); // Auto-update when email is selected
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

}
