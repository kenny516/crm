package site.easy.to.build.crm.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.easy.to.build.crm.entity.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExportDTO {
    CustomerLoginInfo customerLoginInfo;
    //List<Budget> budgets;
    List<Ticket> tickets;
    List<Lead> leads;
}
