package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Parameter updateThresholdAlert(@RequestParam Double value) {
        Parameter parameter = findByKey("thresholdAlertBudget");
        parameter.setParameterValue(value);
        parameterRepository.save(parameter);
        return parameter;
    }
}
