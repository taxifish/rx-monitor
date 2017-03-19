package org.resha.rx.hz;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

public class HzServer {

    private static final String MAP_NAME_PARAM = "map";
    private static final String DEFAULT_MAP_NAME = "monitor";

    private HazelcastInstance hz;

    private HzServer() {
        Config cfg = new Config();
        hz = Hazelcast.newHazelcastInstance(cfg);
    }

    private void initMap(String mapName) {
        Map<String, String> map = hz.getMap(mapName);

        map.put("sensor_1", "27.20");
        map.put("sensor_2", "27.20");
        map.put("sensor_3", "27.20");
        map.put("sensor_4", "27.20");
        map.put("sensor_5", "27.20");
        map.put("sensor_6", "27.20");
        map.put("sensor_7", "27.20");
        map.put("sensor_8", "27.20");

    }

    public static void main(String[] args) {
        String mapName = System.getProperty(MAP_NAME_PARAM, DEFAULT_MAP_NAME);

        HzServer hzServer = new HzServer();
        hzServer.initMap(mapName);
    }
}
