package site.easy.to.build.crm.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.easy.to.build.crm.customValidations.customer.UniqueEmail;

@JsonPropertyOrder({"customer_email", "Budget"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BudgetDtoCsv {

    @JsonProperty(value = "customer_email")
    @Email(message = "Email is not valid.")
    @NotBlank(message = "Email can't be blank.")
    @UniqueEmail
    public String customerEmail;

    @JsonProperty(value = "Budget")
    @Positive(message = "Budget must be a positive number.")
    public Double budget;

}
