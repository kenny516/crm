package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Parameter;
import site.easy.to.build.crm.repository.ParameterRepository;

@AllArgsConstructor
@Service
public class ParameterService {
    private ParameterRepository parameterRepository;

    public Parameter findByKey(String Key) {
        return parameterRepository.findById(Key).orElse(null);
    }

    public Parameter findThresholdAlert(){
        return findByKey("thresholdAlertBudget");
    }
}
