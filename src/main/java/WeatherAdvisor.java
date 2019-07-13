import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class WeatherAdvisor {
    boolean rainExpected(Double lat, Double lon,
                         LocalDateTime startsAt, LocalDateTime endsAt) {
        URL url = null;
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&APPID=dd996276fad4a09076f9697010116892");
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url + "", String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode list = root.path("list");

            JsonNode start = list.path(0);
            JsonNode end = list.path(list.size() - 1);
            JsonNode startDt = start.path("dt_txt");
            JsonNode endDt = end.path("dt_txt");

            String startStr = startDt.textValue();
            String endStr = endDt.textValue();

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(startStr, dateTimeFormatter);
            LocalDateTime endDate = LocalDateTime.parse(endStr, dateTimeFormatter);

            if (startDate.isAfter(startsAt)) {
                startsAt = startDate;
            }

            if (endDate.isBefore(endsAt)) {
                endsAt = endDate;
            }

            if (startsAt.isBefore(startDate) && endsAt.isBefore(startDate)) {
                return false;
            }
            if (startsAt.isAfter(endDate) && endsAt.isAfter(endDate)) {
                return false;
            }

            Duration duration = Duration.between(startDate, startsAt);
            long startHours = duration.toHours();
            int startId = (int) startHours / 3;
            duration = Duration.between(endsAt, endDate);
            int endId = 39 - (int) duration.toHours() / 3;

            JsonNode startNode = list.path(startId);
            JsonNode startNodeDate = startNode.path("dt_txt");
            LocalDateTime startIdDate =
                    LocalDateTime.parse(startNodeDate.textValue(), dateTimeFormatter);

            if (endsAt.isEqual(startsAt) && (startIdDate.getHour() % 3 == 0)
                    && (startIdDate.getMinute() == 0) && (startIdDate.getSecond() == 0)) {
                JsonNode subList = list.path(startId);
                JsonNode weather = subList.path("weather");
                JsonNode subWeather = weather.path(0);
                JsonNode main = subWeather.path("main");
                String mainStr = main.asText();
                String value = "Rain";
                if (mainStr.equals(value)) {
                    return true;
                }
                subList = list.path(startId - 1);
                weather = subList.path("weather");
                subWeather = weather.path(0);
                main = subWeather.path("main");
                mainStr = main.asText();
                if (mainStr.equals(value)) {
                    return true;
                }
            }
            if (endsAt.isEqual(startsAt)) {
                JsonNode subList = list.path(startId);
                JsonNode weather = subList.path("weather");
                JsonNode subWeather = weather.path(0);
                JsonNode main = subWeather.path("main");
                String mainStr = main.asText();
                String value = "Rain";
                if (mainStr.equals(value)) {
                    return true;
                }
            }

            for (int i = startId; i < endId; i++) {
                JsonNode subList = list.path(i);
                JsonNode weather = subList.path("weather");
                JsonNode subWeather = weather.path(0);
                JsonNode main = subWeather.path("main");
                String mainStr = main.asText();
                String value = "Rain";

                if (mainStr.equals(value)) {
                    return true;
                }
            }

            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

}
