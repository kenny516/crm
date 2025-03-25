package site.easy.to.build.crm.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.easy.to.build.crm.customValidations.customer.UniqueEmail;

@JsonPropertyOrder({"customer_email", "subject_or_name","type","status","expense"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TicketLeadDtoCsv  {

    @JsonProperty(value = "customer_email")
    @Email(message = "Email is not valid.")
    @NotBlank(message = "Email can't be blank.")
    @UniqueEmail
    public String customerEmail;

    @JsonProperty(value = "subject_or_name")
    public String subjectOrName;

    @JsonProperty(value = "type")
    @NotBlank(message = "type is required")
    @Pattern(regexp = "^(ticket|lead|tickets|leads)$",message = "Type must be either 'ticket' or 'lead'")
    public String type;

    @JsonProperty(value = "status")
    @NotBlank(message = "Status is required")
    public String status;

    @JsonProperty(value = "expense")
    @Positive(message = "Expense must be a positive number")
    public Double expense;


    @AssertTrue(message = "Status is invalid for the provided type")
    public boolean isStatusValid() {
        if ("ticket".equalsIgnoreCase(type) || "tickets".equalsIgnoreCase(type)) {
             return status.matches("^(open|assigned|on-hold|in-progress|resolved|closed|reopened|pending-customer-response|escalated|archived)$");
        } else if ("lead".equalsIgnoreCase(type) || "leads".equalsIgnoreCase(type)) {
            return status.matches("^(meeting-to-schedule|scheduled|archived|success|assign-to-sales)$");
        }
        return false;
    }

}
