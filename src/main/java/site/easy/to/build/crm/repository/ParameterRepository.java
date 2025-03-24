package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.Parameter;

public interface ParameterRepository extends JpaRepository<Parameter, String> {
}
