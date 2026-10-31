package com.wynprice.secretrooms.server.utils;

import java.util.Optional;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class ModelDataUtils {
   public static <T> Optional<T> getData(ModelData data, ModelProperty<T> property) {
      return !data.has(property) ? Optional.empty() : Optional.ofNullable(data.get(property));
   }
}
