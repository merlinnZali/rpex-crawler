package ttm.eu;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Log4j2
public class Util {

    private Util() {}

    /**
     *
     * @param datalist data to save ...string[]
     * @param filename filename without extension string
     */
    public static void saveToCsv(List<String[]> datalist, String filename ){
        if(StringUtils.isNoneBlank(filename) && !datalist.isEmpty()){
            if(filename.contains(".")){
                filename = filename.substring(0, filename.indexOf("."));
            }
            try (CSVWriter writer = new CSVWriter(new FileWriter(filename + ".csv"))){
                writer.writeAll(datalist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param datalist data to save ...string[]
     * @param filename filename without extension string
     */
    public static void saveToJson(List<String[]> datalist, String filename ){
        if(StringUtils.isNoneBlank(filename) && !datalist.isEmpty()){
            if(filename.contains(".")){
                filename = filename.substring(0, filename.indexOf("."));
            }
            Gson gson = new Gson();
            String json = gson.toJson(datalist);
            try (FileWriter writer = new FileWriter(filename + ".csv")){
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
