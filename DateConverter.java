import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class MyData {
    int day;
    int month;
    int year;
    String dayOfWeek;

    public MyData(int day, int month, int year, String dayOfWeek) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.dayOfWeek = dayOfWeek;
    }

    public String toMyString() {
        return String.format("%02d/%02d/%04d (%s)", day, month, year, dayOfWeek);
    }
}

public class DateConverter {
    public static void main(String[] args) {
        try {
            convertDate("InputData.txt", "MyData.txt");
        } catch (IOException e) {
            System.out.println("File error");
            return;
        }
    }

    public static boolean compareDates(MyData firstData, MyData secondData){
        return (firstData.year == secondData.year) && (firstData.month == secondData.month) && (firstData.day == secondData.day);
    }

    public static MyData convertOne(String input){ //  dd / mm / yyyy / weekday or dd / m / yyyy / weekday
        String[] parts = input.split("/");
        int day = Integer.parseInt(parts[0].trim());
        int month = Integer.parseInt(parts[1].trim());
        int year = Integer.parseInt(parts[2].trim());
        String dayOfWeek = parts[3].trim();

        MyData myData = new MyData(day, month, year, dayOfWeek);
        return myData;
    }

    public static MyData convertTwo(String input){ //  yyyy-mm-dd weekday
        String[] parts = input.split("-| ");
        int year = Integer.parseInt(parts[0].trim());
        int month = Integer.parseInt(parts[1].trim());
        int day = Integer.parseInt(parts[2].trim());
        String dayOfWeek = parts[3].trim();

        MyData myData = new MyData(day, month, year, dayOfWeek);
        return myData;
    }  
    
    public static MyData convertThree(String input){ //  weekday dd.mm.yyyy
        String[] parts = input.split(" |\\.");
        int year = Integer.parseInt(parts[3].trim());
        int month = Integer.parseInt(parts[2].trim());
        int day = Integer.parseInt(parts[1].trim());
        String dayOfWeek = parts[0].trim();
        MyData myData = new MyData(0, 0, 0, "dayOfWeek");
        if(day > 0 && month > 0 && month < 13){
            myData = new MyData(day, month, year, dayOfWeek);
        }
        return myData;
    } 

    public static void convertDate(String inputFile, String outputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            MyData previousData = new MyData(-1, -1, -1, "");
            String line;
            while ((line = reader.readLine()) != null) {
                Pattern pattern1 = Pattern.compile("\\d{2} */ *\\d{1,2} */ *\\d{4} */ *(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)"); // dd / mm / yyyy / weekday or dd / m / yyyy / weekday
                Pattern pattern2 = Pattern.compile("\\d{4}-\\d{2}-\\d{2} +(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)"); // yyyy-mm-dd weekday
                Pattern pattern3 = Pattern.compile("(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday) +\\d{2}\\.\\d{2}\\.\\d{4}"); // weekday dd.mm.yyyy
                
                Matcher matcher1 = pattern1.matcher(line);
                Matcher matcher2 = pattern2.matcher(line);
                Matcher matcher3 = pattern3.matcher(line);

                boolean matchFound1 = matcher1.find();
                boolean matchFound2 = matcher2.find();
                boolean matchFound3 = matcher3.find();

                MyData myData = new MyData(0, 0, 0, "");

                if(matchFound1){
                    myData = convertOne(line);
                }
                if(matchFound2){
                    myData = convertTwo(line);
                }
                if(matchFound3){
                    myData = convertThree(line);
                }
                if(myData.day == 0){
                    System.out.println("Wrong date format!\n" + line);
                    continue;
                }
                if(!compareDates(myData, previousData)){
                    writer.write(myData.toMyString());
                    writer.newLine();
                    previousData = myData;
                }
            }

            System.out.println("Data converted successfully!");
        }
    }
}
