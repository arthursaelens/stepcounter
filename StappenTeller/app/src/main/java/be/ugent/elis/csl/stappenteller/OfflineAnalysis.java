package be.ugent.elis.csl.stappenteller;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OfflineAnalysis {
    private static final Pattern mFilenamePattern = Pattern.compile("(.+-([0-9]+))\\.csv");

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("You must provide a file or directory with the relevant data!");
            System.err.println("Usage: OfflineAnalysis FILE|DIRECTORY");
            System.exit(1);
        }

        File path = new File(args[0]);
        if (path.isDirectory()) {
            // scan for files matching the pattern
            for (File file : path.listFiles()) {
                Matcher match = mFilenamePattern.matcher(file.getName());
                if (match.matches()) {
                    System.err.println(String.format("Processing %s...", file.getName()));
                    process(createDetectors(), file);
                }
            }
        } else {
            Matcher match = mFilenamePattern.matcher(path.getName());
            if (!match.matches()) {
                System.err.println("Provided file does not match the expected file name pattern!");
                System.exit(1);
            }
            process(createDetectors(), path);
        }
    }

    private static IDetector[] createDetectors() {
        return new IDetector[]
                {
                        new DummyDetector()
                };
    }

    private static void process(IDetector[] detectors, File input) {
        // parse the filename
        Matcher match = mFilenamePattern.matcher(input.getName());
        if (!match.matches()) {
            throw (new RuntimeException("Could not match filename"));
        }
        String basename = match.group(1);
        int expectedSteps = Integer.parseInt(match.group(2));

        try {
            FileReader reader = new FileReader(input);
            CSVReader csvReader = new CSVReader(reader);

            // skip the header
            String[] input_record = csvReader.readNext();
            assert (input_record != null);

            Map<IDetector, List<Map<String, String>>> output_data = new HashMap<>();
            Map<IDetector, Set<String>> output_fields = new HashMap<>();
            for (IDetector detector : detectors) {
                output_data.put(detector, new ArrayList<Map<String, String>>());
                output_fields.put(detector, new HashSet<String>());
            }

            // parse all records
            while ((input_record = csvReader.readNext()) != null) {
                long timestamp = Long.parseLong(input_record[0]);
                float x = Float.parseFloat(input_record[1]);
                float y = Float.parseFloat(input_record[2]);
                float z = Float.parseFloat(input_record[3]);

                for (IDetector detector : detectors) {
                    DetectorLog log = new DetectorLog();
                    detector.addAccelerationData(timestamp, x, y, z, log);
                    output_fields.get(detector).addAll(log.keys());

                    log.put("timestamp", timestamp);
                    log.put("x", x);
                    log.put("y", y);
                    log.put("z", z);
                    output_data.get(detector).add(log.data());
                }
            }

            // write the new output
            for (IDetector detector : detectors) {
                String name = detector.getClass().getSimpleName();
                System.out.println(String.format("%s: %s %d/%d", input.getName(), name, detector
                        .getSteps(), expectedSteps));

                String output = String.format("%s%c%s-OfflineAnalysis_%s.csv",
                        input.getAbsoluteFile().getParent(), File.separatorChar, basename, name);
                FileWriter writer = new FileWriter(output);
                CSVWriter csvWriter = new CSVWriter(writer);

                // write the header
                List<String> header = new ArrayList<>();
                header.add("timestamp");
                header.add("x");
                header.add("y");
                header.add("z");
                for (String field : output_fields.get(detector))
                    header.add(field);
                String[] output_record = new String[header.size()];
                csvWriter.writeNext(header.toArray(output_record));

                // write the data
                for (Map<String, String> data : output_data.get(detector)) {
                    int i = 0;
                    for (String field : header)
                        output_record[i++] = data.getOrDefault(field, "");
                    csvWriter.writeNext(output_record);
                }

                csvWriter.close();
            }

            csvReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
