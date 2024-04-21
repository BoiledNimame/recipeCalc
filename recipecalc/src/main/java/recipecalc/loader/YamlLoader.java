package recipecalc.loader;
    
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlLoader {
    public static Map<String, Object> loadAsMap() {
        final Yaml yaml = new Yaml();
        try {
            InputStream recipe = new FileInputStream(Paths.get("./recipe.yaml").toFile());

            @SuppressWarnings("unchecked")
            Map<String, Object> configMap = (Map<String, Object>) yaml.load(recipe);

            recipe.close();
            return configMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}