package site.easy.to.build.crm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import site.easy.to.build.crm.DTO.ExportDTO;
import site.easy.to.build.crm.util.DatabaseCustomUtil;


@Controller
@RequestMapping("/export")
@AllArgsConstructor
public class ExportController {
    private final DatabaseCustomUtil databaseCustomUtil;

    @GetMapping("/{customerId}")
    public ResponseEntity<ExportDTO> generateExport(@PathVariable("customerId") Integer customerId) throws JsonProcessingException {
        ExportDTO exportDTO = databaseCustomUtil.exportDTO(customerId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename \"export.json\"")
                .body(exportDTO);
    }
}
