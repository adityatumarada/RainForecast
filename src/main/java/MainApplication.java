import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ./gradlew bootrun -q -Pargs=12.9762,77.6033,"2019-05-23 08:15","2019-05-24 09:15"
// ./gradlew bootrun -q -Pargs=12.9762,77.6033,"2019-05-23 08:15","2019-05-29 09:15"


public class MainApplication {
    public static void main(String args[]) {
        WeatherAdvisor weatherAdvisor = new WeatherAdvisor();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Double latitude = new Double(args[0]);
        Double longitude = new Double(args[1]);

        LocalDateTime startsAt = LocalDateTime.parse(args[2], dateTimeFormatter);
        LocalDateTime endsAt= LocalDateTime.parse(args[3], dateTimeFormatter);

        if (!validpar(latitude, longitude, startsAt, endsAt))
            return;

        if (weatherAdvisor.rainExpected(latitude, longitude, startsAt, endsAt)) {
            System.out.println("Yes");
        } else {
            System.out.println("No");
        }
    }

    public static boolean validpar(Double latitude,Double longitude,LocalDateTime startsAt,LocalDateTime endsAt){
        if (latitude<0||latitude>90||longitude<0||latitude>180) {
            System.out.println("Invalid latitude or langitude");
            return false;
        }

        return true;
    }
}
