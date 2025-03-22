package site.easy.to.build.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "parameter_crm")
public class Parameter {

    @Id
    @Column(name = "parameter_key")
    String parameterKey;

    @Column(name = "parameter_value")
    Double parameterValue;

}
