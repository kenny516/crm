package site.easy.to.build.crm.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import site.easy.to.build.crm.DTO.CustomerDtoCsv;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.user.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Component
public class CsvUtil {

    private final CsvMapper csvMapper = new CsvMapper();
    private final Validator validator;

    public <T> List<T> read(Class<T> clazz, InputStream inputStream, StringBuilder errorMessage) throws IOException {
        csvMapper.enable(CsvParser.Feature.TRIM_SPACES);
        csvMapper.enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema csvSchema = csvMapper.schemaFor(clazz).withHeader().withColumnSeparator(',');
        MappingIterator<T> mappingIterator = csvMapper.readerFor(clazz).with(csvSchema).readValues(inputStream);

        List<T> result = mappingIterator.readAll();
        for (int i = 0; i < result.size(); i++) {
            Set<ConstraintViolation<T>> violations = validator.validate(result.get(i));
            if (!violations.isEmpty()) {
                errorMessage.append(buildErrorMessage(i + 2, violations));
            }
        }
        return result;
    }

    private <T> String buildErrorMessage(int rowNumber, Set<ConstraintViolation<T>> violations) {
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("<div class=\"alert alert-danger\" role=\"alert\">");
        errorBuilder.append(String.format("<strong>Validation errors in row %d:</strong><ul>", rowNumber));
        for (ConstraintViolation<T> violation : violations) {
            errorBuilder.append(String.format("<li class=\"list-unstyled\"><strong>Field:</strong> %s, <strong>Error:</strong> %s</li>", violation.getPropertyPath(), violation.getMessage()));
        }
        errorBuilder.append("</ul></div>");
        return errorBuilder.toString();
    }



}