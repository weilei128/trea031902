import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

public class TestCsv {
    public static void main(String[] args) {
        try {
            // 测试CSV写入
            File dir = new File("data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            File file = new File("data/test.csv");
            try (Writer writer = new FileWriter(file)) {
                String[] header = {"id", "name", "email"};
                CSVWriter csvWriter = new CSVWriter(writer, 
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
                csvWriter.writeNext(header);
                csvWriter.writeNext(new String[]{"1", "test", "test@example.com"});
                System.out.println("CSV文件创建成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}