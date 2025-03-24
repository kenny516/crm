package site.easy.to.build.crm.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@AllArgsConstructor
public class CsvUtil {
    private final CsvMapper csvMapper = new CsvMapper();

    public <T> List<T> read(Class<T> clazz, InputStream inputStream) throws IOException {
        CsvSchema csvSchema = csvMapper.schemaFor(clazz).withHeader();
        MappingIterator<T> mappingIterator = csvMapper.readerFor(clazz).with(csvSchema).readValues(inputStream);
        return mappingIterator.readAll();
    }

    public <T> void read(Class<T> clazz,List<T> objets, File file) throws IOException {
        CsvSchema csvSchema = csvMapper.schemaFor(clazz).withHeader();
        csvMapper.writer(csvSchema).writeValue(file, objets);
    }
}
