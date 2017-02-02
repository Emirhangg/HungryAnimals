package oortcloud.hungryanimals.configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;
import oortcloud.hungryanimals.HungryAnimals;
import oortcloud.hungryanimals.core.lib.References;
import oortcloud.hungryanimals.entities.properties.handler.HungryAnimalManager;

public class ConfigurationHandlerFoodPreferenceBlock {

	public static void init(File directory) {
		if (!directory.exists()) {
			try {
				Files.createDirectories(directory.toPath());
			} catch (IOException e) {
				HungryAnimals.logger.warn("Couldn\'t create food preference folder {}", new Object[] { directory, e });
				return;
			}
		}

		for (Class<? extends EntityAnimal> i : HungryAnimalManager.getInstance().getRegisteredAnimal()) {
			File iFile = new File(directory, EntityList.CLASS_TO_NAME.get(i) + ".json");

			if (!iFile.exists()) {
				createDefaultConfigurationFile(iFile);
			}
		}
	}

	public static void createDefaultConfigurationFile(File file) {
		URL url = ConfigurationHandlerFoodPreferenceBlock.class
				.getResource("/assets/" + References.MODID + "/food_preferences/block/" + file.getName());

		if (url == null) {
			HungryAnimals.logger.warn("Couldn\'t load food preference {} from {}", new Object[] { file, url });
			return;
		}
		
		String s;

		try {
			s = Resources.toString(url, Charsets.UTF_8);
			file.createNewFile();
			FileWriter o = new FileWriter(file);
			o.write(s);
			o.close();
		} catch (IOException ioexception) {
			HungryAnimals.logger.warn("Couldn\'t load food preference {} from {}", new Object[] { file, url, ioexception });
		}
	}

	public static void sync() {

	}

}
