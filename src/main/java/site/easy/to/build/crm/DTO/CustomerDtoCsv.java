package site.easy.to.build.crm.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.easy.to.build.crm.customValidations.customer.UniqueEmail;

@JsonPropertyOrder({"customer_email", "customer_name"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerDtoCsv {
    @JsonProperty(value = "customer_email")
    @Email(message = "Email is not valid.")
    @NotBlank(message = "Email can't be blank.")
    @UniqueEmail
    String email;

    @JsonProperty(value = "customer_name")
    @NotBlank(message = "Name can't be blank.")
    String name;

}
