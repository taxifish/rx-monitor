package org.resha.rx.hz;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class HzClient {

    private static final String MAP_NAME_PROPERTY = "map";
    private static final String DEFAULT_MAP_NAME = "monitor";

    private static final String TICK_PROPERTY = "tick";
    private static final String DEFAULT_TICK = "100";

    private static final String MUTATION_PROPERTY = "mutation";
    private static final String DEFAULT_MUTATION = "0.4";

    private HazelcastInstance hz;
    private double mutation;
    private double mutator = 0.0;
    private String mapName;

    public static void main(String[] args) {
        try {
            int tick = Integer.parseInt(System.getProperty(TICK_PROPERTY, DEFAULT_TICK));
            double mutation = Double.parseDouble(System.getProperty(MUTATION_PROPERTY, DEFAULT_MUTATION));
            String mapName = System.getProperty(MAP_NAME_PROPERTY, DEFAULT_MAP_NAME);

            HzClient hzClient = new HzClient(tick, mapName, mutation);


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private HzClient(int tick, String mapName, double mutation) {
        this.mutation = mutation;
        this.mapName = mapName;

        ClientConfig clientConfig = new ClientConfig();
        hz = HazelcastClient.newHazelcastClient(clientConfig);

        Observable.interval(tick, TimeUnit.MILLISECONDS).subscribe(s -> mutateMap());

    }

    private void mutateMap() {
        IMap<String, String> map = hz.getMap(mapName);

        for (String key: map.keySet()) {
            Double num =  Double.parseDouble(map.get(key));

            if (mutator == 0.0) {
                mutator = num * mutation;
            }

            num = num + mutator * (Math.random() - 0.5);
            if (num > 100) num = 99.0;
            if (num < 0) num = 1.0;

            map.put(key, String.format("%.2f", num));

        }

    }
}
