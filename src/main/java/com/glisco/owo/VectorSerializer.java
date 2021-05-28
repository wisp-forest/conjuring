package com.glisco.owo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.math.vector.Vector3d;

public class VectorSerializer {

    public static JsonObject toJson(Vector3d Vector3d, JsonObject object, String key) {

        JsonArray vectorArray = new JsonArray();
        vectorArray.add(Vector3d.x);
        vectorArray.add(Vector3d.y);
        vectorArray.add(Vector3d.z);

        object.add(key, vectorArray);

        return object;
    }

    public static Vector3d fromJson(JsonObject object, String key) {

        JsonArray vectorArray = object.get(key).getAsJsonArray();
        double x = vectorArray.get(0).getAsDouble();
        double y = vectorArray.get(1).getAsDouble();
        double z = vectorArray.get(2).getAsDouble();

        return new Vector3d(x, y, z);
    }

}
