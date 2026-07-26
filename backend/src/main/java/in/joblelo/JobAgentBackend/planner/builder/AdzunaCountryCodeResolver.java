package in.joblelo.JobAgentBackend.planner.builder;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class AdzunaCountryCodeResolver {

    private static final String DEFAULT_COUNTRY = "in";

    private static final Map<String, String> LOCATION_TO_COUNTRY = new HashMap<>();

    static {

        // ---------------- India ----------------
        add("in", "india", "bharat", "new delhi", "delhi", "mumbai",
                "bangalore", "bengaluru", "hyderabad", "pune",
                "chennai", "kolkata", "gurgaon", "gurugram",
                "noida", "ghaziabad", "faridabad");

        // ---------------- United Kingdom ----------------
        add("gb", "uk", "united kingdom", "great britain",
                "england", "scotland", "wales",
                "london", "manchester", "birmingham",
                "liverpool", "leeds", "glasgow",
                "edinburgh", "bristol");

        // ---------------- United States ----------------
        add("us", "usa", "united states", "america",
                "new york", "los angeles", "san francisco",
                "seattle", "chicago", "boston",
                "austin", "miami", "dallas");

        // ---------------- Canada ----------------
        add("ca", "canada", "toronto",
                "vancouver", "montreal",
                "ottawa", "calgary");

        // ---------------- Australia ----------------
        add("au", "australia", "sydney",
                "melbourne", "brisbane",
                "perth", "adelaide");

        // ---------------- Germany ----------------
        add("de", "germany", "berlin",
                "munich", "hamburg",
                "frankfurt", "cologne");

        // ---------------- France ----------------
        add("fr", "france", "paris",
                "lyon", "marseille",
                "nice");

        // ---------------- Netherlands ----------------
        add("nl", "netherlands", "amsterdam",
                "rotterdam", "utrecht",
                "the hague");

        // ---------------- Belgium ----------------
        add("be", "belgium", "brussels",
                "antwerp", "ghent");

        // ---------------- Spain ----------------
        add("es", "spain", "madrid",
                "barcelona", "valencia",
                "seville");

        // ---------------- Italy ----------------
        add("it", "italy", "rome",
                "milan", "turin",
                "florence");

        // ---------------- Poland ----------------
        add("pl", "poland", "warsaw",
                "krakow", "wroclaw");

        // ---------------- Singapore ----------------
        add("sg", "singapore");

        // ---------------- New Zealand ----------------
        add("nz", "new zealand",
                "auckland", "wellington",
                "christchurch");

        // ---------------- South Africa ----------------
        add("za", "south africa",
                "cape town", "johannesburg",
                "durban");

        // ---------------- Mexico ----------------
        add("mx", "mexico",
                "mexico city",
                "guadalajara");

        // ---------------- Brazil ----------------
        add("br", "brazil",
                "sao paulo",
                "rio de janeiro");

    }

    private static void add(String code, String... names) {
        for (String name : names) {
            LOCATION_TO_COUNTRY.put(name.toLowerCase(Locale.ROOT), code);
        }
    }

    public String resolve(String location) {

        if (location == null || location.isBlank()) {
            return DEFAULT_COUNTRY;
        }

        String normalized = location.toLowerCase(Locale.ROOT).trim();

        // Exact match
        if (LOCATION_TO_COUNTRY.containsKey(normalized)) {
            return LOCATION_TO_COUNTRY.get(normalized);
        }

        // Contains match
        for (Map.Entry<String, String> entry : LOCATION_TO_COUNTRY.entrySet()) {

            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }

        }

        return DEFAULT_COUNTRY;
    }

}