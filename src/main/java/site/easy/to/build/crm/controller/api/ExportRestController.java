package site.easy.to.build.crm.controller.api;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.DTO.ExportDTO;
import site.easy.to.build.crm.util.DatabaseCustomUtil;


@RestController
@RequestMapping("/api/import")
@AllArgsConstructor
public class ExportRestController {
    private DatabaseCustomUtil databaseCustomUtil;
    private final Gson gson = new Gson();

    @PostMapping("")
    public ResponseEntity<Boolean> export(@RequestBody ExportDTO exportDTO) {
        databaseCustomUtil.saveExportDto(exportDTO);
        return ResponseEntity.ok(true);
    }
}
